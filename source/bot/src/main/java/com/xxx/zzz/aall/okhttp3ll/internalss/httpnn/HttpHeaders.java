
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.equal;
import static com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine.HTTP_CONTINUE;

import com.xxx.zzz.aall.okhttp3ll.Challengeza;
import com.xxx.zzz.aall.okhttp3ll.CookieJarza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xxx.zzz.aall.okhttp3ll.Cookieza;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;


public final class HttpHeaders {
  private static final String TOKEN = "([^ \"=]*)";
  private static final String QUOTED_STRING = "\"([^\"]*)\"";
  private static final Pattern PARAMETER
      = Pattern.compile(" +" + TOKEN + "=(:?" + QUOTED_STRING + "|" + TOKEN + ") *(:?,|$)");

  private HttpHeaders() {
  }

  public static long contentLength(Responseza response) {
    return contentLength(response.headers());
  }

  public static long contentLength(Headersza headers) {
    return stringToLong(headers.get("Content-Length"));
  }

  private static long stringToLong(String s) {
    if (s == null) return -1;
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException e) {
      return -1;
    }
  }


  public static boolean varyMatches(
          Responseza cachedResponse, Headersza cachedRequest, Requestza newRequest) {
    for (String field : varyFields(cachedResponse)) {
      if (!equal(cachedRequest.values(field), newRequest.headers(field))) return false;
    }
    return true;
  }


  public static boolean hasVaryAll(Responseza response) {
    return hasVaryAll(response.headers());
  }


  public static boolean hasVaryAll(Headersza responseHeaders) {
    return varyFields(responseHeaders).contains("*");
  }

  private static Set<String> varyFields(Responseza response) {
    return varyFields(response.headers());
  }


  public static Set<String> varyFields(Headersza responseHeaders) {
    Set<String> result = Collections.emptySet();
    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
      if (!"Vary".equalsIgnoreCase(responseHeaders.name(i))) continue;

      String value = responseHeaders.value(i);
      if (result.isEmpty()) {
        result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      }
      for (String varyField : value.split(",")) {
        result.add(varyField.trim());
      }
    }
    return result;
  }


  public static Headersza varyHeaders(Responseza response) {
    
    
    
    Headersza requestHeaders = response.networkResponse().request().headers();
    Headersza responseHeaders = response.headers();
    return varyHeaders(requestHeaders, responseHeaders);
  }


  public static Headersza varyHeaders(Headersza requestHeaders, Headersza responseHeaders) {
    Set<String> varyFields = varyFields(responseHeaders);
    if (varyFields.isEmpty()) return new Headersza.Builder().build();

    Headersza.Builder result = new Headersza.Builder();
    for (int i = 0, size = requestHeaders.size(); i < size; i++) {
      String fieldName = requestHeaders.name(i);
      if (varyFields.contains(fieldName)) {
        result.add(fieldName, requestHeaders.value(i));
      }
    }
    return result.build();
  }


  public static List<Challengeza> parseChallenges(Headersza responseHeaders, String challengeHeader) {
    
    
    
    
    
    List<Challengeza> challenges = new ArrayList<>();
    List<String> authenticationHeaders = responseHeaders.values(challengeHeader);
    for (String header : authenticationHeaders) {
      int index = header.indexOf(' ');
      if (index == -1) continue;

      Matcher matcher = PARAMETER.matcher(header);
      for (int i = index; matcher.find(i); i = matcher.end()) {
        if (header.regionMatches(true, matcher.start(1), "realm", 0, 5)) {
          String scheme = header.substring(0, index);
          String realm = matcher.group(3);
          if (realm != null) {
            challenges.add(new Challengeza(scheme, realm));
            break;
          }
        }
      }
    }
    return challenges;
  }

  public static void receiveHeaders(CookieJarza cookieJar, HttpUrlza url, Headersza headers) {
    if (cookieJar == CookieJarza.NO_COOKIES) return;

    List<Cookieza> cookies = Cookieza.parseAll(url, headers);
    if (cookies.isEmpty()) return;

    cookieJar.saveFromResponse(url, cookies);
  }


  public static boolean hasBody(Responseza response) {
    
    if (response.request().method().equals("HEAD")) {
      return false;
    }

    int responseCode = response.code();
    if ((responseCode < HTTP_CONTINUE || responseCode >= 200)
        && responseCode != HTTP_NO_CONTENT
        && responseCode != HTTP_NOT_MODIFIED) {
      return true;
    }

    
    
    if (contentLength(response) != -1
        || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
      return true;
    }

    return false;
  }


  public static int skipUntil(String input, int pos, String characters) {
    for (; pos < input.length(); pos++) {
      if (characters.indexOf(input.charAt(pos)) != -1) {
        break;
      }
    }
    return pos;
  }


  public static int skipWhitespace(String input, int pos) {
    for (; pos < input.length(); pos++) {
      char c = input.charAt(pos);
      if (c != ' ' && c != '\t') {
        break;
      }
    }
    return pos;
  }


  public static int parseSeconds(String value, int defaultValue) {
    try {
      long seconds = Long.parseLong(value);
      if (seconds > Integer.MAX_VALUE) {
        return Integer.MAX_VALUE;
      } else if (seconds < 0) {
        return 0;
      } else {
        return (int) seconds;
      }
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
