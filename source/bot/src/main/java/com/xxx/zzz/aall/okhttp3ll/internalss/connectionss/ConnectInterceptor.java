
package com.xxx.zzz.aall.okhttp3ll.internalss.connectionss;

import com.xxx.zzz.aall.okhttp3ll.Interceptorza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpCodec;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RealInterceptorChain;

import java.io.IOException;


public final class ConnectInterceptor implements Interceptorza {
  public final OkHttpClientza client;

  public ConnectInterceptor(OkHttpClientza client) {
    this.client = client;
  }

  @Override public Responseza intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    Requestza request = realChain.request();
    StreamAllocation streamAllocation = realChain.streamAllocation();

    
    boolean doExtensiveHealthChecks = !request.method().equals("GET");
    HttpCodec httpCodec = streamAllocation.newStream(client, doExtensiveHealthChecks);
    RealConnection connection = streamAllocation.connection();

    return realChain.proceed(request, streamAllocation, httpCodec, connection);
  }
}
