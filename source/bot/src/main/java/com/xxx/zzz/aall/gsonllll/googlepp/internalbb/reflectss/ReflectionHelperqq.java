package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.reflectss;

import com.xxx.zzz.aall.gsonllll.googlepp.JsonIOExceptionq;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionHelperqq {
  private ReflectionHelperqq() { }


  public static void makeAccessible(Field field) throws JsonIOExceptionq {
    try {
      field.setAccessible(true);
    } catch (Exception exception) {
      throw new JsonIOExceptionq("Failed making field '" + field.getDeclaringClass().getName() + "#"
          + field.getName() + "' accessible; either change its visibility or write a custom "
          + "TypeAdapter for its declaring type", exception);
    }
  }


  private static String constructorToString(Constructor<?> constructor) {
    StringBuilder stringBuilder = new StringBuilder(constructor.getDeclaringClass().getName())
      .append('#')
      .append(constructor.getDeclaringClass().getSimpleName())
      .append('(');
    Class<?>[] parameters = constructor.getParameterTypes();
    for (int i = 0; i < parameters.length; i++) {
      if (i > 0) {
        stringBuilder.append(", ");
      }
      stringBuilder.append(parameters[i].getSimpleName());
    }

    return stringBuilder.append(')').toString();
  }


  public static String tryMakeAccessible(Constructor<?> constructor) {
    try {
      constructor.setAccessible(true);
      return null;
    } catch (Exception exception) {
      return "Failed making constructor '" + constructorToString(constructor) + "' accessible; "
          + "either change its visibility or write a custom InstanceCreator or TypeAdapter for its declaring type: "

          + exception.getMessage();
    }
  }
}
