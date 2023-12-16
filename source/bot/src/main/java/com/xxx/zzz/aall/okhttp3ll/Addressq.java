
package com.xxx.zzz.aall.okhttp3ll;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.equal;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;


public final class Addressq {
  final HttpUrlza url;
  final Dnsza dns;
  final SocketFactory socketFactory;
  final Authenticatorq proxyAuthenticator;
  final List<Protocolza> protocols;
  final List<ConnectionSpecza> connectionSpecs;
  final ProxySelector proxySelector;
  final @Nullableq
  Proxy proxy;
  final @Nullableq
  SSLSocketFactory sslSocketFactory;
  final @Nullableq
  HostnameVerifier hostnameVerifier;
  final @Nullableq
  CertificatePinnerza certificatePinner;

  public Addressq(String uriHost, int uriPort, Dnsza dns, SocketFactory socketFactory,
                  @Nullableq SSLSocketFactory sslSocketFactory, @Nullableq HostnameVerifier hostnameVerifier,
                  @Nullableq CertificatePinnerza certificatePinner, Authenticatorq proxyAuthenticator,
                  @Nullableq Proxy proxy, List<Protocolza> protocols, List<ConnectionSpecza> connectionSpecs,
                  ProxySelector proxySelector) {
    this.url = new HttpUrlza.Builder()
        .scheme(sslSocketFactory != null ? "https" : "http")
        .host(uriHost)
        .port(uriPort)
        .build();

    if (dns == null) throw new NullPointerException("dns == null");
    this.dns = dns;

    if (socketFactory == null) throw new NullPointerException("socketFactory == null");
    this.socketFactory = socketFactory;

    if (proxyAuthenticator == null) {
      throw new NullPointerException("proxyAuthenticator == null");
    }
    this.proxyAuthenticator = proxyAuthenticator;

    if (protocols == null) throw new NullPointerException("protocols == null");
    this.protocols = Utilaq.immutableList(protocols);

    if (connectionSpecs == null) throw new NullPointerException("connectionSpecs == null");
    this.connectionSpecs = Utilaq.immutableList(connectionSpecs);

    if (proxySelector == null) throw new NullPointerException("proxySelector == null");
    this.proxySelector = proxySelector;

    this.proxy = proxy;
    this.sslSocketFactory = sslSocketFactory;
    this.hostnameVerifier = hostnameVerifier;
    this.certificatePinner = certificatePinner;
  }


  public HttpUrlza url() {
    return url;
  }


  public Dnsza dns() {
    return dns;
  }


  public SocketFactory socketFactory() {
    return socketFactory;
  }


  public Authenticatorq proxyAuthenticator() {
    return proxyAuthenticator;
  }


  public List<Protocolza> protocols() {
    return protocols;
  }

  public List<ConnectionSpecza> connectionSpecs() {
    return connectionSpecs;
  }


  public ProxySelector proxySelector() {
    return proxySelector;
  }


  public @Nullableq
  Proxy proxy() {
    return proxy;
  }


  public @Nullableq
  SSLSocketFactory sslSocketFactory() {
    return sslSocketFactory;
  }


  public @Nullableq
  HostnameVerifier hostnameVerifier() {
    return hostnameVerifier;
  }


  public @Nullableq
  CertificatePinnerza certificatePinner() {
    return certificatePinner;
  }

  @Override public boolean equals(@Nullableq Object other) {
    return other instanceof Addressq
        && url.equals(((Addressq) other).url)
        && equalsNonHost((Addressq) other);
  }

  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + url.hashCode();
    result = 31 * result + dns.hashCode();
    result = 31 * result + proxyAuthenticator.hashCode();
    result = 31 * result + protocols.hashCode();
    result = 31 * result + connectionSpecs.hashCode();
    result = 31 * result + proxySelector.hashCode();
    result = 31 * result + (proxy != null ? proxy.hashCode() : 0);
    result = 31 * result + (sslSocketFactory != null ? sslSocketFactory.hashCode() : 0);
    result = 31 * result + (hostnameVerifier != null ? hostnameVerifier.hashCode() : 0);
    result = 31 * result + (certificatePinner != null ? certificatePinner.hashCode() : 0);
    return result;
  }

  boolean equalsNonHost(Addressq that) {
    return this.dns.equals(that.dns)
        && this.proxyAuthenticator.equals(that.proxyAuthenticator)
        && this.protocols.equals(that.protocols)
        && this.connectionSpecs.equals(that.connectionSpecs)
        && this.proxySelector.equals(that.proxySelector)
        && equal(this.proxy, that.proxy)
        && equal(this.sslSocketFactory, that.sslSocketFactory)
        && equal(this.hostnameVerifier, that.hostnameVerifier)
        && equal(this.certificatePinner, that.certificatePinner)
        && this.url().port() == that.url().port();
  }

  @Override public String toString() {
    StringBuilder result = new StringBuilder()
        .append("Address{")
        .append(url.host()).append(":").append(url.port());

    if (proxy != null) {
      result.append(", proxy=").append(proxy);
    } else {
      result.append(", proxySelector=").append(proxySelector);
    }

    result.append("}");
    return result.toString();
  }
}
