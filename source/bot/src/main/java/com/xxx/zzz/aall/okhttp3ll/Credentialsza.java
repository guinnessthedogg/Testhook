
package com.xxx.zzz.aall.okhttp3ll;

import java.nio.charset.Charset;

import com.xxx.zzz.aall.okioss.ByteStringzaq;


public final class Credentialsza {
  private Credentialsza() {
  }


  public static String basic(String userName, String password) {
    return basic(userName, password, Charset.forName("ISO-8859-1"));
  }

  public static String basic(String userName, String password, Charset charset) {
    String usernameAndPassword = userName + ":" + password;
    byte[] bytes = usernameAndPassword.getBytes(charset);
    String encoded = ByteStringzaq.of(bytes).base64();
    return "Basic " + encoded;
  }
}
