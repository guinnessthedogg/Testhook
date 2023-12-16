
package com.xxx.zzz.aall.okhttp3ll;

import java.net.InetSocketAddress;
import java.net.Proxy;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class Routeza {
  final Addressq address;
  final Proxy proxy;
  final InetSocketAddress inetSocketAddress;

  public Routeza(Addressq address, Proxy proxy, InetSocketAddress inetSocketAddress) {
    if (address == null) {
      throw new NullPointerException("address == null");
    }
    if (proxy == null) {
      throw new NullPointerException("proxy == null");
    }
    if (inetSocketAddress == null) {
      throw new NullPointerException("inetSocketAddress == null");
    }
    this.address = address;
    this.proxy = proxy;
    this.inetSocketAddress = inetSocketAddress;
  }

  public Addressq address() {
    return address;
  }


  public Proxy proxy() {
    return proxy;
  }

  public InetSocketAddress socketAddress() {
    return inetSocketAddress;
  }


  public boolean requiresTunnel() {
    return address.sslSocketFactory != null && proxy.type() == Proxy.Type.HTTP;
  }

  @Override public boolean equals(@Nullableq Object other) {
    return other instanceof Routeza
        && ((Routeza) other).address.equals(address)
        && ((Routeza) other).proxy.equals(proxy)
        && ((Routeza) other).inetSocketAddress.equals(inetSocketAddress);
  }

  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + address.hashCode();
    result = 31 * result + proxy.hashCode();
    result = 31 * result + inetSocketAddress.hashCode();
    return result;
  }

  @Override public String toString() {
    return "Route{" + inetSocketAddress + "}";
  }
}
