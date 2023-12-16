
package com.xxx.zzz.aall.okioss;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


public final class InflaterSourcezaq implements Sourcezaq {
  private final BufferedSourcezaqdfs source;
  private final Inflater inflater;

  
  private int bufferBytesHeldByInflater;
  private boolean closed;

  public InflaterSourcezaq(Sourcezaq source, Inflater inflater) {
    this(Okiozaq.buffer(source), inflater);
  }

  
  InflaterSourcezaq(BufferedSourcezaqdfs source, Inflater inflater) {
    if (source == null) throw new IllegalArgumentException("source == null");
    if (inflater == null) throw new IllegalArgumentException("inflater == null");
    this.source = source;
    this.inflater = inflater;
  }

  @Override public long read(
          Bufferzaq sink, long byteCount) throws IOException {
    if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (closed) throw new IllegalStateException("closed");
    if (byteCount == 0) return 0;

    while (true) {
      boolean sourceExhausted = refill();


      try {
        Segmentzaq tail = sink.writableSegment(1);
        int bytesInflated = inflater.inflate(tail.data, tail.limit, Segmentzaq.SIZE - tail.limit);
        if (bytesInflated > 0) {
          tail.limit += bytesInflated;
          sink.size += bytesInflated;
          return bytesInflated;
        }
        if (inflater.finished() || inflater.needsDictionary()) {
          releaseInflatedBytes();
          if (tail.pos == tail.limit) {

            sink.head = tail.pop();
            SegmentPoolzaq.recycle(tail);
          }
          return -1;
        }
        if (sourceExhausted) throw new EOFException("source exhausted prematurely");
      } catch (DataFormatException e) {
        throw new IOException(e);
      }
    }
  }

  
  public boolean refill() throws IOException {
    if (!inflater.needsInput()) return false;

    releaseInflatedBytes();
    if (inflater.getRemaining() != 0) throw new IllegalStateException("?");


    if (source.exhausted()) return true;


    Segmentzaq head = source.buffer().head;
    bufferBytesHeldByInflater = head.limit - head.pos;
    inflater.setInput(head.data, head.pos, bufferBytesHeldByInflater);
    return false;
  }

  
  private void releaseInflatedBytes() throws IOException {
    if (bufferBytesHeldByInflater == 0) return;
    int toRelease = bufferBytesHeldByInflater - inflater.getRemaining();
    bufferBytesHeldByInflater -= toRelease;
    source.skip(toRelease);
  }

  @Override public Timeoutzaq timeout() {
    return source.timeout();
  }

  @Override public void close() throws IOException {
    if (closed) return;
    inflater.end();
    closed = true;
    source.close();
  }
}
