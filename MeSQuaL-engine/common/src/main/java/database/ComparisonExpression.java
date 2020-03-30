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

public class ComparisonExpression implements BooleanExpression<ComparisonOperator, StringLiteral> {
    private ComparisonOperator operator;
    private int operatorArity = 2;
    private StringLiteral firstOperand;
    private StringLiteral secondOperand;

    public ComparisonExpression() {
        this.operator = null;
        this.firstOperand = null;
        this.secondOperand = null;
    }

    public ComparisonExpression(ComparisonOperator operator, StringLiteral firstOperand, StringLiteral secondOperand) {
        this.operator = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    public int getOperatorArity() {
        return 2;
    }

    public List getOperandsList() {
        List<StringLiteral> operandList = new ArrayList<StringLiteral>();
        operandList.add(this.firstOperand);
        operandList.add(this.secondOperand);
        return operandList;
    }

    public void setOperandsList(List<StringLiteral> operandsList) {
        if (operandsList.size() != this.operatorArity) {
            try {
                throw new IllegalArgumentException("Size of operands list does not corresponds to the arity of the operator.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        this.firstOperand = operandsList.get(0);
        this.secondOperand = operandsList.get(1);
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        query.append(this.firstOperand.getLiteral())
                .append(this.operator.toString())
                .append(this.secondOperand.getLiteral());
        return new SQLString(query.toString());
    }
}
