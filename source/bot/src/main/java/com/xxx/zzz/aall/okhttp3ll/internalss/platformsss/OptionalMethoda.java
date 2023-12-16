

package com.xxx.zzz.aall.okhttp3ll.internalss.platformsss;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


class OptionalMethoda<T> {


  private final Class<?> returnType;

  private final String methodName;

  private final Class[] methodParams;


  OptionalMethoda(Class<?> returnType, String methodName, Class... methodParams) {
    this.returnType = returnType;
    this.methodName = methodName;
    this.methodParams = methodParams;
  }


  public boolean isSupported(T target) {
    return getMethod(target.getClass()) != null;
  }


  public Object invokeOptional(T target, Object... args) throws InvocationTargetException {
    Method m = getMethod(target.getClass());
    if (m == null) {
      return null;
    }
    try {
      return m.invoke(target, args);
    } catch (IllegalAccessException e) {
      return null;
    }
  }


  public Object invokeOptionalWithoutCheckedException(T target, Object... args) {
    try {
      return invokeOptional(target, args);
    } catch (InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if (targetException instanceof RuntimeException) {
        throw (RuntimeException) targetException;
      }
      AssertionError error = new AssertionError("Unexpected exception");
      error.initCause(targetException);
      throw error;
    }
  }


  public Object invoke(T target, Object... args) throws InvocationTargetException {
    Method m = getMethod(target.getClass());
    if (m == null) {
      throw new AssertionError("Method " + methodName + " not supported for object " + target);
    }
    try {
      return m.invoke(target, args);
    } catch (IllegalAccessException e) {
      
      AssertionError error = new AssertionError("Unexpectedly could not call: " + m);
      error.initCause(e);
      throw error;
    }
  }


  public Object invokeWithoutCheckedException(T target, Object... args) {
    try {
      return invoke(target, args);
    } catch (InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if (targetException instanceof RuntimeException) {
        throw (RuntimeException) targetException;
      }
      AssertionError error = new AssertionError("Unexpected exception");
      error.initCause(targetException);
      throw error;
    }
  }


  private Method getMethod(Class<?> clazz) {
    Method method = null;
    if (methodName != null) {
      method = getPublicMethod(clazz, methodName, methodParams);
      if (method != null
          && returnType != null
          && !returnType.isAssignableFrom(method.getReturnType())) {

        
        method = null;
      }
    }
    return method;
  }

  private static Method getPublicMethod(Class<?> clazz, String methodName, Class[] parameterTypes) {
    Method method = null;
    try {
      method = clazz.getMethod(methodName, parameterTypes);
      if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
        method = null;
      }
    } catch (NoSuchMethodException e) {
      
    }
    return method;
  }
}

