

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.JsonTreeReaderqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.JsonTreeWriterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;



public abstract class TypeAdapterqdscvvf<T> {

  
  public abstract void write(JsonWriterq out, T value) throws IOException;

  
  public final void toJson(Writer out, T value) throws IOException {
    JsonWriterq writer = new JsonWriterq(out);
    write(writer, value);
  }

  
  public final TypeAdapterqdscvvf<T> nullSafe() {
    return new TypeAdapterqdscvvf<T>() {
      @Override public void write(JsonWriterq out, T value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          TypeAdapterqdscvvf.this.write(out, value);
        }
      }
      @Override public T read(JsonReaderq reader) throws IOException {
        if (reader.peek() == JsonTokenq.NULL) {
          reader.nextNull();
          return null;
        }
        return TypeAdapterqdscvvf.this.read(reader);
      }
    };
  }

  
  public final String toJson(T value) {
    StringWriter stringWriter = new StringWriter();
    try {
      toJson(stringWriter, value);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return stringWriter.toString();
  }

  
  public final JsonElementq toJsonTree(T value) {
    try {
      JsonTreeWriterqq jsonWriter = new JsonTreeWriterqq();
      write(jsonWriter, value);
      return jsonWriter.get();
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    }
  }

  
  public abstract T read(JsonReaderq in) throws IOException;

  
  public final T fromJson(Reader in) throws IOException {
    JsonReaderq reader = new JsonReaderq(in);
    return read(reader);
  }

  
  public final T fromJson(String json) throws IOException {
    return fromJson(new StringReader(json));
  }

  
  public final T fromJsonTree(JsonElementq jsonTree) {
    try {
      JsonReaderq jsonReader = new JsonTreeReaderqq(jsonTree);
      return read(jsonReader);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    }
  }
}
