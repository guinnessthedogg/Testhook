
package com.xxx.zzz.aall.okhttp3ll.internalss.tlsss;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public abstract class TrustRootIndex {

  public abstract X509Certificate findByIssuerAndSignature(X509Certificate cert);

  public static TrustRootIndex get(X509TrustManager trustManager) {
    try {


      Method method = trustManager.getClass().getDeclaredMethod(
          "findTrustAnchorByIssuerAndSignature", X509Certificate.class);
      method.setAccessible(true);
      return new AndroidTrustRootIndex(trustManager, method);
    } catch (NoSuchMethodException e) {
      return get(trustManager.getAcceptedIssuers());
    }
  }

  public static TrustRootIndex get(X509Certificate... caCerts) {
    return new BasicTrustRootIndex(caCerts);
  }


  static final class AndroidTrustRootIndex extends TrustRootIndex {
    private final X509TrustManager trustManager;
    private final Method findByIssuerAndSignatureMethod;

    AndroidTrustRootIndex(X509TrustManager trustManager, Method findByIssuerAndSignatureMethod) {
      this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
      this.trustManager = trustManager;
    }

    @Override public X509Certificate findByIssuerAndSignature(X509Certificate cert) {
      try {
        TrustAnchor trustAnchor = (TrustAnchor) findByIssuerAndSignatureMethod.invoke(
            trustManager, cert);
        return trustAnchor != null
            ? trustAnchor.getTrustedCert()
            : null;
      } catch (IllegalAccessException e) {
        throw new AssertionError();
      } catch (InvocationTargetException e) {
        return null;
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof AndroidTrustRootIndex)) {
        return false;
      }
      AndroidTrustRootIndex that = (AndroidTrustRootIndex) obj;
      return trustManager.equals(that.trustManager)
              && findByIssuerAndSignatureMethod.equals(that.findByIssuerAndSignatureMethod);
    }

    @Override
    public int hashCode() {
      return trustManager.hashCode() + 31 * findByIssuerAndSignatureMethod.hashCode();
    }
  }


  static final class BasicTrustRootIndex extends TrustRootIndex {
    private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts;

    BasicTrustRootIndex(X509Certificate... caCerts) {
      subjectToCaCerts = new LinkedHashMap<>();
      for (X509Certificate caCert : caCerts) {
        X500Principal subject = caCert.getSubjectX500Principal();
        Set<X509Certificate> subjectCaCerts = subjectToCaCerts.get(subject);
        if (subjectCaCerts == null) {
          subjectCaCerts = new LinkedHashSet<>(1);
          subjectToCaCerts.put(subject, subjectCaCerts);
        }
        subjectCaCerts.add(caCert);
      }
    }

    @Override public X509Certificate findByIssuerAndSignature(X509Certificate cert) {
      X500Principal issuer = cert.getIssuerX500Principal();
      Set<X509Certificate> subjectCaCerts = subjectToCaCerts.get(issuer);
      if (subjectCaCerts == null) return null;

      for (X509Certificate caCert : subjectCaCerts) {
        PublicKey publicKey = caCert.getPublicKey();
        try {
          cert.verify(publicKey);
          return caCert;
        } catch (Exception ignored) {
        }
      }

      return null;
    }

    @Override public boolean equals(Object other) {
      if (other == this) return true;
      return other instanceof BasicTrustRootIndex
          && ((BasicTrustRootIndex) other).subjectToCaCerts.equals(subjectToCaCerts);
    }

    @Override public int hashCode() {
      return subjectToCaCerts.hashCode();
    }
  }
}
