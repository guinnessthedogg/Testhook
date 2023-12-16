
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;


public final class Handshakeza {
  private final TlsVersionza tlsVersion;
  private final CipherSuiteza cipherSuite;
  private final List<Certificate> peerCertificates;
  private final List<Certificate> localCertificates;

  private Handshakeza(TlsVersionza tlsVersion, CipherSuiteza cipherSuite,
                      List<Certificate> peerCertificates, List<Certificate> localCertificates) {
    this.tlsVersion = tlsVersion;
    this.cipherSuite = cipherSuite;
    this.peerCertificates = peerCertificates;
    this.localCertificates = localCertificates;
  }

  public static Handshakeza get(SSLSession session) {
    String cipherSuiteString = session.getCipherSuite();
    if (cipherSuiteString == null) throw new IllegalStateException("cipherSuite == null");
    CipherSuiteza cipherSuite = CipherSuiteza.forJavaName(cipherSuiteString);

    String tlsVersionString = session.getProtocol();
    if (tlsVersionString == null) throw new IllegalStateException("tlsVersion == null");
    TlsVersionza tlsVersion = TlsVersionza.forJavaName(tlsVersionString);

    Certificate[] peerCertificates;
    try {
      peerCertificates = session.getPeerCertificates();
    } catch (SSLPeerUnverifiedException ignored) {
      peerCertificates = null;
    }
    List<Certificate> peerCertificatesList = peerCertificates != null
        ? Utilaq.immutableList(peerCertificates)
        : Collections.<Certificate>emptyList();

    Certificate[] localCertificates = session.getLocalCertificates();
    List<Certificate> localCertificatesList = localCertificates != null
        ? Utilaq.immutableList(localCertificates)
        : Collections.<Certificate>emptyList();

    return new Handshakeza(tlsVersion, cipherSuite, peerCertificatesList, localCertificatesList);
  }

  public static Handshakeza get(TlsVersionza tlsVersion, CipherSuiteza cipherSuite,
                                List<Certificate> peerCertificates, List<Certificate> localCertificates) {
    if (tlsVersion == null) throw new NullPointerException("tlsVersion == null");
    if (cipherSuite == null) throw new NullPointerException("cipherSuite == null");
    return new Handshakeza(tlsVersion, cipherSuite, Utilaq.immutableList(peerCertificates),
        Utilaq.immutableList(localCertificates));
  }


  public TlsVersionza tlsVersion() {
    return tlsVersion;
  }


  public CipherSuiteza cipherSuite() {
    return cipherSuite;
  }


  public List<Certificate> peerCertificates() {
    return peerCertificates;
  }


  public @Nullableq
  Principal peerPrincipal() {
    return !peerCertificates.isEmpty()
        ? ((X509Certificate) peerCertificates.get(0)).getSubjectX500Principal()
        : null;
  }


  public List<Certificate> localCertificates() {
    return localCertificates;
  }


  public @Nullableq
  Principal localPrincipal() {
    return !localCertificates.isEmpty()
        ? ((X509Certificate) localCertificates.get(0)).getSubjectX500Principal()
        : null;
  }

  @Override public boolean equals(@Nullableq Object other) {
    if (!(other instanceof Handshakeza)) return false;
    Handshakeza that = (Handshakeza) other;
    return tlsVersion.equals(that.tlsVersion)
        && cipherSuite.equals(that.cipherSuite)
        && peerCertificates.equals(that.peerCertificates)
        && localCertificates.equals(that.localCertificates);
  }

  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + tlsVersion.hashCode();
    result = 31 * result + cipherSuite.hashCode();
    result = 31 * result + peerCertificates.hashCode();
    result = 31 * result + localCertificates.hashCode();
    return result;
  }
}
