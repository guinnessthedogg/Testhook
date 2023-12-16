
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import java.io.IOException;

import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.ResponseBodyza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okioss.Sinkzaq;


public interface HttpCodec {

  int DISCARD_STREAM_TIMEOUT_MILLIS = 100;


  Sinkzaq createRequestBody(Requestza request, long contentLength);


  void writeRequestHeaders(Requestza request) throws IOException;


  void flushRequest() throws IOException;


  void finishRequest() throws IOException;


  Responseza.Builder readResponseHeaders(boolean expectContinue) throws IOException;


  ResponseBodyza openResponseBody(Responseza response) throws IOException;


  void cancel();
}
