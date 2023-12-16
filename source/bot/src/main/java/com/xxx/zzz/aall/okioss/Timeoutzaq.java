
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;


public class Timeoutzaq {

  public static final Timeoutzaq NONE = new Timeoutzaq() {
    @Override public Timeoutzaq timeout(long timeout, TimeUnit unit) {
      return this;
    }

    @Override public Timeoutzaq deadlineNanoTime(long deadlineNanoTime) {
      return this;
    }

    @Override public void throwIfReached() throws IOException {
    }
  };


  private boolean hasDeadline;
  private long deadlineNanoTime;
  private long timeoutNanos;

  public Timeoutzaq() {
  }


  public Timeoutzaq timeout(long timeout, TimeUnit unit) {
    if (timeout < 0) throw new IllegalArgumentException("timeout < 0: " + timeout);
    if (unit == null) throw new IllegalArgumentException("unit == null");
    this.timeoutNanos = unit.toNanos(timeout);
    return this;
  }


  public long timeoutNanos() {
    return timeoutNanos;
  }


  public boolean hasDeadline() {
    return hasDeadline;
  }


  public long deadlineNanoTime() {
    if (!hasDeadline) throw new IllegalStateException("No deadline");
    return deadlineNanoTime;
  }


  public Timeoutzaq deadlineNanoTime(long deadlineNanoTime) {
    this.hasDeadline = true;
    this.deadlineNanoTime = deadlineNanoTime;
    return this;
  }


  public final Timeoutzaq deadline(long duration, TimeUnit unit) {
    if (duration <= 0) throw new IllegalArgumentException("duration <= 0: " + duration);
    if (unit == null) throw new IllegalArgumentException("unit == null");
    return deadlineNanoTime(System.nanoTime() + unit.toNanos(duration));
  }


  public Timeoutzaq clearTimeout() {
    this.timeoutNanos = 0;
    return this;
  }


  public Timeoutzaq clearDeadline() {
    this.hasDeadline = false;
    return this;
  }


  public void throwIfReached() throws IOException {
    if (Thread.interrupted()) {
      throw new InterruptedIOException("thread interrupted");
    }

    if (hasDeadline && deadlineNanoTime - System.nanoTime() <= 0) {
      throw new InterruptedIOException("deadline reached");
    }
  }


  public final void waitUntilNotified(Object monitor) throws InterruptedIOException {
    try {
      boolean hasDeadline = hasDeadline();
      long timeoutNanos = timeoutNanos();

      if (!hasDeadline && timeoutNanos == 0L) {
        monitor.wait();
        return;
      }


      long waitNanos;
      long start = System.nanoTime();
      if (hasDeadline && timeoutNanos != 0) {
        long deadlineNanos = deadlineNanoTime() - start;
        waitNanos = Math.min(timeoutNanos, deadlineNanos);
      } else if (hasDeadline) {
        waitNanos = deadlineNanoTime() - start;
      } else {
        waitNanos = timeoutNanos;
      }


      long elapsedNanos = 0L;
      if (waitNanos > 0L) {
        long waitMillis = waitNanos / 1000000L;
        monitor.wait(waitMillis, (int) (waitNanos - waitMillis * 1000000L));
        elapsedNanos = System.nanoTime() - start;
      }


      if (elapsedNanos >= waitNanos) {
        throw new InterruptedIOException("timeout");
      }
    } catch (InterruptedException e) {
      throw new InterruptedIOException("interrupted");
    }
  }
}
