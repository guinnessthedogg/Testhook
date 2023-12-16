
package com.xxx.zzz.aall.okioss;

import java.io.IOException;


public abstract class ForwardingSinkzaq implements Sinkzaq {
  private final Sinkzaq delegate;

  public ForwardingSinkzaq(Sinkzaq delegate) {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
  }


  public final Sinkzaq delegate() {
    return delegate;
  }

  @Override public void write(Bufferzaq source, long byteCount) throws IOException {
    delegate.write(source, byteCount);
  }

  @Override public void flush() throws IOException {
    delegate.flush();
  }

  @Override public Timeoutzaq timeout() {
    return delegate.timeout();
  }

  @Override public void close() throws IOException {
    delegate.close();
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "(" + delegate.toString() + ")";
  }
}
