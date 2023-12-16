
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.IOException;
import java.util.List;

import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;


public interface PushObservera {
  
  boolean onRequest(int streamId, List<Headera> requestHeaders);

  
  boolean onHeaders(int streamId, List<Headera> responseHeaders, boolean last);

  
  boolean onData(int streamId, BufferedSourcezaqdfs source, int byteCount, boolean last)
      throws IOException;

  
  void onReset(int streamId, ErrorCodeq errorCode);

  PushObservera CANCEL = new PushObservera() {

    @Override public boolean onRequest(int streamId, List<Headera> requestHeaders) {
      return true;
    }

    @Override public boolean onHeaders(int streamId, List<Headera> responseHeaders, boolean last) {
      return true;
    }

    @Override public boolean onData(int streamId, BufferedSourcezaqdfs source, int byteCount,
                                    boolean last) throws IOException {
      source.skip(byteCount);
      return true;
    }

    @Override public void onReset(int streamId, ErrorCodeq errorCode) {
    }
  };
}
