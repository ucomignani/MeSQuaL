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

public class ContractMap implements Map<String, Contract>{
    
    private HashMap<String, Contract> constraintMap;

    public ContractMap() {
        this.constraintMap = new HashMap<>();
    }

    public ContractMap(HashMap<String, Contract> constraintMap) {
        this.constraintMap = constraintMap;
    }

    @Override
    public int size() {
        return this.constraintMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.constraintMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.constraintMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.constraintMap.containsValue(o);
    }

    @Override
    public Contract get(Object o) {
        return this.constraintMap.get(o);
    }

    @Override
    public Contract put(String constraintString, Contract constraintList) {
        return this.constraintMap.put(constraintString, constraintList);
    }

    @Override
    public Contract remove(Object o) {
        return this.constraintMap.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Contract> map) {
        this.constraintMap.putAll(map);
    }

    @Override
    public void clear() {
        this.constraintMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.constraintMap.keySet();
    }

    @Override
    public Collection< Contract> values() {
        return this.constraintMap.values();
    }

    @Override
    public Set<Entry<String, Contract>> entrySet() {
        return this.constraintMap.entrySet();
    }
}
