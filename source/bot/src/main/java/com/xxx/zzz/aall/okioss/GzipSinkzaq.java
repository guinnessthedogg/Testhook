
package com.xxx.zzz.aall.okioss;

import static java.util.zip.Deflater.DEFAULT_COMPRESSION;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;


public final class GzipSinkzaq implements Sinkzaq {

  private final BufferedSinkzaqds sink;


  private final Deflater deflater;


  private final DeflaterSinkzaq deflaterSink;

  private boolean closed;


  private final CRC32 crc = new CRC32();

  public GzipSinkzaq(Sinkzaq sink) {
    if (sink == null) throw new IllegalArgumentException("sink == null");
    this.deflater = new Deflater(DEFAULT_COMPRESSION, true );
    this.sink = Okiozaq.buffer(sink);
    this.deflaterSink = new DeflaterSinkzaq(this.sink, deflater);

    writeHeader();
  }

  @Override public void write(Bufferzaq source, long byteCount) throws IOException {
    if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (byteCount == 0) return;

    updateCrc(source, byteCount);
    deflaterSink.write(source, byteCount);
  }

  @Override public void flush() throws IOException {
    deflaterSink.flush();
  }

  @Override public Timeoutzaq timeout() {
    return sink.timeout();
  }

  @Override public void close() throws IOException {
    if (closed) return;






    Throwable thrown = null;
    try {
      deflaterSink.finishDeflate();
      writeFooter();
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


  public Deflater deflater() {
    return deflater;
  }

  private void writeHeader() {

    Bufferzaq buffer = this.sink.buffer();
    buffer.writeShort(0x1f8b);
    buffer.writeByte(0x08);
    buffer.writeByte(0x00);
    buffer.writeInt(0x00);
    buffer.writeByte(0x00);
    buffer.writeByte(0x00);
  }

  private void writeFooter() throws IOException {
    sink.writeIntLe((int) crc.getValue());
    sink.writeIntLe((int) deflater.getBytesRead());
  }


  private void updateCrc(Bufferzaq buffer, long byteCount) {
    for (Segmentzaq head = buffer.head; byteCount > 0; head = head.next) {
      int segmentLength = (int) Math.min(byteCount, head.limit - head.pos);
      crc.update(head.data, head.pos, segmentLength);
      byteCount -= segmentLength;
    }
  }
}
