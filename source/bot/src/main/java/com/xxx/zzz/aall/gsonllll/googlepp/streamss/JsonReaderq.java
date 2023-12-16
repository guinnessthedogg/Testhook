

package com.xxx.zzz.aall.gsonllll.googlepp.streamss;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.JsonReaderInternalAccessq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.JsonTreeReaderqq;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;


public class JsonReaderq implements Closeable {
  private static final long MIN_INCOMPLETE_INTEGER = Long.MIN_VALUE / 10;

  private static final int PEEKED_NONE = 0;
  private static final int PEEKED_BEGIN_OBJECT = 1;
  private static final int PEEKED_END_OBJECT = 2;
  private static final int PEEKED_BEGIN_ARRAY = 3;
  private static final int PEEKED_END_ARRAY = 4;
  private static final int PEEKED_TRUE = 5;
  private static final int PEEKED_FALSE = 6;
  private static final int PEEKED_NULL = 7;
  private static final int PEEKED_SINGLE_QUOTED = 8;
  private static final int PEEKED_DOUBLE_QUOTED = 9;
  private static final int PEEKED_UNQUOTED = 10;

  private static final int PEEKED_BUFFERED = 11;
  private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
  private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
  private static final int PEEKED_UNQUOTED_NAME = 14;

  private static final int PEEKED_LONG = 15;
  private static final int PEEKED_NUMBER = 16;
  private static final int PEEKED_EOF = 17;


  private static final int NUMBER_CHAR_NONE = 0;
  private static final int NUMBER_CHAR_SIGN = 1;
  private static final int NUMBER_CHAR_DIGIT = 2;
  private static final int NUMBER_CHAR_DECIMAL = 3;
  private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
  private static final int NUMBER_CHAR_EXP_E = 5;
  private static final int NUMBER_CHAR_EXP_SIGN = 6;
  private static final int NUMBER_CHAR_EXP_DIGIT = 7;


  private final Reader in;


  private boolean lenient = false;

  static final int BUFFER_SIZE = 1024;

  private final char[] buffer = new char[BUFFER_SIZE];
  private int pos = 0;
  private int limit = 0;

  private int lineNumber = 0;
  private int lineStart = 0;

  int peeked = PEEKED_NONE;


  private long peekedLong;


  private int peekedNumberLength;


  private String peekedString;


  private int[] stack = new int[32];
  private int stackSize = 0;
  {
    stack[stackSize++] = JsonScopeq.EMPTY_DOCUMENT;
  }


  private String[] pathNames = new String[32];
  private int[] pathIndices = new int[32];


  public JsonReaderq(Reader in) {
    if (in == null) {
      throw new NullPointerException("in == null");
    }
    this.in = in;
  }


  public final void setLenient(boolean lenient) {
    this.lenient = lenient;
  }


  public final boolean isLenient() {
    return lenient;
  }


  public void beginArray() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_BEGIN_ARRAY) {
      push(JsonScopeq.EMPTY_ARRAY);
      pathIndices[stackSize - 1] = 0;
      peeked = PEEKED_NONE;
    } else {
      throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString());
    }
  }


  public void endArray() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_END_ARRAY) {
      stackSize--;
      pathIndices[stackSize - 1]++;
      peeked = PEEKED_NONE;
    } else {
      throw new IllegalStateException("Expected END_ARRAY but was " + peek() + locationString());
    }
  }


  public void beginObject() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_BEGIN_OBJECT) {
      push(JsonScopeq.EMPTY_OBJECT);
      peeked = PEEKED_NONE;
    } else {
      throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + locationString());
    }
  }


  public void endObject() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_END_OBJECT) {
      stackSize--;
      pathNames[stackSize] = null;
      pathIndices[stackSize - 1]++;
      peeked = PEEKED_NONE;
    } else {
      throw new IllegalStateException("Expected END_OBJECT but was " + peek() + locationString());
    }
  }


  public boolean hasNext() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    return p != PEEKED_END_OBJECT && p != PEEKED_END_ARRAY && p != PEEKED_EOF;
  }


  public JsonTokenq peek() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    switch (p) {
    case PEEKED_BEGIN_OBJECT:
      return JsonTokenq.BEGIN_OBJECT;
    case PEEKED_END_OBJECT:
      return JsonTokenq.END_OBJECT;
    case PEEKED_BEGIN_ARRAY:
      return JsonTokenq.BEGIN_ARRAY;
    case PEEKED_END_ARRAY:
      return JsonTokenq.END_ARRAY;
    case PEEKED_SINGLE_QUOTED_NAME:
    case PEEKED_DOUBLE_QUOTED_NAME:
    case PEEKED_UNQUOTED_NAME:
      return JsonTokenq.NAME;
    case PEEKED_TRUE:
    case PEEKED_FALSE:
      return JsonTokenq.BOOLEAN;
    case PEEKED_NULL:
      return JsonTokenq.NULL;
    case PEEKED_SINGLE_QUOTED:
    case PEEKED_DOUBLE_QUOTED:
    case PEEKED_UNQUOTED:
    case PEEKED_BUFFERED:
      return JsonTokenq.STRING;
    case PEEKED_LONG:
    case PEEKED_NUMBER:
      return JsonTokenq.NUMBER;
    case PEEKED_EOF:
      return JsonTokenq.END_DOCUMENT;
    default:
      throw new AssertionError();
    }
  }

  int doPeek() throws IOException {
    int peekStack = stack[stackSize - 1];
    if (peekStack == JsonScopeq.EMPTY_ARRAY) {
      stack[stackSize - 1] = JsonScopeq.NONEMPTY_ARRAY;
    } else if (peekStack == JsonScopeq.NONEMPTY_ARRAY) {

      int c = nextNonWhitespace(true);
      switch (c) {
      case ']':
        return peeked = PEEKED_END_ARRAY;
      case ';':
        checkLenient();
      case ',':
        break;
      default:
        throw syntaxError("Unterminated array");
      }
    } else if (peekStack == JsonScopeq.EMPTY_OBJECT || peekStack == JsonScopeq.NONEMPTY_OBJECT) {
      stack[stackSize - 1] = JsonScopeq.DANGLING_NAME;

      if (peekStack == JsonScopeq.NONEMPTY_OBJECT) {
        int c = nextNonWhitespace(true);
        switch (c) {
        case '}':
          return peeked = PEEKED_END_OBJECT;
        case ';':
          checkLenient();
        case ',':
          break;
        default:
          throw syntaxError("Unterminated object");
        }
      }
      int c = nextNonWhitespace(true);
      switch (c) {
      case '"':
        return peeked = PEEKED_DOUBLE_QUOTED_NAME;
      case '\'':
        checkLenient();
        return peeked = PEEKED_SINGLE_QUOTED_NAME;
      case '}':
        if (peekStack != JsonScopeq.NONEMPTY_OBJECT) {
          return peeked = PEEKED_END_OBJECT;
        } else {
          throw syntaxError("Expected name");
        }
      default:
        checkLenient();
        pos--;
        if (isLiteral((char) c)) {
          return peeked = PEEKED_UNQUOTED_NAME;
        } else {
          throw syntaxError("Expected name");
        }
      }
    } else if (peekStack == JsonScopeq.DANGLING_NAME) {
      stack[stackSize - 1] = JsonScopeq.NONEMPTY_OBJECT;

      int c = nextNonWhitespace(true);
      switch (c) {
      case ':':
        break;
      case '=':
        checkLenient();
        if ((pos < limit || fillBuffer(1)) && buffer[pos] == '>') {
          pos++;
        }
        break;
      default:
        throw syntaxError("Expected ':'");
      }
    } else if (peekStack == JsonScopeq.EMPTY_DOCUMENT) {
      if (lenient) {
        consumeNonExecutePrefix();
      }
      stack[stackSize - 1] = JsonScopeq.NONEMPTY_DOCUMENT;
    } else if (peekStack == JsonScopeq.NONEMPTY_DOCUMENT) {
      int c = nextNonWhitespace(false);
      if (c == -1) {
        return peeked = PEEKED_EOF;
      } else {
        checkLenient();
        pos--;
      }
    } else if (peekStack == JsonScopeq.CLOSED) {
      throw new IllegalStateException("JsonReader is closed");
    }

    int c = nextNonWhitespace(true);
    switch (c) {
    case ']':
      if (peekStack == JsonScopeq.EMPTY_ARRAY) {
        return peeked = PEEKED_END_ARRAY;
      }

    case ';':
    case ',':

      if (peekStack == JsonScopeq.EMPTY_ARRAY || peekStack == JsonScopeq.NONEMPTY_ARRAY) {
        checkLenient();
        pos--;
        return peeked = PEEKED_NULL;
      } else {
        throw syntaxError("Unexpected value");
      }
    case '\'':
      checkLenient();
      return peeked = PEEKED_SINGLE_QUOTED;
    case '"':
      return peeked = PEEKED_DOUBLE_QUOTED;
    case '[':
      return peeked = PEEKED_BEGIN_ARRAY;
    case '{':
      return peeked = PEEKED_BEGIN_OBJECT;
    default:
      pos--;
    }

    int result = peekKeyword();
    if (result != PEEKED_NONE) {
      return result;
    }

    result = peekNumber();
    if (result != PEEKED_NONE) {
      return result;
    }

    if (!isLiteral(buffer[pos])) {
      throw syntaxError("Expected value");
    }

    checkLenient();
    return peeked = PEEKED_UNQUOTED;
  }

  private int peekKeyword() throws IOException {

    char c = buffer[pos];
    String keyword;
    String keywordUpper;
    int peeking;
    if (c == 't' || c == 'T') {
      keyword = "true";
      keywordUpper = "TRUE";
      peeking = PEEKED_TRUE;
    } else if (c == 'f' || c == 'F') {
      keyword = "false";
      keywordUpper = "FALSE";
      peeking = PEEKED_FALSE;
    } else if (c == 'n' || c == 'N') {
      keyword = "null";
      keywordUpper = "NULL";
      peeking = PEEKED_NULL;
    } else {
      return PEEKED_NONE;
    }


    int length = keyword.length();
    for (int i = 1; i < length; i++) {
      if (pos + i >= limit && !fillBuffer(i + 1)) {
        return PEEKED_NONE;
      }
      c = buffer[pos + i];
      if (c != keyword.charAt(i) && c != keywordUpper.charAt(i)) {
        return PEEKED_NONE;
      }
    }

    if ((pos + length < limit || fillBuffer(length + 1))
        && isLiteral(buffer[pos + length])) {
      return PEEKED_NONE;
    }


    pos += length;
    return peeked = peeking;
  }

  private int peekNumber() throws IOException {

    char[] buffer = this.buffer;
    int p = pos;
    int l = limit;

    long value = 0;
    boolean negative = false;
    boolean fitsInLong = true;
    int last = NUMBER_CHAR_NONE;

    int i = 0;

    charactersOfNumber:
    for (; true; i++) {
      if (p + i == l) {
        if (i == buffer.length) {


          return PEEKED_NONE;
        }
        if (!fillBuffer(i + 1)) {
          break;
        }
        p = pos;
        l = limit;
      }

      char c = buffer[p + i];
      switch (c) {
      case '-':
        if (last == NUMBER_CHAR_NONE) {
          negative = true;
          last = NUMBER_CHAR_SIGN;
          continue;
        } else if (last == NUMBER_CHAR_EXP_E) {
          last = NUMBER_CHAR_EXP_SIGN;
          continue;
        }
        return PEEKED_NONE;

      case '+':
        if (last == NUMBER_CHAR_EXP_E) {
          last = NUMBER_CHAR_EXP_SIGN;
          continue;
        }
        return PEEKED_NONE;

      case 'e':
      case 'E':
        if (last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_FRACTION_DIGIT) {
          last = NUMBER_CHAR_EXP_E;
          continue;
        }
        return PEEKED_NONE;

      case '.':
        if (last == NUMBER_CHAR_DIGIT) {
          last = NUMBER_CHAR_DECIMAL;
          continue;
        }
        return PEEKED_NONE;

      default:
        if (c < '0' || c > '9') {
          if (!isLiteral(c)) {
            break charactersOfNumber;
          }
          return PEEKED_NONE;
        }
        if (last == NUMBER_CHAR_SIGN || last == NUMBER_CHAR_NONE) {
          value = -(c - '0');
          last = NUMBER_CHAR_DIGIT;
        } else if (last == NUMBER_CHAR_DIGIT) {
          if (value == 0) {
            return PEEKED_NONE;
          }
          long newValue = value * 10 - (c - '0');
          fitsInLong &= value > MIN_INCOMPLETE_INTEGER
              || (value == MIN_INCOMPLETE_INTEGER && newValue < value);
          value = newValue;
        } else if (last == NUMBER_CHAR_DECIMAL) {
          last = NUMBER_CHAR_FRACTION_DIGIT;
        } else if (last == NUMBER_CHAR_EXP_E || last == NUMBER_CHAR_EXP_SIGN) {
          last = NUMBER_CHAR_EXP_DIGIT;
        }
      }
    }


    if (last == NUMBER_CHAR_DIGIT && fitsInLong && (value != Long.MIN_VALUE || negative) && (value!=0 || false==negative)) {
      peekedLong = negative ? value : -value;
      pos += i;
      return peeked = PEEKED_LONG;
    } else if (last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_FRACTION_DIGIT
        || last == NUMBER_CHAR_EXP_DIGIT) {
      peekedNumberLength = i;
      return peeked = PEEKED_NUMBER;
    } else {
      return PEEKED_NONE;
    }
  }

  private boolean isLiteral(char c) throws IOException {
    switch (c) {
    case '/':
    case '\\':
    case ';':
    case '#':
    case '=':
      checkLenient();
    case '{':
    case '}':
    case '[':
    case ']':
    case ':':
    case ',':
    case ' ':
    case '\t':
    case '\f':
    case '\r':
    case '\n':
      return false;
    default:
      return true;
    }
  }


  public String nextName() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    String result;
    if (p == PEEKED_UNQUOTED_NAME) {
      result = nextUnquotedValue();
    } else if (p == PEEKED_SINGLE_QUOTED_NAME) {
      result = nextQuotedValue('\'');
    } else if (p == PEEKED_DOUBLE_QUOTED_NAME) {
      result = nextQuotedValue('"');
    } else {
      throw new IllegalStateException("Expected a name but was " + peek() + locationString());
    }
    peeked = PEEKED_NONE;
    pathNames[stackSize - 1] = result;
    return result;
  }


  public String nextString() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    String result;
    if (p == PEEKED_UNQUOTED) {
      result = nextUnquotedValue();
    } else if (p == PEEKED_SINGLE_QUOTED) {
      result = nextQuotedValue('\'');
    } else if (p == PEEKED_DOUBLE_QUOTED) {
      result = nextQuotedValue('"');
    } else if (p == PEEKED_BUFFERED) {
      result = peekedString;
      peekedString = null;
    } else if (p == PEEKED_LONG) {
      result = Long.toString(peekedLong);
    } else if (p == PEEKED_NUMBER) {
      result = new String(buffer, pos, peekedNumberLength);
      pos += peekedNumberLength;
    } else {
      throw new IllegalStateException("Expected a string but was " + peek() + locationString());
    }
    peeked = PEEKED_NONE;
    pathIndices[stackSize - 1]++;
    return result;
  }


  public boolean nextBoolean() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_TRUE) {
      peeked = PEEKED_NONE;
      pathIndices[stackSize - 1]++;
      return true;
    } else if (p == PEEKED_FALSE) {
      peeked = PEEKED_NONE;
      pathIndices[stackSize - 1]++;
      return false;
    }
    throw new IllegalStateException("Expected a boolean but was " + peek() + locationString());
  }


  public void nextNull() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_NULL) {
      peeked = PEEKED_NONE;
      pathIndices[stackSize - 1]++;
    } else {
      throw new IllegalStateException("Expected null but was " + peek() + locationString());
    }
  }


  public double nextDouble() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    if (p == PEEKED_LONG) {
      peeked = PEEKED_NONE;
      pathIndices[stackSize - 1]++;
      return (double) peekedLong;
    }

    if (p == PEEKED_NUMBER) {
      peekedString = new String(buffer, pos, peekedNumberLength);
      pos += peekedNumberLength;
    } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED) {
      peekedString = nextQuotedValue(p == PEEKED_SINGLE_QUOTED ? '\'' : '"');
    } else if (p == PEEKED_UNQUOTED) {
      peekedString = nextUnquotedValue();
    } else if (p != PEEKED_BUFFERED) {
      throw new IllegalStateException("Expected a double but was " + peek() + locationString());
    }

    peeked = PEEKED_BUFFERED;
    double result = Double.parseDouble(peekedString);
    if (!lenient && (Double.isNaN(result) || Double.isInfinite(result))) {
      throw new MalformedJsonExceptionq(
          "JSON forbids NaN and infinities: " + result + locationString());
    }
    peekedString = null;
    peeked = PEEKED_NONE;
    pathIndices[stackSize - 1]++;
    return result;
  }


  public long nextLong() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    if (p == PEEKED_LONG) {
      peeked = PEEKED_NONE;
      pathIndices[stackSize - 1]++;
      return peekedLong;
    }

    if (p == PEEKED_NUMBER) {
      peekedString = new String(buffer, pos, peekedNumberLength);
      pos += peekedNumberLength;
    } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED || p == PEEKED_UNQUOTED) {
      if (p == PEEKED_UNQUOTED) {
        peekedString = nextUnquotedValue();
      } else {
        peekedString = nextQuotedValue(p == PEEKED_SINGLE_QUOTED ? '\'' : '"');
      }
      try {
        long result = Long.parseLong(peekedString);
        peeked = PEEKED_NONE;
        pathIndices[stackSize - 1]++;
        return result;
      } catch (NumberFormatException ignored) {

      }
    } else {
      throw new IllegalStateException("Expected a long but was " + peek() + locationString());
    }

    peeked = PEEKED_BUFFERED;
    double asDouble = Double.parseDouble(peekedString);
    long result = (long) asDouble;
    if (result != asDouble) {
      throw new NumberFormatException("Expected a long but was " + peekedString + locationString());
    }
    peekedString = null;
    peeked = PEEKED_NONE;
    pathIndices[stackSize - 1]++;
    return result;
  }


  private String nextQuotedValue(char quote) throws IOException {

    char[] buffer = this.buffer;
    StringBuilder builder = null;
    while (true) {
      int p = pos;
      int l = limit;
      
      int start = p;
      while (p < l) {
        int c = buffer[p++];

        if (c == quote) {
          pos = p;
          int len = p - start - 1;
          if (builder == null) {
            return new String(buffer, start, len);
          } else {
            builder.append(buffer, start, len);
            return builder.toString();
          }
        } else if (c == '\\') {
          pos = p;
          int len = p - start - 1;
          if (builder == null) {
            int estimatedLength = (len + 1) * 2;
            builder = new StringBuilder(Math.max(estimatedLength, 16));
          }
          builder.append(buffer, start, len);
          builder.append(readEscapeCharacter());
          p = pos;
          l = limit;
          start = p;
        } else if (c == '\n') {
          lineNumber++;
          lineStart = p;
        }
      }

      if (builder == null) {
        int estimatedLength = (p - start) * 2;
        builder = new StringBuilder(Math.max(estimatedLength, 16));
      }
      builder.append(buffer, start, p - start);
      pos = p;
      if (!fillBuffer(1)) {
        throw syntaxError("Unterminated string");
      }
    }
  }

  
  @SuppressWarnings("fallthrough")
  private String nextUnquotedValue() throws IOException {
    StringBuilder builder = null;
    int i = 0;

    findNonLiteralCharacter:
    while (true) {
      for (; pos + i < limit; i++) {
        switch (buffer[pos + i]) {
        case '/':
        case '\\':
        case ';':
        case '#':
        case '=':
          checkLenient();
        case '{':
        case '}':
        case '[':
        case ']':
        case ':':
        case ',':
        case ' ':
        case '\t':
        case '\f':
        case '\r':
        case '\n':
          break findNonLiteralCharacter;
        }
      }


      if (i < buffer.length) {
        if (fillBuffer(i + 1)) {
          continue;
        } else {
          break;
        }
      }


      if (builder == null) {
        builder = new StringBuilder(Math.max(i,16));
      }
      builder.append(buffer, pos, i);
      pos += i;
      i = 0;
      if (!fillBuffer(1)) {
        break;
      }
    }

    String result = (null == builder) ? new String(buffer, pos, i) : builder.append(buffer, pos, i).toString();
    pos += i;
    return result;
  }

  private void skipQuotedValue(char quote) throws IOException {

    char[] buffer = this.buffer;
    do {
      int p = pos;
      int l = limit;
      
      while (p < l) {
        int c = buffer[p++];
        if (c == quote) {
          pos = p;
          return;
        } else if (c == '\\') {
          pos = p;
          readEscapeCharacter();
          p = pos;
          l = limit;
        } else if (c == '\n') {
          lineNumber++;
          lineStart = p;
        }
      }
      pos = p;
    } while (fillBuffer(1));
    throw syntaxError("Unterminated string");
  }

  private void skipUnquotedValue() throws IOException {
    do {
      int i = 0;
      for (; pos + i < limit; i++) {
        switch (buffer[pos + i]) {
        case '/':
        case '\\':
        case ';':
        case '#':
        case '=':
          checkLenient();
        case '{':
        case '}':
        case '[':
        case ']':
        case ':':
        case ',':
        case ' ':
        case '\t':
        case '\f':
        case '\r':
        case '\n':
          pos += i;
          return;
        }
      }
      pos += i;
    } while (fillBuffer(1));
  }

  
  public int nextInt() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    int result;
    if (p == PEEKED_LONG) {
      result = (int) peekedLong;
      if (peekedLong != result) {
        throw new NumberFormatException("Expected an int but was " + peekedLong + locationString());
      }
      peeked = PEEKED_NONE;
      pathIndices[stackSize - 1]++;
      return result;
    }

    if (p == PEEKED_NUMBER) {
      peekedString = new String(buffer, pos, peekedNumberLength);
      pos += peekedNumberLength;
    } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED || p == PEEKED_UNQUOTED) {
      if (p == PEEKED_UNQUOTED) {
        peekedString = nextUnquotedValue();
      } else {
        peekedString = nextQuotedValue(p == PEEKED_SINGLE_QUOTED ? '\'' : '"');
      }
      try {
        result = Integer.parseInt(peekedString);
        peeked = PEEKED_NONE;
        pathIndices[stackSize - 1]++;
        return result;
      } catch (NumberFormatException ignored) {

      }
    } else {
      throw new IllegalStateException("Expected an int but was " + peek() + locationString());
    }

    peeked = PEEKED_BUFFERED;
    double asDouble = Double.parseDouble(peekedString);
    result = (int) asDouble;
    if (result != asDouble) {
      throw new NumberFormatException("Expected an int but was " + peekedString + locationString());
    }
    peekedString = null;
    peeked = PEEKED_NONE;
    pathIndices[stackSize - 1]++;
    return result;
  }

  
  public void close() throws IOException {
    peeked = PEEKED_NONE;
    stack[0] = JsonScopeq.CLOSED;
    stackSize = 1;
    in.close();
  }

  
  public void skipValue() throws IOException {
    int count = 0;
    do {
      int p = peeked;
      if (p == PEEKED_NONE) {
        p = doPeek();
      }

      if (p == PEEKED_BEGIN_ARRAY) {
        push(JsonScopeq.EMPTY_ARRAY);
        count++;
      } else if (p == PEEKED_BEGIN_OBJECT) {
        push(JsonScopeq.EMPTY_OBJECT);
        count++;
      } else if (p == PEEKED_END_ARRAY) {
        stackSize--;
        count--;
      } else if (p == PEEKED_END_OBJECT) {
        stackSize--;
        count--;
      } else if (p == PEEKED_UNQUOTED_NAME || p == PEEKED_UNQUOTED) {
        skipUnquotedValue();
      } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_SINGLE_QUOTED_NAME) {
        skipQuotedValue('\'');
      } else if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_DOUBLE_QUOTED_NAME) {
        skipQuotedValue('"');
      } else if (p == PEEKED_NUMBER) {
        pos += peekedNumberLength;
      }
      peeked = PEEKED_NONE;
    } while (count != 0);

    pathIndices[stackSize - 1]++;
    pathNames[stackSize - 1] = "null";
  }

  private void push(int newTop) {
    if (stackSize == stack.length) {
      int newLength = stackSize * 2;
      stack = Arrays.copyOf(stack, newLength);
      pathIndices = Arrays.copyOf(pathIndices, newLength);
      pathNames = Arrays.copyOf(pathNames, newLength);
    }
    stack[stackSize++] = newTop;
  }

  
  private boolean fillBuffer(int minimum) throws IOException {
    char[] buffer = this.buffer;
    lineStart -= pos;
    if (limit != pos) {
      limit -= pos;
      System.arraycopy(buffer, pos, buffer, 0, limit);
    } else {
      limit = 0;
    }

    pos = 0;
    int total;
    while ((total = in.read(buffer, limit, buffer.length - limit)) != -1) {
      limit += total;


      if (lineNumber == 0 && lineStart == 0 && limit > 0 && buffer[0] == '\ufeff') {
        pos++;
        lineStart++;
        minimum++;
      }

      if (limit >= minimum) {
        return true;
      }
    }
    return false;
  }

  
  private int nextNonWhitespace(boolean throwOnEof) throws IOException {
    
    char[] buffer = this.buffer;
    int p = pos;
    int l = limit;
    while (true) {
      if (p == l) {
        pos = p;
        if (!fillBuffer(1)) {
          break;
        }
        p = pos;
        l = limit;
      }

      int c = buffer[p++];
      if (c == '\n') {
        lineNumber++;
        lineStart = p;
        continue;
      } else if (c == ' ' || c == '\r' || c == '\t') {
        continue;
      }

      if (c == '/') {
        pos = p;
        if (p == l) {
          pos--;
          boolean charsLoaded = fillBuffer(2);
          pos++;
          if (!charsLoaded) {
            return c;
          }
        }

        checkLenient();
        char peek = buffer[pos];
        switch (peek) {
        case '*':

          pos++;
          if (!skipTo("*/")) {
            throw syntaxError("Unterminated comment");
          }
          p = pos + 2;
          l = limit;
          continue;

        case '/':

          pos++;
          skipToEndOfLine();
          p = pos;
          l = limit;
          continue;

        default:
          return c;
        }
      } else if (c == '#') {
        pos = p;
        
        checkLenient();
        skipToEndOfLine();
        p = pos;
        l = limit;
      } else {
        pos = p;
        return c;
      }
    }
    if (throwOnEof) {
      throw new EOFException("End of input" + locationString());
    } else {
      return -1;
    }
  }

  private void checkLenient() throws IOException {
    if (!lenient) {
      throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
    }
  }

  
  private void skipToEndOfLine() throws IOException {
    while (pos < limit || fillBuffer(1)) {
      char c = buffer[pos++];
      if (c == '\n') {
        lineNumber++;
        lineStart = pos;
        break;
      } else if (c == '\r') {
        break;
      }
    }
  }

  
  private boolean skipTo(String toFind) throws IOException {
    int length = toFind.length();
    outer:
    for (; pos + length <= limit || fillBuffer(length); pos++) {
      if (buffer[pos] == '\n') {
        lineNumber++;
        lineStart = pos + 1;
        continue;
      }
      for (int c = 0; c < length; c++) {
        if (buffer[pos + c] != toFind.charAt(c)) {
          continue outer;
        }
      }
      return true;
    }
    return false;
  }

  @Override public String toString() {
    return getClass().getSimpleName() + locationString();
  }

  String locationString() {
    int line = lineNumber + 1;
    int column = pos - lineStart + 1;
    return " at line " + line + " column " + column + " path " + getPath();
  }

  private String getPath(boolean usePreviousPath) {
    StringBuilder result = new StringBuilder().append('$');
    for (int i = 0; i < stackSize; i++) {
      switch (stack[i]) {
        case JsonScopeq.EMPTY_ARRAY:
        case JsonScopeq.NONEMPTY_ARRAY:
          int pathIndex = pathIndices[i];

          if (usePreviousPath && pathIndex > 0 && i == stackSize - 1) {
            pathIndex--;
          }
          result.append('[').append(pathIndex).append(']');
          break;
        case JsonScopeq.EMPTY_OBJECT:
        case JsonScopeq.DANGLING_NAME:
        case JsonScopeq.NONEMPTY_OBJECT:
          result.append('.');
          if (pathNames[i] != null) {
            result.append(pathNames[i]);
          }
          break;
        case JsonScopeq.NONEMPTY_DOCUMENT:
        case JsonScopeq.EMPTY_DOCUMENT:
        case JsonScopeq.CLOSED:
          break;
      }
    }
    return result.toString();
  }

  
  public String getPreviousPath() {
    return getPath(true);
  }

  
  public String getPath() {
    return getPath(false);
  }

  
  private char readEscapeCharacter() throws IOException {
    if (pos == limit && !fillBuffer(1)) {
      throw syntaxError("Unterminated escape sequence");
    }

    char escaped = buffer[pos++];
    switch (escaped) {
    case 'u':
      if (pos + 4 > limit && !fillBuffer(4)) {
        throw syntaxError("Unterminated escape sequence");
      }

      char result = 0;
      for (int i = pos, end = i + 4; i < end; i++) {
        char c = buffer[i];
        result <<= 4;
        if (c >= '0' && c <= '9') {
          result += (c - '0');
        } else if (c >= 'a' && c <= 'f') {
          result += (c - 'a' + 10);
        } else if (c >= 'A' && c <= 'F') {
          result += (c - 'A' + 10);
        } else {
          throw new NumberFormatException("\\u" + new String(buffer, pos, 4));
        }
      }
      pos += 4;
      return result;

    case 't':
      return '\t';

    case 'b':
      return '\b';

    case 'n':
      return '\n';

    case 'r':
      return '\r';

    case 'f':
      return '\f';

    case '\n':
      lineNumber++;
      lineStart = pos;


    case '\'':
    case '"':
    case '\\':
    case '/':
      return escaped;
    default:

      throw syntaxError("Invalid escape sequence");
    }
  }

  
  private IOException syntaxError(String message) throws IOException {
    throw new MalformedJsonExceptionq(message + locationString());
  }

  
  private void consumeNonExecutePrefix() throws IOException {

    nextNonWhitespace(true);
    pos--;

    if (pos + 5 > limit && !fillBuffer(5)) {
      return;
    }

    int p = pos;
    char[] buf = buffer;
    if(buf[p] != ')' || buf[p + 1] != ']' || buf[p + 2] != '}' || buf[p + 3] != '\'' || buf[p + 4] != '\n') {
      return;
    }


    pos += 5;
  }

  static {
    JsonReaderInternalAccessq.INSTANCE = new JsonReaderInternalAccessq() {
      @Override public void promoteNameToValue(JsonReaderq reader) throws IOException {
        if (reader instanceof JsonTreeReaderqq) {
          ((JsonTreeReaderqq)reader).promoteNameToValue();
          return;
        }
        int p = reader.peeked;
        if (p == PEEKED_NONE) {
          p = reader.doPeek();
        }
        if (p == PEEKED_DOUBLE_QUOTED_NAME) {
          reader.peeked = PEEKED_DOUBLE_QUOTED;
        } else if (p == PEEKED_SINGLE_QUOTED_NAME) {
          reader.peeked = PEEKED_SINGLE_QUOTED;
        } else if (p == PEEKED_UNQUOTED_NAME) {
          reader.peeked = PEEKED_UNQUOTED;
        } else {
          throw new IllegalStateException(
              "Expected a name but was " + reader.peek() + reader.locationString());
        }
      }
    };
  }
}
