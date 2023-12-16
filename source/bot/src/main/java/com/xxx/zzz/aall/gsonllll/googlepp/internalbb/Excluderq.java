

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb;

import com.xxx.zzz.aall.gsonllll.googlepp.ExclusionStrategyq;
import com.xxx.zzz.aall.gsonllll.googlepp.FieldAttributesq;
import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.Sinceqq;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.Usntilqq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.Exposeqq;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Excluderq implements TypeAdapterFactoryqqeeqw, Cloneable {
  private static final double IGNORE_VERSIONS = -1.0d;
  public static final Excluderq DEFAULT = new Excluderq();

  private double version = IGNORE_VERSIONS;
  private int modifiers = Modifier.TRANSIENT | Modifier.STATIC;
  private boolean serializeInnerClasses = true;
  private boolean requireExpose;
  private List<ExclusionStrategyq> serializationStrategies = Collections.emptyList();
  private List<ExclusionStrategyq> deserializationStrategies = Collections.emptyList();

  @Override protected Excluderq clone() {
    try {
      return (Excluderq) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  public Excluderq withVersion(double ignoreVersionsAfter) {
    Excluderq result = clone();
    result.version = ignoreVersionsAfter;
    return result;
  }

  public Excluderq withModifiers(int... modifiers) {
    Excluderq result = clone();
    result.modifiers = 0;
    for (int modifier : modifiers) {
      result.modifiers |= modifier;
    }
    return result;
  }

  public Excluderq disableInnerClassSerialization() {
    Excluderq result = clone();
    result.serializeInnerClasses = false;
    return result;
  }

  public Excluderq excludeFieldsWithoutExposeAnnotation() {
    Excluderq result = clone();
    result.requireExpose = true;
    return result;
  }

  public Excluderq withExclusionStrategy(ExclusionStrategyq exclusionStrategy,
                                         boolean serialization, boolean deserialization) {
    Excluderq result = clone();
    if (serialization) {
      result.serializationStrategies = new ArrayList<ExclusionStrategyq>(serializationStrategies);
      result.serializationStrategies.add(exclusionStrategy);
    }
    if (deserialization) {
      result.deserializationStrategies
          = new ArrayList<ExclusionStrategyq>(deserializationStrategies);
      result.deserializationStrategies.add(exclusionStrategy);
    }
    return result;
  }

  public <T> TypeAdapterqdscvvf<T> create(final Gsonq gson, final TypeTokenq<T> type) {
    Class<?> rawType = type.getRawType();
    boolean excludeClass = excludeClassChecks(rawType);

    final boolean skipSerialize = excludeClass || excludeClassInStrategy(rawType, true);
    final boolean skipDeserialize = excludeClass ||  excludeClassInStrategy(rawType, false);

    if (!skipSerialize && !skipDeserialize) {
      return null;
    }

    return new TypeAdapterqdscvvf<T>() {
      
      private TypeAdapterqdscvvf<T> delegate;

      @Override public T read(JsonReaderq in) throws IOException {
        if (skipDeserialize) {
          in.skipValue();
          return null;
        }
        return delegate().read(in);
      }

      @Override public void write(JsonWriterq out, T value) throws IOException {
        if (skipSerialize) {
          out.nullValue();
          return;
        }
        delegate().write(out, value);
      }

      private TypeAdapterqdscvvf<T> delegate() {
        TypeAdapterqdscvvf<T> d = delegate;
        return d != null
            ? d
            : (delegate = gson.getDelegateAdapter(Excluderq.this, type));
      }
    };
  }

  public boolean excludeField(Field field, boolean serialize) {
    if ((modifiers & field.getModifiers()) != 0) {
      return true;
    }

    if (version != Excluderq.IGNORE_VERSIONS
        && !isValidVersion(field.getAnnotation(Sinceqq.class), field.getAnnotation(Usntilqq.class))) {
      return true;
    }

    if (field.isSynthetic()) {
      return true;
    }

    if (requireExpose) {
      Exposeqq annotation = field.getAnnotation(Exposeqq.class);
      if (annotation == null || (serialize ? !annotation.serialize() : !annotation.deserialize())) {
        return true;
      }
    }

    if (!serializeInnerClasses && isInnerClass(field.getType())) {
      return true;
    }

    if (isAnonymousOrNonStaticLocal(field.getType())) {
      return true;
    }

    List<ExclusionStrategyq> list = serialize ? serializationStrategies : deserializationStrategies;
    if (!list.isEmpty()) {
      FieldAttributesq fieldAttributes = new FieldAttributesq(field);
      for (ExclusionStrategyq exclusionStrategy : list) {
        if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean excludeClassChecks(Class<?> clazz) {
      if (version != Excluderq.IGNORE_VERSIONS && !isValidVersion(clazz.getAnnotation(Sinceqq.class), clazz.getAnnotation(Usntilqq.class))) {
          return true;
      }

      if (!serializeInnerClasses && isInnerClass(clazz)) {
          return true;
      }

      if (isAnonymousOrNonStaticLocal(clazz)) {
          return true;
      }

      return false;
  }

  public boolean excludeClass(Class<?> clazz, boolean serialize) {
      return excludeClassChecks(clazz) ||
              excludeClassInStrategy(clazz, serialize);
  }

  private boolean excludeClassInStrategy(Class<?> clazz, boolean serialize) {
      List<ExclusionStrategyq> list = serialize ? serializationStrategies : deserializationStrategies;
      for (ExclusionStrategyq exclusionStrategy : list) {
          if (exclusionStrategy.shouldSkipClass(clazz)) {
              return true;
          }
      }
      return false;
  }

  private boolean isAnonymousOrNonStaticLocal(Class<?> clazz) {
    return !Enum.class.isAssignableFrom(clazz) && !isStatic(clazz)
        && (clazz.isAnonymousClass() || clazz.isLocalClass());
  }

  private boolean isInnerClass(Class<?> clazz) {
    return clazz.isMemberClass() && !isStatic(clazz);
  }

  private boolean isStatic(Class<?> clazz) {
    return (clazz.getModifiers() & Modifier.STATIC) != 0;
  }

  private boolean isValidVersion(Sinceqq since, Usntilqq until) {
    return isValidSince(since) && isValidUntil(until);
  }

  private boolean isValidSince(Sinceqq annotation) {
    if (annotation != null) {
      double annotationVersion = annotation.value();
      if (annotationVersion > version) {
        return false;
      }
    }
    return true;
  }

  private boolean isValidUntil(Usntilqq annotation) {
    if (annotation != null) {
      double annotationVersion = annotation.value();
      if (annotationVersion <= version) {
        return false;
      }
    }
    return true;
  }
}
