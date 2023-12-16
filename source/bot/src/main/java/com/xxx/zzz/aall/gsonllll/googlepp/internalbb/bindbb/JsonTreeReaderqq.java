

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.JsonArrayq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonElementq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonNullq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonObjectq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonPrimitiveq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;


public final class JsonTreeReaderqq extends JsonReaderq {
  private static final Reader UNREADABLE_READER = new Reader() {
    @Override public int read(char[] buffer, int offset, int count) throws IOException {
      throw new AssertionError();
    }
    @Override public void close() throws IOException {
      throw new AssertionError();
    }
  };
  private static final Object SENTINEL_CLOSED = new Object();


  private Object[] stack = new Object[32];
  private int stackSize = 0;


  private String[] pathNames = new String[32];
  private int[] pathIndices = new int[32];

  public JsonTreeReaderqq(JsonElementq element) {
    super(UNREADABLE_READER);
    push(element);
  }

  @Override public void beginArray() throws IOException {
    expect(JsonTokenq.BEGIN_ARRAY);
    JsonArrayq array = (JsonArrayq) peekStack();
    push(array.iterator());
    pathIndices[stackSize - 1] = 0;
  }

  @Override public void endArray() throws IOException {
    expect(JsonTokenq.END_ARRAY);
    popStack();
    popStack();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
  }

  @Override public void beginObject() throws IOException {
    expect(JsonTokenq.BEGIN_OBJECT);
    JsonObjectq object = (JsonObjectq) peekStack();
    push(object.entrySet().iterator());
  }

  @Override public void endObject() throws IOException {
    expect(JsonTokenq.END_OBJECT);
    popStack();
    popStack();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
  }

  @Override public boolean hasNext() throws IOException {
    JsonTokenq token = peek();
    return token != JsonTokenq.END_OBJECT && token != JsonTokenq.END_ARRAY && token != JsonTokenq.END_DOCUMENT;
  }

  @Override public JsonTokenq peek() throws IOException {
    if (stackSize == 0) {
      return JsonTokenq.END_DOCUMENT;
    }

    Object o = peekStack();
    if (o instanceof Iterator) {
      boolean isObject = stack[stackSize - 2] instanceof JsonObjectq;
      Iterator<?> iterator = (Iterator<?>) o;
      if (iterator.hasNext()) {
        if (isObject) {
          return JsonTokenq.NAME;
        } else {
          push(iterator.next());
          return peek();
        }
      } else {
        return isObject ? JsonTokenq.END_OBJECT : JsonTokenq.END_ARRAY;
      }
    } else if (o instanceof JsonObjectq) {
      return JsonTokenq.BEGIN_OBJECT;
    } else if (o instanceof JsonArrayq) {
      return JsonTokenq.BEGIN_ARRAY;
    } else if (o instanceof JsonPrimitiveq) {
      JsonPrimitiveq primitive = (JsonPrimitiveq) o;
      if (primitive.isString()) {
        return JsonTokenq.STRING;
      } else if (primitive.isBoolean()) {
        return JsonTokenq.BOOLEAN;
      } else if (primitive.isNumber()) {
        return JsonTokenq.NUMBER;
      } else {
        throw new AssertionError();
      }
    } else if (o instanceof JsonNullq) {
      return JsonTokenq.NULL;
    } else if (o == SENTINEL_CLOSED) {
      throw new IllegalStateException("JsonReader is closed");
    } else {
      throw new AssertionError();
    }
  }

  private Object peekStack() {
    return stack[stackSize - 1];
  }

  private Object popStack() {
    Object result = stack[--stackSize];
    stack[stackSize] = null;
    return result;
  }

  private void expect(JsonTokenq expected) throws IOException {
    if (peek() != expected) {
      throw new IllegalStateException(
          "Expected " + expected + " but was " + peek() + locationString());
    }
  }

  @Override public String nextName() throws IOException {
    expect(JsonTokenq.NAME);
    Iterator<?> i = (Iterator<?>) peekStack();
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
    String result = (String) entry.getKey();
    pathNames[stackSize - 1] = result;
    push(entry.getValue());
    return result;
  }

  @Override public String nextString() throws IOException {
    JsonTokenq token = peek();
    if (token != JsonTokenq.STRING && token != JsonTokenq.NUMBER) {
      throw new IllegalStateException(
          "Expected " + JsonTokenq.STRING + " but was " + token + locationString());
    }
    String result = ((JsonPrimitiveq) popStack()).getAsString();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
    return result;
  }

  @Override public boolean nextBoolean() throws IOException {
    expect(JsonTokenq.BOOLEAN);
    boolean result = ((JsonPrimitiveq) popStack()).getAsBoolean();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
    return result;
  }

  @Override public void nextNull() throws IOException {
    expect(JsonTokenq.NULL);
    popStack();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
  }

  @Override public double nextDouble() throws IOException {
    JsonTokenq token = peek();
    if (token != JsonTokenq.NUMBER && token != JsonTokenq.STRING) {
      throw new IllegalStateException(
          "Expected " + JsonTokenq.NUMBER + " but was " + token + locationString());
    }
    double result = ((JsonPrimitiveq) peekStack()).getAsDouble();
    if (!isLenient() && (Double.isNaN(result) || Double.isInfinite(result))) {
      throw new NumberFormatException("JSON forbids NaN and infinities: " + result);
    }
    popStack();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
    return result;
  }

  @Override public long nextLong() throws IOException {
    JsonTokenq token = peek();
    if (token != JsonTokenq.NUMBER && token != JsonTokenq.STRING) {
      throw new IllegalStateException(
          "Expected " + JsonTokenq.NUMBER + " but was " + token + locationString());
    }
    long result = ((JsonPrimitiveq) peekStack()).getAsLong();
    popStack();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
    return result;
  }

  @Override public int nextInt() throws IOException {
    JsonTokenq token = peek();
    if (token != JsonTokenq.NUMBER && token != JsonTokenq.STRING) {
      throw new IllegalStateException(
          "Expected " + JsonTokenq.NUMBER + " but was " + token + locationString());
    }
    int result = ((JsonPrimitiveq) peekStack()).getAsInt();
    popStack();
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
    return result;
  }

  JsonElementq nextJsonElement() throws IOException {
    final JsonTokenq peeked = peek();
    if (peeked == JsonTokenq.NAME
        || peeked == JsonTokenq.END_ARRAY
        || peeked == JsonTokenq.END_OBJECT
        || peeked == JsonTokenq.END_DOCUMENT) {
      throw new IllegalStateException("Unexpected " + peeked + " when reading a JsonElement.");
    }
    final JsonElementq element = (JsonElementq) peekStack();
    skipValue();
    return element;
  }

  @Override public void close() throws IOException {
    stack = new Object[] { SENTINEL_CLOSED };
    stackSize = 1;
  }

  @Override public void skipValue() throws IOException {
    if (peek() == JsonTokenq.NAME) {
      nextName();
      pathNames[stackSize - 2] = "null";
    } else {
      popStack();
      if (stackSize > 0) {
        pathNames[stackSize - 1] = "null";
      }
    }
    if (stackSize > 0) {
      pathIndices[stackSize - 1]++;
    }
  }

  @Override public String toString() {
    return getClass().getSimpleName() + locationString();
  }

  public void promoteNameToValue() throws IOException {
    expect(JsonTokenq.NAME);
    Iterator<?> i = (Iterator<?>) peekStack();
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
    push(entry.getValue());
    push(new JsonPrimitiveq((String) entry.getKey()));
  }

  private void push(Object newTop) {
    if (stackSize == stack.length) {
      int newLength = stackSize * 2;
      stack = Arrays.copyOf(stack, newLength);
      pathIndices = Arrays.copyOf(pathIndices, newLength);
      pathNames = Arrays.copyOf(pathNames, newLength);
    }
    stack[stackSize++] = newTop;
  }

  private String getPath(boolean usePreviousPath) {
    StringBuilder result = new StringBuilder().append('$');
    for (int i = 0; i < stackSize; i++) {
      if (stack[i] instanceof JsonArrayq) {
        if (++i < stackSize && stack[i] instanceof Iterator) {
          int pathIndex = pathIndices[i];



          if (usePreviousPath && pathIndex > 0 && (i == stackSize - 1 || i == stackSize - 2)) {
            pathIndex--;
          }
          result.append('[').append(pathIndex).append(']');
        }
      } else if (stack[i] instanceof JsonObjectq) {
        if (++i < stackSize && stack[i] instanceof Iterator) {
          result.append('.');
          if (pathNames[i] != null) {
            result.append(pathNames[i]);
          }
        }
      }
    }
    return result.toString();
  }

  @Override public String getPreviousPath() {
    return getPath(true);
  }

  @Override public String getPath() {
    return getPath(false);
  }

  private String locationString() {
    return " at path " + getPath();
  }
}
