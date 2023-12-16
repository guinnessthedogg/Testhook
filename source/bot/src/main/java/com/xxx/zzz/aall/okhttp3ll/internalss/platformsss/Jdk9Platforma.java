
package com.xxx.zzz.aall.okhttp3ll.internalss.platformsss;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.xxx.zzz.aall.okhttp3ll.Protocolza;


final class Jdk9Platforma extends Platformq {
  final Method setProtocolMethod;
  final Method getProtocolMethod;

  Jdk9Platforma(Method setProtocolMethod, Method getProtocolMethod) {
    this.setProtocolMethod = setProtocolMethod;
    this.getProtocolMethod = getProtocolMethod;
  }

  @Override
  public void configureTlsExtensions(SSLSocket sslSocket, String hostname,
      List<Protocolza> protocols) {
    try {
      SSLParameters sslParameters = sslSocket.getSSLParameters();

      List<String> names = alpnProtocolNames(protocols);

      setProtocolMethod.invoke(sslParameters,
          new Object[] {names.toArray(new String[names.size()])});

      sslSocket.setSSLParameters(sslParameters);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new AssertionError();
    }
  }

  @Override
  public String getSelectedProtocol(SSLSocket socket) {
    try {
      String protocol = (String) getProtocolMethod.invoke(socket);

      
      
      if (protocol == null || protocol.equals("")) {
        return null;
      }

      return protocol;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new AssertionError();
    }
  }

  @Override public X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
    
    
    
    
    throw new UnsupportedOperationException(
        "clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 9+");
  }

  public static Jdk9Platforma buildIfSupported() {
    
    try {
      Method setProtocolMethod =
          SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
      Method getProtocolMethod = SSLSocket.class.getMethod("getApplicationProtocol");

      return new Jdk9Platforma(setProtocolMethod, getProtocolMethod);
    } catch (NoSuchMethodException ignored) {
      
    }

    return null;
  }
}
