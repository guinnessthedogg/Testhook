
package com.xxx.zzz.aall.okhttp3ll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public final class CipherSuiteza {
  
  static final Comparator<String> ORDER_BY_NAME = new Comparator<String>() {
    @Override public int compare(String a, String b) {
      for (int i = 4, limit = Math.min(a.length(), b.length()); i < limit; i++) {
        char charA = a.charAt(i);
        char charB = b.charAt(i);
        if (charA != charB) return charA < charB ? -1 : 1;
      }
      int lengthA = a.length();
      int lengthB = b.length();
      if (lengthA != lengthB) return lengthA < lengthB ? -1 : 1;
      return 0;
    }
  };

  
  private static final Map<String, CipherSuiteza> INSTANCES = new TreeMap<>(ORDER_BY_NAME);

  

  
  public static final CipherSuiteza TLS_RSA_WITH_NULL_MD5 = of("SSL_RSA_WITH_NULL_MD5", 0x0001);
  public static final CipherSuiteza TLS_RSA_WITH_NULL_SHA = of("SSL_RSA_WITH_NULL_SHA", 0x0002);
  public static final CipherSuiteza TLS_RSA_EXPORT_WITH_RC4_40_MD5 = of("SSL_RSA_EXPORT_WITH_RC4_40_MD5", 0x0003);
  public static final CipherSuiteza TLS_RSA_WITH_RC4_128_MD5 = of("SSL_RSA_WITH_RC4_128_MD5", 0x0004);
  public static final CipherSuiteza TLS_RSA_WITH_RC4_128_SHA = of("SSL_RSA_WITH_RC4_128_SHA", 0x0005);
  
  
  public static final CipherSuiteza TLS_RSA_EXPORT_WITH_DES40_CBC_SHA = of("SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", 0x0008);
  public static final CipherSuiteza TLS_RSA_WITH_DES_CBC_SHA = of("SSL_RSA_WITH_DES_CBC_SHA", 0x0009);
  public static final CipherSuiteza TLS_RSA_WITH_3DES_EDE_CBC_SHA = of("SSL_RSA_WITH_3DES_EDE_CBC_SHA", 0x000a);
  
  
  
  
  
  
  public static final CipherSuiteza TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA = of("SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", 0x0011);
  public static final CipherSuiteza TLS_DHE_DSS_WITH_DES_CBC_SHA = of("SSL_DHE_DSS_WITH_DES_CBC_SHA", 0x0012);
  public static final CipherSuiteza TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA = of("SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", 0x0013);
  public static final CipherSuiteza TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA = of("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 0x0014);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_DES_CBC_SHA = of("SSL_DHE_RSA_WITH_DES_CBC_SHA", 0x0015);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA = of("SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", 0x0016);
  public static final CipherSuiteza TLS_DH_anon_EXPORT_WITH_RC4_40_MD5 = of("SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 0x0017);
  public static final CipherSuiteza TLS_DH_anon_WITH_RC4_128_MD5 = of("SSL_DH_anon_WITH_RC4_128_MD5", 0x0018);
  public static final CipherSuiteza TLS_DH_anon_EXPORT_WITH_DES40_CBC_SHA = of("SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", 0x0019);
  public static final CipherSuiteza TLS_DH_anon_WITH_DES_CBC_SHA = of("SSL_DH_anon_WITH_DES_CBC_SHA", 0x001a);
  public static final CipherSuiteza TLS_DH_anon_WITH_3DES_EDE_CBC_SHA = of("SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", 0x001b);
  public static final CipherSuiteza TLS_KRB5_WITH_DES_CBC_SHA = of("TLS_KRB5_WITH_DES_CBC_SHA", 0x001e);
  public static final CipherSuiteza TLS_KRB5_WITH_3DES_EDE_CBC_SHA = of("TLS_KRB5_WITH_3DES_EDE_CBC_SHA", 0x001f);
  public static final CipherSuiteza TLS_KRB5_WITH_RC4_128_SHA = of("TLS_KRB5_WITH_RC4_128_SHA", 0x0020);
  
  public static final CipherSuiteza TLS_KRB5_WITH_DES_CBC_MD5 = of("TLS_KRB5_WITH_DES_CBC_MD5", 0x0022);
  public static final CipherSuiteza TLS_KRB5_WITH_3DES_EDE_CBC_MD5 = of("TLS_KRB5_WITH_3DES_EDE_CBC_MD5", 0x0023);
  public static final CipherSuiteza TLS_KRB5_WITH_RC4_128_MD5 = of("TLS_KRB5_WITH_RC4_128_MD5", 0x0024);
  
  public static final CipherSuiteza TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA = of("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", 0x0026);
  
  public static final CipherSuiteza TLS_KRB5_EXPORT_WITH_RC4_40_SHA = of("TLS_KRB5_EXPORT_WITH_RC4_40_SHA", 0x0028);
  public static final CipherSuiteza TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5 = of("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", 0x0029);
  
  public static final CipherSuiteza TLS_KRB5_EXPORT_WITH_RC4_40_MD5 = of("TLS_KRB5_EXPORT_WITH_RC4_40_MD5", 0x002b);
  
  
  
  public static final CipherSuiteza TLS_RSA_WITH_AES_128_CBC_SHA = of("TLS_RSA_WITH_AES_128_CBC_SHA", 0x002f);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_AES_128_CBC_SHA = of("TLS_DHE_DSS_WITH_AES_128_CBC_SHA", 0x0032);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_AES_128_CBC_SHA = of("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", 0x0033);
  public static final CipherSuiteza TLS_DH_anon_WITH_AES_128_CBC_SHA = of("TLS_DH_anon_WITH_AES_128_CBC_SHA", 0x0034);
  public static final CipherSuiteza TLS_RSA_WITH_AES_256_CBC_SHA = of("TLS_RSA_WITH_AES_256_CBC_SHA", 0x0035);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_AES_256_CBC_SHA = of("TLS_DHE_DSS_WITH_AES_256_CBC_SHA", 0x0038);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_AES_256_CBC_SHA = of("TLS_DHE_RSA_WITH_AES_256_CBC_SHA", 0x0039);
  public static final CipherSuiteza TLS_DH_anon_WITH_AES_256_CBC_SHA = of("TLS_DH_anon_WITH_AES_256_CBC_SHA", 0x003a);
  public static final CipherSuiteza TLS_RSA_WITH_NULL_SHA256 = of("TLS_RSA_WITH_NULL_SHA256", 0x003b);
  public static final CipherSuiteza TLS_RSA_WITH_AES_128_CBC_SHA256 = of("TLS_RSA_WITH_AES_128_CBC_SHA256", 0x003c);
  public static final CipherSuiteza TLS_RSA_WITH_AES_256_CBC_SHA256 = of("TLS_RSA_WITH_AES_256_CBC_SHA256", 0x003d);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_AES_128_CBC_SHA256 = of("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", 0x0040);
  public static final CipherSuiteza TLS_RSA_WITH_CAMELLIA_128_CBC_SHA = of("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA", 0x0041);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA = of("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA", 0x0044);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA = of("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA", 0x0045);
  
  public static final CipherSuiteza TLS_DHE_RSA_WITH_AES_128_CBC_SHA256 = of("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", 0x0067);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 = of("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", 0x006a);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 = of("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", 0x006b);
  public static final CipherSuiteza TLS_DH_anon_WITH_AES_128_CBC_SHA256 = of("TLS_DH_anon_WITH_AES_128_CBC_SHA256", 0x006c);
  public static final CipherSuiteza TLS_DH_anon_WITH_AES_256_CBC_SHA256 = of("TLS_DH_anon_WITH_AES_256_CBC_SHA256", 0x006d);
  public static final CipherSuiteza TLS_RSA_WITH_CAMELLIA_256_CBC_SHA = of("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA", 0x0084);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA = of("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA", 0x0087);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA = of("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA", 0x0088);
  
  public static final CipherSuiteza TLS_PSK_WITH_RC4_128_SHA = of("TLS_PSK_WITH_RC4_128_SHA", 0x008a);
  public static final CipherSuiteza TLS_PSK_WITH_3DES_EDE_CBC_SHA = of("TLS_PSK_WITH_3DES_EDE_CBC_SHA", 0x008b);
  public static final CipherSuiteza TLS_PSK_WITH_AES_128_CBC_SHA = of("TLS_PSK_WITH_AES_128_CBC_SHA", 0x008c);
  public static final CipherSuiteza TLS_PSK_WITH_AES_256_CBC_SHA = of("TLS_PSK_WITH_AES_256_CBC_SHA", 0x008d);
  
  
  
  
  
  
  
  
  public static final CipherSuiteza TLS_RSA_WITH_SEED_CBC_SHA = of("TLS_RSA_WITH_SEED_CBC_SHA", 0x0096);
  
  
  
  
  
  public static final CipherSuiteza TLS_RSA_WITH_AES_128_GCM_SHA256 = of("TLS_RSA_WITH_AES_128_GCM_SHA256", 0x009c);
  public static final CipherSuiteza TLS_RSA_WITH_AES_256_GCM_SHA384 = of("TLS_RSA_WITH_AES_256_GCM_SHA384", 0x009d);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_AES_128_GCM_SHA256 = of("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", 0x009e);
  public static final CipherSuiteza TLS_DHE_RSA_WITH_AES_256_GCM_SHA384 = of("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", 0x009f);
  
  
  public static final CipherSuiteza TLS_DHE_DSS_WITH_AES_128_GCM_SHA256 = of("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", 0x00a2);
  public static final CipherSuiteza TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 = of("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", 0x00a3);
  
  
  public static final CipherSuiteza TLS_DH_anon_WITH_AES_128_GCM_SHA256 = of("TLS_DH_anon_WITH_AES_128_GCM_SHA256", 0x00a6);
  public static final CipherSuiteza TLS_DH_anon_WITH_AES_256_GCM_SHA384 = of("TLS_DH_anon_WITH_AES_256_GCM_SHA384", 0x00a7);
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public static final CipherSuiteza TLS_EMPTY_RENEGOTIATION_INFO_SCSV = of("TLS_EMPTY_RENEGOTIATION_INFO_SCSV", 0x00ff);
  public static final CipherSuiteza TLS_FALLBACK_SCSV = of("TLS_FALLBACK_SCSV", 0x5600);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_NULL_SHA = of("TLS_ECDH_ECDSA_WITH_NULL_SHA", 0xc001);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_RC4_128_SHA = of("TLS_ECDH_ECDSA_WITH_RC4_128_SHA", 0xc002);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA = of("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", 0xc003);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA = of("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", 0xc004);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA = of("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 0xc005);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_NULL_SHA = of("TLS_ECDHE_ECDSA_WITH_NULL_SHA", 0xc006);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_RC4_128_SHA = of("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", 0xc007);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA = of("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", 0xc008);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA = of("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", 0xc009);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA = of("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", 0xc00a);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_NULL_SHA = of("TLS_ECDH_RSA_WITH_NULL_SHA", 0xc00b);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_RC4_128_SHA = of("TLS_ECDH_RSA_WITH_RC4_128_SHA", 0xc00c);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA = of("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", 0xc00d);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_AES_128_CBC_SHA = of("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", 0xc00e);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_AES_256_CBC_SHA = of("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", 0xc00f);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_NULL_SHA = of("TLS_ECDHE_RSA_WITH_NULL_SHA", 0xc010);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_RC4_128_SHA = of("TLS_ECDHE_RSA_WITH_RC4_128_SHA", 0xc011);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA = of("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", 0xc012);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA = of("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", 0xc013);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA = of("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", 0xc014);
  public static final CipherSuiteza TLS_ECDH_anon_WITH_NULL_SHA = of("TLS_ECDH_anon_WITH_NULL_SHA", 0xc015);
  public static final CipherSuiteza TLS_ECDH_anon_WITH_RC4_128_SHA = of("TLS_ECDH_anon_WITH_RC4_128_SHA", 0xc016);
  public static final CipherSuiteza TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA = of("TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", 0xc017);
  public static final CipherSuiteza TLS_ECDH_anon_WITH_AES_128_CBC_SHA = of("TLS_ECDH_anon_WITH_AES_128_CBC_SHA", 0xc018);
  public static final CipherSuiteza TLS_ECDH_anon_WITH_AES_256_CBC_SHA = of("TLS_ECDH_anon_WITH_AES_256_CBC_SHA", 0xc019);
  
  
  
  
  
  
  
  
  
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256 = of("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 0xc023);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384 = of("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", 0xc024);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256 = of("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 0xc025);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384 = of("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", 0xc026);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256 = of("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", 0xc027);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384 = of("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 0xc028);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256 = of("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", 0xc029);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384 = of("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", 0xc02a);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256 = of("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 0xc02b);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384 = of("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", 0xc02c);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256 = of("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", 0xc02d);
  public static final CipherSuiteza TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384 = of("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", 0xc02e);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 = of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", 0xc02f);
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384 = of("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", 0xc030);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256 = of("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", 0xc031);
  public static final CipherSuiteza TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384 = of("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", 0xc032);
  
  
  public static final CipherSuiteza TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA = of("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA", 0xc035);
  public static final CipherSuiteza TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA = of("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA", 0xc036);
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public static final CipherSuiteza TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256 = of("TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", 0xcca8);
  public static final CipherSuiteza TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256 = of("TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256", 0xcca9);
  
  
  
  
  

  final String javaName;

  
  public static synchronized CipherSuiteza forJavaName(String javaName) {
    CipherSuiteza result = INSTANCES.get(javaName);
    if (result == null) {
      result = new CipherSuiteza(javaName);
      INSTANCES.put(javaName, result);
    }
    return result;
  }

  static List<CipherSuiteza> forJavaNames(String... cipherSuites) {
    List<CipherSuiteza> result = new ArrayList<>(cipherSuites.length);
    for (String cipherSuite : cipherSuites) {
      result.add(forJavaName(cipherSuite));
    }
    return Collections.unmodifiableList(result);
  }

  private CipherSuiteza(String javaName) {
    if (javaName == null) {
      throw new NullPointerException();
    }
    this.javaName = javaName;
  }

  
  private static CipherSuiteza of(String javaName, int value) {
    return forJavaName(javaName);
  }

  
  public String javaName() {
    return javaName;
  }

  @Override public String toString() {
    return javaName;
  }
}
