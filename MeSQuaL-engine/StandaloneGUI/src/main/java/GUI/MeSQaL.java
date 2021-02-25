/*
 *     This file is part of MeSQuaL2.
 *
 *     Foobar is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MeSQuaL2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MeSQuaL2. If not, see <https://www.gnu.org/licenses/>.
 */

package GUI;


import Socket.SQuaLqueryEngine;
import database.DataTable;
import database.QWithResults;
import database.SQLResult;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class MeSQaL {
    private JPanel mainJPanel;

    /*****
     * QUERIES
     */
    private JPanel queryMainPanel;
    private JTabbedPane queryPanel;
    private JTextArea queryText;
    private JButton executeButton;
    private JButton clearButton;
    /*****
     * RESULTS
     */
    private JPanel resultsMainPanel;
    private JScrollPane resultsDisplayScrollPane;
    private JPanel results;
    private JTable sqlResultsJTable;
    private JTable udfResultsJTable;

    public MeSQaL() {


        /**
         * Query tab
         */
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recoverAndExecuteQuery();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearQueryTest();
            }
        });


        PieChart pieChart = new PieChart("Which operating system are you using?");

        results.setLayout(new GridLayout());
        //results.add(pieChart.getChartPanel(), BorderLayout.CENTER);
        results.validate();
        /**
         * Result tab
         */


    }

    private void clearQueryTest() {
        queryText.setText(null);
    }

    private void recoverAndExecuteQuery() {
        // get query string
        String queryString = queryText.getText();
        System.out.println("Query string :" + queryString);

        SQuaLqueryEngine qe = new SQuaLqueryEngine();
        QWithResults qWithQueryResult = qe.executeQuery(queryString);

        // print SQL query results in Results tab
        queryAndPrintResultInJTable("SELECT * FROM UDFresults;", udfResultsJTable);
        queryAndPrintResultInJTable("SELECT * FROM MeSQuaLresults;", sqlResultsJTable);
    }

    private void queryAndPrintResultInJTable(String query, JTable jtable) {
        SQuaLqueryEngine sqe = new SQuaLqueryEngine();
        DataTable dt = sqe.executeSqlQuery(query);
        jtable.setModel(buildTableModel(dt));
    }

    public static DefaultTableModel buildTableModel(DataTable dt) {
        return new DefaultTableModel(dt.getData(), dt.getColumnNames());
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("MeSQuaL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(new MeSQaL().mainJPanel);

        frame.setSize(750, 550);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
