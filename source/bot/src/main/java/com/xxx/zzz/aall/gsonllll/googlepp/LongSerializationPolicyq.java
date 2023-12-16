

package com.xxx.zzz.aall.gsonllll.googlepp;


public enum LongSerializationPolicyq {

  DEFAULT() {
    @Override public JsonElementq serialize(Long value) {
      if (value == null) {
        return JsonNullq.INSTANCE;
      }
      return new JsonPrimitiveq(value);
    }
  },
  

  STRING() {
    @Override public JsonElementq serialize(Long value) {
      if (value == null) {
        return JsonNullq.INSTANCE;
      }
      return new JsonPrimitiveq(value.toString());
    }
  };
  

  public abstract JsonElementq serialize(Long value);
}
