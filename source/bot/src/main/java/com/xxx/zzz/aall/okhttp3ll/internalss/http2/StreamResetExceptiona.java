
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.IOException;


public final class StreamResetExceptiona extends IOException {
  public final ErrorCodeq errorCode;

  public StreamResetExceptiona(ErrorCodeq errorCode) {
    super("stream was reset: " + errorCode);
    this.errorCode = errorCode;
  }
}
