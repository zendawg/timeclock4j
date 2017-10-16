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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.statefive.timeclockj.ClockManager;

import org.statefive.timeclockj.TimeClockParser;

/**
 *
 * @author rich
 */
public class ReportTimePanel {

  private Date startDate;
  private Date endDate;
  private JComboBox comboBoxProjects;

  public JPanel getReportTimePanel() {
    JPanel panelClockInOptions = new JPanel();
    panelClockInOptions.setLayout(new GridLayout(3, 3));
    addDateTimeComponents(panelClockInOptions);
    final Vector projects = getProjects();
    comboBoxProjects = new JComboBox(projects);
    panelClockInOptions.add(new JLabel("Project:"));
    panelClockInOptions.add(comboBoxProjects);
    return panelClockInOptions;
  }

  public void addDateTimeComponents(JPanel panel) {

    final JCheckBox checkBoxStart = new JCheckBox("Start ("
            + TimeClockParser.DATE_FORMAT.toLowerCase() + ")");
    final JTextField textFieldStart = new JTextField(12);
    final JCheckBox checkBoxEnd = new JCheckBox("End ("
            + TimeClockParser.DATE_FORMAT.toLowerCase() + ")");
    final JTextField textFieldEnd = new JTextField(12);
    textFieldStart.setEnabled(false);
    textFieldEnd.setEnabled(false);
    panel.add(checkBoxStart);
    panel.add(textFieldStart);
    panel.add(checkBoxEnd);
    panel.add(textFieldEnd);

    final SimpleDateFormat dateFormat = new SimpleDateFormat(
            TimeClockParser.DATE_FORMAT);
    dateFormat.setLenient(false);

    textFieldStart.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        if (!e.isTemporary()) {
          try {
            Date d = dateFormat.parse(textFieldStart.getText());
            startDate = d;
          } catch (ParseException pex) {
            JOptionPane.showMessageDialog(checkBoxStart, "Format is "
                    + TimeClockParser.DATE_FORMAT.toLowerCase(),
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            checkBoxStart.setSelected(false);
          }
        }
      }
    });

    textFieldEnd.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        if (!e.isTemporary()) {
          try {
            Date d = dateFormat.parse(textFieldEnd.getText());
            endDate = d;
          } catch (Exception pex) {
            JOptionPane.showMessageDialog(null, "Format is "
                    + TimeClockParser.DATE_FORMAT.toLowerCase(),
                    "Invalid Time", JOptionPane.ERROR_MESSAGE);
            checkBoxEnd.setSelected(false);

          }
        }
      }
    });
    textFieldStart.setText(dateFormat.format(Calendar.getInstance().getTime()));
    textFieldEnd.setText(dateFormat.format(Calendar.getInstance().getTime()));

    checkBoxStart.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        setComponentStates(checkBoxStart, textFieldStart);
      }
    });

    checkBoxEnd.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        setComponentStates(checkBoxEnd, textFieldEnd);
      }
    });
  }
  
  /**
   * 
   * @return 
   */
  public Date getStartDate() {
    return startDate;
  }
  
  /**
   * 
   * @return 
   */
  public Date getEndDate() {
    return endDate;
  }
  
  public String getSelectedProject() {
    return comboBoxProjects.getSelectedItem().toString();
  }

  /**
   * 
   * @param checkBox
   * @param textField
   */
  public void setComponentStates(JCheckBox checkBox, JTextField textField) {

    if (checkBox.isSelected()) {
      textField.setEnabled(true);
      textField.requestFocus();
    } else {
      textField.setEnabled(false);
    }
  }

  /**
   * Gets all projects from the clock manager; also, the default
   * project is given at the start of the list and the 'New Project'
   * option added at the end.
   * 
   * @return a list of projects, including the default and an option to
   * add a new project.
   */
  private Vector getProjects() {
    final Vector projects = new Vector();
    for (int i = 0; i < ClockManager.getInstance().getProjects().length; i++) {
      String project = ClockManager.getInstance().getProjects()[i];
      if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
        projects.add(project);
      }
    }
    return projects;
  }
}
