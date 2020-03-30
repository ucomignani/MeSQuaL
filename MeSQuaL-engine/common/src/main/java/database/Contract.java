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

import java.util.ArrayList;
import java.util.List;

public class Contract {

    private boolean isReplace;
    private String contractName;
    private List<ContractTypeName> contractTypeNameList;
    private List<Constraint> constraintList;


    public Contract(boolean isReplace,
                    String contractName,
                    List<ContractTypeName> contractTypeNameList,
                    List<Constraint> constraintList) {
        this.isReplace = isReplace;
        this.contractName = contractName;
        this.contractTypeNameList = contractTypeNameList;
        this.constraintList = constraintList;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean replace) {
        isReplace = replace;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public List<ContractTypeName> getContractTypeNameList() {
        return contractTypeNameList;
    }

    public void setContractTypeNameList(ArrayList<ContractTypeName> contractTypeNameList) {
        this.contractTypeNameList = contractTypeNameList;
    }

    public List<Constraint> getConstraintList() {
        return constraintList;
    }

    public void setConstraintList(ArrayList<Constraint> constraintList) {
        this.constraintList = constraintList;
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        query.append(this.isReplace ? "REPLACE " : "CREATE ")
                .append("CONTRACT ").append(this.contractName)
                .append(" FROM ").append(printConstraintNamesList()).append(" (")
                .append(printConstraintList()).append(");");
        return new SQLString(query.toString());
    }

    private String printConstraintList() {
        StringBuilder ctString = new StringBuilder();
        boolean first = true;

        for (Constraint ct : this.constraintList) {
            if (!first) ctString.append(" AND ");
            ctString.append(ct.toSQLString().getQueryString());
            first = false;
        }
        return ctString.toString();
    }

    private String printConstraintNamesList() {
        StringBuilder ctnString = new StringBuilder();

        for(int i=0; i < this.contractTypeNameList.size(); i++) {
            ctnString.append(this.contractTypeNameList.get(i).toSQLString().getQueryString());
            if(i < this.contractTypeNameList.size()-1){
                ctnString.append(", ");
            }
        }
        return ctnString.toString();
    }
}
