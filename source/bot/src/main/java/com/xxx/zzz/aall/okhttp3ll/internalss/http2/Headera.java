
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okioss.ByteStringzaq;


public final class Headera {

  public static final ByteStringzaq PSEUDO_PREFIX = ByteStringzaq.encodeUtf8(":");
  public static final ByteStringzaq RESPONSE_STATUS = ByteStringzaq.encodeUtf8(":status");
  public static final ByteStringzaq TARGET_METHOD = ByteStringzaq.encodeUtf8(":method");
  public static final ByteStringzaq TARGET_PATH = ByteStringzaq.encodeUtf8(":path");
  public static final ByteStringzaq TARGET_SCHEME = ByteStringzaq.encodeUtf8(":scheme");
  public static final ByteStringzaq TARGET_AUTHORITY = ByteStringzaq.encodeUtf8(":authority");

  
  public final ByteStringzaq name;
  
  public final ByteStringzaq value;
  final int hpackSize;


  public Headera(String name, String value) {
    this(ByteStringzaq.encodeUtf8(name), ByteStringzaq.encodeUtf8(value));
  }

  public Headera(ByteStringzaq name, String value) {
    this(name, ByteStringzaq.encodeUtf8(value));
  }

  public Headera(ByteStringzaq name, ByteStringzaq value) {
    this.name = name;
    this.value = value;
    this.hpackSize = 32 + name.size() + value.size();
  }

  @Override public boolean equals(Object other) {
    if (other instanceof Headera) {
      Headera that = (Headera) other;
      return this.name.equals(that.name)
          && this.value.equals(that.value);
    }
    return false;
  }

  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + name.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }

  @Override public String toString() {
    return Utilaq.format("%s: %s", name.utf8(), value.utf8());
  }
}
