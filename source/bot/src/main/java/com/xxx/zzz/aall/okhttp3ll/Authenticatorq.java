
package com.xxx.zzz.aall.okhttp3ll;

import java.io.IOException;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public interface Authenticatorq {

  Authenticatorq NONE = new Authenticatorq() {
    @Override public Requestza authenticate(Routeza route, Responseza response) {
      return null;
    }
  };


  @Nullableq
  Requestza authenticate(Routeza route, Responseza response) throws IOException;
}
