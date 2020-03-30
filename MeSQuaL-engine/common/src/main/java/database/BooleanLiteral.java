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

public class BooleanLiteral implements BooleanExpression<BooleanOperator,Boolean> {

    private BooleanOperator operator = null;
    private int operatorArity = 1;
    private boolean booleanLiteral;

    public BooleanLiteral(boolean booleanLiteral) {
        this.booleanLiteral = booleanLiteral;
    }

    public Boolean getLiteral() {
        return booleanLiteral;
    }

    public BooleanOperator getOperator() {
        return null;
    }

    public void setOperator(BooleanOperator operator) {
        throw new IllegalArgumentException("Cannot set an operator in a BooleanLiteral.");
    }

    public int getOperatorArity() {
        return 1;
    }

    public List<Boolean> getOperandsList() {
        List<Boolean> literalValueList = new ArrayList<Boolean>();
        literalValueList.add(this.booleanLiteral);
        return literalValueList;
    }

    public void setOperandsList(List<Boolean> operandsList) {
        if (operandsList.size() != this.operatorArity) {
            try {
                throw new IllegalArgumentException("Size of operands list does not corresponds to the arity of the operator.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        this.booleanLiteral = operandsList.get(0);
    }

    public SQLString toSQLString() {
        return new SQLString(Boolean.toString(booleanLiteral).toUpperCase());
    }
}
