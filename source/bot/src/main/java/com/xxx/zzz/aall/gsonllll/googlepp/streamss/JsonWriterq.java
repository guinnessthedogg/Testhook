

package com.xxx.zzz.aall.gsonllll.googlepp.streamss;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;


public class JsonWriterq implements Closeable, Flushable {


  private static final Pattern VALID_JSON_NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");

  
  private static final String[] REPLACEMENT_CHARS;
  private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
  static {
    REPLACEMENT_CHARS = new String[128];
    for (int i = 0; i <= 0x1f; i++) {
      REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
    }
    REPLACEMENT_CHARS['"'] = "\\\"";
    REPLACEMENT_CHARS['\\'] = "\\\\";
    REPLACEMENT_CHARS['\t'] = "\\t";
    REPLACEMENT_CHARS['\b'] = "\\b";
    REPLACEMENT_CHARS['\n'] = "\\n";
    REPLACEMENT_CHARS['\r'] = "\\r";
    REPLACEMENT_CHARS['\f'] = "\\f";
    HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
    HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
    HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
    HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
    HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
    HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
  }

  
  private final Writer out;

  private int[] stack = new int[32];
  private int stackSize = 0;
  {
    push(JsonScopeq.EMPTY_DOCUMENT);
  }

  
  private String indent;

  
  private String separator = ":";

  private boolean lenient;

  private boolean htmlSafe;

  private String deferredName;

  private boolean serializeNulls = true;

  
  public JsonWriterq(Writer out) {
    if (out == null) {
      throw new NullPointerException("out == null");
    }
    this.out = out;
  }

  
  public final void setIndent(String indent) {
    if (indent.length() == 0) {
      this.indent = null;
      this.separator = ":";
    } else {
      this.indent = indent;
      this.separator = ": ";
    }
  }

  
  public final void setLenient(boolean lenient) {
    this.lenient = lenient;
  }

  
  public boolean isLenient() {
    return lenient;
  }

  
  public final void setHtmlSafe(boolean htmlSafe) {
    this.htmlSafe = htmlSafe;
  }

  
  public final boolean isHtmlSafe() {
    return htmlSafe;
  }

  
  public final void setSerializeNulls(boolean serializeNulls) {
    this.serializeNulls = serializeNulls;
  }

  
  public final boolean getSerializeNulls() {
    return serializeNulls;
  }

  
  public JsonWriterq beginArray() throws IOException {
    writeDeferredName();
    return open(JsonScopeq.EMPTY_ARRAY, '[');
  }

  
  public JsonWriterq endArray() throws IOException {
    return close(JsonScopeq.EMPTY_ARRAY, JsonScopeq.NONEMPTY_ARRAY, ']');
  }

  
  public JsonWriterq beginObject() throws IOException {
    writeDeferredName();
    return open(JsonScopeq.EMPTY_OBJECT, '{');
  }

  
  public JsonWriterq endObject() throws IOException {
    return close(JsonScopeq.EMPTY_OBJECT, JsonScopeq.NONEMPTY_OBJECT, '}');
  }

  
  private JsonWriterq open(int empty, char openBracket) throws IOException {
    beforeValue();
    push(empty);
    out.write(openBracket);
    return this;
  }

  
  private JsonWriterq close(int empty, int nonempty, char closeBracket)
      throws IOException {
    int context = peek();
    if (context != nonempty && context != empty) {
      throw new IllegalStateException("Nesting problem.");
    }
    if (deferredName != null) {
      throw new IllegalStateException("Dangling name: " + deferredName);
    }

    stackSize--;
    if (context == nonempty) {
      newline();
    }
    out.write(closeBracket);
    return this;
  }

  private void push(int newTop) {
    if (stackSize == stack.length) {
      stack = Arrays.copyOf(stack, stackSize * 2);
    }
    stack[stackSize++] = newTop;
  }

  
  private int peek() {
    if (stackSize == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    return stack[stackSize - 1];
  }

  
  private void replaceTop(int topOfStack) {
    stack[stackSize - 1] = topOfStack;
  }

  
  public JsonWriterq name(String name) throws IOException {
    if (name == null) {
      throw new NullPointerException("name == null");
    }
    if (deferredName != null) {
      throw new IllegalStateException();
    }
    if (stackSize == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    deferredName = name;
    return this;
  }

  private void writeDeferredName() throws IOException {
    if (deferredName != null) {
      beforeName();
      string(deferredName);
      deferredName = null;
    }
  }

  
  public JsonWriterq value(String value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    writeDeferredName();
    beforeValue();
    string(value);
    return this;
  }

  
  public JsonWriterq jsonValue(String value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    writeDeferredName();
    beforeValue();
    out.append(value);
    return this;
  }

  
  public JsonWriterq nullValue() throws IOException {
    if (deferredName != null) {
      if (serializeNulls) {
        writeDeferredName();
      } else {
        deferredName = null;
        return this;
      }
    }
    beforeValue();
    out.write("null");
    return this;
  }

  
  public JsonWriterq value(boolean value) throws IOException {
    writeDeferredName();
    beforeValue();
    out.write(value ? "true" : "false");
    return this;
  }

  
  public JsonWriterq value(Boolean value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    writeDeferredName();
    beforeValue();
    out.write(value ? "true" : "false");
    return this;
  }

  
  public JsonWriterq value(double value) throws IOException {
    writeDeferredName();
    if (!lenient && (Double.isNaN(value) || Double.isInfinite(value))) {
      throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
    }
    beforeValue();
    out.append(Double.toString(value));
    return this;
  }

  
  public JsonWriterq value(long value) throws IOException {
    writeDeferredName();
    beforeValue();
    out.write(Long.toString(value));
    return this;
  }

  
  private static boolean isTrustedNumberType(Class<? extends Number> c) {


    return c == Integer.class || c == Long.class || c == Double.class || c == Float.class || c == Byte.class || c == Short.class
        || c == BigDecimal.class || c == BigInteger.class || c == AtomicInteger.class || c == AtomicLong.class;
  }

  
  public JsonWriterq value(Number value) throws IOException {
    if (value == null) {
      return nullValue();
    }

    writeDeferredName();
    String string = value.toString();
    if (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN")) {
      if (!lenient) {
        throw new IllegalArgumentException("Numeric values must be finite, but was " + string);
      }
    } else {
      Class<? extends Number> numberClass = value.getClass();

      if (!isTrustedNumberType(numberClass) && !VALID_JSON_NUMBER_PATTERN.matcher(string).matches()) {
        throw new IllegalArgumentException("String created by " + numberClass + " is not a valid JSON number: " + string);
      }
    }

    beforeValue();
    out.append(string);
    return this;
  }

  
  public void flush() throws IOException {
    if (stackSize == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    out.flush();
  }

  
  public void close() throws IOException {
    out.close();

    int size = stackSize;
    if (size > 1 || size == 1 && stack[size - 1] != JsonScopeq.NONEMPTY_DOCUMENT) {
      throw new IOException("Incomplete document");
    }
    stackSize = 0;
  }

  private void string(String value) throws IOException {
    String[] replacements = htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
    out.write('\"');
    int last = 0;
    int length = value.length();
    for (int i = 0; i < length; i++) {
      char c = value.charAt(i);
      String replacement;
      if (c < 128) {
        replacement = replacements[c];
        if (replacement == null) {
          continue;
        }
      } else if (c == '\u2028') {
        replacement = "\\u2028";
      } else if (c == '\u2029') {
        replacement = "\\u2029";
      } else {
        continue;
      }
      if (last < i) {
        out.write(value, last, i - last);
      }
      out.write(replacement);
      last = i + 1;
    }
    if (last < length) {
      out.write(value, last, length - last);
    }
    out.write('\"');
  }

  private void newline() throws IOException {
    if (indent == null) {
      return;
    }

    out.write('\n');
    for (int i = 1, size = stackSize; i < size; i++) {
      out.write(indent);
    }
  }

  
  private void beforeName() throws IOException {
    int context = peek();
    if (context == JsonScopeq.NONEMPTY_OBJECT) {
      out.write(',');
    } else if (context != JsonScopeq.EMPTY_OBJECT) {
      throw new IllegalStateException("Nesting problem.");
    }
    newline();
    replaceTop(JsonScopeq.DANGLING_NAME);
  }

  
  @SuppressWarnings("fallthrough")
  private void beforeValue() throws IOException {
    switch (peek()) {
    case JsonScopeq.NONEMPTY_DOCUMENT:
      if (!lenient) {
        throw new IllegalStateException(
            "JSON must have only one top-level value.");
      }

    case JsonScopeq.EMPTY_DOCUMENT:
      replaceTop(JsonScopeq.NONEMPTY_DOCUMENT);
      break;

    case JsonScopeq.EMPTY_ARRAY:
      replaceTop(JsonScopeq.NONEMPTY_ARRAY);
      newline();
      break;

    case JsonScopeq.NONEMPTY_ARRAY:
      out.append(',');
      newline();
      break;

    case JsonScopeq.DANGLING_NAME:
      out.append(separator);
      replaceTop(JsonScopeq.NONEMPTY_OBJECT);
      break;

    default:
      throw new IllegalStateException("Nesting problem.");
    }
  }
}
