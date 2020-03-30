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

public class ContractType {

    private boolean isReplace;
    private String contractTypeName="";
    private List<Dimension> dimensionList;

    public ContractType(boolean isReplace, String contractTypeName, List<Dimension> dimensionList) {
        this.isReplace=isReplace;
        this.contractTypeName=contractTypeName;
        this.dimensionList=dimensionList;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public String getContractTypeName() {
        return contractTypeName;
    }

    public List<Dimension> getDimensionList() {
        return dimensionList;
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        query.append(this.isReplace? "REPLACE " : "CREATE ").append("CONTRACTTYPE ")
                .append(this.contractTypeName).append(" (");
        for(int i=0; i<this.dimensionList.size(); i++){
            query.append(this.dimensionList.get(i).toSQLString().getQueryString());
            if(i < this.dimensionList.size()-1){
                query.append(", ");
            }
        }
        query.append(");");
        return new SQLString(query.toString());
    }
}
