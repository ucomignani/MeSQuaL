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

public class UnaryBooleanExpression implements BooleanExpression<BooleanOperator,BooleanExpression> {

    private BooleanOperator operator;
    private int operatorArity = 1;
    private BooleanExpression operand;

    public UnaryBooleanExpression(BooleanOperator operator, BooleanExpression operand) {
        this.operator = operator;
        this.operatorArity = 1;
        this.operand = operand;
    }

    public BooleanOperator getOperator() {
        return operator;
    }

    public void setOperator(BooleanOperator operator) {
        this.operator = operator;
    }

    public int getOperatorArity() {
        return this.operatorArity;
    }

    public List<BooleanExpression> getOperandsList() {
        List<BooleanExpression> operandList = new ArrayList<BooleanExpression>();
        operandList.add(this.operand);
        return operandList;
    }

    public void setOperandsList(List<BooleanExpression> operandsList) {
        if (operandsList.size() != this.operatorArity) {
            try {
                throw new IllegalArgumentException("Size of operands list does not corresponds to the arity of the operator.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        this.operand = operandsList.get(0);
    }

    public BooleanExpression getOperand() {
        return operand;
    }

    public void setOperand(BooleanExpression operand) {
        this.operand = operand;
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        query.append(this.operator.toString()).append(" ").append(this.operand.toSQLString().getQueryString());
        return new SQLString(query.toString());
    }
}