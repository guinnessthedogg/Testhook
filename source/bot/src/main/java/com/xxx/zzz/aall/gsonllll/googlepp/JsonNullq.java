

package com.xxx.zzz.aall.gsonllll.googlepp;


public final class JsonNullq extends JsonElementq {
  
  public static final JsonNullq INSTANCE = new JsonNullq();

  
  @Deprecated
  public JsonNullq() {

  }

  
  @Override
  public JsonNullq deepCopy() {
    return INSTANCE;
  }

  
  @Override
  public int hashCode() {
    return JsonNullq.class.hashCode();
  }

  
  @Override
  public boolean equals(Object other) {
    return this == other || other instanceof JsonNullq;
  }
}
