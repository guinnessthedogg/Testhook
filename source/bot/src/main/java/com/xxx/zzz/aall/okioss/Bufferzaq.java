
package com.xxx.zzz.aall.okioss;

import static com.xxx.zzz.aall.okioss.Utilzaqq.checkOffsetAndCount;
import static com.xxx.zzz.aall.okioss.Utilzaqq.reverseBytesLong;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public final class Bufferzaq implements BufferedSourcezaqdfs, BufferedSinkzaqds, Cloneable {
  private static final byte[] DIGITS =
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  static final int REPLACEMENT_CHARACTER = '\ufffd';

  @Nullableq
  Segmentzaq head;
  long size;

  public Bufferzaq() {
  }

  
  public long size() {
    return size;
  }

  @Override public Bufferzaq buffer() {
    return this;
  }

  @Override public OutputStream outputStream() {
    return new OutputStream() {
      @Override public void write(int b) {
        writeByte((byte) b);
      }

      @Override public void write(byte[] data, int offset, int byteCount) {
        Bufferzaq.this.write(data, offset, byteCount);
      }

      @Override public void flush() {
      }

      @Override public void close() {
      }

      @Override public String toString() {
        return Bufferzaq.this + ".outputStream()";
      }
    };
  }

  @Override public Bufferzaq emitCompleteSegments() {
    return this;
  }

  @Override public BufferedSinkzaqds emit() {
    return this;
  }

  @Override public boolean exhausted() {
    return size == 0;
  }

  @Override public void require(long byteCount) throws EOFException {
    if (size < byteCount) throw new EOFException();
  }

  @Override public boolean request(long byteCount) {
    return size >= byteCount;
  }

  @Override public InputStream inputStream() {
    return new InputStream() {
      @Override public int read() {
        if (size > 0) return readByte() & 0xff;
        return -1;
      }

      @Override public int read(byte[] sink, int offset, int byteCount) {
        return Bufferzaq.this.read(sink, offset, byteCount);
      }

      @Override public int available() {
        return (int) Math.min(size, Integer.MAX_VALUE);
      }

      @Override public void close() {
      }

      @Override public String toString() {
        return Bufferzaq.this + ".inputStream()";
      }
    };
  }

  
  public Bufferzaq copyTo(OutputStream out) throws IOException {
    return copyTo(out, 0, size);
  }

  
  public Bufferzaq copyTo(OutputStream out, long offset, long byteCount) throws IOException {
    if (out == null) throw new IllegalArgumentException("out == null");
    checkOffsetAndCount(size, offset, byteCount);
    if (byteCount == 0) return this;


    Segmentzaq s = head;
    for (; offset >= (s.limit - s.pos); s = s.next) {
      offset -= (s.limit - s.pos);
    }


    for (; byteCount > 0; s = s.next) {
      int pos = (int) (s.pos + offset);
      int toCopy = (int) Math.min(s.limit - pos, byteCount);
      out.write(s.data, pos, toCopy);
      byteCount -= toCopy;
      offset = 0;
    }

    return this;
  }

  
  public Bufferzaq copyTo(Bufferzaq out, long offset, long byteCount) {
    if (out == null) throw new IllegalArgumentException("out == null");
    checkOffsetAndCount(size, offset, byteCount);
    if (byteCount == 0) return this;

    out.size += byteCount;


    Segmentzaq s = head;
    for (; offset >= (s.limit - s.pos); s = s.next) {
      offset -= (s.limit - s.pos);
    }


    for (; byteCount > 0; s = s.next) {
      Segmentzaq copy = new Segmentzaq(s);
      copy.pos += offset;
      copy.limit = Math.min(copy.pos + (int) byteCount, copy.limit);
      if (out.head == null) {
        out.head = copy.next = copy.prev = copy;
      } else {
        out.head.prev.push(copy);
      }
      byteCount -= copy.limit - copy.pos;
      offset = 0;
    }

    return this;
  }

  
  public Bufferzaq writeTo(OutputStream out) throws IOException {
    return writeTo(out, size);
  }

  
  public Bufferzaq writeTo(OutputStream out, long byteCount) throws IOException {
    if (out == null) throw new IllegalArgumentException("out == null");
    checkOffsetAndCount(size, 0, byteCount);

    Segmentzaq s = head;
    while (byteCount > 0) {
      int toCopy = (int) Math.min(byteCount, s.limit - s.pos);
      out.write(s.data, s.pos, toCopy);

      s.pos += toCopy;
      size -= toCopy;
      byteCount -= toCopy;

      if (s.pos == s.limit) {
        Segmentzaq toRecycle = s;
        head = s = toRecycle.pop();
        SegmentPoolzaq.recycle(toRecycle);
      }
    }

    return this;
  }

  
  public Bufferzaq readFrom(InputStream in) throws IOException {
    readFrom(in, Long.MAX_VALUE, true);
    return this;
  }

  
  public Bufferzaq readFrom(InputStream in, long byteCount) throws IOException {
    if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    readFrom(in, byteCount, false);
    return this;
  }

  private void readFrom(InputStream in, long byteCount, boolean forever) throws IOException {
    if (in == null) throw new IllegalArgumentException("in == null");
    while (byteCount > 0 || forever) {
      Segmentzaq tail = writableSegment(1);
      int maxToCopy = (int) Math.min(byteCount, Segmentzaq.SIZE - tail.limit);
      int bytesRead = in.read(tail.data, tail.limit, maxToCopy);
      if (bytesRead == -1) {
        if (forever) return;
        throw new EOFException();
      }
      tail.limit += bytesRead;
      size += bytesRead;
      byteCount -= bytesRead;
    }
  }

  
  public long completeSegmentByteCount() {
    long result = size;
    if (result == 0) return 0;


    Segmentzaq tail = head.prev;
    if (tail.limit < Segmentzaq.SIZE && tail.owner) {
      result -= tail.limit - tail.pos;
    }

    return result;
  }

  @Override public byte readByte() {
    if (size == 0) throw new IllegalStateException("size == 0");

    Segmentzaq segment = head;
    int pos = segment.pos;
    int limit = segment.limit;

    byte[] data = segment.data;
    byte b = data[pos++];
    size -= 1;

    if (pos == limit) {
      head = segment.pop();
      SegmentPoolzaq.recycle(segment);
    } else {
      segment.pos = pos;
    }

    return b;
  }

  
  public byte getByte(long pos) {
    checkOffsetAndCount(size, pos, 1);
    for (Segmentzaq s = head; true; s = s.next) {
      int segmentByteCount = s.limit - s.pos;
      if (pos < segmentByteCount) return s.data[s.pos + (int) pos];
      pos -= segmentByteCount;
    }
  }

  @Override public short readShort() {
    if (size < 2) throw new IllegalStateException("size < 2: " + size);

    Segmentzaq segment = head;
    int pos = segment.pos;
    int limit = segment.limit;


    if (limit - pos < 2) {
      int s = (readByte() & 0xff) << 8
          |   (readByte() & 0xff);
      return (short) s;
    }

    byte[] data = segment.data;
    int s = (data[pos++] & 0xff) << 8
        |   (data[pos++] & 0xff);
    size -= 2;

    if (pos == limit) {
      head = segment.pop();
      SegmentPoolzaq.recycle(segment);
    } else {
      segment.pos = pos;
    }

    return (short) s;
  }

  @Override public int readInt() {
    if (size < 4) throw new IllegalStateException("size < 4: " + size);

    Segmentzaq segment = head;
    int pos = segment.pos;
    int limit = segment.limit;


    if (limit - pos < 4) {
      return (readByte() & 0xff) << 24
          |  (readByte() & 0xff) << 16
          |  (readByte() & 0xff) <<  8
          |  (readByte() & 0xff);
    }

    byte[] data = segment.data;
    int i = (data[pos++] & 0xff) << 24
        |   (data[pos++] & 0xff) << 16
        |   (data[pos++] & 0xff) <<  8
        |   (data[pos++] & 0xff);
    size -= 4;

    if (pos == limit) {
      head = segment.pop();
      SegmentPoolzaq.recycle(segment);
    } else {
      segment.pos = pos;
    }

    return i;
  }

  @Override public long readLong() {
    if (size < 8) throw new IllegalStateException("size < 8: " + size);

    Segmentzaq segment = head;
    int pos = segment.pos;
    int limit = segment.limit;


    if (limit - pos < 8) {
      return (readInt() & 0xffffffffL) << 32
          |  (readInt() & 0xffffffffL);
    }

    byte[] data = segment.data;
    long v = (data[pos++] & 0xffL) << 56
        |    (data[pos++] & 0xffL) << 48
        |    (data[pos++] & 0xffL) << 40
        |    (data[pos++] & 0xffL) << 32
        |    (data[pos++] & 0xffL) << 24
        |    (data[pos++] & 0xffL) << 16
        |    (data[pos++] & 0xffL) <<  8
        |    (data[pos++] & 0xffL);
    size -= 8;

    if (pos == limit) {
      head = segment.pop();
      SegmentPoolzaq.recycle(segment);
    } else {
      segment.pos = pos;
    }

    return v;
  }

  @Override public short readShortLe() {
    return Utilzaqq.reverseBytesShort(readShort());
  }

  @Override public int readIntLe() {
    return Utilzaqq.reverseBytesInt(readInt());
  }

  @Override public long readLongLe() {
    return Utilzaqq.reverseBytesLong(readLong());
  }

  @Override public long readDecimalLong() {
    if (size == 0) throw new IllegalStateException("size == 0");


    long value = 0;
    int seen = 0;
    boolean negative = false;
    boolean done = false;

    long overflowZone = Long.MIN_VALUE / 10;
    long overflowDigit = (Long.MIN_VALUE % 10) + 1;

    do {
      Segmentzaq segment = head;

      byte[] data = segment.data;
      int pos = segment.pos;
      int limit = segment.limit;

      for (; pos < limit; pos++, seen++) {
        byte b = data[pos];
        if (b >= '0' && b <= '9') {
          int digit = '0' - b;


          if (value < overflowZone || value == overflowZone && digit < overflowDigit) {
            Bufferzaq buffer = new Bufferzaq().writeDecimalLong(value).writeByte(b);
            if (!negative) buffer.readByte();
            throw new NumberFormatException("Number too large: " + buffer.readUtf8());
          }
          value *= 10;
          value += digit;
        } else if (b == '-' && seen == 0) {
          negative = true;
          overflowDigit -= 1;
        } else {
          if (seen == 0) {
            throw new NumberFormatException(
                "Expected leading [0-9] or '-' character but was 0x" + Integer.toHexString(b));
          }

          done = true;
          break;
        }
      }

      if (pos == limit) {
        head = segment.pop();
        SegmentPoolzaq.recycle(segment);
      } else {
        segment.pos = pos;
      }
    } while (!done && head != null);

    size -= seen;
    return negative ? value : -value;
  }

  @Override public long readHexadecimalUnsignedLong() {
    if (size == 0) throw new IllegalStateException("size == 0");

    long value = 0;
    int seen = 0;
    boolean done = false;

    do {
      Segmentzaq segment = head;

      byte[] data = segment.data;
      int pos = segment.pos;
      int limit = segment.limit;

      for (; pos < limit; pos++, seen++) {
        int digit;

        byte b = data[pos];
        if (b >= '0' && b <= '9') {
          digit = b - '0';
        } else if (b >= 'a' && b <= 'f') {
          digit = b - 'a' + 10;
        } else if (b >= 'A' && b <= 'F') {
          digit = b - 'A' + 10;
        } else {
          if (seen == 0) {
            throw new NumberFormatException(
                "Expected leading [0-9a-fA-F] character but was 0x" + Integer.toHexString(b));
          }

          done = true;
          break;
        }


        if ((value & 0xf000000000000000L) != 0) {
          Bufferzaq buffer = new Bufferzaq().writeHexadecimalUnsignedLong(value).writeByte(b);
          throw new NumberFormatException("Number too large: " + buffer.readUtf8());
        }

        value <<= 4;
        value |= digit;
      }

      if (pos == limit) {
        head = segment.pop();
        SegmentPoolzaq.recycle(segment);
      } else {
        segment.pos = pos;
      }
    } while (!done && head != null);

    size -= seen;
    return value;
  }

  @Override public ByteStringzaq readByteString() {
    return new ByteStringzaq(readByteArray());
  }

  @Override public ByteStringzaq readByteString(long byteCount) throws EOFException {
    return new ByteStringzaq(readByteArray(byteCount));
  }

  @Override public int select(Ofdsptionszaq options) {
    Segmentzaq s = head;
    if (s == null) return options.indexOf(ByteStringzaq.EMPTY);

    ByteStringzaq[] byteStrings = options.byteStrings;
    for (int i = 0, listSize = byteStrings.length; i < listSize; i++) {
      ByteStringzaq b = byteStrings[i];
      if (size >= b.size() && rangeEquals(s, s.pos, b, 0, b.size())) {
        try {
          skip(b.size());
          return i;
        } catch (EOFException e) {
          throw new AssertionError(e);
        }
      }
    }
    return -1;
  }

  
  int selectPrefix(Ofdsptionszaq options) {
    Segmentzaq s = head;
    ByteStringzaq[] byteStrings = options.byteStrings;
    for (int i = 0, listSize = byteStrings.length; i < listSize; i++) {
      ByteStringzaq b = byteStrings[i];
      int bytesLimit = (int) Math.min(size, b.size());
      if (bytesLimit == 0 || rangeEquals(s, s.pos, b, 0, bytesLimit)) {
        return i;
      }
    }
    return -1;
  }

  @Override public void readFully(Bufferzaq sink, long byteCount) throws EOFException {
    if (size < byteCount) {
      sink.write(this, size);
      throw new EOFException();
    }
    sink.write(this, byteCount);
  }

  @Override public long readAll(Sinkzaq sink) throws IOException {
    long byteCount = size;
    if (byteCount > 0) {
      sink.write(this, byteCount);
    }
    return byteCount;
  }

  @Override public String readUtf8() {
    try {
      return readString(size, Utilzaqq.UTF_8);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }

  @Override public String readUtf8(long byteCount) throws EOFException {
    return readString(byteCount, Utilzaqq.UTF_8);
  }

  @Override public String readString(Charset charset) {
    try {
      return readString(size, charset);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }

  @Override public String readString(long byteCount, Charset charset) throws EOFException {
    checkOffsetAndCount(size, 0, byteCount);
    if (charset == null) throw new IllegalArgumentException("charset == null");
    if (byteCount > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
    }
    if (byteCount == 0) return "";

    Segmentzaq s = head;
    if (s.pos + byteCount > s.limit) {

      return new String(readByteArray(byteCount), charset);
    }

    String result = new String(s.data, s.pos, (int) byteCount, charset);
    s.pos += byteCount;
    size -= byteCount;

    if (s.pos == s.limit) {
      head = s.pop();
      SegmentPoolzaq.recycle(s);
    }

    return result;
  }

  @Override public @Nullableq
  String readUtf8Line() throws EOFException {
    long newline = indexOf((byte) '\n');

    if (newline == -1) {
      return size != 0 ? readUtf8(size) : null;
    }

    return readUtf8Line(newline);
  }

  @Override public String readUtf8LineStrict() throws EOFException {
    return readUtf8LineStrict(Long.MAX_VALUE);
  }

  @Override public String readUtf8LineStrict(long limit) throws EOFException {
    if (limit < 0) throw new IllegalArgumentException("limit < 0: " + limit);
    long scanLength = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1;
    long newline = indexOf((byte) '\n', 0, scanLength);
    if (newline != -1) return readUtf8Line(newline);
    if (scanLength < size()
        && getByte(scanLength - 1) == '\r' && getByte(scanLength) == '\n') {
      return readUtf8Line(scanLength);
    }
    Bufferzaq data = new Bufferzaq();
    copyTo(data, 0, Math.min(32, size()));
    throw new EOFException("\\n not found: limit=" + Math.min(size(), limit)
        + " content=" + data.readByteString().hex() + '…');
  }

  String readUtf8Line(long newline) throws EOFException {
    if (newline > 0 && getByte(newline - 1) == '\r') {

      String result = readUtf8((newline - 1));
      skip(2);
      return result;

    } else {

      String result = readUtf8(newline);
      skip(1);
      return result;
    }
  }

  @Override public int readUtf8CodePoint() throws EOFException {
    if (size == 0) throw new EOFException();

    byte b0 = getByte(0);
    int codePoint;
    int byteCount;
    int min;

    if ((b0 & 0x80) == 0) {

      codePoint = b0 & 0x7f;
      byteCount = 1;
      min = 0x0;

    } else if ((b0 & 0xe0) == 0xc0) {

      codePoint = b0 & 0x1f;
      byteCount = 2;
      min = 0x80;

    } else if ((b0 & 0xf0) == 0xe0) {

      codePoint = b0 & 0x0f;
      byteCount = 3;
      min = 0x800;

    } else if ((b0 & 0xf8) == 0xf0) {

      codePoint = b0 & 0x07;
      byteCount = 4;
      min = 0x10000;

    } else {

      skip(1);
      return REPLACEMENT_CHARACTER;
    }

    if (size < byteCount) {
      throw new EOFException("size < " + byteCount + ": " + size
          + " (to read code point prefixed 0x" + Integer.toHexString(b0) + ")");
    }




    for (int i = 1; i < byteCount; i++) {
      byte b = getByte(i);
      if ((b & 0xc0) == 0x80) {

        codePoint <<= 6;
        codePoint |= b & 0x3f;
      } else {
        skip(i);
        return REPLACEMENT_CHARACTER;
      }
    }

    skip(byteCount);

    if (codePoint > 0x10ffff) {
      return REPLACEMENT_CHARACTER;
    }

    if (codePoint >= 0xd800 && codePoint <= 0xdfff) {
      return REPLACEMENT_CHARACTER;
    }

    if (codePoint < min) {
      return REPLACEMENT_CHARACTER;
    }

    return codePoint;
  }

  @Override public byte[] readByteArray() {
    try {
      return readByteArray(size);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }

  @Override public byte[] readByteArray(long byteCount) throws EOFException {
    checkOffsetAndCount(size, 0, byteCount);
    if (byteCount > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
    }

    byte[] result = new byte[(int) byteCount];
    readFully(result);
    return result;
  }

  @Override public int read(byte[] sink) {
    return read(sink, 0, sink.length);
  }

  @Override public void readFully(byte[] sink) throws EOFException {
    int offset = 0;
    while (offset < sink.length) {
      int read = read(sink, offset, sink.length - offset);
      if (read == -1) throw new EOFException();
      offset += read;
    }
  }

  @Override public int read(byte[] sink, int offset, int byteCount) {
    checkOffsetAndCount(sink.length, offset, byteCount);

    Segmentzaq s = head;
    if (s == null) return -1;
    int toCopy = Math.min(byteCount, s.limit - s.pos);
    System.arraycopy(s.data, s.pos, sink, offset, toCopy);

    s.pos += toCopy;
    size -= toCopy;

    if (s.pos == s.limit) {
      head = s.pop();
      SegmentPoolzaq.recycle(s);
    }

    return toCopy;
  }

  
  public void clear() {
    try {
      skip(size);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }

  
  @Override public void skip(long byteCount) throws EOFException {
    while (byteCount > 0) {
      if (head == null) throw new EOFException();

      int toSkip = (int) Math.min(byteCount, head.limit - head.pos);
      size -= toSkip;
      byteCount -= toSkip;
      head.pos += toSkip;

      if (head.pos == head.limit) {
        Segmentzaq toRecycle = head;
        head = toRecycle.pop();
        SegmentPoolzaq.recycle(toRecycle);
      }
    }
  }

  @Override public Bufferzaq write(ByteStringzaq byteString) {
    if (byteString == null) throw new IllegalArgumentException("byteString == null");
    byteString.write(this);
    return this;
  }

  @Override public Bufferzaq writeUtf8(String string) {
    return writeUtf8(string, 0, string.length());
  }

  @Override public Bufferzaq writeUtf8(String string, int beginIndex, int endIndex) {
    if (string == null) throw new IllegalArgumentException("string == null");
    if (beginIndex < 0) throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
    }
    if (endIndex > string.length()) {
      throw new IllegalArgumentException(
          "endIndex > string.length: " + endIndex + " > " + string.length());
    }


    for (int i = beginIndex; i < endIndex;) {
      int c = string.charAt(i);

      if (c < 0x80) {
        Segmentzaq tail = writableSegment(1);
        byte[] data = tail.data;
        int segmentOffset = tail.limit - i;
        int runLimit = Math.min(endIndex, Segmentzaq.SIZE - segmentOffset);


        data[segmentOffset + i++] = (byte) c;



        while (i < runLimit) {
          c = string.charAt(i);
          if (c >= 0x80) break;
          data[segmentOffset + i++] = (byte) c;
        }

        int runSize = i + segmentOffset - tail.limit;
        tail.limit += runSize;
        size += runSize;

      } else if (c < 0x800) {

        writeByte(c >>  6        | 0xc0);
        writeByte(c       & 0x3f | 0x80);
        i++;

      } else if (c < 0xd800 || c > 0xdfff) {

        writeByte(c >> 12        | 0xe0);
        writeByte(c >>  6 & 0x3f | 0x80);
        writeByte(c       & 0x3f | 0x80);
        i++;

      } else {


        int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
        if (c > 0xdbff || low < 0xdc00 || low > 0xdfff) {
          writeByte('?');
          i++;
          continue;
        }




        int codePoint = 0x010000 + ((c & ~0xd800) << 10 | low & ~0xdc00);


        writeByte(codePoint >> 18        | 0xf0);
        writeByte(codePoint >> 12 & 0x3f | 0x80);
        writeByte(codePoint >>  6 & 0x3f | 0x80);
        writeByte(codePoint       & 0x3f | 0x80);
        i += 2;
      }
    }

    return this;
  }

  @Override public Bufferzaq writeUtf8CodePoint(int codePoint) {
    if (codePoint < 0x80) {

      writeByte(codePoint);

    } else if (codePoint < 0x800) {

      writeByte(codePoint >>  6        | 0xc0);
      writeByte(codePoint       & 0x3f | 0x80);

    } else if (codePoint < 0x10000) {
      if (codePoint >= 0xd800 && codePoint <= 0xdfff) {

        writeByte('?');
      } else {

        writeByte(codePoint >> 12        | 0xe0);
        writeByte(codePoint >>  6 & 0x3f | 0x80);
        writeByte(codePoint       & 0x3f | 0x80);
      }

    } else if (codePoint <= 0x10ffff) {

      writeByte(codePoint >> 18        | 0xf0);
      writeByte(codePoint >> 12 & 0x3f | 0x80);
      writeByte(codePoint >>  6 & 0x3f | 0x80);
      writeByte(codePoint       & 0x3f | 0x80);

    } else {
      throw new IllegalArgumentException(
          "Unexpected code point: " + Integer.toHexString(codePoint));
    }

    return this;
  }

  @Override public Bufferzaq writeString(String string, Charset charset) {
    return writeString(string, 0, string.length(), charset);
  }

  @Override
  public Bufferzaq writeString(String string, int beginIndex, int endIndex, Charset charset) {
    if (string == null) throw new IllegalArgumentException("string == null");
    if (beginIndex < 0) throw new IllegalAccessError("beginIndex < 0: " + beginIndex);
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
    }
    if (endIndex > string.length()) {
      throw new IllegalArgumentException(
          "endIndex > string.length: " + endIndex + " > " + string.length());
    }
    if (charset == null) throw new IllegalArgumentException("charset == null");
    if (charset.equals(Utilzaqq.UTF_8)) return writeUtf8(string, beginIndex, endIndex);
    byte[] data = string.substring(beginIndex, endIndex).getBytes(charset);
    return write(data, 0, data.length);
  }

  @Override public Bufferzaq write(byte[] source) {
    if (source == null) throw new IllegalArgumentException("source == null");
    return write(source, 0, source.length);
  }

  @Override public Bufferzaq write(byte[] source, int offset, int byteCount) {
    if (source == null) throw new IllegalArgumentException("source == null");
    checkOffsetAndCount(source.length, offset, byteCount);

    int limit = offset + byteCount;
    while (offset < limit) {
      Segmentzaq tail = writableSegment(1);

      int toCopy = Math.min(limit - offset, Segmentzaq.SIZE - tail.limit);
      System.arraycopy(source, offset, tail.data, tail.limit, toCopy);

      offset += toCopy;
      tail.limit += toCopy;
    }

    size += byteCount;
    return this;
  }

  @Override public long writeAll(Sourcezaq source) throws IOException {
    if (source == null) throw new IllegalArgumentException("source == null");
    long totalBytesRead = 0;
    for (long readCount; (readCount = source.read(this, Segmentzaq.SIZE)) != -1; ) {
      totalBytesRead += readCount;
    }
    return totalBytesRead;
  }

  @Override public BufferedSinkzaqds write(Sourcezaq source, long byteCount) throws IOException {
    while (byteCount > 0) {
      long read = source.read(this, byteCount);
      if (read == -1) throw new EOFException();
      byteCount -= read;
    }
    return this;
  }

  @Override public Bufferzaq writeByte(int b) {
    Segmentzaq tail = writableSegment(1);
    tail.data[tail.limit++] = (byte) b;
    size += 1;
    return this;
  }

  @Override public Bufferzaq writeShort(int s) {
    Segmentzaq tail = writableSegment(2);
    byte[] data = tail.data;
    int limit = tail.limit;
    data[limit++] = (byte) ((s >>> 8) & 0xff);
    data[limit++] = (byte)  (s        & 0xff);
    tail.limit = limit;
    size += 2;
    return this;
  }

  @Override public Bufferzaq writeShortLe(int s) {
    return writeShort(Utilzaqq.reverseBytesShort((short) s));
  }

  @Override public Bufferzaq writeInt(int i) {
    Segmentzaq tail = writableSegment(4);
    byte[] data = tail.data;
    int limit = tail.limit;
    data[limit++] = (byte) ((i >>> 24) & 0xff);
    data[limit++] = (byte) ((i >>> 16) & 0xff);
    data[limit++] = (byte) ((i >>>  8) & 0xff);
    data[limit++] = (byte)  (i         & 0xff);
    tail.limit = limit;
    size += 4;
    return this;
  }

  @Override public Bufferzaq writeIntLe(int i) {
    return writeInt(Utilzaqq.reverseBytesInt(i));
  }

  @Override public Bufferzaq writeLong(long v) {
    Segmentzaq tail = writableSegment(8);
    byte[] data = tail.data;
    int limit = tail.limit;
    data[limit++] = (byte) ((v >>> 56L) & 0xff);
    data[limit++] = (byte) ((v >>> 48L) & 0xff);
    data[limit++] = (byte) ((v >>> 40L) & 0xff);
    data[limit++] = (byte) ((v >>> 32L) & 0xff);
    data[limit++] = (byte) ((v >>> 24L) & 0xff);
    data[limit++] = (byte) ((v >>> 16L) & 0xff);
    data[limit++] = (byte) ((v >>>  8L) & 0xff);
    data[limit++] = (byte)  (v          & 0xff);
    tail.limit = limit;
    size += 8;
    return this;
  }

  @Override public Bufferzaq writeLongLe(long v) {
    return writeLong(reverseBytesLong(v));
  }

  @Override public Bufferzaq writeDecimalLong(long v) {
    if (v == 0) {

      return writeByte('0');
    }

    boolean negative = false;
    if (v < 0) {
      v = -v;
      if (v < 0) {
        return writeUtf8("-9223372036854775808");
      }
      negative = true;
    }


    int width =
          v < 100000000L
        ? v < 10000L
        ? v < 100L
        ? v < 10L ? 1 : 2
        : v < 1000L ? 3 : 4
        : v < 1000000L
        ? v < 100000L ? 5 : 6
        : v < 10000000L ? 7 : 8
        : v < 1000000000000L
        ? v < 10000000000L
        ? v < 1000000000L ? 9 : 10
        : v < 100000000000L ? 11 : 12
        : v < 1000000000000000L
        ? v < 10000000000000L ? 13
        : v < 100000000000000L ? 14 : 15
        : v < 100000000000000000L
        ? v < 10000000000000000L ? 16 : 17
        : v < 1000000000000000000L ? 18 : 19;
    if (negative) {
      ++width;
    }

    Segmentzaq tail = writableSegment(width);
    byte[] data = tail.data;
    int pos = tail.limit + width;
    while (v != 0) {
      int digit = (int) (v % 10);
      data[--pos] = DIGITS[digit];
      v /= 10;
    }
    if (negative) {
      data[--pos] = '-';
    }

    tail.limit += width;
    this.size += width;
    return this;
  }

  @Override public Bufferzaq writeHexadecimalUnsignedLong(long v) {
    if (v == 0) {

      return writeByte('0');
    }

    int width = Long.numberOfTrailingZeros(Long.highestOneBit(v)) / 4 + 1;

    Segmentzaq tail = writableSegment(width);
    byte[] data = tail.data;
    for (int pos = tail.limit + width - 1, start = tail.limit; pos >= start; pos--) {
      data[pos] = DIGITS[(int) (v & 0xF)];
      v >>>= 4;
    }
    tail.limit += width;
    size += width;
    return this;
  }

  
  Segmentzaq writableSegment(int minimumCapacity) {
    if (minimumCapacity < 1 || minimumCapacity > Segmentzaq.SIZE) throw new IllegalArgumentException();

    if (head == null) {
      head = SegmentPoolzaq.take();
      return head.next = head.prev = head;
    }

    Segmentzaq tail = head.prev;
    if (tail.limit + minimumCapacity > Segmentzaq.SIZE || !tail.owner) {
      tail = tail.push(SegmentPoolzaq.take());
    }
    return tail;
  }

  @Override public void write(Bufferzaq source, long byteCount) {
    if (source == null) throw new IllegalArgumentException("source == null");
    if (source == this) throw new IllegalArgumentException("source == this");
    checkOffsetAndCount(source.size, 0, byteCount);

    while (byteCount > 0) {
      if (byteCount < (source.head.limit - source.head.pos)) {
        Segmentzaq tail = head != null ? head.prev : null;
        if (tail != null && tail.owner
            && (byteCount + tail.limit - (tail.shared ? 0 : tail.pos) <= Segmentzaq.SIZE)) {
          
          source.head.writeTo(tail, (int) byteCount);
          source.size -= byteCount;
          size += byteCount;
          return;
        } else {
          source.head = source.head.split((int) byteCount);
        }
      }

      Segmentzaq segmentToMove = source.head;
      long movedByteCount = segmentToMove.limit - segmentToMove.pos;
      source.head = segmentToMove.pop();
      if (head == null) {
        head = segmentToMove;
        head.next = head.prev = head;
      } else {
        Segmentzaq tail = head.prev;
        tail = tail.push(segmentToMove);
        tail.compact();
      }
      source.size -= movedByteCount;
      size += movedByteCount;
      byteCount -= movedByteCount;
    }
  }

  @Override public long read(Bufferzaq sink, long byteCount) {
    if (sink == null) throw new IllegalArgumentException("sink == null");
    if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (size == 0) return -1L;
    if (byteCount > size) byteCount = size;
    sink.write(this, byteCount);
    return byteCount;
  }

  @Override public long indexOf(byte b) {
    return indexOf(b, 0, Long.MAX_VALUE);
  }

  
  @Override public long indexOf(byte b, long fromIndex) {
    return indexOf(b, fromIndex, Long.MAX_VALUE);
  }

  @Override public long indexOf(byte b, long fromIndex, long toIndex) {
    if (fromIndex < 0 || toIndex < fromIndex) {
      throw new IllegalArgumentException(
          String.format("size=%s fromIndex=%s toIndex=%s", size, fromIndex, toIndex));
    }

    if (toIndex > size) toIndex = size;
    if (fromIndex == toIndex) return -1L;

    Segmentzaq s;
    long offset;

    
    findSegmentAndOffset: {
      
      s = head;
      if (s == null) {
        
        return -1L;
      } else if (size - fromIndex < fromIndex) {
        
        offset = size;
        while (offset > fromIndex) {
          s = s.prev;
          offset -= (s.limit - s.pos);
        }
      } else {
        
        offset = 0L;
        for (long nextOffset; (nextOffset = offset + (s.limit - s.pos)) < fromIndex; ) {
          s = s.next;
          offset = nextOffset;
        }
      }
    }

    
    while (offset < toIndex) {
      byte[] data = s.data;
      int limit = (int) Math.min(s.limit, s.pos + toIndex - offset);
      int pos = (int) (s.pos + fromIndex - offset);
      for (; pos < limit; pos++) {
        if (data[pos] == b) {
          return pos - s.pos + offset;
        }
      }

      
      offset += (s.limit - s.pos);
      fromIndex = offset;
      s = s.next;
    }

    return -1L;
  }

  @Override public long indexOf(ByteStringzaq bytes) throws IOException {
    return indexOf(bytes, 0);
  }

  @Override public long indexOf(ByteStringzaq bytes, long fromIndex) throws IOException {
    if (bytes.size() == 0) throw new IllegalArgumentException("bytes is empty");
    if (fromIndex < 0) throw new IllegalArgumentException("fromIndex < 0");

    Segmentzaq s;
    long offset;

    
    findSegmentAndOffset: {
      
      s = head;
      if (s == null) {
        
        return -1L;
      } else if (size - fromIndex < fromIndex) {
        
        offset = size;
        while (offset > fromIndex) {
          s = s.prev;
          offset -= (s.limit - s.pos);
        }
      } else {
        
        offset = 0L;
        for (long nextOffset; (nextOffset = offset + (s.limit - s.pos)) < fromIndex; ) {
          s = s.next;
          offset = nextOffset;
        }
      }
    }

    
    
    byte b0 = bytes.getByte(0);
    int bytesSize = bytes.size();
    long resultLimit = size - bytesSize + 1;
    while (offset < resultLimit) {
      
      byte[] data = s.data;
      int segmentLimit = (int) Math.min(s.limit, s.pos + resultLimit - offset);
      for (int pos = (int) (s.pos + fromIndex - offset); pos < segmentLimit; pos++) {
        if (data[pos] == b0 && rangeEquals(s, pos + 1, bytes, 1, bytesSize)) {
          return pos - s.pos + offset;
        }
      }

      
      offset += (s.limit - s.pos);
      fromIndex = offset;
      s = s.next;
    }

    return -1L;
  }

  @Override public long indexOfElement(ByteStringzaq targetBytes) {
    return indexOfElement(targetBytes, 0);
  }

  @Override public long indexOfElement(ByteStringzaq targetBytes, long fromIndex) {
    if (fromIndex < 0) throw new IllegalArgumentException("fromIndex < 0");

    Segmentzaq s;
    long offset;

    
    findSegmentAndOffset: {
      
      s = head;
      if (s == null) {
        
        return -1L;
      } else if (size - fromIndex < fromIndex) {
        
        offset = size;
        while (offset > fromIndex) {
          s = s.prev;
          offset -= (s.limit - s.pos);
        }
      } else {
        
        offset = 0L;
        for (long nextOffset; (nextOffset = offset + (s.limit - s.pos)) < fromIndex; ) {
          s = s.next;
          offset = nextOffset;
        }
      }
    }

    
    
    
    if (targetBytes.size() == 2) {
      
      byte b0 = targetBytes.getByte(0);
      byte b1 = targetBytes.getByte(1);
      while (offset < size) {
        byte[] data = s.data;
        for (int pos = (int) (s.pos + fromIndex - offset), limit = s.limit; pos < limit; pos++) {
          int b = data[pos];
          if (b == b0 || b == b1) {
            return pos - s.pos + offset;
          }
        }

        
        offset += (s.limit - s.pos);
        fromIndex = offset;
        s = s.next;
      }
    } else {
      
      byte[] targetByteArray = targetBytes.internalArray();
      while (offset < size) {
        byte[] data = s.data;
        for (int pos = (int) (s.pos + fromIndex - offset), limit = s.limit; pos < limit; pos++) {
          int b = data[pos];
          for (byte t : targetByteArray) {
            if (b == t) return pos - s.pos + offset;
          }
        }

        
        offset += (s.limit - s.pos);
        fromIndex = offset;
        s = s.next;
      }
    }

    return -1L;
  }

  @Override public boolean rangeEquals(long offset, ByteStringzaq bytes) {
    return rangeEquals(offset, bytes, 0, bytes.size());
  }

  @Override public boolean rangeEquals(
          long offset, ByteStringzaq bytes, int bytesOffset, int byteCount) {
    if (offset < 0
        || bytesOffset < 0
        || byteCount < 0
        || size - offset < byteCount
        || bytes.size() - bytesOffset < byteCount) {
      return false;
    }
    for (int i = 0; i < byteCount; i++) {
      if (getByte(offset + i) != bytes.getByte(bytesOffset + i)) {
        return false;
      }
    }
    return true;
  }

  
  private boolean rangeEquals(
          Segmentzaq segment, int segmentPos, ByteStringzaq bytes, int bytesOffset, int bytesLimit) {
    int segmentLimit = segment.limit;
    byte[] data = segment.data;

    for (int i = bytesOffset; i < bytesLimit; ) {
      if (segmentPos == segmentLimit) {
        segment = segment.next;
        data = segment.data;
        segmentPos = segment.pos;
        segmentLimit = segment.limit;
      }

      if (data[segmentPos] != bytes.getByte(i)) {
        return false;
      }

      segmentPos++;
      i++;
    }

    return true;
  }

  @Override public void flush() {
  }

  @Override public void close() {
  }

  @Override public Timeoutzaq timeout() {
    return Timeoutzaq.NONE;
  }

  
  List<Integer> segmentSizes() {
    if (head == null) return Collections.emptyList();
    List<Integer> result = new ArrayList<>();
    result.add(head.limit - head.pos);
    for (Segmentzaq s = head.next; s != head; s = s.next) {
      result.add(s.limit - s.pos);
    }
    return result;
  }

  
  public ByteStringzaq md5() {
    return digest("MD5");
  }

  
  public ByteStringzaq sha1() {
    return digest("SHA-1");
  }

  
  public ByteStringzaq sha256() {
    return digest("SHA-256");
  }

  
  public ByteStringzaq sha512() {
      return digest("SHA-512");
  }

  private ByteStringzaq digest(String algorithm) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      if (head != null) {
        messageDigest.update(head.data, head.pos, head.limit - head.pos);
        for (Segmentzaq s = head.next; s != head; s = s.next) {
          messageDigest.update(s.data, s.pos, s.limit - s.pos);
        }
      }
      return ByteStringzaq.of(messageDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    }
  }

  
  public ByteStringzaq hmacSha1(ByteStringzaq key) {
    return hmac("HmacSHA1", key);
  }

  
  public ByteStringzaq hmacSha256(ByteStringzaq key) {
    return hmac("HmacSHA256", key);
  }

  
  public ByteStringzaq hmacSha512(ByteStringzaq key) {
      return hmac("HmacSHA512", key);
  }

  private ByteStringzaq hmac(String algorithm, ByteStringzaq key) {
    try {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
      if (head != null) {
        mac.update(head.data, head.pos, head.limit - head.pos);
        for (Segmentzaq s = head.next; s != head; s = s.next) {
          mac.update(s.data, s.pos, s.limit - s.pos);
        }
      }
      return ByteStringzaq.of(mac.doFinal());
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Bufferzaq)) return false;
    Bufferzaq that = (Bufferzaq) o;
    if (size != that.size) return false;
    if (size == 0) return true; 

    Segmentzaq sa = this.head;
    Segmentzaq sb = that.head;
    int posA = sa.pos;
    int posB = sb.pos;

    for (long pos = 0, count; pos < size; pos += count) {
      count = Math.min(sa.limit - posA, sb.limit - posB);

      for (int i = 0; i < count; i++) {
        if (sa.data[posA++] != sb.data[posB++]) return false;
      }

      if (posA == sa.limit) {
        sa = sa.next;
        posA = sa.pos;
      }

      if (posB == sb.limit) {
        sb = sb.next;
        posB = sb.pos;
      }
    }

    return true;
  }

  @Override public int hashCode() {
    Segmentzaq s = head;
    if (s == null) return 0;
    int result = 1;
    do {
      for (int pos = s.pos, limit = s.limit; pos < limit; pos++) {
        result = 31 * result + s.data[pos];
      }
      s = s.next;
    } while (s != head);
    return result;
  }

  
  @Override public String toString() {
    return snapshot().toString();
  }

  
  @Override public Bufferzaq clone() {
    Bufferzaq result = new Bufferzaq();
    if (size == 0) return result;

    result.head = new Segmentzaq(head);
    result.head.next = result.head.prev = result.head;
    for (Segmentzaq s = head.next; s != head; s = s.next) {
      result.head.prev.push(new Segmentzaq(s));
    }
    result.size = size;
    return result;
  }

  
  public ByteStringzaq snapshot() {
    if (size > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + size);
    }
    return snapshot((int) size);
  }

  
  public ByteStringzaq snapshot(int byteCount) {
    if (byteCount == 0) return ByteStringzaq.EMPTY;
    return new SegmentedByteStringzaqdsad(this, byteCount);
  }
}
