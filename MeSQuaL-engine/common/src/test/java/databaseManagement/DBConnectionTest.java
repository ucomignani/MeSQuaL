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

import database.ContractMap;
import database.DimensionMap;
import database.SQuaLException;
import database.SQuaLScript;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import squalParser.ParseException;
import squalParser.SQualParser;

import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DBConnectionTest {

    private Path workingDirectoryPath = Paths.get(System.getProperty("user.dir"));

    public final static String PRJROOT = "./src/common/src/test/ressources/";

    // test the connection with the database
    private void runBoolTest(Connection c) throws SQLException {
        Statement stat = c.createStatement();
        try {
            stat.execute("CREATE TABLE A(a INTEGER)");
            stat.executeUpdate("INSERT INTO A VALUES(1)");
            ResultSet rs = stat
                    .executeQuery("SELECT CASE WHEN A=1 THEN 1 ELSE 0 END FROM A");
            Assertions.assertTrue(rs.next());
            Assertions.assertTrue(rs.getBoolean(1));
        } finally {
            stat.execute("DROP TABLE A");
            c.close();
        }
    }

    @Test
    public void testMysqlDriverAvailability() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Assertions.fail("Class com.mysql.cj.jdbc.Driver not found");
        }
    }

    @Test
    public void testMysqlDatabaseConnection() {

        MysqlDatabaseConnection connection =
                null;
        try {
            connection = new MysqlDatabaseConnection("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            runBoolTest(connection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail("SQL exception");
        }

        connection.close();
    }

    @Test
    public void testMysqlDatabaseConnectionWithConnectionParametersConstructor() {

        ConnectionParameters connectionParameters =
                new ConnectionParameters("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                        "root", "rootPassword", "sensor", "UTC");
        MysqlDatabaseConnection connection = null;
        try {
            connection = new MysqlDatabaseConnection(connectionParameters);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during connection.");
        }

        try {
            runBoolTest(connection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection.close();
    }

    @Test
    public void testMysqlDatabaseConnectionQuerySubmission() throws SQLException {
        MysqlDatabaseConnection connection =
                null;
        try {
            connection = new MysqlDatabaseConnection("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during connection.");
        }

        connection.submitDataManipulation("DROP TABLE IF EXISTS Patient");
        connection.submitDataManipulation("CREATE TABLE Patient(id INTEGER, name VARCHAR(255))");
        connection.submitUpdate("INSERT INTO Patient VALUES (1, 'toto')");
        ResultSet results = connection.submitQuery("SELECT * FROM Patient");

        System.out.println(results.toString());
    }

    @Test
    public void testMysqlDatabaseCreateTable() throws SQLException {
        MysqlDatabaseConnection connection =
                null;
        try {
            connection = new MysqlDatabaseConnection("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during connection.");
        }

        String tableName = "Patient";
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute("id", "INTEGER"));
        attributeList.add(new Attribute("name", "VARCHAR(255)"));

        List<String> values = new ArrayList<>();
        values.add("1");
        values.add("'toto'");

        connection.dropTable(tableName);
        connection.createTable(tableName, attributeList);
        connection.insertTuple(tableName, values);
        ResultSet results = connection.submitQuery("SELECT * FROM Patient");

        ResultSetMetaData rsmd = results.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) System.out.print(",\t");
            System.out.print(rsmd.getColumnName(i));
        }
        System.out.println("");
        while (results.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",\t");
                String columnValue = results.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

    @Test
    public void testMysqlDatabaseAddContract() throws SQLException {
        MysqlDatabaseConnection connection =
                null;
        try {
            connection = new MysqlDatabaseConnection("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during connection");
        }

        SQualParser squalParser = new SQualParser(new StringReader(" "));
        SQuaLScript squalScript = null;
        SqualElementsManager squalElementsManager = new SqualElementsManager(connection);

        squalParser.ReInit(new StringReader("CREATE CONTRACTTYPE ct1(" +
                "comp BY FUNCTION '../UDF_scripts/completeness.py' LANGUAGE Python" +
                ");" +
                "CREATE CONTRACT testNonDeclaredContract " +
                "(Ct.comp > 0.5 AND Ct.toto >= 50);" +
                "CREATE CONTRACT completeness " +
                "(Ct.comp > 0.5 AND Ct.toto >= 50);" +
                "{ SELECT * From Patient; } " +
                "QWITH completeness AND Ct.toto >= 50 AND Ct.comp = 0.6;"));

        try {
            squalScript = squalParser.squalScript(squalElementsManager, new DimensionMap(), new ContractMap());
        } catch (ParseException e) {
            e.printStackTrace();
            Assertions.fail("Problem during parsing");
        }

        try {
            SQuaLScript finalSqualScript = squalScript;
            String expectedExceptionMessage = "Dimension toto does not exists in the declared dimensions.";

            Exception exception = assertThrows(SQuaLException.class, () -> {
                finalSqualScript.evaluate(workingDirectoryPath);
            });

            String expectedMessage = expectedExceptionMessage;
            String actualMessage = exception.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during evaluation");
        }
        ResultSet results = connection.submitQuery("SELECT * FROM Patient");

        ResultSetMetaData rsmd = results.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) System.out.print(",\t");
            System.out.print(rsmd.getColumnName(i));
        }
        System.out.println("");
        while (results.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",\t");
                String columnValue = results.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

    @Test
    public void testMysql() throws SQLException {
        MysqlDatabaseConnection connection =
                null;
        try {
            connection = new MysqlDatabaseConnection("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during connection");
        }

        SQualParser squalParser = new SQualParser(new StringReader(" "));
        SQuaLScript squalScript = null;
        SqualElementsManager squalElementsManager = new SqualElementsManager(connection);

        squalParser.ReInit(new StringReader("{ SELECT * From Patient;} " +
                "QWITH completeness AND Ct.toto >= 50 AND Ct.comp = 0.6;"));

        try {
            squalScript = squalParser.squalScript(squalElementsManager, new DimensionMap(), new ContractMap());
        } catch (ParseException e) {
            e.printStackTrace();
            Assertions.fail("Problem during parsing");
        }
        try {
            assert squalScript != null;
            SQuaLScript finalSqualScript = squalScript;
            String expectedExceptionMessage = "Dimension toto does not exists in the declared dimensions.";

            Exception exception = assertThrows(SQuaLException.class, () -> {
                finalSqualScript.evaluate(workingDirectoryPath);
            });

            String expectedMessage = expectedExceptionMessage;
            String actualMessage = exception.getMessage();        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Problem during parsing");
        }
        assert connection != null;
        ResultSet results = connection.submitQuery("SELECT * FROM Patient");

        ResultSetMetaData rsmd = results.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) System.out.print(",\t");
            System.out.print(rsmd.getColumnName(i));
        }
        System.out.println();
        while (results.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",\t");
                String columnValue = results.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }
}
