
package com.xxx.zzz.aall.okioss;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

final class RealBufferedSinkzaqfd implements BufferedSinkzaqds {
  public final Bufferzaq buffer = new Bufferzaq();
  public final Sinkzaq sink;
  boolean closed;

  RealBufferedSinkzaqfd(Sinkzaq sink) {
    if (sink == null) throw new NullPointerException("sink == null");
    this.sink = sink;
  }

  @Override public Bufferzaq buffer() {
    return buffer;
  }

  @Override public void write(Bufferzaq source, long byteCount)
      throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(source, byteCount);
    emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds write(ByteStringzaq byteString) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(byteString);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeUtf8(String string) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeUtf8(string);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeUtf8(String string, int beginIndex, int endIndex)
      throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeUtf8(string, beginIndex, endIndex);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeUtf8CodePoint(int codePoint) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeUtf8CodePoint(codePoint);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeString(String string, Charset charset) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeString(string, charset);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeString(String string, int beginIndex, int endIndex,
                                                 Charset charset) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeString(string, beginIndex, endIndex, charset);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds write(byte[] source) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(source);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds write(byte[] source, int offset, int byteCount) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(source, offset, byteCount);
    return emitCompleteSegments();
  }

  @Override public long writeAll(Sourcezaq source) throws IOException {
    if (source == null) throw new IllegalArgumentException("source == null");
    long totalBytesRead = 0;
    for (long readCount; (readCount = source.read(buffer, Segmentzaq.SIZE)) != -1; ) {
      totalBytesRead += readCount;
      emitCompleteSegments();
    }
    return totalBytesRead;
  }

  @Override public BufferedSinkzaqds write(Sourcezaq source, long byteCount) throws IOException {
    while (byteCount > 0) {
      long read = source.read(buffer, byteCount);
      if (read == -1) throw new EOFException();
      byteCount -= read;
      emitCompleteSegments();
    }
    return this;
  }

  @Override public BufferedSinkzaqds writeByte(int b) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeByte(b);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeShort(int s) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeShort(s);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeShortLe(int s) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeShortLe(s);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeInt(int i) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeInt(i);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeIntLe(int i) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeIntLe(i);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeLong(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeLong(v);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeLongLe(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeLongLe(v);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeDecimalLong(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeDecimalLong(v);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds writeHexadecimalUnsignedLong(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeHexadecimalUnsignedLong(v);
    return emitCompleteSegments();
  }

  @Override public BufferedSinkzaqds emitCompleteSegments() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    long byteCount = buffer.completeSegmentByteCount();
    if (byteCount > 0) sink.write(buffer, byteCount);
    return this;
  }

  @Override public BufferedSinkzaqds emit() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    long byteCount = buffer.size();
    if (byteCount > 0) sink.write(buffer, byteCount);
    return this;
  }

  @Override public OutputStream outputStream() {
    return new OutputStream() {
      @Override public void write(int b) throws IOException {
        if (closed) throw new IOException("closed");
        buffer.writeByte((byte) b);
        emitCompleteSegments();
      }

      @Override public void write(byte[] data, int offset, int byteCount) throws IOException {
        if (closed) throw new IOException("closed");
        buffer.write(data, offset, byteCount);
        emitCompleteSegments();
      }

      @Override public void flush() throws IOException {

        if (!closed) {
          RealBufferedSinkzaqfd.this.flush();
        }
      }

      @Override public void close() throws IOException {
        RealBufferedSinkzaqfd.this.close();
      }

      @Override public String toString() {
        return RealBufferedSinkzaqfd.this + ".outputStream()";
      }
    };
  }

  @Override public void flush() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    if (buffer.size > 0) {
      sink.write(buffer, buffer.size);
    }
    sink.flush();
  }

  @Override public void close() throws IOException {
    if (closed) return;



    Throwable thrown = null;
    try {
      if (buffer.size > 0) {
        sink.write(buffer, buffer.size);
      }
    } catch (Throwable e) {
      thrown = e;
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
    return "buffer(" + sink + ")";
  }
}
