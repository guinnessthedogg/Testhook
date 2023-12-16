
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import com.xxx.zzz.aall.okhttp3ll.Connectionza;
import com.xxx.zzz.aall.okhttp3ll.Interceptorza;

import java.io.IOException;
import java.util.List;

import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RealConnection;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;


public final class RealInterceptorChain implements Interceptorza.Chain {
  private final List<Interceptorza> interceptors;
  private final StreamAllocation streamAllocation;
  private final HttpCodec httpCodec;
  private final RealConnection connection;
  private final int index;
  private final Requestza request;
  private int calls;

  public RealInterceptorChain(List<Interceptorza> interceptors, StreamAllocation streamAllocation,
                              HttpCodec httpCodec, RealConnection connection, int index, Requestza request) {
    this.interceptors = interceptors;
    this.connection = connection;
    this.streamAllocation = streamAllocation;
    this.httpCodec = httpCodec;
    this.index = index;
    this.request = request;
  }

  @Override public Connectionza connection() {
    return connection;
  }

  public StreamAllocation streamAllocation() {
    return streamAllocation;
  }

  public HttpCodec httpStream() {
    return httpCodec;
  }

  @Override public Requestza request() {
    return request;
  }

  @Override public Responseza proceed(Requestza request) throws IOException {
    return proceed(request, streamAllocation, httpCodec, connection);
  }

  public Responseza proceed(Requestza request, StreamAllocation streamAllocation, HttpCodec httpCodec,
                            RealConnection connection) throws IOException {
    if (index >= interceptors.size()) throw new AssertionError();

    calls++;

    
    if (this.httpCodec != null && !this.connection.supportsUrl(request.url())) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must retain the same host and port");
    }

    
    if (this.httpCodec != null && calls > 1) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must call proceed() exactly once");
    }

    
    RealInterceptorChain next = new RealInterceptorChain(
        interceptors, streamAllocation, httpCodec, connection, index + 1, request);
    Interceptorza interceptor = interceptors.get(index);
    Responseza response = interceptor.intercept(next);

    
    if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
      throw new IllegalStateException("network interceptor " + interceptor
          + " must call proceed() exactly once");
    }

    
    if (response == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }

    return response;
  }
}
