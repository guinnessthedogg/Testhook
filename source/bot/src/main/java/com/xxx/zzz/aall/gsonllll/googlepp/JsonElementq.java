

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Streamsq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;


public abstract class JsonElementq {

  public abstract JsonElementq deepCopy();


  public boolean isJsonArray() {
    return this instanceof JsonArrayq;
  }


  public boolean isJsonObject() {
    return this instanceof JsonObjectq;
  }


  public boolean isJsonPrimitive() {
    return this instanceof JsonPrimitiveq;
  }


  public boolean isJsonNull() {
    return this instanceof JsonNullq;
  }


  public JsonObjectq getAsJsonObject() {
    if (isJsonObject()) {
      return (JsonObjectq) this;
    }
    throw new IllegalStateException("Not a JSON Object: " + this);
  }


  public JsonArrayq getAsJsonArray() {
    if (isJsonArray()) {
      return (JsonArrayq) this;
    }
    throw new IllegalStateException("Not a JSON Array: " + this);
  }


  public JsonPrimitiveq getAsJsonPrimitive() {
    if (isJsonPrimitive()) {
      return (JsonPrimitiveq) this;
    }
    throw new IllegalStateException("Not a JSON Primitive: " + this);
  }


  public JsonNullq getAsJsonNull() {
    if (isJsonNull()) {
      return (JsonNullq) this;
    }
    throw new IllegalStateException("Not a JSON Null: " + this);
  }


  public boolean getAsBoolean() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public Number getAsNumber() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public String getAsString() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public double getAsDouble() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public float getAsFloat() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public long getAsLong() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public int getAsInt() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public byte getAsByte() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  @Deprecated
  public char getAsCharacter() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public BigDecimal getAsBigDecimal() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public BigInteger getAsBigInteger() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  public short getAsShort() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }


  @Override
  public String toString() {
    try {
      StringWriter stringWriter = new StringWriter();
      JsonWriterq jsonWriter = new JsonWriterq(stringWriter);
      jsonWriter.setLenient(true);
      Streamsq.write(this, jsonWriter);
      return stringWriter.toString();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
}
