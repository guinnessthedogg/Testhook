
package com.xxx.zzz.aall.okhttp3ll.internalss.cachenn;

import java.io.IOException;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.ForwardingSinkzaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;


class FaultHidingSinkq extends ForwardingSinkzaq {
  private boolean hasErrors;

  FaultHidingSinkq(Sinkzaq delegate) {
    super(delegate);
  }

  @Override public void write(Bufferzaq source, long byteCount) throws IOException {
    if (hasErrors) {
      source.skip(byteCount);
      return;
    }
    try {
      super.write(source, byteCount);
    } catch (IOException e) {
      hasErrors = true;
      onException(e);
    }
  }

  @Override public void flush() throws IOException {
    if (hasErrors) return;
    try {
      super.flush();
    } catch (IOException e) {
      hasErrors = true;
      onException(e);
    }
  }

  @Override public void close() throws IOException {
    if (hasErrors) return;
    try {
      super.close();
    } catch (IOException e) {
      hasErrors = true;
      onException(e);
    }
  }

  protected void onException(IOException e) {
  }
}
