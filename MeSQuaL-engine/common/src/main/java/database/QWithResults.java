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

import java.sql.ResultSet;
import java.util.List;

public class QWithResults {
    private SQLResult sqlQueryResults = null;
    private List<UDFResult> udfResults = null;

    public QWithResults(SQLResult sqlQueryResults, List<UDFResult> udfResults) {
        this.sqlQueryResults = sqlQueryResults;
        this.udfResults = udfResults;
    }

    public SQLResult getSqlQueryResults() {
        return sqlQueryResults;
    }

    public void setSqlQueryResults(SQLResult sqlQueryResults) {
        this.sqlQueryResults = sqlQueryResults;
    }

    public List<UDFResult> getUdfResults() {
        return udfResults;
    }

    public void setUdfResults(List<UDFResult> udfResults) {
        this.udfResults = udfResults;
    }
}
