
package com.xxx.zzz.aall.okioss;


public final class Utf8zaq {
  private Utf8zaq() {
  }


  public static long size(String string) {
    return size(string, 0, string.length());
  }


  public static long size(String string, int beginIndex, int endIndex) {
    if (string == null) throw new IllegalArgumentException("string == null");
    if (beginIndex < 0) throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
    }
    if (endIndex > string.length()) {
      throw new IllegalArgumentException(
          "endIndex > string.length: " + endIndex + " > " + string.length());
    }

    long result = 0;
    for (int i = beginIndex; i < endIndex;) {
      int c = string.charAt(i);

      if (c < 0x80) {

        result++;
        i++;

      } else if (c < 0x800) {

        result += 2;
        i++;

      } else if (c < 0xd800 || c > 0xdfff) {

        result += 3;
        i++;

      } else {
        int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
        if (c > 0xdbff || low < 0xdc00 || low > 0xdfff) {

          result++;
          i++;

        } else {

          result += 4;
          i += 2;
        }
      }
    }

    return result;
  }
}
