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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.statefive.timeclockj.ui.AbstractSystemTray;
import org.statefive.timeclockj.ui.ReportTimePanel;

/**
 *
 * @author irch
 */
public class SpecifyTimeReportListener implements ActionListener {

    private AbstractSystemTray systemTray;

    public AbstractSystemTray getSystemTray() {
        return systemTray;
    }

    public void setSystemTray(AbstractSystemTray systemTray) {
        this.systemTray = systemTray;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // show dialog with date, time & description options
      final ReportTimePanel panel = new ReportTimePanel();
      JPanel panelOptions = panel.getReportTimePanel();
      JOptionPane optionPane = new JOptionPane();
      JButton buttonOK = new JButton("OK");
      JButton buttonCancel = new JButton("Cancel");
      optionPane.setMessage(panelOptions);
      optionPane.setOptions(new JButton[]{buttonOK, buttonCancel});

      final JDialog dialog = optionPane.createDialog(null,
              "Specify Report Time");
      buttonOK.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            String project = panel.getSelectedProject();
            systemTray.writeReports(new String[]{project}, panel.getStartDate(),
                    panel.getEndDate());
          } finally {
            dialog.dispose();
          }
        }
      });
      buttonCancel.addActionListener(
              new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                  dialog.dispose();
                }
              });
      optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
      dialog.setVisible(true);
    }
  }