
package com.xxx.zzz.aall.okhttp3ll.internalss.connectionss;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PROXY_AUTH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.okhttp3ll.Addressq;
import com.xxx.zzz.aall.okhttp3ll.CertificatePinnerza;
import com.xxx.zzz.aall.okhttp3ll.Connectionza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Routeza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpHeaders;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.ErrorCodeq;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2Streama;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;
import com.xxx.zzz.aall.okhttp3ll.internalss.tlsss.OkHostnameVerifier;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.xxx.zzz.aall.okhttp3ll.ConnectionPoolza;
import com.xxx.zzz.aall.okhttp3ll.ConnectionSpecza;
import com.xxx.zzz.aall.okhttp3ll.Handshakeza;
import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Versionaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpCodec;
import com.xxx.zzz.aall.okhttp3ll.internalss.http1.Http1Codec;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2Codeca;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2Connectiona;
import com.xxx.zzz.aall.okhttp3ll.internalss.wssss.RealWebSocket;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;

public final class RealConnection extends Http2Connectiona.Listener implements Connectionza {
  private static final String NPE_THROW_WITH_NULL = "throw with null exception";
  private final ConnectionPoolza connectionPool;
  private final Routeza route;

  

  
  private Socket rawSocket;

  
  private Socket socket;
  private Handshakeza handshake;
  private Protocolza protocol;
  private Http2Connectiona http2Connection;
  private BufferedSourcezaqdfs source;
  private BufferedSinkzaqds sink;

  

  
  public boolean noNewStreams;

  public int successCount;

  
  public int allocationLimit = 1;

  
  public final List<Reference<StreamAllocation>> allocations = new ArrayList<>();

  
  public long idleAtNanos = Long.MAX_VALUE;

  public RealConnection(ConnectionPoolza connectionPool, Routeza route) {
    this.connectionPool = connectionPool;
    this.route = route;
  }

  public static RealConnection testConnection(
          ConnectionPoolza connectionPool, Routeza route, Socket socket, long idleAtNanos) {
    RealConnection result = new RealConnection(connectionPool, route);
    result.socket = socket;
    result.idleAtNanos = idleAtNanos;
    return result;
  }

  public void connect(
      int connectTimeout, int readTimeout, int writeTimeout, boolean connectionRetryEnabled) {
    if (protocol != null) throw new IllegalStateException("already connected");

    RouteException routeException = null;
    List<ConnectionSpecza> connectionSpecs = route.address().connectionSpecs();
    ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);

    if (route.address().sslSocketFactory() == null) {
      if (!connectionSpecs.contains(ConnectionSpecza.CLEARTEXT)) {
        throw new RouteException(new UnknownServiceException(
            "CLEARTEXT communication not enabled for client"));
      }
      String host = route.address().url().host();
      if (!Platformq.get().isCleartextTrafficPermitted(host)) {
        throw new RouteException(new UnknownServiceException(
            "CLEARTEXT communication to " + host + " not permitted by network security policy"));
      }
    }

    while (true) {
      try {
        if (route.requiresTunnel()) {
          connectTunnel(connectTimeout, readTimeout, writeTimeout);
        } else {
          connectSocket(connectTimeout, readTimeout);
        }
        establishProtocol(connectionSpecSelector);
        break;
      } catch (IOException e) {
        Utilaq.closeQuietly(socket);
        Utilaq.closeQuietly(rawSocket);
        socket = null;
        rawSocket = null;
        source = null;
        sink = null;
        handshake = null;
        protocol = null;
        http2Connection = null;

        if (routeException == null) {
          routeException = new RouteException(e);
        } else {
          routeException.addConnectException(e);
        }

        if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(e)) {
          throw routeException;
        }
      }
    }

    if (http2Connection != null) {
      synchronized (connectionPool) {
        allocationLimit = http2Connection.maxConcurrentStreams();
      }
    }
  }

  
  private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout)
      throws IOException {
    Requestza tunnelRequest = createTunnelRequest();
    HttpUrlza url = tunnelRequest.url();
    int attemptedConnections = 0;
    int maxAttempts = 21;
    while (true) {
      if (++attemptedConnections > maxAttempts) {
        throw new ProtocolException("Too many tunnel connections attempted: " + maxAttempts);
      }

      connectSocket(connectTimeout, readTimeout);
      tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);

      if (tunnelRequest == null) break; 

      
      
      Utilaq.closeQuietly(rawSocket);
      rawSocket = null;
      sink = null;
      source = null;
    }
  }

  
  private void connectSocket(int connectTimeout, int readTimeout) throws IOException {
    Proxy proxy = route.proxy();
    Addressq address = route.address();

    rawSocket = proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP
        ? address.socketFactory().createSocket()
        : new Socket(proxy);

    rawSocket.setSoTimeout(readTimeout);
    try {
      Platformq.get().connectSocket(rawSocket, route.socketAddress(), connectTimeout);
    } catch (ConnectException e) {
      ConnectException ce = new ConnectException("Failed to connect to " + route.socketAddress());
      ce.initCause(e);
      throw ce;
    }

    
    
    
    
    try {
      source = Okiozaq.buffer(Okiozaq.source(rawSocket));
      sink = Okiozaq.buffer(Okiozaq.sink(rawSocket));
    } catch (NullPointerException npe) {
      if (NPE_THROW_WITH_NULL.equals(npe.getMessage())) {
        throw new IOException(npe);
      }
    }
  }

  private void establishProtocol(ConnectionSpecSelector connectionSpecSelector) throws IOException {
    if (route.address().sslSocketFactory() == null) {
      protocol = Protocolza.HTTP_1_1;
      socket = rawSocket;
      return;
    }

    connectTls(connectionSpecSelector);

    if (protocol == Protocolza.HTTP_2) {
      socket.setSoTimeout(0); 
      http2Connection = new Http2Connectiona.Builder(true)
          .socket(socket, route.address().url().host(), source, sink)
          .listener(this)
          .build();
      http2Connection.start();
    }
  }

  private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
    Addressq address = route.address();
    SSLSocketFactory sslSocketFactory = address.sslSocketFactory();
    boolean success = false;
    SSLSocket sslSocket = null;
    try {
      
      sslSocket = (SSLSocket) sslSocketFactory.createSocket(
          rawSocket, address.url().host(), address.url().port(), true );

      
      ConnectionSpecza connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
      if (connectionSpec.supportsTlsExtensions()) {
        Platformq.get().configureTlsExtensions(
            sslSocket, address.url().host(), address.protocols());
      }

      
      sslSocket.startHandshake();
      Handshakeza unverifiedHandshake = Handshakeza.get(sslSocket.getSession());

      
      if (!address.hostnameVerifier().verify(address.url().host(), sslSocket.getSession())) {
        X509Certificate cert = (X509Certificate) unverifiedHandshake.peerCertificates().get(0);
        throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:"
            + "\n    certificate: " + CertificatePinnerza.pin(cert)
            + "\n    DN: " + cert.getSubjectDN().getName()
            + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
      }

      
      address.certificatePinner().check(address.url().host(),
          unverifiedHandshake.peerCertificates());

      
      String maybeProtocol = connectionSpec.supportsTlsExtensions()
          ? Platformq.get().getSelectedProtocol(sslSocket)
          : null;
      socket = sslSocket;
      source = Okiozaq.buffer(Okiozaq.source(socket));
      sink = Okiozaq.buffer(Okiozaq.sink(socket));
      handshake = unverifiedHandshake;
      protocol = maybeProtocol != null
          ? Protocolza.get(maybeProtocol)
          : Protocolza.HTTP_1_1;
      success = true;
    } catch (AssertionError e) {
      if (Utilaq.isAndroidGetsocknameError(e)) throw new IOException(e);
      throw e;
    } finally {
      if (sslSocket != null) {
        Platformq.get().afterHandshake(sslSocket);
      }
      if (!success) {
        Utilaq.closeQuietly(sslSocket);
      }
    }
  }

  
  private Requestza createTunnel(int readTimeout, int writeTimeout, Requestza tunnelRequest,
                                 HttpUrlza url) throws IOException {
    
    String requestLine = "CONNECT " + Utilaq.hostHeader(url, true) + " HTTP/1.1";
    while (true) {
      Http1Codec tunnelConnection = new Http1Codec(null, null, source, sink);
      source.timeout().timeout(readTimeout, MILLISECONDS);
      sink.timeout().timeout(writeTimeout, MILLISECONDS);
      tunnelConnection.writeRequest(tunnelRequest.headers(), requestLine);
      tunnelConnection.finishRequest();
      Responseza response = tunnelConnection.readResponseHeaders(false)
          .request(tunnelRequest)
          .build();
      
      
      long contentLength = HttpHeaders.contentLength(response);
      if (contentLength == -1L) {
        contentLength = 0L;
      }
      Sourcezaq body = tunnelConnection.newFixedLengthSource(contentLength);
      Utilaq.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
      body.close();

      switch (response.code()) {
        case HTTP_OK:
          
          
          
          
          if (!source.buffer().exhausted() || !sink.buffer().exhausted()) {
            throw new IOException("TLS tunnel buffered too many bytes!");
          }
          return null;

        case HTTP_PROXY_AUTH:
          tunnelRequest = route.address().proxyAuthenticator().authenticate(route, response);
          if (tunnelRequest == null) throw new IOException("Failed to authenticate with proxy");

          if ("close".equalsIgnoreCase(response.header("Connection"))) {
            return tunnelRequest;
          }
          break;

        default:
          throw new IOException(
              "Unexpected response code for CONNECT: " + response.code());
      }
    }
  }

  
  private Requestza createTunnelRequest() {
    return new Requestza.Builder()
        .url(route.address().url())
        .header("Host", Utilaq.hostHeader(route.address().url(), true))
        .header("Proxy-Connection", "Keep-Alive") 
        .header("User-Agent", Versionaq.userAgent())
        .build();
  }

  
  public boolean isEligible(Addressq address, @Nullableq Routeza route) {
    
    if (allocations.size() >= allocationLimit || noNewStreams) return false;

    
    if (!Internalq.instance.equalsNonHost(this.route.address(), address)) return false;

    
    if (address.url().host().equals(this.route().address().url().host())) {
      return true; 
    }

    
    
    
    

    
    if (http2Connection == null) return false;

    
    
    
    if (route == null) return false;
    if (route.proxy().type() != Proxy.Type.DIRECT) return false;
    if (this.route.proxy().type() != Proxy.Type.DIRECT) return false;
    if (!this.route.socketAddress().equals(route.socketAddress())) return false;

    
    if (route.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE) return false;
    if (!supportsUrl(address.url())) return false;

    
    try {
      address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
    } catch (SSLPeerUnverifiedException e) {
      return false;
    }

    return true; 
  }

  public boolean supportsUrl(HttpUrlza url) {
    if (url.port() != route.address().url().port()) {
      return false; 
    }

    if (!url.host().equals(route.address().url().host())) {
      
      return handshake != null && OkHostnameVerifier.INSTANCE.verify(
          url.host(), (X509Certificate) handshake.peerCertificates().get(0));
    }

    return true; 
  }

  public HttpCodec newCodec(
          OkHttpClientza client, StreamAllocation streamAllocation) throws SocketException {
    if (http2Connection != null) {
      return new Http2Codeca(client, streamAllocation, http2Connection);
    } else {
      socket.setSoTimeout(client.readTimeoutMillis());
      source.timeout().timeout(client.readTimeoutMillis(), MILLISECONDS);
      sink.timeout().timeout(client.writeTimeoutMillis(), MILLISECONDS);
      return new Http1Codec(client, streamAllocation, source, sink);
    }
  }

  public RealWebSocket.Streams newWebSocketStreams(final StreamAllocation streamAllocation) {
    return new RealWebSocket.Streams(true, source, sink) {
      @Override public void close() throws IOException {
        streamAllocation.streamFinished(true, streamAllocation.codec());
      }
    };
  }

  @Override public Routeza route() {
    return route;
  }

  public void cancel() {
    
    Utilaq.closeQuietly(rawSocket);
  }

  @Override public Socket socket() {
    return socket;
  }

  
  public boolean isHealthy(boolean doExtensiveChecks) {
    if (socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
      return false;
    }

    if (http2Connection != null) {
      return !http2Connection.isShutdown();
    }

    if (doExtensiveChecks) {
      try {
        int readTimeout = socket.getSoTimeout();
        try {
          socket.setSoTimeout(1);
          if (source.exhausted()) {
            return false; 
          }
          return true;
        } finally {
          socket.setSoTimeout(readTimeout);
        }
      } catch (SocketTimeoutException ignored) {
        
      } catch (IOException e) {
        return false; 
      }
    }

    return true;
  }

  
  @Override public void onStream(Http2Streama stream) throws IOException {
    stream.close(ErrorCodeq.REFUSED_STREAM);
  }

  
  @Override public void onSettings(Http2Connectiona connection) {
    synchronized (connectionPool) {
      allocationLimit = connection.maxConcurrentStreams();
    }
  }

  @Override public Handshakeza handshake() {
    return handshake;
  }

  
  public boolean isMultiplexed() {
    return http2Connection != null;
  }

  @Override public Protocolza protocol() {
    return protocol;
  }

  @Override public String toString() {
    return "Connection{"
        + route.address().url().host() + ":" + route.address().url().port()
        + ", proxy="
        + route.proxy()
        + " hostAddress="
        + route.socketAddress()
        + " cipherSuite="
        + (handshake != null ? handshake.cipherSuite() : "none")
        + " protocol="
        + protocol
        + '}';
  }
}
