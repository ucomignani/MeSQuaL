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

package Socket;


public class MeSQuaLserver {

    public static void main(String[] args) {
        System.out.println("<program>  Copyright (C) <year>  <name of author>\n"
                + "This program comes with ABSOLUTELY NO WARRANTY.\n"
                + "This is free software, and you are welcome to redistribute it under certain conditions; see GNU GPL v3 for details.");
        new Socket.WebsocketServer().start();
    }

}
