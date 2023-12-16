
package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Streamsq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.MalformedJsonExceptionq;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


public final class JsonaParserqxcv {

  @Deprecated
  public JsonaParserqxcv() {}


  public static JsonElementq parseString(String json) throws JsonSyntaxExceptionq {
    return parseReader(new StringReader(json));
  }


  public static JsonElementq parseReader(Reader reader) throws JsonIOExceptionq, JsonSyntaxExceptionq {
    try {
      JsonReaderq jsonReader = new JsonReaderq(reader);
      JsonElementq element = parseReader(jsonReader);
      if (!element.isJsonNull() && jsonReader.peek() != JsonTokenq.END_DOCUMENT) {
        throw new JsonSyntaxExceptionq("Did not consume the entire document.");
      }
      return element;
    } catch (MalformedJsonExceptionq e) {
      throw new JsonSyntaxExceptionq(e);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxExceptionq(e);
    }
  }


  public static JsonElementq parseReader(JsonReaderq reader)
      throws JsonIOExceptionq, JsonSyntaxExceptionq {
    boolean lenient = reader.isLenient();
    reader.setLenient(true);
    try {
      return Streamsq.parse(reader);
    } catch (StackOverflowError e) {
      throw new JsonParseExceptionq("Failed parsing JSON source: " + reader + " to Json", e);
    } catch (OutOfMemoryError e) {
      throw new JsonParseExceptionq("Failed parsing JSON source: " + reader + " to Json", e);
    } finally {
      reader.setLenient(lenient);
    }
  }


  @Deprecated
  public JsonElementq parse(String json) throws JsonSyntaxExceptionq {
    return parseString(json);
  }


  @Deprecated
  public JsonElementq parse(Reader json) throws JsonIOExceptionq, JsonSyntaxExceptionq {
    return parseReader(json);
  }


  @Deprecated
  public JsonElementq parse(JsonReaderq json) throws JsonIOExceptionq, JsonSyntaxExceptionq {
    return parseReader(json);
  }
}
