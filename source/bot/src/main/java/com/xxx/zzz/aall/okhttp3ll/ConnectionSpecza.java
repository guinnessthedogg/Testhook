
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;

import java.util.Arrays;
import java.util.List;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.net.ssl.SSLSocket;


public final class ConnectionSpecza {

  
  
  
  private static final CipherSuiteza[] APPROVED_CIPHER_SUITES = new CipherSuiteza[] {
      CipherSuiteza.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
      CipherSuiteza.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
      CipherSuiteza.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
      CipherSuiteza.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
      CipherSuiteza.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
      CipherSuiteza.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,

      
      
      
      CipherSuiteza.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
      CipherSuiteza.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
      CipherSuiteza.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
      CipherSuiteza.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
      CipherSuiteza.TLS_RSA_WITH_AES_128_GCM_SHA256,
      CipherSuiteza.TLS_RSA_WITH_AES_256_GCM_SHA384,
      CipherSuiteza.TLS_RSA_WITH_AES_128_CBC_SHA,
      CipherSuiteza.TLS_RSA_WITH_AES_256_CBC_SHA,
      CipherSuiteza.TLS_RSA_WITH_3DES_EDE_CBC_SHA,
  };


  public static final ConnectionSpecza MODERN_TLS = new Builder(true)
      .cipherSuites(APPROVED_CIPHER_SUITES)
      .tlsVersions(TlsVersionza.TLS_1_3, TlsVersionza.TLS_1_2, TlsVersionza.TLS_1_1, TlsVersionza.TLS_1_0)
      .supportsTlsExtensions(true)
      .build();


  public static final ConnectionSpecza COMPATIBLE_TLS = new Builder(MODERN_TLS)
      .tlsVersions(TlsVersionza.TLS_1_0)
      .supportsTlsExtensions(true)
      .build();


  public static final ConnectionSpecza CLEARTEXT = new Builder(false).build();

  final boolean tls;
  final boolean supportsTlsExtensions;
  final @Nullableq
  String[] cipherSuites;
  final @Nullableq
  String[] tlsVersions;

  ConnectionSpecza(Builder builder) {
    this.tls = builder.tls;
    this.cipherSuites = builder.cipherSuites;
    this.tlsVersions = builder.tlsVersions;
    this.supportsTlsExtensions = builder.supportsTlsExtensions;
  }

  public boolean isTls() {
    return tls;
  }


  public @Nullableq
  List<CipherSuiteza> cipherSuites() {
    return cipherSuites != null ? CipherSuiteza.forJavaNames(cipherSuites) : null;
  }


  public @Nullableq
  List<TlsVersionza> tlsVersions() {
    return tlsVersions != null ? TlsVersionza.forJavaNames(tlsVersions) : null;
  }

  public boolean supportsTlsExtensions() {
    return supportsTlsExtensions;
  }


  void apply(SSLSocket sslSocket, boolean isFallback) {
    ConnectionSpecza specToApply = supportedSpec(sslSocket, isFallback);

    if (specToApply.tlsVersions != null) {
      sslSocket.setEnabledProtocols(specToApply.tlsVersions);
    }
    if (specToApply.cipherSuites != null) {
      sslSocket.setEnabledCipherSuites(specToApply.cipherSuites);
    }
  }


  private ConnectionSpecza supportedSpec(SSLSocket sslSocket, boolean isFallback) {
    String[] cipherSuitesIntersection = cipherSuites != null
        ? Utilaq.intersect(CipherSuiteza.ORDER_BY_NAME, sslSocket.getEnabledCipherSuites(), cipherSuites)
        : sslSocket.getEnabledCipherSuites();
    String[] tlsVersionsIntersection = tlsVersions != null
        ? Utilaq.intersect(Utilaq.NATURAL_ORDER, sslSocket.getEnabledProtocols(), tlsVersions)
        : sslSocket.getEnabledProtocols();

    
    
    String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
    int indexOfFallbackScsv = Utilaq.indexOf(
        CipherSuiteza.ORDER_BY_NAME, supportedCipherSuites, "TLS_FALLBACK_SCSV");
    if (isFallback && indexOfFallbackScsv != -1) {
      cipherSuitesIntersection = Utilaq.concat(
          cipherSuitesIntersection, supportedCipherSuites[indexOfFallbackScsv]);
    }

    return new Builder(this)
        .cipherSuites(cipherSuitesIntersection)
        .tlsVersions(tlsVersionsIntersection)
        .build();
  }


  public boolean isCompatible(SSLSocket socket) {
    if (!tls) {
      return false;
    }

    if (tlsVersions != null && !Utilaq.nonEmptyIntersection(
        Utilaq.NATURAL_ORDER, tlsVersions, socket.getEnabledProtocols())) {
      return false;
    }

    if (cipherSuites != null && !Utilaq.nonEmptyIntersection(
        CipherSuiteza.ORDER_BY_NAME, cipherSuites, socket.getEnabledCipherSuites())) {
      return false;
    }

    return true;
  }

  @Override public boolean equals(@Nullableq Object other) {
    if (!(other instanceof ConnectionSpecza)) return false;
    if (other == this) return true;

    ConnectionSpecza that = (ConnectionSpecza) other;
    if (this.tls != that.tls) return false;

    if (tls) {
      if (!Arrays.equals(this.cipherSuites, that.cipherSuites)) return false;
      if (!Arrays.equals(this.tlsVersions, that.tlsVersions)) return false;
      if (this.supportsTlsExtensions != that.supportsTlsExtensions) return false;
    }

    return true;
  }

  @Override public int hashCode() {
    int result = 17;
    if (tls) {
      result = 31 * result + Arrays.hashCode(cipherSuites);
      result = 31 * result + Arrays.hashCode(tlsVersions);
      result = 31 * result + (supportsTlsExtensions ? 0 : 1);
    }
    return result;
  }

  @Override public String toString() {
    if (!tls) {
      return "ConnectionSpec()";
    }

    String cipherSuitesString = cipherSuites != null ? cipherSuites().toString() : "[all enabled]";
    String tlsVersionsString = tlsVersions != null ? tlsVersions().toString() : "[all enabled]";
    return "ConnectionSpec("
        + "cipherSuites=" + cipherSuitesString
        + ", tlsVersions=" + tlsVersionsString
        + ", supportsTlsExtensions=" + supportsTlsExtensions
        + ")";
  }

  public static final class Builder {
    boolean tls;
    @Nullableq
    String[] cipherSuites;
    @Nullableq
    String[] tlsVersions;
    boolean supportsTlsExtensions;

    Builder(boolean tls) {
      this.tls = tls;
    }

    public Builder(ConnectionSpecza connectionSpec) {
      this.tls = connectionSpec.tls;
      this.cipherSuites = connectionSpec.cipherSuites;
      this.tlsVersions = connectionSpec.tlsVersions;
      this.supportsTlsExtensions = connectionSpec.supportsTlsExtensions;
    }

    public Builder allEnabledCipherSuites() {
      if (!tls) throw new IllegalStateException("no cipher suites for cleartext connections");
      this.cipherSuites = null;
      return this;
    }

    public Builder cipherSuites(CipherSuiteza... cipherSuites) {
      if (!tls) throw new IllegalStateException("no cipher suites for cleartext connections");

      String[] strings = new String[cipherSuites.length];
      for (int i = 0; i < cipherSuites.length; i++) {
        strings[i] = cipherSuites[i].javaName;
      }
      return cipherSuites(strings);
    }

    public Builder cipherSuites(String... cipherSuites) {
      if (!tls) throw new IllegalStateException("no cipher suites for cleartext connections");

      if (cipherSuites.length == 0) {
        throw new IllegalArgumentException("At least one cipher suite is required");
      }

      this.cipherSuites = cipherSuites.clone(); 
      return this;
    }

    public Builder allEnabledTlsVersions() {
      if (!tls) throw new IllegalStateException("no TLS versions for cleartext connections");
      this.tlsVersions = null;
      return this;
    }

    public Builder tlsVersions(TlsVersionza... tlsVersions) {
      if (!tls) throw new IllegalStateException("no TLS versions for cleartext connections");

      String[] strings = new String[tlsVersions.length];
      for (int i = 0; i < tlsVersions.length; i++) {
        strings[i] = tlsVersions[i].javaName;
      }

      return tlsVersions(strings);
    }

    public Builder tlsVersions(String... tlsVersions) {
      if (!tls) throw new IllegalStateException("no TLS versions for cleartext connections");

      if (tlsVersions.length == 0) {
        throw new IllegalArgumentException("At least one TLS version is required");
      }

      this.tlsVersions = tlsVersions.clone(); 
      return this;
    }

    public Builder supportsTlsExtensions(boolean supportsTlsExtensions) {
      if (!tls) throw new IllegalStateException("no TLS extensions for cleartext connections");
      this.supportsTlsExtensions = supportsTlsExtensions;
      return this;
    }

    public ConnectionSpecza build() {
      return new ConnectionSpecza(this);
    }
  }
}
