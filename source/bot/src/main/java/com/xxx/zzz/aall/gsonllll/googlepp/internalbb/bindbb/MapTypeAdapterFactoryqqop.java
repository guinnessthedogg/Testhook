

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Typesq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ConstructorConstructorq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.JsonReaderInternalAccessq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Streamsq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonElementq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonPrimitiveq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ObjectConstructorq;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class MapTypeAdapterFactoryqqop implements TypeAdapterFactoryqqeeqw {
  private final ConstructorConstructorq constructorConstructor;
  final boolean complexMapKeySerialization;

  public MapTypeAdapterFactoryqqop(ConstructorConstructorq constructorConstructor,
                                   boolean complexMapKeySerialization) {
    this.constructorConstructor = constructorConstructor;
    this.complexMapKeySerialization = complexMapKeySerialization;
  }

  @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
    Type type = typeToken.getType();

    Class<? super T> rawType = typeToken.getRawType();
    if (!Map.class.isAssignableFrom(rawType)) {
      return null;
    }

    Class<?> rawTypeOfSrc = $Gson$Typesq.getRawType(type);
    Type[] keyAndValueTypes = $Gson$Typesq.getMapKeyAndValueTypes(type, rawTypeOfSrc);
    TypeAdapterqdscvvf<?> keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
    TypeAdapterqdscvvf<?> valueAdapter = gson.getAdapter(TypeTokenq.get(keyAndValueTypes[1]));
    ObjectConstructorq<T> constructor = constructorConstructor.get(typeToken);

    @SuppressWarnings({"unchecked", "rawtypes"})

    TypeAdapterqdscvvf<T> result = new Adapter(gson, keyAndValueTypes[0], keyAdapter,
        keyAndValueTypes[1], valueAdapter, constructor);
    return result;
  }

  
  private TypeAdapterqdscvvf<?> getKeyAdapter(Gsonq context, Type keyType) {
    return (keyType == boolean.class || keyType == Boolean.class)
        ? TypeAdaptersqq.BOOLEAN_AS_STRING
        : context.getAdapter(TypeTokenq.get(keyType));
  }

  private final class Adapter<K, V> extends TypeAdapterqdscvvf<Map<K, V>> {
    private final TypeAdapterqdscvvf<K> keyTypeAdapter;
    private final TypeAdapterqdscvvf<V> valueTypeAdapter;
    private final ObjectConstructorq<? extends Map<K, V>> constructor;

    public Adapter(Gsonq context, Type keyType, TypeAdapterqdscvvf<K> keyTypeAdapter,
                   Type valueType, TypeAdapterqdscvvf<V> valueTypeAdapter,
                   ObjectConstructorq<? extends Map<K, V>> constructor) {
      this.keyTypeAdapter =
        new TypeAdapterRuntimeTypeWrapperqq<K>(context, keyTypeAdapter, keyType);
      this.valueTypeAdapter =
        new TypeAdapterRuntimeTypeWrapperqq<V>(context, valueTypeAdapter, valueType);
      this.constructor = constructor;
    }

    @Override public Map<K, V> read(JsonReaderq in) throws IOException {
      JsonTokenq peek = in.peek();
      if (peek == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      Map<K, V> map = constructor.construct();

      if (peek == JsonTokenq.BEGIN_ARRAY) {
        in.beginArray();
        while (in.hasNext()) {
          in.beginArray();
          K key = keyTypeAdapter.read(in);
          V value = valueTypeAdapter.read(in);
          V replaced = map.put(key, value);
          if (replaced != null) {
            throw new JsonSyntaxExceptionq("duplicate key: " + key);
          }
          in.endArray();
        }
        in.endArray();
      } else {
        in.beginObject();
        while (in.hasNext()) {
          JsonReaderInternalAccessq.INSTANCE.promoteNameToValue(in);
          K key = keyTypeAdapter.read(in);
          V value = valueTypeAdapter.read(in);
          V replaced = map.put(key, value);
          if (replaced != null) {
            throw new JsonSyntaxExceptionq("duplicate key: " + key);
          }
        }
        in.endObject();
      }
      return map;
    }

    @Override public void write(JsonWriterq out, Map<K, V> map) throws IOException {
      if (map == null) {
        out.nullValue();
        return;
      }

      if (!complexMapKeySerialization) {
        out.beginObject();
        for (Map.Entry<K, V> entry : map.entrySet()) {
          out.name(String.valueOf(entry.getKey()));
          valueTypeAdapter.write(out, entry.getValue());
        }
        out.endObject();
        return;
      }

      boolean hasComplexKeys = false;
      List<JsonElementq> keys = new ArrayList<JsonElementq>(map.size());

      List<V> values = new ArrayList<V>(map.size());
      for (Map.Entry<K, V> entry : map.entrySet()) {
        JsonElementq keyElement = keyTypeAdapter.toJsonTree(entry.getKey());
        keys.add(keyElement);
        values.add(entry.getValue());
        hasComplexKeys |= keyElement.isJsonArray() || keyElement.isJsonObject();
      }

      if (hasComplexKeys) {
        out.beginArray();
        for (int i = 0, size = keys.size(); i < size; i++) {
          out.beginArray();
          Streamsq.write(keys.get(i), out);
          valueTypeAdapter.write(out, values.get(i));
          out.endArray();
        }
        out.endArray();
      } else {
        out.beginObject();
        for (int i = 0, size = keys.size(); i < size; i++) {
          JsonElementq keyElement = keys.get(i);
          out.name(keyToString(keyElement));
          valueTypeAdapter.write(out, values.get(i));
        }
        out.endObject();
      }
    }

    private String keyToString(JsonElementq keyElement) {
      if (keyElement.isJsonPrimitive()) {
        JsonPrimitiveq primitive = keyElement.getAsJsonPrimitive();
        if (primitive.isNumber()) {
          return String.valueOf(primitive.getAsNumber());
        } else if (primitive.isBoolean()) {
          return Boolean.toString(primitive.getAsBoolean());
        } else if (primitive.isString()) {
          return primitive.getAsString();
        } else {
          throw new AssertionError();
        }
      } else if (keyElement.isJsonNull()) {
        return "null";
      } else {
        throw new AssertionError();
      }
    }
  }
}
