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

import UDFCalls.UDFLanguage;
import databaseManagement.ConnectionParameters;
import databaseManagement.MysqlDatabaseConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import squalParser.ParseException;
import squalParser.SQualParser;

import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QWithSubQueryTest {
    private Path workingDirectoryPath = Paths.get(System.getProperty("user.dir"));

    private static SQualParser squalParser = new SQualParser(new StringReader(" "));

    private ConnectionParameters connectionParameters =
            new ConnectionParameters("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");

    @Test
    public void testMysqlQwithSubQuerySubmission() throws Exception {
        QwithQuery qWithQuery = null;


        // Test QWith query execution
        squalParser.ReInit(new StringReader("{ SELECT * FROM A " +
                "WHERE A.a IN {{SELECT * FROM Ap} QWITH ct1.completeness > 0.5 AND Test}" +
                "} QWITH ct1.completeness > 0.5 AND ct1.toto >= 50;"));
        MysqlDatabaseConnection c = new MysqlDatabaseConnection(connectionParameters);

        Dimension dimension = new Dimension("ct1", new Name("completeness"), new Scope(ScopeLevel.DATABASE),
                SqlType.FLOAT, UDFLanguage.PYTHON, "../UDF_scripts/completeness.py");

        DimensionMap dimMap = new DimensionMap();
        ContractMap contMap = new ContractMap();
        dimMap.put(dimension.getDimensionName().getName(), dimension);

        c.submitDataManipulation("DROP TABLE A");
        c.submitDataManipulation("DROP TABLE Ap");

        c.submitDataManipulation("CREATE TABLE A(a INTEGER, b VARCHAR(255), c FLOAT)");
        c.submitDataManipulation("INSERT INTO A VALUES(1, \"Test\", 12.6)");
        c.submitDataManipulation("INSERT INTO A VALUES(2, \"Test2\", 0.6)");
        c.submitDataManipulation("INSERT INTO A VALUES(2, NULL, 0.6)");
        c.submitDataManipulation("INSERT INTO A VALUES(3, NULL, 0.6)");

        c.submitDataManipulation("CREATE TABLE Ap(d INTEGER)");
        c.submitDataManipulation("INSERT INTO Ap VALUES(1)");
        c.submitDataManipulation("INSERT INTO Ap VALUES(2)");

        try {
            qWithQuery = squalParser.qwithQuery();
            qWithQuery.executeQuery(workingDirectoryPath, c, dimMap, contMap);
        } catch (ParseException e) {
            e.printStackTrace();
            Assertions.fail("Problem during execution of a Qwith query." + e);
        } finally {
            //c.submitDataManipulation("DROP TABLE A");
            //c.submitDataManipulation("DROP TABLE Ap");
            c.close();
        }
    }
}
