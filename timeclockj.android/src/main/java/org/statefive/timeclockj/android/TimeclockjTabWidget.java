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
package org.statefive.timeclockj.android;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 *
 * @author rich
 */
public class TimeclockjTabWidget extends TabActivity{


  private int currentTab = 0;
  
  List<TabHost.TabSpec> list = new ArrayList<TabHost.TabSpec>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    list = this.getTabSpecList();
  }

  /**
   *
   */
  @Override
  public void onResume() {
    super.onResume();
    addTabs();
    getTabHost().setCurrentTab(this.currentTab);

  }

  /**
   *
   */
  private void addTabs() {

    for (TabHost.TabSpec s : list) {
      list.remove(s);
    }

    list.addAll(this.getTabSpecList());
  }

  /**
   *
   * @return
   */
  private List<TabHost.TabSpec> getTabSpecList() {

    Resources res = getResources(); // Resource object to get Drawables
    TabHost tabHost = getTabHost();  // The activity TabHost

    tabHost.setCurrentTab(0); //IMPORTANT
    tabHost.clearAllTabs();  // clear all tabs from the tabhost

    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
    Intent intent;  // Reusable Intent for each tab
    List<TabHost.TabSpec> specList = new ArrayList<TabHost.TabSpec>();

    // Initialize a TabSpec for each tab and add it to the TabHost
    intent = new Intent().setClass(this, ClockActivity.class);
    spec = tabHost.newTabSpec("clock").setIndicator("Clock",
            res.getDrawable(R.drawable.ic_tab_clock)).setContent(intent);
    tabHost.addTab(spec);

    intent = new Intent().setClass(this, ReportActivity.class);
    spec = tabHost.newTabSpec("report").setIndicator("Report",
            res.getDrawable(R.drawable.ic_tab_report)).setContent(intent);
    tabHost.addTab(spec);

    intent = new Intent().setClass(this, InfoActivity.class);
    spec = tabHost.newTabSpec("info").setIndicator("Info",
            res.getDrawable(R.drawable.ic_tab_info)).setContent(intent);
    tabHost.addTab(spec);

    boolean dropbox = Utils.getInstance().isDropboxCompatible(this);
    if (dropbox) {
      intent = new Intent().setClass(this, DropboxActivity.class);
      spec = tabHost.newTabSpec("dropbox").setIndicator("Sync",
              res.getDrawable(R.drawable.ic_tab_sync)).setContent(intent);
      tabHost.addTab(spec);
    }
    tabHost.setCurrentTab(0);

    return specList;
  }

  /**
   *
   */
  @Override
  protected void onPause() {
    super.onPause();
    this.currentTab = getTabHost().getCurrentTab();
  }
}
