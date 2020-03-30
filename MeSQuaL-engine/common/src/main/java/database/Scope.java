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

public class Scope {
    private ScopeLevel scopeLevel = ScopeLevel.UNKNOWN;
    private Name targetName = null;

    private boolean hasArgument = false;

    public Scope() {
    }

    public Scope(ScopeLevel c) {
        scopeLevel = c;
    }

    public Scope(ScopeLevel c, Name n) {
        scopeLevel = c;
        targetName = n;
        if (n != null) hasArgument = true;
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        query.append(this.scopeLevel);

        if (this.targetName != null)
            query.append(" ").append(this.targetName.toSQLString().getQueryString());

        return new SQLString(query.toString());
    }

}
