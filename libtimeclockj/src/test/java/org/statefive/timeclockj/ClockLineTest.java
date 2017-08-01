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

import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author rich
 */
public class ClockLineTest extends TestCase {

  public ClockLineTest(String testName) {
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

  /**
   * Test of getClockDate method, of class ClockLine.
   */
  public void testSetGetClockDate() {
    Date d = Calendar.getInstance().getTime();
    ClockLine line = new ClockLine();
    assertNull(line.getClockDate());
    line.setClockDate(d);
    assertEquals(d, line.getClockDate());
  }

  /**
   * Test of getClockStatus method, of class ClockLine.
   */
  public void testSetGetClockStatus() {
    ClockInOutEnum clockStatus = ClockInOutEnum.OUT;
    ClockLine line = new ClockLine();
    assertNull(line.getClockStatus());
    line.setClockStatus(clockStatus);
    assertEquals(clockStatus, line.getClockStatus());
  }

  /**
   * Test of getText method, of class ClockLine.
   */
  public void testSetGetText() {
    
  }

  /**
   * Test of toString method, of class ClockLine.
   */
  public void testToString() {
    ClockLine line = new ClockLine();
    String text = "test text";
    Date d = Calendar.getInstance().getTime();
    ClockInOutEnum clockStatus = ClockInOutEnum.IN;
    line.setClockStatus(clockStatus);
    line.setClockDate(d);
    line.setText(text);
    assertEquals("i " + ClockManager.getInstance().formatDate(d) + " " + text,
      line.toString());
  }

  /**
   * Test of toString method, of class ClockLine.
   */
  public void testToStringWithoutText() {
    ClockLine line = new ClockLine();
    Date d = Calendar.getInstance().getTime();
    ClockInOutEnum clockStatus = ClockInOutEnum.IN;
    line.setClockStatus(clockStatus);
    line.setClockDate(d);
    assertEquals("i " + ClockManager.getInstance().formatDate(d),
      line.toString());
  }
}
