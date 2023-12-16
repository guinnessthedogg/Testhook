
package com.xxx.zzz.aall.okhttp3ll.internalss.tlsss;

import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.X509TrustManager;


public abstract class CertificateChainCleaner {
  public abstract List<Certificate> clean(List<Certificate> chain, String hostname)
      throws SSLPeerUnverifiedException;

  public static CertificateChainCleaner get(X509TrustManager trustManager) {
    return Platformq.get().buildCertificateChainCleaner(trustManager);
  }

  public static CertificateChainCleaner get(X509Certificate... caCerts) {
    return new BasicCertificateChainCleaner(TrustRootIndex.get(caCerts));
  }
}
