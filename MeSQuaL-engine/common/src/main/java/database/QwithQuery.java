/*
 *     This file is part of MeSQuaL.
 *
 *     MeSQuaL is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MeSQuaL is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MeSQuaL. If not, see <https://www.gnu.org/licenses/>.
 */

package database;

import com.opencsv.CSVWriter;
import databaseManagement.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class QwithQuery {

    private boolean onSqlQueryResult = false;
    private String sqlQuery = "";
    private QwithConstraints qwithConstraints = null;
    private List<QwithQuery> qwithSubQueries = null;
    private List<UDFResult> udfResults = new ArrayList<>();

    public QwithQuery(boolean onSqlQueryResult, String sqlQuery, QwithConstraints qwithConstraints) {
        this.onSqlQueryResult = onSqlQueryResult;
        this.sqlQuery = sqlQuery;
        this.qwithConstraints = qwithConstraints;
    }

    public QwithQuery(boolean onSqlQueryResult, String sqlQuery, QwithConstraints qwithConstraints, List<QwithQuery> qwithSubQueries) {
        this.onSqlQueryResult = onSqlQueryResult;
        this.sqlQuery = sqlQuery;
        this.qwithConstraints = qwithConstraints;
        this.qwithSubQueries = qwithSubQueries;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public QwithConstraints getQwithConstraints() {
        return qwithConstraints;
    }

    public void setQwithConstraints(QwithConstraints qwithConstraints) {
        this.qwithConstraints = qwithConstraints;
    }

    public ResultSet executeMySqlQuery(DatabaseConnection dbConnection) {
        String sqlQuery = this.sqlQuery;

        // end with ';' if not already the case
        if (!sqlQuery.substring(sqlQuery.length() - 1).equals(';'))
            sqlQuery += ";";

        return dbConnection.submitQuery(sqlQuery);
    }

    public QWithResults executeQuery(Path workingDirectoryPath, DatabaseConnection dbConnection, DimensionMap dimMap, ContractMap contractMap) throws SQuaLException, SQLException {
        // query timestamp
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        // get an UUID to identify the request
        String queryUUID = UUID.randomUUID().toString();
        String queryId = queryUUID; // corresponds to the id used to store the query in the DBMS, equals to queryUUID if the query is not already stored

        // store query if needed
        ResultSet resultSetQueryId = dbConnection.submitQuery("SELECT * FROM MeSQuaLqueries WHERE query = \"" + this.toSQLString().getQueryString() + "\"");
        if(resultSetQueryId.next()){
            queryId = resultSetQueryId.getString("queryId");
        } else {
            dbConnection.submitDataManipulation("INSERT INTO MeSQuaLqueries VALUES('" + queryId + "',\"" + this.toSQLString().getQueryString() + "\")");
        }


                // paths of input files
        List<Path> csvPaths = null;

        SQLResult sqlQueryResults = null;
        if (this.onSqlQueryResult) {
            // execute SQL query and save results in a csv (in order to pass it to the user defined functions)
            Path outputCsvPath = Paths.get("/tmp/result-sql-" + queryUUID + ".csv");
            // exec time
            long startTime = System.nanoTime();
            sqlQueryResults = executeSqlQuery(dbConnection, outputCsvPath);
            // exec time
            long stopTime = System.nanoTime();
            System.out.println("executeSqlQuery, exec time: " + (stopTime - startTime));

            csvPaths = sqlQueryResults.getOutputFilesPaths();

            // load results in a dedicated view used by grafana
            loadSQLqueryInDB(dbConnection);

            // save query in the DB

        } else {
            // TODO: extract database using CSV files
        }

        // execute UDF over SQL query results
        // exec time
        long startTime = System.nanoTime();
        executeQwithDimensions(queryId, workingDirectoryPath, dbConnection, dimMap, queryUUID, csvPaths);
        executeQwithContracts(queryId, workingDirectoryPath, dbConnection, dimMap, contractMap, queryUUID, csvPaths);
        // exec time
        long stopTime = System.nanoTime();
        System.out.println("executeUDFs, exec time: " + (stopTime - startTime));

        QWithResults qWithOutput = new QWithResults(sqlQueryResults, udfResults);

        putResultsInDb(timestamp, qWithOutput, dbConnection);

        return qWithOutput;
    }

    private void loadSQLqueryInDB(DatabaseConnection dbConnection) {
        String sqlQuery = "CREATE OR REPLACE VIEW MeSQuaLresults AS "+ this.sqlQuery;

        // end with ';' if not already the case
        if (!sqlQuery.substring(sqlQuery.length() - 1).equals(';'))
            sqlQuery += ";";

        dbConnection.submitDataManipulation(sqlQuery);
    }

    private void putResultsInDb(Timestamp timestamp, QWithResults qWithOutput, DatabaseConnection dbConnection) {
        for (UDFResult udfRes : qWithOutput.getUdfResults()) {
            StringLiteral sl = (StringLiteral) udfRes.getComparedValue();
            // database
            float dbResult = udfRes.getDbResult();
            if (dbResult != -1) {
                StringBuilder query = new StringBuilder("INSERT INTO UDFresults VALUES(");
                query.append("'").append(udfRes.getQueryId()).append("'")
                        .append(",'").append(timestamp.toString()).append("'")
                        .append(",'").append(udfRes.getContractTypeName()).append("'")
                        .append(",'").append(udfRes.getDimensionName()).append("'")
                        .append(",").append(udfRes.getComparedValue().toSQLString().getQueryString())
                        .append(",'").append(udfRes.getOperator().toString()).append("'")
                        .append(",").append("'db'")
                        .append(", NULL")
                        .append(",").append(dbResult)
                        .append(",").append(!udfRes.getOperator().evaluateFloat(dbResult, Float.parseFloat(sl.getLiteral()))).append(")");

                dbConnection.submitDataManipulation(query.toString());
            }

            // instances
            Map<String, Float> instancesResults = udfRes.getInstancesResults();
            if (instancesResults != null)
                for (String key : instancesResults.keySet()) {
                    StringBuilder query = new StringBuilder("INSERT INTO UDFresults VALUES(");
                    query.append("'").append(udfRes.getQueryId()).append("'")
                            .append(",'").append(timestamp.toString()).append("'")
                            .append(",'").append(udfRes.getContractTypeName()).append("'")
                            .append(",'").append(udfRes.getDimensionName()).append("'")
                            .append(",").append(udfRes.getComparedValue().toSQLString().getQueryString())
                            .append(",'").append(udfRes.getOperator().toString()).append("'")
                            .append(",").append("'inst'")
                            .append(",'").append(key).append("'")
                            .append(",").append(instancesResults.get(key))
                            .append(",").append(!udfRes.getOperator().evaluateFloat(instancesResults.get(key), Float.parseFloat(sl.getLiteral()))).append(")");

                    dbConnection.submitDataManipulation(query.toString());
                }

            // attributes
            Map<String, Float> attributesResults = udfRes.getAttributesResults();
            if (attributesResults != null)
                for (String key : attributesResults.keySet()) {
                    StringBuilder query = new StringBuilder("INSERT INTO UDFresults VALUES(");
                    query.append("'").append(udfRes.getQueryId()).append("'")
                            .append(",'").append(timestamp.toString()).append("'")
                            .append(",'").append(udfRes.getContractTypeName()).append("'")
                            .append(",'").append(udfRes.getDimensionName()).append("'")
                            .append(",").append(udfRes.getComparedValue().toSQLString().getQueryString())
                            .append(",'").append(udfRes.getOperator().toString()).append("'")
                            .append(",").append("'att'")
                            .append(",'").append(key).append("'")
                            .append(",").append(attributesResults.get(key))
                            .append(",").append(!udfRes.getOperator().evaluateFloat(attributesResults.get(key), Float.parseFloat(sl.getLiteral()))).append(")");

                    dbConnection.submitDataManipulation(query.toString());
                }
            // rows
            Map<String, Float> rowsResults = udfRes.getRowsResults();
            if (rowsResults != null)
                for (String key : rowsResults.keySet()) {
                    StringBuilder query = new StringBuilder("INSERT INTO UDFresults VALUES(");
                    query.append("'").append(udfRes.getQueryId()).append("'")
                            .append(",'").append(timestamp.toString()).append("'")
                            .append(",'").append(udfRes.getContractTypeName()).append("'")
                            .append(",'").append(udfRes.getDimensionName()).append("'")
                            .append(",").append(udfRes.getComparedValue().toSQLString().getQueryString())
                            .append(",'").append(udfRes.getOperator().toString()).append("'")
                            .append(",").append("'row'")
                            .append(",'").append(key).append("'")
                            .append(",").append(rowsResults.get(key))
                            .append(",").append(!udfRes.getOperator().evaluateFloat(rowsResults.get(key), Float.parseFloat(sl.getLiteral()))).append(")");

                    dbConnection.submitDataManipulation(query.toString());
                }

            // cells
            Vector<Vector<Float>> cellsMatrixResults = udfRes.getCellsMatrixResults();
            if (cellsMatrixResults != null)
                for (int i = 0; i < cellsMatrixResults.size(); i++) {
                    Vector<Float> row = cellsMatrixResults.get(i);
                    for (int j = 0; i < row.size(); j++) {
                        float cell = row.get(j);
                        StringBuilder query = new StringBuilder("INSERT INTO UDFresults VALUES(");
                        query.append("'").append(udfRes.getQueryId()).append("'")
                                .append(",'").append(timestamp.toString()).append("'")
                                .append(",'").append(udfRes.getContractTypeName()).append("'")
                                .append(",'").append(udfRes.getDimensionName()).append("'")
                                .append(",").append(udfRes.getComparedValue().toSQLString())
                                .append(",'").append(udfRes.getOperator().toString()).append("'")
                                .append(",").append("'cell'")
                                .append(",").append("'r" + i + "_c" + j + "'")
                                .append(",").append(rowsResults.get(cell))
                                .append(",").append(!udfRes.getOperator().evaluateFloat(cell, Float.parseFloat(sl.getLiteral()))).append(")");

                        dbConnection.submitDataManipulation(query.toString());
                    }
                }
        }
    }

    private void executeQwithContracts(String queryId, Path workingDirectoryPath, DatabaseConnection dbConnection, DimensionMap dimMap, ContractMap contractMap, String queryUUID, List<Path> csvPaths) throws SQuaLException{
        for (String contractName : this.getQwithConstraints().getContractNames()) {
            System.err.println("Contract eval: " + contractName);

            if (contractMap.containsKey(contractName)) {
                for (Constraint cons : contractMap.get(contractName).getConstraintList()) {
                    if (dimMap.containsKey(cons.getDimension_name())) {
                        System.err.println("Contract " + contractName + ", eval dim: " + cons.getDimension_name());

                        Dimension currentDimension = dimMap.get(cons.getDimension_name());

                        Path outputFilePath = Paths.get("/tmp/result-qwith-"
                                + contractName + "_" + cons.getDimension_name() + queryUUID + ".json");
                        evaluateDimension(queryId, workingDirectoryPath, dbConnection, csvPaths, cons, currentDimension, outputFilePath);
                    } else {
                        throw new SQuaLException("Dimension " + cons.getDimension_name()
                                + " does not exists in the declared dimensions.");
                    }
                }
            } else {
                throw new SQuaLException("Contract " + contractName
                        + " does not exists in the declared contracts.");
            }
        }
    }

    private void evaluateDimension(String queryId, Path workingDirectoryPath, DatabaseConnection dbConnection, List<Path> csvPaths, Constraint cons, Dimension currentDimension, Path outputFilePath) {
        currentDimension.evaluate(workingDirectoryPath, dbConnection, csvPaths, outputFilePath);
        try {
            String contents = new String(Files.readAllBytes(outputFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.udfResults.add(new UDFResult(queryId, currentDimension.getContractTypeName(), currentDimension.getDimensionName().getName(), outputFilePath,
                cons.getComparison_op(), cons.getCompared_value(), cons.getCtype_name()));
    }

    private void executeQwithDimensions(String queryId, Path workingDirectoryPath, DatabaseConnection dbConnection, DimensionMap dimMap, String queryUUID, List<Path> csvPaths) {
        for (Constraint cons : this.getQwithConstraints().getConstraints()) {
            if (dimMap.containsKey(cons.getDimension_name())) {
                Dimension currentDimension = dimMap.get(cons.getDimension_name());
                Path outputFilePath = Paths.get("/tmp/result-qwith-"
                        + currentDimension.getDimensionName().getName() + queryUUID + ".json");
                evaluateDimension(queryId, workingDirectoryPath, dbConnection, csvPaths, cons, currentDimension, outputFilePath);
            } else {
                System.err.println("Dimension " + cons.getDimension_name()
                        + " does not exists in the declared dimensions.");
            }
        }
    }

    private SQLResult executeSqlQuery(DatabaseConnection dbConnection, Path outputCsvPath) throws SQLException {
        List<Path> paths = new ArrayList<Path>();
        paths.add(outputCsvPath);

        ResultSet sqlQueryResults = executeMySqlQuery(dbConnection);
        RowSetFactory rsFactory = null;
        CachedRowSet rowset = null;
        try {
            rsFactory = RowSetProvider.newFactory();
            rowset = rsFactory.createCachedRowSet();
            rowset.populate(sqlQueryResults);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            writeCsvFromResultSet(outputCsvPath, rowset.createCopy());
            paths.add(outputCsvPath);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String contents = new String(Files.readAllBytes(outputCsvPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SQLResult(rowset, paths);
    }

    private void writeCsvFromResultSet(Path outputCsvPath, ResultSet sqlQueryResults) {
        try (CSVWriter writer = new CSVWriter(
                Files.newBufferedWriter(outputCsvPath, StandardCharsets.UTF_8),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            writer.writeAll(sqlQueryResults, true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();

        query.append("{").append(this.sqlQuery)
                .append("} QWITH ").append(this.qwithConstraints.toSQLString().getQueryString())
                .append(";");

        return new SQLString(query.toString());
    }
}
