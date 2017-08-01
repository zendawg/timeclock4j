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
import org.junit.Test;


/**
 *
 * @author rich
 */
public class ClockOutExceptionTest extends TestCase {

  public ClockOutExceptionTest(String testName) {
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

  @Test
  public void testClockInException() {
    ClockOutException ciex = new ClockOutException();
    assertEquals(null, ciex.getMessage());
  }

  @Test
  public void testClockInExceptionString() {
    String msg = "This is an error.";
    ClockOutException ciex = new ClockOutException(msg);
    assertEquals(msg, ciex.getMessage());
  }
}
