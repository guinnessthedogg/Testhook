
package com.xxx.zzz.aall.okioss;

import static com.xxx.zzz.aall.okioss.Utilzaqq.checkOffsetAndCount;

import com.xxx.zzz.aall.orgsss.animal_sniffer.IgnoreJRERequirement;

import java.io.IOException;
import java.util.zip.Deflater;


public final class DeflaterSinkzaq implements Sinkzaq {
  private final BufferedSinkzaqds sink;
  private final Deflater deflater;
  private boolean closed;

  public DeflaterSinkzaq(Sinkzaq sink, Deflater deflater) {
    this(Okiozaq.buffer(sink), deflater);
  }

  
  DeflaterSinkzaq(BufferedSinkzaqds sink, Deflater deflater) {
    if (sink == null) throw new IllegalArgumentException("source == null");
    if (deflater == null) throw new IllegalArgumentException("inflater == null");
    this.sink = sink;
    this.deflater = deflater;
  }

  @Override public void write(Bufferzaq source, long byteCount) throws IOException {
    checkOffsetAndCount(source.size, 0, byteCount);
    while (byteCount > 0) {

      Segmentzaq head = source.head;
      int toDeflate = (int) Math.min(byteCount, head.limit - head.pos);
      deflater.setInput(head.data, head.pos, toDeflate);


      deflate(false);


      source.size -= toDeflate;
      head.pos += toDeflate;
      if (head.pos == head.limit) {
        source.head = head.pop();
        SegmentPoolzaq.recycle(head);
      }

      byteCount -= toDeflate;
    }
  }

  @IgnoreJRERequirement
  private void deflate(boolean syncFlush) throws IOException {
    Bufferzaq buffer = sink.buffer();
    while (true) {
      Segmentzaq s = buffer.writableSegment(1);





      int deflated = syncFlush
          ? deflater.deflate(s.data, s.limit, Segmentzaq.SIZE - s.limit, Deflater.SYNC_FLUSH)
          : deflater.deflate(s.data, s.limit, Segmentzaq.SIZE - s.limit);

      if (deflated > 0) {
        s.limit += deflated;
        buffer.size += deflated;
        sink.emitCompleteSegments();
      } else if (deflater.needsInput()) {
        if (s.pos == s.limit) {

          buffer.head = s.pop();
          SegmentPoolzaq.recycle(s);
        }
        return;
      }
    }
  }

  @Override public void flush() throws IOException {
    deflate(true);
    sink.flush();
  }

  void finishDeflate() throws IOException {
    deflater.finish();
    deflate(false);
  }

  @Override public void close() throws IOException {
    if (closed) return;



    Throwable thrown = null;
    try {
      finishDeflate();
    } catch (Throwable e) {
      thrown = e;
    }

    try {
      deflater.end();
    } catch (Throwable e) {
      if (thrown == null) thrown = e;
    }

    try {
      sink.close();
    } catch (Throwable e) {
      if (thrown == null) thrown = e;
    }
    closed = true;

    if (thrown != null) Utilzaqq.sneakyRethrow(thrown);
  }

  @Override public Timeoutzaq timeout() {
    return sink.timeout();
  }

  @Override public String toString() {
    return "DeflaterSink(" + sink + ")";
  }
}
