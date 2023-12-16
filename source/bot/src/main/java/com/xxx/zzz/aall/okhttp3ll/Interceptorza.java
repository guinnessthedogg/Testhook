
package com.xxx.zzz.aall.okhttp3ll;

import java.io.IOException;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public interface Interceptorza {
  Responseza intercept(Chain chain) throws IOException;

  interface Chain {
    Requestza request();

    Responseza proceed(Requestza request) throws IOException;


    @Nullableq
    Connectionza connection();
  }
}
