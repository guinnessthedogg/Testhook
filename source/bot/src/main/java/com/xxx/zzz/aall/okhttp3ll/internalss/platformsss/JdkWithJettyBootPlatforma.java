
package com.xxx.zzz.aall.okhttp3ll.internalss.platformsss;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.net.ssl.SSLSocket;

import com.xxx.zzz.aall.okhttp3ll.Protocolza;


class JdkWithJettyBootPlatforma extends Platformq {
  private final Method putMethod;
  private final Method getMethod;
  private final Method removeMethod;
  private final Class<?> clientProviderClass;
  private final Class<?> serverProviderClass;

  JdkWithJettyBootPlatforma(Method putMethod, Method getMethod, Method removeMethod,
                            Class<?> clientProviderClass, Class<?> serverProviderClass) {
    this.putMethod = putMethod;
    this.getMethod = getMethod;
    this.removeMethod = removeMethod;
    this.clientProviderClass = clientProviderClass;
    this.serverProviderClass = serverProviderClass;
  }

  @Override public void configureTlsExtensions(
      SSLSocket sslSocket, String hostname, List<Protocolza> protocols) {
    List<String> names = alpnProtocolNames(protocols);

    try {
      Object provider = Proxy.newProxyInstance(Platformq.class.getClassLoader(),
          new Class[] {clientProviderClass, serverProviderClass}, new JettyNegoProvider(names));
      putMethod.invoke(null, sslSocket, provider);
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  @Override public void afterHandshake(SSLSocket sslSocket) {
    try {
      removeMethod.invoke(null, sslSocket);
    } catch (IllegalAccessException | InvocationTargetException ignored) {
      throw new AssertionError();
    }
  }

  @Override public String getSelectedProtocol(SSLSocket socket) {
    try {
      JettyNegoProvider provider =
          (JettyNegoProvider) Proxy.getInvocationHandler(getMethod.invoke(null, socket));
      if (!provider.unsupported && provider.selected == null) {
        Platformq.get().log(INFO, "ALPN callback dropped: HTTP/2 is disabled. "
            + "Is alpn-boot on the boot class path?", null);
        return null;
      }
      return provider.unsupported ? null : provider.selected;
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new AssertionError();
    }
  }

  public static Platformq buildIfSupported() {
    
    try {
      String negoClassName = "org.eclipse.jetty.alpn.ALPN";
      Class<?> negoClass = Class.forName(negoClassName);
      Class<?> providerClass = Class.forName(negoClassName + "$Provider");
      Class<?> clientProviderClass = Class.forName(negoClassName + "$ClientProvider");
      Class<?> serverProviderClass = Class.forName(negoClassName + "$ServerProvider");
      Method putMethod = negoClass.getMethod("put", SSLSocket.class, providerClass);
      Method getMethod = negoClass.getMethod("get", SSLSocket.class);
      Method removeMethod = negoClass.getMethod("remove", SSLSocket.class);
      return new JdkWithJettyBootPlatforma(
          putMethod, getMethod, removeMethod, clientProviderClass, serverProviderClass);
    } catch (ClassNotFoundException | NoSuchMethodException ignored) {
    }

    return null;
  }


  private static class JettyNegoProvider implements InvocationHandler {

    private final List<String> protocols;

    boolean unsupported;

    String selected;

    JettyNegoProvider(List<String> protocols) {
      this.protocols = protocols;
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      Class<?> returnType = method.getReturnType();
      if (args == null) {
        args = Utilaq.EMPTY_STRING_ARRAY;
      }
      if (methodName.equals("supports") && boolean.class == returnType) {
        return true; 
      } else if (methodName.equals("unsupported") && void.class == returnType) {
        this.unsupported = true; 
        return null;
      } else if (methodName.equals("protocols") && args.length == 0) {
        return protocols; 
      } else if ((methodName.equals("selectProtocol") || methodName.equals("select"))
          && String.class == returnType && args.length == 1 && args[0] instanceof List) {
        List<String> peerProtocols = (List) args[0];
        
        for (int i = 0, size = peerProtocols.size(); i < size; i++) {
          if (protocols.contains(peerProtocols.get(i))) {
            return selected = peerProtocols.get(i);
          }
        }
        return selected = protocols.get(0); 
      } else if ((methodName.equals("protocolSelected") || methodName.equals("selected"))
          && args.length == 1) {
        this.selected = (String) args[0]; 
        return null;
      } else {
        return method.invoke(this, args);
      }
    }
  }
}
