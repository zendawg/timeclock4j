/*
 * Copyright (C) 2011 State Five
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.statefive.timeclockj;

/**
 *
 * @author rich
 */
public class TestUtils {
  public static final String BASIC_CLOCK_DATA = 
          "i 2010/10/05 13:28:57 Project A\n"
            + "o 2010/10/05 13:40:01\n"
            + "i 2010/10/05 18:46:42 Project B\n"
            + "o 2010/10/05 22:23:59\n"
            + " this line will not get parsed\n"
            // default project:
            + "i 2010/10/05 22:37:11\n"
            + "o 2010/10/06 00:11:14\n"
            + "i 2010/10/06 08:42:46 Project A\n"
            + "o 2010/10/06 09:37:05";
}
