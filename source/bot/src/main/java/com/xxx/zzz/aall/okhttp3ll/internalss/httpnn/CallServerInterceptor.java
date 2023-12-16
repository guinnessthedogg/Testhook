
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import java.io.IOException;
import java.net.ProtocolException;

import com.xxx.zzz.aall.okhttp3ll.Interceptorza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RealConnection;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;


public final class CallServerInterceptor implements Interceptorza {
  private final boolean forWebSocket;

  public CallServerInterceptor(boolean forWebSocket) {
    this.forWebSocket = forWebSocket;
  }

  @Override public Responseza intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    HttpCodec httpCodec = realChain.httpStream();
    StreamAllocation streamAllocation = realChain.streamAllocation();
    RealConnection connection = (RealConnection) realChain.connection();
    Requestza request = realChain.request();

    long sentRequestMillis = System.currentTimeMillis();
    httpCodec.writeRequestHeaders(request);

    Responseza.Builder responseBuilder = null;
    if (HttpMethod.permitsRequestBody(request.method()) && request.body() != null) {
      
      
      
      if ("100-continue".equalsIgnoreCase(request.header("Expect"))) {
        httpCodec.flushRequest();
        responseBuilder = httpCodec.readResponseHeaders(true);
      }

      if (responseBuilder == null) {
        
        Sinkzaq requestBodyOut = httpCodec.createRequestBody(request, request.body().contentLength());
        BufferedSinkzaqds bufferedRequestBody = Okiozaq.buffer(requestBodyOut);
        request.body().writeTo(bufferedRequestBody);
        bufferedRequestBody.close();
      } else if (!connection.isMultiplexed()) {
        
        
        
        streamAllocation.noNewStreams();
      }
    }

    httpCodec.finishRequest();

    if (responseBuilder == null) {
      responseBuilder = httpCodec.readResponseHeaders(false);
    }

    Responseza response = responseBuilder
        .request(request)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();

    int code = response.code();
    if (forWebSocket && code == 101) {
      
      response = response.newBuilder()
          .body(Utilaq.EMPTY_RESPONSE)
          .build();
    } else {
      response = response.newBuilder()
          .body(httpCodec.openResponseBody(response))
          .build();
    }

    if ("close".equalsIgnoreCase(response.request().header("Connection"))
        || "close".equalsIgnoreCase(response.header("Connection"))) {
      streamAllocation.noNewStreams();
    }

    if ((code == 204 || code == 205) && response.body().contentLength() > 0) {
      throw new ProtocolException(
          "HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
    }

    return response;
  }
}
