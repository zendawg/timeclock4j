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
package org.statefive.timeclockj.android.v2;

import android.os.AsyncTask.Status;

/**
 * Implemented by classes that have an interest in knowing when a file
 * has finished parsing.
 * 
 * @author rich
 * 
 * @since 1.1
 */
public interface FileParseListener {
  /** Called */
  public void fileParseUpdate(Status status);
}
