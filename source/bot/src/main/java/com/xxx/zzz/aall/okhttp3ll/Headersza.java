

package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class Headersza {
  private final String[] namesAndValues;

  Headersza(Builder builder) {
    this.namesAndValues = builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
  }

  private Headersza(String[] namesAndValues) {
    this.namesAndValues = namesAndValues;
  }


  public @Nullableq
  String get(String name) {
    return get(namesAndValues, name);
  }


  public @Nullableq
  Date getDate(String name) {
    String value = get(name);
    return value != null ? HttpDate.parse(value) : null;
  }


  public int size() {
    return namesAndValues.length / 2;
  }


  public String name(int index) {
    return namesAndValues[index * 2];
  }


  public String value(int index) {
    return namesAndValues[index * 2 + 1];
  }


  public Set<String> names() {
    TreeSet<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0, size = size(); i < size; i++) {
      result.add(name(i));
    }
    return Collections.unmodifiableSet(result);
  }


  public List<String> values(String name) {
    List<String> result = null;
    for (int i = 0, size = size(); i < size; i++) {
      if (name.equalsIgnoreCase(name(i))) {
        if (result == null) result = new ArrayList<>(2);
        result.add(value(i));
      }
    }
    return result != null
        ? Collections.unmodifiableList(result)
        : Collections.<String>emptyList();
  }

  public Builder newBuilder() {
    Builder result = new Builder();
    Collections.addAll(result.namesAndValues, namesAndValues);
    return result;
  }


  @Override public boolean equals(@Nullableq Object other) {
    return other instanceof Headersza
        && Arrays.equals(((Headersza) other).namesAndValues, namesAndValues);
  }

  @Override public int hashCode() {
    return Arrays.hashCode(namesAndValues);
  }

  @Override public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0, size = size(); i < size; i++) {
      result.append(name(i)).append(": ").append(value(i)).append("\n");
    }
    return result.toString();
  }

  public Map<String, List<String>> toMultimap() {
    Map<String, List<String>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0, size = size(); i < size; i++) {
      String name = name(i).toLowerCase(Locale.US);
      List<String> values = result.get(name);
      if (values == null) {
        values = new ArrayList<>(2);
        result.put(name, values);
      }
      values.add(value(i));
    }
    return result;
  }

  private static String get(String[] namesAndValues, String name) {
    for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
      if (name.equalsIgnoreCase(namesAndValues[i])) {
        return namesAndValues[i + 1];
      }
    }
    return null;
  }


  public static Headersza of(String... namesAndValues) {
    if (namesAndValues == null) throw new NullPointerException("namesAndValues == null");
    if (namesAndValues.length % 2 != 0) {
      throw new IllegalArgumentException("Expected alternating header names and values");
    }

    
    namesAndValues = namesAndValues.clone();
    for (int i = 0; i < namesAndValues.length; i++) {
      if (namesAndValues[i] == null) throw new IllegalArgumentException("Headers cannot be null");
      namesAndValues[i] = namesAndValues[i].trim();
    }

    
    for (int i = 0; i < namesAndValues.length; i += 2) {
      String name = namesAndValues[i];
      String value = namesAndValues[i + 1];
      if (name.length() == 0 || name.indexOf('\0') != -1 || value.indexOf('\0') != -1) {
        throw new IllegalArgumentException("Unexpected header: " + name + ": " + value);
      }
    }

    return new Headersza(namesAndValues);
  }


  public static Headersza of(Map<String, String> headers) {
    if (headers == null) throw new NullPointerException("headers == null");

    
    String[] namesAndValues = new String[headers.size() * 2];
    int i = 0;
    for (Map.Entry<String, String> header : headers.entrySet()) {
      if (header.getKey() == null || header.getValue() == null) {
        throw new IllegalArgumentException("Headers cannot be null");
      }
      String name = header.getKey().trim();
      String value = header.getValue().trim();
      if (name.length() == 0 || name.indexOf('\0') != -1 || value.indexOf('\0') != -1) {
        throw new IllegalArgumentException("Unexpected header: " + name + ": " + value);
      }
      namesAndValues[i] = name;
      namesAndValues[i + 1] = value;
      i += 2;
    }

    return new Headersza(namesAndValues);
  }

  public static final class Builder {
    final List<String> namesAndValues = new ArrayList<>(20);


    Builder addLenient(String line) {
      int index = line.indexOf(":", 1);
      if (index != -1) {
        return addLenient(line.substring(0, index), line.substring(index + 1));
      } else if (line.startsWith(":")) {
        
        
        return addLenient("", line.substring(1)); 
      } else {
        return addLenient("", line); 
      }
    }


    public Builder add(String line) {
      int index = line.indexOf(":");
      if (index == -1) {
        throw new IllegalArgumentException("Unexpected header: " + line);
      }
      return add(line.substring(0, index).trim(), line.substring(index + 1));
    }


    public Builder add(String name, String value) {
      checkNameAndValue(name, value);
      return addLenient(name, value);
    }


    Builder addLenient(String name, String value) {
      namesAndValues.add(name);
      namesAndValues.add(value.trim());
      return this;
    }

    public Builder removeAll(String name) {
      for (int i = 0; i < namesAndValues.size(); i += 2) {
        if (name.equalsIgnoreCase(namesAndValues.get(i))) {
          namesAndValues.remove(i); 
          namesAndValues.remove(i); 
          i -= 2;
        }
      }
      return this;
    }


    public Builder set(String name, String value) {
      checkNameAndValue(name, value);
      removeAll(name);
      addLenient(name, value);
      return this;
    }

    private void checkNameAndValue(String name, String value) {
      if (name == null) throw new NullPointerException("name == null");
      if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
      for (int i = 0, length = name.length(); i < length; i++) {
        char c = name.charAt(i);
        if (c <= '\u0020' || c >= '\u007f') {
          throw new IllegalArgumentException(Utilaq.format(
              "Unexpected char %#04x at %d in header name: %s", (int) c, i, name));
        }
      }
      if (value == null) throw new NullPointerException("value for name " + name + " == null");
      for (int i = 0, length = value.length(); i < length; i++) {
        char c = value.charAt(i);
        if ((c <= '\u001f' && c != '\t') || c >= '\u007f') {
          throw new IllegalArgumentException(Utilaq.format(
              "Unexpected char %#04x at %d in %s value: %s", (int) c, i, name, value));
        }
      }
    }


    public String get(String name) {
      for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
        if (name.equalsIgnoreCase(namesAndValues.get(i))) {
          return namesAndValues.get(i + 1);
        }
      }
      return null;
    }

    public Headersza build() {
      return new Headersza(this);
    }
  }
}
