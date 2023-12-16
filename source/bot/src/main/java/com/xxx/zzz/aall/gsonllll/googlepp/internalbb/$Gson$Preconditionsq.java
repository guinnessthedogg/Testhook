

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb;


public final class $Gson$Preconditionsq {
  private $Gson$Preconditionsq() {
    throw new UnsupportedOperationException();
  }

  public static <T> T checkNotNull(T obj) {
    if (obj == null) {
      throw new NullPointerException();
    }
    return obj;
  }

  public static void checkArgument(boolean condition) {
    if (!condition) {
      throw new IllegalArgumentException();
    }
  }
}
