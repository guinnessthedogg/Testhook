
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import java.net.HttpURLConnection;
import java.net.Proxy;

import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;

public final class RequestLine {
  private RequestLine() {
  }

  
  public static String get(Requestza request, Proxy.Type proxyType) {
    StringBuilder result = new StringBuilder();
    result.append(request.method());
    result.append(' ');

    if (includeAuthorityInRequestLine(request, proxyType)) {
      result.append(request.url());
    } else {
      result.append(requestPath(request.url()));
    }

    result.append(" HTTP/1.1");
    return result.toString();
  }

  
  private static boolean includeAuthorityInRequestLine(Requestza request, Proxy.Type proxyType) {
    return !request.isHttps() && proxyType == Proxy.Type.HTTP;
  }

  
  public static String requestPath(HttpUrlza url) {
    String path = url.encodedPath();
    String query = url.encodedQuery();
    return query != null ? (path + '?' + query) : path;
  }
}
