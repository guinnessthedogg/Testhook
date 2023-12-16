

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.ToNumberPolicyq;
import com.xxx.zzz.aall.gsonllll.googlepp.ToNumberStrategyq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.LinkedTreeMapq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class ObjectTypeAdapterqq extends TypeAdapterqdscvvf<Object> {

  private static final TypeAdapterFactoryqqeeqw DOUBLE_FACTORY = newFactory(ToNumberPolicyq.DOUBLE);

  private final Gsonq gson;
  private final ToNumberStrategyq toNumberStrategy;

  private ObjectTypeAdapterqq(Gsonq gson, ToNumberStrategyq toNumberStrategy) {
    this.gson = gson;
    this.toNumberStrategy = toNumberStrategy;
  }

  private static TypeAdapterFactoryqqeeqw newFactory(final ToNumberStrategyq toNumberStrategy) {
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> type) {
        if (type.getRawType() == Object.class) {
          return (TypeAdapterqdscvvf<T>) new ObjectTypeAdapterqq(gson, toNumberStrategy);
        }
        return null;
      }
    };
  }

  public static TypeAdapterFactoryqqeeqw getFactory(ToNumberStrategyq toNumberStrategy) {
    if (toNumberStrategy == ToNumberPolicyq.DOUBLE) {
      return DOUBLE_FACTORY;
    } else {
      return newFactory(toNumberStrategy);
    }
  }

  @Override public Object read(JsonReaderq in) throws IOException {
    JsonTokenq token = in.peek();
    switch (token) {
    case BEGIN_ARRAY:
      List<Object> list = new ArrayList<Object>();
      in.beginArray();
      while (in.hasNext()) {
        list.add(read(in));
      }
      in.endArray();
      return list;

    case BEGIN_OBJECT:
      Map<String, Object> map = new LinkedTreeMapq<String, Object>();
      in.beginObject();
      while (in.hasNext()) {
        map.put(in.nextName(), read(in));
      }
      in.endObject();
      return map;

    case STRING:
      return in.nextString();

    case NUMBER:
      return toNumberStrategy.readNumber(in);

    case BOOLEAN:
      return in.nextBoolean();

    case NULL:
      in.nextNull();
      return null;

    default:
      throw new IllegalStateException();
    }
  }

  @SuppressWarnings("unchecked")
  @Override public void write(JsonWriterq out, Object value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }

    TypeAdapterqdscvvf<Object> typeAdapter = (TypeAdapterqdscvvf<Object>) gson.getAdapter(value.getClass());
    if (typeAdapter instanceof ObjectTypeAdapterqq) {
      out.beginObject();
      out.endObject();
      return;
    }

    typeAdapter.write(out, value);
  }
}
