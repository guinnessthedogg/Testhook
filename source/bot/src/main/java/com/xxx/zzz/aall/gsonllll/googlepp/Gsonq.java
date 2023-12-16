

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.ConstructorConstructorq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.Exposeqq;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.Sinceqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Excluderq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.GsonBuildConfigq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.LazilyParsedNumberq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Primitivesq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Streamsq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.ArrayTypeAdapterqqopip;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.CollectionTypeAdapterFactoryqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.DateTypeAdapterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.JsonAdapterAnnotationTypeAdapterFactoryqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.JsonTreeReaderqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.JsonTreeWriterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.MapTypeAdapterFactoryqqop;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.NumberTypeAdapterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.ObjectTypeAdapterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.ReflectiveTypeAdapterFactoryqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.TypeAdaptersqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.sqlbb.SqlTypesSupportq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.MalformedJsonExceptionq;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;


public final class Gsonq {
  static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
  static final boolean DEFAULT_LENIENT = false;
  static final boolean DEFAULT_PRETTY_PRINT = false;
  static final boolean DEFAULT_ESCAPE_HTML = true;
  static final boolean DEFAULT_SERIALIZE_NULLS = false;
  static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
  static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
  static final boolean DEFAULT_USE_JDK_UNSAFE = true;
  static final String DEFAULT_DATE_PATTERN = null;
  static final FieldbNamingbStrategyq DEFAULT_FIELD_NAMING_STRATEGY = FieldNamingPolicyq.IDENTITY;
  static final ToNumberStrategyq DEFAULT_OBJECT_TO_NUMBER_STRATEGY = ToNumberPolicyq.DOUBLE;
  static final ToNumberStrategyq DEFAULT_NUMBER_TO_NUMBER_STRATEGY = ToNumberPolicyq.LAZILY_PARSED_NUMBER;

  private static final TypeTokenq<?> NULL_KEY_SURROGATE = TypeTokenq.get(Object.class);
  private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";


  private final ThreadLocal<Map<TypeTokenq<?>, FutureTypeAdapter<?>>> calls
      = new ThreadLocal<Map<TypeTokenq<?>, FutureTypeAdapter<?>>>();

  private final Map<TypeTokenq<?>, TypeAdapterqdscvvf<?>> typeTokenCache = new ConcurrentHashMap<TypeTokenq<?>, TypeAdapterqdscvvf<?>>();

  private final ConstructorConstructorq constructorConstructor;
  private final JsonAdapterAnnotationTypeAdapterFactoryqq jsonAdapterFactory;

  final List<TypeAdapterFactoryqqeeqw> factories;

  final Excluderq excluder;
  final FieldbNamingbStrategyq fieldNamingStrategy;
  final Map<Type, InstanceCreatorq<?>> instanceCreators;
  final boolean serializeNulls;
  final boolean complexMapKeySerialization;
  final boolean generateNonExecutableJson;
  final boolean htmlSafe;
  final boolean prettyPrinting;
  final boolean lenient;
  final boolean serializeSpecialFloatingPointValues;
  final boolean useJdkUnsafe;
  final String datePattern;
  final int dateStyle;
  final int timeStyle;
  final LongSerializationPolicyq longSerializationPolicy;
  final List<TypeAdapterFactoryqqeeqw> builderFactories;
  final List<TypeAdapterFactoryqqeeqw> builderHierarchyFactories;
  final ToNumberStrategyq objectToNumberStrategy;
  final ToNumberStrategyq numberToNumberStrategy;


  public Gsonq() {
    this(Excluderq.DEFAULT, DEFAULT_FIELD_NAMING_STRATEGY,
        Collections.<Type, InstanceCreatorq<?>>emptyMap(), DEFAULT_SERIALIZE_NULLS,
        DEFAULT_COMPLEX_MAP_KEYS, DEFAULT_JSON_NON_EXECUTABLE, DEFAULT_ESCAPE_HTML,
        DEFAULT_PRETTY_PRINT, DEFAULT_LENIENT, DEFAULT_SPECIALIZE_FLOAT_VALUES,
        DEFAULT_USE_JDK_UNSAFE,
        LongSerializationPolicyq.DEFAULT, DEFAULT_DATE_PATTERN, DateFormat.DEFAULT, DateFormat.DEFAULT,
        Collections.<TypeAdapterFactoryqqeeqw>emptyList(), Collections.<TypeAdapterFactoryqqeeqw>emptyList(),
        Collections.<TypeAdapterFactoryqqeeqw>emptyList(), DEFAULT_OBJECT_TO_NUMBER_STRATEGY, DEFAULT_NUMBER_TO_NUMBER_STRATEGY);
  }

  Gsonq(Excluderq excluder, FieldbNamingbStrategyq fieldNamingStrategy,
        Map<Type, InstanceCreatorq<?>> instanceCreators, boolean serializeNulls,
        boolean complexMapKeySerialization, boolean generateNonExecutableGson, boolean htmlSafe,
        boolean prettyPrinting, boolean lenient, boolean serializeSpecialFloatingPointValues,
        boolean useJdkUnsafe,
        LongSerializationPolicyq longSerializationPolicy, String datePattern, int dateStyle,
        int timeStyle, List<TypeAdapterFactoryqqeeqw> builderFactories,
        List<TypeAdapterFactoryqqeeqw> builderHierarchyFactories,
        List<TypeAdapterFactoryqqeeqw> factoriesToBeAdded,
        ToNumberStrategyq objectToNumberStrategy, ToNumberStrategyq numberToNumberStrategy) {
    this.excluder = excluder;
    this.fieldNamingStrategy = fieldNamingStrategy;
    this.instanceCreators = instanceCreators;
    this.constructorConstructor = new ConstructorConstructorq(instanceCreators, useJdkUnsafe);
    this.serializeNulls = serializeNulls;
    this.complexMapKeySerialization = complexMapKeySerialization;
    this.generateNonExecutableJson = generateNonExecutableGson;
    this.htmlSafe = htmlSafe;
    this.prettyPrinting = prettyPrinting;
    this.lenient = lenient;
    this.serializeSpecialFloatingPointValues = serializeSpecialFloatingPointValues;
    this.useJdkUnsafe = useJdkUnsafe;
    this.longSerializationPolicy = longSerializationPolicy;
    this.datePattern = datePattern;
    this.dateStyle = dateStyle;
    this.timeStyle = timeStyle;
    this.builderFactories = builderFactories;
    this.builderHierarchyFactories = builderHierarchyFactories;
    this.objectToNumberStrategy = objectToNumberStrategy;
    this.numberToNumberStrategy = numberToNumberStrategy;

    List<TypeAdapterFactoryqqeeqw> factories = new ArrayList<TypeAdapterFactoryqqeeqw>();


    factories.add(TypeAdaptersqq.JSON_ELEMENT_FACTORY);
    factories.add(ObjectTypeAdapterqq.getFactory(objectToNumberStrategy));


    factories.add(excluder);


    factories.addAll(factoriesToBeAdded);


    factories.add(TypeAdaptersqq.STRING_FACTORY);
    factories.add(TypeAdaptersqq.INTEGER_FACTORY);
    factories.add(TypeAdaptersqq.BOOLEAN_FACTORY);
    factories.add(TypeAdaptersqq.BYTE_FACTORY);
    factories.add(TypeAdaptersqq.SHORT_FACTORY);
    TypeAdapterqdscvvf<Number> longAdapter = longAdapter(longSerializationPolicy);
    factories.add(TypeAdaptersqq.newFactory(long.class, Long.class, longAdapter));
    factories.add(TypeAdaptersqq.newFactory(double.class, Double.class,
            doubleAdapter(serializeSpecialFloatingPointValues)));
    factories.add(TypeAdaptersqq.newFactory(float.class, Float.class,
            floatAdapter(serializeSpecialFloatingPointValues)));
    factories.add(NumberTypeAdapterqq.getFactory(numberToNumberStrategy));
    factories.add(TypeAdaptersqq.ATOMIC_INTEGER_FACTORY);
    factories.add(TypeAdaptersqq.ATOMIC_BOOLEAN_FACTORY);
    factories.add(TypeAdaptersqq.newFactory(AtomicLong.class, atomicLongAdapter(longAdapter)));
    factories.add(TypeAdaptersqq.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(longAdapter)));
    factories.add(TypeAdaptersqq.ATOMIC_INTEGER_ARRAY_FACTORY);
    factories.add(TypeAdaptersqq.CHARACTER_FACTORY);
    factories.add(TypeAdaptersqq.STRING_BUILDER_FACTORY);
    factories.add(TypeAdaptersqq.STRING_BUFFER_FACTORY);
    factories.add(TypeAdaptersqq.newFactory(BigDecimal.class, TypeAdaptersqq.BIG_DECIMAL));
    factories.add(TypeAdaptersqq.newFactory(BigInteger.class, TypeAdaptersqq.BIG_INTEGER));

    factories.add(TypeAdaptersqq.newFactory(LazilyParsedNumberq.class, TypeAdaptersqq.LAZILY_PARSED_NUMBER));
    factories.add(TypeAdaptersqq.URL_FACTORY);
    factories.add(TypeAdaptersqq.URI_FACTORY);
    factories.add(TypeAdaptersqq.UUID_FACTORY);
    factories.add(TypeAdaptersqq.CURRENCY_FACTORY);
    factories.add(TypeAdaptersqq.LOCALE_FACTORY);
    factories.add(TypeAdaptersqq.INET_ADDRESS_FACTORY);
    factories.add(TypeAdaptersqq.BIT_SET_FACTORY);
    factories.add(DateTypeAdapterqq.FACTORY);
    factories.add(TypeAdaptersqq.CALENDAR_FACTORY);

    if (SqlTypesSupportq.SUPPORTS_SQL_TYPES) {
      factories.add(SqlTypesSupportq.TIME_FACTORY);
      factories.add(SqlTypesSupportq.DATE_FACTORY);
      factories.add(SqlTypesSupportq.TIMESTAMP_FACTORY);
    }

    factories.add(ArrayTypeAdapterqqopip.FACTORY);
    factories.add(TypeAdaptersqq.CLASS_FACTORY);


    factories.add(new CollectionTypeAdapterFactoryqq(constructorConstructor));
    factories.add(new MapTypeAdapterFactoryqqop(constructorConstructor, complexMapKeySerialization));
    this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactoryqq(constructorConstructor);
    factories.add(jsonAdapterFactory);
    factories.add(TypeAdaptersqq.ENUM_FACTORY);
    factories.add(new ReflectiveTypeAdapterFactoryqq(
        constructorConstructor, fieldNamingStrategy, excluder, jsonAdapterFactory));

    this.factories = Collections.unmodifiableList(factories);
  }


  public GsonBuilderq newBuilder() {
    return new GsonBuilderq(this);
  }


  @Deprecated
  public Excluderq excluder() {
    return excluder;
  }


  public FieldbNamingbStrategyq fieldNamingStrategy() {
    return fieldNamingStrategy;
  }


  public boolean serializeNulls() {
    return serializeNulls;
  }


  public boolean htmlSafe() {
    return htmlSafe;
  }

  private TypeAdapterqdscvvf<Number> doubleAdapter(boolean serializeSpecialFloatingPointValues) {
    if (serializeSpecialFloatingPointValues) {
      return TypeAdaptersqq.DOUBLE;
    }
    return new TypeAdapterqdscvvf<Number>() {
      @Override public Double read(JsonReaderq in) throws IOException {
        if (in.peek() == JsonTokenq.NULL) {
          in.nextNull();
          return null;
        }
        return in.nextDouble();
      }
      @Override public void write(JsonWriterq out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
          return;
        }
        double doubleValue = value.doubleValue();
        checkValidFloatingPoint(doubleValue);
        out.value(value);
      }
    };
  }

  private TypeAdapterqdscvvf<Number> floatAdapter(boolean serializeSpecialFloatingPointValues) {
    if (serializeSpecialFloatingPointValues) {
      return TypeAdaptersqq.FLOAT;
    }
    return new TypeAdapterqdscvvf<Number>() {
      @Override public Float read(JsonReaderq in) throws IOException {
        if (in.peek() == JsonTokenq.NULL) {
          in.nextNull();
          return null;
        }
        return (float) in.nextDouble();
      }
      @Override public void write(JsonWriterq out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
          return;
        }
        float floatValue = value.floatValue();
        checkValidFloatingPoint(floatValue);
        out.value(value);
      }
    };
  }

  static void checkValidFloatingPoint(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      throw new IllegalArgumentException(value
          + " is not a valid double value as per JSON specification. To override this"
          + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
    }
  }

  private static TypeAdapterqdscvvf<Number> longAdapter(LongSerializationPolicyq longSerializationPolicy) {
    if (longSerializationPolicy == LongSerializationPolicyq.DEFAULT) {
      return TypeAdaptersqq.LONG;
    }
    return new TypeAdapterqdscvvf<Number>() {
      @Override public Number read(JsonReaderq in) throws IOException {
        if (in.peek() == JsonTokenq.NULL) {
          in.nextNull();
          return null;
        }
        return in.nextLong();
      }
      @Override public void write(JsonWriterq out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
          return;
        }
        out.value(value.toString());
      }
    };
  }

  private static TypeAdapterqdscvvf<AtomicLong> atomicLongAdapter(final TypeAdapterqdscvvf<Number> longAdapter) {
    return new TypeAdapterqdscvvf<AtomicLong>() {
      @Override public void write(JsonWriterq out, AtomicLong value) throws IOException {
        longAdapter.write(out, value.get());
      }
      @Override public AtomicLong read(JsonReaderq in) throws IOException {
        Number value = longAdapter.read(in);
        return new AtomicLong(value.longValue());
      }
    }.nullSafe();
  }

  private static TypeAdapterqdscvvf<AtomicLongArray> atomicLongArrayAdapter(final TypeAdapterqdscvvf<Number> longAdapter) {
    return new TypeAdapterqdscvvf<AtomicLongArray>() {
      @Override public void write(JsonWriterq out, AtomicLongArray value) throws IOException {
        out.beginArray();
        for (int i = 0, length = value.length(); i < length; i++) {
          longAdapter.write(out, value.get(i));
        }
        out.endArray();
      }
      @Override public AtomicLongArray read(JsonReaderq in) throws IOException {
        List<Long> list = new ArrayList<Long>();
        in.beginArray();
        while (in.hasNext()) {
            long value = longAdapter.read(in).longValue();
            list.add(value);
        }
        in.endArray();
        int length = list.size();
        AtomicLongArray array = new AtomicLongArray(length);
        for (int i = 0; i < length; ++i) {
          array.set(i, list.get(i));
        }
        return array;
      }
    }.nullSafe();
  }


  @SuppressWarnings("unchecked")
  public <T> TypeAdapterqdscvvf<T> getAdapter(TypeTokenq<T> type) {
    TypeAdapterqdscvvf<?> cached = typeTokenCache.get(type == null ? NULL_KEY_SURROGATE : type);
    if (cached != null) {
      return (TypeAdapterqdscvvf<T>) cached;
    }

    Map<TypeTokenq<?>, FutureTypeAdapter<?>> threadCalls = calls.get();
    boolean requiresThreadLocalCleanup = false;
    if (threadCalls == null) {
      threadCalls = new HashMap<TypeTokenq<?>, FutureTypeAdapter<?>>();
      calls.set(threadCalls);
      requiresThreadLocalCleanup = true;
    }


    FutureTypeAdapter<T> ongoingCall = (FutureTypeAdapter<T>) threadCalls.get(type);
    if (ongoingCall != null) {
      return ongoingCall;
    }

    try {
      FutureTypeAdapter<T> call = new FutureTypeAdapter<T>();
      threadCalls.put(type, call);

      for (TypeAdapterFactoryqqeeqw factory : factories) {
        TypeAdapterqdscvvf<T> candidate = factory.create(this, type);
        if (candidate != null) {
          call.setDelegate(candidate);
          typeTokenCache.put(type, candidate);
          return candidate;
        }
      }
      throw new IllegalArgumentException("GSON (" + GsonBuildConfigq.VERSION + ") cannot handle " + type);
    } finally {
      threadCalls.remove(type);

      if (requiresThreadLocalCleanup) {
        calls.remove();
      }
    }
  }


  public <T> TypeAdapterqdscvvf<T> getDelegateAdapter(TypeAdapterFactoryqqeeqw skipPast, TypeTokenq<T> type) {


    if (!factories.contains(skipPast)) {
      skipPast = jsonAdapterFactory;
    }

    boolean skipPastFound = false;
    for (TypeAdapterFactoryqqeeqw factory : factories) {
      if (!skipPastFound) {
        if (factory == skipPast) {
          skipPastFound = true;
        }
        continue;
      }

      TypeAdapterqdscvvf<T> candidate = factory.create(this, type);
      if (candidate != null) {
        return candidate;
      }
    }
    throw new IllegalArgumentException("GSON cannot serialize " + type);
  }


  public <T> TypeAdapterqdscvvf<T> getAdapter(Class<T> type) {
    return getAdapter(TypeTokenq.get(type));
  }


  public JsonElementq toJsonTree(Object src) {
    if (src == null) {
      return JsonNullq.INSTANCE;
    }
    return toJsonTree(src, src.getClass());
  }


  public JsonElementq toJsonTree(Object src, Type typeOfSrc) {
    JsonTreeWriterqq writer = new JsonTreeWriterqq();
    toJson(src, typeOfSrc, writer);
    return writer.get();
  }


  public String toJson(Object src) {
    if (src == null) {
      return toJson(JsonNullq.INSTANCE);
    }
    return toJson(src, src.getClass());
  }


  public String toJson(Object src, Type typeOfSrc) {
    StringWriter writer = new StringWriter();
    toJson(src, typeOfSrc, writer);
    return writer.toString();
  }


  public void toJson(Object src, Appendable writer) throws JsonIOExceptionq {
    if (src != null) {
      toJson(src, src.getClass(), writer);
    } else {
      toJson(JsonNullq.INSTANCE, writer);
    }
  }


  public void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOExceptionq {
    try {
      JsonWriterq jsonWriter = newJsonWriter(Streamsq.writerForAppendable(writer));
      toJson(src, typeOfSrc, jsonWriter);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    }
  }


  @SuppressWarnings("unchecked")
  public void toJson(Object src, Type typeOfSrc, JsonWriterq writer) throws JsonIOExceptionq {
    TypeAdapterqdscvvf<?> adapter = getAdapter(TypeTokenq.get(typeOfSrc));
    boolean oldLenient = writer.isLenient();
    writer.setLenient(true);
    boolean oldHtmlSafe = writer.isHtmlSafe();
    writer.setHtmlSafe(htmlSafe);
    boolean oldSerializeNulls = writer.getSerializeNulls();
    writer.setSerializeNulls(serializeNulls);
    try {
      ((TypeAdapterqdscvvf<Object>) adapter).write(writer, src);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    } catch (AssertionError e) {
      AssertionError error = new AssertionError("AssertionError (GSON " + GsonBuildConfigq.VERSION + "): " + e.getMessage());
      error.initCause(e);
      throw error;
    } finally {
      writer.setLenient(oldLenient);
      writer.setHtmlSafe(oldHtmlSafe);
      writer.setSerializeNulls(oldSerializeNulls);
    }
  }


  public String toJson(JsonElementq jsonElement) {
    StringWriter writer = new StringWriter();
    toJson(jsonElement, writer);
    return writer.toString();
  }


  public void toJson(JsonElementq jsonElement, Appendable writer) throws JsonIOExceptionq {
    try {
      JsonWriterq jsonWriter = newJsonWriter(Streamsq.writerForAppendable(writer));
      toJson(jsonElement, jsonWriter);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    }
  }


  public JsonWriterq newJsonWriter(Writer writer) throws IOException {
    if (generateNonExecutableJson) {
      writer.write(JSON_NON_EXECUTABLE_PREFIX);
    }
    JsonWriterq jsonWriter = new JsonWriterq(writer);
    if (prettyPrinting) {
      jsonWriter.setIndent("  ");
    }
    jsonWriter.setHtmlSafe(htmlSafe);
    jsonWriter.setLenient(lenient);
    jsonWriter.setSerializeNulls(serializeNulls);
    return jsonWriter;
  }

  
  public JsonReaderq newJsonReader(Reader reader) {
    JsonReaderq jsonReader = new JsonReaderq(reader);
    jsonReader.setLenient(lenient);
    return jsonReader;
  }

  
  public void toJson(JsonElementq jsonElement, JsonWriterq writer) throws JsonIOExceptionq {
    boolean oldLenient = writer.isLenient();
    writer.setLenient(true);
    boolean oldHtmlSafe = writer.isHtmlSafe();
    writer.setHtmlSafe(htmlSafe);
    boolean oldSerializeNulls = writer.getSerializeNulls();
    writer.setSerializeNulls(serializeNulls);
    try {
      Streamsq.write(jsonElement, writer);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    } catch (AssertionError e) {
      AssertionError error = new AssertionError("AssertionError (GSON " + GsonBuildConfigq.VERSION + "): " + e.getMessage());
      error.initCause(e);
      throw error;
    } finally {
      writer.setLenient(oldLenient);
      writer.setHtmlSafe(oldHtmlSafe);
      writer.setSerializeNulls(oldSerializeNulls);
    }
  }

  
  public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxExceptionq {
    Object object = fromJson(json, (Type) classOfT);
    return Primitivesq.wrap(classOfT).cast(object);
  }

  
  @SuppressWarnings("unchecked")
  public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxExceptionq {
    if (json == null) {
      return null;
    }
    StringReader reader = new StringReader(json);
    T target = (T) fromJson(reader, typeOfT);
    return target;
  }

  
  public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxExceptionq, JsonIOExceptionq {
    JsonReaderq jsonReader = newJsonReader(json);
    Object object = fromJson(jsonReader, classOfT);
    assertFullConsumption(object, jsonReader);
    return Primitivesq.wrap(classOfT).cast(object);
  }

  
  @SuppressWarnings("unchecked")
  public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOExceptionq, JsonSyntaxExceptionq {
    JsonReaderq jsonReader = newJsonReader(json);
    T object = (T) fromJson(jsonReader, typeOfT);
    assertFullConsumption(object, jsonReader);
    return object;
  }

  private static void assertFullConsumption(Object obj, JsonReaderq reader) {
    try {
      if (obj != null && reader.peek() != JsonTokenq.END_DOCUMENT) {
        throw new JsonIOExceptionq("JSON document was not fully consumed.");
      }
    } catch (MalformedJsonExceptionq e) {
      throw new JsonSyntaxExceptionq(e);
    } catch (IOException e) {
      throw new JsonIOExceptionq(e);
    }
  }

  
  @SuppressWarnings("unchecked")
  public <T> T fromJson(JsonReaderq reader, Type typeOfT) throws JsonIOExceptionq, JsonSyntaxExceptionq {
    boolean isEmpty = true;
    boolean oldLenient = reader.isLenient();
    reader.setLenient(true);
    try {
      reader.peek();
      isEmpty = false;
      TypeTokenq<T> typeToken = (TypeTokenq<T>) TypeTokenq.get(typeOfT);
      TypeAdapterqdscvvf<T> typeAdapter = getAdapter(typeToken);
      T object = typeAdapter.read(reader);
      return object;
    } catch (EOFException e) {
      
      if (isEmpty) {
        return null;
      }
      throw new JsonSyntaxExceptionq(e);
    } catch (IllegalStateException e) {
      throw new JsonSyntaxExceptionq(e);
    } catch (IOException e) {
      throw new JsonSyntaxExceptionq(e);
    } catch (AssertionError e) {
      AssertionError error = new AssertionError("AssertionError (GSON " + GsonBuildConfigq.VERSION + "): " + e.getMessage());
      error.initCause(e);
      throw error;
    } finally {
      reader.setLenient(oldLenient);
    }
  }

  
  public <T> T fromJson(JsonElementq json, Class<T> classOfT) throws JsonSyntaxExceptionq {
    Object object = fromJson(json, (Type) classOfT);
    return Primitivesq.wrap(classOfT).cast(object);
  }

  
  @SuppressWarnings("unchecked")
  public <T> T fromJson(JsonElementq json, Type typeOfT) throws JsonSyntaxExceptionq {
    if (json == null) {
      return null;
    }
    return (T) fromJson(new JsonTreeReaderqq(json), typeOfT);
  }

  static class FutureTypeAdapter<T> extends TypeAdapterqdscvvf<T> {
    private TypeAdapterqdscvvf<T> delegate;

    public void setDelegate(TypeAdapterqdscvvf<T> typeAdapter) {
      if (delegate != null) {
        throw new AssertionError();
      }
      delegate = typeAdapter;
    }

    @Override public T read(JsonReaderq in) throws IOException {
      if (delegate == null) {
        throw new IllegalStateException();
      }
      return delegate.read(in);
    }

    @Override public void write(JsonWriterq out, T value) throws IOException {
      if (delegate == null) {
        throw new IllegalStateException();
      }
      delegate.write(out, value);
    }
  }

  @Override
  public String toString() {
    return new StringBuilder("{serializeNulls:")
        .append(serializeNulls)
        .append(",factories:").append(factories)
        .append(",instanceCreators:").append(constructorConstructor)
        .append("}")
        .toString();
  }
}
