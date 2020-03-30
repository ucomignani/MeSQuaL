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

package javacc;

import database.BooleanOperator;
import database.QwithQuery;
import databaseManagement.ConnectionParameters;
import databaseManagement.MysqlDatabaseConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import squalParser.ParseException;
import squalParser.SQualParser;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

public class squalParserTest {

    private static SQualParser squalParser = new SQualParser(new StringReader(" "));

    private ConnectionParameters connectionParameters =
            new ConnectionParameters("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");

    private String complexSQLQuery = "SELECT e.employee_id AS \\\"Employee #\\\"\n" +
            "  , e.first_name || ' ' || e.last_name AS \\\"Name\\\"\n" +
            "  , e.email AS \\\"Email\\\"  , e.phone_number AS \\\"Phone\\\"  , TO_CHAR(e.hire_date, 'MM/DD/YYYY')" +
            " AS \\\"Hire Date\\\"  , TO_CHAR(e.salary, 'L99G999D99', 'NLS_NUMERIC_CHARACTERS = ''.,'' NLS_CURRENCY = ''$''') AS \\\"Salary\\\"  , e.commission_pct AS \\\"Comission %\\\" " +
            " , 'works as ' || j.job_title || ' in ' || d.department_name || ' department (manager: '  || dm.first_name || ' ' || dm.last_name || ') and immediate supervisor: ' || m.first_name || ' ' || m.last_name AS \\\"Current Job\\\"  , TO_CHAR(j.min_salary, 'L99G999D99', 'NLS_NUMERIC_CHARACTERS = ''.,'' NLS_CURRENCY = ''$''') || ' - ' || TO_CHAR(j.max_salary, 'L99G999D99', 'NLS_NUMERIC_CHARACTERS = ''.,'' NLS_CURRENCY = ''$''') AS \\\"Current Salary\\\"  ," +
            " l.street_address || ', ' || l.postal_code || ', ' || l.city || ', ' || l.state_province || ', '  || c.country_name || ' (' || r.region_name || ')' AS \\\"Location\\\"  , jh.job_id AS \\\"History Job ID\\\"  , 'worked from ' || TO_CHAR(jh.start_date, 'MM/DD/YYYY') || ' to ' || TO_CHAR(jh.end_date, 'MM/DD/YYYY') ||  ' as ' || jj.job_title || ' in ' || dd.department_name || ' department' AS \\\"History Job Title\\\" " +
            "FROM employees e -- to get title of current job_id JOIN jobs j ON e.job_id = j.job_id -- to get name of current manager_id " +
            "LEFT JOIN employees m ON e.manager_id = m.employee_id -- to get name of current department_id LEFT JOIN departments d ON d.department_id = e.department_id -- to get name of manager of current department -- (not equal to current manager and can be equal to the employee itself) LEFT JOIN employees dm ON d.manager_id = dm.employee_id -- to get name of location LEFT JOIN locations l ON d.location_id = l.location_id LEFT JOIN countries c ON l.country_id = c.country_id LEFT JOIN regions r ON c.region_id = r.region_id -- to get job history of employee " +
            "LEFT JOIN job_history jh ON e.employee_id = jh.employee_id -- to get title of job history job_id LEFT JOIN jobs jj ON jj.job_id = jh.job_id -- to get namee of department from job history LEFT JOIN departments dd ON dd.department_id = jh.department_id ORDER BY e.employee_id\n" +
            "WHERE 1 > 2";


    @Test
    public void testBooleanOperator() {

        //test OR
        try {
            squalParser.ReInit(new StringReader("OR"));
            Assertions.assertEquals(BooleanOperator.OR,
                    squalParser.binaryBooleanOperator());

            squalParser.ReInit(new StringReader("or"));
            Assertions.assertEquals(BooleanOperator.OR,
                    squalParser.binaryBooleanOperator());

            squalParser.ReInit(new StringReader("oR"));
            Assertions.assertEquals(BooleanOperator.OR,
                    squalParser.binaryBooleanOperator());

            squalParser.ReInit(new StringReader("oR"));
            Assertions.assertEquals("OR", squalParser.binaryBooleanOperator().toString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of OR");
        }

        // test AND
        try {
            squalParser.ReInit(new StringReader("AND"));
            Assertions.assertEquals(BooleanOperator.AND,
                    squalParser.binaryBooleanOperator());

            squalParser.ReInit(new StringReader("and"));
            Assertions.assertEquals(BooleanOperator.AND,
                    squalParser.binaryBooleanOperator());

            squalParser.ReInit(new StringReader("aNd"));
            Assertions.assertEquals(BooleanOperator.AND,
                    squalParser.binaryBooleanOperator());

            squalParser.ReInit(new StringReader("aNd"));
            Assertions.assertEquals("AND", squalParser.binaryBooleanOperator().toString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of AND");
        }
    }

    @Test
    public void testBooleanLiteral() {

        //test TRUE
        try {
            squalParser.ReInit(new StringReader("TRUE"));
            Assertions.assertTrue(squalParser.booleanLiteral().getLiteral().booleanValue());

            squalParser.ReInit(new StringReader("true"));
            Assertions.assertTrue(squalParser.booleanLiteral().getLiteral().booleanValue());

            squalParser.ReInit(new StringReader("tRuE"));
            Assertions.assertTrue(squalParser.booleanLiteral().getLiteral().booleanValue());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of TRUE\n" + e);
        }

        // test FALSE
        try {
            squalParser.ReInit(new StringReader("FALSE"));
            Assertions.assertFalse(squalParser.booleanLiteral().getLiteral().booleanValue());


            squalParser.ReInit(new StringReader("false"));
            Assertions.assertFalse(squalParser.booleanLiteral().getLiteral().booleanValue());

            squalParser.ReInit(new StringReader("FaLsE"));
            Assertions.assertFalse(squalParser.booleanLiteral().getLiteral().booleanValue());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of FALSE.\n" + e);
        }
    }

    @Test
    public void testUnaryBooleanExpression() {

        try {
            squalParser.ReInit(new StringReader("NOT TRUE"));
            Assertions.assertEquals("NOT TRUE",
                    squalParser.unaryBooleanExpression().toSQLString().getQueryString());

            squalParser.ReInit(new StringReader("NoT nOt fAlSe"));
            Assertions.assertEquals("NOT NOT FALSE",
                    squalParser.unaryBooleanExpression().toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of an unary boolean expression.");
        }

    }

    @Test
    public void testBinaryBooleanExpression() {

        try {
            squalParser.ReInit(new StringReader("FaLsE aNd NOT TRUE"));
            Assertions.assertEquals("FALSE AND NOT TRUE",
                    squalParser.binaryBooleanExpression().toSQLString().getQueryString());

            squalParser.ReInit(new StringReader("NOt nOT FaLSE oR NoT tRuE"));
            Assertions.assertEquals("NOT NOT FALSE OR NOT TRUE",
                    squalParser.binaryBooleanExpression().toSQLString().getQueryString());

            squalParser.ReInit(new StringReader("NOt nOT NOT NOT ( NOT 2 = 'toto' oR NoT 1<3 )"));
            Assertions.assertEquals("NOT NOT NOT NOT NOT 2 = 'toto' OR NOT 1 < 3",
                    squalParser.binaryBooleanExpression().toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a binary boolean expression.\n" + e);
        }

    }

    @Test
    public void testComparisonExpression() {

        try {

            squalParser.ReInit(new StringReader("2 = 2"));
            Assertions.assertEquals("2 = 2",
                    squalParser.comparisonExpression().toSQLString().getQueryString());

            squalParser.ReInit(new StringReader("1 >= 'toto'"));
            Assertions.assertEquals("1 >= 'toto'",
                    squalParser.comparisonExpression().toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a comparison expression." + e);
        }

    }

    @Test
    public void testContract_type_name() {

        try {

            squalParser.ReInit(new StringReader("Schema1.table1"));
            System.out.println(squalParser.contract_type_name().toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a contract type name." + e);
        }

    }

    @Test
    public void testContract() {

        try {

            squalParser.ReInit(new StringReader("CREATE CONTRACT contractTest " +
                    "FROM tab.ctype1, tab.ctype2 ( " +
                    "Const_1 = TRUE " +
                    "AND NOT(Const2 >= 1) );"));
            Assertions.assertEquals("CREATE CONTRACT contractTest " +
                            "FROM tab.ctype1, tab.ctype2 (Const_1 = TRUE AND NOT(Const2 >= 1));",
                    squalParser.contract().toSQLString().getQueryString());

            squalParser.ReInit(new StringReader("CREATE CONTRACT contractTest " +
                    "FROM tab.ctype1, tab.ctype2 ( " +
                    "Const_1 = TRUE " +
                    "AND Const2 >= 1 );"));
            System.out.println(squalParser.contract().toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a contract." + e);
        }

    }

    @Test
    public void testDimension() {

        try {

            squalParser.ReInit(new StringReader("dimA VARCHAR ON ROW A.b BY FUNCTION '~/test/testFun.extension' LANGUAGE Python"));
            Assertions.assertEquals("dimA VARCHAR ON ROW A.b BY FUNCTION '~/test/testFun.extension' LANGUAGE PYTHON",
                    squalParser.dimension("ct1").toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a dimension." + e);
        }

    }

    @Test
    public void testContractType() {

        try {

            squalParser.ReInit(new StringReader("CREATE CONTRACTTYPE testCT(" +
                    "dimA VARCHAR ON ROW A.b BY FUNCTION '~/test/testFun.extension' LANGUAGE Python," +
                    "dimB INT ON CELL A.b BY FUNCTION './test/testFun.extension' LANGUAGE SQL);"));
            Assertions.assertEquals("CREATE CONTRACTTYPE testCT (" +
                            "dimA VARCHAR ON ROW A.b BY FUNCTION '~/test/testFun.extension' LANGUAGE Python," +
                            "dimB INT ON CELL A.b BY FUNCTION './test/testFun.extension' LANGUAGE SQL);",
                    squalParser.contractType().toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a contracttype." + e);
        }

    }

    @Test
    public void testQwithQuery() {
        QwithQuery qWithQuery = null;
        complexSQLQuery = "{SELECT * FrOm Patient, Admission WheRe Patient.test = true AND Patient.id = Admission.PatientId}";

        try {

            squalParser.ReInit(new StringReader(complexSQLQuery
                    + " QWITH test = 22 AND toto >= 50;"));
            qWithQuery = squalParser.qwithQuery();

            Assertions.assertEquals("{SELECT * FrOm Patient, Admission WheRe Patient.test = true AND Patient.id = Admission.PatientId} QWITH test = 22, toto >= 50;",
                    qWithQuery.toSQLString().getQueryString());

        } catch (ParseException e) {
            Assertions.fail("Problem during parsing of a contracttype." + e);
        }


        // Test SQL query execution
        squalParser.ReInit(new StringReader("{ SELECT a, b, c FROM A WHERE TRUE} QWITH test = 22 AND toto >= 50;"));
        MysqlDatabaseConnection c = null;
        try {
            c = new MysqlDatabaseConnection(connectionParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            qWithQuery = squalParser.qwithQuery();

            c.submitDataManipulation("CREATE TABLE A(a INTEGER, b VARCHAR(255), c FLOAT)");
            c.submitDataManipulation("INSERT INTO A VALUES(1, \"Test\", 12.6)");
            c.submitDataManipulation("INSERT INTO A VALUES(2, \"Test2\", 0.6)");

            ResultSet rs = qWithQuery.executeMySqlQuery(c);

            while (rs.next()) {
                // retrieve and print the values for the current row
                int i = rs.getInt("a");
                String s = rs.getString("b");
                float f = rs.getFloat("c");
                Assertions.assertTrue((i + " " + s + " " + f).equals("1 Test 12.6")
                        || (i + " " + s + " " + f).equals("2 Test2 0.6"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            c.submitDataManipulation("DROP TABLE A");
            c.close();
        }
    }

}
