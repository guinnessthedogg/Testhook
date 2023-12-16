
package com.xxx.zzz.aall.okioss;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


final class SegmentPoolzaq {


  static final long MAX_SIZE = 64 * 1024;


  static @Nullableq
  Segmentzaq next;


  static long byteCount;

  private SegmentPoolzaq() {
  }

  static Segmentzaq take() {
    synchronized (SegmentPoolzaq.class) {
      if (next != null) {
        Segmentzaq result = next;
        next = result.next;
        result.next = null;
        byteCount -= Segmentzaq.SIZE;
        return result;
      }
    }
    return new Segmentzaq();
  }

  static void recycle(Segmentzaq segment) {
    if (segment.next != null || segment.prev != null) throw new IllegalArgumentException();
    if (segment.shared) return;
    synchronized (SegmentPoolzaq.class) {
      if (byteCount + Segmentzaq.SIZE > MAX_SIZE) return;
      byteCount += Segmentzaq.SIZE;
      segment.next = next;
      segment.pos = segment.limit = 0;
      next = segment;
    }
  }
}
