

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.JsonArrayq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonObjectq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonElementq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonNullq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonPrimitiveq;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public final class JsonTreeWriterqq extends JsonWriterq {
  private static final Writer UNWRITABLE_WRITER = new Writer() {
    @Override public void write(char[] buffer, int offset, int counter) {
      throw new AssertionError();
    }
    @Override public void flush() throws IOException {
      throw new AssertionError();
    }
    @Override public void close() throws IOException {
      throw new AssertionError();
    }
  };
  
  private static final JsonPrimitiveq SENTINEL_CLOSED = new JsonPrimitiveq("closed");

  
  private final List<JsonElementq> stack = new ArrayList<JsonElementq>();

  
  private String pendingName;

  
  private JsonElementq product = JsonNullq.INSTANCE;

  public JsonTreeWriterqq() {
    super(UNWRITABLE_WRITER);
  }

  
  public JsonElementq get() {
    if (!stack.isEmpty()) {
      throw new IllegalStateException("Expected one JSON element but was " + stack);
    }
    return product;
  }

  private JsonElementq peek() {
    return stack.get(stack.size() - 1);
  }

  private void put(JsonElementq value) {
    if (pendingName != null) {
      if (!value.isJsonNull() || getSerializeNulls()) {
        JsonObjectq object = (JsonObjectq) peek();
        object.add(pendingName, value);
      }
      pendingName = null;
    } else if (stack.isEmpty()) {
      product = value;
    } else {
      JsonElementq element = peek();
      if (element instanceof JsonArrayq) {
        ((JsonArrayq) element).add(value);
      } else {
        throw new IllegalStateException();
      }
    }
  }

  @Override public JsonWriterq beginArray() throws IOException {
    JsonArrayq array = new JsonArrayq();
    put(array);
    stack.add(array);
    return this;
  }

  @Override public JsonWriterq endArray() throws IOException {
    if (stack.isEmpty() || pendingName != null) {
      throw new IllegalStateException();
    }
    JsonElementq element = peek();
    if (element instanceof JsonArrayq) {
      stack.remove(stack.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }

  @Override public JsonWriterq beginObject() throws IOException {
    JsonObjectq object = new JsonObjectq();
    put(object);
    stack.add(object);
    return this;
  }

  @Override public JsonWriterq endObject() throws IOException {
    if (stack.isEmpty() || pendingName != null) {
      throw new IllegalStateException();
    }
    JsonElementq element = peek();
    if (element instanceof JsonObjectq) {
      stack.remove(stack.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }

  @Override public JsonWriterq name(String name) throws IOException {
    if (name == null) {
      throw new NullPointerException("name == null");
    }
    if (stack.isEmpty() || pendingName != null) {
      throw new IllegalStateException();
    }
    JsonElementq element = peek();
    if (element instanceof JsonObjectq) {
      pendingName = name;
      return this;
    }
    throw new IllegalStateException();
  }

  @Override public JsonWriterq value(String value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    put(new JsonPrimitiveq(value));
    return this;
  }

  @Override public JsonWriterq nullValue() throws IOException {
    put(JsonNullq.INSTANCE);
    return this;
  }

  @Override public JsonWriterq value(boolean value) throws IOException {
    put(new JsonPrimitiveq(value));
    return this;
  }

  @Override public JsonWriterq value(Boolean value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    put(new JsonPrimitiveq(value));
    return this;
  }

  @Override public JsonWriterq value(double value) throws IOException {
    if (!isLenient() && (Double.isNaN(value) || Double.isInfinite(value))) {
      throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
    }
    put(new JsonPrimitiveq(value));
    return this;
  }

  @Override public JsonWriterq value(long value) throws IOException {
    put(new JsonPrimitiveq(value));
    return this;
  }

  @Override public JsonWriterq value(Number value) throws IOException {
    if (value == null) {
      return nullValue();
    }

    if (!isLenient()) {
      double d = value.doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
      }
    }

    put(new JsonPrimitiveq(value));
    return this;
  }

  @Override public void flush() throws IOException {
  }

  @Override public void close() throws IOException {
    if (!stack.isEmpty()) {
      throw new IOException("Incomplete document");
    }
    stack.add(SENTINEL_CLOSED);
  }
}
