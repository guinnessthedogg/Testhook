
package com.xxx.zzz.aall.okioss;

import java.io.IOException;


public abstract class ForwardingSourcezaq implements Sourcezaq {
  private final Sourcezaq delegate;

  public ForwardingSourcezaq(Sourcezaq delegate) {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
  }


  public final Sourcezaq delegate() {
    return delegate;
  }

  @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
    return delegate.read(sink, byteCount);
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
