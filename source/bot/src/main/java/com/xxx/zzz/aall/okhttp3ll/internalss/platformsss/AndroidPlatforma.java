
package com.xxx.zzz.aall.okhttp3ll.internalss.platformsss;

import android.util.Log;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.CertificateChainCleaner;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.xxx.zzz.aall.okhttp3ll.Protocolza;


class AndroidPlatforma extends Platformq {
  private static final int MAX_LOG_LENGTH = 4000;

  private final Class<?> sslParametersClass;
  private final OptionalMethoda<Socket> setUseSessionTickets;
  private final OptionalMethoda<Socket> setHostname;

  
  private final OptionalMethoda<Socket> getAlpnSelectedProtocol;
  private final OptionalMethoda<Socket> setAlpnProtocols;

  private final CloseGuard closeGuard = CloseGuard.get();

  AndroidPlatforma(Class<?> sslParametersClass, OptionalMethoda<Socket> setUseSessionTickets,
                   OptionalMethoda<Socket> setHostname, OptionalMethoda<Socket> getAlpnSelectedProtocol,
                   OptionalMethoda<Socket> setAlpnProtocols) {
    this.sslParametersClass = sslParametersClass;
    this.setUseSessionTickets = setUseSessionTickets;
    this.setHostname = setHostname;
    this.getAlpnSelectedProtocol = getAlpnSelectedProtocol;
    this.setAlpnProtocols = setAlpnProtocols;
  }

  @Override public void connectSocket(Socket socket, InetSocketAddress address,
      int connectTimeout) throws IOException {
    try {
      socket.connect(address, connectTimeout);
    } catch (AssertionError e) {
      if (Utilaq.isAndroidGetsocknameError(e)) throw new IOException(e);
      throw e;
    } catch (SecurityException e) {
      
      
      IOException ioException = new IOException("Exception in connect");
      ioException.initCause(e);
      throw ioException;
    }
  }

  @Override public X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
    Object context = readFieldOrNull(sslSocketFactory, sslParametersClass, "sslParameters");
    if (context == null) {
      
      
      try {
        Class<?> gmsSslParametersClass = Class.forName(
            "com.google.android.gms.org.conscrypt.SSLParametersImpl", false,
            sslSocketFactory.getClass().getClassLoader());
        context = readFieldOrNull(sslSocketFactory, gmsSslParametersClass, "sslParameters");
      } catch (ClassNotFoundException e) {
        return super.trustManager(sslSocketFactory);
      }
    }

    X509TrustManager x509TrustManager = readFieldOrNull(
        context, X509TrustManager.class, "x509TrustManager");
    if (x509TrustManager != null) return x509TrustManager;

    return readFieldOrNull(context, X509TrustManager.class, "trustManager");
  }

  @Override public void configureTlsExtensions(
      SSLSocket sslSocket, String hostname, List<Protocolza> protocols) {
    
    if (hostname != null) {
      setUseSessionTickets.invokeOptionalWithoutCheckedException(sslSocket, true);
      setHostname.invokeOptionalWithoutCheckedException(sslSocket, hostname);
    }

    
    if (setAlpnProtocols != null && setAlpnProtocols.isSupported(sslSocket)) {
      Object[] parameters = {concatLengthPrefixed(protocols)};
      setAlpnProtocols.invokeWithoutCheckedException(sslSocket, parameters);
    }
  }

  @Override public String getSelectedProtocol(SSLSocket socket) {
    if (getAlpnSelectedProtocol == null) return null;
    if (!getAlpnSelectedProtocol.isSupported(socket)) return null;

    byte[] alpnResult = (byte[]) getAlpnSelectedProtocol.invokeWithoutCheckedException(socket);
    return alpnResult != null ? new String(alpnResult, Utilaq.UTF_8) : null;
  }

  @Override public void log(int level, String message, Throwable t) {
    int logLevel = level == WARN ? Log.WARN : Log.DEBUG;
    if (t != null) message = message + '\n' + Log.getStackTraceString(t);

    
    for (int i = 0, length = message.length(); i < length; i++) {
      int newline = message.indexOf('\n', i);
      newline = newline != -1 ? newline : length;
      do {
        int end = Math.min(newline, i + MAX_LOG_LENGTH);
        Log.println(logLevel, "OkHttp", message.substring(i, end));
        i = end;
      } while (i < newline);
    }
  }

  @Override public Object getStackTraceForCloseable(String closer) {
    return closeGuard.createAndOpen(closer);
  }

  @Override public void logCloseableLeak(String message, Object stackTrace) {
    boolean reported = closeGuard.warnIfOpen(stackTrace);
    if (!reported) {
      
      log(WARN, message, null);
    }
  }

  @Override public boolean isCleartextTrafficPermitted(String hostname) {
    try {
      Class<?> networkPolicyClass = Class.forName("android.security.NetworkSecurityPolicy");
      Method getInstanceMethod = networkPolicyClass.getMethod("getInstance");
      Object networkSecurityPolicy = getInstanceMethod.invoke(null);
      Method isCleartextTrafficPermittedMethod = networkPolicyClass
          .getMethod("isCleartextTrafficPermitted", String.class);
      return (boolean) isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy, hostname);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      return super.isCleartextTrafficPermitted(hostname);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new AssertionError();
    }
  }

  public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager trustManager) {
    try {
      Class<?> extensionsClass = Class.forName("android.net.http.X509TrustManagerExtensions");
      Constructor<?> constructor = extensionsClass.getConstructor(X509TrustManager.class);
      Object extensions = constructor.newInstance(trustManager);
      Method checkServerTrusted = extensionsClass.getMethod(
          "checkServerTrusted", X509Certificate[].class, String.class, String.class);
      return new AndroidCertificateChainCleaner(extensions, checkServerTrusted);
    } catch (Exception e) {
      return super.buildCertificateChainCleaner(trustManager);
    }
  }

  public static Platformq buildIfSupported() {
    
    try {
      Class<?> sslParametersClass;
      try {
        sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
      } catch (ClassNotFoundException e) {
        
        sslParametersClass = Class.forName(
            "org.apache.harmony.xnet.provider.jsse.SSLParametersImpl");
      }

      OptionalMethoda<Socket> setUseSessionTickets = new OptionalMethoda<>(
          null, "setUseSessionTickets", boolean.class);
      OptionalMethoda<Socket> setHostname = new OptionalMethoda<>(
          null, "setHostname", String.class);
      OptionalMethoda<Socket> getAlpnSelectedProtocol = null;
      OptionalMethoda<Socket> setAlpnProtocols = null;

      
      try {
        Class.forName("android.net.Network"); 
        getAlpnSelectedProtocol = new OptionalMethoda<>(byte[].class, "getAlpnSelectedProtocol");
        setAlpnProtocols = new OptionalMethoda<>(null, "setAlpnProtocols", byte[].class);
      } catch (ClassNotFoundException ignored) {
      }

      return new AndroidPlatforma(sslParametersClass, setUseSessionTickets, setHostname,
          getAlpnSelectedProtocol, setAlpnProtocols);
    } catch (ClassNotFoundException ignored) {
      
    }

    return null;
  }

  
  static final class AndroidCertificateChainCleaner extends CertificateChainCleaner {
    private final Object x509TrustManagerExtensions;
    private final Method checkServerTrusted;

    AndroidCertificateChainCleaner(Object x509TrustManagerExtensions, Method checkServerTrusted) {
      this.x509TrustManagerExtensions = x509TrustManagerExtensions;
      this.checkServerTrusted = checkServerTrusted;
    }

    @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"}) 
    @Override public List<Certificate> clean(List<Certificate> chain, String hostname)
        throws SSLPeerUnverifiedException {
      try {
        X509Certificate[] certificates = chain.toArray(new X509Certificate[chain.size()]);
        return (List<Certificate>) checkServerTrusted.invoke(
            x509TrustManagerExtensions, certificates, "RSA", hostname);
      } catch (InvocationTargetException e) {
        SSLPeerUnverifiedException exception = new SSLPeerUnverifiedException(e.getMessage());
        exception.initCause(e);
        throw exception;
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    @Override public boolean equals(Object other) {
      return other instanceof AndroidCertificateChainCleaner; 
    }

    @Override public int hashCode() {
      return 0;
    }
  }

  
  static final class CloseGuard {
    private final Method getMethod;
    private final Method openMethod;
    private final Method warnIfOpenMethod;

    CloseGuard(Method getMethod, Method openMethod, Method warnIfOpenMethod) {
      this.getMethod = getMethod;
      this.openMethod = openMethod;
      this.warnIfOpenMethod = warnIfOpenMethod;
    }

    Object createAndOpen(String closer) {
      if (getMethod != null) {
        try {
          Object closeGuardInstance = getMethod.invoke(null);
          openMethod.invoke(closeGuardInstance, closer);
          return closeGuardInstance;
        } catch (Exception ignored) {
        }
      }
      return null;
    }

    boolean warnIfOpen(Object closeGuardInstance) {
      boolean reported = false;
      if (closeGuardInstance != null) {
        try {
          warnIfOpenMethod.invoke(closeGuardInstance);
          reported = true;
        } catch (Exception ignored) {
        }
      }
      return reported;
    }

    static CloseGuard get() {
      Method getMethod;
      Method openMethod;
      Method warnIfOpenMethod;

      try {
        Class<?> closeGuardClass = Class.forName("dalvik.system.CloseGuard");
        getMethod = closeGuardClass.getMethod("get");
        openMethod = closeGuardClass.getMethod("open", String.class);
        warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen");
      } catch (Exception ignored) {
        getMethod = null;
        openMethod = null;
        warnIfOpenMethod = null;
      }
      return new CloseGuard(getMethod, openMethod, warnIfOpenMethod);
    }
  }
}
