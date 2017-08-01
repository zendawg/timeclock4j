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

import junit.framework.TestCase;

/**
 *
 * @author rich
 */
public class ClockExceptionTest extends TestCase {

  public ClockExceptionTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testClockInException() {
    ClockException ciex = new ClockException();
    assertEquals(null, ciex.getMessage());
  }

  public void testClockInExceptionString() {
    String msg = "This is an error.";
    ClockException ciex = new ClockException(msg);
    assertEquals(msg, ciex.getMessage());
  }
}
