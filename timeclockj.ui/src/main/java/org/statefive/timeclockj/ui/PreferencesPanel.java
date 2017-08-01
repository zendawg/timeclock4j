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
package org.statefive.timeclockj.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author rich
 */
public class PreferencesPanel extends JFrame {

  private JButton buttonOK;
  private JButton buttonCancel;
  
  private JTextField textFieldFileName;
  private JTextField textFieldInterval;
  private JTextField textFieldReportDirectory;
  private JCheckBox checkBoxReloadOnChange;

  public PreferencesPanel() {
    super("TimeClockJ Preferences");
    textFieldFileName = new JTextField(".timelog");
    textFieldReportDirectory = new JTextField(".timeclockj/reports");
    textFieldInterval = new JTextField("1", 5);
    checkBoxReloadOnChange = new JCheckBox("Reload on file-change");
    buttonCancel = new  JButton("Cancel");
    buttonOK = new  JButton("OK");
    this.getContentPane().setLayout(new BorderLayout());
    Container c = this.getContentPane();
    JPanel panelFiles = new JPanel(new GridLayout(2, 2));
    panelFiles.add(new JLabel("Timeclock File"));
    panelFiles.add(textFieldFileName);
    panelFiles.add(new  JLabel("Reports Directory"));
    panelFiles.add(textFieldReportDirectory);
    JPanel panelFileWatch = new JPanel(new BorderLayout());
    panelFileWatch.setBorder(BorderFactory.createTitledBorder("File Reloading"));
    panelFileWatch.add(BorderLayout.CENTER, checkBoxReloadOnChange);
    JPanel panelInterval = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelInterval.add(new JLabel("Interval (seconds)"));
    panelInterval.add(textFieldInterval);
    panelFileWatch.add(BorderLayout.SOUTH, panelInterval);
    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(buttonOK);
    panelButtons.add(buttonCancel);
    c.add(BorderLayout.NORTH, panelFiles);
    c.add(BorderLayout.CENTER, panelFileWatch);
    c.add(BorderLayout.SOUTH, panelButtons);
  }

}
