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

import java.util.ArrayList;
import java.util.List;

public class StringLiteral implements BooleanExpression<ComparisonOperator, String> {

    String literal;
    private ComparisonOperator operator = null;
    private int operatorArity = 1;

    public StringLiteral(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public ComparisonOperator getOperator() {
        return this.operator;
    }

    public void setOperator(ComparisonOperator operator) {
        throw new IllegalArgumentException("Cannot set an operator in a StringLiteral.");
    }

    public int getOperatorArity() {
        return this.operatorArity;
    }

    public List<String> getOperandsList() {
        List<String> literalValueList = new ArrayList<String>();
        literalValueList.add(this.literal);
        return literalValueList;
    }

    public void setOperandsList(List<String> operandsList) {
        if (operandsList.size() != this.operatorArity) {
            try {
                throw new IllegalArgumentException("Size of operands list does not corresponds to the arity of the operator.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        this.literal = operandsList.get(0);
    }

    public SQLString toSQLString() {
        return new SQLString(this.literal);
    }
}
