

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;


public final class $Gson$Typesq {
  static final Type[] EMPTY_TYPE_ARRAY = new Type[] {};

  private $Gson$Typesq() {
    throw new UnsupportedOperationException();
  }


  public static ParameterizedType newParameterizedTypeWithOwner(
      Type ownerType, Type rawType, Type... typeArguments) {
    return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
  }


  public static GenericArrayType arrayOf(Type componentType) {
    return new GenericArrayTypeImpl(componentType);
  }


  public static WildcardType subtypeOf(Type bound) {
    Type[] upperBounds;
    if (bound instanceof WildcardType) {
      upperBounds = ((WildcardType) bound).getUpperBounds();
    } else {
      upperBounds = new Type[] { bound };
    }
    return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
  }


  public static WildcardType supertypeOf(Type bound) {
    Type[] lowerBounds;
    if (bound instanceof WildcardType) {
      lowerBounds = ((WildcardType) bound).getLowerBounds();
    } else {
      lowerBounds = new Type[] { bound };
    }
    return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
  }


  public static Type canonicalize(Type type) {
    if (type instanceof Class) {
      Class<?> c = (Class<?>) type;
      return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;

    } else if (type instanceof ParameterizedType) {
      ParameterizedType p = (ParameterizedType) type;
      return new ParameterizedTypeImpl(p.getOwnerType(),
          p.getRawType(), p.getActualTypeArguments());

    } else if (type instanceof GenericArrayType) {
      GenericArrayType g = (GenericArrayType) type;
      return new GenericArrayTypeImpl(g.getGenericComponentType());

    } else if (type instanceof WildcardType) {
      WildcardType w = (WildcardType) type;
      return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());

    } else {

      return type;
    }
  }

  public static Class<?> getRawType(Type type) {
    if (type instanceof Class<?>) {

      return (Class<?>) type;

    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;




      Type rawType = parameterizedType.getRawType();
      $Gson$Preconditionsq.checkArgument(rawType instanceof Class);
      return (Class<?>) rawType;

    } else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType)type).getGenericComponentType();
      return Array.newInstance(getRawType(componentType), 0).getClass();

    } else if (type instanceof TypeVariable) {


      return Object.class;

    } else if (type instanceof WildcardType) {
      return getRawType(((WildcardType) type).getUpperBounds()[0]);

    } else {
      String className = type == null ? "null" : type.getClass().getName();
      throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
          + "GenericArrayType, but <" + type + "> is of type " + className);
    }
  }

  static boolean equal(Object a, Object b) {
    return a == b || (a != null && a.equals(b));
  }


  public static boolean equals(Type a, Type b) {
    if (a == b) {

      return true;

    } else if (a instanceof Class) {

      return a.equals(b);

    } else if (a instanceof ParameterizedType) {
      if (!(b instanceof ParameterizedType)) {
        return false;
      }


      ParameterizedType pa = (ParameterizedType) a;
      ParameterizedType pb = (ParameterizedType) b;
      return equal(pa.getOwnerType(), pb.getOwnerType())
          && pa.getRawType().equals(pb.getRawType())
          && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());

    } else if (a instanceof GenericArrayType) {
      if (!(b instanceof GenericArrayType)) {
        return false;
      }

      GenericArrayType ga = (GenericArrayType) a;
      GenericArrayType gb = (GenericArrayType) b;
      return equals(ga.getGenericComponentType(), gb.getGenericComponentType());

    } else if (a instanceof WildcardType) {
      if (!(b instanceof WildcardType)) {
        return false;
      }

      WildcardType wa = (WildcardType) a;
      WildcardType wb = (WildcardType) b;
      return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
          && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

    } else if (a instanceof TypeVariable) {
      if (!(b instanceof TypeVariable)) {
        return false;
      }
      TypeVariable<?> va = (TypeVariable<?>) a;
      TypeVariable<?> vb = (TypeVariable<?>) b;
      return va.getGenericDeclaration() == vb.getGenericDeclaration()
          && va.getName().equals(vb.getName());

    } else {

      return false;
    }
  }

  static int hashCodeOrZero(Object o) {
    return o != null ? o.hashCode() : 0;
  }

  public static String typeToString(Type type) {
    return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
  }


  static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
    if (toResolve == rawType) {
      return context;
    }


    if (toResolve.isInterface()) {
      Class<?>[] interfaces = rawType.getInterfaces();
      for (int i = 0, length = interfaces.length; i < length; i++) {
        if (interfaces[i] == toResolve) {
          return rawType.getGenericInterfaces()[i];
        } else if (toResolve.isAssignableFrom(interfaces[i])) {
          return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
        }
      }
    }


    if (!rawType.isInterface()) {
      while (rawType != Object.class) {
        Class<?> rawSupertype = rawType.getSuperclass();
        if (rawSupertype == toResolve) {
          return rawType.getGenericSuperclass();
        } else if (toResolve.isAssignableFrom(rawSupertype)) {
          return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
        }
        rawType = rawSupertype;
      }
    }


    return toResolve;
  }


  static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
    if (context instanceof WildcardType) {

      context = ((WildcardType)context).getUpperBounds()[0];
    }
    $Gson$Preconditionsq.checkArgument(supertype.isAssignableFrom(contextRawType));
    return resolve(context, contextRawType,
        $Gson$Typesq.getGenericSupertype(context, contextRawType, supertype));
  }


  public static Type getArrayComponentType(Type array) {
    return array instanceof GenericArrayType
        ? ((GenericArrayType) array).getGenericComponentType()
        : ((Class<?>) array).getComponentType();
  }


  public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
    Type collectionType = getSupertype(context, contextRawType, Collection.class);

    if (collectionType instanceof WildcardType) {
      collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
    }
    if (collectionType instanceof ParameterizedType) {
      return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
    }
    return Object.class;
  }


  public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {

    if (context == Properties.class) {
      return new Type[] { String.class, String.class };
    }

    Type mapType = getSupertype(context, contextRawType, Map.class);
    if (mapType instanceof ParameterizedType) {
      ParameterizedType mapParameterizedType = (ParameterizedType) mapType;
      return mapParameterizedType.getActualTypeArguments();
    }
    return new Type[] { Object.class, Object.class };
  }

  public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {

    return resolve(context, contextRawType, toResolve, new HashMap<TypeVariable<?>, Type>());
  }

  private static Type resolve(Type context, Class<?> contextRawType, Type toResolve,
                              Map<TypeVariable<?>, Type> visitedTypeVariables) {

    TypeVariable<?> resolving = null;
    while (true) {
      if (toResolve instanceof TypeVariable) {
        TypeVariable<?> typeVariable = (TypeVariable<?>) toResolve;
        Type previouslyResolved = visitedTypeVariables.get(typeVariable);
        if (previouslyResolved != null) {
          return (previouslyResolved == Void.TYPE) ? toResolve : previouslyResolved;
        }

        visitedTypeVariables.put(typeVariable, Void.TYPE);
        if (resolving == null) {
          resolving = typeVariable;
        }

        toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
        if (toResolve == typeVariable) {
          break;
        }

      } else if (toResolve instanceof Class && ((Class<?>) toResolve).isArray()) {
        Class<?> original = (Class<?>) toResolve;
        Type componentType = original.getComponentType();
        Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
        toResolve = equal(componentType, newComponentType)
            ? original
            : arrayOf(newComponentType);
        break;

      } else if (toResolve instanceof GenericArrayType) {
        GenericArrayType original = (GenericArrayType) toResolve;
        Type componentType = original.getGenericComponentType();
        Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
        toResolve = equal(componentType, newComponentType)
            ? original
            : arrayOf(newComponentType);
        break;

      } else if (toResolve instanceof ParameterizedType) {
        ParameterizedType original = (ParameterizedType) toResolve;
        Type ownerType = original.getOwnerType();
        Type newOwnerType = resolve(context, contextRawType, ownerType, visitedTypeVariables);
        boolean changed = !equal(newOwnerType, ownerType);

        Type[] args = original.getActualTypeArguments();
        for (int t = 0, length = args.length; t < length; t++) {
          Type resolvedTypeArgument = resolve(context, contextRawType, args[t], visitedTypeVariables);
          if (!equal(resolvedTypeArgument, args[t])) {
            if (!changed) {
              args = args.clone();
              changed = true;
            }
            args[t] = resolvedTypeArgument;
          }
        }

        toResolve = changed
            ? newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args)
            : original;
        break;

      } else if (toResolve instanceof WildcardType) {
        WildcardType original = (WildcardType) toResolve;
        Type[] originalLowerBound = original.getLowerBounds();
        Type[] originalUpperBound = original.getUpperBounds();

        if (originalLowerBound.length == 1) {
          Type lowerBound = resolve(context, contextRawType, originalLowerBound[0], visitedTypeVariables);
          if (lowerBound != originalLowerBound[0]) {
            toResolve = supertypeOf(lowerBound);
            break;
          }
        } else if (originalUpperBound.length == 1) {
          Type upperBound = resolve(context, contextRawType, originalUpperBound[0], visitedTypeVariables);
          if (upperBound != originalUpperBound[0]) {
            toResolve = subtypeOf(upperBound);
            break;
          }
        }
        toResolve = original;
        break;

      } else {
        break;
      }
    }

    if (resolving != null) {
      visitedTypeVariables.put(resolving, toResolve);
    }
    return toResolve;
  }

  static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
    Class<?> declaredByRaw = declaringClassOf(unknown);


    if (declaredByRaw == null) {
      return unknown;
    }

    Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
    if (declaredBy instanceof ParameterizedType) {
      int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
      return ((ParameterizedType) declaredBy).getActualTypeArguments()[index];
    }

    return unknown;
  }

  private static int indexOf(Object[] array, Object toFind) {
    for (int i = 0, length = array.length; i < length; i++) {
      if (toFind.equals(array[i])) {
        return i;
      }
    }
    throw new NoSuchElementException();
  }


  private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
    GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
    return genericDeclaration instanceof Class
        ? (Class<?>) genericDeclaration
        : null;
  }

  static void checkNotPrimitive(Type type) {
    $Gson$Preconditionsq.checkArgument(!(type instanceof Class<?>) || !((Class<?>) type).isPrimitive());
  }

  private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {

      if (rawType instanceof Class<?>) {
        Class<?> rawTypeAsClass = (Class<?>) rawType;
        boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers())
            || rawTypeAsClass.getEnclosingClass() == null;
        $Gson$Preconditionsq.checkArgument(ownerType != null || isStaticOrTopLevelClass);
      }

      this.ownerType = ownerType == null ? null : canonicalize(ownerType);
      this.rawType = canonicalize(rawType);
      this.typeArguments = typeArguments.clone();
      for (int t = 0, length = this.typeArguments.length; t < length; t++) {
        $Gson$Preconditionsq.checkNotNull(this.typeArguments[t]);
        checkNotPrimitive(this.typeArguments[t]);
        this.typeArguments[t] = canonicalize(this.typeArguments[t]);
      }
    }

    public Type[] getActualTypeArguments() {
      return typeArguments.clone();
    }

    public Type getRawType() {
      return rawType;
    }

    public Type getOwnerType() {
      return ownerType;
    }

    @Override public boolean equals(Object other) {
      return other instanceof ParameterizedType
          && $Gson$Typesq.equals(this, (ParameterizedType) other);
    }

    @Override public int hashCode() {
      return Arrays.hashCode(typeArguments)
          ^ rawType.hashCode()
          ^ hashCodeOrZero(ownerType);
    }

    @Override public String toString() {
      int length = typeArguments.length;
      if (length == 0) {
        return typeToString(rawType);
      }

      StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
      stringBuilder.append(typeToString(rawType)).append("<").append(typeToString(typeArguments[0]));
      for (int i = 1; i < length; i++) {
        stringBuilder.append(", ").append(typeToString(typeArguments[i]));
      }
      return stringBuilder.append(">").toString();
    }

    private static final long serialVersionUID = 0;
  }

  private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
    private final Type componentType;

    public GenericArrayTypeImpl(Type componentType) {
      this.componentType = canonicalize(componentType);
    }

    public Type getGenericComponentType() {
      return componentType;
    }

    @Override public boolean equals(Object o) {
      return o instanceof GenericArrayType
          && $Gson$Typesq.equals(this, (GenericArrayType) o);
    }

    @Override public int hashCode() {
      return componentType.hashCode();
    }

    @Override public String toString() {
      return typeToString(componentType) + "[]";
    }

    private static final long serialVersionUID = 0;
  }


  private static final class WildcardTypeImpl implements WildcardType, Serializable {
    private final Type upperBound;
    private final Type lowerBound;

    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
      $Gson$Preconditionsq.checkArgument(lowerBounds.length <= 1);
      $Gson$Preconditionsq.checkArgument(upperBounds.length == 1);

      if (lowerBounds.length == 1) {
        $Gson$Preconditionsq.checkNotNull(lowerBounds[0]);
        checkNotPrimitive(lowerBounds[0]);
        $Gson$Preconditionsq.checkArgument(upperBounds[0] == Object.class);
        this.lowerBound = canonicalize(lowerBounds[0]);
        this.upperBound = Object.class;

      } else {
        $Gson$Preconditionsq.checkNotNull(upperBounds[0]);
        checkNotPrimitive(upperBounds[0]);
        this.lowerBound = null;
        this.upperBound = canonicalize(upperBounds[0]);
      }
    }

    public Type[] getUpperBounds() {
      return new Type[] { upperBound };
    }

    public Type[] getLowerBounds() {
      return lowerBound != null ? new Type[] { lowerBound } : EMPTY_TYPE_ARRAY;
    }

    @Override public boolean equals(Object other) {
      return other instanceof WildcardType
          && $Gson$Typesq.equals(this, (WildcardType) other);
    }

    @Override public int hashCode() {

      return (lowerBound != null ? 31 + lowerBound.hashCode() : 1)
          ^ (31 + upperBound.hashCode());
    }

    @Override public String toString() {
      if (lowerBound != null) {
        return "? super " + typeToString(lowerBound);
      } else if (upperBound == Object.class) {
        return "?";
      } else {
        return "? extends " + typeToString(upperBound);
      }
    }

    private static final long serialVersionUID = 0;
  }
}
