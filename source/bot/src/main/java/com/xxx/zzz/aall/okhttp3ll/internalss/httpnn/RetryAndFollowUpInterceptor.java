
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_PROXY_AUTH;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;
import static com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine.HTTP_PERM_REDIRECT;
import static com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine.HTTP_TEMP_REDIRECT;

import com.xxx.zzz.aall.okhttp3ll.Addressq;
import com.xxx.zzz.aall.okhttp3ll.CertificatePinnerza;
import com.xxx.zzz.aall.okhttp3ll.Connectionza;
import com.xxx.zzz.aall.okhttp3ll.Interceptorza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Routeza;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpRetryException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;

import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.RequestBodyza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RouteException;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.ConnectionShutdownExceptionq;


public final class RetryAndFollowUpInterceptor implements Interceptorza {

  private static final int MAX_FOLLOW_UPS = 20;

  private final OkHttpClientza client;
  private final boolean forWebSocket;
  private StreamAllocation streamAllocation;
  private Object callStackTrace;
  private volatile boolean canceled;

  public RetryAndFollowUpInterceptor(OkHttpClientza client, boolean forWebSocket) {
    this.client = client;
    this.forWebSocket = forWebSocket;
  }


  public void cancel() {
    canceled = true;
    StreamAllocation streamAllocation = this.streamAllocation;
    if (streamAllocation != null) streamAllocation.cancel();
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void setCallStackTrace(Object callStackTrace) {
    this.callStackTrace = callStackTrace;
  }

  public StreamAllocation streamAllocation() {
    return streamAllocation;
  }

  @Override public Responseza intercept(Chain chain) throws IOException {
    Requestza request = chain.request();

    streamAllocation = new StreamAllocation(
        client.connectionPool(), createAddress(request.url()), callStackTrace);

    int followUpCount = 0;
    Responseza priorResponse = null;
    while (true) {
      if (canceled) {
        streamAllocation.release();
        throw new IOException("Canceled");
      }

      Responseza response = null;
      boolean releaseConnection = true;
      try {
        response = ((RealInterceptorChain) chain).proceed(request, streamAllocation, null, null);
        releaseConnection = false;
      } catch (RouteException e) {
        
        if (!recover(e.getLastConnectException(), false, request)) {
          throw e.getLastConnectException();
        }
        releaseConnection = false;
        continue;
      } catch (IOException e) {
        
        boolean requestSendStarted = !(e instanceof ConnectionShutdownExceptionq);
        if (!recover(e, requestSendStarted, request)) throw e;
        releaseConnection = false;
        continue;
      } finally {
        
        if (releaseConnection) {
          streamAllocation.streamFailed(null);
          streamAllocation.release();
        }
      }

      
      if (priorResponse != null) {
        response = response.newBuilder()
            .priorResponse(priorResponse.newBuilder()
                    .body(null)
                    .build())
            .build();
      }

      Requestza followUp = followUpRequest(response);

      if (followUp == null) {
        if (!forWebSocket) {
          streamAllocation.release();
        }
        return response;
      }

      closeQuietly(response.body());

      if (++followUpCount > MAX_FOLLOW_UPS) {
        streamAllocation.release();
        throw new ProtocolException("Too many follow-up requests: " + followUpCount);
      }

      if (followUp.body() instanceof UnrepeatableRequestBody) {
        streamAllocation.release();
        throw new HttpRetryException("Cannot retry streamed HTTP body", response.code());
      }

      if (!sameConnection(response, followUp.url())) {
        streamAllocation.release();
        streamAllocation = new StreamAllocation(
            client.connectionPool(), createAddress(followUp.url()), callStackTrace);
      } else if (streamAllocation.codec() != null) {
        throw new IllegalStateException("Closing the body of " + response
            + " didn't close its backing stream. Bad interceptor?");
      }

      request = followUp;
      priorResponse = response;
    }
  }

  private Addressq createAddress(HttpUrlza url) {
    SSLSocketFactory sslSocketFactory = null;
    HostnameVerifier hostnameVerifier = null;
    CertificatePinnerza certificatePinner = null;
    if (url.isHttps()) {
      sslSocketFactory = client.sslSocketFactory();
      hostnameVerifier = client.hostnameVerifier();
      certificatePinner = client.certificatePinner();
    }

    return new Addressq(url.host(), url.port(), client.dns(), client.socketFactory(),
        sslSocketFactory, hostnameVerifier, certificatePinner, client.proxyAuthenticator(),
        client.proxy(), client.protocols(), client.connectionSpecs(), client.proxySelector());
  }


  private boolean recover(IOException e, boolean requestSendStarted, Requestza userRequest) {
    streamAllocation.streamFailed(e);

    
    if (!client.retryOnConnectionFailure()) return false;

    
    if (requestSendStarted && userRequest.body() instanceof UnrepeatableRequestBody) return false;

    
    if (!isRecoverable(e, requestSendStarted)) return false;

    
    if (!streamAllocation.hasMoreRoutes()) return false;

    
    return true;
  }

  private boolean isRecoverable(IOException e, boolean requestSendStarted) {
    
    if (e instanceof ProtocolException) {
      return false;
    }

    
    
    if (e instanceof InterruptedIOException) {
      return e instanceof SocketTimeoutException && !requestSendStarted;
    }

    
    
    if (e instanceof SSLHandshakeException) {
      
      
      if (e.getCause() instanceof CertificateException) {
        return false;
      }
    }
    if (e instanceof SSLPeerUnverifiedException) {
      
      return false;
    }

    
    
    
    return true;
  }


  private Requestza followUpRequest(Responseza userResponse) throws IOException {
    if (userResponse == null) throw new IllegalStateException();
    Connectionza connection = streamAllocation.connection();
    Routeza route = connection != null
        ? connection.route()
        : null;
    int responseCode = userResponse.code();

    final String method = userResponse.request().method();
    switch (responseCode) {
      case HTTP_PROXY_AUTH:
        Proxy selectedProxy = route != null
            ? route.proxy()
            : client.proxy();
        if (selectedProxy.type() != Proxy.Type.HTTP) {
          throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
        }
        return client.proxyAuthenticator().authenticate(route, userResponse);

      case HTTP_UNAUTHORIZED:
        return client.authenticator().authenticate(route, userResponse);

      case HTTP_PERM_REDIRECT:
      case HTTP_TEMP_REDIRECT:
        
        
        if (!method.equals("GET") && !method.equals("HEAD")) {
          return null;
        }
        
      case HTTP_MULT_CHOICE:
      case HTTP_MOVED_PERM:
      case HTTP_MOVED_TEMP:
      case HTTP_SEE_OTHER:
        
        if (!client.followRedirects()) return null;

        String location = userResponse.header("Location");
        if (location == null) return null;
        HttpUrlza url = userResponse.request().url().resolve(location);

        
        if (url == null) return null;

        
        boolean sameScheme = url.scheme().equals(userResponse.request().url().scheme());
        if (!sameScheme && !client.followSslRedirects()) return null;

        
        Requestza.Builder requestBuilder = userResponse.request().newBuilder();
        if (HttpMethod.permitsRequestBody(method)) {
          final boolean maintainBody = HttpMethod.redirectsWithBody(method);
          if (HttpMethod.redirectsToGet(method)) {
            requestBuilder.method("GET", null);
          } else {
            RequestBodyza requestBody = maintainBody ? userResponse.request().body() : null;
            requestBuilder.method(method, requestBody);
          }
          if (!maintainBody) {
            requestBuilder.removeHeader("Transfer-Encoding");
            requestBuilder.removeHeader("Content-Length");
            requestBuilder.removeHeader("Content-Type");
          }
        }

        
        
        
        if (!sameConnection(userResponse, url)) {
          requestBuilder.removeHeader("Authorization");
        }

        return requestBuilder.url(url).build();

      case HTTP_CLIENT_TIMEOUT:
        
        
        
        if (userResponse.request().body() instanceof UnrepeatableRequestBody) {
          return null;
        }

        return userResponse.request();

      default:
        return null;
    }
  }


  private boolean sameConnection(Responseza response, HttpUrlza followUp) {
    HttpUrlza url = response.request().url();
    return url.host().equals(followUp.host())
        && url.port() == followUp.port()
        && url.scheme().equals(followUp.scheme());
  }
}
