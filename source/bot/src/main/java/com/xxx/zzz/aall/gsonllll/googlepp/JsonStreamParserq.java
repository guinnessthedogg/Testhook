
package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Streamsq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.MalformedJsonExceptionq;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;


public final class JsonStreamParserq implements Iterator<JsonElementq> {
  private final JsonReaderq parser;
  private final Object lock;


  public JsonStreamParserq(String json) {
    this(new StringReader(json));      
  }
  

  public JsonStreamParserq(Reader reader) {
    parser = new JsonReaderq(reader);
    parser.setLenient(true);
    lock = new Object();
  }
  

  public JsonElementq next() throws JsonParseExceptionq {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    
    try {
      return Streamsq.parse(parser);
    } catch (StackOverflowError e) {
      throw new JsonParseExceptionq("Failed parsing JSON source to Json", e);
    } catch (OutOfMemoryError e) {
      throw new JsonParseExceptionq("Failed parsing JSON source to Json", e);
    } catch (JsonParseExceptionq e) {
      throw e.getCause() instanceof EOFException ? new NoSuchElementException() : e;
    }
  }


  public boolean hasNext() {
    synchronized (lock) {
      try {
        return parser.peek() != JsonTokenq.END_DOCUMENT;
      } catch (MalformedJsonExceptionq e) {
        throw new JsonSyntaxExceptionq(e);
      } catch (IOException e) {
        throw new JsonIOExceptionq(e);
      }
    }
  }


  public void remove() {
    throw new UnsupportedOperationException();
  }
}
