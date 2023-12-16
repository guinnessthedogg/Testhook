

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Preconditionsq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.DefaultaDateaTypeaAdapterqqdsa;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.TreeTypeAdapterqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.TypeAdaptersqq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.Exposeqq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.Excluderq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.sqlbb.SqlTypesSupportq;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class GsonBuilderq {
  private Excluderq excluder = Excluderq.DEFAULT;
  private LongSerializationPolicyq longSerializationPolicy = LongSerializationPolicyq.DEFAULT;
  private FieldbNamingbStrategyq fieldNamingPolicy = FieldNamingPolicyq.IDENTITY;
  private final Map<Type, InstanceCreatorq<?>> instanceCreators
      = new HashMap<Type, InstanceCreatorq<?>>();
  private final List<TypeAdapterFactoryqqeeqw> factories = new ArrayList<TypeAdapterFactoryqqeeqw>();

  private final List<TypeAdapterFactoryqqeeqw> hierarchyFactories = new ArrayList<TypeAdapterFactoryqqeeqw>();
  private boolean serializeNulls = Gsonq.DEFAULT_SERIALIZE_NULLS;
  private String datePattern = Gsonq.DEFAULT_DATE_PATTERN;
  private int dateStyle = DateFormat.DEFAULT;
  private int timeStyle = DateFormat.DEFAULT;
  private boolean complexMapKeySerialization = Gsonq.DEFAULT_COMPLEX_MAP_KEYS;
  private boolean serializeSpecialFloatingPointValues = Gsonq.DEFAULT_SPECIALIZE_FLOAT_VALUES;
  private boolean escapeHtmlChars = Gsonq.DEFAULT_ESCAPE_HTML;
  private boolean prettyPrinting = Gsonq.DEFAULT_PRETTY_PRINT;
  private boolean generateNonExecutableJson = Gsonq.DEFAULT_JSON_NON_EXECUTABLE;
  private boolean lenient = Gsonq.DEFAULT_LENIENT;
  private boolean useJdkUnsafe = Gsonq.DEFAULT_USE_JDK_UNSAFE;
  private ToNumberStrategyq objectToNumberStrategy = Gsonq.DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
  private ToNumberStrategyq numberToNumberStrategy = Gsonq.DEFAULT_NUMBER_TO_NUMBER_STRATEGY;


  public GsonBuilderq() {
  }


  GsonBuilderq(Gsonq gson) {
    this.excluder = gson.excluder;
    this.fieldNamingPolicy = gson.fieldNamingStrategy;
    this.instanceCreators.putAll(gson.instanceCreators);
    this.serializeNulls = gson.serializeNulls;
    this.complexMapKeySerialization = gson.complexMapKeySerialization;
    this.generateNonExecutableJson = gson.generateNonExecutableJson;
    this.escapeHtmlChars = gson.htmlSafe;
    this.prettyPrinting = gson.prettyPrinting;
    this.lenient = gson.lenient;
    this.serializeSpecialFloatingPointValues = gson.serializeSpecialFloatingPointValues;
    this.longSerializationPolicy = gson.longSerializationPolicy;
    this.datePattern = gson.datePattern;
    this.dateStyle = gson.dateStyle;
    this.timeStyle = gson.timeStyle;
    this.factories.addAll(gson.builderFactories);
    this.hierarchyFactories.addAll(gson.builderHierarchyFactories);
    this.useJdkUnsafe = gson.useJdkUnsafe;
    this.objectToNumberStrategy = gson.objectToNumberStrategy;
    this.numberToNumberStrategy = gson.numberToNumberStrategy;
  }


  public GsonBuilderq setVersion(double ignoreVersionsAfter) {
    excluder = excluder.withVersion(ignoreVersionsAfter);
    return this;
  }


  public GsonBuilderq excludeFieldsWithModifiers(int... modifiers) {
    excluder = excluder.withModifiers(modifiers);
    return this;
  }


  public GsonBuilderq generateNonExecutableJson() {
    this.generateNonExecutableJson = true;
    return this;
  }


  public GsonBuilderq excludeFieldsWithoutExposeAnnotation() {
    excluder = excluder.excludeFieldsWithoutExposeAnnotation();
    return this;
  }


  public GsonBuilderq serializeNulls() {
    this.serializeNulls = true;
    return this;
  }


  public GsonBuilderq enableComplexMapKeySerialization() {
    complexMapKeySerialization = true;
    return this;
  }


  public GsonBuilderq disableInnerClassSerialization() {
    excluder = excluder.disableInnerClassSerialization();
    return this;
  }


  public GsonBuilderq setLongSerializationPolicy(LongSerializationPolicyq serializationPolicy) {
    this.longSerializationPolicy = serializationPolicy;
    return this;
  }


  public GsonBuilderq setFieldNamingPolicy(FieldNamingPolicyq namingConvention) {
    this.fieldNamingPolicy = namingConvention;
    return this;
  }


  public GsonBuilderq setFieldNamingStrategy(FieldbNamingbStrategyq fieldNamingStrategy) {
    this.fieldNamingPolicy = fieldNamingStrategy;
    return this;
  }


  public GsonBuilderq setObjectToNumberStrategy(ToNumberStrategyq objectToNumberStrategy) {
    this.objectToNumberStrategy = objectToNumberStrategy;
    return this;
  }


  public GsonBuilderq setNumberToNumberStrategy(ToNumberStrategyq numberToNumberStrategy) {
    this.numberToNumberStrategy = numberToNumberStrategy;
    return this;
  }


  public GsonBuilderq setExclusionStrategies(ExclusionStrategyq... strategies) {
    for (ExclusionStrategyq strategy : strategies) {
      excluder = excluder.withExclusionStrategy(strategy, true, true);
    }
    return this;
  }


  public GsonBuilderq addSerializationExclusionStrategy(ExclusionStrategyq strategy) {
    excluder = excluder.withExclusionStrategy(strategy, true, false);
    return this;
  }


  public GsonBuilderq addDeserializationExclusionStrategy(ExclusionStrategyq strategy) {
    excluder = excluder.withExclusionStrategy(strategy, false, true);
    return this;
  }


  public GsonBuilderq setPrettyPrinting() {
    prettyPrinting = true;
    return this;
  }


  public GsonBuilderq setLenient() {
    lenient = true;
    return this;
  }


  public GsonBuilderq disableHtmlEscaping() {
    this.escapeHtmlChars = false;
    return this;
  }


  public GsonBuilderq setDateFormat(String pattern) {

    this.datePattern = pattern;
    return this;
  }


  public GsonBuilderq setDateFormat(int style) {
    this.dateStyle = style;
    this.datePattern = null;
    return this;
  }

  
  public GsonBuilderq setDateFormat(int dateStyle, int timeStyle) {
    this.dateStyle = dateStyle;
    this.timeStyle = timeStyle;
    this.datePattern = null;
    return this;
  }

  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public GsonBuilderq registerTypeAdapter(Type type, Object typeAdapter) {
    $Gson$Preconditionsq.checkArgument(typeAdapter instanceof JsonSerializerq<?>
        || typeAdapter instanceof JsonDeserializerq<?>
        || typeAdapter instanceof InstanceCreatorq<?>
        || typeAdapter instanceof TypeAdapterqdscvvf<?>);
    if (typeAdapter instanceof InstanceCreatorq<?>) {
      instanceCreators.put(type, (InstanceCreatorq) typeAdapter);
    }
    if (typeAdapter instanceof JsonSerializerq<?> || typeAdapter instanceof JsonDeserializerq<?>) {
      TypeTokenq<?> typeToken = TypeTokenq.get(type);
      factories.add(TreeTypeAdapterqq.newFactoryWithMatchRawType(typeToken, typeAdapter));
    }
    if (typeAdapter instanceof TypeAdapterqdscvvf<?>) {
      factories.add(TypeAdaptersqq.newFactory(TypeTokenq.get(type), (TypeAdapterqdscvvf)typeAdapter));
    }
    return this;
  }

  
  public GsonBuilderq registerTypeAdapterFactory(TypeAdapterFactoryqqeeqw factory) {
    factories.add(factory);
    return this;
  }

  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public GsonBuilderq registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
    $Gson$Preconditionsq.checkArgument(typeAdapter instanceof JsonSerializerq<?>
        || typeAdapter instanceof JsonDeserializerq<?>
        || typeAdapter instanceof TypeAdapterqdscvvf<?>);
    if (typeAdapter instanceof JsonDeserializerq || typeAdapter instanceof JsonSerializerq) {
      hierarchyFactories.add(TreeTypeAdapterqq.newTypeHierarchyFactory(baseType, typeAdapter));
    }
    if (typeAdapter instanceof TypeAdapterqdscvvf<?>) {
      factories.add(TypeAdaptersqq.newTypeHierarchyFactory(baseType, (TypeAdapterqdscvvf)typeAdapter));
    }
    return this;
  }

  public GsonBuilderq serializeSpecialFloatingPointValues() {
    this.serializeSpecialFloatingPointValues = true;
    return this;
  }

  
  public GsonBuilderq disableJdkUnsafe() {
    this.useJdkUnsafe = false;
    return this;
  }

  
  public Gsonq create() {
    List<TypeAdapterFactoryqqeeqw> factories = new ArrayList<TypeAdapterFactoryqqeeqw>(this.factories.size() + this.hierarchyFactories.size() + 3);
    factories.addAll(this.factories);
    Collections.reverse(factories);

    List<TypeAdapterFactoryqqeeqw> hierarchyFactories = new ArrayList<TypeAdapterFactoryqqeeqw>(this.hierarchyFactories);
    Collections.reverse(hierarchyFactories);
    factories.addAll(hierarchyFactories);

    addTypeAdaptersForDate(datePattern, dateStyle, timeStyle, factories);

    return new Gsonq(excluder, fieldNamingPolicy, instanceCreators,
        serializeNulls, complexMapKeySerialization,
        generateNonExecutableJson, escapeHtmlChars, prettyPrinting, lenient,
        serializeSpecialFloatingPointValues, useJdkUnsafe, longSerializationPolicy,
        datePattern, dateStyle, timeStyle,
        this.factories, this.hierarchyFactories, factories, objectToNumberStrategy, numberToNumberStrategy);
  }

  private void addTypeAdaptersForDate(String datePattern, int dateStyle, int timeStyle,
      List<TypeAdapterFactoryqqeeqw> factories) {
    TypeAdapterFactoryqqeeqw dateAdapterFactory;
    boolean sqlTypesSupported = SqlTypesSupportq.SUPPORTS_SQL_TYPES;
    TypeAdapterFactoryqqeeqw sqlTimestampAdapterFactory = null;
    TypeAdapterFactoryqqeeqw sqlDateAdapterFactory = null;

    if (datePattern != null && !datePattern.trim().isEmpty()) {
      dateAdapterFactory = DefaultaDateaTypeaAdapterqqdsa.DateType.DATE.createAdapterFactory(datePattern);

      if (sqlTypesSupported) {
        sqlTimestampAdapterFactory = SqlTypesSupportq.TIMESTAMP_DATE_TYPE.createAdapterFactory(datePattern);
        sqlDateAdapterFactory = SqlTypesSupportq.DATE_DATE_TYPE.createAdapterFactory(datePattern);
      }
    } else if (dateStyle != DateFormat.DEFAULT && timeStyle != DateFormat.DEFAULT) {
      dateAdapterFactory = DefaultaDateaTypeaAdapterqqdsa.DateType.DATE.createAdapterFactory(dateStyle, timeStyle);

      if (sqlTypesSupported) {
        sqlTimestampAdapterFactory = SqlTypesSupportq.TIMESTAMP_DATE_TYPE.createAdapterFactory(dateStyle, timeStyle);
        sqlDateAdapterFactory = SqlTypesSupportq.DATE_DATE_TYPE.createAdapterFactory(dateStyle, timeStyle);
      }
    } else {
      return;
    }

    factories.add(dateAdapterFactory);
    if (sqlTypesSupported) {
      factories.add(sqlTimestampAdapterFactory);
      factories.add(sqlDateAdapterFactory);
    }
  }
}
