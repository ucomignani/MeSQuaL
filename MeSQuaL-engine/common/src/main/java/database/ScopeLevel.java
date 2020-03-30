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

/**
 * Scopes:
 * CELL                       all rowid * all columns of all tables of the database
 * CELL tableName.rowid       given row id  * all columns of given table of the database
 * CELL attributeName         all rowid * given column of all tables of the database
 * CELL table.attributeName   all rowid * given column of the given table of the database
 * ROW				        all rowid * all table of the database
 * ROW tableName.rowid		given rowid * of given table of the database
 * COLUMN				        all columns of all tables of the database)
 * COLUMN table.attributeName given column of the given table of the database
 * TABLE				        all tables of the database
 * TABLE tableName 	        given table of the database
 * DATABASE			  	    given database
 */

public enum ScopeLevel {
    CELL,
    ROW,
    COLUMN,
    TABLE,
    DATABASE,
    UNKNOWN;

    public static ScopeLevel fromString(String dimScope) {
        switch (dimScope) {
            case "CELL":
                return CELL;
            case "ROW":
                return ROW;
            case "COLUMN":
                return COLUMN;
            case "TABLE":
                return TABLE;
            case "DATABASE":
                return DATABASE;
            case "UNKNOWN":
                return UNKNOWN;
            default:
                return null;
        }
    }
}
