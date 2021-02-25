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

import databaseManagement.ConnectionParameters;
import databaseManagement.MysqlDatabaseConnection;
import databaseManagement.SqualElementsManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import squalParser.ParseException;
import squalParser.SQualParser;

import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SQuaLScriptTest {

    private Path workingDirectoryPath = Paths.get(System.getProperty("user.dir"));

    private static SQualParser squalParser = new SQualParser(new StringReader(" "));

    private ConnectionParameters connectionParameters =
            new ConnectionParameters("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "Test", "UTC");

    @Test
    public void testSQuaLScriptSubmission() {
        SQuaLScript squalScript = null;


// Test QWith query execution
        squalParser.ReInit(new StringReader("REPLACE CONTRACTTYPE ct1(" +
                "comp BY FUNCTION '../UDF_scripts/completeness.py' LANGUAGE Python" +
                ");" +
                "REPLACE CONTRACT testNonDeclaredContract " +
                "(ct1.comp > 0.5 AND ct1.toto >= 50);" +
                "REPLACE CONTRACT completeness " +
                "(ct1.comp > 0.5 AND ct1.toto >= 50);" +
                "{ SELECT * FROM A, B WHERE A.a > 1 OR B.f IS NULL} " +
                "QWITH completeness AND ct1.toto >= 50 AND ct1.comp = 0.6;"));
        MysqlDatabaseConnection c = null;
        try {
            c = new MysqlDatabaseConnection(connectionParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SqualElementsManager sem = new SqualElementsManager(c);

        try {
            c.submitDataManipulation("CREATE TABLE A(a INTEGER, b VARCHAR(255), c FLOAT)");
            c.submitDataManipulation("INSERT INTO A VALUES(1, \"Test\", 12.6)");
            c.submitDataManipulation("INSERT INTO A VALUES(2, \"Test2\", 0.6)");
            c.submitDataManipulation("INSERT INTO A VALUES(2, NULL, 0.6)");

            c.submitDataManipulation("CREATE TABLE B(d INTEGER, e VARCHAR(255), f FLOAT)");
            c.submitDataManipulation("INSERT INTO B VALUES(1, NULL, NULL)");
            c.submitDataManipulation("INSERT INTO B VALUES(2, \"Test2\", 0.6)");
            c.submitDataManipulation("INSERT INTO B VALUES(2, NULL, 0.6)");

            squalScript = squalParser.squalScript(sem, new DimensionMap(), new ContractMap());
            try {
                squalScript.evaluate(workingDirectoryPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Assertions.fail("Problem during execution of a Qwith query." + e);
        } finally {
            c.submitDataManipulation("DROP TABLE A");
            c.submitDataManipulation("DROP TABLE B");
            c.close();
        }
    }
}
