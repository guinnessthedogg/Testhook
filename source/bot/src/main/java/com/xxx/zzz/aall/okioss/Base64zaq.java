


package com.xxx.zzz.aall.okioss;

import java.io.UnsupportedEncodingException;

final class Base64zaq {
  private Base64zaq() {
  }

  public static byte[] decode(String in) {

    int limit = in.length();
    for (; limit > 0; limit--) {
      char c = in.charAt(limit - 1);
      if (c != '=' && c != '\n' && c != '\r' && c != ' ' && c != '\t') {
        break;
      }
    }


    byte[] out = new byte[(int) (limit * 6L / 8L)];
    int outCount = 0;
    int inCount = 0;

    int word = 0;
    for (int pos = 0; pos < limit; pos++) {
      char c = in.charAt(pos);

      int bits;
      if (c >= 'A' && c <= 'Z') {



        bits = c - 65;
      } else if (c >= 'a' && c <= 'z') {



        bits = c - 71;
      } else if (c >= '0' && c <= '9') {



        bits = c + 4;
      } else if (c == '+' || c == '-') {
        bits = 62;
      } else if (c == '/' || c == '_') {
        bits = 63;
      } else if (c == '\n' || c == '\r' || c == ' ' || c == '\t') {
        continue;
      } else {
        return null;
      }


      word = (word << 6) | (byte) bits;


      inCount++;
      if (inCount % 4 == 0) {
        out[outCount++] = (byte) (word >> 16);
        out[outCount++] = (byte) (word >> 8);
        out[outCount++] = (byte) word;
      }
    }

    int lastWordChars = inCount % 4;
    if (lastWordChars == 1) {

      return null;
    } else if (lastWordChars == 2) {

      word = word << 12;
      out[outCount++] = (byte) (word >> 16);
    } else if (lastWordChars == 3) {

      word = word << 6;
      out[outCount++] = (byte) (word >> 16);
      out[outCount++] = (byte) (word >> 8);
    }


    if (outCount == out.length) return out;


    byte[] prefix = new byte[outCount];
    System.arraycopy(out, 0, prefix, 0, outCount);
    return prefix;
  }

  private static final byte[] MAP = new byte[] {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
      'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
      'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
      '5', '6', '7', '8', '9', '+', '/'
  };

  private static final byte[] URL_MAP = new byte[] {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
      'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
      'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
      '5', '6', '7', '8', '9', '-', '_'
  };

  public static String encode(byte[] in) {
    return encode(in, MAP);
  }

  public static String encodeUrl(byte[] in) {
    return encode(in, URL_MAP);
  }

  private static String encode(byte[] in, byte[] map) {
    int length = (in.length + 2) / 3 * 4;
    byte[] out = new byte[length];
    int index = 0, end = in.length - in.length % 3;
    for (int i = 0; i < end; i += 3) {
      out[index++] = map[(in[i] & 0xff) >> 2];
      out[index++] = map[((in[i] & 0x03) << 4) | ((in[i + 1] & 0xff) >> 4)];
      out[index++] = map[((in[i + 1] & 0x0f) << 2) | ((in[i + 2] & 0xff) >> 6)];
      out[index++] = map[(in[i + 2] & 0x3f)];
    }
    switch (in.length % 3) {
      case 1:
        out[index++] = map[(in[end] & 0xff) >> 2];
        out[index++] = map[(in[end] & 0x03) << 4];
        out[index++] = '=';
        out[index++] = '=';
        break;
      case 2:
        out[index++] = map[(in[end] & 0xff) >> 2];
        out[index++] = map[((in[end] & 0x03) << 4) | ((in[end + 1] & 0xff) >> 4)];
        out[index++] = map[((in[end + 1] & 0x0f) << 2)];
        out[index++] = '=';
        break;
    }
    try {
      return new String(out, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError(e);
    }
  }
}
