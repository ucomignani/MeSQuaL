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

import UDFCalls.Call;
import UDFCalls.ExternalCall;
import UDFCalls.UDFLanguage;
import databaseManagement.DatabaseConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Dimension {

    private String contractTypeName;
    private Name dimensionName;
    private Scope dimensionScope;
    private SqlType functionOutputType;
    private UDFLanguage functionUdfLanguage = UDFLanguage.UNKNOWN;
    private String functionName;
    private Call call = null;

    public Dimension(ContractType contractType, Name dimensionName, Scope dimensionScope,
                     SqlType functionOutputType, UDFLanguage functionUdfLanguage, String functionName) {
        this.contractTypeName = contractType.getContractTypeName();
        this.dimensionName = dimensionName;
        this.dimensionScope = dimensionScope;
        this.functionOutputType = functionOutputType;
        this.functionUdfLanguage = functionUdfLanguage;
        this.functionName = functionName;
    }

    public Dimension(String contractTypeName, Name dimensionName, Scope dimensionScope,
                     SqlType functionOutputType, UDFLanguage functionUdfLanguage, String functionName) {
        this.contractTypeName = contractTypeName;
        this.dimensionName = dimensionName;
        this.dimensionScope = dimensionScope;
        this.functionOutputType = functionOutputType;
        this.functionUdfLanguage = functionUdfLanguage;
        this.functionName = functionName;
    }

    public Dimension(String contractTypeName, Name dimensionName,
                     UDFLanguage functionUdfLanguage, String functionName) {
        this.contractTypeName = contractTypeName;
        this.dimensionName = dimensionName;
        this.dimensionScope = new Scope();
        this.functionOutputType = SqlType.FLOAT;
        this.functionUdfLanguage = functionUdfLanguage;
        this.functionName = functionName;
    }

    public Name getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(Name dimensionName) {
        this.dimensionName = dimensionName;
    }

    public Scope getDimensionScope() {
        return dimensionScope;
    }

    public void setDimensionScope(Scope dimensionScope) {
        this.dimensionScope = dimensionScope;
    }

    public SqlType getFunctionOutputType() {
        return functionOutputType;
    }

    public void setFunctionOutputType(SqlType functionOutputType) {
        this.functionOutputType = functionOutputType;
    }

    public UDFLanguage getFunctionUdfLanguage() {
        return functionUdfLanguage;
    }

    public void setFunctionUdfLanguage(UDFLanguage functionUdfLanguage) {
        this.functionUdfLanguage = functionUdfLanguage;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        query.append(this.dimensionName.toSQLString().getQueryString())
                // .append(" ")
                // .append(this.functionOutputType)
                // .append(" ON ").append(this.dimensionScope.toSQLString().getQueryString())
                .append(" BY FUNCTION ").append(this.functionName)
                .append(" LANGUAGE ").append(this.functionUdfLanguage);
        return new SQLString(query.toString());
    }


    public boolean evaluate(Path workingDirectoryPath, DatabaseConnection con, List<Path> inputCsvFiles, Path outputFilePath) {

        // Exécution de la fonction associée à la dimension
        switch (this.functionUdfLanguage) {

            case PYTHON: {
                ExternalCall ec = new ExternalCall(this.functionUdfLanguage,
                        Paths.get(workingDirectoryPath.toAbsolutePath() + "/" + this.functionName),
                        null);

                Path executableBinary = ec.getPath();
                String s = null;

                try {

                    StringBuilder command = new StringBuilder();
                    command //.append(functionUdfLanguage.getBinaryCall()).append(" ")
                            .append(executableBinary).append(" ")
                            .append(outputFilePath).append(" ")
                            .append(ec.isSomeParams() ? (" " + ec.getParams()) : "");
                    for (Path path : inputCsvFiles) {
                        command.append(" ").append(path);
                    }

                    Process p = Runtime.getRuntime().exec(command.toString());

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    System.out.println("Standard output:\n");
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }

                    // read any errors from the attempted command
                    System.out.println("Error output (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
            break;

        }

        return true;
    }

    public String getContractTypeName() {
        return this.contractTypeName;
    }
}
