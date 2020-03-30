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
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DimensionTest {

    private Path workingDirectoryPath = Paths.get(System.getProperty("user.dir"));

    private ConnectionParameters connectionParameters =
            new ConnectionParameters("com.mysql.cj.jdbc.Driver", "localhost", 3306,
                    "root", "rootPassword", "sensor", "UTC");

    @Test
    public void testMysqlDatabaseConnectionQuerySubmission() {

        MysqlDatabaseConnection connection = null;
        try {
            connection = new MysqlDatabaseConnection(connectionParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Path> inputFiles = new ArrayList<>();
        inputFiles.add(Paths.get("completeness.ipynb"));

        Dimension dimension = new Dimension("ct1", new Name("TestCompleteness"), new Scope(ScopeLevel.DATABASE),
                SqlType.FLOAT, UDFLanguage.PYTHON, "../UDF_scripts/completeness.py");


        Path outputFilePath = Paths.get("../requests_results/output_test_dimension.txt");
        dimension.evaluate(workingDirectoryPath, connection, inputFiles, outputFilePath);

    }

}
