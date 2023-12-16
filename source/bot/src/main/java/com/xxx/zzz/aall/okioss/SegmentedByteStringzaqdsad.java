
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


final class SegmentedByteStringzaqdsad extends ByteStringzaq {
  final transient byte[][] segments;
  final transient int[] directory;

  SegmentedByteStringzaqdsad(Bufferzaq buffer, int byteCount) {
    super(null);
    Utilzaqq.checkOffsetAndCount(buffer.size, 0, byteCount);


    int offset = 0;
    int segmentCount = 0;
    for (Segmentzaq s = buffer.head; offset < byteCount; s = s.next) {
      if (s.limit == s.pos) {
        throw new AssertionError("s.limit == s.pos");
      }
      offset += s.limit - s.pos;
      segmentCount++;
    }


    this.segments = new byte[segmentCount][];
    this.directory = new int[segmentCount * 2];
    offset = 0;
    segmentCount = 0;
    for (Segmentzaq s = buffer.head; offset < byteCount; s = s.next) {
      segments[segmentCount] = s.data;
      offset += s.limit - s.pos;
      if (offset > byteCount) {
        offset = byteCount;
      }
      directory[segmentCount] = offset;
      directory[segmentCount + segments.length] = s.pos;
      s.shared = true;
      segmentCount++;
    }
  }

  @Override public String utf8() {
    return toByteString().utf8();
  }

  @Override public String string(Charset charset) {
    return toByteString().string(charset);
  }

  @Override public String base64() {
    return toByteString().base64();
  }

  @Override public String hex() {
    return toByteString().hex();
  }

  @Override public ByteStringzaq toAsciiLowercase() {
    return toByteString().toAsciiLowercase();
  }

  @Override public ByteStringzaq toAsciiUppercase() {
    return toByteString().toAsciiUppercase();
  }

  @Override public ByteStringzaq md5() {
    return toByteString().md5();
  }

  @Override public ByteStringzaq sha1() {
    return toByteString().sha1();
  }

  @Override public ByteStringzaq sha256() {
    return toByteString().sha256();
  }

  @Override public ByteStringzaq hmacSha1(ByteStringzaq key) {
    return toByteString().hmacSha1(key);
  }

  @Override public ByteStringzaq hmacSha256(ByteStringzaq key) {
    return toByteString().hmacSha256(key);
  }

  @Override public String base64Url() {
    return toByteString().base64Url();
  }

  @Override public ByteStringzaq substring(int beginIndex) {
    return toByteString().substring(beginIndex);
  }

  @Override public ByteStringzaq substring(int beginIndex, int endIndex) {
    return toByteString().substring(beginIndex, endIndex);
  }

  @Override public byte getByte(int pos) {
    Utilzaqq.checkOffsetAndCount(directory[segments.length - 1], pos, 1);
    int segment = segment(pos);
    int segmentOffset = segment == 0 ? 0 : directory[segment - 1];
    int segmentPos = directory[segment + segments.length];
    return segments[segment][pos - segmentOffset + segmentPos];
  }


  private int segment(int pos) {

    int i = Arrays.binarySearch(directory, 0, segments.length, pos + 1);
    return i >= 0 ? i : ~i;
  }

  @Override public int size() {
    return directory[segments.length - 1];
  }

  @Override public byte[] toByteArray() {
    byte[] result = new byte[directory[segments.length - 1]];
    int segmentOffset = 0;
    for (int s = 0, segmentCount = segments.length; s < segmentCount; s++) {
      int segmentPos = directory[segmentCount + s];
      int nextSegmentOffset = directory[s];
      System.arraycopy(segments[s], segmentPos, result, segmentOffset,
          nextSegmentOffset - segmentOffset);
      segmentOffset = nextSegmentOffset;
    }
    return result;
  }

  @Override public ByteBuffer asByteBuffer() {
    return ByteBuffer.wrap(toByteArray()).asReadOnlyBuffer();
  }

  @Override public void write(OutputStream out) throws IOException {
    if (out == null) throw new IllegalArgumentException("out == null");
    int segmentOffset = 0;
    for (int s = 0, segmentCount = segments.length; s < segmentCount; s++) {
      int segmentPos = directory[segmentCount + s];
      int nextSegmentOffset = directory[s];
      out.write(segments[s], segmentPos, nextSegmentOffset - segmentOffset);
      segmentOffset = nextSegmentOffset;
    }
  }

  @Override void write(Bufferzaq buffer) {
    int segmentOffset = 0;
    for (int s = 0, segmentCount = segments.length; s < segmentCount; s++) {
      int segmentPos = directory[segmentCount + s];
      int nextSegmentOffset = directory[s];
      Segmentzaq segment = new Segmentzaq(segments[s], segmentPos,
          segmentPos + nextSegmentOffset - segmentOffset);
      if (buffer.head == null) {
        buffer.head = segment.next = segment.prev = segment;
      } else {
        buffer.head.prev.push(segment);
      }
      segmentOffset = nextSegmentOffset;
    }
    buffer.size += segmentOffset;
  }

  @Override public boolean rangeEquals(
          int offset, ByteStringzaq other, int otherOffset, int byteCount) {
    if (offset < 0 || offset > size() - byteCount) return false;

    for (int s = segment(offset); byteCount > 0; s++) {
      int segmentOffset = s == 0 ? 0 : directory[s - 1];
      int segmentSize = directory[s] - segmentOffset;
      int stepSize = Math.min(byteCount, segmentOffset + segmentSize - offset);
      int segmentPos = directory[segments.length + s];
      int arrayOffset = offset - segmentOffset + segmentPos;
      if (!other.rangeEquals(otherOffset, segments[s], arrayOffset, stepSize)) return false;
      offset += stepSize;
      otherOffset += stepSize;
      byteCount -= stepSize;
    }
    return true;
  }

  @Override public boolean rangeEquals(int offset, byte[] other, int otherOffset, int byteCount) {
    if (offset < 0 || offset > size() - byteCount
        || otherOffset < 0 || otherOffset > other.length - byteCount) {
      return false;
    }

    for (int s = segment(offset); byteCount > 0; s++) {
      int segmentOffset = s == 0 ? 0 : directory[s - 1];
      int segmentSize = directory[s] - segmentOffset;
      int stepSize = Math.min(byteCount, segmentOffset + segmentSize - offset);
      int segmentPos = directory[segments.length + s];
      int arrayOffset = offset - segmentOffset + segmentPos;
      if (!Utilzaqq.arrayRangeEquals(segments[s], arrayOffset, other, otherOffset, stepSize)) return false;
      offset += stepSize;
      otherOffset += stepSize;
      byteCount -= stepSize;
    }
    return true;
  }

  @Override public int indexOf(byte[] other, int fromIndex) {
    return toByteString().indexOf(other, fromIndex);
  }

  @Override public int lastIndexOf(byte[] other, int fromIndex) {
    return toByteString().lastIndexOf(other, fromIndex);
  }


  private ByteStringzaq toByteString() {
    return new ByteStringzaq(toByteArray());
  }

  @Override byte[] internalArray() {
    return toByteArray();
  }

  @Override public boolean equals(Object o) {
    if (o == this) return true;
    return o instanceof ByteStringzaq
        && ((ByteStringzaq) o).size() == size()
        && rangeEquals(0, ((ByteStringzaq) o), 0, size());
  }

  @Override public int hashCode() {
    int result = hashCode;
    if (result != 0) return result;


    result = 1;
    int segmentOffset = 0;
    for (int s = 0, segmentCount = segments.length; s < segmentCount; s++) {
      byte[] segment = segments[s];
      int segmentPos = directory[segmentCount + s];
      int nextSegmentOffset = directory[s];
      int segmentSize = nextSegmentOffset - segmentOffset;
      for (int i = segmentPos, limit = segmentPos + segmentSize; i < limit; i++) {
        result = (31 * result) + segment[i];
      }
      segmentOffset = nextSegmentOffset;
    }
    return (hashCode = result);
  }

  @Override public String toString() {
    return toByteString().toString();
  }

  private Object writeReplace() {
    return toByteString();
  }
}
