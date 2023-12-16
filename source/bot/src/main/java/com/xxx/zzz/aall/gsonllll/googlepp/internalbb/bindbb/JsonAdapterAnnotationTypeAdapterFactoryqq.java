

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonDeserializerq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSerializerq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ConstructorConstructorq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.JsonAdapterqq;


public final class JsonAdapterAnnotationTypeAdapterFactoryqq implements TypeAdapterFactoryqqeeqw {
  private final ConstructorConstructorq constructorConstructor;

  public JsonAdapterAnnotationTypeAdapterFactoryqq(ConstructorConstructorq constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> targetType) {
    Class<? super T> rawType = targetType.getRawType();
    JsonAdapterqq annotation = rawType.getAnnotation(JsonAdapterqq.class);
    if (annotation == null) {
      return null;
    }
    return (TypeAdapterqdscvvf<T>) getTypeAdapter(constructorConstructor, gson, targetType, annotation);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  TypeAdapterqdscvvf<?> getTypeAdapter(ConstructorConstructorq constructorConstructor, Gsonq gson,
                                       TypeTokenq<?> type, JsonAdapterqq annotation) {
    Object instance = constructorConstructor.get(TypeTokenq.get(annotation.value())).construct();

    TypeAdapterqdscvvf<?> typeAdapter;
    if (instance instanceof TypeAdapterqdscvvf) {
      typeAdapter = (TypeAdapterqdscvvf<?>) instance;
    } else if (instance instanceof TypeAdapterFactoryqqeeqw) {
      typeAdapter = ((TypeAdapterFactoryqqeeqw) instance).create(gson, type);
    } else if (instance instanceof JsonSerializerq || instance instanceof JsonDeserializerq) {
      JsonSerializerq<?> serializer = instance instanceof JsonSerializerq
          ? (JsonSerializerq) instance
          : null;
      JsonDeserializerq<?> deserializer = instance instanceof JsonDeserializerq
          ? (JsonDeserializerq) instance
          : null;
      typeAdapter = new TreeTypeAdapterqq(serializer, deserializer, gson, type, null);
    } else {
      throw new IllegalArgumentException("Invalid attempt to bind an instance of "
          + instance.getClass().getName() + " as a @JsonAdapter for " + type.toString()
          + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory,"
          + " JsonSerializer or JsonDeserializer.");
    }

    if (typeAdapter != null && annotation.nullSafe()) {
      typeAdapter = typeAdapter.nullSafe();
    }

    return typeAdapter;
  }
}
