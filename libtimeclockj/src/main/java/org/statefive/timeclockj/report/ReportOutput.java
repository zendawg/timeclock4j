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
package org.statefive.timeclockj.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.statefive.timeclockj.ClockManager;

/**
 *
 * @author rich
 */
public class ReportOutput {

  /**
   * 
   * @param projects
   * @return
   */
  public String getConsoleOutput(String[] projects) {
    String output = null;

    output += "Project Reports\n";
    output += "Report generated on " + ClockManager.getInstance().formatDate(
            new Date(Calendar.getInstance().getTimeInMillis()));
    for (String name : projects) {
      output += "\n";
      if (ClockManager.DEFAULT_PROJECT_STRING.equals(name)) {
        // html doesn't like '<defalt>':
        name = "default";
      }
      output += "Project: " + name + "\n";
      if ("default".equals(name)) {
        name = ClockManager.DEFAULT_PROJECT_STRING;
      }
      ProjectReport report = ReportFactory.getInstance().getReport(name);
      Duration totalDuration = ReportFactory.getInstance().long2Duration(
              report.getTotalTime());
      if (name != null && name.equals(ClockManager.getInstance().getCurrentProject())) {
        output += "Clocked-in at time of report generation.\n";
      }
      output += "Duration: " + totalDuration.toString() + "\n";
      output += "Clock-ins: " + report.getDurations().size() + "\n";
      List<Duration> durations = report.getDurations();
      if (report.getDurations().size() > 0) {
        Date lastDate = null;
        for (int i = 0; i < durations.size(); i++) {
          Duration duration = durations.get(i);

          Date start = new Date(duration.getStartTime());
          Date end = new Date(duration.getEndTime());
          if (i == 0) {
            output += formatDate(start) + "\n";
            output += "\tStart\t\tEnd\t\tDuration\n";
          }
          if (start.getDate() != end.getDate()) {
            output += "\t" + formatTime(start) + "\t\t\n";

            output += formatDate(end) + "\n";
            output += "\n\tStart\t\tEnd\t\tDuration\n";
            output += "\t\t\t" + formatTime(end) + "\t" + duration.toString() + "\n";

          } else {
            if (lastDate != null && lastDate.getDate() != start.getDate()) {
              output += formatDate(start) + "\n";
              output += "\n\tStart\t\tEnd\t\tDuration\n";
            }
            output += "\t" + formatTime(start) + "\t" + formatTime(end) + "\t" + duration.toString() + "\n";
          }
          lastDate = end;
        }
      } else {
        output += "No Clock Periods Recorded.\n";
      }
    }
    output += "End of Reports";
    return output;
  }

  /**
   *
   * @param projects
   * @return
   */
  public String getHtmlOutput(String[] projects) {
    return getHtmlOutput(projects, null, null);
  }

  /**
   *
   * @param projects
   * @param dStart
   * @param dEnd
   * @return
   * 
   * @since 1.1
   */
  public String getHtmlOutput(String[] projects, Date dStart, Date dEnd) {
    final String REGEX_REPLACE = "<REGEX_REPLACE:MONTH_LINKS>";

    StringBuilder html = new StringBuilder("<html><body><a name=\"topofpage\"/>");
    html.append("<h1>Project Reports</h1>");
    if (projects.length > 1) {
      // generate in-document links:
      html.append("<ul>");
      for (String linkProject : projects) {
        if (ClockManager.DEFAULT_PROJECT_STRING.equals(linkProject)) {
          // html doesn't like '<defalt>':
          linkProject = "default";
        }
        html.append("<li><a href=\"#");
        html.append(linkProject);
        html.append("\">");
        html.append(linkProject);
        html.append("</a>");
      }
      html.append("</ul>");
    }
    html.append("<p>Report generated on ");
    html.append(ClockManager.getInstance().formatDate(
            new Date(Calendar.getInstance().getTimeInMillis())));
    html.append(" <a href=\"http://www.statefive.org/timeclockj\">"
            + "www.statefive.org - timeclockj</a></p>");
    String monthLinks = "";
    for (String name : projects) {
      monthLinks = "";
      html.append("<hr>");
      if (ClockManager.DEFAULT_PROJECT_STRING.equals(name)) {
        // html doesn't like '<default>':
        name = "default";
      }
      html.append("<a name=\"");
      html.append(name);
      html.append("\"/>");
      html.append("<p><h2>Project: ");
      html.append(name);
      html.append("</h2></p>");
      if ("default".equals(name)) {
        name = ClockManager.DEFAULT_PROJECT_STRING;
      }
      ProjectReport report =
              ReportFactory.getInstance().getReport(name, dStart, dEnd);
      Duration totalDuration = ReportFactory.getInstance().long2Duration(
              report.getTotalTime());
      if (name != null && name.equals(ClockManager.getInstance().getCurrentProject())) {
        html.append("<p><font color=\"ff0000\">Clocked-in at time of report generation.</font></p>");
      }
      html.append("<p><b>Duration:</b> ");
      html.append(totalDuration.toString());
      html.append("</p>");
      html.append("<p><b>Clock-ins:</b> ");
      html.append(report.getDurations().size());
      html.append("</p>");
      html.append(REGEX_REPLACE);
      SimpleDateFormat monthFormat = new SimpleDateFormat("MMMMMMMMMMM yyyy");
      int month = -1;
      List<Duration> durations = report.getDurations();
      if (report.getDurations().size() > 0) {
        Date lastDate = null;
        for (int i = 0; i < durations.size(); i++) {
          Duration duration = durations.get(i);

          Date start = new Date(duration.getStartTime());
          Date end = new Date(duration.getEndTime());
          if (i == 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(start);
            if (month != c.get(Calendar.MONTH)) {
              month = c.get(Calendar.MONTH);
              monthLinks += "<li><a href=\"#" + name + formatDate(start) + "\">" + monthFormat.format(start) + "</a>";
              html.append("<a name=\"");
              html.append(name);
              html.append(formatDate(start));
              html.append("\"/>");
              html.append("<h2>");
              html.append(monthFormat.format(start));
              html.append("</h2>");
            }
            html.append("<p>");
            html.append(formatDate(start));
            html.append(",&nbsp;");
            html.append(calculateHoursForDay(start, durations));
            html.append("</p>");

            html.append("<table border=\"5\" "
                    + "frame=\"ABOVE\""
                    + "rules=\"COLS|ROWS\""
                    + "width=\"100%\""
                    + "cellpadding=\"5\">");
            html.append("<tr><td width=\"15%\" align=\"center\"><b>Start</b></td>"
                    + "<td width=\"15%\" align=\"center\"><b>End</b></td>"
                    + "<td width=\"15%\" align=\"center\"><b>Duration</b></td>"
                    + "<td width=\"55%\" align=\"left\"><b>Description</b></td>"
                    + "</tr>");
          }
          String description = "";
          if (duration.getDescription() != null) {
            description = duration.getDescription();
          } else if (name.equals(ClockManager.getInstance().getCurrentProject())
                  && report.getDurations().size() - 1 == i) {
            // still clocked-in
            description = "<font color=\"ff0000\">Clocked-in at time of report generation.</font>";
          }
          if (start.getDate() != end.getDate()) {
//            System.out.println("start=" + formatDate(start) + ", end="
//                    + formatDate(end) + " not equal.");
            html.append("<tr><td width=\"15%\" align=\"center\">");
            html.append(formatTime(start));
            html.append("</td>"
                    + "<td width=\"15%\" align=\"center\"></td>"
                    + "<td width=\"15%\" align=\"center\"></td>"
                    + "<td width=\"55%\" align=\"left\"></td></tr>"
                    + "</table>");

            html.append("<p>");
            html.append(formatDate(end));
            html.append(",&nbsp;");
            html.append(calculateHoursForDay(end, durations));
            html.append("</p>");
            html.append("<table border=\"5\" "
                    + "frame=\"ABOVE\""
                    + "rules=\"COLS|ROWS\""
                    + "width=\"100%\""
                    + "cellpadding=\"5\">");
            html.append("<tr><td width=\"15%\" align=\"center\"><b>Start</b></td>"
                    + "<td width=\"15%\" align=\"center\"><b>End</b></td>"
                    + "<td width=\"15%\" align=\"center\"><b>Duration</b></td>"
                    + "<td width=\"55%\" align=\"left\"><b>Description</b></td>"
                    + "</tr>");
            html.append("<tr><td width=\"15%\" align=\"center\"></td>"
                    + "<td width=\"15%\" align=\"center\">");
            html.append(formatTime(end));
            html.append("</td>" + "<td width=\"15%\" align=\"center\">");
            html.append(duration.toString());
            html.append("</td><td width=\"55%\" align=\"left\">");
            html.append(description);
            html.append("</td></tr>");
          } else {
            if (lastDate != null && lastDate.getDate() != start.getDate()) {
              html.append("</table>");

              Calendar c = Calendar.getInstance();
              c.setTime(start);
              if (month != c.get(Calendar.MONTH)) {
                month = c.get(Calendar.MONTH);
                html.append("<a href=\"#");
                html.append(name);
                html.append("\">Back to project</a>");
                monthLinks += "<li><a href=\"#" + name + formatDate(start) + "\">" + monthFormat.format(start) + "</a>";
                html.append("<a name=\"");
                html.append(name);
                html.append(formatDate(start));
                html.append("\"/>");
                html.append("<h2>");
                html.append(monthFormat.format(start));
                html.append("</h2>");
              }
              html.append("<p>");
              html.append(formatDate(start));
              html.append(",&nbsp;");
              html.append(calculateHoursForDay(start, durations));
              html.append("</p>");
              html.append("<table border=\"5\" frame=\"ABOVE\" width=\"100%\"rules=\"COLS|ROWS\" cellpadding=\"5\">");
              html.append("<tr><td width=\"15%\" align=\"center\"><b>Start</b></td>"
                      + "<td width=\"15%\" align=\"center\"><b>End</b></td>"
                      + "<td width=\"15%\" align=\"center\"><b>Duration</b></td>"
                      + "<td width=\"55%\" align=\"left\"><b>Description</b></td>"
                      + "</tr>");
            }
            html.append("<tr><td width=\"15%\" align=\"center\">");
            html.append(formatTime(start));
            html.append("</td><td width=\"15%\" align=\"center\">");
            html.append(formatTime(end));
            html.append("</td>");
            html.append("<td width=\"15%\" align=\"center\">");
            html.append(duration.toString());
            html.append("</td><td width=\"55%\" align=\"left\">");
            html.append(description);
            html.append("</td></tr>");
          }
          if (i == durations.size() - 1) {
            html.append("</table>");
          }
          lastDate = end;
        }
        html.append("<p><a href=\"#topofpage\">(Top of Page)</a></p>");
        html = new StringBuilder(html.toString().replace(REGEX_REPLACE, "<ul>" + monthLinks + "</ul>"));
      } else {
        html.append("<p>No Clock Periods Recorded.</p>");
      }
    }
    html.append("<p><End of Reports</p>");
    html.append("</body></html>");
    return html.toString();
  }

  /**
   * For a given day, go through the durations, retrieve all durations that
   * (i) start on the given date, (ii) end on the same date but begin
   * on the a prior day and (iii) start on the same day but end on a different
   * day. In case (ii), time is calculated from midnight of the start of the
   * date; in case (iii) the time is calculated up to midnight of the specified
   * date.
   *
   * @param date the non-{@code null} date to search for in the list.
   *
   * @param durations a non-{@code null} list of durations.
   *
   * @return a duration turned to a string.
   */
  private String calculateHoursForDay(Date date, List<Duration> durations) {
    Calendar calDate = Calendar.getInstance();
    calDate.setTime(date);
    long duration = 0;

    for (Duration d : durations) {
      Calendar calStart = Calendar.getInstance();
      Calendar calEnd = Calendar.getInstance();
      calStart.setTimeInMillis(d.getStartTime());
      calEnd.setTimeInMillis(d.getEndTime());
      if ((calDate.get(Calendar.DAY_OF_MONTH) == calStart.get(Calendar.DAY_OF_MONTH)
              && calDate.get(Calendar.YEAR) == calStart.get(Calendar.YEAR)
              && calDate.get(Calendar.MONTH) == calStart.get(Calendar.MONTH))
              || (calDate.get(Calendar.DAY_OF_MONTH) == calEnd.get(Calendar.DAY_OF_MONTH)
              && calDate.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)
              && calDate.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH))) {
        if (calStart.get(Calendar.DAY_OF_MONTH) == calEnd.get(Calendar.DAY_OF_MONTH)) {
          // start and end on same day:
          duration += d.longValue();
        } else if (calDate.get(Calendar.DAY_OF_MONTH) == calEnd.get(Calendar.DAY_OF_MONTH) - 1) {
          // starts today, but ends tomorrow:
          calEnd.set(Calendar.DAY_OF_MONTH, calStart.get(Calendar.DAY_OF_MONTH));
          calEnd.set(Calendar.HOUR_OF_DAY, 23);
          calEnd.set(Calendar.MINUTE, 59);
          calEnd.set(Calendar.SECOND, 59);
          duration += calEnd.getTimeInMillis() - calStart.getTimeInMillis();
        } else if (calDate.get(Calendar.DAY_OF_MONTH) == calStart.get(Calendar.DAY_OF_MONTH) + 1) {
          // started prior day, but ends today:
          calStart.set(Calendar.DAY_OF_MONTH, calEnd.get(Calendar.DAY_OF_MONTH));
          calStart.set(Calendar.HOUR_OF_DAY, 0);
          calStart.set(Calendar.MINUTE, 0);
          calStart.set(Calendar.SECOND, 0);
          duration += calEnd.getTimeInMillis() - calStart.getTimeInMillis();
        }
      }
    }
    return ReportFactory.getInstance().long2Duration(duration).toString();
  }

  private String formatTime(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    return sdf.format(date);
  }

  private String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy");
    return sdf.format(date);
  }
}
