/*
 * Copyright (C) 2017 State Five
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
package org.statefive.timeclockj.ui.listener;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFrame;
import org.statefive.timeclockj.ui.SystemTrayAwt;
import org.statefive.timeclockj.ui.Utils;

/**
 *
 * @author irch
 */
public class HelpListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      JHelp helpViewer = null;
      try {
        // Get the classloader of this class.
        ClassLoader cl = SystemTrayAwt.class.getClassLoader();
        // Use the findHelpSet method of HelpSet to create a URL referencing the helpset file.
        // Note that in this example the location of the helpset is implied as being in the same
        // directory as the program by specifying "jhelpset.hs" without any directory prefix,
        // this should be adjusted to suit the implementation.
        URL url = HelpSet.findHelpSet(cl, "doc/jhelpset.hs");
        // Create a new JHelp object with a new HelpSet.
        helpViewer = new JHelp(new HelpSet(cl, url));
        // Set the initial entry point in the table of contents.
        helpViewer.setCurrentID("Top");
      } catch (Exception ex) {
        System.err.println("API Help Set not found: " + ex.getMessage());
      }

      // Create a new frame.
      JFrame frame = new JFrame();
      // Set it's size.

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setSize(screenSize.width - 200, screenSize.height - 200);
      // Add the created helpViewer to it.
      frame.getContentPane().add(helpViewer);
      // Set a default close operation.
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      Utils.centerFrame(frame);
      // Make the frame visible.
      frame.setVisible(true);

    }
  }