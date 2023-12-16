
package com.xxx.zzz.aall.okhttp3ll.internalss.tlsss;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLPeerUnverifiedException;


public final class BasicCertificateChainCleaner extends CertificateChainCleaner {
  
  private static final int MAX_SIGNERS = 9;

  private final TrustRootIndex trustRootIndex;

  public BasicCertificateChainCleaner(TrustRootIndex trustRootIndex) {
    this.trustRootIndex = trustRootIndex;
  }

  
  @Override public List<Certificate> clean(List<Certificate> chain, String hostname)
      throws SSLPeerUnverifiedException {
    Deque<Certificate> queue = new ArrayDeque<>(chain);
    List<Certificate> result = new ArrayList<>();
    result.add(queue.removeFirst());
    boolean foundTrustedCertificate = false;

    followIssuerChain:
    for (int c = 0; c < MAX_SIGNERS; c++) {
      X509Certificate toVerify = (X509Certificate) result.get(result.size() - 1);




      X509Certificate trustedCert = trustRootIndex.findByIssuerAndSignature(toVerify);
      if (trustedCert != null) {
        if (result.size() > 1 || !toVerify.equals(trustedCert)) {
          result.add(trustedCert);
        }
        if (verifySignature(trustedCert, trustedCert)) {
          return result;
        }
        foundTrustedCertificate = true;
        continue;
      }

      
      
      for (Iterator<Certificate> i = queue.iterator(); i.hasNext(); ) {
        X509Certificate signingCert = (X509Certificate) i.next();
        if (verifySignature(toVerify, signingCert)) {
          i.remove();
          result.add(signingCert);
          continue followIssuerChain;
        }
      }

      
      if (foundTrustedCertificate) {
        return result;
      }

      
      throw new SSLPeerUnverifiedException(
          "Failed to find a trusted cert that signed " + toVerify);
    }

    throw new SSLPeerUnverifiedException("Certificate chain too long: " + result);
  }

  
  private boolean verifySignature(X509Certificate toVerify, X509Certificate signingCert) {
    if (!toVerify.getIssuerDN().equals(signingCert.getSubjectDN())) return false;
    try {
      toVerify.verify(signingCert.getPublicKey());
      return true;
    } catch (GeneralSecurityException verifyFailed) {
      return false;
    }
  }

  @Override public int hashCode() {
    return trustRootIndex.hashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) return true;
    return other instanceof BasicCertificateChainCleaner
        && ((BasicCertificateChainCleaner) other).trustRootIndex.equals(trustRootIndex);
  }
}
