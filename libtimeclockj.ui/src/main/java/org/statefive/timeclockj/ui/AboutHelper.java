/*
 *  Copyright (C) 2011 rich
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.statefive.timeclockj.ui;

/**
 *
 * @author rich
 */
public class AboutHelper {

  /** */
  public static final String DOLLAR_VERSION = "${version}";

  /**
   *
   * @return
   */
  public static String getAboutHtml() {
    return "<html><body style=\"font-family: sans-serif; font-size:1pc;\">"
      + "<center><h1>TimeClockJ</h1>"
      + "<p>Version " + AboutHelper.DOLLAR_VERSION
      + "</p>"
      + "<p>&copy; 2010 Richard Meeking, Contributors</p>"
      + "<p>License: GNU Public License V3</p>"
      + "<p align=\"center\"><a href=\"http://www.statefive.org\">"
      + "http://www.state5.org</a></p>"
      + "</center></body></html>";
  }

  /**
   * 
   * @return
   */
  public static String getContributorHtml() {
    return "<html><body style=\"font-family: sans-serif; font-size:1pc;\">"
      + "<h1>Contributors</h1>"
      + "<p>The following table details installation and runtime"
      + " software and products"
      + " used to run the desktop UI:</p>"
      + "<ul>"
      + "  <li><b>Apache Commons CLI</b>, "
      + "      The Apache Software License, Version 2.0, "
      + "      <a href=\"http://commons.apache.org/cli/\">"
      + "      http://commons.apache.org/cli/</a>"
      + "  <li><b>Dropbox</b>, "
      + "      MIT-style license, "
      + "      <a href=\"http://www.dropbox.com/\">"
      + "      http://www.dropbox.com/</a>"
      + "  <li><b>FAMFAMFAM Silk Icons</b>, "
      + "      Creative Commons Attribution 2.5 License, "
      + "      <a href=\"http://www.famfamfam.com/lab/icons/silk/\">"
      + "      http://www.famfamfam.com/lab/icons/silk/</a>"
      + "  <li><b>JavaHelp</b>, "
      + "      GNU General Public License - Version 2, "
      + "      <a href=\"https://javahelp.dev.java.net/\">"
      + "      https://javahelp.dev.java.net/</a>"
      + "  <li><b>PackJacket</b>, "
      + "      GNU Public License (Version 3), "
      + "      <a href=\"http://packjacket.sourceforge.net/\">"
      + "      http://packjacket.sourceforge.net/</a>"
      + "</ul>"
      + "</body></html>";
  }

}
