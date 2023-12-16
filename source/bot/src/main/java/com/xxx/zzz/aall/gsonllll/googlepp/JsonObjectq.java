

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.LinkedTreeMapq;

import java.util.Map;
import java.util.Set;


public final class JsonObjectq extends JsonElementq {
  private final LinkedTreeMapq<String, JsonElementq> members =
      new LinkedTreeMapq<String, JsonElementq>();

  
  @Override
  public JsonObjectq deepCopy() {
    JsonObjectq result = new JsonObjectq();
    for (Map.Entry<String, JsonElementq> entry : members.entrySet()) {
      result.add(entry.getKey(), entry.getValue().deepCopy());
    }
    return result;
  }

  
  public void add(String property, JsonElementq value) {
    members.put(property, value == null ? JsonNullq.INSTANCE : value);
  }

  
  public JsonElementq remove(String property) {
    return members.remove(property);
  }

  
  public void addProperty(String property, String value) {
    add(property, value == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(value));
  }

  
  public void addProperty(String property, Number value) {
    add(property, value == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(value));
  }

  
  public void addProperty(String property, Boolean value) {
    add(property, value == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(value));
  }

  
  public void addProperty(String property, Character value) {
    add(property, value == null ? JsonNullq.INSTANCE : new JsonPrimitiveq(value));
  }

  
  public Set<Map.Entry<String, JsonElementq>> entrySet() {
    return members.entrySet();
  }

  
  public Set<String> keySet() {
    return members.keySet();
  }

  
  public int size() {
    return members.size();
  }

  
  public boolean has(String memberName) {
    return members.containsKey(memberName);
  }

  
  public JsonElementq get(String memberName) {
    return members.get(memberName);
  }

  
  public JsonPrimitiveq getAsJsonPrimitive(String memberName) {
    return (JsonPrimitiveq) members.get(memberName);
  }

  
  public JsonArrayq getAsJsonArray(String memberName) {
    return (JsonArrayq) members.get(memberName);
  }

  
  public JsonObjectq getAsJsonObject(String memberName) {
    return (JsonObjectq) members.get(memberName);
  }

  @Override
  public boolean equals(Object o) {
    return (o == this) || (o instanceof JsonObjectq
        && ((JsonObjectq) o).members.equals(members));
  }

  @Override
  public int hashCode() {
    return members.hashCode();
  }
}
