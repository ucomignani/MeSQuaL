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

public class ContractTypeName {

    private String schema_name;
    private String contract_type_name;

    public ContractTypeName(String schema_name, String contract_type_name) {
        this.schema_name = schema_name;
        this.contract_type_name = contract_type_name;
    }

    public String getSchema_name() {
        return schema_name;
    }

    public void setSchema_name(String schema_name) {
        this.schema_name = schema_name;
    }

    public String getConstraint_type_name() {
        return contract_type_name;
    }

    public void setConstraint_type_name(String contract_type_name) {
        this.contract_type_name = contract_type_name;
    }


    public SQLString toSQLString() {
        return new SQLString(this.contract_type_name);
    }
}
