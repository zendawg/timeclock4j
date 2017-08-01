/*
 * Copyright (C) 2013 State Five
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
package org.statefive.timeclockj.android;

import android.content.SharedPreferences;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rich
 */
public class DropboxUpdateTool {
  
  final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
  final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";
  final static public String ACCOUNT_PREFS_NAME =
          DropboxAuthActivity.class.getName() + ".prefs";
  /** */
  private static DropboxUpdateTool tool;
  /** */
  private DropboxAPI<AndroidAuthSession> api;

  /**
   * 
   * @return 
   */
  public DropboxAPI<AndroidAuthSession> getApi() {
    return api;
  }
  
  /** */
  private List<DropboxListener> listeners = new ArrayList<DropboxListener>();
  
  /**
   * 
   * @return 
   */
  public static DropboxUpdateTool getInstance() {
    if (tool == null) {
      tool = new DropboxUpdateTool();
    }
    return tool;
  }
  
  /**
   * 
   * @param l 
   */
  public void addListener(DropboxListener l) {
    this.listeners.add(l);
  }
  
  /**
   * 
   * @param loggedIn
   * @param account 
   */
  public void updateListener(boolean loggedIn, DropboxAPI.Account account) {
    for (DropboxListener l : listeners) {
      l.dropboxLogin(loggedIn, account);
    }
  }
  
  /**
   * 
   * @param syncInProgress 
   */
  public void updateListener(boolean syncInProgress) {
    for (DropboxListener l : listeners) {
      l.dropboxPushPullUpdate(syncInProgress);
    }
  }
  
  /**
   * 
   * @param syncInProgress 
   */
  public void updateListenerAuthorising(boolean authorising) {
    for (DropboxListener l : listeners) {
      l.dropboxAuthorising(authorising);
    }
  }

  /**
   * 
   * @param api 
   */
  public void setApi(DropboxAPI<AndroidAuthSession> api) {
    this.api = api;
  }
  

  /**
   *
   */
  public void clearKeys(SharedPreferences prefs) {
    SharedPreferences.Editor edit = prefs.edit();
    edit.remove(ACCESS_KEY_NAME);
    edit.remove(ACCESS_SECRET_NAME);
    edit.commit();
  }
  
}
