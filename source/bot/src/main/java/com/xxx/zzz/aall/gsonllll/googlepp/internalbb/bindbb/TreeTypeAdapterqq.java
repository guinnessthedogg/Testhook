

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonDeserializerq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonParseExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSerializerq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Preconditionsq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Streamsq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonDeserializationContextq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonElementq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSerializationContextq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;

import java.io.IOException;
import java.lang.reflect.Type;


public final class TreeTypeAdapterqq<T> extends TypeAdapterqdscvvf<T> {
  private final JsonSerializerq<T> serializer;
  private final JsonDeserializerq<T> deserializer;
  final Gsonq gson;
  private final TypeTokenq<T> typeToken;
  private final TypeAdapterFactoryqqeeqw skipPast;
  private final GsonContextImpl context = new GsonContextImpl();

  
  private volatile TypeAdapterqdscvvf<T> delegate;

  public TreeTypeAdapterqq(JsonSerializerq<T> serializer, JsonDeserializerq<T> deserializer,
                           Gsonq gson, TypeTokenq<T> typeToken, TypeAdapterFactoryqqeeqw skipPast) {
    this.serializer = serializer;
    this.deserializer = deserializer;
    this.gson = gson;
    this.typeToken = typeToken;
    this.skipPast = skipPast;
  }

  @Override public T read(JsonReaderq in) throws IOException {
    if (deserializer == null) {
      return delegate().read(in);
    }
    JsonElementq value = Streamsq.parse(in);
    if (value.isJsonNull()) {
      return null;
    }
    return deserializer.deserialize(value, typeToken.getType(), context);
  }

  @Override public void write(JsonWriterq out, T value) throws IOException {
    if (serializer == null) {
      delegate().write(out, value);
      return;
    }
    if (value == null) {
      out.nullValue();
      return;
    }
    JsonElementq tree = serializer.serialize(value, typeToken.getType(), context);
    Streamsq.write(tree, out);
  }

  private TypeAdapterqdscvvf<T> delegate() {

    TypeAdapterqdscvvf<T> d = delegate;
    return d != null
        ? d
        : (delegate = gson.getDelegateAdapter(skipPast, typeToken));
  }

  
  public static TypeAdapterFactoryqqeeqw newFactory(TypeTokenq<?> exactType, Object typeAdapter) {
    return new SingleTypeFactory(typeAdapter, exactType, false, null);
  }

  
  public static TypeAdapterFactoryqqeeqw newFactoryWithMatchRawType(
          TypeTokenq<?> exactType, Object typeAdapter) {

    boolean matchRawType = exactType.getType() == exactType.getRawType();
    return new SingleTypeFactory(typeAdapter, exactType, matchRawType, null);
  }

  
  public static TypeAdapterFactoryqqeeqw newTypeHierarchyFactory(
      Class<?> hierarchyType, Object typeAdapter) {
    return new SingleTypeFactory(typeAdapter, null, false, hierarchyType);
  }

  private static final class SingleTypeFactory implements TypeAdapterFactoryqqeeqw {
    private final TypeTokenq<?> exactType;
    private final boolean matchRawType;
    private final Class<?> hierarchyType;
    private final JsonSerializerq<?> serializer;
    private final JsonDeserializerq<?> deserializer;

    SingleTypeFactory(Object typeAdapter, TypeTokenq<?> exactType, boolean matchRawType,
                      Class<?> hierarchyType) {
      serializer = typeAdapter instanceof JsonSerializerq
          ? (JsonSerializerq<?>) typeAdapter
          : null;
      deserializer = typeAdapter instanceof JsonDeserializerq
          ? (JsonDeserializerq<?>) typeAdapter
          : null;
      $Gson$Preconditionsq.checkArgument(serializer != null || deserializer != null);
      this.exactType = exactType;
      this.matchRawType = matchRawType;
      this.hierarchyType = hierarchyType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> type) {
      boolean matches = exactType != null
          ? exactType.equals(type) || matchRawType && exactType.getType() == type.getRawType()
          : hierarchyType.isAssignableFrom(type.getRawType());
      return matches
          ? new TreeTypeAdapterqq<T>((JsonSerializerq<T>) serializer,
              (JsonDeserializerq<T>) deserializer, gson, type, this)
          : null;
    }
  }

  private final class GsonContextImpl implements JsonSerializationContextq, JsonDeserializationContextq {
    @Override public JsonElementq serialize(Object src) {
      return gson.toJsonTree(src);
    }
    @Override public JsonElementq serialize(Object src, Type typeOfSrc) {
      return gson.toJsonTree(src, typeOfSrc);
    }
    @SuppressWarnings("unchecked")
    @Override public <R> R deserialize(JsonElementq json, Type typeOfT) throws JsonParseExceptionq {
      return (R) gson.fromJson(json, typeOfT);
    }
  };
}
