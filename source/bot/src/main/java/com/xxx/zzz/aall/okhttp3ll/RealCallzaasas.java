
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.CallServerInterceptor;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RealInterceptorChain;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RetryAndFollowUpInterceptor;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xxx.zzz.aall.okhttp3ll.internalss.NamedRunnableq;
import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.CacheInterceptorq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.ConnectInterceptor;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.BridgeInterceptor;

final class RealCallzaasas implements Callzadasd {
  final OkHttpClientza client;
  final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;
  final EventListenerza eventListener;

  
  final Requestza originalRequest;
  final boolean forWebSocket;

  
  private boolean executed;

  RealCallzaasas(OkHttpClientza client, Requestza originalRequest, boolean forWebSocket) {
    final EventListenerza.Factory eventListenerFactory = client.eventListenerFactory();

    this.client = client;
    this.originalRequest = originalRequest;
    this.forWebSocket = forWebSocket;
    this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);

    
    this.eventListener = eventListenerFactory.create(this);
  }

  @Override public Requestza request() {
    return originalRequest;
  }

  @Override public Responseza execute() throws IOException {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    try {
      client.dispatcher().executed(this);
      Responseza result = getResponseWithInterceptorChain();
      if (result == null) throw new IOException("Canceled");
      return result;
    } finally {
      client.dispatcher().finished(this);
    }
  }

  private void captureCallStackTrace() {
    Object callStackTrace = Platformq.get().getStackTraceForCloseable("response.body().close()");
    retryAndFollowUpInterceptor.setCallStackTrace(callStackTrace);
  }

  @Override public void enqueue(Callbackza responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
  }

  @Override public void cancel() {
    retryAndFollowUpInterceptor.cancel();
  }

  @Override public synchronized boolean isExecuted() {
    return executed;
  }

  @Override public boolean isCanceled() {
    return retryAndFollowUpInterceptor.isCanceled();
  }

  @SuppressWarnings("CloneDoesntCallSuperClone") 
  @Override public RealCallzaasas clone() {
    return new RealCallzaasas(client, originalRequest, forWebSocket);
  }

  StreamAllocation streamAllocation() {
    return retryAndFollowUpInterceptor.streamAllocation();
  }

  final class AsyncCall extends NamedRunnableq {
    private final Callbackza responseCallback;

    AsyncCall(Callbackza responseCallback) {
      super("OkHttp %s", redactedUrl());
      this.responseCallback = responseCallback;
    }

    String host() {
      return originalRequest.url().host();
    }

    Requestza request() {
      return originalRequest;
    }

    RealCallzaasas get() {
      return RealCallzaasas.this;
    }

    @Override protected void execute() {
      boolean signalledCallback = false;
      try {
        Responseza response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCallzaasas.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCallzaasas.this, response);
        }
      } catch (IOException e) {
        if (signalledCallback) {
          
          Platformq.get().log(Platformq.INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          responseCallback.onFailure(RealCallzaasas.this, e);
        }
      } finally {
        client.dispatcher().finished(this);
      }
    }
  }

  
  String toLoggableString() {
    return (isCanceled() ? "canceled " : "")
        + (forWebSocket ? "web socket" : "call")
        + " to " + redactedUrl();
  }

  String redactedUrl() {
    return originalRequest.url().redact();
  }

  Responseza getResponseWithInterceptorChain() throws IOException {
    
    List<Interceptorza> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptorq(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptorza.Chain chain = new RealInterceptorChain(
        interceptors, null, null, null, 0, originalRequest);
    return chain.proceed(originalRequest);
  }
}
