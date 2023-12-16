
package com.xxx.zzz.aall.okhttp3ll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public enum TlsVersionza {
  TLS_1_3("TLSv1.3"), 
  TLS_1_2("TLSv1.2"), 
  TLS_1_1("TLSv1.1"), 
  TLS_1_0("TLSv1"),   
  SSL_3_0("SSLv3"),   
  ;

  final String javaName;

  TlsVersionza(String javaName) {
    this.javaName = javaName;
  }

  public static TlsVersionza forJavaName(String javaName) {
    switch (javaName) {
      case "TLSv1.3":
        return TLS_1_3;
      case "TLSv1.2":
        return TLS_1_2;
      case "TLSv1.1":
        return TLS_1_1;
      case "TLSv1":
        return TLS_1_0;
      case "SSLv3":
        return SSL_3_0;
    }
    throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
  }

  static List<TlsVersionza> forJavaNames(String... tlsVersions) {
    List<TlsVersionza> result = new ArrayList<>(tlsVersions.length);
    for (String tlsVersion : tlsVersions) {
      result.add(forJavaName(tlsVersion));
    }
    return Collections.unmodifiableList(result);
  }

  public String javaName() {
    return javaName;
  }
}
