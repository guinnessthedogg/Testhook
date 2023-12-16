
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import com.xxx.zzz.aall.okhttp3ll.MediaTypeza;
import com.xxx.zzz.aall.okhttp3ll.ResponseBodyza;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;

public final class RealResponseBody extends ResponseBodyza {
  private final Headersza headers;
  private final BufferedSourcezaqdfs source;

  public RealResponseBody(Headersza headers, BufferedSourcezaqdfs source) {
    this.headers = headers;
    this.source = source;
  }

  @Override public MediaTypeza contentType() {
    String contentType = headers.get("Content-Type");
    return contentType != null ? MediaTypeza.parse(contentType) : null;
  }

  @Override public long contentLength() {
    return HttpHeaders.contentLength(headers);
  }

  @Override public BufferedSourcezaqdfs source() {
    return source;
  }
}
