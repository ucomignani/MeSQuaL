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

public class Constraint {

    private ComparisonOperator comparison_op = null;
    private ContractTypeName ctype_name = null;
    private String dimension_name = null;
    private BooleanExpression compared_value = null;

    public Constraint(ComparisonOperator comparison_op, ContractTypeName ctype_name,
                      String dimension_name, BooleanExpression compared_value) {
        this.comparison_op = comparison_op;
        this.ctype_name = ctype_name;
        this.dimension_name = dimension_name;
        this.compared_value = compared_value;

    }

    public ComparisonOperator getComparison_op() {
        return comparison_op;
    }

    public void setComparison_op(ComparisonOperator comparison_op) {
        this.comparison_op = comparison_op;
    }

    public ContractTypeName getCtype_name() {
        return ctype_name;
    }

    public void setCtype_name(ContractTypeName ctype_name) {
        this.ctype_name = ctype_name;
    }

    public String getDimension_name() {
        return dimension_name;
    }

    public void setDimension_name(String dimension_name) {
        this.dimension_name = dimension_name;
    }

    public BooleanExpression getCompared_value() {
        return compared_value;
    }

    public void setCompared_value(BooleanExpression compared_value) {
        this.compared_value = compared_value;
    }

    public SQLString toSQLString() {
        StringBuilder ctString = new StringBuilder();
        ctString.append(this.ctype_name!=null ? this.ctype_name.toSQLString().getQueryString() +"." : "")
                .append(this.dimension_name)
                .append(this.comparison_op.toString())
                .append(this.compared_value.toSQLString().getQueryString());
        return new SQLString(ctString.toString());
    }
}
