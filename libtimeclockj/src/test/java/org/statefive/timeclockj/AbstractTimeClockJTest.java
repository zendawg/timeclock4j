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

import java.lang.reflect.Field;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author rich
 */

@Ignore public class AbstractTimeClockJTest extends TestCase {

  public AbstractTimeClockJTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    try {
      Field f = ClockManager.class.getDeclaredField("clockManager");
      f.setAccessible(true);
      f.set(ClockManager.class, null);
    } catch (NoSuchFieldException nsfex) {
      // TODO this information is no good to a user. Expand!
      throw new ClockException("This is a bug. Please post this issue: "
        + nsfex.getMessage());
    } catch (IllegalAccessException iaex) {
      // TODO this information is no good to a user. Expand!
      throw new ClockException("This is a bug. Please post this issue: "
        + iaex.getMessage());
    }
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
