
package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapperqq<T> extends TypeAdapterqdscvvf<T> {
  private final Gsonq context;
  private final TypeAdapterqdscvvf<T> delegate;
  private final Type type;

  TypeAdapterRuntimeTypeWrapperqq(Gsonq context, TypeAdapterqdscvvf<T> delegate, Type type) {
    this.context = context;
    this.delegate = delegate;
    this.type = type;
  }

  @Override
  public T read(JsonReaderq in) throws IOException {
    return delegate.read(in);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void write(JsonWriterq out, T value) throws IOException {






    TypeAdapterqdscvvf chosen = delegate;
    Type runtimeType = getRuntimeTypeIfMoreSpecific(type, value);
    if (runtimeType != type) {
      TypeAdapterqdscvvf runtimeTypeAdapter = context.getAdapter(TypeTokenq.get(runtimeType));
      if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactoryqq.Adapter)) {

        chosen = runtimeTypeAdapter;
      } else if (!(delegate instanceof ReflectiveTypeAdapterFactoryqq.Adapter)) {


        chosen = delegate;
      } else {

        chosen = runtimeTypeAdapter;
      }
    }
    chosen.write(out, value);
  }


  private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
    if (value != null
        && (type == Object.class || type instanceof TypeVariable<?> || type instanceof Class<?>)) {
      type = value.getClass();
    }
    return type;
  }
}
