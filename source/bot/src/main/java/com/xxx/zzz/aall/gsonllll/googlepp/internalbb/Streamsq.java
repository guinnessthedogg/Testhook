

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb;

import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonElementq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonIOExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonNullq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonParseExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.TypeAdaptersqq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.MalformedJsonExceptionq;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;


public final class Streamsq {
  private Streamsq() {
    throw new UnsupportedOperationException();
  }


  public static JsonElementq parse(JsonReaderq reader) throws JsonParseExceptionq {
    boolean isEmpty = true;
    try {
      reader.peek();
      isEmpty = false;
      return TypeAdaptersqq.JSON_ELEMENT.read(reader);
    } catch (EOFException e) {

      if (isEmpty) {
        return JsonNullq.INSTANCE;
      }

      throw new JsonSyntaxExceptionq(e);
    } catch (MalformedJsonExceptionq e) {
      throw new JsonSyntaxExceptionq(e);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxExceptionq(e);
    }
  }


  public static void write(JsonElementq element, JsonWriterq writer) throws IOException {
    TypeAdaptersqq.JSON_ELEMENT.write(writer, element);
  }

  public static Writer writerForAppendable(Appendable appendable) {
    return appendable instanceof Writer ? (Writer) appendable : new AppendableWriter(appendable);
  }


  private static final class AppendableWriter extends Writer {
    private final Appendable appendable;
    private final CurrentWrite currentWrite = new CurrentWrite();

    AppendableWriter(Appendable appendable) {
      this.appendable = appendable;
    }

    @Override public void write(char[] chars, int offset, int length) throws IOException {
      currentWrite.chars = chars;
      appendable.append(currentWrite, offset, offset + length);
    }

    @Override public void write(int i) throws IOException {
      appendable.append((char) i);
    }

    @Override public void flush() {}
    @Override public void close() {}


    static class CurrentWrite implements CharSequence {
      char[] chars;
      public int length() {
        return chars.length;
      }
      public char charAt(int i) {
        return chars[i];
      }
      public CharSequence subSequence(int start, int end) {
        return new String(chars, start, end - start);
      }
    }
  }

}
