
package com.xxx.zzz.aall.okhttp3ll.internalss.connectionss;

import com.xxx.zzz.aall.okhttp3ll.Addressq;
import com.xxx.zzz.aall.okhttp3ll.Routeza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;


public final class RouteSelector {
  private final Addressq address;
  private final RouteDatabase routeDatabase;


  private Proxy lastProxy;
  private InetSocketAddress lastInetSocketAddress;


  private List<Proxy> proxies = Collections.emptyList();
  private int nextProxyIndex;


  private List<InetSocketAddress> inetSocketAddresses = Collections.emptyList();
  private int nextInetSocketAddressIndex;


  private final List<Routeza> postponedRoutes = new ArrayList<>();

  public RouteSelector(Addressq address, RouteDatabase routeDatabase) {
    this.address = address;
    this.routeDatabase = routeDatabase;

    resetNextProxy(address.url(), address.proxy());
  }


  public boolean hasNext() {
    return hasNextInetSocketAddress()
        || hasNextProxy()
        || hasNextPostponed();
  }

  public Routeza next() throws IOException {
    
    if (!hasNextInetSocketAddress()) {
      if (!hasNextProxy()) {
        if (!hasNextPostponed()) {
          throw new NoSuchElementException();
        }
        return nextPostponed();
      }
      lastProxy = nextProxy();
    }
    lastInetSocketAddress = nextInetSocketAddress();

    Routeza route = new Routeza(address, lastProxy, lastInetSocketAddress);
    if (routeDatabase.shouldPostpone(route)) {
      postponedRoutes.add(route);
      
      return next();
    }

    return route;
  }


  public void connectFailed(Routeza failedRoute, IOException failure) {
    if (failedRoute.proxy().type() != Proxy.Type.DIRECT && address.proxySelector() != null) {
      
      address.proxySelector().connectFailed(
          address.url().uri(), failedRoute.proxy().address(), failure);
    }

    routeDatabase.failed(failedRoute);
  }


  private void resetNextProxy(HttpUrlza url, Proxy proxy) {
    if (proxy != null) {
      
      proxies = Collections.singletonList(proxy);
    } else {
      
      List<Proxy> proxiesOrNull = address.proxySelector().select(url.uri());
      proxies = proxiesOrNull != null && !proxiesOrNull.isEmpty()
          ? Utilaq.immutableList(proxiesOrNull)
          : Utilaq.immutableList(Proxy.NO_PROXY);
    }
    nextProxyIndex = 0;
  }


  private boolean hasNextProxy() {
    return nextProxyIndex < proxies.size();
  }


  private Proxy nextProxy() throws IOException {
    if (!hasNextProxy()) {
      throw new SocketException("No route to " + address.url().host()
          + "; exhausted proxy configurations: " + proxies);
    }
    Proxy result = proxies.get(nextProxyIndex++);
    resetNextInetSocketAddress(result);
    return result;
  }


  private void resetNextInetSocketAddress(Proxy proxy) throws IOException {
    
    inetSocketAddresses = new ArrayList<>();

    String socketHost;
    int socketPort;
    if (proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.SOCKS) {
      socketHost = address.url().host();
      socketPort = address.url().port();
    } else {
      SocketAddress proxyAddress = proxy.address();
      if (!(proxyAddress instanceof InetSocketAddress)) {
        throw new IllegalArgumentException(
            "Proxy.address() is not an " + "InetSocketAddress: " + proxyAddress.getClass());
      }
      InetSocketAddress proxySocketAddress = (InetSocketAddress) proxyAddress;
      socketHost = getHostString(proxySocketAddress);
      socketPort = proxySocketAddress.getPort();
    }

    if (socketPort < 1 || socketPort > 65535) {
      throw new SocketException("No route to " + socketHost + ":" + socketPort
          + "; port is out of range");
    }

    if (proxy.type() == Proxy.Type.SOCKS) {
      inetSocketAddresses.add(InetSocketAddress.createUnresolved(socketHost, socketPort));
    } else {
      
      List<InetAddress> addresses = address.dns().lookup(socketHost);
      if (addresses.isEmpty()) {
        throw new UnknownHostException(address.dns() + " returned no addresses for " + socketHost);
      }

      for (int i = 0, size = addresses.size(); i < size; i++) {
        InetAddress inetAddress = addresses.get(i);
        inetSocketAddresses.add(new InetSocketAddress(inetAddress, socketPort));
      }
    }

    nextInetSocketAddressIndex = 0;
  }


  
  static String getHostString(InetSocketAddress socketAddress) {
    InetAddress address = socketAddress.getAddress();
    if (address == null) {
      
      
      
      return socketAddress.getHostName();
    }
    
    
    return address.getHostAddress();
  }


  private boolean hasNextInetSocketAddress() {
    return nextInetSocketAddressIndex < inetSocketAddresses.size();
  }


  private InetSocketAddress nextInetSocketAddress() throws IOException {
    if (!hasNextInetSocketAddress()) {
      throw new SocketException("No route to " + address.url().host()
          + "; exhausted inet socket addresses: " + inetSocketAddresses);
    }
    return inetSocketAddresses.get(nextInetSocketAddressIndex++);
  }


  private boolean hasNextPostponed() {
    return !postponedRoutes.isEmpty();
  }


  private Routeza nextPostponed() {
    return postponedRoutes.remove(0);
  }
}
