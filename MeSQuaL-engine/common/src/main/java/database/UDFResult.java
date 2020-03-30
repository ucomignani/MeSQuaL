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

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

public class UDFResult {
    private String contractTypeName;
    private String dimensionName;
    private boolean containsViolation = false;

    private float dbResult = -1;
    private Map<String, Float> instancesResults;
    private Map<String, Float> attributesResults;
    private Map<String, Float> rowsResults;
    private Vector<Vector<Float>> cellsMatrixResults;

    private ComparisonOperator operator = ComparisonOperator.NONE;
    private BooleanExpression comparedValue = null;
    private ContractTypeName comparedValueType = null;
    private String queryId;

    public UDFResult(String queryId, Path resultsCsvFilePath) {
        this.queryId = queryId;

        //TODO : extends to cells. Extends to multiples instances for the QWITH token applied to the whole dattabase instead of a query result
        this.cellsMatrixResults = new Vector<Vector<Float>>();

        commonConstructorImplementation(resultsCsvFilePath);
    }

    public UDFResult(String queryId, String contractTypeName, String dimensionName, Path resultsCsvFilePath, ComparisonOperator comparison_op, BooleanExpression compared_value, ContractTypeName ctype_name) {
        //TODO : extends to cells. Extends to multiples instances for the QWITH token applied to the whole dattabase instead of a query result
        this.queryId = queryId;
        this.contractTypeName = contractTypeName;
        this.dimensionName = dimensionName;
        this.cellsMatrixResults = new Vector<Vector<Float>>();
        this.operator = comparison_op;
        this.comparedValue = compared_value;
        this.comparedValueType = ctype_name;

        commonConstructorImplementation(resultsCsvFilePath);
    }

    public boolean hasViolation() {
        return containsViolation;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public float getDbResult() {
        return dbResult;
    }

    public Map<String, Float> getInstancesResults() {
        return instancesResults;
    }

    public Map<String, Float> getAttributesResults() {
        return attributesResults;
    }

    public Map<String, Float> getRowsResults() {
        return rowsResults;
    }

    public Vector<Vector<Float>> getCellsMatrixResults() {
        return cellsMatrixResults;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public BooleanExpression getComparedValue() {
        return comparedValue;
    }

    public ContractTypeName getComparedValueType() {
        return comparedValueType;
    }

    private void commonConstructorImplementation(Path resultsCsvFilePath) {
        try {

            String contents = new String(Files.readAllBytes(resultsCsvFilePath));

            JSONObject obj = new JSONObject(contents);

            if (obj.has("db")) {
                this.dbResult = obj.getFloat("db");
                if (isViolation(dbResult)) containsViolation = true;
            }

            if (obj.has("Instances")) {
                JSONArray arrayInstances = obj.getJSONArray("Instances");
                this.instancesResults = new HashMap<>();
                for (int i = 0; i < arrayInstances.length(); i++) {
                    String instanceName = arrayInstances.getJSONObject(i).getString("name");
                    Float result = arrayInstances.getJSONObject(i).getFloat("result");
                    if (isViolation(result)) containsViolation = true;
                    this.instancesResults.put(instanceName, result);
                }
            }

            if (obj.has("Attributes")) {
                JSONArray arrayAttributes = obj.getJSONArray("Attributes");
                this.attributesResults = new HashMap<>();
                for (int i = 0; i < arrayAttributes.length(); i++) {
                    String attributeName = arrayAttributes.getJSONObject(i).getString("name");
                    Float result = arrayAttributes.getJSONObject(i).getFloat("result");
                    if (isViolation(result)) containsViolation = true;
                    this.attributesResults.put(attributeName, result);
                }
            }

            if (obj.has("Rows")) {
                JSONArray arrayRows = obj.getJSONArray("Rows");
                this.rowsResults = new HashMap<>();
                for (int i = 0; i < arrayRows.length(); i++) {
                    String rowNumber = arrayRows.getJSONObject(i).getString("number");
                    Float result = arrayRows.getJSONObject(i).getFloat("result");
                    if (isViolation(result)) containsViolation = true;
                    this.rowsResults.put(rowNumber, result);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isViolation(float result) {
        StringLiteral sl = (StringLiteral) this.comparedValue;
        return !this.operator.evaluateFloat(result, Float.parseFloat(sl.getLiteral()));
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        if (this.dbResult != -1)
            output.append("Database: ").append(this.dbResult);

        if (this.instancesResults != null) {
            output.append("\nInstances:\n");
            for (String instanceName : this.instancesResults.keySet()) {
                output.append("\t").append(instanceName)
                        .append(": ").append(this.attributesResults.get(instanceName));
            }
        }

        if (this.attributesResults != null) {
            output.append("\nAttributes:\n");
            for (String attributeName : this.attributesResults.keySet()) {
                output.append("\t").append(attributeName)
                        .append(": ").append(this.attributesResults.get(attributeName));
            }
        }

        if (this.rowsResults != null) {
            output.append("\n\nRows:\n");
            for (String rowId : this.rowsResults.keySet()) {
                output.append("\t").append(rowId)
                        .append(": ").append(this.rowsResults.get(rowId));
            }
        }

        return output.toString();
    }

    public String toHtml(String color) {
        String uuid = UUID.randomUUID().toString();
        StringBuilder output = new StringBuilder();
        output.append("<html><font color=")
                .append(color)
                .append(">");

        output.append("Dimension: " + this.dimensionName)
                .append("; comparison: " + this.operator.toString() + this.comparedValue.toSQLString().getQueryString());

//        if (this.dbResult != -1)
//            output.append("Database: ").append(this.dbResult);
//
//        if (this.instancesResults != null) {
//            output.append("\nInstances:\n");
//            for (String instanceName : this.instancesResults.keySet()) {
//                output.append("\t").append(instanceName)
//                        .append(": ").append(this.attributesResults.get(instanceName));
//            }
//        }
//
//        if (this.attributesResults != null) {
//            output.append("\nAttributes:\n");
//            for (String attributeName : this.attributesResults.keySet()) {
//                output.append("\t").append(attributeName)
//                        .append(": ").append(this.attributesResults.get(attributeName));
//            }
//        }
//
//        if (this.rowsResults != null) {
//            output.append("\n\nRows:\n");
//            for (String rowId : this.rowsResults.keySet()) {
//                output.append("\t").append(rowId)
//                        .append(": ").append(this.rowsResults.get(rowId));
//            }
//        }
        output.append("</html><!-- " + uuid.toString() + "-->");
        return output.toString();
    }

    public String getQueryId() {
        return this.queryId;
    }

    public String getContractTypeName() {
        return this.contractTypeName;
    }
}
