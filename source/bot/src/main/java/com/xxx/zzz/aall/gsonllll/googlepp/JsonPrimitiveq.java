

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Preconditionsq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.LazilyParsedNumberq;

import java.math.BigDecimal;
import java.math.BigInteger;


public final class JsonPrimitiveq extends JsonElementq {

  private final Object value;


  public JsonPrimitiveq(Boolean bool) {
    value = $Gson$Preconditionsq.checkNotNull(bool);
  }


  public JsonPrimitiveq(Number number) {
    value = $Gson$Preconditionsq.checkNotNull(number);
  }


  public JsonPrimitiveq(String string) {
    value = $Gson$Preconditionsq.checkNotNull(string);
  }


  public JsonPrimitiveq(Character c) {


    value = $Gson$Preconditionsq.checkNotNull(c).toString();
  }


  @Override
  public JsonPrimitiveq deepCopy() {
    return this;
  }


  public boolean isBoolean() {
    return value instanceof Boolean;
  }


  @Override
  public boolean getAsBoolean() {
    if (isBoolean()) {
      return ((Boolean) value).booleanValue();
    }

    return Boolean.parseBoolean(getAsString());
  }


  public boolean isNumber() {
    return value instanceof Number;
  }


  @Override
  public Number getAsNumber() {
    return value instanceof String ? new LazilyParsedNumberq((String) value) : (Number) value;
  }


  public boolean isString() {
    return value instanceof String;
  }


  @Override
  public String getAsString() {
    if (isNumber()) {
      return getAsNumber().toString();
    } else if (isBoolean()) {
      return ((Boolean) value).toString();
    } else {
      return (String) value;
    }
  }


  @Override
  public double getAsDouble() {
    return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
  }


  @Override
  public BigDecimal getAsBigDecimal() {
    return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
  }


  @Override
  public BigInteger getAsBigInteger() {
    return value instanceof BigInteger ?
        (BigInteger) value : new BigInteger(value.toString());
  }


  @Override
  public float getAsFloat() {
    return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
  }


  @Override
  public long getAsLong() {
    return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
  }


  @Override
  public short getAsShort() {
    return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
  }


  @Override
  public int getAsInt() {
    return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
  }

  @Override
  public byte getAsByte() {
    return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
  }

  @Override
  public char getAsCharacter() {
    return getAsString().charAt(0);
  }

  @Override
  public int hashCode() {
    if (value == null) {
      return 31;
    }

    if (isIntegral(this)) {
      long value = getAsNumber().longValue();
      return (int) (value ^ (value >>> 32));
    }
    if (value instanceof Number) {
      long value = Double.doubleToLongBits(getAsNumber().doubleValue());
      return (int) (value ^ (value >>> 32));
    }
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    JsonPrimitiveq other = (JsonPrimitiveq)obj;
    if (value == null) {
      return other.value == null;
    }
    if (isIntegral(this) && isIntegral(other)) {
      return getAsNumber().longValue() == other.getAsNumber().longValue();
    }
    if (value instanceof Number && other.value instanceof Number) {
      double a = getAsNumber().doubleValue();


      double b = other.getAsNumber().doubleValue();
      return a == b || (Double.isNaN(a) && Double.isNaN(b));
    }
    return value.equals(other.value);
  }


  private static boolean isIntegral(JsonPrimitiveq primitive) {
    if (primitive.value instanceof Number) {
      Number number = (Number) primitive.value;
      return number instanceof BigInteger || number instanceof Long || number instanceof Integer
          || number instanceof Short || number instanceof Byte;
    }
    return false;
  }
}
