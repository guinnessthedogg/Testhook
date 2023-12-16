

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonArrayq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonObjectq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.LazilyParsedNumberq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonElementq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonIOExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonNullq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonPrimitiveq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.annotationss.SerializedNameqq;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;


public final class TypeAdaptersqq {
  private TypeAdaptersqq() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("rawtypes")
  public static final TypeAdapterqdscvvf<Class> CLASS = new TypeAdapterqdscvvf<Class>() {
    @Override
    public void write(JsonWriterq out, Class value) throws IOException {
      throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: "
              + value.getName() + ". Forgot to register a type adapter?");
    }
    @Override
    public Class read(JsonReaderq in) throws IOException {
      throw new UnsupportedOperationException(
              "Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
    }
  }.nullSafe();

  public static final TypeAdapterFactoryqqeeqw CLASS_FACTORY = newFactory(Class.class, CLASS);

  public static final TypeAdapterqdscvvf<BitSet> BIT_SET = new TypeAdapterqdscvvf<BitSet>() {
    @Override public BitSet read(JsonReaderq in) throws IOException {
      BitSet bitset = new BitSet();
      in.beginArray();
      int i = 0;
      JsonTokenq tokenType = in.peek();
      while (tokenType != JsonTokenq.END_ARRAY) {
        boolean set;
        switch (tokenType) {
        case NUMBER:
        case STRING:
          int intValue = in.nextInt();
          if (intValue == 0) {
            set = false;
          } else if (intValue == 1) {
            set = true;
          } else {
            throw new JsonSyntaxExceptionq("Invalid bitset value " + intValue + ", expected 0 or 1; at path " + in.getPreviousPath());
          }
          break;
        case BOOLEAN:
          set = in.nextBoolean();
          break;
        default:
          throw new JsonSyntaxExceptionq("Invalid bitset value type: " + tokenType + "; at path " + in.getPath());
        }
        if (set) {
          bitset.set(i);
        }
        ++i;
        tokenType = in.peek();
      }
      in.endArray();
      return bitset;
    }

    @Override public void write(JsonWriterq out, BitSet src) throws IOException {
      out.beginArray();
      for (int i = 0, length = src.length(); i < length; i++) {
        int value = (src.get(i)) ? 1 : 0;
        out.value(value);
      }
      out.endArray();
    }
  }.nullSafe();

  public static final TypeAdapterFactoryqqeeqw BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);

  public static final TypeAdapterqdscvvf<Boolean> BOOLEAN = new TypeAdapterqdscvvf<Boolean>() {
    @Override
    public Boolean read(JsonReaderq in) throws IOException {
      JsonTokenq peek = in.peek();
      if (peek == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      } else if (peek == JsonTokenq.STRING) {

        return Boolean.parseBoolean(in.nextString());
      }
      return in.nextBoolean();
    }
    @Override
    public void write(JsonWriterq out, Boolean value) throws IOException {
      out.value(value);
    }
  };


  public static final TypeAdapterqdscvvf<Boolean> BOOLEAN_AS_STRING = new TypeAdapterqdscvvf<Boolean>() {
    @Override public Boolean read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return Boolean.valueOf(in.nextString());
    }

    @Override public void write(JsonWriterq out, Boolean value) throws IOException {
      out.value(value == null ? "null" : value.toString());
    }
  };

  public static final TypeAdapterFactoryqqeeqw BOOLEAN_FACTORY
      = newFactory(boolean.class, Boolean.class, BOOLEAN);

  public static final TypeAdapterqdscvvf<Number> BYTE = new TypeAdapterqdscvvf<Number>() {
    @Override
    public Number read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      int intValue;
      try {
        intValue = in.nextInt();
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq(e);
      }

      if (intValue > 255 || intValue < Byte.MIN_VALUE) {
        throw new JsonSyntaxExceptionq("Lossy conversion from " + intValue + " to byte; at path " + in.getPreviousPath());
      }
      return (byte) intValue;
    }
    @Override
    public void write(JsonWriterq out, Number value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterFactoryqqeeqw BYTE_FACTORY
      = newFactory(byte.class, Byte.class, BYTE);

  public static final TypeAdapterqdscvvf<Number> SHORT = new TypeAdapterqdscvvf<Number>() {
    @Override
    public Number read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      int intValue;
      try {
        intValue = in.nextInt();
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq(e);
      }

      if (intValue > 65535 || intValue < Short.MIN_VALUE) {
        throw new JsonSyntaxExceptionq("Lossy conversion from " + intValue + " to short; at path " + in.getPreviousPath());
      }
      return (short) intValue;
    }
    @Override
    public void write(JsonWriterq out, Number value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterFactoryqqeeqw SHORT_FACTORY
      = newFactory(short.class, Short.class, SHORT);

  public static final TypeAdapterqdscvvf<Number> INTEGER = new TypeAdapterqdscvvf<Number>() {
    @Override
    public Number read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      try {
        return in.nextInt();
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq(e);
      }
    }
    @Override
    public void write(JsonWriterq out, Number value) throws IOException {
      out.value(value);
    }
  };
  public static final TypeAdapterFactoryqqeeqw INTEGER_FACTORY
      = newFactory(int.class, Integer.class, INTEGER);

  public static final TypeAdapterqdscvvf<AtomicInteger> ATOMIC_INTEGER = new TypeAdapterqdscvvf<AtomicInteger>() {
    @Override public AtomicInteger read(JsonReaderq in) throws IOException {
      try {
        return new AtomicInteger(in.nextInt());
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq(e);
      }
    }
    @Override public void write(JsonWriterq out, AtomicInteger value) throws IOException {
      out.value(value.get());
    }
  }.nullSafe();
  public static final TypeAdapterFactoryqqeeqw ATOMIC_INTEGER_FACTORY =
      newFactory(AtomicInteger.class, TypeAdaptersqq.ATOMIC_INTEGER);

  public static final TypeAdapterqdscvvf<AtomicBoolean> ATOMIC_BOOLEAN = new TypeAdapterqdscvvf<AtomicBoolean>() {
    @Override public AtomicBoolean read(JsonReaderq in) throws IOException {
      return new AtomicBoolean(in.nextBoolean());
    }
    @Override public void write(JsonWriterq out, AtomicBoolean value) throws IOException {
      out.value(value.get());
    }
  }.nullSafe();
  public static final TypeAdapterFactoryqqeeqw ATOMIC_BOOLEAN_FACTORY =
      newFactory(AtomicBoolean.class, TypeAdaptersqq.ATOMIC_BOOLEAN);

  public static final TypeAdapterqdscvvf<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = new TypeAdapterqdscvvf<AtomicIntegerArray>() {
    @Override public AtomicIntegerArray read(JsonReaderq in) throws IOException {
        List<Integer> list = new ArrayList<Integer>();
        in.beginArray();
        while (in.hasNext()) {
          try {
            int integer = in.nextInt();
            list.add(integer);
          } catch (NumberFormatException e) {
            throw new JsonSyntaxExceptionq(e);
          }
        }
        in.endArray();
        int length = list.size();
        AtomicIntegerArray array = new AtomicIntegerArray(length);
        for (int i = 0; i < length; ++i) {
          array.set(i, list.get(i));
        }
        return array;
    }
    @Override public void write(JsonWriterq out, AtomicIntegerArray value) throws IOException {
      out.beginArray();
      for (int i = 0, length = value.length(); i < length; i++) {
        out.value(value.get(i));
      }
      out.endArray();
    }
  }.nullSafe();
  public static final TypeAdapterFactoryqqeeqw ATOMIC_INTEGER_ARRAY_FACTORY =
      newFactory(AtomicIntegerArray.class, TypeAdaptersqq.ATOMIC_INTEGER_ARRAY);

  public static final TypeAdapterqdscvvf<Number> LONG = new TypeAdapterqdscvvf<Number>() {
    @Override
    public Number read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      try {
        return in.nextLong();
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq(e);
      }
    }
    @Override
    public void write(JsonWriterq out, Number value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterqdscvvf<Number> FLOAT = new TypeAdapterqdscvvf<Number>() {
    @Override
    public Number read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return (float) in.nextDouble();
    }
    @Override
    public void write(JsonWriterq out, Number value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterqdscvvf<Number> DOUBLE = new TypeAdapterqdscvvf<Number>() {
    @Override
    public Number read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return in.nextDouble();
    }
    @Override
    public void write(JsonWriterq out, Number value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterqdscvvf<Character> CHARACTER = new TypeAdapterqdscvvf<Character>() {
    @Override
    public Character read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      String str = in.nextString();
      if (str.length() != 1) {
        throw new JsonSyntaxExceptionq("Expecting character, got: " + str + "; at " + in.getPreviousPath());
      }
      return str.charAt(0);
    }
    @Override
    public void write(JsonWriterq out, Character value) throws IOException {
      out.value(value == null ? null : String.valueOf(value));
    }
  };

  public static final TypeAdapterFactoryqqeeqw CHARACTER_FACTORY
      = newFactory(char.class, Character.class, CHARACTER);

  public static final TypeAdapterqdscvvf<String> STRING = new TypeAdapterqdscvvf<String>() {
    @Override
    public String read(JsonReaderq in) throws IOException {
      JsonTokenq peek = in.peek();
      if (peek == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      if (peek == JsonTokenq.BOOLEAN) {
        return Boolean.toString(in.nextBoolean());
      }
      return in.nextString();
    }
    @Override
    public void write(JsonWriterq out, String value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterqdscvvf<BigDecimal> BIG_DECIMAL = new TypeAdapterqdscvvf<BigDecimal>() {
    @Override public BigDecimal read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      String s = in.nextString();
      try {
        return new BigDecimal(s);
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as BigDecimal; at path " + in.getPreviousPath(), e);
      }
    }

    @Override public void write(JsonWriterq out, BigDecimal value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterqdscvvf<BigInteger> BIG_INTEGER = new TypeAdapterqdscvvf<BigInteger>() {
    @Override public BigInteger read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      String s = in.nextString();
      try {
        return new BigInteger(s);
      } catch (NumberFormatException e) {
        throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as BigInteger; at path " + in.getPreviousPath(), e);
      }
    }

    @Override public void write(JsonWriterq out, BigInteger value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterqdscvvf<LazilyParsedNumberq> LAZILY_PARSED_NUMBER = new TypeAdapterqdscvvf<LazilyParsedNumberq>() {



    @Override public LazilyParsedNumberq read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return new LazilyParsedNumberq(in.nextString());
    }

    @Override public void write(JsonWriterq out, LazilyParsedNumberq value) throws IOException {
      out.value(value);
    }
  };

  public static final TypeAdapterFactoryqqeeqw STRING_FACTORY = newFactory(String.class, STRING);

  public static final TypeAdapterqdscvvf<StringBuilder> STRING_BUILDER = new TypeAdapterqdscvvf<StringBuilder>() {
    @Override
    public StringBuilder read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return new StringBuilder(in.nextString());
    }
    @Override
    public void write(JsonWriterq out, StringBuilder value) throws IOException {
      out.value(value == null ? null : value.toString());
    }
  };

  public static final TypeAdapterFactoryqqeeqw STRING_BUILDER_FACTORY =
    newFactory(StringBuilder.class, STRING_BUILDER);

  public static final TypeAdapterqdscvvf<StringBuffer> STRING_BUFFER = new TypeAdapterqdscvvf<StringBuffer>() {
    @Override
    public StringBuffer read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return new StringBuffer(in.nextString());
    }
    @Override
    public void write(JsonWriterq out, StringBuffer value) throws IOException {
      out.value(value == null ? null : value.toString());
    }
  };

  public static final TypeAdapterFactoryqqeeqw STRING_BUFFER_FACTORY =
    newFactory(StringBuffer.class, STRING_BUFFER);

  public static final TypeAdapterqdscvvf<URL> URL = new TypeAdapterqdscvvf<URL>() {
    @Override
    public URL read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      String nextString = in.nextString();
      return "null".equals(nextString) ? null : new URL(nextString);
    }
    @Override
    public void write(JsonWriterq out, URL value) throws IOException {
      out.value(value == null ? null : value.toExternalForm());
    }
  };

  public static final TypeAdapterFactoryqqeeqw URL_FACTORY = newFactory(URL.class, URL);

  public static final TypeAdapterqdscvvf<URI> URI = new TypeAdapterqdscvvf<URI>() {
    @Override
    public URI read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      try {
        String nextString = in.nextString();
        return "null".equals(nextString) ? null : new URI(nextString);
      } catch (URISyntaxException e) {
        throw new JsonIOExceptionq(e);
      }
    }
    @Override
    public void write(JsonWriterq out, URI value) throws IOException {
      out.value(value == null ? null : value.toASCIIString());
    }
  };

  public static final TypeAdapterFactoryqqeeqw URI_FACTORY = newFactory(URI.class, URI);

  public static final TypeAdapterqdscvvf<InetAddress> INET_ADDRESS = new TypeAdapterqdscvvf<InetAddress>() {
    @Override
    public InetAddress read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }

      return InetAddress.getByName(in.nextString());
    }
    @Override
    public void write(JsonWriterq out, InetAddress value) throws IOException {
      out.value(value == null ? null : value.getHostAddress());
    }
  };

  public static final TypeAdapterFactoryqqeeqw INET_ADDRESS_FACTORY =
    newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);

  public static final TypeAdapterqdscvvf<UUID> UUID = new TypeAdapterqdscvvf<UUID>() {
    @Override
    public UUID read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      String s = in.nextString();
      try {
        return java.util.UUID.fromString(s);
      } catch (IllegalArgumentException e) {
        throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as UUID; at path " + in.getPreviousPath(), e);
      }
    }
    @Override
    public void write(JsonWriterq out, UUID value) throws IOException {
      out.value(value == null ? null : value.toString());
    }
  };

  public static final TypeAdapterFactoryqqeeqw UUID_FACTORY = newFactory(UUID.class, UUID);

  public static final TypeAdapterqdscvvf<Currency> CURRENCY = new TypeAdapterqdscvvf<Currency>() {
    @Override
    public Currency read(JsonReaderq in) throws IOException {
      String s = in.nextString();
      try {
        return Currency.getInstance(s);
      } catch (IllegalArgumentException e) {
        throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as Currency; at path " + in.getPreviousPath(), e);
      }
    }
    @Override
    public void write(JsonWriterq out, Currency value) throws IOException {
      out.value(value.getCurrencyCode());
    }
  }.nullSafe();
  public static final TypeAdapterFactoryqqeeqw CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);

  public static final TypeAdapterqdscvvf<Calendar> CALENDAR = new TypeAdapterqdscvvf<Calendar>() {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY_OF_MONTH = "dayOfMonth";
    private static final String HOUR_OF_DAY = "hourOfDay";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";

    @Override
    public Calendar read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return  null;
      }
      in.beginObject();
      int year = 0;
      int month = 0;
      int dayOfMonth = 0;
      int hourOfDay = 0;
      int minute = 0;
      int second = 0;
      while (in.peek() != JsonTokenq.END_OBJECT) {
        String name = in.nextName();
        int value = in.nextInt();
        if (YEAR.equals(name)) {
          year = value;
        } else if (MONTH.equals(name)) {
          month = value;
        } else if (DAY_OF_MONTH.equals(name)) {
          dayOfMonth = value;
        } else if (HOUR_OF_DAY.equals(name)) {
          hourOfDay = value;
        } else if (MINUTE.equals(name)) {
          minute = value;
        } else if (SECOND.equals(name)) {
          second = value;
        }
      }
      in.endObject();
      return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    @Override
    public void write(JsonWriterq out, Calendar value) throws IOException {
      if (value == null) {
        out.nullValue();
        return;
      }
      out.beginObject();
      out.name(YEAR);
      out.value(value.get(Calendar.YEAR));
      out.name(MONTH);
      out.value(value.get(Calendar.MONTH));
      out.name(DAY_OF_MONTH);
      out.value(value.get(Calendar.DAY_OF_MONTH));
      out.name(HOUR_OF_DAY);
      out.value(value.get(Calendar.HOUR_OF_DAY));
      out.name(MINUTE);
      out.value(value.get(Calendar.MINUTE));
      out.name(SECOND);
      out.value(value.get(Calendar.SECOND));
      out.endObject();
    }
  };

  public static final TypeAdapterFactoryqqeeqw CALENDAR_FACTORY =
    newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);

  public static final TypeAdapterqdscvvf<Locale> LOCALE = new TypeAdapterqdscvvf<Locale>() {
    @Override
    public Locale read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      String locale = in.nextString();
      StringTokenizer tokenizer = new StringTokenizer(locale, "_");
      String language = null;
      String country = null;
      String variant = null;
      if (tokenizer.hasMoreElements()) {
        language = tokenizer.nextToken();
      }
      if (tokenizer.hasMoreElements()) {
        country = tokenizer.nextToken();
      }
      if (tokenizer.hasMoreElements()) {
        variant = tokenizer.nextToken();
      }
      if (country == null && variant == null) {
        return new Locale(language);
      } else if (variant == null) {
        return new Locale(language, country);
      } else {
        return new Locale(language, country, variant);
      }
    }
    @Override
    public void write(JsonWriterq out, Locale value) throws IOException {
      out.value(value == null ? null : value.toString());
    }
  };

  public static final TypeAdapterFactoryqqeeqw LOCALE_FACTORY = newFactory(Locale.class, LOCALE);

  public static final TypeAdapterqdscvvf<JsonElementq> JSON_ELEMENT = new TypeAdapterqdscvvf<JsonElementq>() {
    @Override public JsonElementq read(JsonReaderq in) throws IOException {
      if (in instanceof JsonTreeReaderqq) {
        return ((JsonTreeReaderqq) in).nextJsonElement();
      }

      switch (in.peek()) {
      case STRING:
        return new JsonPrimitiveq(in.nextString());
      case NUMBER:
        String number = in.nextString();
        return new JsonPrimitiveq(new LazilyParsedNumberq(number));
      case BOOLEAN:
        return new JsonPrimitiveq(in.nextBoolean());
      case NULL:
        in.nextNull();
        return JsonNullq.INSTANCE;
      case BEGIN_ARRAY:
        JsonArrayq array = new JsonArrayq();
        in.beginArray();
        while (in.hasNext()) {
          array.add(read(in));
        }
        in.endArray();
        return array;
      case BEGIN_OBJECT:
        JsonObjectq object = new JsonObjectq();
        in.beginObject();
        while (in.hasNext()) {
          object.add(in.nextName(), read(in));
        }
        in.endObject();
        return object;
      case END_DOCUMENT:
      case NAME:
      case END_OBJECT:
      case END_ARRAY:
      default:
        throw new IllegalArgumentException();
      }
    }

    @Override public void write(JsonWriterq out, JsonElementq value) throws IOException {
      if (value == null || value.isJsonNull()) {
        out.nullValue();
      } else if (value.isJsonPrimitive()) {
        JsonPrimitiveq primitive = value.getAsJsonPrimitive();
        if (primitive.isNumber()) {
          out.value(primitive.getAsNumber());
        } else if (primitive.isBoolean()) {
          out.value(primitive.getAsBoolean());
        } else {
          out.value(primitive.getAsString());
        }

      } else if (value.isJsonArray()) {
        out.beginArray();
        for (JsonElementq e : value.getAsJsonArray()) {
          write(out, e);
        }
        out.endArray();

      } else if (value.isJsonObject()) {
        out.beginObject();
        for (Map.Entry<String, JsonElementq> e : value.getAsJsonObject().entrySet()) {
          out.name(e.getKey());
          write(out, e.getValue());
        }
        out.endObject();

      } else {
        throw new IllegalArgumentException("Couldn't write " + value.getClass());
      }
    }
  };

  public static final TypeAdapterFactoryqqeeqw JSON_ELEMENT_FACTORY
      = newTypeHierarchyFactory(JsonElementq.class, JSON_ELEMENT);

  private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapterqdscvvf<T> {
    private final Map<String, T> nameToConstant = new HashMap<String, T>();
    private final Map<T, String> constantToName = new HashMap<T, String>();

    public EnumTypeAdapter(final Class<T> classOfT) {
      try {



        Field[] constantFields = AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
          @Override public Field[] run() {
            Field[] fields = classOfT.getDeclaredFields();
            ArrayList<Field> constantFieldsList = new ArrayList<Field>(fields.length);
            for (Field f : fields) {
              if (f.isEnumConstant()) {
                constantFieldsList.add(f);
              }
            }

            Field[] constantFields = constantFieldsList.toArray(new Field[0]);
            AccessibleObject.setAccessible(constantFields, true);
            return constantFields;
          }
        });
        for (Field constantField : constantFields) {
          @SuppressWarnings("unchecked")
          T constant = (T)(constantField.get(null));
          String name = constant.name();
          SerializedNameqq annotation = constantField.getAnnotation(SerializedNameqq.class);
          if (annotation != null) {
            name = annotation.value();
            for (String alternate : annotation.alternate()) {
              nameToConstant.put(alternate, constant);
            }
          }
          nameToConstant.put(name, constant);
          constantToName.put(constant, name);
        }
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    @Override public T read(JsonReaderq in) throws IOException {
      if (in.peek() == JsonTokenq.NULL) {
        in.nextNull();
        return null;
      }
      return nameToConstant.get(in.nextString());
    }

    @Override public void write(JsonWriterq out, T value) throws IOException {
      out.value(value == null ? null : constantToName.get(value));
    }
  }

  public static final TypeAdapterFactoryqqeeqw ENUM_FACTORY = new TypeAdapterFactoryqqeeqw() {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
      Class<? super T> rawType = typeToken.getRawType();
      if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
        return null;
      }
      if (!rawType.isEnum()) {
        rawType = rawType.getSuperclass();
      }
      return (TypeAdapterqdscvvf<T>) new EnumTypeAdapter(rawType);
    }
  };

  public static <TT> TypeAdapterFactoryqqeeqw newFactory(
          final TypeTokenq<TT> type, final TypeAdapterqdscvvf<TT> typeAdapter) {
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
        return typeToken.equals(type) ? (TypeAdapterqdscvvf<T>) typeAdapter : null;
      }
    };
  }

  public static <TT> TypeAdapterFactoryqqeeqw newFactory(
      final Class<TT> type, final TypeAdapterqdscvvf<TT> typeAdapter) {
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
        return typeToken.getRawType() == type ? (TypeAdapterqdscvvf<T>) typeAdapter : null;
      }
      @Override public String toString() {
        return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
      }
    };
  }

  public static <TT> TypeAdapterFactoryqqeeqw newFactory(
      final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapterqdscvvf<? super TT> typeAdapter) {
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        return (rawType == unboxed || rawType == boxed) ? (TypeAdapterqdscvvf<T>) typeAdapter : null;
      }
      @Override public String toString() {
        return "Factory[type=" + boxed.getName()
            + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
      }
    };
  }

  public static <TT> TypeAdapterFactoryqqeeqw newFactoryForMultipleTypes(final Class<TT> base,
                                                                         final Class<? extends TT> sub, final TypeAdapterqdscvvf<? super TT> typeAdapter) {
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        return (rawType == base || rawType == sub) ? (TypeAdapterqdscvvf<T>) typeAdapter : null;
      }
      @Override public String toString() {
        return "Factory[type=" + base.getName()
            + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
      }
    };
  }


  public static <T1> TypeAdapterFactoryqqeeqw newTypeHierarchyFactory(
      final Class<T1> clazz, final TypeAdapterqdscvvf<T1> typeAdapter) {
    return new TypeAdapterFactoryqqeeqw() {
      @SuppressWarnings("unchecked")
      @Override public <T2> TypeAdapterqdscvvf<T2> create(Gsonq gson, TypeTokenq<T2> typeToken) {
        final Class<? super T2> requestedType = typeToken.getRawType();
        if (!clazz.isAssignableFrom(requestedType)) {
          return null;
        }
        return (TypeAdapterqdscvvf<T2>) new TypeAdapterqdscvvf<T1>() {
          @Override public void write(JsonWriterq out, T1 value) throws IOException {
            typeAdapter.write(out, value);
          }

          @Override public T1 read(JsonReaderq in) throws IOException {
            T1 result = typeAdapter.read(in);
            if (result != null && !requestedType.isInstance(result)) {
              throw new JsonSyntaxExceptionq("Expected a " + requestedType.getName()
                  + " but was " + result.getClass().getName() + "; at path " + in.getPreviousPath());
            }
            return result;
          }
        };
      }
      @Override public String toString() {
        return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
      }
    };
  }
}