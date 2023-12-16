
package com.xxx.zzz.aall.okioss;

import static com.xxx.zzz.aall.okioss.Utilzaqq.checkOffsetAndCount;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public class AsyncTimeoutzaq extends Timeoutzaq {

  private static final int TIMEOUT_WRITE_SIZE = 64 * 1024;


  private static final long IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60);
  private static final long IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(IDLE_TIMEOUT_MILLIS);


  static @Nullableq
  AsyncTimeoutzaq head;


  private boolean inQueue;


  private @Nullableq
  AsyncTimeoutzaq next;


  private long timeoutAt;

  public final void enter() {
    if (inQueue) throw new IllegalStateException("Unbalanced enter/exit");
    long timeoutNanos = timeoutNanos();
    boolean hasDeadline = hasDeadline();
    if (timeoutNanos == 0 && !hasDeadline) {
      return;
    }
    inQueue = true;
    scheduleTimeout(this, timeoutNanos, hasDeadline);
  }

  private static synchronized void scheduleTimeout(
          AsyncTimeoutzaq node, long timeoutNanos, boolean hasDeadline) {

    if (head == null) {
      head = new AsyncTimeoutzaq();
      new Watchdog().start();
    }

    long now = System.nanoTime();
    if (timeoutNanos != 0 && hasDeadline) {


      node.timeoutAt = now + Math.min(timeoutNanos, node.deadlineNanoTime() - now);
    } else if (timeoutNanos != 0) {
      node.timeoutAt = now + timeoutNanos;
    } else if (hasDeadline) {
      node.timeoutAt = node.deadlineNanoTime();
    } else {
      throw new AssertionError();
    }


    long remainingNanos = node.remainingNanos(now);
    for (AsyncTimeoutzaq prev = head; true; prev = prev.next) {
      if (prev.next == null || remainingNanos < prev.next.remainingNanos(now)) {
        node.next = prev.next;
        prev.next = node;
        if (prev == head) {
          AsyncTimeoutzaq.class.notify();
        }
        break;
      }
    }
  }


  public final boolean exit() {
    if (!inQueue) return false;
    inQueue = false;
    return cancelScheduledTimeout(this);
  }


  private static synchronized boolean cancelScheduledTimeout(AsyncTimeoutzaq node) {

    for (AsyncTimeoutzaq prev = head; prev != null; prev = prev.next) {
      if (prev.next == node) {
        prev.next = node.next;
        node.next = null;
        return false;
      }
    }


    return true;
  }


  private long remainingNanos(long now) {
    return timeoutAt - now;
  }


  protected void timedOut() {
  }


  public final Sinkzaq sink(final Sinkzaq sink) {
    return new Sinkzaq() {
      @Override public void write(Bufferzaq source, long byteCount) throws IOException {
        checkOffsetAndCount(source.size, 0, byteCount);

        while (byteCount > 0L) {

          long toWrite = 0L;
          for (Segmentzaq s = source.head; toWrite < TIMEOUT_WRITE_SIZE; s = s.next) {
            int segmentSize = source.head.limit - source.head.pos;
            toWrite += segmentSize;
            if (toWrite >= byteCount) {
              toWrite = byteCount;
              break;
            }
          }


          boolean throwOnTimeout = false;
          enter();
          try {
            sink.write(source, toWrite);
            byteCount -= toWrite;
            throwOnTimeout = true;
          } catch (IOException e) {
            throw exit(e);
          } finally {
            exit(throwOnTimeout);
          }
        }
      }

      @Override public void flush() throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          sink.flush();
          throwOnTimeout = true;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }

      @Override public void close() throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          sink.close();
          throwOnTimeout = true;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }

      @Override public Timeoutzaq timeout() {
        return AsyncTimeoutzaq.this;
      }

      @Override public String toString() {
        return "AsyncTimeout.sink(" + sink + ")";
      }
    };
  }


  public final Sourcezaq source(final Sourcezaq source) {
    return new Sourcezaq() {
      @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          long result = source.read(sink, byteCount);
          throwOnTimeout = true;
          return result;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }

      @Override public void close() throws IOException {
        boolean throwOnTimeout = false;
        try {
          source.close();
          throwOnTimeout = true;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }

      @Override public Timeoutzaq timeout() {
        return AsyncTimeoutzaq.this;
      }

      @Override public String toString() {
        return "AsyncTimeout.source(" + source + ")";
      }
    };
  }


  final void exit(boolean throwOnTimeout) throws IOException {
    boolean timedOut = exit();
    if (timedOut && throwOnTimeout) throw newTimeoutException(null);
  }


  final IOException exit(IOException cause) throws IOException {
    if (!exit()) return cause;
    return newTimeoutException(cause);
  }


  protected IOException newTimeoutException(@Nullableq IOException cause) {
    InterruptedIOException e = new InterruptedIOException("timeout");
    if (cause != null) {
      e.initCause(cause);
    }
    return e;
  }

  private static final class Watchdog extends Thread {
    Watchdog() {
      super("Okio Watchdog");
      setDaemon(true);
    }

    public void run() {
      while (true) {
        try {
          AsyncTimeoutzaq timedOut;
          synchronized (AsyncTimeoutzaq.class) {
            timedOut = awaitTimeout();


            if (timedOut == null) continue;



            if (timedOut == head) {
              head = null;
              return;
            }
          }


          timedOut.timedOut();
        } catch (InterruptedException ignored) {
        }
      }
    }
  }


  static @Nullableq
  AsyncTimeoutzaq awaitTimeout() throws InterruptedException {

    AsyncTimeoutzaq node = head.next;


    if (node == null) {
      long startNanos = System.nanoTime();
      AsyncTimeoutzaq.class.wait(IDLE_TIMEOUT_MILLIS);
      return head.next == null && (System.nanoTime() - startNanos) >= IDLE_TIMEOUT_NANOS
          ? head
          : null;
    }

    long waitNanos = node.remainingNanos(System.nanoTime());


    if (waitNanos > 0) {


      long waitMillis = waitNanos / 1000000L;
      waitNanos -= (waitMillis * 1000000L);
      AsyncTimeoutzaq.class.wait(waitMillis, (int) waitNanos);
      return null;
    }


    head.next = node.next;
    node.next = null;
    return node;
  }
}
