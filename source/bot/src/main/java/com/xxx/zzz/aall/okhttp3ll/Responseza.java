
package com.xxx.zzz.aall.okhttp3ll;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_PROXY_AUTH;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpHeaders;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;


public final class Responseza implements Closeable {
  final Requestza request;
  final Protocolza protocol;
  final int code;
  final String message;
  final @Nullableq
  Handshakeza handshake;
  final Headersza headers;
  final @Nullableq
  ResponseBodyza body;
  final @Nullableq
  Responseza networkResponse;
  final @Nullableq
  Responseza cacheResponse;
  final @Nullableq
  Responseza priorResponse;
  final long sentRequestAtMillis;
  final long receivedResponseAtMillis;

  private volatile CacheControlz cacheControl; 

  Responseza(Builder builder) {
    this.request = builder.request;
    this.protocol = builder.protocol;
    this.code = builder.code;
    this.message = builder.message;
    this.handshake = builder.handshake;
    this.headers = builder.headers.build();
    this.body = builder.body;
    this.networkResponse = builder.networkResponse;
    this.cacheResponse = builder.cacheResponse;
    this.priorResponse = builder.priorResponse;
    this.sentRequestAtMillis = builder.sentRequestAtMillis;
    this.receivedResponseAtMillis = builder.receivedResponseAtMillis;
  }


  public Requestza request() {
    return request;
  }


  public Protocolza protocol() {
    return protocol;
  }


  public int code() {
    return code;
  }


  public boolean isSuccessful() {
    return code >= 200 && code < 300;
  }


  public String message() {
    return message;
  }


  public Handshakeza handshake() {
    return handshake;
  }

  public List<String> headers(String name) {
    return headers.values(name);
  }

  public @Nullableq
  String header(String name) {
    return header(name, null);
  }

  public @Nullableq
  String header(String name, @Nullableq String defaultValue) {
    String result = headers.get(name);
    return result != null ? result : defaultValue;
  }

  public Headersza headers() {
    return headers;
  }


  public ResponseBodyza peekBody(long byteCount) throws IOException {
    BufferedSourcezaqdfs source = body.source();
    source.request(byteCount);
    Bufferzaq copy = source.buffer().clone();

    
    Bufferzaq result;
    if (copy.size() > byteCount) {
      result = new Bufferzaq();
      result.write(copy, byteCount);
      copy.clear();
    } else {
      result = copy;
    }

    return ResponseBodyza.create(body.contentType(), result.size(), result);
  }


  public @Nullableq
  ResponseBodyza body() {
    return body;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }


  public boolean isRedirect() {
    switch (code) {
      case StatusLine.HTTP_PERM_REDIRECT:
      case StatusLine.HTTP_TEMP_REDIRECT:
      case HTTP_MULT_CHOICE:
      case HTTP_MOVED_PERM:
      case HTTP_MOVED_TEMP:
      case HTTP_SEE_OTHER:
        return true;
      default:
        return false;
    }
  }


  public @Nullableq
  Responseza networkResponse() {
    return networkResponse;
  }


  public @Nullableq
  Responseza cacheResponse() {
    return cacheResponse;
  }


  public @Nullableq
  Responseza priorResponse() {
    return priorResponse;
  }


  public List<Challengeza> challenges() {
    String responseField;
    if (code == HTTP_UNAUTHORIZED) {
      responseField = "WWW-Authenticate";
    } else if (code == HTTP_PROXY_AUTH) {
      responseField = "Proxy-Authenticate";
    } else {
      return Collections.emptyList();
    }
    return HttpHeaders.parseChallenges(headers(), responseField);
  }


  public CacheControlz cacheControl() {
    CacheControlz result = cacheControl;
    return result != null ? result : (cacheControl = CacheControlz.parse(headers));
  }


  public long sentRequestAtMillis() {
    return sentRequestAtMillis;
  }


  public long receivedResponseAtMillis() {
    return receivedResponseAtMillis;
  }


  @Override public void close() {
    body.close();
  }

  @Override public String toString() {
    return "Response{protocol="
        + protocol
        + ", code="
        + code
        + ", message="
        + message
        + ", url="
        + request.url()
        + '}';
  }

  public static class Builder {
    Requestza request;
    Protocolza protocol;
    int code = -1;
    String message;
    @Nullableq
    Handshakeza handshake;
    Headersza.Builder headers;
    ResponseBodyza body;
    Responseza networkResponse;
    Responseza cacheResponse;
    Responseza priorResponse;
    long sentRequestAtMillis;
    long receivedResponseAtMillis;

    public Builder() {
      headers = new Headersza.Builder();
    }

    Builder(Responseza response) {
      this.request = response.request;
      this.protocol = response.protocol;
      this.code = response.code;
      this.message = response.message;
      this.handshake = response.handshake;
      this.headers = response.headers.newBuilder();
      this.body = response.body;
      this.networkResponse = response.networkResponse;
      this.cacheResponse = response.cacheResponse;
      this.priorResponse = response.priorResponse;
      this.sentRequestAtMillis = response.sentRequestAtMillis;
      this.receivedResponseAtMillis = response.receivedResponseAtMillis;
    }

    public Builder request(Requestza request) {
      this.request = request;
      return this;
    }

    public Builder protocol(Protocolza protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder code(int code) {
      this.code = code;
      return this;
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder handshake(@Nullableq Handshakeza handshake) {
      this.handshake = handshake;
      return this;
    }


    public Builder header(String name, String value) {
      headers.set(name, value);
      return this;
    }


    public Builder addHeader(String name, String value) {
      headers.add(name, value);
      return this;
    }

    public Builder removeHeader(String name) {
      headers.removeAll(name);
      return this;
    }


    public Builder headers(Headersza headers) {
      this.headers = headers.newBuilder();
      return this;
    }

    public Builder body(@Nullableq ResponseBodyza body) {
      this.body = body;
      return this;
    }

    public Builder networkResponse(@Nullableq Responseza networkResponse) {
      if (networkResponse != null) checkSupportResponse("networkResponse", networkResponse);
      this.networkResponse = networkResponse;
      return this;
    }

    public Builder cacheResponse(@Nullableq Responseza cacheResponse) {
      if (cacheResponse != null) checkSupportResponse("cacheResponse", cacheResponse);
      this.cacheResponse = cacheResponse;
      return this;
    }

    private void checkSupportResponse(String name, Responseza response) {
      if (response.body != null) {
        throw new IllegalArgumentException(name + ".body != null");
      } else if (response.networkResponse != null) {
        throw new IllegalArgumentException(name + ".networkResponse != null");
      } else if (response.cacheResponse != null) {
        throw new IllegalArgumentException(name + ".cacheResponse != null");
      } else if (response.priorResponse != null) {
        throw new IllegalArgumentException(name + ".priorResponse != null");
      }
    }

    public Builder priorResponse(@Nullableq Responseza priorResponse) {
      if (priorResponse != null) checkPriorResponse(priorResponse);
      this.priorResponse = priorResponse;
      return this;
    }

    private void checkPriorResponse(Responseza response) {
      if (response.body != null) {
        throw new IllegalArgumentException("priorResponse.body != null");
      }
    }

    public Builder sentRequestAtMillis(long sentRequestAtMillis) {
      this.sentRequestAtMillis = sentRequestAtMillis;
      return this;
    }

    public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
      this.receivedResponseAtMillis = receivedResponseAtMillis;
      return this;
    }

    public Responseza build() {
      if (request == null) throw new IllegalStateException("request == null");
      if (protocol == null) throw new IllegalStateException("protocol == null");
      if (code < 0) throw new IllegalStateException("code < 0: " + code);
      if (message == null) throw new IllegalStateException("message == null");
      return new Responseza(this);
    }
  }
}
