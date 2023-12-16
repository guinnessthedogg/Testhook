

package com.xxx.zzz.aall.gsonllll.googlepp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public final class JsonArrayq extends JsonElementq implements Iterable<JsonElementq> {
  private final List<JsonElementq> elements;

  
  public JsonArrayq() {
    elements = new ArrayList<JsonElementq>();
  }
  
  public JsonArrayq(int capacity) {
    elements = new ArrayList<JsonElementq>(capacity);
  }

  
  @Override
  public JsonArrayq deepCopy() {
    if (!elements.isEmpty()) {
      JsonArrayq result = new JsonArrayq(elements.size());
      for (JsonElementq element : elements) {
        result.add(element.deepCopy());
      }
      return result;
    }
    return new JsonArrayq();
  }

  
  public void add(Boolean bool) {
    elements.add(bool == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(bool));
  }

  
  public void add(Character character) {
    elements.add(character == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(character));
  }

  
  public void add(Number number) {
    elements.add(number == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(number));
  }

  
  public void add(String string) {
    elements.add(string == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(string));
  }

  
  public void add(JsonElementq element) {
    if (element == null) {
      element = JsonNullq.INSTANCE;
    }
    elements.add(element);
  }

  
  public void addAll(JsonArrayq array) {
    elements.addAll(array.elements);
  }

  
  public JsonElementq set(int index, JsonElementq element) {
    return elements.set(index, element);
  }

  
  public boolean remove(JsonElementq element) {
    return elements.remove(element);
  }

  
  public JsonElementq remove(int index) {
    return elements.remove(index);
  }

  
  public boolean contains(JsonElementq element) {
    return elements.contains(element);
  }

  
  public int size() {
    return elements.size();
  }
  
  
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  
  public Iterator<JsonElementq> iterator() {
    return elements.iterator();
  }

  
  public JsonElementq get(int i) {
    return elements.get(i);
  }

  
  @Override
  public Number getAsNumber() {
    if (elements.size() == 1) {
      return elements.get(0).getAsNumber();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public String getAsString() {
    if (elements.size() == 1) {
      return elements.get(0).getAsString();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public double getAsDouble() {
    if (elements.size() == 1) {
      return elements.get(0).getAsDouble();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public BigDecimal getAsBigDecimal() {
    if (elements.size() == 1) {
      return elements.get(0).getAsBigDecimal();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public BigInteger getAsBigInteger() {
    if (elements.size() == 1) {
      return elements.get(0).getAsBigInteger();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public float getAsFloat() {
    if (elements.size() == 1) {
      return elements.get(0).getAsFloat();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public long getAsLong() {
    if (elements.size() == 1) {
      return elements.get(0).getAsLong();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public int getAsInt() {
    if (elements.size() == 1) {
      return elements.get(0).getAsInt();
    }
    throw new IllegalStateException();
  }

  @Override
  public byte getAsByte() {
    if (elements.size() == 1) {
      return elements.get(0).getAsByte();
    }
    throw new IllegalStateException();
  }

  @Override
  public char getAsCharacter() {
    if (elements.size() == 1) {
      JsonElementq element = elements.get(0);
      @SuppressWarnings("deprecation")
      char result = element.getAsCharacter();
      return result;
    }
    throw new IllegalStateException();
  }

  
  @Override
  public short getAsShort() {
    if (elements.size() == 1) {
      return elements.get(0).getAsShort();
    }
    throw new IllegalStateException();
  }

  
  @Override
  public boolean getAsBoolean() {
    if (elements.size() == 1) {
      return elements.get(0).getAsBoolean();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object o) {
    return (o == this) || (o instanceof JsonArrayq && ((JsonArrayq) o).elements.equals(elements));
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }
}
