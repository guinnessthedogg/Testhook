

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb;

import com.xxx.zzz.aall.gsonllll.googlepp.InstanceCreatorq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonIOExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.reflectss.ReflectionHelperqq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public final class ConstructorConstructorq {
  private final Map<Type, InstanceCreatorq<?>> instanceCreators;
  private final boolean useJdkUnsafe;

  public ConstructorConstructorq(Map<Type, InstanceCreatorq<?>> instanceCreators, boolean useJdkUnsafe) {
    this.instanceCreators = instanceCreators;
    this.useJdkUnsafe = useJdkUnsafe;
  }

  public <T> ObjectConstructorq<T> get(TypeTokenq<T> typeToken) {
    final Type type = typeToken.getType();
    final Class<? super T> rawType = typeToken.getRawType();



    @SuppressWarnings("unchecked")
    final InstanceCreatorq<T> typeCreator = (InstanceCreatorq<T>) instanceCreators.get(type);
    if (typeCreator != null) {
      return new ObjectConstructorq<T>() {
        @Override public T construct() {
          return typeCreator.createInstance(type);
        }
      };
    }


    @SuppressWarnings("unchecked")
    final InstanceCreatorq<T> rawTypeCreator =
        (InstanceCreatorq<T>) instanceCreators.get(rawType);
    if (rawTypeCreator != null) {
      return new ObjectConstructorq<T>() {
        @Override public T construct() {
          return rawTypeCreator.createInstance(type);
        }
      };
    }

    ObjectConstructorq<T> defaultConstructor = newDefaultConstructor(rawType);
    if (defaultConstructor != null) {
      return defaultConstructor;
    }

    ObjectConstructorq<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
    if (defaultImplementation != null) {
      return defaultImplementation;
    }

    return newUnsafeAllocator(rawType);
  }

  private <T> ObjectConstructorq<T> newDefaultConstructor(Class<? super T> rawType) {
    if (Modifier.isAbstract(rawType.getModifiers())) {
      return null;
    }

    final Constructor<? super T> constructor;
    try {
      constructor = rawType.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      return null;
    }

    final String exceptionMessage = ReflectionHelperqq.tryMakeAccessible(constructor);
    if (exceptionMessage != null) {

      return new ObjectConstructorq<T>() {
        @Override
        public T construct() {



          throw new JsonIOExceptionq(exceptionMessage);
        }
      };
    }

    return new ObjectConstructorq<T>() {
      @Override public T construct() {
        try {
          @SuppressWarnings("unchecked")
          T newInstance = (T) constructor.newInstance();
          return newInstance;
        } catch (InstantiationException e) {

          throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
        } catch (InvocationTargetException e) {


          throw new RuntimeException("Failed to invoke " + constructor + " with no args",
              e.getTargetException());
        } catch (IllegalAccessException e) {
          throw new AssertionError(e);
        }
      }
    };
  }


  @SuppressWarnings("unchecked")
  private <T> ObjectConstructorq<T> newDefaultImplementationConstructor(
      final Type type, Class<? super T> rawType) {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new TreeSet<Object>();
          }
        };
      } else if (EnumSet.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @SuppressWarnings("rawtypes")
          @Override public T construct() {
            if (type instanceof ParameterizedType) {
              Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
              if (elementType instanceof Class) {
                return (T) EnumSet.noneOf((Class)elementType);
              } else {
                throw new JsonIOExceptionq("Invalid EnumSet type: " + type.toString());
              }
            } else {
              throw new JsonIOExceptionq("Invalid EnumSet type: " + type.toString());
            }
          }
        };
      } else if (Set.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new LinkedHashSet<Object>();
          }
        };
      } else if (Queue.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new ArrayDeque<Object>();
          }
        };
      } else {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new ArrayList<Object>();
          }
        };
      }
    }

    if (Map.class.isAssignableFrom(rawType)) {


      if (rawType == EnumMap.class) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            if (type instanceof ParameterizedType) {
              Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
              if (elementType instanceof Class) {
                @SuppressWarnings("rawtypes")
                T map = (T) new EnumMap((Class) elementType);
                return map;
              } else {
                throw new JsonIOExceptionq("Invalid EnumMap type: " + type.toString());
              }
            } else {
              throw new JsonIOExceptionq("Invalid EnumMap type: " + type.toString());
            }
          }
        };
      } else if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new ConcurrentSkipListMap<Object, Object>();
          }
        };
      } else if (ConcurrentMap.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new ConcurrentHashMap<Object, Object>();
          }
        };
      } else if (SortedMap.class.isAssignableFrom(rawType)) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new TreeMap<Object, Object>();
          }
        };
      } else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(
          TypeTokenq.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new LinkedHashMap<Object, Object>();
          }
        };
      } else {
        return new ObjectConstructorq<T>() {
          @Override public T construct() {
            return (T) new LinkedTreeMapq<String, Object>();
          }
        };
      }
    }

    return null;
  }

  private <T> ObjectConstructorq<T> newUnsafeAllocator(final Class<? super T> rawType) {
    if (useJdkUnsafe) {
      return new ObjectConstructorq<T>() {
        private final UnsafeAllocatorq unsafeAllocator = UnsafeAllocatorq.create();
        @Override public T construct() {
          try {
            @SuppressWarnings("unchecked")
            T newInstance = (T) unsafeAllocator.newInstance(rawType);
            return newInstance;
          } catch (Exception e) {
            throw new RuntimeException(("Unable to create instance of " + rawType + ". "
                + "Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args "
                + "constructor may fix this problem."), e);
          }
        }
      };
    } else {
      final String exceptionMessage = "Unable to create instance of " + rawType + "; usage of JDK Unsafe "
          + "is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args "
          + "constructor, or enabling usage of JDK Unsafe may fix this problem.";
      return new ObjectConstructorq<T>() {
        @Override public T construct() {
          throw new JsonIOExceptionq(exceptionMessage);
        }
      };
    }
  }

  @Override public String toString() {
    return instanceCreators.toString();
  }
}
