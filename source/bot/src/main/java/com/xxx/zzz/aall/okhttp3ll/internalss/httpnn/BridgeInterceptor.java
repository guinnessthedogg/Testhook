
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import java.io.IOException;
import java.util.List;

import com.xxx.zzz.aall.okhttp3ll.CookieJarza;
import com.xxx.zzz.aall.okhttp3ll.Interceptorza;
import com.xxx.zzz.aall.okhttp3ll.MediaTypeza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.Cookieza;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okhttp3ll.RequestBodyza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Versionaq;
import com.xxx.zzz.aall.okioss.GzipSourcezaq;
import com.xxx.zzz.aall.okioss.Okiozaq;


public final class BridgeInterceptor implements Interceptorza {
  private final CookieJarza cookieJar;

  public BridgeInterceptor(CookieJarza cookieJar) {
    this.cookieJar = cookieJar;
  }

  @Override public Responseza intercept(Chain chain) throws IOException {
    Requestza userRequest = chain.request();
    Requestza.Builder requestBuilder = userRequest.newBuilder();

    RequestBodyza body = userRequest.body();
    if (body != null) {
      MediaTypeza contentType = body.contentType();
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString());
      }

      long contentLength = body.contentLength();
      if (contentLength != -1) {
        requestBuilder.header("Content-Length", Long.toString(contentLength));
        requestBuilder.removeHeader("Transfer-Encoding");
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked");
        requestBuilder.removeHeader("Content-Length");
      }
    }

    if (userRequest.header("Host") == null) {
      requestBuilder.header("Host", Utilaq.hostHeader(userRequest.url(), false));
    }

    if (userRequest.header("Connection") == null) {
      requestBuilder.header("Connection", "Keep-Alive");
    }

    
    
    boolean transparentGzip = false;
    if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
      transparentGzip = true;
      requestBuilder.header("Accept-Encoding", "gzip");
    }

    List<Cookieza> cookies = cookieJar.loadForRequest(userRequest.url());
    if (!cookies.isEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies));
    }

    if (userRequest.header("User-Agent") == null) {
      requestBuilder.header("User-Agent", Versionaq.userAgent());
    }

    Responseza networkResponse = chain.proceed(requestBuilder.build());

    HttpHeaders.receiveHeaders(cookieJar, userRequest.url(), networkResponse.headers());

    Responseza.Builder responseBuilder = networkResponse.newBuilder()
        .request(userRequest);

    if (transparentGzip
        && "gzip".equalsIgnoreCase(networkResponse.header("Content-Encoding"))
        && HttpHeaders.hasBody(networkResponse)) {
      GzipSourcezaq responseBody = new GzipSourcezaq(networkResponse.body().source());
      Headersza strippedHeaders = networkResponse.headers().newBuilder()
          .removeAll("Content-Encoding")
          .removeAll("Content-Length")
          .build();
      responseBuilder.headers(strippedHeaders);
      responseBuilder.body(new RealResponseBody(strippedHeaders, Okiozaq.buffer(responseBody)));
    }

    return responseBuilder.build();
  }


  private String cookieHeader(List<Cookieza> cookies) {
    StringBuilder cookieHeader = new StringBuilder();
    for (int i = 0, size = cookies.size(); i < size; i++) {
      if (i > 0) {
        cookieHeader.append("; ");
      }
      Cookieza cookie = cookies.get(i);
      cookieHeader.append(cookie.name()).append('=').append(cookie.value());
    }
    return cookieHeader.toString();
  }
}
