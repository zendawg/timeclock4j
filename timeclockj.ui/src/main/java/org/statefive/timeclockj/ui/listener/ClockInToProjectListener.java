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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.TimeClockUtils;
import org.statefive.timeclockj.ui.AbstractSystemTray;

/**
 *
 * @author irch
 */
public class ClockInToProjectListener implements ActionListener {

    private String project;

    private AbstractSystemTray systemTray;

    public AbstractSystemTray getSystemTray() {
        return systemTray;
    }

    public void setSystemTray(AbstractSystemTray systemTray) {
        this.systemTray = systemTray;
    }

    public ClockInToProjectListener(String project) {
      this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        TimeClockUtils.clockIn(systemTray.getTimelog(), project);
        systemTray.setUserClockChange(true);
        systemTray.refreshComponents();
      } catch (IOException ioex) {
        JOptionPane.showMessageDialog(null,
                ioex.getMessage(), "I/O Error",
                JOptionPane.ERROR_MESSAGE);
      } catch (ClockException cex) {
        JOptionPane.showMessageDialog(null,
                "The project is not clocked-in.", "Error Clocking Out",
                JOptionPane.ERROR_MESSAGE);
      }
    }
  }