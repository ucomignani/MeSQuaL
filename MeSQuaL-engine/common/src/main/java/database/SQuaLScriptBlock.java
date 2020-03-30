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

public class SQuaLScriptBlock {

    private BlockType blockType = null;
    private ContractType contractType = null;
    private Contract contract = null;
    private QwithQuery qwithQuery = null;

    public SQuaLScriptBlock(ContractType contractType) {
        this.blockType = BlockType.CONTRACT_TYPE;
        this.contractType = contractType;
    }

    public SQuaLScriptBlock(Contract contract) {
        this.blockType = BlockType.CONTRACT;
        this.contract = contract;
    }

    public SQuaLScriptBlock(QwithQuery qwithQuery) {
        this.blockType = BlockType.QWITH_QUERY;
        this.qwithQuery = qwithQuery;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public Contract getContract() {
        return contract;
    }

    public QwithQuery getQwithQuery() {
        return qwithQuery;
    }

    public boolean isContractType() {
        return this.blockType == BlockType.CONTRACT_TYPE;
    }

    public boolean isContract() {
        return this.blockType == BlockType.CONTRACT;
    }

    public boolean isQwithQuery() {
        return this.blockType == BlockType.QWITH_QUERY;
    }
}
