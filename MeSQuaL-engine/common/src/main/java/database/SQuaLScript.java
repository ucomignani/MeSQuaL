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

import databaseManagement.DatabaseConnection;
import databaseManagement.SqualElementsManager;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class SQuaLScript {
    private List<SQuaLScriptBlock> squalScriptBlocksList = null;
    private DimensionMap dimensionMap;
    private ContractMap contractMap;
    private SqualElementsManager squalElementsManager;

    public SQuaLScript(List<SQuaLScriptBlock> squalScriptBlocksList, DimensionMap dimensionMap,
                       ContractMap contractMap, SqualElementsManager squalElementsManager) {
        this.squalScriptBlocksList = squalScriptBlocksList;
        this.squalElementsManager = squalElementsManager;
        this.dimensionMap = dimensionMap;
        loadDimensionMapFromDB();
        this.contractMap = contractMap;
        loadContractMapFromDB();
    }

    private void loadContractMapFromDB() {
        try {
            this.contractMap.putAll(this.squalElementsManager.getContractMap());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDimensionMapFromDB() {
        try {
            this.dimensionMap.putAll(this.squalElementsManager.getDimensionMap());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseConnection getDbConnection() {
        return squalElementsManager.getDbConnector();
    }

    public List<SQuaLScriptBlock> getSqualScriptBlocksList() {
        return squalScriptBlocksList;
    }

    public void setSqualScriptBlocksList(List<SQuaLScriptBlock> squalScriptBlocksList) {
        this.squalScriptBlocksList = squalScriptBlocksList;
    }

    public QWithResults evaluate(Path workingDirectoryPath) throws Exception {
        if (this.dimensionMap == null) {
            System.err.println("Needs a dimension map to evaluate a SQuaL script");
        } else {
            for (SQuaLScriptBlock squalBlock : this.squalScriptBlocksList) {
                switch (squalBlock.getBlockType()) {
                    case CONTRACT_TYPE:
                        ContractType contractType = squalBlock.getContractType();
                        List<Dimension> dimensionList = contractType.getDimensionList();
                        for (Dimension dim : dimensionList) {
                            Name dimensionName = dim.getDimensionName();

                            // if REPLACE or if the dimension did not exists, add it to the dimension map, else warn user
                            if (contractType.isReplace() || !this.dimensionMap.containsKey(dimensionName.getName())) {
                                this.dimensionMap.put(dimensionName.getName(), dim);
                                squalElementsManager.addContractType(contractType);
                            } else {
                                throw new Exception("Dimension " + dimensionName.getName() + " already exists." +
                                        " Please create your ContractType using REPLACE instead of CREATE " +
                                        "if you want to replace the old definition.");
                            }
                        }
                        break;

                    case CONTRACT:
                        Contract contract = squalBlock.getContract();
                        System.out.println("Contract " + contract.getContractName());
                        // if REPLACE or if the contract did not exists, add it to the contract map, else warn user
                        if (contract.isReplace() || !this.contractMap.containsKey(contract.getContractName())) {
                            this.contractMap.put(contract.getContractName(), contract);
                            squalElementsManager.addContract(contract);
                        } else {
                            throw new Exception("Contract " + contract.getContractName() + " already exists." +
                                    " Please create your Contract using REPLACE instead of CREATE " +
                                    "if you want to replace the old definition.");
                        }
                        break;

                    case QWITH_QUERY:
                        return squalBlock.getQwithQuery().executeQuery(workingDirectoryPath, getDbConnection(), this.dimensionMap, this.contractMap);

                    default:
                        System.err.println("Wrong SQuaLBlock type.");
                }
            }
        }
        return null;
    }
}
