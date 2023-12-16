

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ConstructorConstructorq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Primitivesq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.FieldbNamingbStrategyq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.JsonAdapterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.SerializedNameqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Typesq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Excluderq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ObjectConstructorq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.reflectss.ReflectionHelperqq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class ReflectiveTypeAdapterFactoryqq implements TypeAdapterFactoryqqeeqw {
  private final ConstructorConstructorq constructorConstructor;
  private final FieldbNamingbStrategyq fieldNamingPolicy;
  private final Excluderq excluder;
  private final JsonAdapterAnnotationTypeAdapterFactoryqq jsonAdapterFactory;

  public ReflectiveTypeAdapterFactoryqq(ConstructorConstructorq constructorConstructor,
                                        FieldbNamingbStrategyq fieldNamingPolicy, Excluderq excluder,
                                        JsonAdapterAnnotationTypeAdapterFactoryqq jsonAdapterFactory) {
    this.constructorConstructor = constructorConstructor;
    this.fieldNamingPolicy = fieldNamingPolicy;
    this.excluder = excluder;
    this.jsonAdapterFactory = jsonAdapterFactory;
  }

  public boolean excludeField(Field f, boolean serialize) {
    return excludeField(f, serialize, excluder);
  }

  static boolean excludeField(Field f, boolean serialize, Excluderq excluder) {
    return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
  }


  private List<String> getFieldNames(Field f) {
    SerializedNameqq annotation = f.getAnnotation(SerializedNameqq.class);
    if (annotation == null) {
      String name = fieldNamingPolicy.translateName(f);
      return Collections.singletonList(name);
    }

    String serializedName = annotation.value();
    String[] alternates = annotation.alternate();
    if (alternates.length == 0) {
      return Collections.singletonList(serializedName);
    }

    List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
    fieldNames.add(serializedName);
    for (String alternate : alternates) {
      fieldNames.add(alternate);
    }
    return fieldNames;
  }

  @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, final TypeTokenq<T> type) {
    Class<? super T> raw = type.getRawType();

    if (!Object.class.isAssignableFrom(raw)) {
      return null;
    }

    ObjectConstructorq<T> constructor = constructorConstructor.get(type);
    return new Adapter<T>(constructor, getBoundFields(gson, type, raw));
  }

  private BoundField createBoundField(
          final Gsonq context, final Field field, final String name,
          final TypeTokenq<?> fieldType, boolean serialize, boolean deserialize) {
    final boolean isPrimitive = Primitivesq.isPrimitive(fieldType.getRawType());

    JsonAdapterqq annotation = field.getAnnotation(JsonAdapterqq.class);
    TypeAdapterqdscvvf<?> mapped = null;
    if (annotation != null) {
      mapped = jsonAdapterFactory.getTypeAdapter(
          constructorConstructor, context, fieldType, annotation);
    }
    final boolean jsonAdapterPresent = mapped != null;
    if (mapped == null) mapped = context.getAdapter(fieldType);

    final TypeAdapterqdscvvf<?> typeAdapter = mapped;
    return new BoundField(name, serialize, deserialize) {
      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override void write(JsonWriterq writer, Object value)
          throws IOException, IllegalAccessException {
        Object fieldValue = field.get(value);
        TypeAdapterqdscvvf t = jsonAdapterPresent ? typeAdapter
            : new TypeAdapterRuntimeTypeWrapperqq(context, typeAdapter, fieldType.getType());
        t.write(writer, fieldValue);
      }
      @Override void read(JsonReaderq reader, Object value)
          throws IOException, IllegalAccessException {
        Object fieldValue = typeAdapter.read(reader);
        if (fieldValue != null || !isPrimitive) {
          field.set(value, fieldValue);
        }
      }
      @Override public boolean writeField(Object value) throws IOException, IllegalAccessException {
        if (!serialized) return false;
        Object fieldValue = field.get(value);
        return fieldValue != value;
      }
    };
  }

  private Map<String, BoundField> getBoundFields(Gsonq context, TypeTokenq<?> type, Class<?> raw) {
    Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
    if (raw.isInterface()) {
      return result;
    }

    Type declaredType = type.getType();
    while (raw != Object.class) {
      Field[] fields = raw.getDeclaredFields();
      for (Field field : fields) {
        boolean serialize = excludeField(field, true);
        boolean deserialize = excludeField(field, false);
        if (!serialize && !deserialize) {
          continue;
        }
        ReflectionHelperqq.makeAccessible(field);
        Type fieldType = $Gson$Typesq.resolve(type.getType(), raw, field.getGenericType());
        List<String> fieldNames = getFieldNames(field);
        BoundField previous = null;
        for (int i = 0, size = fieldNames.size(); i < size; ++i) {
          String name = fieldNames.get(i);
          if (i != 0) serialize = false;
          BoundField boundField = createBoundField(context, field, name,
              TypeTokenq.get(fieldType), serialize, deserialize);
          BoundField replaced = result.put(name, boundField);
          if (previous == null) previous = replaced;
        }
        if (previous != null) {
          throw new IllegalArgumentException(declaredType
              + " declares multiple JSON fields named " + previous.name);
        }
      }
      type = TypeTokenq.get($Gson$Typesq.resolve(type.getType(), raw, raw.getGenericSuperclass()));
      raw = type.getRawType();
    }
    return result;
  }

  static abstract class BoundField {
    final String name;
    final boolean serialized;
    final boolean deserialized;

    protected BoundField(String name, boolean serialized, boolean deserialized) {
      this.name = name;
      this.serialized = serialized;
      this.deserialized = deserialized;
    }
    abstract boolean writeField(Object value) throws IOException, IllegalAccessException;
    abstract void write(JsonWriterq writer, Object value) throws IOException, IllegalAccessException;
    abstract void read(JsonReaderq reader, Object value) throws IOException, IllegalAccessException;
  }

  public static final class Adapter<T> extends TypeAdapterqdscvvf<T> {
    private final ObjectConstructorq<T> constructor;
    private final Map<String, BoundField> boundFields;

    Adapter(ObjectConstructorq<T> constructor, Map<String, BoundField> boundFields) {
      this.constructor = constructor;
      this.boundFields = boundFields;
    }

    @Override public T read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      T instance = constructor.construct();

      try {
        in.beginObject();
        while (in.hasNext()) {
          String name = in.nextName();
          BoundField field = boundFields.get(name);
          if (field == null || !field.deserialized) {
            in.skipValue();
          } else {
            field.read(in, instance);
          }
        }
      } catch (IllegalStateException e) {
        throw new JsonSyntaxExceptionq(e);
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
      in.endObject();
      return instance;
    }

    @Override public void write(JsonWriterq out, T value) throws IOException {
      if (value == null) {
        out.nullValue();
        return;
      }

      out.beginObject();
      try {
        for (BoundField boundField : boundFields.values()) {
          if (boundField.writeField(value)) {
            out.name(boundField.name);
            boundField.write(out, value);
          }
        }
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
      out.endObject();
    }
  }
}
