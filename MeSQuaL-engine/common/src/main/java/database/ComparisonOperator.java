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

public enum ComparisonOperator {
    NONE,
    NOT_EQUAL,
    EQUAL,
    GREATER,
    LESSER,
    GREATER_OR_EQUAL,
    LESSER_OR_EQUAL;


    public static ComparisonOperator fromString(String comparisonOp) {
        switch (comparisonOp) {
            case "EQUAL":
                return EQUAL;
            case "NOT_EQUAL":
                return NOT_EQUAL;
            case "GREATER":
                return GREATER;
            case "GREATER_OR_EQUAL":
                return GREATER_OR_EQUAL;
            case "LESSER":
                return LESSER;
            case "LESSER_OR_EQUAL":
                return LESSER_OR_EQUAL;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case EQUAL:
                return " = ";
            case NOT_EQUAL:
                return " != ";
            case GREATER:
                return " > ";
            case GREATER_OR_EQUAL:
                return " >= ";
            case LESSER:
                return " < ";
            case LESSER_OR_EQUAL:
                return " <= ";
            default:
                return null;
        }
    }

    public boolean evaluateFloat(float val1, float val2){
        switch (this) {
            case EQUAL:
                return val1 == val2;
            case NOT_EQUAL:
                return val1 != val2;
            case GREATER:
                return val1 > val2;
            case GREATER_OR_EQUAL:
                return val1 >= val2;
            case LESSER:
                return val1 < val2;
            case LESSER_OR_EQUAL:
                return val1 <= val2;
            default:
                return false; // TODO: use custom exception
        }
    }

    public String toStringKeyword() {
        switch (this) {
            case EQUAL:
                return "'EQUAL'";
            case NOT_EQUAL:
                return "'NOT_EQUAL'";
            case GREATER:
                return "'GREATER'";
            case GREATER_OR_EQUAL:
                return "'GREATER_OR_EQUAL'";
            case LESSER:
                return "'LESSER'";
            case LESSER_OR_EQUAL:
                return "'LESSER_OR_EQUAL'";
            default:
                return null;
        }
    }
}
