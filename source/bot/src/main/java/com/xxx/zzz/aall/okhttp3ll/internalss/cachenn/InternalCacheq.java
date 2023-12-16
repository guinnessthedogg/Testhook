
package com.xxx.zzz.aall.okhttp3ll.internalss.cachenn;

import com.xxx.zzz.aall.okhttp3ll.Cachea;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;

import java.io.IOException;


public interface InternalCacheq {
  Responseza get(Requestza request) throws IOException;

  CacheRequestq put(Responseza response) throws IOException;


  void remove(Requestza request) throws IOException;


  void update(Responseza cached, Responseza network);


  void trackConditionalCacheHit();


  void trackResponse(CacheStrategyq cacheStrategy);
}
