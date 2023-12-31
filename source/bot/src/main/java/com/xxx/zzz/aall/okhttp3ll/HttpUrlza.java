
package com.xxx.zzz.aall.okhttp3ll;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.delimiterOffset;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.publicsuffix.PublicSuffixDatabase;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okioss.Bufferzaq;


public final class HttpUrlza {
  private static final char[] HEX_DIGITS =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
  static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
  static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
  static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
  static final String QUERY_ENCODE_SET = " \"'<>#";
  static final String QUERY_COMPONENT_ENCODE_SET = " \"'<>#&=";
  static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
  static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
  static final String FRAGMENT_ENCODE_SET = "";
  static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";


  final String scheme;


  private final String username;


  private final String password;


  final String host;


  final int port;


  private final List<String> pathSegments;


  private final @Nullableq
  List<String> queryNamesAndValues;


  private final @Nullableq
  String fragment;


  private final String url;

  HttpUrlza(Builder builder) {
    this.scheme = builder.scheme;
    this.username = percentDecode(builder.encodedUsername, false);
    this.password = percentDecode(builder.encodedPassword, false);
    this.host = builder.host;
    this.port = builder.effectivePort();
    this.pathSegments = percentDecode(builder.encodedPathSegments, false);
    this.queryNamesAndValues = builder.encodedQueryNamesAndValues != null
        ? percentDecode(builder.encodedQueryNamesAndValues, true)
        : null;
    this.fragment = builder.encodedFragment != null
        ? percentDecode(builder.encodedFragment, false)
        : null;
    this.url = builder.toString();
  }


  public URL url() {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e); 
    }
  }


  public URI uri() {
    String uri = newBuilder().reencodeForUri().toString();
    try {
      return new URI(uri);
    } catch (URISyntaxException e) {
      
      try {
        String stripped = uri.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", "");
        return URI.create(stripped);
      } catch (Exception e1) {
        throw new RuntimeException(e); 
      }
    }
  }


  public String scheme() {
    return scheme;
  }

  public boolean isHttps() {
    return scheme.equals("https");
  }


  public String encodedUsername() {
    if (username.isEmpty()) return "";
    int usernameStart = scheme.length() + 3; 
    int usernameEnd = Utilaq.delimiterOffset(url, usernameStart, url.length(), ":@");
    return url.substring(usernameStart, usernameEnd);
  }


  public String username() {
    return username;
  }


  public String encodedPassword() {
    if (password.isEmpty()) return "";
    int passwordStart = url.indexOf(':', scheme.length() + 3) + 1;
    int passwordEnd = url.indexOf('@');
    return url.substring(passwordStart, passwordEnd);
  }


  public String password() {
    return password;
  }


  public String host() {
    return host;
  }


  public int port() {
    return port;
  }


  public static int defaultPort(String scheme) {
    if (scheme.equals("http")) {
      return 80;
    } else if (scheme.equals("https")) {
      return 443;
    } else {
      return -1;
    }
  }


  public int pathSize() {
    return pathSegments.size();
  }


  public String encodedPath() {
    int pathStart = url.indexOf('/', scheme.length() + 3); 
    int pathEnd = Utilaq.delimiterOffset(url, pathStart, url.length(), "?#");
    return url.substring(pathStart, pathEnd);
  }

  static void pathSegmentsToString(StringBuilder out, List<String> pathSegments) {
    for (int i = 0, size = pathSegments.size(); i < size; i++) {
      out.append('/');
      out.append(pathSegments.get(i));
    }
  }


  public List<String> encodedPathSegments() {
    int pathStart = url.indexOf('/', scheme.length() + 3);
    int pathEnd = Utilaq.delimiterOffset(url, pathStart, url.length(), "?#");
    List<String> result = new ArrayList<>();
    for (int i = pathStart; i < pathEnd; ) {
      i++; 
      int segmentEnd = Utilaq.delimiterOffset(url, i, pathEnd, '/');
      result.add(url.substring(i, segmentEnd));
      i = segmentEnd;
    }
    return result;
  }


  public List<String> pathSegments() {
    return pathSegments;
  }


  public @Nullableq
  String encodedQuery() {
    if (queryNamesAndValues == null) return null; 
    int queryStart = url.indexOf('?') + 1;
    int queryEnd = Utilaq.delimiterOffset(url, queryStart + 1, url.length(), '#');
    return url.substring(queryStart, queryEnd);
  }

  static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
    for (int i = 0, size = namesAndValues.size(); i < size; i += 2) {
      String name = namesAndValues.get(i);
      String value = namesAndValues.get(i + 1);
      if (i > 0) out.append('&');
      out.append(name);
      if (value != null) {
        out.append('=');
        out.append(value);
      }
    }
  }


  static List<String> queryStringToNamesAndValues(String encodedQuery) {
    List<String> result = new ArrayList<>();
    for (int pos = 0; pos <= encodedQuery.length(); ) {
      int ampersandOffset = encodedQuery.indexOf('&', pos);
      if (ampersandOffset == -1) ampersandOffset = encodedQuery.length();

      int equalsOffset = encodedQuery.indexOf('=', pos);
      if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
        result.add(encodedQuery.substring(pos, ampersandOffset));
        result.add(null); 
      } else {
        result.add(encodedQuery.substring(pos, equalsOffset));
        result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
      }
      pos = ampersandOffset + 1;
    }
    return result;
  }


  public @Nullableq
  String query() {
    if (queryNamesAndValues == null) return null; 
    StringBuilder result = new StringBuilder();
    namesAndValuesToQueryString(result, queryNamesAndValues);
    return result.toString();
  }


  public int querySize() {
    return queryNamesAndValues != null ? queryNamesAndValues.size() / 2 : 0;
  }


  public @Nullableq
  String queryParameter(String name) {
    if (queryNamesAndValues == null) return null;
    for (int i = 0, size = queryNamesAndValues.size(); i < size; i += 2) {
      if (name.equals(queryNamesAndValues.get(i))) {
        return queryNamesAndValues.get(i + 1);
      }
    }
    return null;
  }


  public Set<String> queryParameterNames() {
    if (queryNamesAndValues == null) return Collections.emptySet();
    Set<String> result = new LinkedHashSet<>();
    for (int i = 0, size = queryNamesAndValues.size(); i < size; i += 2) {
      result.add(queryNamesAndValues.get(i));
    }
    return Collections.unmodifiableSet(result);
  }


  public List<String> queryParameterValues(String name) {
    if (queryNamesAndValues == null) return Collections.emptyList();
    List<String> result = new ArrayList<>();
    for (int i = 0, size = queryNamesAndValues.size(); i < size; i += 2) {
      if (name.equals(queryNamesAndValues.get(i))) {
        result.add(queryNamesAndValues.get(i + 1));
      }
    }
    return Collections.unmodifiableList(result);
  }


  public String queryParameterName(int index) {
    if (queryNamesAndValues == null) throw new IndexOutOfBoundsException();
    return queryNamesAndValues.get(index * 2);
  }


  public String queryParameterValue(int index) {
    if (queryNamesAndValues == null) throw new IndexOutOfBoundsException();
    return queryNamesAndValues.get(index * 2 + 1);
  }


  public @Nullableq
  String encodedFragment() {
    if (fragment == null) return null;
    int fragmentStart = url.indexOf('#') + 1;
    return url.substring(fragmentStart);
  }


  public @Nullableq
  String fragment() {
    return fragment;
  }


  public String redact() {
    return newBuilder("/...")
        .username("")
        .password("")
        .build()
        .toString();
  }


  public @Nullableq
  HttpUrlza resolve(String link) {
    Builder builder = newBuilder(link);
    return builder != null ? builder.build() : null;
  }

  public Builder newBuilder() {
    Builder result = new Builder();
    result.scheme = scheme;
    result.encodedUsername = encodedUsername();
    result.encodedPassword = encodedPassword();
    result.host = host;
    
    result.port = port != defaultPort(scheme) ? port : -1;
    result.encodedPathSegments.clear();
    result.encodedPathSegments.addAll(encodedPathSegments());
    result.encodedQuery(encodedQuery());
    result.encodedFragment = encodedFragment();
    return result;
  }


  public @Nullableq
  Builder newBuilder(String link) {
    Builder builder = new Builder();
    Builder.ParseResult result = builder.parse(this, link);
    return result == Builder.ParseResult.SUCCESS ? builder : null;
  }


  public static @Nullableq
  HttpUrlza parse(String url) {
    Builder builder = new Builder();
    Builder.ParseResult result = builder.parse(null, url);
    return result == Builder.ParseResult.SUCCESS ? builder.build() : null;
  }


  public static @Nullableq
  HttpUrlza get(URL url) {
    return parse(url.toString());
  }


  static HttpUrlza getChecked(String url) throws MalformedURLException, UnknownHostException {
    Builder builder = new Builder();
    Builder.ParseResult result = builder.parse(null, url);
    switch (result) {
      case SUCCESS:
        return builder.build();
      case INVALID_HOST:
        throw new UnknownHostException("Invalid host: " + url);
      case UNSUPPORTED_SCHEME:
      case MISSING_SCHEME:
      case INVALID_PORT:
      default:
        throw new MalformedURLException("Invalid URL: " + result + " for " + url);
    }
  }

  public static @Nullableq
  HttpUrlza get(URI uri) {
    return parse(uri.toString());
  }

  @Override public boolean equals(@Nullableq Object other) {
    return other instanceof HttpUrlza && ((HttpUrlza) other).url.equals(url);
  }

  @Override public int hashCode() {
    return url.hashCode();
  }

  @Override public String toString() {
    return url;
  }


  public @Nullableq
  String topPrivateDomain() {
    if (Utilaq.verifyAsIpAddress(host)) return null;
    return PublicSuffixDatabase.get().getEffectiveTldPlusOne(host);
  }

  public static final class Builder {
    @Nullableq
    String scheme;
    String encodedUsername = "";
    String encodedPassword = "";
    @Nullableq
    String host;
    int port = -1;
    final List<String> encodedPathSegments = new ArrayList<>();
    @Nullableq
    List<String> encodedQueryNamesAndValues;
    @Nullableq
    String encodedFragment;

    public Builder() {
      encodedPathSegments.add(""); 
    }

    public Builder scheme(String scheme) {
      if (scheme == null) {
        throw new NullPointerException("scheme == null");
      } else if (scheme.equalsIgnoreCase("http")) {
        this.scheme = "http";
      } else if (scheme.equalsIgnoreCase("https")) {
        this.scheme = "https";
      } else {
        throw new IllegalArgumentException("unexpected scheme: " + scheme);
      }
      return this;
    }

    public Builder username(String username) {
      if (username == null) throw new NullPointerException("username == null");
      this.encodedUsername = canonicalize(username, USERNAME_ENCODE_SET, false, false, false, true);
      return this;
    }

    public Builder encodedUsername(String encodedUsername) {
      if (encodedUsername == null) throw new NullPointerException("encodedUsername == null");
      this.encodedUsername = canonicalize(
          encodedUsername, USERNAME_ENCODE_SET, true, false, false, true);
      return this;
    }

    public Builder password(String password) {
      if (password == null) throw new NullPointerException("password == null");
      this.encodedPassword = canonicalize(password, PASSWORD_ENCODE_SET, false, false, false, true);
      return this;
    }

    public Builder encodedPassword(String encodedPassword) {
      if (encodedPassword == null) throw new NullPointerException("encodedPassword == null");
      this.encodedPassword = canonicalize(
          encodedPassword, PASSWORD_ENCODE_SET, true, false, false, true);
      return this;
    }


    public Builder host(String host) {
      if (host == null) throw new NullPointerException("host == null");
      String encoded = canonicalizeHost(host, 0, host.length());
      if (encoded == null) throw new IllegalArgumentException("unexpected host: " + host);
      this.host = encoded;
      return this;
    }

    public Builder port(int port) {
      if (port <= 0 || port > 65535) throw new IllegalArgumentException("unexpected port: " + port);
      this.port = port;
      return this;
    }

    int effectivePort() {
      return port != -1 ? port : defaultPort(scheme);
    }

    public Builder addPathSegment(String pathSegment) {
      if (pathSegment == null) throw new NullPointerException("pathSegment == null");
      push(pathSegment, 0, pathSegment.length(), false, false);
      return this;
    }


    public Builder addPathSegments(String pathSegments) {
      if (pathSegments == null) throw new NullPointerException("pathSegments == null");
      return addPathSegments(pathSegments, false);
    }

    public Builder addEncodedPathSegment(String encodedPathSegment) {
      if (encodedPathSegment == null) {
        throw new NullPointerException("encodedPathSegment == null");
      }
      push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
      return this;
    }


    public Builder addEncodedPathSegments(String encodedPathSegments) {
      if (encodedPathSegments == null) {
        throw new NullPointerException("encodedPathSegments == null");
      }
      return addPathSegments(encodedPathSegments, true);
    }

    private Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
      int offset = 0;
      do {
        int segmentEnd = Utilaq.delimiterOffset(pathSegments, offset, pathSegments.length(), "/\\");
        boolean addTrailingSlash = segmentEnd < pathSegments.length();
        push(pathSegments, offset, segmentEnd, addTrailingSlash, alreadyEncoded);
        offset = segmentEnd + 1;
      } while (offset <= pathSegments.length());
      return this;
    }

    public Builder setPathSegment(int index, String pathSegment) {
      if (pathSegment == null) throw new NullPointerException("pathSegment == null");
      String canonicalPathSegment = canonicalize(
          pathSegment, 0, pathSegment.length(), PATH_SEGMENT_ENCODE_SET, false, false, false, true);
      if (isDot(canonicalPathSegment) || isDotDot(canonicalPathSegment)) {
        throw new IllegalArgumentException("unexpected path segment: " + pathSegment);
      }
      encodedPathSegments.set(index, canonicalPathSegment);
      return this;
    }

    public Builder setEncodedPathSegment(int index, String encodedPathSegment) {
      if (encodedPathSegment == null) {
        throw new NullPointerException("encodedPathSegment == null");
      }
      String canonicalPathSegment = canonicalize(encodedPathSegment,
          0, encodedPathSegment.length(), PATH_SEGMENT_ENCODE_SET, true, false, false, true);
      encodedPathSegments.set(index, canonicalPathSegment);
      if (isDot(canonicalPathSegment) || isDotDot(canonicalPathSegment)) {
        throw new IllegalArgumentException("unexpected path segment: " + encodedPathSegment);
      }
      return this;
    }

    public Builder removePathSegment(int index) {
      encodedPathSegments.remove(index);
      if (encodedPathSegments.isEmpty()) {
        encodedPathSegments.add(""); 
      }
      return this;
    }

    public Builder encodedPath(String encodedPath) {
      if (encodedPath == null) throw new NullPointerException("encodedPath == null");
      if (!encodedPath.startsWith("/")) {
        throw new IllegalArgumentException("unexpected encodedPath: " + encodedPath);
      }
      resolvePath(encodedPath, 0, encodedPath.length());
      return this;
    }

    public Builder query(@Nullableq String query) {
      this.encodedQueryNamesAndValues = query != null
          ? queryStringToNamesAndValues(canonicalize(
          query, QUERY_ENCODE_SET, false, false, true, true))
          : null;
      return this;
    }

    public Builder encodedQuery(@Nullableq String encodedQuery) {
      this.encodedQueryNamesAndValues = encodedQuery != null
          ? queryStringToNamesAndValues(
          canonicalize(encodedQuery, QUERY_ENCODE_SET, true, false, true, true))
          : null;
      return this;
    }


    public Builder addQueryParameter(String name, @Nullableq String value) {
      if (name == null) throw new NullPointerException("name == null");
      if (encodedQueryNamesAndValues == null) encodedQueryNamesAndValues = new ArrayList<>();
      encodedQueryNamesAndValues.add(
          canonicalize(name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
      encodedQueryNamesAndValues.add(value != null
          ? canonicalize(value, QUERY_COMPONENT_ENCODE_SET, false, false, true, true)
          : null);
      return this;
    }


    public Builder addEncodedQueryParameter(String encodedName, @Nullableq String encodedValue) {
      if (encodedName == null) throw new NullPointerException("encodedName == null");
      if (encodedQueryNamesAndValues == null) encodedQueryNamesAndValues = new ArrayList<>();
      encodedQueryNamesAndValues.add(
          canonicalize(encodedName, QUERY_COMPONENT_ENCODE_SET, true, false, true, true));
      encodedQueryNamesAndValues.add(encodedValue != null
          ? canonicalize(encodedValue, QUERY_COMPONENT_ENCODE_SET, true, false, true, true)
          : null);
      return this;
    }

    public Builder setQueryParameter(String name, @Nullableq String value) {
      removeAllQueryParameters(name);
      addQueryParameter(name, value);
      return this;
    }

    public Builder setEncodedQueryParameter(String encodedName, @Nullableq String encodedValue) {
      removeAllEncodedQueryParameters(encodedName);
      addEncodedQueryParameter(encodedName, encodedValue);
      return this;
    }

    public Builder removeAllQueryParameters(String name) {
      if (name == null) throw new NullPointerException("name == null");
      if (encodedQueryNamesAndValues == null) return this;
      String nameToRemove = canonicalize(
          name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
      removeAllCanonicalQueryParameters(nameToRemove);
      return this;
    }

    public Builder removeAllEncodedQueryParameters(String encodedName) {
      if (encodedName == null) throw new NullPointerException("encodedName == null");
      if (encodedQueryNamesAndValues == null) return this;
      removeAllCanonicalQueryParameters(
          canonicalize(encodedName, QUERY_COMPONENT_ENCODE_SET, true, false, true, true));
      return this;
    }

    private void removeAllCanonicalQueryParameters(String canonicalName) {
      for (int i = encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
        if (canonicalName.equals(encodedQueryNamesAndValues.get(i))) {
          encodedQueryNamesAndValues.remove(i + 1);
          encodedQueryNamesAndValues.remove(i);
          if (encodedQueryNamesAndValues.isEmpty()) {
            encodedQueryNamesAndValues = null;
            return;
          }
        }
      }
    }

    public Builder fragment(@Nullableq String fragment) {
      this.encodedFragment = fragment != null
          ? canonicalize(fragment, FRAGMENT_ENCODE_SET, false, false, false, false)
          : null;
      return this;
    }

    public Builder encodedFragment(@Nullableq String encodedFragment) {
      this.encodedFragment = encodedFragment != null
          ? canonicalize(encodedFragment, FRAGMENT_ENCODE_SET, true, false, false, false)
          : null;
      return this;
    }


    Builder reencodeForUri() {
      for (int i = 0, size = encodedPathSegments.size(); i < size; i++) {
        String pathSegment = encodedPathSegments.get(i);
        encodedPathSegments.set(i,
            canonicalize(pathSegment, PATH_SEGMENT_ENCODE_SET_URI, true, true, false, true));
      }
      if (encodedQueryNamesAndValues != null) {
        for (int i = 0, size = encodedQueryNamesAndValues.size(); i < size; i++) {
          String component = encodedQueryNamesAndValues.get(i);
          if (component != null) {
            encodedQueryNamesAndValues.set(i,
                canonicalize(component, QUERY_COMPONENT_ENCODE_SET_URI, true, true, true, true));
          }
        }
      }
      if (encodedFragment != null) {
        encodedFragment = canonicalize(
            encodedFragment, FRAGMENT_ENCODE_SET_URI, true, true, false, false);
      }
      return this;
    }

    public HttpUrlza build() {
      if (scheme == null) throw new IllegalStateException("scheme == null");
      if (host == null) throw new IllegalStateException("host == null");
      return new HttpUrlza(this);
    }

    @Override public String toString() {
      StringBuilder result = new StringBuilder();
      result.append(scheme);
      result.append("://");

      if (!encodedUsername.isEmpty() || !encodedPassword.isEmpty()) {
        result.append(encodedUsername);
        if (!encodedPassword.isEmpty()) {
          result.append(':');
          result.append(encodedPassword);
        }
        result.append('@');
      }

      if (host.indexOf(':') != -1) {
        
        result.append('[');
        result.append(host);
        result.append(']');
      } else {
        result.append(host);
      }

      int effectivePort = effectivePort();
      if (effectivePort != defaultPort(scheme)) {
        result.append(':');
        result.append(effectivePort);
      }

      pathSegmentsToString(result, encodedPathSegments);

      if (encodedQueryNamesAndValues != null) {
        result.append('?');
        namesAndValuesToQueryString(result, encodedQueryNamesAndValues);
      }

      if (encodedFragment != null) {
        result.append('#');
        result.append(encodedFragment);
      }

      return result.toString();
    }

    enum ParseResult {
      SUCCESS,
      MISSING_SCHEME,
      UNSUPPORTED_SCHEME,
      INVALID_PORT,
      INVALID_HOST,
    }

    ParseResult parse(@Nullableq HttpUrlza base, String input) {
      int pos = Utilaq.skipLeadingAsciiWhitespace(input, 0, input.length());
      int limit = Utilaq.skipTrailingAsciiWhitespace(input, pos, input.length());

      
      int schemeDelimiterOffset = schemeDelimiterOffset(input, pos, limit);
      if (schemeDelimiterOffset != -1) {
        if (input.regionMatches(true, pos, "https:", 0, 6)) {
          this.scheme = "https";
          pos += "https:".length();
        } else if (input.regionMatches(true, pos, "http:", 0, 5)) {
          this.scheme = "http";
          pos += "http:".length();
        } else {
          return ParseResult.UNSUPPORTED_SCHEME; 
        }
      } else if (base != null) {
        this.scheme = base.scheme;
      } else {
        return ParseResult.MISSING_SCHEME; 
      }

      
      boolean hasUsername = false;
      boolean hasPassword = false;
      int slashCount = slashCount(input, pos, limit);
      if (slashCount >= 2 || base == null || !base.scheme.equals(this.scheme)) {
        pos += slashCount;
        authority:
        while (true) {
          int componentDelimiterOffset = Utilaq.delimiterOffset(input, pos, limit, "@/\\?#");
          int c = componentDelimiterOffset != limit
              ? input.charAt(componentDelimiterOffset)
              : -1;
          switch (c) {
            case '@':
              
              if (!hasPassword) {
                int passwordColonOffset = Utilaq.delimiterOffset(
                    input, pos, componentDelimiterOffset, ':');
                String canonicalUsername = canonicalize(
                    input, pos, passwordColonOffset, USERNAME_ENCODE_SET, true, false, false, true);
                this.encodedUsername = hasUsername
                    ? this.encodedUsername + "%40" + canonicalUsername
                    : canonicalUsername;
                if (passwordColonOffset != componentDelimiterOffset) {
                  hasPassword = true;
                  this.encodedPassword = canonicalize(input, passwordColonOffset + 1,
                      componentDelimiterOffset, PASSWORD_ENCODE_SET, true, false, false, true);
                }
                hasUsername = true;
              } else {
                this.encodedPassword = this.encodedPassword + "%40" + canonicalize(input, pos,
                    componentDelimiterOffset, PASSWORD_ENCODE_SET, true, false, false, true);
              }
              pos = componentDelimiterOffset + 1;
              break;

            case -1:
            case '/':
            case '\\':
            case '?':
            case '#':
              
              int portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
              if (portColonOffset + 1 < componentDelimiterOffset) {
                this.host = canonicalizeHost(input, pos, portColonOffset);
                this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                if (this.port == -1) return ParseResult.INVALID_PORT; 
              } else {
                this.host = canonicalizeHost(input, pos, portColonOffset);
                this.port = defaultPort(this.scheme);
              }
              if (this.host == null) return ParseResult.INVALID_HOST; 
              pos = componentDelimiterOffset;
              break authority;
          }
        }
      } else {
        
        this.encodedUsername = base.encodedUsername();
        this.encodedPassword = base.encodedPassword();
        this.host = base.host;
        this.port = base.port;
        this.encodedPathSegments.clear();
        this.encodedPathSegments.addAll(base.encodedPathSegments());
        if (pos == limit || input.charAt(pos) == '#') {
          encodedQuery(base.encodedQuery());
        }
      }

      
      int pathDelimiterOffset = Utilaq.delimiterOffset(input, pos, limit, "?#");
      resolvePath(input, pos, pathDelimiterOffset);
      pos = pathDelimiterOffset;

      
      if (pos < limit && input.charAt(pos) == '?') {
        int queryDelimiterOffset = Utilaq.delimiterOffset(input, pos, limit, '#');
        this.encodedQueryNamesAndValues = queryStringToNamesAndValues(canonicalize(
            input, pos + 1, queryDelimiterOffset, QUERY_ENCODE_SET, true, false, true, true));
        pos = queryDelimiterOffset;
      }

      
      if (pos < limit && input.charAt(pos) == '#') {
        this.encodedFragment = canonicalize(
            input, pos + 1, limit, FRAGMENT_ENCODE_SET, true, false, false, false);
      }

      return ParseResult.SUCCESS;
    }

    private void resolvePath(String input, int pos, int limit) {
      
      if (pos == limit) {
        
        return;
      }
      char c = input.charAt(pos);
      if (c == '/' || c == '\\') {
        
        encodedPathSegments.clear();
        encodedPathSegments.add("");
        pos++;
      } else {
        
        encodedPathSegments.set(encodedPathSegments.size() - 1, "");
      }

      
      for (int i = pos; i < limit; ) {
        int pathSegmentDelimiterOffset = Utilaq.delimiterOffset(input, i, limit, "/\\");
        boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
        push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
        i = pathSegmentDelimiterOffset;
        if (segmentHasTrailingSlash) i++;
      }
    }


    private void push(String input, int pos, int limit, boolean addTrailingSlash,
        boolean alreadyEncoded) {
      String segment = canonicalize(
          input, pos, limit, PATH_SEGMENT_ENCODE_SET, alreadyEncoded, false, false, true);
      if (isDot(segment)) {
        return; 
      }
      if (isDotDot(segment)) {
        pop();
        return;
      }
      if (encodedPathSegments.get(encodedPathSegments.size() - 1).isEmpty()) {
        encodedPathSegments.set(encodedPathSegments.size() - 1, segment);
      } else {
        encodedPathSegments.add(segment);
      }
      if (addTrailingSlash) {
        encodedPathSegments.add("");
      }
    }

    private boolean isDot(String input) {
      return input.equals(".") || input.equalsIgnoreCase("%2e");
    }

    private boolean isDotDot(String input) {
      return input.equals("..")
          || input.equalsIgnoreCase("%2e.")
          || input.equalsIgnoreCase(".%2e")
          || input.equalsIgnoreCase("%2e%2e");
    }


    private void pop() {
      String removed = encodedPathSegments.remove(encodedPathSegments.size() - 1);

      
      if (removed.isEmpty() && !encodedPathSegments.isEmpty()) {
        encodedPathSegments.set(encodedPathSegments.size() - 1, "");
      } else {
        encodedPathSegments.add("");
      }
    }


    private static int schemeDelimiterOffset(String input, int pos, int limit) {
      if (limit - pos < 2) return -1;

      char c0 = input.charAt(pos);
      if ((c0 < 'a' || c0 > 'z') && (c0 < 'A' || c0 > 'Z')) return -1; 

      for (int i = pos + 1; i < limit; i++) {
        char c = input.charAt(i);

        if ((c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || (c >= '0' && c <= '9')
            || c == '+'
            || c == '-'
            || c == '.') {
          continue; 
        } else if (c == ':') {
          return i; 
        } else {
          return -1; 
        }
      }

      return -1; 
    }


    private static int slashCount(String input, int pos, int limit) {
      int slashCount = 0;
      while (pos < limit) {
        char c = input.charAt(pos);
        if (c == '\\' || c == '/') {
          slashCount++;
          pos++;
        } else {
          break;
        }
      }
      return slashCount;
    }


    private static int portColonOffset(String input, int pos, int limit) {
      for (int i = pos; i < limit; i++) {
        switch (input.charAt(i)) {
          case '[':
            while (++i < limit) {
              if (input.charAt(i) == ']') break;
            }
            break;
          case ':':
            return i;
        }
      }
      return limit; 
    }

    private static String canonicalizeHost(String input, int pos, int limit) {
      
      
      String percentDecoded = percentDecode(input, pos, limit, false);

      
      if (percentDecoded.contains(":")) {
        
        InetAddress inetAddress = percentDecoded.startsWith("[") && percentDecoded.endsWith("]")
            ? decodeIpv6(percentDecoded, 1, percentDecoded.length() - 1)
            : decodeIpv6(percentDecoded, 0, percentDecoded.length());
        if (inetAddress == null) return null;
        byte[] address = inetAddress.getAddress();
        if (address.length == 16) return inet6AddressToAscii(address);
        throw new AssertionError();
      }

      return Utilaq.domainToAscii(percentDecoded);
    }


    private static @Nullableq
    InetAddress decodeIpv6(String input, int pos, int limit) {
      byte[] address = new byte[16];
      int b = 0;
      int compress = -1;
      int groupOffset = -1;

      for (int i = pos; i < limit; ) {
        if (b == address.length) return null; 

        
        if (i + 2 <= limit && input.regionMatches(i, "::", 0, 2)) {
          
          if (compress != -1) return null; 
          i += 2;
          b += 2;
          compress = b;
          if (i == limit) break;
        } else if (b != 0) {
          
          if (input.regionMatches(i, ":", 0, 1)) {
            i++;
          } else if (input.regionMatches(i, ".", 0, 1)) {
            
            if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) return null;
            b += 2; 
            break;
          } else {
            return null; 
          }
        }

        
        int value = 0;
        groupOffset = i;
        for (; i < limit; i++) {
          char c = input.charAt(i);
          int hexDigit = decodeHexDigit(c);
          if (hexDigit == -1) break;
          value = (value << 4) + hexDigit;
        }
        int groupLength = i - groupOffset;
        if (groupLength == 0 || groupLength > 4) return null; 

        
        address[b++] = (byte) ((value >>> 8) & 0xff);
        address[b++] = (byte) (value & 0xff);
      }
      if (b != address.length) {
        if (compress == -1) return null; 
        System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
        Arrays.fill(address, compress, compress + (address.length - b), (byte) 0);
      }

      try {
        return InetAddress.getByAddress(address);
      } catch (UnknownHostException e) {
        throw new AssertionError();
      }
    }


    private static boolean decodeIpv4Suffix(
        String input, int pos, int limit, byte[] address, int addressOffset) {
      int b = addressOffset;

      for (int i = pos; i < limit; ) {
        if (b == address.length) return false; 

        
        if (b != addressOffset) {
          if (input.charAt(i) != '.') return false; 
          i++;
        }

        
        int value = 0;
        int groupOffset = i;
        for (; i < limit; i++) {
          char c = input.charAt(i);
          if (c < '0' || c > '9') break;
          if (value == 0 && groupOffset != i) return false; 
          value = (value * 10) + c - '0';
          if (value > 255) return false; 
        }
        int groupLength = i - groupOffset;
        if (groupLength == 0) return false; 

        
        address[b++] = (byte) value;
      }

      if (b != addressOffset + 4) return false; 
      return true; 
    }


    private static String inet6AddressToAscii(byte[] address) {
      
      
      
      int longestRunOffset = -1;
      int longestRunLength = 0;
      for (int i = 0; i < address.length; i += 2) {
        int currentRunOffset = i;
        while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
          i += 2;
        }
        int currentRunLength = i - currentRunOffset;
        if (currentRunLength > longestRunLength && currentRunLength >= 4) {
          longestRunOffset = currentRunOffset;
          longestRunLength = currentRunLength;
        }
      }

      
      Bufferzaq result = new Bufferzaq();
      for (int i = 0; i < address.length; ) {
        if (i == longestRunOffset) {
          result.writeByte(':');
          i += longestRunLength;
          if (i == 16) result.writeByte(':');
        } else {
          if (i > 0) result.writeByte(':');
          int group = (address[i] & 0xff) << 8 | address[i + 1] & 0xff;
          result.writeHexadecimalUnsignedLong(group);
          i += 2;
        }
      }
      return result.readUtf8();
    }

    private static int parsePort(String input, int pos, int limit) {
      try {
        
        String portString = canonicalize(input, pos, limit, "", false, false, false, true);
        int i = Integer.parseInt(portString);
        if (i > 0 && i <= 65535) return i;
        return -1;
      } catch (NumberFormatException e) {
        return -1; 
      }
    }
  }

  static String percentDecode(String encoded, boolean plusIsSpace) {
    return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
  }

  private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
    int size = list.size();
    List<String> result = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      String s = list.get(i);
      result.add(s != null ? percentDecode(s, plusIsSpace) : null);
    }
    return Collections.unmodifiableList(result);
  }

  static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
    for (int i = pos; i < limit; i++) {
      char c = encoded.charAt(i);
      if (c == '%' || (c == '+' && plusIsSpace)) {
        
        Bufferzaq out = new Bufferzaq();
        out.writeUtf8(encoded, pos, i);
        percentDecode(out, encoded, i, limit, plusIsSpace);
        return out.readUtf8();
      }
    }

    
    return encoded.substring(pos, limit);
  }

  static void percentDecode(Bufferzaq out, String encoded, int pos, int limit, boolean plusIsSpace) {
    int codePoint;
    for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
      codePoint = encoded.codePointAt(i);
      if (codePoint == '%' && i + 2 < limit) {
        int d1 = decodeHexDigit(encoded.charAt(i + 1));
        int d2 = decodeHexDigit(encoded.charAt(i + 2));
        if (d1 != -1 && d2 != -1) {
          out.writeByte((d1 << 4) + d2);
          i += 2;
          continue;
        }
      } else if (codePoint == '+' && plusIsSpace) {
        out.writeByte(' ');
        continue;
      }
      out.writeUtf8CodePoint(codePoint);
    }
  }

  static boolean percentEncoded(String encoded, int pos, int limit) {
    return pos + 2 < limit
        && encoded.charAt(pos) == '%'
        && decodeHexDigit(encoded.charAt(pos + 1)) != -1
        && decodeHexDigit(encoded.charAt(pos + 2)) != -1;
  }

  static int decodeHexDigit(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    return -1;
  }


  static String canonicalize(String input, int pos, int limit, String encodeSet,
      boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
    int codePoint;
    for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
      codePoint = input.codePointAt(i);
      if (codePoint < 0x20
          || codePoint == 0x7f
          || codePoint >= 0x80 && asciiOnly
          || encodeSet.indexOf(codePoint) != -1
          || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
          || codePoint == '+' && plusIsSpace) {
        
        Bufferzaq out = new Bufferzaq();
        out.writeUtf8(input, pos, i);
        canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
            asciiOnly);
        return out.readUtf8();
      }
    }

    
    return input.substring(pos, limit);
  }

  static void canonicalize(Bufferzaq out, String input, int pos, int limit, String encodeSet,
                           boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
    Bufferzaq utf8Buffer = null; 
    int codePoint;
    for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
      codePoint = input.codePointAt(i);
      if (alreadyEncoded
          && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
        
      } else if (codePoint == '+' && plusIsSpace) {
        
        out.writeUtf8(alreadyEncoded ? "+" : "%2B");
      } else if (codePoint < 0x20
          || codePoint == 0x7f
          || codePoint >= 0x80 && asciiOnly
          || encodeSet.indexOf(codePoint) != -1
          || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
        
        if (utf8Buffer == null) {
          utf8Buffer = new Bufferzaq();
        }
        utf8Buffer.writeUtf8CodePoint(codePoint);
        while (!utf8Buffer.exhausted()) {
          int b = utf8Buffer.readByte() & 0xff;
          out.writeByte('%');
          out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
          out.writeByte(HEX_DIGITS[b & 0xf]);
        }
      } else {
        
        out.writeUtf8CodePoint(codePoint);
      }
    }
  }

  static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
      boolean plusIsSpace, boolean asciiOnly) {
    return canonicalize(
        input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly);
  }
}
