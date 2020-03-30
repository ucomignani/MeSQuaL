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

import com.mysql.cj.exceptions.ConnectionIsClosedException;

import java.sql.*;
import java.util.List;


public class MysqlDatabaseConnection implements DatabaseConnection {

    private ConnectionParameters connectionParameters;
    private Connection connection;

    public MysqlDatabaseConnection(String driverName, String host, int port,
                                   String username, String password, String dbName, String serverTimezone) throws Exception {

        this.connectionParameters = new ConnectionParameters(driverName, host, port,
                username, password, dbName, serverTimezone);

        connectToMysqlFromConnectionParameters(connectionParameters);
    }

    public MysqlDatabaseConnection(ConnectionParameters connectionParameters) throws Exception {

        this.connectionParameters = connectionParameters;

        connectToMysqlFromConnectionParameters(connectionParameters);
    }

    /**
     * Generate the url used to connect to a MySQL database (without credentials)
     *
     * @param connectionParameters
     * @return the url used for the connection
     */
    private String generateUrlMysqlConnection(ConnectionParameters connectionParameters) {
        StringBuilder urlConnection = new StringBuilder();
        urlConnection.append("jdbc:mysql://")
                .append(connectionParameters.getHost()).append(":").append(connectionParameters.getPort())
                .append("/").append(connectionParameters.getDbName())
                .append("?serverTimezone=").append(connectionParameters.getServerTimezone());
        return urlConnection.toString();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    @Override
    public void setConnectionParameters(ConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

    /**
     * Submit a query manipulating data (use statement.execute(query))
     *
     * @param query
     * @return
     */
    @Override
    public boolean submitDataManipulation(String query) {

        checkConnectionBeforeQuery();

        try {
            Statement statement = this.connection.createStatement();

            return statement.execute(query);

        } catch (SQLException e) {
            System.out.println("Error when executing query: " + query + "\n" + e);
        }
        return false;
    }

    private void checkConnectionBeforeQuery() {
        try {
            if (this.connection.isClosed()) {
                System.out.println("Cannot perform a query through a closed connection. " +
                        "Please reconnect to the database.");
                throw new ConnectionIsClosedException();
            }
        } catch (SQLException e) {
            System.out.println("Error when checking if the connection is closed: " + e);
        }
    }

    /**
     * Submit a query updating data (use statement.executeUpdate(query))
     *
     * @param query
     * @return
     */
    @Override
    public int submitUpdate(String query) {

        checkConnectionBeforeQuery();

        try {
            Statement statement = this.connection.createStatement();

            return statement.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println("Error when executing query: " + query + "\n" + e);
        }
        return 0;
    }

    @Override
    public int createTable(String name, List<Attribute> attributeList) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(name).append(" (");
        String prefix = "";
        for(Attribute att : attributeList){
            query.append(prefix);
            prefix = ",";
            query.append(att.getName()).append(" ").append(att.getType());
        }
        query.append(");");

        return submitUpdate(query.toString());
    }

    @Override
    public int dropTable(String name) {
        StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS ")
                .append(name).append(";");

        return submitUpdate(query.toString());
    }

    @Override
    public int insertTuple(String tableName, List<String> values) {
        StringBuilder query = new StringBuilder("INSERT INTO ")
                .append(tableName).append(" VALUES (");
        String prefix = "";
        for(String val : values){
            query.append(prefix);
            prefix = ",";
            query.append(val);
        }
        query.append(");");

        return submitUpdate(query.toString());    }

    /**
     * Submit a query to visualize data (use statement.executeQuery(query))
     *
     * @param query
     * @return
     */
    @Override
    public ResultSet submitQuery(String query) {

        checkConnectionBeforeQuery();

        try {
            Statement statement = this.connection.createStatement();

            return statement.executeQuery(query);

        } catch (SQLException e) {
            System.out.println("Error when executing query: " + query + "\n" + e);
        }
        return null;
    }

    /**
     * Relaunch a new connection to MySQL (close the previous connection if needed)
     */
    @Override
    public void restartConnection() throws Exception {

        closeConnectionIfOpen();

        String urlConnection = generateUrlMysqlConnection(this.connectionParameters);
        connectionToMysqlFromUrl(urlConnection, this.connectionParameters.getUsername(), this.connectionParameters.getPassword());
    }

    private void closeConnectionIfOpen() {
        try {
            if (!this.connection.isClosed())
                this.connection.close();
        } catch (SQLException e) {
            System.out.println("Error when checking if the connection is closed: " + e);
        }
    }

    private void connectionToMysqlFromUrl(String urlConnection, String username, String password) throws Exception {
        try {
            this.connection = DriverManager.getConnection(urlConnection, username, password);
        } catch (SQLException e) {
            throw new Exception("Error when connecting to Mysql: " + e);
        }
    }

    private void connectToMysqlFromConnectionParameters(ConnectionParameters connectionParameters) throws Exception {
        String urlConnection = generateUrlMysqlConnection(connectionParameters);

        try {
            Class.forName(connectionParameters.getDriverName()); // Exception if the driver is unavailable
        } catch (ClassNotFoundException e) {
            System.out.println("Mysql jdbc class not found: " + e);
        }

        connectionToMysqlFromUrl(urlConnection, connectionParameters.getUsername(), connectionParameters.getPassword());
    }

    /**
     * Close the current connection to Mysql
     */
    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Error when closing the connection to Mysql: " + e);
        }
    }

}
