
package com.xxx.zzz.aall.okhttp3ll.internalss.cachenn;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import java.io.IOException;

import com.xxx.zzz.aall.okhttp3ll.Interceptorza;
import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpCodec;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpHeaders;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpMethod;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RealResponseBody;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;
import com.xxx.zzz.aall.okioss.Timeoutzaq;


public final class CacheInterceptorq implements Interceptorza {
  final InternalCacheq cache;

  public CacheInterceptorq(InternalCacheq cache) {
    this.cache = cache;
  }

  @Override public Responseza intercept(Chain chain) throws IOException {
    Responseza cacheCandidate = cache != null
        ? cache.get(chain.request())
        : null;

    long now = System.currentTimeMillis();

    CacheStrategyq strategy = new CacheStrategyq.Factory(now, chain.request(), cacheCandidate).get();
    Requestza networkRequest = strategy.networkRequest;
    Responseza cacheResponse = strategy.cacheResponse;

    if (cache != null) {
      cache.trackResponse(strategy);
    }

    if (cacheCandidate != null && cacheResponse == null) {
      Utilaq.closeQuietly(cacheCandidate.body()); 
    }

    
    if (networkRequest == null && cacheResponse == null) {
      return new Responseza.Builder()
          .request(chain.request())
          .protocol(Protocolza.HTTP_1_1)
          .code(504)
          .message("Unsatisfiable Request (only-if-cached)")
          .body(Utilaq.EMPTY_RESPONSE)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build();
    }

    
    if (networkRequest == null) {
      return cacheResponse.newBuilder()
          .cacheResponse(stripBody(cacheResponse))
          .build();
    }

    Responseza networkResponse = null;
    try {
      networkResponse = chain.proceed(networkRequest);
    } finally {
      
      if (networkResponse == null && cacheCandidate != null) {
        Utilaq.closeQuietly(cacheCandidate.body());
      }
    }

    
    if (cacheResponse != null) {
      if (networkResponse.code() == HTTP_NOT_MODIFIED) {
        Responseza response = cacheResponse.newBuilder()
            .headers(combine(cacheResponse.headers(), networkResponse.headers()))
            .sentRequestAtMillis(networkResponse.sentRequestAtMillis())
            .receivedResponseAtMillis(networkResponse.receivedResponseAtMillis())
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build();
        networkResponse.body().close();

        
        
        cache.trackConditionalCacheHit();
        cache.update(cacheResponse, response);
        return response;
      } else {
        Utilaq.closeQuietly(cacheResponse.body());
      }
    }

    Responseza response = networkResponse.newBuilder()
        .cacheResponse(stripBody(cacheResponse))
        .networkResponse(stripBody(networkResponse))
        .build();

    if (cache != null) {
      if (HttpHeaders.hasBody(response) && CacheStrategyq.isCacheable(response, networkRequest)) {
        
        CacheRequestq cacheRequest = cache.put(response);
        return cacheWritingResponse(cacheRequest, response);
      }

      if (HttpMethod.invalidatesCache(networkRequest.method())) {
        try {
          cache.remove(networkRequest);
        } catch (IOException ignored) {
          
        }
      }
    }

    return response;
  }

  private static Responseza stripBody(Responseza response) {
    return response != null && response.body() != null
        ? response.newBuilder().body(null).build()
        : response;
  }


  private Responseza cacheWritingResponse(final CacheRequestq cacheRequest, Responseza response)
      throws IOException {
    
    if (cacheRequest == null) return response;
    Sinkzaq cacheBodyUnbuffered = cacheRequest.body();
    if (cacheBodyUnbuffered == null) return response;

    final BufferedSourcezaqdfs source = response.body().source();
    final BufferedSinkzaqds cacheBody = Okiozaq.buffer(cacheBodyUnbuffered);

    Sourcezaq cacheWritingSource = new Sourcezaq() {
      boolean cacheRequestClosed;

      @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
        long bytesRead;
        try {
          bytesRead = source.read(sink, byteCount);
        } catch (IOException e) {
          if (!cacheRequestClosed) {
            cacheRequestClosed = true;
            cacheRequest.abort(); 
          }
          throw e;
        }

        if (bytesRead == -1) {
          if (!cacheRequestClosed) {
            cacheRequestClosed = true;
            cacheBody.close(); 
          }
          return -1;
        }

        sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
        cacheBody.emitCompleteSegments();
        return bytesRead;
      }

      @Override public Timeoutzaq timeout() {
        return source.timeout();
      }

      @Override public void close() throws IOException {
        if (!cacheRequestClosed
            && !Utilaq.discard(this, HttpCodec.DISCARD_STREAM_TIMEOUT_MILLIS, MILLISECONDS)) {
          cacheRequestClosed = true;
          cacheRequest.abort();
        }
        source.close();
      }
    };

    return response.newBuilder()
        .body(new RealResponseBody(response.headers(), Okiozaq.buffer(cacheWritingSource)))
        .build();
  }


  private static Headersza combine(Headersza cachedHeaders, Headersza networkHeaders) {
    Headersza.Builder result = new Headersza.Builder();

    for (int i = 0, size = cachedHeaders.size(); i < size; i++) {
      String fieldName = cachedHeaders.name(i);
      String value = cachedHeaders.value(i);
      if ("Warning".equalsIgnoreCase(fieldName) && value.startsWith("1")) {
        continue; 
      }
      if (!isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null) {
        Internalq.instance.addLenient(result, fieldName, value);
      }
    }

    for (int i = 0, size = networkHeaders.size(); i < size; i++) {
      String fieldName = networkHeaders.name(i);
      if ("Content-Length".equalsIgnoreCase(fieldName)) {
        continue; 
      }
      if (isEndToEnd(fieldName)) {
        Internalq.instance.addLenient(result, fieldName, networkHeaders.value(i));
      }
    }

    return result.build();
  }


  static boolean isEndToEnd(String fieldName) {
    return !"Connection".equalsIgnoreCase(fieldName)
        && !"Keep-Alive".equalsIgnoreCase(fieldName)
        && !"Proxy-Authenticate".equalsIgnoreCase(fieldName)
        && !"Proxy-Authorization".equalsIgnoreCase(fieldName)
        && !"TE".equalsIgnoreCase(fieldName)
        && !"Trailers".equalsIgnoreCase(fieldName)
        && !"Transfer-Encoding".equalsIgnoreCase(fieldName)
        && !"Upgrade".equalsIgnoreCase(fieldName);
  }
}
