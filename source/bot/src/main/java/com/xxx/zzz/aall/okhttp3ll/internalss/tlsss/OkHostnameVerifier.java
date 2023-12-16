
package com.xxx.zzz.aall.okhttp3ll.internalss.tlsss;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;

import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;


public final class OkHostnameVerifier implements HostnameVerifier {
  public static final OkHostnameVerifier INSTANCE = new OkHostnameVerifier();

  private static final int ALT_DNS_NAME = 2;
  private static final int ALT_IPA_NAME = 7;

  private OkHostnameVerifier() {
  }

  @Override
  public boolean verify(String host, SSLSession session) {
    try {
      Certificate[] certificates = session.getPeerCertificates();
      return verify(host, (X509Certificate) certificates[0]);
    } catch (SSLException e) {
      return false;
    }
  }

  public boolean verify(String host, X509Certificate certificate) {
    return Utilaq.verifyAsIpAddress(host)
        ? verifyIpAddress(host, certificate)
        : verifyHostname(host, certificate);
  }

  
  private boolean verifyIpAddress(String ipAddress, X509Certificate certificate) {
    List<String> altNames = getSubjectAltNames(certificate, ALT_IPA_NAME);
    for (int i = 0, size = altNames.size(); i < size; i++) {
      if (ipAddress.equalsIgnoreCase(altNames.get(i))) {
        return true;
      }
    }
    return false;
  }

  
  private boolean verifyHostname(String hostname, X509Certificate certificate) {
    hostname = hostname.toLowerCase(Locale.US);
    boolean hasDns = false;
    List<String> altNames = getSubjectAltNames(certificate, ALT_DNS_NAME);
    for (int i = 0, size = altNames.size(); i < size; i++) {
      hasDns = true;
      if (verifyHostname(hostname, altNames.get(i))) {
        return true;
      }
    }

    if (!hasDns) {
      X500Principal principal = certificate.getSubjectX500Principal();

      String cn = new DistinguishedNameParsercxz(principal).findMostSpecific("cn");
      if (cn != null) {
        return verifyHostname(hostname, cn);
      }
    }

    return false;
  }

  public static List<String> allSubjectAltNames(X509Certificate certificate) {
    List<String> altIpaNames = getSubjectAltNames(certificate, ALT_IPA_NAME);
    List<String> altDnsNames = getSubjectAltNames(certificate, ALT_DNS_NAME);
    List<String> result = new ArrayList<>(altIpaNames.size() + altDnsNames.size());
    result.addAll(altIpaNames);
    result.addAll(altDnsNames);
    return result;
  }

  private static List<String> getSubjectAltNames(X509Certificate certificate, int type) {
    List<String> result = new ArrayList<>();
    try {
      Collection<?> subjectAltNames = certificate.getSubjectAlternativeNames();
      if (subjectAltNames == null) {
        return Collections.emptyList();
      }
      for (Object subjectAltName : subjectAltNames) {
        List<?> entry = (List<?>) subjectAltName;
        if (entry == null || entry.size() < 2) {
          continue;
        }
        Integer altNameType = (Integer) entry.get(0);
        if (altNameType == null) {
          continue;
        }
        if (altNameType == type) {
          String altName = (String) entry.get(1);
          if (altName != null) {
            result.add(altName);
          }
        }
      }
      return result;
    } catch (CertificateParsingException e) {
      return Collections.emptyList();
    }
  }

  
  public boolean verifyHostname(String hostname, String pattern) {
    
    
    if ((hostname == null) || (hostname.length() == 0) || (hostname.startsWith("."))
        || (hostname.endsWith(".."))) {
      
      return false;
    }
    if ((pattern == null) || (pattern.length() == 0) || (pattern.startsWith("."))
        || (pattern.endsWith(".."))) {
      
      return false;
    }

    
    
    
    
    
    
    
    
    
    if (!hostname.endsWith(".")) {
      hostname += '.';
    }
    if (!pattern.endsWith(".")) {
      pattern += '.';
    }
    

    pattern = pattern.toLowerCase(Locale.US);
    

    if (!pattern.contains("*")) {
      
      return hostname.equals(pattern);
    }
    

    
    
    
    
    
    
    
    
    

    if ((!pattern.startsWith("*.")) || (pattern.indexOf('*', 1) != -1)) {
      
      
      return false;
    }

    
    
    
    if (hostname.length() < pattern.length()) {
      
      return false;
    }

    if ("*.".equals(pattern)) {
      
      return false;
    }

    
    String suffix = pattern.substring(1);
    if (!hostname.endsWith(suffix)) {
      
      return false;
    }

    
    int suffixStartIndexInHostname = hostname.length() - suffix.length();
    if ((suffixStartIndexInHostname > 0)
        && (hostname.lastIndexOf('.', suffixStartIndexInHostname - 1) != -1)) {
      
      return false;
    }

    
    return true;
  }
}
