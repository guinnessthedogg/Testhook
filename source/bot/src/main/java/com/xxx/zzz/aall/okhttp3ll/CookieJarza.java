
package com.xxx.zzz.aall.okhttp3ll;

import java.util.Collections;
import java.util.List;


public interface CookieJarza {

  CookieJarza NO_COOKIES = new CookieJarza() {
    @Override public void saveFromResponse(HttpUrlza url, List<Cookieza> cookies) {
    }

    @Override public List<Cookieza> loadForRequest(HttpUrlza url) {
      return Collections.emptyList();
    }
  };


  void saveFromResponse(HttpUrlza url, List<Cookieza> cookies);


  List<Cookieza> loadForRequest(HttpUrlza url);
}
