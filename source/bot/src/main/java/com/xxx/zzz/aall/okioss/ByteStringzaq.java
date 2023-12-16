
package com.xxx.zzz.aall.okioss;

import static com.xxx.zzz.aall.okioss.Utilzaqq.arrayRangeEquals;
import static com.xxx.zzz.aall.okioss.Utilzaqq.checkOffsetAndCount;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class ByteStringzaq implements Serializable, Comparable<ByteStringzaq> {
  static final char[] HEX_DIGITS =
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  private static final long serialVersionUID = 1L;


  public static final ByteStringzaq EMPTY = ByteStringzaq.of();

  final byte[] data;
  transient int hashCode;
  transient String utf8;

  ByteStringzaq(byte[] data) {
    this.data = data;
  }


  public static ByteStringzaq of(byte... data) {
    if (data == null) throw new IllegalArgumentException("data == null");
    return new ByteStringzaq(data.clone());
  }


  public static ByteStringzaq of(byte[] data, int offset, int byteCount) {
    if (data == null) throw new IllegalArgumentException("data == null");
    checkOffsetAndCount(data.length, offset, byteCount);

    byte[] copy = new byte[byteCount];
    System.arraycopy(data, offset, copy, 0, byteCount);
    return new ByteStringzaq(copy);
  }

  public static ByteStringzaq of(ByteBuffer data) {
    if (data == null) throw new IllegalArgumentException("data == null");

    byte[] copy = new byte[data.remaining()];
    data.get(copy);
    return new ByteStringzaq(copy);
  }


  public static ByteStringzaq encodeUtf8(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    ByteStringzaq byteString = new ByteStringzaq(s.getBytes(Utilzaqq.UTF_8));
    byteString.utf8 = s;
    return byteString;
  }


  public static ByteStringzaq encodeString(String s, Charset charset) {
    if (s == null) throw new IllegalArgumentException("s == null");
    if (charset == null) throw new IllegalArgumentException("charset == null");
    return new ByteStringzaq(s.getBytes(charset));
  }


  public String utf8() {
    String result = utf8;

    return result != null ? result : (utf8 = new String(data, Utilzaqq.UTF_8));
  }


  public String string(Charset charset) {
    if (charset == null) throw new IllegalArgumentException("charset == null");
    return new String(data, charset);
  }


  public String base64() {
    return Base64zaq.encode(data);
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
      return ByteStringzaq.of(MessageDigest.getInstance(algorithm).digest(data));
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
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
      return ByteStringzaq.of(mac.doFinal(data));
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }


  public String base64Url() {
    return Base64zaq.encodeUrl(data);
  }


  public static @Nullableq
  ByteStringzaq decodeBase64(String base64) {
    if (base64 == null) throw new IllegalArgumentException("base64 == null");
    byte[] decoded = Base64zaq.decode(base64);
    return decoded != null ? new ByteStringzaq(decoded) : null;
  }


  public String hex() {
    char[] result = new char[data.length * 2];
    int c = 0;
    for (byte b : data) {
      result[c++] = HEX_DIGITS[(b >> 4) & 0xf];
      result[c++] = HEX_DIGITS[b & 0xf];
    }
    return new String(result);
  }


  public static ByteStringzaq decodeHex(String hex) {
    if (hex == null) throw new IllegalArgumentException("hex == null");
    if (hex.length() % 2 != 0) throw new IllegalArgumentException("Unexpected hex string: " + hex);

    byte[] result = new byte[hex.length() / 2];
    for (int i = 0; i < result.length; i++) {
      int d1 = decodeHexDigit(hex.charAt(i * 2)) << 4;
      int d2 = decodeHexDigit(hex.charAt(i * 2 + 1));
      result[i] = (byte) (d1 + d2);
    }
    return of(result);
  }

  private static int decodeHexDigit(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    throw new IllegalArgumentException("Unexpected hex digit: " + c);
  }


  public static ByteStringzaq read(InputStream in, int byteCount) throws IOException {
    if (in == null) throw new IllegalArgumentException("in == null");
    if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);

    byte[] result = new byte[byteCount];
    for (int offset = 0, read; offset < byteCount; offset += read) {
      read = in.read(result, offset, byteCount - offset);
      if (read == -1) throw new EOFException();
    }
    return new ByteStringzaq(result);
  }


  public ByteStringzaq toAsciiLowercase() {

    for (int i = 0; i < data.length; i++) {
      byte c = data[i];
      if (c < 'A' || c > 'Z') continue;



      byte[] lowercase = data.clone();
      lowercase[i++] = (byte) (c - ('A' - 'a'));
      for (; i < lowercase.length; i++) {
        c = lowercase[i];
        if (c < 'A' || c > 'Z') continue;
        lowercase[i] = (byte) (c - ('A' - 'a'));
      }
      return new ByteStringzaq(lowercase);
    }
    return this;
  }


  public ByteStringzaq toAsciiUppercase() {

    for (int i = 0; i < data.length; i++) {
      byte c = data[i];
      if (c < 'a' || c > 'z') continue;



      byte[] lowercase = data.clone();
      lowercase[i++] = (byte) (c - ('a' - 'A'));
      for (; i < lowercase.length; i++) {
        c = lowercase[i];
        if (c < 'a' || c > 'z') continue;
        lowercase[i] = (byte) (c - ('a' - 'A'));
      }
      return new ByteStringzaq(lowercase);
    }
    return this;
  }


  public ByteStringzaq substring(int beginIndex) {
    return substring(beginIndex, data.length);
  }


  public ByteStringzaq substring(int beginIndex, int endIndex) {
    if (beginIndex < 0) throw new IllegalArgumentException("beginIndex < 0");
    if (endIndex > data.length) {
      throw new IllegalArgumentException("endIndex > length(" + data.length + ")");
    }

    int subLen = endIndex - beginIndex;
    if (subLen < 0) throw new IllegalArgumentException("endIndex < beginIndex");

    if ((beginIndex == 0) && (endIndex == data.length)) {
      return this;
    }

    byte[] copy = new byte[subLen];
    System.arraycopy(data, beginIndex, copy, 0, subLen);
    return new ByteStringzaq(copy);
  }


  public byte getByte(int pos) {
    return data[pos];
  }


  public int size() {
    return data.length;
  }


  public byte[] toByteArray() {
    return data.clone();
  }


  byte[] internalArray() {
    return data;
  }


  public ByteBuffer asByteBuffer() {
    return ByteBuffer.wrap(data).asReadOnlyBuffer();
  }


  public void write(OutputStream out) throws IOException {
    if (out == null) throw new IllegalArgumentException("out == null");
    out.write(data);
  }


  void write(Bufferzaq buffer) {
    buffer.write(data, 0, data.length);
  }


  public boolean rangeEquals(int offset, ByteStringzaq other, int otherOffset, int byteCount) {
    return other.rangeEquals(otherOffset, this.data, offset, byteCount);
  }


  public boolean rangeEquals(int offset, byte[] other, int otherOffset, int byteCount) {
    return offset >= 0 && offset <= data.length - byteCount
        && otherOffset >= 0 && otherOffset <= other.length - byteCount
        && arrayRangeEquals(data, offset, other, otherOffset, byteCount);
  }

  public final boolean startsWith(ByteStringzaq prefix) {
    return rangeEquals(0, prefix, 0, prefix.size());
  }

  public final boolean startsWith(byte[] prefix) {
    return rangeEquals(0, prefix, 0, prefix.length);
  }

  public final boolean endsWith(ByteStringzaq suffix) {
    return rangeEquals(size() - suffix.size(), suffix, 0, suffix.size());
  }

  public final boolean endsWith(byte[] suffix) {
    return rangeEquals(size() - suffix.length, suffix, 0, suffix.length);
  }

  public final int indexOf(ByteStringzaq other) {
    return indexOf(other.internalArray(), 0);
  }

  public final int indexOf(ByteStringzaq other, int fromIndex) {
    return indexOf(other.internalArray(), fromIndex);
  }

  public final int indexOf(byte[] other) {
    return indexOf(other, 0);
  }

  public int indexOf(byte[] other, int fromIndex) {
    fromIndex = Math.max(fromIndex, 0);
    for (int i = fromIndex, limit = data.length - other.length; i <= limit; i++) {
      if (arrayRangeEquals(data, i, other, 0, other.length)) {
        return i;
      }
    }
    return -1;
  }

  public final int lastIndexOf(ByteStringzaq other) {
    return lastIndexOf(other.internalArray(), size());
  }

  public final int lastIndexOf(ByteStringzaq other, int fromIndex) {
    return lastIndexOf(other.internalArray(), fromIndex);
  }

  public final int lastIndexOf(byte[] other) {
    return lastIndexOf(other, size());
  }

  public int lastIndexOf(byte[] other, int fromIndex) {
    fromIndex = Math.min(fromIndex, data.length - other.length);
    for (int i = fromIndex; i >= 0; i--) {
      if (arrayRangeEquals(data, i, other, 0, other.length)) {
        return i;
      }
    }
    return -1;
  }

  @Override public boolean equals(Object o) {
    if (o == this) return true;
    return o instanceof ByteStringzaq
        && ((ByteStringzaq) o).size() == data.length
        && ((ByteStringzaq) o).rangeEquals(0, data, 0, data.length);
  }

  @Override public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = Arrays.hashCode(data));
  }

  @Override public int compareTo(ByteStringzaq byteString) {
    int sizeA = size();
    int sizeB = byteString.size();
    for (int i = 0, size = Math.min(sizeA, sizeB); i < size; i++) {
      int byteA = getByte(i) & 0xff;
      int byteB = byteString.getByte(i) & 0xff;
      if (byteA == byteB) continue;
      return byteA < byteB ? -1 : 1;
    }
    if (sizeA == sizeB) return 0;
    return sizeA < sizeB ? -1 : 1;
  }


  @Override public String toString() {
    if (data.length == 0) {
      return "[size=0]";
    }

    String text = utf8();
    int i = codePointIndexToCharIndex(text, 64);

    if (i == -1) {
      return data.length <= 64
          ? "[hex=" + hex() + "]"
          : "[size=" + data.length + " hex=" + substring(0, 64).hex() + "…]";
    }

    String safeText = text.substring(0, i)
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\r", "\\r");
    return i < text.length()
        ? "[size=" + data.length + " text=" + safeText + "…]"
        : "[text=" + safeText + "]";
  }

  static int codePointIndexToCharIndex(String s, int codePointCount) {
    for (int i = 0, j = 0, length = s.length(), c; i < length; i += Character.charCount(c)) {
      if (j == codePointCount) {
        return i;
      }
      c = s.codePointAt(i);
      if ((Character.isISOControl(c) && c != '\n' && c != '\r')
          || c == Bufferzaq.REPLACEMENT_CHARACTER) {
        return -1;
      }
      j++;
    }
    return s.length();
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int dataLength = in.readInt();
    ByteStringzaq byteString = ByteStringzaq.read(in, dataLength);
    try {
      Field field = ByteStringzaq.class.getDeclaredField("data");
      field.setAccessible(true);
      field.set(this, byteString.data);
    } catch (NoSuchFieldException e) {
      throw new AssertionError();
    } catch (IllegalAccessException e) {
      throw new AssertionError();
    }
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeInt(data.length);
    out.write(data);
  }
}
