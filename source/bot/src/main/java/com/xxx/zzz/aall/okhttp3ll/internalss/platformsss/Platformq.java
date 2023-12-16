
package com.xxx.zzz.aall.okhttp3ll.internalss.platformsss;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.TrustRootIndex;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.BasicCertificateChainCleaner;
import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.CertificateChainCleaner;
import com.xxx.zzz.aall.okioss.Bufferzaq;


public class Platformq {
  private static final Platformq PLATFORM = findPlatform();
  public static final int INFO = 4;
  public static final int WARN = 5;
  private static final Logger logger = Logger.getLogger(OkHttpClientza.class.getName());

  public static Platformq get() {
    return PLATFORM;
  }

  
  public String getPrefix() {
    return "OkHttp";
  }

  public X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
    
    
    
    try {
      Class<?> sslContextClass = Class.forName("sun.security.ssl.SSLContextImpl");
      Object context = readFieldOrNull(sslSocketFactory, sslContextClass, "context");
      if (context == null) return null;
      return readFieldOrNull(context, X509TrustManager.class, "trustManager");
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  
  public void configureTlsExtensions(SSLSocket sslSocket, String hostname,
      List<Protocolza> protocols) {
  }

  
  public void afterHandshake(SSLSocket sslSocket) {
  }

  
  public String getSelectedProtocol(SSLSocket socket) {
    return null;
  }

  public void connectSocket(Socket socket, InetSocketAddress address,
      int connectTimeout) throws IOException {
    socket.connect(address, connectTimeout);
  }

  public void log(int level, String message, Throwable t) {
    Level logLevel = level == WARN ? Level.WARNING : Level.INFO;
    logger.log(logLevel, message, t);
  }

  public boolean isCleartextTrafficPermitted(String hostname) {
    return true;
  }

  
  public Object getStackTraceForCloseable(String closer) {
    if (logger.isLoggable(Level.FINE)) {
      return new Throwable(closer); 
    }
    return null;
  }

  public void logCloseableLeak(String message, Object stackTrace) {
    if (stackTrace == null) {
      message += " To see where this was allocated, set the OkHttpClient logger level to FINE: "
          + "Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
    }
    log(WARN, message, (Throwable) stackTrace);
  }

  public static List<String> alpnProtocolNames(List<Protocolza> protocols) {
    List<String> names = new ArrayList<>(protocols.size());
    for (int i = 0, size = protocols.size(); i < size; i++) {
      Protocolza protocol = protocols.get(i);
      if (protocol == Protocolza.HTTP_1_0) continue; 
      names.add(protocol.toString());
    }
    return names;
  }

  public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager trustManager) {
    return new BasicCertificateChainCleaner(TrustRootIndex.get(trustManager));
  }

  
  private static Platformq findPlatform() {
    Platformq android = AndroidPlatforma.buildIfSupported();

    if (android != null) {
      return android;
    }

    Platformq jdk9 = Jdk9Platforma.buildIfSupported();

    if (jdk9 != null) {
      return jdk9;
    }

    Platformq jdkWithJettyBoot = JdkWithJettyBootPlatforma.buildIfSupported();

    if (jdkWithJettyBoot != null) {
      return jdkWithJettyBoot;
    }

    
    return new Platformq();
  }

  
  static byte[] concatLengthPrefixed(List<Protocolza> protocols) {
    Bufferzaq result = new Bufferzaq();
    for (int i = 0, size = protocols.size(); i < size; i++) {
      Protocolza protocol = protocols.get(i);
      if (protocol == Protocolza.HTTP_1_0) continue; 
      result.writeByte(protocol.toString().length());
      result.writeUtf8(protocol.toString());
    }
    return result.readByteArray();
  }

  static <T> T readFieldOrNull(Object instance, Class<T> fieldType, String fieldName) {
    for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
      try {
        Field field = c.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(instance);
        if (value == null || !fieldType.isInstance(value)) return null;
        return fieldType.cast(value);
      } catch (NoSuchFieldException ignored) {
      } catch (IllegalAccessException e) {
        throw new AssertionError();
      }
    }

    
    if (!fieldName.equals("delegate")) {
      Object delegate = readFieldOrNull(instance, Object.class, "delegate");
      if (delegate != null) return readFieldOrNull(delegate, fieldType, fieldName);
    }

    return null;
  }
}
