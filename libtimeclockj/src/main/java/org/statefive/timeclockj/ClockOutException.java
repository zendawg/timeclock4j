/*
 * Copyright (c) Richard Meeking, Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
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
public class ClockOutException extends ClockException {

    /**
     * Creates a new instance of <code>ClockOutException</code> without detail message.
     */
    public ClockOutException() {

    }


    /**
     * Constructs an instance of <code>ClockOutException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ClockOutException(String msg) {
        super(msg);
    }
}
