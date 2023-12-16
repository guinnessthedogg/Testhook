
package com.xxx.zzz.aall.okhttp3ll;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.equal;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.CertificateChainCleaner;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.net.ssl.SSLPeerUnverifiedException;

import com.xxx.zzz.aall.okioss.ByteStringzaq;


public final class CertificatePinnerza {
  public static final CertificatePinnerza DEFAULT = new Builder().build();

  private final Set<Pin> pins;
  private final @Nullableq
  CertificateChainCleaner certificateChainCleaner;

  CertificatePinnerza(Set<Pin> pins, @Nullableq CertificateChainCleaner certificateChainCleaner) {
    this.pins = pins;
    this.certificateChainCleaner = certificateChainCleaner;
  }

  @Override public boolean equals(@Nullableq Object other) {
    if (other == this) return true;
    return other instanceof CertificatePinnerza
        && (equal(certificateChainCleaner, ((CertificatePinnerza) other).certificateChainCleaner)
        && pins.equals(((CertificatePinnerza) other).pins));
  }

  @Override public int hashCode() {
    int result = certificateChainCleaner != null ? certificateChainCleaner.hashCode() : 0;
    result = 31 * result + pins.hashCode();
    return result;
  }

  
  public void check(String hostname, List<Certificate> peerCertificates)
      throws SSLPeerUnverifiedException {
    List<Pin> pins = findMatchingPins(hostname);
    if (pins.isEmpty()) return;

    if (certificateChainCleaner != null) {
      peerCertificates = certificateChainCleaner.clean(peerCertificates, hostname);
    }

    for (int c = 0, certsSize = peerCertificates.size(); c < certsSize; c++) {
      X509Certificate x509Certificate = (X509Certificate) peerCertificates.get(c);

      
      ByteStringzaq sha1 = null;
      ByteStringzaq sha256 = null;

      for (int p = 0, pinsSize = pins.size(); p < pinsSize; p++) {
        Pin pin = pins.get(p);
        if (pin.hashAlgorithm.equals("sha256/")) {
          if (sha256 == null) sha256 = sha256(x509Certificate);
          if (pin.hash.equals(sha256)) return; 
        } else if (pin.hashAlgorithm.equals("sha1/")) {
          if (sha1 == null) sha1 = sha1(x509Certificate);
          if (pin.hash.equals(sha1)) return; 
        } else {
          throw new AssertionError();
        }
      }
    }

    
    StringBuilder message = new StringBuilder()
        .append("Certificate pinning failure!")
        .append("\n  Peer certificate chain:");
    for (int c = 0, certsSize = peerCertificates.size(); c < certsSize; c++) {
      X509Certificate x509Certificate = (X509Certificate) peerCertificates.get(c);
      message.append("\n    ").append(pin(x509Certificate))
          .append(": ").append(x509Certificate.getSubjectDN().getName());
    }
    message.append("\n  Pinned certificates for ").append(hostname).append(":");
    for (int p = 0, pinsSize = pins.size(); p < pinsSize; p++) {
      Pin pin = pins.get(p);
      message.append("\n    ").append(pin);
    }
    throw new SSLPeerUnverifiedException(message.toString());
  }

  
  public void check(String hostname, Certificate... peerCertificates)
      throws SSLPeerUnverifiedException {
    check(hostname, Arrays.asList(peerCertificates));
  }

  
  List<Pin> findMatchingPins(String hostname) {
    List<Pin> result = Collections.emptyList();
    for (Pin pin : pins) {
      if (pin.matches(hostname)) {
        if (result.isEmpty()) result = new ArrayList<>();
        result.add(pin);
      }
    }
    return result;
  }

  
  CertificatePinnerza withCertificateChainCleaner(CertificateChainCleaner certificateChainCleaner) {
    return equal(this.certificateChainCleaner, certificateChainCleaner)
        ? this
        : new CertificatePinnerza(pins, certificateChainCleaner);
  }

  
  public static String pin(Certificate certificate) {
    if (!(certificate instanceof X509Certificate)) {
      throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }
    return "sha256/" + sha256((X509Certificate) certificate).base64();
  }

  static ByteStringzaq sha1(X509Certificate x509Certificate) {
    return ByteStringzaq.of(x509Certificate.getPublicKey().getEncoded()).sha1();
  }

  static ByteStringzaq sha256(X509Certificate x509Certificate) {
    return ByteStringzaq.of(x509Certificate.getPublicKey().getEncoded()).sha256();
  }

  static final class Pin {
    private static final String WILDCARD = "*.";
    
    final String pattern;
    
    final String canonicalHostname;
    
    final String hashAlgorithm;
    
    final ByteStringzaq hash;

    Pin(String pattern, String pin) {
      this.pattern = pattern;
      this.canonicalHostname = pattern.startsWith(WILDCARD)
          ? HttpUrlza.parse("http://" + pattern.substring(WILDCARD.length())).host()
          : HttpUrlza.parse("http://" + pattern).host();
      if (pin.startsWith("sha1/")) {
        this.hashAlgorithm = "sha1/";
        this.hash = ByteStringzaq.decodeBase64(pin.substring("sha1/".length()));
      } else if (pin.startsWith("sha256/")) {
        this.hashAlgorithm = "sha256/";
        this.hash = ByteStringzaq.decodeBase64(pin.substring("sha256/".length()));
      } else {
        throw new IllegalArgumentException("pins must start with 'sha256/' or 'sha1/': " + pin);
      }

      if (this.hash == null) {
        throw new IllegalArgumentException("pins must be base64: " + pin);
      }
    }

    boolean matches(String hostname) {
      if (pattern.startsWith(WILDCARD)) {
        int firstDot = hostname.indexOf('.');
        return hostname.regionMatches(false, firstDot + 1, canonicalHostname, 0,
            canonicalHostname.length());
      }

      return hostname.equals(canonicalHostname);
    }

    @Override public boolean equals(Object other) {
      return other instanceof Pin
          && pattern.equals(((Pin) other).pattern)
          && hashAlgorithm.equals(((Pin) other).hashAlgorithm)
          && hash.equals(((Pin) other).hash);
    }

    @Override public int hashCode() {
      int result = 17;
      result = 31 * result + pattern.hashCode();
      result = 31 * result + hashAlgorithm.hashCode();
      result = 31 * result + hash.hashCode();
      return result;
    }

    @Override public String toString() {
      return hashAlgorithm + hash.base64();
    }
  }

  
  public static final class Builder {
    private final List<Pin> pins = new ArrayList<>();

    
    public Builder add(String pattern, String... pins) {
      if (pattern == null) throw new NullPointerException("pattern == null");

      for (String pin : pins) {
        this.pins.add(new Pin(pattern, pin));
      }

      return this;
    }

    public CertificatePinnerza build() {
      return new CertificatePinnerza(new LinkedHashSet<>(pins), null);
    }
  }
}
