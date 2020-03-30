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

package databaseManagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public interface DatabaseConnection {
    Connection getConnection();

    void setConnection(Connection connection);

    ConnectionParameters getConnectionParameters();

    void setConnectionParameters(ConnectionParameters connectionParameters);

    boolean submitDataManipulation(String query);

    int submitUpdate(String query);

    int createTable(String tableName, List<Attribute> attributeList);

    int dropTable(String tableName);

    int insertTuple(String tableName, List<String> values);

    ResultSet submitQuery(String query);

    void restartConnection() throws Exception;

    void close();
}
