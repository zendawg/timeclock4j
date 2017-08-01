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
package org.statefive.timeclockj.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 *
 * @author rich
 */
public class ViewWebContentActivity extends Activity {

  public static final String WEB_CONTENT =
          "org.statefive.timeclockj.android.ViewWebContentActivity.WEB_CONTENT";

  private WebView webview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle extras = getIntent().getExtras();
    webview = new WebView(this.getApplicationContext());
    webview.setWebViewClient(new SimpleWebViewClient());
    final String content =
            extras.getString(ViewWebContentActivity.WEB_CONTENT);
    webview.loadDataWithBaseURL(null, content,
            "text/html", "UTF8", null);
    setContentView(webview);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
          if (webview.canGoBack() == true) {
            webview.goBack();
          } else {
            finish();
          }
          return true;
      }

    }
    return super.onKeyDown(keyCode, event);
  }

  private class SimpleWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true;
    }
  }
}
