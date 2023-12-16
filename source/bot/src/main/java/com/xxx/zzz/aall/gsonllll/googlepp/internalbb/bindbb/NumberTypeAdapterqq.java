

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.ToNumberPolicyq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.ToNumberStrategyq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;

import java.io.IOException;


public final class NumberTypeAdapterqq extends TypeAdapterqdscvvf<Number> {

  private static final TypeAdapterFactoryqqeeqw LAZILY_PARSED_NUMBER_FACTORY = newFactory(ToNumberPolicyq.LAZILY_PARSED_NUMBER);

  private final ToNumberStrategyq toNumberStrategy;

  private NumberTypeAdapterqq(ToNumberStrategyq toNumberStrategy) {
    this.toNumberStrategy = toNumberStrategy;
  }

  private static TypeAdapterFactoryqqeeqw newFactory(ToNumberStrategyq toNumberStrategy) {
    final NumberTypeAdapterqq adapter = new NumberTypeAdapterqq(toNumberStrategy);
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> type) {
        return type.getRawType() == Number.class ? (TypeAdapterqdscvvf<T>) adapter : null;
      }
    };
  }

  public static TypeAdapterFactoryqqeeqw getFactory(ToNumberStrategyq toNumberStrategy) {
    if (toNumberStrategy == ToNumberPolicyq.LAZILY_PARSED_NUMBER) {
      return LAZILY_PARSED_NUMBER_FACTORY;
    } else {
      return newFactory(toNumberStrategy);
    }
  }

  @Override public Number read(JsonReaderq in) throws IOException {
    JsonTokenq jsonToken = in.peek();
    switch (jsonToken) {
    case NULL:
      in.nextNull();
      return null;
    case NUMBER:
    case STRING:
      return toNumberStrategy.readNumber(in);
    default:
      throw new JsonSyntaxExceptionq("Expecting number, got: " + jsonToken + "; at path " + in.getPath());
    }
  }

  @Override public void write(JsonWriterq out, Number value) throws IOException {
    out.value(value);
  }
}
