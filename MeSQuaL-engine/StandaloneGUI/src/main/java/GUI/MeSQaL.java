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
import databaseManagement.ConnectionParameters;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MeSQaL extends JPanel implements ChangeListener {

    /**
     * main part of the GUI
     */
    JFrame frame = new JFrame("MeSQuaL");
    private JPanel mainJPanel;

    /**
     * Main engine
     */
    private SQuaLqueryEngine squalQueryEngine;

    {
        try {
            squalQueryEngine = new SQuaLqueryEngine();
        } catch (Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(frame, exception);
        }
    }

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
    private JTable queriesHistoryJTable;
    private JTable queriesHistoryTabJTable;
    private JPanel queriesHistoryJPanel;
    private JPanel contractJPanel;
    private JPanel contractTypeJPanel;
    private JTable contractJTable;
    private JTable contractTypeJTable;
    private JButton applyButton;
    private JButton resetToDefaultButton;
    private JPasswordField passwordJText;
    private JTextField driverJTextField;
    private JTextField hostJTextField;
    private JTextField portJTextField;
    private JTextField usernameJTextField;
    private JTextField databaseNameJTextField;
    private JTextField timeZoneJTextField;
    private JPanel settingJPanel;
    private JPanel settingButtonsJPanel;

    public MeSQaL() {

        /**
         * use a change listener to detect panel selection
         */
        queryPanel.addChangeListener(this);

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
         * Results panel
         */


        /**
         * Queries history panel
         */
        queryAndPrintResultInJTable("SELECT * FROM MeSQuaLqueries;", queriesHistoryJTable);
        queryAndPrintResultInJTable("SELECT * FROM MeSQuaLqueries;", queriesHistoryTabJTable);

        /**
         * Contract panel
         */
        queryAndPrintResultInJTable("SELECT * FROM CONTRACT;", contractJTable);

        /**
         * Contract type history panel
         */
        queryAndPrintResultInJTable("SELECT * FROM CONTRACTTYPE;", contractTypeJTable);

        /**
         * DBMS settings tab
         */
        // set new settings
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectionParameters cp = createConnectionParametersFromFields();
                try {
                    squalQueryEngine.changeConnection(cp);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(frame, exception);
                }
            }
        });

        // reset fields to default values
        resetToDefaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillDbmsFiels(squalQueryEngine.getDefaultConnectionParameters());
            }
        });
    }

    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();

        switch (selectedIndex) {

            case 0:
                /**
                 * Query tab
                 */
                queryAndPrintResultInJTable("SELECT * FROM MeSQuaLqueries;", queriesHistoryJTable);
                break;
            case 1:

                /**
                 * Results panel
                 */
                queryAndPrintResultInJTable("SELECT * FROM MeSQuaLresults;", sqlResultsJTable);
                break;
            case 2:
                /**
                 * Queries history panel
                 */
                queryAndPrintResultInJTable("SELECT * FROM MeSQuaLqueries;", queriesHistoryTabJTable);
                break;
            case 3:
                /**
                 * Contract panel
                 */
                queryAndPrintResultInJTable("SELECT * FROM CONTRACT;", contractJTable);
                break;
            case 4:
                /**
                 * Contract type history panel
                 */
                queryAndPrintResultInJTable("SELECT * FROM CONTRACTTYPE;", contractTypeJTable);
                break;
            case 5:
                /**
                 * DBMS settings tab
                 */
                ConnectionParameters curConnectionParameters = squalQueryEngine.getConnectionParameters();
                // fill fields with current values when tab is selected
                fillDbmsFiels(curConnectionParameters);
                break;
            default:
                try {
                    throw new Exception("Unknown tab number in StateChanged.");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
        }
    }

    private void fillDbmsFiels(ConnectionParameters curConnectionParameters) {
        this.driverJTextField.setText(curConnectionParameters.getDriverName());
        this.hostJTextField.setText(curConnectionParameters.getHost());
        this.portJTextField.setText(Integer.toString(curConnectionParameters.getPort()));
        this.usernameJTextField.setText(curConnectionParameters.getUsername());
        this.passwordJText.setText(curConnectionParameters.getPassword());
        this.databaseNameJTextField.setText(curConnectionParameters.getDbName());
        this.timeZoneJTextField.setText(curConnectionParameters.getServerTimezone());
    }

    private ConnectionParameters createConnectionParametersFromFields() {
        return new ConnectionParameters(this.driverJTextField.getText(),
                this.hostJTextField.getText(),
                Integer.parseInt(this.portJTextField.getText()),
                this.usernameJTextField.getText(),
                this.passwordJText.getPassword().toString(),
                this.databaseNameJTextField.getText(),
                this.timeZoneJTextField.getText());
    }

    private void clearQueryTest() {
        queryText.setText(null);
    }

    private void recoverAndExecuteQuery() {
        // get query string
        String queryString = queryText.getText();
        System.out.println("Query string :" + queryString);

        QWithResults qWithQueryResult = squalQueryEngine.executeQuery(queryString);

        // print SQL query results in Results tab
        queryAndPrintResultInJTable("SELECT * FROM UDFresults;", udfResultsJTable);
        queryAndPrintResultInJTable("SELECT * FROM MeSQuaLresults;", sqlResultsJTable);
    }

    private void queryAndPrintResultInJTable(String query, JTable jtable) {
        DataTable dt = squalQueryEngine.executeSqlQuery(query);
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
