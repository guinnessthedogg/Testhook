
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;
import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.CertificateChainCleaner;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.InternalCacheq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RealConnection;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RouteDatabase;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.OkHostnameVerifier;
import com.xxx.zzz.aall.okhttp3ll.internalss.wssss.RealWebSocket;


public class OkHttpClientza implements Cloneable, Callzadasd.Factory, WebSocketzqa.Factory {
  static final List<Protocolza> DEFAULT_PROTOCOLS = Utilaq.immutableList(
      Protocolza.HTTP_2, Protocolza.HTTP_1_1);

  static final List<ConnectionSpecza> DEFAULT_CONNECTION_SPECS = Utilaq.immutableList(
      ConnectionSpecza.MODERN_TLS, ConnectionSpecza.CLEARTEXT);

  static {
    Internalq.instance = new Internalq() {
      @Override public void addLenient(Headersza.Builder builder, String line) {
        builder.addLenient(line);
      }

      @Override public void addLenient(Headersza.Builder builder, String name, String value) {
        builder.addLenient(name, value);
      }

      @Override public void setCache(Builder builder, InternalCacheq internalCache) {
        builder.setInternalCache(internalCache);
      }

      @Override public boolean connectionBecameIdle(
              ConnectionPoolza pool, RealConnection connection) {
        return pool.connectionBecameIdle(connection);
      }

      @Override public RealConnection get(ConnectionPoolza pool, Addressq address,
                                          StreamAllocation streamAllocation, Routeza route) {
        return pool.get(address, streamAllocation, route);
      }

      @Override public boolean equalsNonHost(Addressq a, Addressq b) {
        return a.equalsNonHost(b);
      }

      @Override public Socket deduplicate(
              ConnectionPoolza pool, Addressq address, StreamAllocation streamAllocation) {
        return pool.deduplicate(address, streamAllocation);
      }

      @Override public void put(ConnectionPoolza pool, RealConnection connection) {
        pool.put(connection);
      }

      @Override public RouteDatabase routeDatabase(ConnectionPoolza connectionPool) {
        return connectionPool.routeDatabase;
      }

      @Override public int code(Responseza.Builder responseBuilder) {
        return responseBuilder.code;
      }

      @Override
      public void apply(ConnectionSpecza tlsConfiguration, SSLSocket sslSocket, boolean isFallback) {
        tlsConfiguration.apply(sslSocket, isFallback);
      }

      @Override public HttpUrlza getHttpUrlChecked(String url)
          throws MalformedURLException, UnknownHostException {
        return HttpUrlza.getChecked(url);
      }

      @Override public StreamAllocation streamAllocation(Callzadasd call) {
        return ((RealCallzaasas) call).streamAllocation();
      }

      @Override public Callzadasd newWebSocketCall(OkHttpClientza client, Requestza originalRequest) {
        return new RealCallzaasas(client, originalRequest, true);
      }
    };
  }

  final DispatcherQ dispatcher;
  final @Nullableq
  Proxy proxy;
  final List<Protocolza> protocols;
  final List<ConnectionSpecza> connectionSpecs;
  final List<Interceptorza> interceptors;
  final List<Interceptorza> networkInterceptors;
  final EventListenerza.Factory eventListenerFactory;
  final ProxySelector proxySelector;
  final CookieJarza cookieJar;
  final @Nullableq
  Cachea cache;
  final @Nullableq
  InternalCacheq internalCache;
  final SocketFactory socketFactory;
  final @Nullableq
  SSLSocketFactory sslSocketFactory;
  final @Nullableq
  CertificateChainCleaner certificateChainCleaner;
  final HostnameVerifier hostnameVerifier;
  final CertificatePinnerza certificatePinner;
  final Authenticatorq proxyAuthenticator;
  final Authenticatorq authenticator;
  final ConnectionPoolza connectionPool;
  final Dnsza dns;
  final boolean followSslRedirects;
  final boolean followRedirects;
  final boolean retryOnConnectionFailure;
  final int connectTimeout;
  final int readTimeout;
  final int writeTimeout;
  final int pingInterval;

  public OkHttpClientza() {
    this(new Builder());
  }

  OkHttpClientza(Builder builder) {
    this.dispatcher = builder.dispatcher;
    this.proxy = builder.proxy;
    this.protocols = builder.protocols;
    this.connectionSpecs = builder.connectionSpecs;
    this.interceptors = Utilaq.immutableList(builder.interceptors);
    this.networkInterceptors = Utilaq.immutableList(builder.networkInterceptors);
    this.eventListenerFactory = builder.eventListenerFactory;
    this.proxySelector = builder.proxySelector;
    this.cookieJar = builder.cookieJar;
    this.cache = builder.cache;
    this.internalCache = builder.internalCache;
    this.socketFactory = builder.socketFactory;

    boolean isTLS = false;
    for (ConnectionSpecza spec : connectionSpecs) {
      isTLS = isTLS || spec.isTls();
    }

    if (builder.sslSocketFactory != null || !isTLS) {
      this.sslSocketFactory = builder.sslSocketFactory;
      this.certificateChainCleaner = builder.certificateChainCleaner;
    } else {
      X509TrustManager trustManager = systemDefaultTrustManager();
      this.sslSocketFactory = systemDefaultSslSocketFactory(trustManager);
      this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
    }

    this.hostnameVerifier = builder.hostnameVerifier;
    this.certificatePinner = builder.certificatePinner.withCertificateChainCleaner(
        certificateChainCleaner);
    this.proxyAuthenticator = builder.proxyAuthenticator;
    this.authenticator = builder.authenticator;
    this.connectionPool = builder.connectionPool;
    this.dns = builder.dns;
    this.followSslRedirects = builder.followSslRedirects;
    this.followRedirects = builder.followRedirects;
    this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
    this.connectTimeout = builder.connectTimeout;
    this.readTimeout = builder.readTimeout;
    this.writeTimeout = builder.writeTimeout;
    this.pingInterval = builder.pingInterval;
  }

  private X509TrustManager systemDefaultTrustManager() {
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
        throw new IllegalStateException("Unexpected default trust managers:"
            + Arrays.toString(trustManagers));
      }
      return (X509TrustManager) trustManagers[0];
    } catch (GeneralSecurityException e) {
      throw new AssertionError(); 
    }
  }

  private SSLSocketFactory systemDefaultSslSocketFactory(X509TrustManager trustManager) {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] { trustManager }, null);
      return sslContext.getSocketFactory();
    } catch (GeneralSecurityException e) {
      throw new AssertionError(); 
    }
  }

  
  public int connectTimeoutMillis() {
    return connectTimeout;
  }

  
  public int readTimeoutMillis() {
    return readTimeout;
  }

  
  public int writeTimeoutMillis() {
    return writeTimeout;
  }

  
  public int pingIntervalMillis() {
    return pingInterval;
  }

  public Proxy proxy() {
    return proxy;
  }

  public ProxySelector proxySelector() {
    return proxySelector;
  }

  public CookieJarza cookieJar() {
    return cookieJar;
  }

  public Cachea cache() {
    return cache;
  }

  InternalCacheq internalCache() {
    return cache != null ? cache.internalCache : internalCache;
  }

  public Dnsza dns() {
    return dns;
  }

  public SocketFactory socketFactory() {
    return socketFactory;
  }

  public SSLSocketFactory sslSocketFactory() {
    return sslSocketFactory;
  }

  public HostnameVerifier hostnameVerifier() {
    return hostnameVerifier;
  }

  public CertificatePinnerza certificatePinner() {
    return certificatePinner;
  }

  public Authenticatorq authenticator() {
    return authenticator;
  }

  public Authenticatorq proxyAuthenticator() {
    return proxyAuthenticator;
  }

  public ConnectionPoolza connectionPool() {
    return connectionPool;
  }

  public boolean followSslRedirects() {
    return followSslRedirects;
  }

  public boolean followRedirects() {
    return followRedirects;
  }

  public boolean retryOnConnectionFailure() {
    return retryOnConnectionFailure;
  }

  public DispatcherQ dispatcher() {
    return dispatcher;
  }

  public List<Protocolza> protocols() {
    return protocols;
  }

  public List<ConnectionSpecza> connectionSpecs() {
    return connectionSpecs;
  }

  
  public List<Interceptorza> interceptors() {
    return interceptors;
  }

  
  public List<Interceptorza> networkInterceptors() {
    return networkInterceptors;
  }

  
   EventListenerza.Factory eventListenerFactory() {
    return eventListenerFactory;
  }

  
  @Override public Callzadasd newCall(Requestza request) {
    return new RealCallzaasas(this, request, false );
  }

  
  @Override public WebSocketzqa newWebSocket(Requestza request, WebSocketListenerzaq listener) {
    RealWebSocket webSocket = new RealWebSocket(request, listener, new Random());
    webSocket.connect(this);
    return webSocket;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static final class Builder {
    DispatcherQ dispatcher;
    @Nullableq
    Proxy proxy;
    List<Protocolza> protocols;
    List<ConnectionSpecza> connectionSpecs;
    final List<Interceptorza> interceptors = new ArrayList<>();
    final List<Interceptorza> networkInterceptors = new ArrayList<>();
    EventListenerza.Factory eventListenerFactory;
    ProxySelector proxySelector;
    CookieJarza cookieJar;
    @Nullableq
    Cachea cache;
    @Nullableq
    InternalCacheq internalCache;
    SocketFactory socketFactory;
    @Nullableq
    SSLSocketFactory sslSocketFactory;
    @Nullableq
    CertificateChainCleaner certificateChainCleaner;
    HostnameVerifier hostnameVerifier;
    CertificatePinnerza certificatePinner;
    Authenticatorq proxyAuthenticator;
    Authenticatorq authenticator;
    ConnectionPoolza connectionPool;
    Dnsza dns;
    boolean followSslRedirects;
    boolean followRedirects;
    boolean retryOnConnectionFailure;
    int connectTimeout;
    int readTimeout;
    int writeTimeout;
    int pingInterval;

    public Builder() {
      dispatcher = new DispatcherQ();
      protocols = DEFAULT_PROTOCOLS;
      connectionSpecs = DEFAULT_CONNECTION_SPECS;
      eventListenerFactory = EventListenerza.factory(EventListenerza.NONE);
      proxySelector = ProxySelector.getDefault();
      cookieJar = CookieJarza.NO_COOKIES;
      socketFactory = SocketFactory.getDefault();
      hostnameVerifier = OkHostnameVerifier.INSTANCE;
      certificatePinner = CertificatePinnerza.DEFAULT;
      proxyAuthenticator = Authenticatorq.NONE;
      authenticator = Authenticatorq.NONE;
      connectionPool = new ConnectionPoolza();
      dns = Dnsza.SYSTEM;
      followSslRedirects = true;
      followRedirects = true;
      retryOnConnectionFailure = true;
      connectTimeout = 10_000;
      readTimeout = 10_000;
      writeTimeout = 10_000;
      pingInterval = 0;
    }

    Builder(OkHttpClientza okHttpClient) {
      this.dispatcher = okHttpClient.dispatcher;
      this.proxy = okHttpClient.proxy;
      this.protocols = okHttpClient.protocols;
      this.connectionSpecs = okHttpClient.connectionSpecs;
      this.interceptors.addAll(okHttpClient.interceptors);
      this.networkInterceptors.addAll(okHttpClient.networkInterceptors);
      this.eventListenerFactory = okHttpClient.eventListenerFactory;
      this.proxySelector = okHttpClient.proxySelector;
      this.cookieJar = okHttpClient.cookieJar;
      this.internalCache = okHttpClient.internalCache;
      this.cache = okHttpClient.cache;
      this.socketFactory = okHttpClient.socketFactory;
      this.sslSocketFactory = okHttpClient.sslSocketFactory;
      this.certificateChainCleaner = okHttpClient.certificateChainCleaner;
      this.hostnameVerifier = okHttpClient.hostnameVerifier;
      this.certificatePinner = okHttpClient.certificatePinner;
      this.proxyAuthenticator = okHttpClient.proxyAuthenticator;
      this.authenticator = okHttpClient.authenticator;
      this.connectionPool = okHttpClient.connectionPool;
      this.dns = okHttpClient.dns;
      this.followSslRedirects = okHttpClient.followSslRedirects;
      this.followRedirects = okHttpClient.followRedirects;
      this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure;
      this.connectTimeout = okHttpClient.connectTimeout;
      this.readTimeout = okHttpClient.readTimeout;
      this.writeTimeout = okHttpClient.writeTimeout;
      this.pingInterval = okHttpClient.pingInterval;
    }

    
    public Builder connectTimeout(long timeout, TimeUnit unit) {
      connectTimeout = checkDuration("timeout", timeout, unit);
      return this;
    }

    
    public Builder readTimeout(long timeout, TimeUnit unit) {
      readTimeout = checkDuration("timeout", timeout, unit);
      return this;
    }

    
    public Builder writeTimeout(long timeout, TimeUnit unit) {
      writeTimeout = checkDuration("timeout", timeout, unit);
      return this;
    }

    
    public Builder pingInterval(long interval, TimeUnit unit) {
      pingInterval = checkDuration("interval", interval, unit);
      return this;
    }

    private static int checkDuration(String name, long duration, TimeUnit unit) {
      if (duration < 0) throw new IllegalArgumentException(name + " < 0");
      if (unit == null) throw new NullPointerException("unit == null");
      long millis = unit.toMillis(duration);
      if (millis > Integer.MAX_VALUE) throw new IllegalArgumentException(name + " too large.");
      if (millis == 0 && duration > 0) throw new IllegalArgumentException(name + " too small.");
      return (int) millis;
    }

    
    public Builder proxy(@Nullableq Proxy proxy) {
      this.proxy = proxy;
      return this;
    }

    
    public Builder proxySelector(ProxySelector proxySelector) {
      this.proxySelector = proxySelector;
      return this;
    }

    
    public Builder cookieJar(CookieJarza cookieJar) {
      if (cookieJar == null) throw new NullPointerException("cookieJar == null");
      this.cookieJar = cookieJar;
      return this;
    }

    
    void setInternalCache(@Nullableq InternalCacheq internalCache) {
      this.internalCache = internalCache;
      this.cache = null;
    }

    
    public Builder cache(@Nullableq Cachea cache) {
      this.cache = cache;
      this.internalCache = null;
      return this;
    }

    
    public Builder dns(Dnsza dns) {
      if (dns == null) throw new NullPointerException("dns == null");
      this.dns = dns;
      return this;
    }

    
    public Builder socketFactory(SocketFactory socketFactory) {
      if (socketFactory == null) throw new NullPointerException("socketFactory == null");
      this.socketFactory = socketFactory;
      return this;
    }

    
    public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
      if (sslSocketFactory == null) throw new NullPointerException("sslSocketFactory == null");
      X509TrustManager trustManager = Platformq.get().trustManager(sslSocketFactory);
      if (trustManager == null) {
        throw new IllegalStateException("Unable to extract the trust manager on " + Platformq.get()
            + ", sslSocketFactory is " + sslSocketFactory.getClass());
      }
      this.sslSocketFactory = sslSocketFactory;
      this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
      return this;
    }

    
    public Builder sslSocketFactory(
        SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
      if (sslSocketFactory == null) throw new NullPointerException("sslSocketFactory == null");
      if (trustManager == null) throw new NullPointerException("trustManager == null");
      this.sslSocketFactory = sslSocketFactory;
      this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
      return this;
    }

    
    public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
      if (hostnameVerifier == null) throw new NullPointerException("hostnameVerifier == null");
      this.hostnameVerifier = hostnameVerifier;
      return this;
    }

    
    public Builder certificatePinner(CertificatePinnerza certificatePinner) {
      if (certificatePinner == null) throw new NullPointerException("certificatePinner == null");
      this.certificatePinner = certificatePinner;
      return this;
    }

    
    public Builder authenticator(Authenticatorq authenticator) {
      if (authenticator == null) throw new NullPointerException("authenticator == null");
      this.authenticator = authenticator;
      return this;
    }

    
    public Builder proxyAuthenticator(Authenticatorq proxyAuthenticator) {
      if (proxyAuthenticator == null) throw new NullPointerException("proxyAuthenticator == null");
      this.proxyAuthenticator = proxyAuthenticator;
      return this;
    }

    
    public Builder connectionPool(ConnectionPoolza connectionPool) {
      if (connectionPool == null) throw new NullPointerException("connectionPool == null");
      this.connectionPool = connectionPool;
      return this;
    }

    
    public Builder followSslRedirects(boolean followProtocolRedirects) {
      this.followSslRedirects = followProtocolRedirects;
      return this;
    }

    
    public Builder followRedirects(boolean followRedirects) {
      this.followRedirects = followRedirects;
      return this;
    }

    
    public Builder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
      this.retryOnConnectionFailure = retryOnConnectionFailure;
      return this;
    }

    
    public Builder dispatcher(DispatcherQ dispatcher) {
      if (dispatcher == null) throw new IllegalArgumentException("dispatcher == null");
      this.dispatcher = dispatcher;
      return this;
    }

    
    public Builder protocols(List<Protocolza> protocols) {
      
      protocols = new ArrayList<>(protocols);

      
      if (!protocols.contains(Protocolza.HTTP_1_1)) {
        throw new IllegalArgumentException("protocols doesn't contain http/1.1: " + protocols);
      }
      if (protocols.contains(Protocolza.HTTP_1_0)) {
        throw new IllegalArgumentException("protocols must not contain http/1.0: " + protocols);
      }
      if (protocols.contains(null)) {
        throw new IllegalArgumentException("protocols must not contain null");
      }

      
      protocols.remove(Protocolza.SPDY_3);

      
      this.protocols = Collections.unmodifiableList(protocols);
      return this;
    }

    public Builder connectionSpecs(List<ConnectionSpecza> connectionSpecs) {
      this.connectionSpecs = Utilaq.immutableList(connectionSpecs);
      return this;
    }

    
    public List<Interceptorza> interceptors() {
      return interceptors;
    }

    public Builder addInterceptor(Interceptorza interceptor) {
      interceptors.add(interceptor);
      return this;
    }

    
    public List<Interceptorza> networkInterceptors() {
      return networkInterceptors;
    }

    public Builder addNetworkInterceptor(Interceptorza interceptor) {
      networkInterceptors.add(interceptor);
      return this;
    }

    
     Builder eventListener(EventListenerza eventListener) {
      if (eventListener == null) throw new NullPointerException("eventListener == null");
      this.eventListenerFactory = EventListenerza.factory(eventListener);
      return this;
    }

    
     Builder eventListenerFactory(EventListenerza.Factory eventListenerFactory) {
      if (eventListenerFactory == null) {
        throw new NullPointerException("eventListenerFactory == null");
      }
      this.eventListenerFactory = eventListenerFactory;
      return this;
    }

    public OkHttpClientza build() {
      return new OkHttpClientza(this);
    }
  }
}
