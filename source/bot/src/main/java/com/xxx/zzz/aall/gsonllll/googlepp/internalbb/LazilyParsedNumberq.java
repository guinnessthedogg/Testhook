
package com.xxx.zzz.aall.gsonllll.googlepp.internalbb;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;


public final class LazilyParsedNumberq extends Number {
  private final String value;

  
  public LazilyParsedNumberq(String value) {
    this.value = value;
  }

  @Override
  public int intValue() {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      try {
        return (int) Long.parseLong(value);
      } catch (NumberFormatException nfe) {
        return new BigDecimal(value).intValue();
      }
    }
  }

  @Override
  public long longValue() {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      return new BigDecimal(value).longValue();
    }
  }

  @Override
  public float floatValue() {
    return Float.parseFloat(value);
  }

  @Override
  public double doubleValue() {
    return Double.parseDouble(value);
  }

  @Override
  public String toString() {
    return value;
  }

  
  private Object writeReplace() throws ObjectStreamException {
    return new BigDecimal(value);
  }

  private void readObject(ObjectInputStream in) throws IOException {

    throw new InvalidObjectException("Deserialization is unsupported");
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof LazilyParsedNumberq) {
      LazilyParsedNumberq other = (LazilyParsedNumberq) obj;
      return value == other.value || value.equals(other.value);
    }
    return false;
  }
}
