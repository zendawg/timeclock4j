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

import java.io.ByteArrayInputStream;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class TimeClockParserTest extends AbstractTimeClockJTest {

  public TimeClockParserTest(String testName) {
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
   * Test of parse method, of class TimeClockParser.
   */
  @Test
  public void testParse() throws Exception {
    String data = TestUtils.BASIC_CLOCK_DATA;
    TimeClockParser parser = new TimeClockParser();
    parser.parse(new ByteArrayInputStream(data.getBytes()));
    assertEquals(3, ClockManager.getInstance().getProjects().length);
    assertEquals(2, ClockManager.getInstance().getClockPeriods("Project A").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods("Project B").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods(
            ClockManager.DEFAULT_PROJECT_STRING).size());
  }
}
