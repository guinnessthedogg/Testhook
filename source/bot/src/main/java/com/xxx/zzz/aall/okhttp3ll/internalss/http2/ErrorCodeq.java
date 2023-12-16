
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

public enum ErrorCodeq {
  
  NO_ERROR(0),

  PROTOCOL_ERROR(1),

  INTERNAL_ERROR(2),

  FLOW_CONTROL_ERROR(3),

  REFUSED_STREAM(7),

  CANCEL(8);

  public final int httpCode;

  ErrorCodeq(int httpCode) {
    this.httpCode = httpCode;
  }

  public static ErrorCodeq fromHttp2(int code) {
    for (ErrorCodeq errorCode : ErrorCodeq.values()) {
      if (errorCode.httpCode == code) return errorCode;
    }
    return null;
  }
}
