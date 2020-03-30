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

package databaseManagement;

import UDFCalls.UDFLanguage;
import database.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqualElementsManager {

    private DatabaseConnection bdConnector = null;

    public SqualElementsManager(DatabaseConnection bdConnector) {
        this.bdConnector = bdConnector;

        createSqualTablesIfNeeded();
    }


    private void createSqualTablesIfNeeded() {
        String nameContract = "CONTRACT";
        List<Attribute> contractAttributeList = new ArrayList<>();
        contractAttributeList.add(new Attribute("contractName", "VARCHAR(255)"));
        contractAttributeList.add(new Attribute("constraintOperator", "VARCHAR(255)"));
        contractAttributeList.add(new Attribute("dimensionName", "VARCHAR(255)"));
        contractAttributeList.add(new Attribute("comparedValue", "VARCHAR(255)"));

        String nameContractType = "CONTRACTTYPE";
        List<Attribute> contractTypeAttributeList = new ArrayList<>();
        contractTypeAttributeList.add(new Attribute("contractTypeName", "VARCHAR(255)"));
        contractTypeAttributeList.add(new Attribute("dimensionName", "VARCHAR(255)"));
        contractTypeAttributeList.add(new Attribute("scope", "VARCHAR(255)"));
        contractTypeAttributeList.add(new Attribute("outputType", "VARCHAR(255)"));
        contractTypeAttributeList.add(new Attribute("language", "VARCHAR(255)"));
        contractTypeAttributeList.add(new Attribute("functionPath", "VARCHAR(255)"));

        String nameUdfResults = "UDFresults";
        List<Attribute> udfResultseAttributeList = new ArrayList<>();
        udfResultseAttributeList.add(new Attribute("queryId", "VARCHAR(255)"));
        udfResultseAttributeList.add(new Attribute("queryTimestamp", "VARCHAR(255)"));
        udfResultseAttributeList.add(new Attribute("contractType", "VARCHAR(255)"));
        udfResultseAttributeList.add(new Attribute("dimensionName", "VARCHAR(255)"));
        udfResultseAttributeList.add(new Attribute("comparedValue", "FLOAT"));
        udfResultseAttributeList.add(new Attribute("operator", "VARCHAR(4)"));
        udfResultseAttributeList.add(new Attribute("elementGranularity", "VARCHAR(255)"));
        udfResultseAttributeList.add(new Attribute("elementId", "VARCHAR(255)"));
        udfResultseAttributeList.add(new Attribute("udfResult", "FLOAT"));
        udfResultseAttributeList.add(new Attribute("violation", "BOOLEAN"));

        String nameQueries = "MeSQuaLqueries";
        List<Attribute> queriesAttributeList = new ArrayList<>();
        queriesAttributeList.add(new Attribute("queryId", "VARCHAR(255)"));
        queriesAttributeList.add(new Attribute("query", "VARCHAR(255)"));


        this.bdConnector.createTable(nameContract, contractAttributeList);
        this.bdConnector.createTable(nameContractType, contractTypeAttributeList);
        this.bdConnector.createTable(nameUdfResults, udfResultseAttributeList);
        this.bdConnector.createTable(nameQueries, queriesAttributeList);
    }

    public void addContract(Contract contract) {
        String contractName = contract.getContractName();

        for (Constraint cons : contract.getConstraintList()) {
            List<String> values = new ArrayList<>();
            values.add("'" + contractName + "'");
            values.add(cons.getComparison_op().toStringKeyword());
            values.add("'" + cons.getDimension_name() + "'");
            values.add(cons.getCompared_value().getOperandsList().get(0).toString());

            try {
                if (!bdConnector.submitQuery("SELECT * FROM CONTRACT WHERE dimensionName = '" + contractName + "';").next())
                    this.bdConnector.insertTuple("CONTRACT", values);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addContractType(ContractType contractType) {
        String contractTypeName = contractType.getContractTypeName();

        for (Dimension dim : contractType.getDimensionList()) {
            List<String> values = new ArrayList<>();
            values.add("'" + contractTypeName + "'");
            values.add("'" + dim.getDimensionName().getName() + "'");
            values.add("'" + dim.getDimensionScope().toSQLString().getQueryString() + "'");
            values.add("'" + dim.getFunctionOutputType() + "'");
            values.add("'" + dim.getFunctionUdfLanguage() + "'");
            values.add("'" + dim.getFunctionName() + "'");

            try {
                if (!bdConnector.submitQuery("SELECT * FROM CONTRACTTYPE WHERE dimensionName = '" + dim.getDimensionName().getName() + "';").next())
                    this.bdConnector.insertTuple("CONTRACTTYPE", values);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteContract(String contractName) {
        this.bdConnector.submitDataManipulation("DELETE FROM CONTRACT WHERE 'contractName' = " +
                contractName);
    }

    public void deleteContractType(String contractTypeName) {
        this.bdConnector.submitDataManipulation("DELETE FROM CONTRACTTYPE WHERE 'contractTypeName' = " +
                contractTypeName);
    }

    public DatabaseConnection getDbConnector() {
        return bdConnector;
    }

    public Map<String, Contract> getContractMap() throws SQLException {
        ResultSet contractNames = this.getDbConnector().submitQuery("SELECT DISTINCT contractName FROM CONTRACT;");
        Map<String, Contract> outputMap = new HashMap<>();

        while (contractNames.next()) {
            String contractName = contractNames.getString(1);
            ResultSet contractConstraints = this.getDbConnector()
                    .submitQuery("SELECT * FROM CONTRACT WHERE contractName = '"
                            + contractName + "';");

            List<Constraint> consList = new ArrayList<>();
            while (contractConstraints.next()) {
                String comparisonOp = contractConstraints.getString(2);
                String dimName = contractConstraints.getString(3);
                String comparedValue = contractConstraints.getString(4);

                consList.add(new Constraint(ComparisonOperator.fromString(comparisonOp), null,
                        dimName, new StringLiteral(comparedValue)));
            }
            outputMap.put(contractName, new Contract(false, contractName, null, consList));
        }

        return outputMap;
    }

    public Map<String, Dimension> getDimensionMap() throws SQLException {
        ResultSet contractTypesDimensions = this.getDbConnector().submitQuery("SELECT * FROM CONTRACTTYPE;");
        Map<String, Dimension> outputMap = new HashMap<>();

        while (contractTypesDimensions.next()) {
            List<Dimension> dimList = new ArrayList<>();
            String ctName = contractTypesDimensions.getString(1);
            String dimName = contractTypesDimensions.getString(2);
            String dimScope = contractTypesDimensions.getString(3);
            String funOutputType = contractTypesDimensions.getString(4);
            String funUdfLanguage = contractTypesDimensions.getString(5);
            String funName = contractTypesDimensions.getString(6);

            outputMap.put(dimName, new Dimension(ctName, new Name(dimName),
                    new Scope(ScopeLevel.fromString(dimScope)),
                    SqlType.fromString(funOutputType),
                    UDFLanguage.fromString(funUdfLanguage),
                    funName));
        }

        return outputMap;
    }
}
