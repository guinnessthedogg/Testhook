

package com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Preconditionsq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Typesq;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;


public class TypeTokenq<T> {
  final Class<? super T> rawType;
  final Type type;
  final int hashCode;


  @SuppressWarnings("unchecked")
  protected TypeTokenq() {
    this.type = getSuperclassTypeParameter(getClass());
    this.rawType = (Class<? super T>) $Gson$Typesq.getRawType(type);
    this.hashCode = type.hashCode();
  }


  @SuppressWarnings("unchecked")
  TypeTokenq(Type type) {
    this.type = $Gson$Typesq.canonicalize($Gson$Preconditionsq.checkNotNull(type));
    this.rawType = (Class<? super T>) $Gson$Typesq.getRawType(this.type);
    this.hashCode = this.type.hashCode();
  }


  static Type getSuperclassTypeParameter(Class<?> subclass) {
    Type superclass = subclass.getGenericSuperclass();
    if (superclass instanceof Class) {
      throw new RuntimeException("Missing type parameter.");
    }
    ParameterizedType parameterized = (ParameterizedType) superclass;
    return $Gson$Typesq.canonicalize(parameterized.getActualTypeArguments()[0]);
  }


  public final Class<? super T> getRawType() {
    return rawType;
  }


  public final Type getType() {
    return type;
  }


  @Deprecated
  public boolean isAssignableFrom(Class<?> cls) {
    return isAssignableFrom((Type) cls);
  }


  @Deprecated
  public boolean isAssignableFrom(Type from) {
    if (from == null) {
      return false;
    }

    if (type.equals(from)) {
      return true;
    }

    if (type instanceof Class<?>) {
      return rawType.isAssignableFrom($Gson$Typesq.getRawType(from));
    } else if (type instanceof ParameterizedType) {
      return isAssignableFrom(from, (ParameterizedType) type,
          new HashMap<String, Type>());
    } else if (type instanceof GenericArrayType) {
      return rawType.isAssignableFrom($Gson$Typesq.getRawType(from))
          && isAssignableFrom(from, (GenericArrayType) type);
    } else {
      throw buildUnexpectedTypeError(
          type, Class.class, ParameterizedType.class, GenericArrayType.class);
    }
  }


  @Deprecated
  public boolean isAssignableFrom(TypeTokenq<?> token) {
    return isAssignableFrom(token.getType());
  }


  private static boolean isAssignableFrom(Type from, GenericArrayType to) {
    Type toGenericComponentType = to.getGenericComponentType();
    if (toGenericComponentType instanceof ParameterizedType) {
      Type t = from;
      if (from instanceof GenericArrayType) {
        t = ((GenericArrayType) from).getGenericComponentType();
      } else if (from instanceof Class<?>) {
        Class<?> classType = (Class<?>) from;
        while (classType.isArray()) {
          classType = classType.getComponentType();
        }
        t = classType;
      }
      return isAssignableFrom(t, (ParameterizedType) toGenericComponentType,
          new HashMap<String, Type>());
    }


    return true;
  }


  private static boolean isAssignableFrom(Type from, ParameterizedType to,
      Map<String, Type> typeVarMap) {

    if (from == null) {
      return false;
    }

    if (to.equals(from)) {
      return true;
    }


    Class<?> clazz = $Gson$Typesq.getRawType(from);
    ParameterizedType ptype = null;
    if (from instanceof ParameterizedType) {
      ptype = (ParameterizedType) from;
    }


    if (ptype != null) {
      Type[] tArgs = ptype.getActualTypeArguments();
      TypeVariable<?>[] tParams = clazz.getTypeParameters();
      for (int i = 0; i < tArgs.length; i++) {
        Type arg = tArgs[i];
        TypeVariable<?> var = tParams[i];
        while (arg instanceof TypeVariable<?>) {
          TypeVariable<?> v = (TypeVariable<?>) arg;
          arg = typeVarMap.get(v.getName());
        }
        typeVarMap.put(var.getName(), arg);
      }


      if (typeEquals(ptype, to, typeVarMap)) {
        return true;
      }
    }

    for (Type itype : clazz.getGenericInterfaces()) {
      if (isAssignableFrom(itype, to, new HashMap<String, Type>(typeVarMap))) {
        return true;
      }
    }


    Type sType = clazz.getGenericSuperclass();
    return isAssignableFrom(sType, to, new HashMap<String, Type>(typeVarMap));
  }


  private static boolean typeEquals(ParameterizedType from,
      ParameterizedType to, Map<String, Type> typeVarMap) {
    if (from.getRawType().equals(to.getRawType())) {
      Type[] fromArgs = from.getActualTypeArguments();
      Type[] toArgs = to.getActualTypeArguments();
      for (int i = 0; i < fromArgs.length; i++) {
        if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static AssertionError buildUnexpectedTypeError(
      Type token, Class<?>... expected) {


    StringBuilder exceptionMessage =
        new StringBuilder("Unexpected type. Expected one of: ");
    for (Class<?> clazz : expected) {
      exceptionMessage.append(clazz.getName()).append(", ");
    }
    exceptionMessage.append("but got: ").append(token.getClass().getName())
        .append(", for type token: ").append(token.toString()).append('.');

    return new AssertionError(exceptionMessage.toString());
  }


  private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
    return to.equals(from)
        || (from instanceof TypeVariable
        && to.equals(typeMap.get(((TypeVariable<?>) from).getName())));

  }

  @Override public final int hashCode() {
    return this.hashCode;
  }

  @Override public final boolean equals(Object o) {
    return o instanceof TypeTokenq<?>
        && $Gson$Typesq.equals(type, ((TypeTokenq<?>) o).type);
  }

  @Override public final String toString() {
    return $Gson$Typesq.typeToString(type);
  }


  public static TypeTokenq<?> get(Type type) {
    return new TypeTokenq<Object>(type);
  }


  public static <T> TypeTokenq<T> get(Class<T> type) {
    return new TypeTokenq<T>(type);
  }


  public static TypeTokenq<?> getParameterized(Type rawType, Type... typeArguments) {
    return new TypeTokenq<Object>($Gson$Typesq.newParameterizedTypeWithOwner(null, rawType, typeArguments));
  }


  public static TypeTokenq<?> getArray(Type componentType) {
    return new TypeTokenq<Object>($Gson$Typesq.arrayOf(componentType));
  }
}
