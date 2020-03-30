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

import java.util.*;

public class QwithConstraints {
    private ArrayList<Constraint> constraintArrayList = null;
    private ArrayList<String> constractNameArrayList = null;

    public QwithConstraints() {
        this.constraintArrayList = new ArrayList<Constraint>();
        this.constractNameArrayList = new ArrayList<String>();
    }

    public int size() {
        return this.constraintArrayList.size();
    }

    public boolean isEmpty() {
        return this.constraintArrayList.isEmpty();
    }

    public boolean contains(Object o) {
        return this.constraintArrayList.contains(o);
    }

    public Iterator<Constraint> iterator() {
        return this.constraintArrayList.iterator();
    }

    public boolean addConstraint(Constraint constraint) {
        return this.constraintArrayList.add(constraint);
    }

    public boolean addContractName(String contractName) {
        return this.constractNameArrayList.add(contractName);
    }

    public boolean addAll(List<Constraint> constraintList) {
        for(Constraint cons : constraintList)
            this.constraintArrayList.add(cons);
        return true;
    }

    public boolean remove(Object o) {
        return this.constraintArrayList.remove(o);
    }


    public Constraint getConstraint(int i) {
        return this.constraintArrayList.get(i);
    }

    public Constraint set(int i, Constraint constraint) {
        return this.constraintArrayList.set(i,constraint);
    }

    public void add(int i, Constraint constraint) {
        this.constraintArrayList.add(i, constraint);
    }

    public Constraint remove(int i) {
        return this.constraintArrayList.remove(i);
    }

    public SQLString toSQLString() {
        StringBuilder query = new StringBuilder();
        for(int i=0; i<this.constraintArrayList.size(); i++){
            query.append(this.constraintArrayList.get(i).toSQLString().getQueryString());
            if(i < this.constraintArrayList.size()-1){
                query.append(" AND ");
            }
        }
        if(this.constraintArrayList.size() > 0 && this.constractNameArrayList.size() > 0){
            query.append(" AND ");
        }
        for(int i=0; i<this.constractNameArrayList.size(); i++){
            query.append(this.constractNameArrayList.get(i));
            if(i < this.constractNameArrayList.size()-1){
                query.append(" AND ");
            }
        }
        return new SQLString(query.toString());
    }

    public ArrayList<Constraint> getConstraints() {
        return this.constraintArrayList;
    }
    public ArrayList<String> getContractNames() {
        return this.constractNameArrayList;
    }
}
