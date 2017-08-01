/*
 * Copyright (c) 2010 Evenflow, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.statefive.timeclockj.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

public class DropboxAuthActivity extends Activity {

  private DropboxAPI<AndroidAuthSession> api;
  private AndroidAuthSession session;
  AppKeyPair appKeys;
  private static final String TAG = "DropboxAuthActivity";
  private static final String APP_KEY = "8hl7plwl6drcs5v";
  private static final String APP_SECRET = readSecretKey();

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DropboxUpdateTool.getInstance().updateListenerAuthorising(true);
    System.out.println("===========================================");
    System.out.println("===========================================");
    System.out.println("--------------- creating ------------------");
    System.out.println("===========================================");
    System.out.println("===========================================");
    appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
    session = new AndroidAuthSession(appKeys, Session.AccessType.DROPBOX);
    if (api == null) {
      api = new DropboxAPI<AndroidAuthSession>(session);
    }
    DropboxUpdateTool.getInstance().setApi(api);
    System.out.println("=================== KEYS: " + getKeys());
    if (getKeys() != null) {
      api.getSession().setAccessTokenPair(new AccessTokenPair(getKeys()[0],
              getKeys()[1]));
    } else {
      api.getSession().startAuthentication(this);
    }
  }

  /**
   *
   */
  @Override
  protected void onResume() {
    super.onResume();
    try {
      if (api != null && api.getSession().authenticationSuccessful()) {
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println(" (RESUME) AUTH SUCCESS");
        System.out.println("===========================================");
        System.out.println("===========================================");
        try {
          // MANDATORY call to complete auth.
          // Sets the access token on the session
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println(" (RESUME) finishAuth start");
        System.out.println("===========================================");
        System.out.println("===========================================");
          api.getSession().finishAuthentication();
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println(" (RESUME) finishAuth end");
        System.out.println("===========================================");
        System.out.println("===========================================");

          AccessTokenPair tokens = api.getSession().getAccessTokenPair();

          // Provide your own storeKeys to persist the access token pair
          // A typical way to store tokens is using SharedPreferences
          try {
            DropboxUpdateTool.getInstance().updateListenerAuthorising(false);
            DropboxUpdateTool.getInstance().updateListener(true, api.accountInfo());
          } catch (DropboxException dex) {
            Toast error = Toast.makeText(this, dex.getMessage(), Toast.LENGTH_LONG);
            error.show();
          } finally {
            storeKeys(tokens.key, tokens.secret);
            finish();
          }
        } catch (IllegalStateException e) {
          Log.i("DbAuthLog", "Error authenticating", e);
        }
      } else {
        // might be the case that the keys are invalid, or for some reason it's
        // failed; it won't hurt to clear the keys in this case:
        DropboxUpdateTool.getInstance().updateListenerAuthorising(true);
        DropboxUpdateTool.getInstance().clearKeys(getSharedPreferences(
                DropboxUpdateTool.ACCOUNT_PREFS_NAME, 0));
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("********************************* AUTH FAIL");
        System.out.println("===========================================");
        System.out.println("===========================================");
      }
    } finally {
      DropboxUpdateTool.getInstance().updateListenerAuthorising(false);
    }
  }

  /**
   * Shows keeping the access keys returned from Trusted Authenticator in a
   * local store, rather than storing user name & password, and
   * re-authenticating each time (which is not to be done, ever).
   *
   * @return Array of [access_key, access_secret], or null if none stored
   */
  public String[] getKeys() {
    SharedPreferences prefs = getSharedPreferences(
            DropboxUpdateTool.ACCOUNT_PREFS_NAME, 0);
    String key = prefs.getString(DropboxUpdateTool.ACCESS_KEY_NAME, null);
    String secret = prefs.getString(DropboxUpdateTool.ACCESS_SECRET_NAME, null);
    if (key != null && secret != null) {
      String[] ret = new String[2];
      ret[0] = key;
      ret[1] = secret;
      return ret;
    } else {
      return null;
    }
  }

  /**
   *
   */
  public void storeKeys(String key, String secret) {
    // Save the access key for later
    SharedPreferences prefs = getSharedPreferences(DropboxUpdateTool.ACCOUNT_PREFS_NAME, 0);
    Editor edit = prefs.edit();
    edit.putString(DropboxUpdateTool.ACCESS_KEY_NAME, key);
    edit.putString(DropboxUpdateTool.ACCESS_SECRET_NAME, secret);
    edit.commit();
  }

  /**
   * Read the secret key from an assets file.
   *
   * @return the secret key. Must pair up with the
   */
  static String readSecretKey() {
    String secretKey = "" + 525 + "ok" + 8 + "y" + "e" + "z" + "qmroy" + 1;
    // read your secret key from file assets/key-pass.txt
    // then pass it back
    return secretKey;
  }
}
