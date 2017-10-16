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

import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.TimeClockUtils;
import org.statefive.timeclockj.ui.AbstractSystemTray;
import org.statefive.timeclockj.ui.IconMessageTypeEnum;

/**
 *
 * @author irch
 */
public class ReloadFileListener implements ActionListener {

    private AbstractSystemTray systemTray;

    public AbstractSystemTray getSystemTray() {
        return systemTray;
    }

    public void setSystemTray(AbstractSystemTray systemTray) {
        this.systemTray = systemTray;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        TimeClockUtils.reloadFile(systemTray.getTimelog());
        systemTray.displayMessage("File I/O",
                "File successfully reloaded",
                IconMessageTypeEnum.INFO, systemTray.getPoint());
        systemTray.refreshComponents();
      } catch (IOException ioex) {
        systemTray.displayMessage("I/O Error",
                ioex.getMessage(),
                IconMessageTypeEnum.ERROR, systemTray.getPoint());
      } catch (ClockException cex) {
        systemTray.displayMessage("Clock Error",
                cex.getMessage(),
                IconMessageTypeEnum.ERROR, systemTray.getPoint());
      }
    }
  }