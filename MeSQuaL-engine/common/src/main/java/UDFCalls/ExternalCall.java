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

package UDFCalls;

import java.nio.file.Path;

public class ExternalCall extends Call {

	private Path path;
	private String params;
	private boolean hasParams;
	
	
	public ExternalCall() {}
	public ExternalCall(UDFLanguage callLanguage, Path path, String params) {
		this.callLanguage = callLanguage;
		this.path   = path;
		this.params = params;

		if(params==null) hasParams = false;
		else
			hasParams = true;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public Path getPath() {
		return path;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getParams() {
		return params;
	}
	public void setSomeParams(boolean hasParams) {
		this.hasParams = hasParams;
	}
	public boolean isSomeParams() {
		return (params != null) && params.length()>0;
	}
	
}
