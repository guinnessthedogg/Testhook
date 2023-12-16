
package com.xxx.zzz.aall.okioss;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


final class Segmentzaq {
  
  static final int SIZE = 8192;

  
  static final int SHARE_MINIMUM = 1024;

  final byte[] data;

  
  int pos;

  
  int limit;

  
  boolean shared;

  
  boolean owner;

  
  Segmentzaq next;

  
  Segmentzaq prev;

  Segmentzaq() {
    this.data = new byte[SIZE];
    this.owner = true;
    this.shared = false;
  }

  Segmentzaq(Segmentzaq shareFrom) {
    this(shareFrom.data, shareFrom.pos, shareFrom.limit);
    shareFrom.shared = true;
  }

  Segmentzaq(byte[] data, int pos, int limit) {
    this.data = data;
    this.pos = pos;
    this.limit = limit;
    this.owner = false;
    this.shared = true;
  }

  
  public @Nullableq
  Segmentzaq pop() {
    Segmentzaq result = next != this ? next : null;
    prev.next = next;
    next.prev = prev;
    next = null;
    prev = null;
    return result;
  }

  
  public Segmentzaq push(Segmentzaq segment) {
    segment.prev = this;
    segment.next = next;
    next.prev = segment;
    next = segment;
    return segment;
  }

  
  public Segmentzaq split(int byteCount) {
    if (byteCount <= 0 || byteCount > limit - pos) throw new IllegalArgumentException();
    Segmentzaq prefix;






    if (byteCount >= SHARE_MINIMUM) {
      prefix = new Segmentzaq(this);
    } else {
      prefix = SegmentPoolzaq.take();
      System.arraycopy(data, pos, prefix.data, 0, byteCount);
    }

    prefix.limit = prefix.pos + byteCount;
    pos += byteCount;
    prev.push(prefix);
    return prefix;
  }

  
  public void compact() {
    if (prev == this) throw new IllegalStateException();
    if (!prev.owner) return;
    int byteCount = limit - pos;
    int availableByteCount = SIZE - prev.limit + (prev.shared ? 0 : prev.pos);
    if (byteCount > availableByteCount) return;
    writeTo(prev, byteCount);
    pop();
    SegmentPoolzaq.recycle(this);
  }

  
  public void writeTo(Segmentzaq sink, int byteCount) {
    if (!sink.owner) throw new IllegalArgumentException();
    if (sink.limit + byteCount > SIZE) {

      if (sink.shared) throw new IllegalArgumentException();
      if (sink.limit + byteCount - sink.pos > SIZE) throw new IllegalArgumentException();
      System.arraycopy(sink.data, sink.pos, sink.data, 0, sink.limit - sink.pos);
      sink.limit -= sink.pos;
      sink.pos = 0;
    }

    System.arraycopy(data, pos, sink.data, sink.limit, byteCount);
    sink.limit += byteCount;
    pos += byteCount;
  }
}
