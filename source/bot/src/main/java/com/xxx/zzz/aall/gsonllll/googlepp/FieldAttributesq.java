

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Preconditionsq;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;


public final class FieldAttributesq {
  private final Field field;


  public FieldAttributesq(Field f) {
    $Gson$Preconditionsq.checkNotNull(f);
    this.field = f;
  }


  public Class<?> getDeclaringClass() {
    return field.getDeclaringClass();
  }


  public String getName() {
    return field.getName();
  }


  public Type getDeclaredType() {
    return field.getGenericType();
  }


  public Class<?> getDeclaredClass() {
    return field.getType();
  }


  public <T extends Annotation> T getAnnotation(Class<T> annotation) {
    return field.getAnnotation(annotation);
  }


  public Collection<Annotation> getAnnotations() {
    return Arrays.asList(field.getAnnotations());
  }


  public boolean hasModifier(int modifier) {
    return (field.getModifiers() & modifier) != 0;
  }


  Object get(Object instance) throws IllegalAccessException {
    return field.get(instance);
  }


  boolean isSynthetic() {
    return field.isSynthetic();
  }
}
