

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Typesq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public final class ArrayTypeAdapterqqopip<E> extends TypeAdapterqdscvvf<Object> {
  public static final TypeAdapterFactoryqqeeqw FACTORY = new TypeAdapterFactoryqqeeqw() {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
      Type type = typeToken.getType();
      if (!(type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray())) {
        return null;
      }

      Type componentType = $Gson$Typesq.getArrayComponentType(type);
      TypeAdapterqdscvvf<?> componentTypeAdapter = gson.getAdapter(TypeTokenq.get(componentType));
      return new ArrayTypeAdapterqqopip(
              gson, componentTypeAdapter, $Gson$Typesq.getRawType(componentType));
    }
  };

  private final Class<E> componentType;
  private final TypeAdapterqdscvvf<E> componentTypeAdapter;

  public ArrayTypeAdapterqqopip(Gsonq context, TypeAdapterqdscvvf<E> componentTypeAdapter, Class<E> componentType) {
    this.componentTypeAdapter =
      new TypeAdapterRuntimeTypeWrapperqq<E>(context, componentTypeAdapter, componentType);
    this.componentType = componentType;
  }

  @Override public Object read(JsonReaderq in) throws IOException {
    if (in.peek() == JsonTokenq.NULL) {
      in.nextNull();
      return null;
    }

    List<E> list = new ArrayList<E>();
    in.beginArray();
    while (in.hasNext()) {
      E instance = componentTypeAdapter.read(in);
      list.add(instance);
    }
    in.endArray();

    int size = list.size();
    Object array = Array.newInstance(componentType, size);
    for (int i = 0; i < size; i++) {
      Array.set(array, i, list.get(i));
    }
    return array;
  }

  @SuppressWarnings("unchecked")
  @Override public void write(JsonWriterq out, Object array) throws IOException {
    if (array == null) {
      out.nullValue();
      return;
    }

    out.beginArray();
    for (int i = 0, length = Array.getLength(array); i < length; i++) {
      E value = (E) Array.get(array, i);
      componentTypeAdapter.write(out, value);
    }
    out.endArray();
  }
}
