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

package Socket;

import database.*;
import databaseManagement.ConnectionParameters;
import databaseManagement.DatabaseConnection;
import databaseManagement.MysqlDatabaseConnection;
import databaseManagement.SqualElementsManager;
import squalParser.ParseException;
import squalParser.SQualParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQuaLqueryEngine {
    private static SQualParser squalParser = new SQualParser(new StringReader(" "));
    private ConnectionParameters connectionParameters =
            new ConnectionParameters("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "Edbt2020", "UTC");
    private DatabaseConnection databaseConnection = null;

    private Path workingDirectoryPath = Paths.get("");  //System.getProperty("user.dir"));


    public SQuaLqueryEngine() {
        createDatabaseConnection(this.connectionParameters);
    }

    private void createDatabaseConnection(ConnectionParameters connectionParameters) {
        switch (connectionParameters.getDriverName()) {
            case "com.mysql.cj.jdbc.Driver":
                try {
                    this.databaseConnection = new MysqlDatabaseConnection(connectionParameters);
                } catch (Exception e) {
                    System.out.println("Database connection: " + e);
                }
                break;
            default:
                this.databaseConnection = null;
        }
    }
    public QWithResults executeQuery(String queryString) {
        // exec time
        long startTime = System.nanoTime();

        QWithResults qWithResults = null;
        InputStream queryStream = new ByteArrayInputStream(queryString.getBytes());

        squalParser.ReInit(queryStream);

        SqualElementsManager sem = new SqualElementsManager(this.databaseConnection);
        SQuaLScript squalScript = null;

        try {
            squalScript = squalParser.squalScript(sem, new DimensionMap(), new ContractMap());
        } catch (ParseException e) {
            System.out.println("Parse exception: " +  e);
        }

        try {
            qWithResults = squalScript.evaluate(workingDirectoryPath);
        } catch (Exception e) {
            System.out.println("SQuaL exception: " +  e);
        }
        // exec time
        long stopTime = System.nanoTime();
        System.out.println("executeQuery, exec time: " + (stopTime - startTime));

        return qWithResults;
    }

    public DataTable executeSqlQuery(String queryString) {
        // exec time
        long startTime = System.nanoTime();

        DataTable sqlResults = null;

        try {
            Statement sqlStatement = this.databaseConnection.getConnection().createStatement();
            ResultSet sqlResultSet = sqlStatement.executeQuery("SELECT * FROM MeSQuaLresults");
            DataTable dtOut = new DataTable(sqlResultSet);
            sqlResultSet.close();
            sqlStatement.close();

            return dtOut;

        } catch (SQLException e) {
            System.out.println("Parse exception: " +  e);
        }

        return null;
    }
}
