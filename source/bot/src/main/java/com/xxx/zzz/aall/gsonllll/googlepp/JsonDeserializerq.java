

package com.xxx.zzz.aall.gsonllll.googlepp;

import java.lang.reflect.Type;


public interface JsonDeserializerq<T> {

  
  public T deserialize(JsonElementq json, Type typeOfT, JsonDeserializationContextq context)
      throws JsonParseExceptionq;
}
