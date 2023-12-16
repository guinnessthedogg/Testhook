
package com.xxx.zzz.aall.okhttp3ll;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class MediaTypeza {
  private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
  private static final String QUOTED = "\"([^\"]*)\"";
  private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);
  private static final Pattern PARAMETER = Pattern.compile(
      ";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

  private final String mediaType;
  private final String type;
  private final String subtype;
  private final @Nullableq
  String charset;

  private MediaTypeza(String mediaType, String type, String subtype, @Nullableq String charset) {
    this.mediaType = mediaType;
    this.type = type;
    this.subtype = subtype;
    this.charset = charset;
  }


  public static @Nullableq
  MediaTypeza parse(String string) {
    Matcher typeSubtype = TYPE_SUBTYPE.matcher(string);
    if (!typeSubtype.lookingAt()) return null;
    String type = typeSubtype.group(1).toLowerCase(Locale.US);
    String subtype = typeSubtype.group(2).toLowerCase(Locale.US);

    String charset = null;
    Matcher parameter = PARAMETER.matcher(string);
    for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
      parameter.region(s, string.length());
      if (!parameter.lookingAt()) return null; 

      String name = parameter.group(1);
      if (name == null || !name.equalsIgnoreCase("charset")) continue;
      String charsetParameter;
      String token = parameter.group(2);
      if (token != null) {
        
        charsetParameter = (token.startsWith("'") && token.endsWith("'") && token.length() > 2)
            ? token.substring(1, token.length() - 1)
            : token;
      } else {
        
        charsetParameter = parameter.group(3);
      }
      if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
        return null; 
      }
      charset = charsetParameter;
    }

    return new MediaTypeza(string, type, subtype, charset);
  }


  public String type() {
    return type;
  }


  public String subtype() {
    return subtype;
  }


  public @Nullableq
  Charset charset() {
    return charset(null);
  }


  public @Nullableq
  Charset charset(@Nullableq Charset defaultValue) {
    try {
      return charset != null ? Charset.forName(charset) : defaultValue;
    } catch (IllegalArgumentException e) {
      return defaultValue; 
    }
  }


  @Override public String toString() {
    return mediaType;
  }

  @Override public boolean equals(@Nullableq Object other) {
    return other instanceof MediaTypeza && ((MediaTypeza) other).mediaType.equals(mediaType);
  }

  @Override public int hashCode() {
    return mediaType.hashCode();
  }
}
