
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ForwardingTimeoutzaq extends Timeoutzaq {
  private Timeoutzaq delegate;

  public ForwardingTimeoutzaq(Timeoutzaq delegate) {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
  }


  public final Timeoutzaq delegate() {
    return delegate;
  }

  public final ForwardingTimeoutzaq setDelegate(Timeoutzaq delegate) {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
    return this;
  }

  @Override public Timeoutzaq timeout(long timeout, TimeUnit unit) {
    return delegate.timeout(timeout, unit);
  }

  @Override public long timeoutNanos() {
    return delegate.timeoutNanos();
  }

  @Override public boolean hasDeadline() {
    return delegate.hasDeadline();
  }

  @Override public long deadlineNanoTime() {
    return delegate.deadlineNanoTime();
  }

  @Override public Timeoutzaq deadlineNanoTime(long deadlineNanoTime) {
    return delegate.deadlineNanoTime(deadlineNanoTime);
  }

  @Override public Timeoutzaq clearTimeout() {
    return delegate.clearTimeout();
  }

  @Override public Timeoutzaq clearDeadline() {
    return delegate.clearDeadline();
  }

  @Override public void throwIfReached() throws IOException {
    delegate.throwIfReached();
  }
}
