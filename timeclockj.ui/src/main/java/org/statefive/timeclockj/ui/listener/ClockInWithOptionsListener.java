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

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ui.AbstractSystemTray;
import org.statefive.timeclockj.ui.ClockOptionsPanel;

/**
 *
 * @author irch
 */
public class ClockInWithOptionsListener implements ActionListener {

    private AbstractSystemTray systemTray;

    public AbstractSystemTray getSystemTray() {
        return systemTray;
    }

    public void setSystemTray(AbstractSystemTray systemTray) {
        this.systemTray = systemTray;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // show dialog with date, time & project choice options
        JButton buttonOK = new JButton("OK");
        JButton buttonCancel = new JButton("Cancel");
        JPanel panelOptions = new ClockOptionsPanel().getClockInPanel(
                systemTray.getClockModel());
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(panelOptions);
        optionPane.setOptions(new JButton[]{buttonOK, buttonCancel});
        optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        final JDialog dialog = optionPane.createDialog(null, "Clock In");

        buttonOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                systemTray.doClock();
                            } catch (IOException ioex) {
                                JOptionPane.showMessageDialog(null,
                                        ioex.getMessage(), "I/O Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } catch (ClockException cex) {
                                JOptionPane.showMessageDialog(null,
                                        "The project is not clocked-in.", "Error Clocking Out",
                                        JOptionPane.ERROR_MESSAGE);
                            } finally {
                                dialog.dispose();
                            }
                        }
                    });
                }
            });
            buttonCancel.addActionListener (
                     
                new ActionListener() {

            @Override
                public void actionPerformed
                (ActionEvent e
                
                    ) {
                dialog.dispose();
                }
            }

            );
            dialog.setVisible (
                    
        
    

true);
    }
}
