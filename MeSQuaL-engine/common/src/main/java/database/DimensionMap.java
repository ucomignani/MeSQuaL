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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DimensionMap implements Map<String, Dimension>{

    private HashMap<String, Dimension> dimensionMap;

    public DimensionMap() {
        this.dimensionMap = new HashMap<>();
    }

    public DimensionMap(HashMap<String, Dimension> dimensionMap) {
        this.dimensionMap = dimensionMap;
    }

    @Override
    public int size() {
        return this.dimensionMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.dimensionMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.dimensionMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.dimensionMap.containsValue(o);
    }

    @Override
    public Dimension get(Object o) {
        return this.dimensionMap.get(o);
    }

    @Override
    public Dimension put(String dimensionString, Dimension dimension) {
        return this.dimensionMap.put(dimensionString, dimension);
    }

    @Override
    public Dimension remove(Object o) {
        return this.dimensionMap.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Dimension> map) {
        this.dimensionMap.putAll(map);
    }

    @Override
    public void clear() {
        this.dimensionMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.dimensionMap.keySet();
    }

    @Override
    public Collection<Dimension> values() {
        return this.dimensionMap.values();
    }

    @Override
    public Set<Entry<String, Dimension>> entrySet() {
        return this.dimensionMap.entrySet();
    }

    public String toStringOfDimensionNames(){
        StringBuilder output = new StringBuilder();

        for(Dimension dim : this.dimensionMap.values()){
            output.append(dim.getDimensionName().getName()).append("\n");
        }

        return output.toString();
    }
}
