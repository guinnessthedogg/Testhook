
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okioss.ByteStringzaq;


public interface WebSocketzqa {
  
  Requestza request();

  
  long queueSize();

  
  boolean send(String text);

  
  boolean send(ByteStringzaq bytes);

  
  boolean close(int code, @Nullableq String reason);

  
  void cancel();

  interface Factory {
    WebSocketzqa newWebSocket(Requestza request, WebSocketListenerzaq listener);
  }
}
