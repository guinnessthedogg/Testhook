

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ConstructorConstructorq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Typesq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ObjectConstructorq;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;


public final class CollectionTypeAdapterFactoryqq implements TypeAdapterFactoryqqeeqw {
  private final ConstructorConstructorq constructorConstructor;

  public CollectionTypeAdapterFactoryqq(ConstructorConstructorq constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }

  @Override
  public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
    Type type = typeToken.getType();

    Class<? super T> rawType = typeToken.getRawType();
    if (!Collection.class.isAssignableFrom(rawType)) {
      return null;
    }

    Type elementType = $Gson$Typesq.getCollectionElementType(type, rawType);
    TypeAdapterqdscvvf<?> elementTypeAdapter = gson.getAdapter(TypeTokenq.get(elementType));
    ObjectConstructorq<T> constructor = constructorConstructor.get(typeToken);

    @SuppressWarnings({"unchecked", "rawtypes"})
    TypeAdapterqdscvvf<T> result = new Adapter(gson, elementType, elementTypeAdapter, constructor);
    return result;
  }

  private static final class Adapter<E> extends TypeAdapterqdscvvf<Collection<E>> {
    private final TypeAdapterqdscvvf<E> elementTypeAdapter;
    private final ObjectConstructorq<? extends Collection<E>> constructor;

    public Adapter(Gsonq context, Type elementType,
                   TypeAdapterqdscvvf<E> elementTypeAdapter,
                   ObjectConstructorq<? extends Collection<E>> constructor) {
      this.elementTypeAdapter =
          new TypeAdapterRuntimeTypeWrapperqq<E>(context, elementTypeAdapter, elementType);
      this.constructor = constructor;
    }

    @Override public Collection<E> read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      Collection<E> collection = constructor.construct();
      in.beginArray();
      while (in.hasNext()) {
        E instance = elementTypeAdapter.read(in);
        collection.add(instance);
      }
      in.endArray();
      return collection;
    }

    @Override public void write(JsonWriterq out, Collection<E> collection) throws IOException {
      if (collection == null) {
        out.nullValue();
        return;
      }

      out.beginArray();
      for (E element : collection) {
        elementTypeAdapter.write(out, element);
      }
      out.endArray();
    }
  }
}
