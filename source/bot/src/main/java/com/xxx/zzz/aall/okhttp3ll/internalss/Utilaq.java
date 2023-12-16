
package com.xxx.zzz.aall.okhttp3ll.internalss;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.IDN;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.xxx.zzz.aall.okhttp3ll.ResponseBodyza;
import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.RequestBodyza;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;


public final class Utilaq {
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  public static final ResponseBodyza EMPTY_RESPONSE = ResponseBodyza.create(null, EMPTY_BYTE_ARRAY);
  public static final RequestBodyza EMPTY_REQUEST = RequestBodyza.create(null, EMPTY_BYTE_ARRAY);

  private static final ByteStringzaq UTF_8_BOM = ByteStringzaq.decodeHex("efbbbf");
  private static final ByteStringzaq UTF_16_BE_BOM = ByteStringzaq.decodeHex("feff");
  private static final ByteStringzaq UTF_16_LE_BOM = ByteStringzaq.decodeHex("fffe");
  private static final ByteStringzaq UTF_32_BE_BOM = ByteStringzaq.decodeHex("0000ffff");
  private static final ByteStringzaq UTF_32_LE_BOM = ByteStringzaq.decodeHex("ffff0000");

  public static final Charset UTF_8 = Charset.forName("UTF-8");
  private static final Charset UTF_16_BE = Charset.forName("UTF-16BE");
  private static final Charset UTF_16_LE = Charset.forName("UTF-16LE");
  private static final Charset UTF_32_BE = Charset.forName("UTF-32BE");
  private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");

  
  public static final TimeZone UTC = TimeZone.getTimeZone("GMT");

  public static final Comparator<String> NATURAL_ORDER = new Comparator<String>() {
    @Override public int compare(String a, String b) {
      return a.compareTo(b);
    }
  };

  
  private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile(
      "([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

  private Utilaq() {
  }

  public static void checkOffsetAndCount(long arrayLength, long offset, long count) {
    if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
      throw new ArrayIndexOutOfBoundsException();
    }
  }

  
  public static boolean equal(Object a, Object b) {
    return a == b || (a != null && a.equals(b));
  }

  
  public static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (RuntimeException rethrown) {
        throw rethrown;
      } catch (Exception ignored) {
      }
    }
  }

  
  public static void closeQuietly(Socket socket) {
    if (socket != null) {
      try {
        socket.close();
      } catch (AssertionError e) {
        if (!isAndroidGetsocknameError(e)) throw e;
      } catch (RuntimeException rethrown) {
        throw rethrown;
      } catch (Exception ignored) {
      }
    }
  }

  
  public static void closeQuietly(ServerSocket serverSocket) {
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (RuntimeException rethrown) {
        throw rethrown;
      } catch (Exception ignored) {
      }
    }
  }

  
  public static boolean discard(Sourcezaq source, int timeout, TimeUnit timeUnit) {
    try {
      return skipAll(source, timeout, timeUnit);
    } catch (IOException e) {
      return false;
    }
  }

  
  public static boolean skipAll(Sourcezaq source, int duration, TimeUnit timeUnit) throws IOException {
    long now = System.nanoTime();
    long originalDuration = source.timeout().hasDeadline()
        ? source.timeout().deadlineNanoTime() - now
        : Long.MAX_VALUE;
    source.timeout().deadlineNanoTime(now + Math.min(originalDuration, timeUnit.toNanos(duration)));
    try {
      Bufferzaq skipBuffer = new Bufferzaq();
      while (source.read(skipBuffer, 8192) != -1) {
        skipBuffer.clear();
      }
      return true; 
    } catch (InterruptedIOException e) {
      return false; 
    } finally {
      if (originalDuration == Long.MAX_VALUE) {
        source.timeout().clearDeadline();
      } else {
        source.timeout().deadlineNanoTime(now + originalDuration);
      }
    }
  }

  
  public static <T> List<T> immutableList(List<T> list) {
    return Collections.unmodifiableList(new ArrayList<>(list));
  }

  
  public static <T> List<T> immutableList(T... elements) {
    return Collections.unmodifiableList(Arrays.asList(elements.clone()));
  }

  public static ThreadFactory threadFactory(final String name, final boolean daemon) {
    return new ThreadFactory() {
      @Override public Thread newThread(Runnable runnable) {
        Thread result = new Thread(runnable, name);
        result.setDaemon(daemon);
        return result;
      }
    };
  }

  
  @SuppressWarnings("unchecked")
  public static String[] intersect(
      Comparator<? super String> comparator, String[] first, String[] second) {
    List<String> result = new ArrayList<>();
    for (String a : first) {
      for (String b : second) {
        if (comparator.compare(a, b) == 0) {
          result.add(a);
          break;
        }
      }
    }
    return result.toArray(new String[result.size()]);
  }

  
  public static boolean nonEmptyIntersection(
      Comparator<String> comparator, String[] first, String[] second) {
    if (first == null || second == null || first.length == 0 || second.length == 0) {
      return false;
    }
    for (String a : first) {
      for (String b : second) {
        if (comparator.compare(a, b) == 0) {
          return true;
        }
      }
    }
    return false;
  }

  public static String hostHeader(HttpUrlza url, boolean includeDefaultPort) {
    String host = url.host().contains(":")
        ? "[" + url.host() + "]"
        : url.host();
    return includeDefaultPort || url.port() != HttpUrlza.defaultPort(url.scheme())
        ? host + ":" + url.port()
        : host;
  }

  
  public static String toHumanReadableAscii(String s) {
    for (int i = 0, length = s.length(), c; i < length; i += Character.charCount(c)) {
      c = s.codePointAt(i);
      if (c > '\u001f' && c < '\u007f') continue;

      Bufferzaq buffer = new Bufferzaq();
      buffer.writeUtf8(s, 0, i);
      for (int j = i; j < length; j += Character.charCount(c)) {
        c = s.codePointAt(j);
        buffer.writeUtf8CodePoint(c > '\u001f' && c < '\u007f' ? c : '?');
      }
      return buffer.readUtf8();
    }
    return s;
  }

  
  public static boolean isAndroidGetsocknameError(AssertionError e) {
    return e.getCause() != null && e.getMessage() != null
        && e.getMessage().contains("getsockname failed");
  }

  public static int indexOf(Comparator<String> comparator, String[] array, String value) {
    for (int i = 0, size = array.length; i < size; i++) {
      if (comparator.compare(array[i], value) == 0) return i;
    }
    return -1;
  }

  public static String[] concat(String[] array, String value) {
    String[] result = new String[array.length + 1];
    System.arraycopy(array, 0, result, 0, array.length);
    result[result.length - 1] = value;
    return result;
  }

  
  public static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
    for (int i = pos; i < limit; i++) {
      switch (input.charAt(i)) {
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case ' ':
          continue;
        default:
          return i;
      }
    }
    return limit;
  }

  
  public static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
    for (int i = limit - 1; i >= pos; i--) {
      switch (input.charAt(i)) {
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case ' ':
          continue;
        default:
          return i + 1;
      }
    }
    return pos;
  }

  
  public static String trimSubstring(String string, int pos, int limit) {
    int start = skipLeadingAsciiWhitespace(string, pos, limit);
    int end = skipTrailingAsciiWhitespace(string, start, limit);
    return string.substring(start, end);
  }

  
  public static int delimiterOffset(String input, int pos, int limit, String delimiters) {
    for (int i = pos; i < limit; i++) {
      if (delimiters.indexOf(input.charAt(i)) != -1) return i;
    }
    return limit;
  }

  
  public static int delimiterOffset(String input, int pos, int limit, char delimiter) {
    for (int i = pos; i < limit; i++) {
      if (input.charAt(i) == delimiter) return i;
    }
    return limit;
  }

  
  public static String domainToAscii(String input) {
    try {
      String result = IDN.toASCII(input).toLowerCase(Locale.US);
      if (result.isEmpty()) return null;

      
      if (containsInvalidHostnameAsciiCodes(result)) {
        return null;
      }
      
      return result;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static boolean containsInvalidHostnameAsciiCodes(String hostnameAscii) {
    for (int i = 0; i < hostnameAscii.length(); i++) {
      char c = hostnameAscii.charAt(i);
      
      
      
      if (c <= '\u001f' || c >= '\u007f') {
        return true;
      }
      
      
      
      if (" #%/:?@[\\]".indexOf(c) != -1) {
        return true;
      }
    }
    return false;
  }

  
  public static int indexOfControlOrNonAscii(String input) {
    for (int i = 0, length = input.length(); i < length; i++) {
      char c = input.charAt(i);
      if (c <= '\u001f' || c >= '\u007f') {
        return i;
      }
    }
    return -1;
  }

  
  public static boolean verifyAsIpAddress(String host) {
    return VERIFY_AS_IP_ADDRESS.matcher(host).matches();
  }

  
  public static String format(String format, Object... args) {
    return String.format(Locale.US, format, args);
  }

  public static Charset bomAwareCharset(BufferedSourcezaqdfs source, Charset charset) throws IOException {
    if (source.rangeEquals(0, UTF_8_BOM)) {
      source.skip(UTF_8_BOM.size());
      return UTF_8;
    }
    if (source.rangeEquals(0, UTF_16_BE_BOM)) {
      source.skip(UTF_16_BE_BOM.size());
      return UTF_16_BE;
    }
    if (source.rangeEquals(0, UTF_16_LE_BOM)) {
      source.skip(UTF_16_LE_BOM.size());
      return UTF_16_LE;
    }
    if (source.rangeEquals(0, UTF_32_BE_BOM)) {
      source.skip(UTF_32_BE_BOM.size());
      return UTF_32_BE;
    }
    if (source.rangeEquals(0, UTF_32_LE_BOM)) {
      source.skip(UTF_32_LE_BOM.size());
      return UTF_32_LE;
    }
    return charset;
  }
}
