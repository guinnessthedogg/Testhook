
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpMethod;

import java.net.URL;
import java.util.List;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class Requestza {
  final HttpUrlza url;
  final String method;
  final Headersza headers;
  final @Nullableq
  RequestBodyza body;
  final Object tag;

  private volatile CacheControlz cacheControl; 

  Requestza(Builder builder) {
    this.url = builder.url;
    this.method = builder.method;
    this.headers = builder.headers.build();
    this.body = builder.body;
    this.tag = builder.tag != null ? builder.tag : this;
  }

  public HttpUrlza url() {
    return url;
  }

  public String method() {
    return method;
  }

  public Headersza headers() {
    return headers;
  }

  public String header(String name) {
    return headers.get(name);
  }

  public List<String> headers(String name) {
    return headers.values(name);
  }

  public @Nullableq
  RequestBodyza body() {
    return body;
  }

  public Object tag() {
    return tag;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  
  public CacheControlz cacheControl() {
    CacheControlz result = cacheControl;
    return result != null ? result : (cacheControl = CacheControlz.parse(headers));
  }

  public boolean isHttps() {
    return url.isHttps();
  }

  @Override public String toString() {
    return "Request{method="
        + method
        + ", url="
        + url
        + ", tag="
        + (tag != this ? tag : null)
        + '}';
  }

  public static class Builder {
    HttpUrlza url;
    String method;
    Headersza.Builder headers;
    RequestBodyza body;
    Object tag;

    public Builder() {
      this.method = "GET";
      this.headers = new Headersza.Builder();
    }

    Builder(Requestza request) {
      this.url = request.url;
      this.method = request.method;
      this.body = request.body;
      this.tag = request.tag;
      this.headers = request.headers.newBuilder();
    }

    public Builder url(HttpUrlza url) {
      if (url == null) throw new NullPointerException("url == null");
      this.url = url;
      return this;
    }

    
    public Builder url(String url) {
      if (url == null) throw new NullPointerException("url == null");

      
      if (url.regionMatches(true, 0, "ws:", 0, 3)) {
        url = "http:" + url.substring(3);
      } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
        url = "https:" + url.substring(4);
      }

      HttpUrlza parsed = HttpUrlza.parse(url);
      if (parsed == null) throw new IllegalArgumentException("unexpected url: " + url);
      return url(parsed);
    }

    
    public Builder url(URL url) {
      if (url == null) throw new NullPointerException("url == null");
      HttpUrlza parsed = HttpUrlza.get(url);
      if (parsed == null) throw new IllegalArgumentException("unexpected url: " + url);
      return url(parsed);
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

    
    public Builder cacheControl(CacheControlz cacheControl) {
      String value = cacheControl.toString();
      if (value.isEmpty()) return removeHeader("Cache-Control");
      return header("Cache-Control", value);
    }

    public Builder get() {
      return method("GET", null);
    }

    public Builder head() {
      return method("HEAD", null);
    }

    public Builder post(RequestBodyza body) {
      return method("POST", body);
    }

    public Builder delete(@Nullableq RequestBodyza body) {
      return method("DELETE", body);
    }

    public Builder delete() {
      return delete(Utilaq.EMPTY_REQUEST);
    }

    public Builder put(RequestBodyza body) {
      return method("PUT", body);
    }

    public Builder patch(RequestBodyza body) {
      return method("PATCH", body);
    }

    public Builder method(String method, @Nullableq RequestBodyza body) {
      if (method == null) throw new NullPointerException("method == null");
      if (method.length() == 0) throw new IllegalArgumentException("method.length() == 0");
      if (body != null && !HttpMethod.permitsRequestBody(method)) {
        throw new IllegalArgumentException("method " + method + " must not have a request body.");
      }
      if (body == null && HttpMethod.requiresRequestBody(method)) {
        throw new IllegalArgumentException("method " + method + " must have a request body.");
      }
      this.method = method;
      this.body = body;
      return this;
    }

    
    public Builder tag(Object tag) {
      this.tag = tag;
      return this;
    }

    public Requestza build() {
      if (url == null) throw new IllegalStateException("url == null");
      return new Requestza(this);
    }
  }
}
