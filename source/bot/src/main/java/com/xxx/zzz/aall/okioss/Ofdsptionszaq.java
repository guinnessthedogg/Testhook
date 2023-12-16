
package com.xxx.zzz.aall.okioss;

import java.util.AbstractList;
import java.util.RandomAccess;


public final class Ofdsptionszaq extends AbstractList<ByteStringzaq> implements RandomAccess {
  final ByteStringzaq[] byteStrings;

  private Ofdsptionszaq(ByteStringzaq[] byteStrings) {
    this.byteStrings = byteStrings;
  }

  public static Ofdsptionszaq of(ByteStringzaq... byteStrings) {
    return new Ofdsptionszaq(byteStrings.clone());
  }

  @Override public ByteStringzaq get(int i) {
    return byteStrings[i];
  }

  @Override public int size() {
    return byteStrings.length;
  }
}
