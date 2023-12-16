
package com.xxx.zzz.aall.okioss;

import java.io.IOException;


public final class Pipezaq {
  final long maxBufferSize;
  final Bufferzaq buffer = new Bufferzaq();
  boolean sinkClosed;
  boolean sourceClosed;
  private final Sinkzaq sink = new PipeSink();
  private final Sourcezaq source = new PipeSource();

  public Pipezaq(long maxBufferSize) {
    if (maxBufferSize < 1L) {
      throw new IllegalArgumentException("maxBufferSize < 1: " + maxBufferSize);
    }
    this.maxBufferSize = maxBufferSize;
  }

  public Sourcezaq source() {
    return source;
  }

  public Sinkzaq sink() {
    return sink;
  }

  final class PipeSink implements Sinkzaq {
    final Timeoutzaq timeout = new Timeoutzaq();

    @Override public void write(Bufferzaq source, long byteCount) throws IOException {
      synchronized (buffer) {
        if (sinkClosed) throw new IllegalStateException("closed");

        while (byteCount > 0) {
          if (sourceClosed) throw new IOException("source is closed");

          long bufferSpaceAvailable = maxBufferSize - buffer.size();
          if (bufferSpaceAvailable == 0) {
            timeout.waitUntilNotified(buffer);
            continue;
          }

          long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount);
          buffer.write(source, bytesToWrite);
          byteCount -= bytesToWrite;
          buffer.notifyAll();
        }
      }
    }

    @Override public void flush() throws IOException {
      synchronized (buffer) {
        if (sinkClosed) throw new IllegalStateException("closed");
        if (sourceClosed && buffer.size() > 0) throw new IOException("source is closed");
      }
    }

    @Override public void close() throws IOException {
      synchronized (buffer) {
        if (sinkClosed) return;
        if (sourceClosed && buffer.size() > 0) throw new IOException("source is closed");
        sinkClosed = true;
        buffer.notifyAll();
      }
    }

    @Override public Timeoutzaq timeout() {
      return timeout;
    }
  }

  final class PipeSource implements Sourcezaq {
    final Timeoutzaq timeout = new Timeoutzaq();

    @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
      synchronized (buffer) {
        if (sourceClosed) throw new IllegalStateException("closed");

        while (buffer.size() == 0) {
          if (sinkClosed) return -1L;
          timeout.waitUntilNotified(buffer);
        }

        long result = buffer.read(sink, byteCount);
        buffer.notifyAll();
        return result;
      }
    }

    @Override public void close() throws IOException {
      synchronized (buffer) {
        sourceClosed = true;
        buffer.notifyAll();
      }
    }

    @Override public Timeoutzaq timeout() {
      return timeout;
    }
  }
}
