
package com.xxx.zzz.aall.okhttp3ll.internalss.cachenn;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_GONE;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_NOT_AUTHORITATIVE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_REQ_TOO_LONG;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okhttp3ll.CacheControlz;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpDate;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpHeaders;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine;


public final class CacheStrategyq {

  public final @Nullableq
  Requestza networkRequest;


  public final @Nullableq
  Responseza cacheResponse;

  CacheStrategyq(Requestza networkRequest, Responseza cacheResponse) {
    this.networkRequest = networkRequest;
    this.cacheResponse = cacheResponse;
  }


  public static boolean isCacheable(Responseza response, Requestza request) {
    
    
    switch (response.code()) {
      case HTTP_OK:
      case HTTP_NOT_AUTHORITATIVE:
      case HTTP_NO_CONTENT:
      case HTTP_MULT_CHOICE:
      case HTTP_MOVED_PERM:
      case HTTP_NOT_FOUND:
      case HTTP_BAD_METHOD:
      case HTTP_GONE:
      case HTTP_REQ_TOO_LONG:
      case HTTP_NOT_IMPLEMENTED:
      case StatusLine.HTTP_PERM_REDIRECT:
        
        break;

      case HTTP_MOVED_TEMP:
      case StatusLine.HTTP_TEMP_REDIRECT:
        
        
        
        if (response.header("Expires") != null
            || response.cacheControl().maxAgeSeconds() != -1
            || response.cacheControl().isPublic()
            || response.cacheControl().isPrivate()) {
          break;
        }
        

      default:
        
        return false;
    }

    
    return !response.cacheControl().noStore() && !request.cacheControl().noStore();
  }

  public static class Factory {
    final long nowMillis;
    final Requestza request;
    final Responseza cacheResponse;


    private Date servedDate;
    private String servedDateString;


    private Date lastModified;
    private String lastModifiedString;


    private Date expires;


    private long sentRequestMillis;


    private long receivedResponseMillis;


    private String etag;


    private int ageSeconds = -1;

    public Factory(long nowMillis, Requestza request, Responseza cacheResponse) {
      this.nowMillis = nowMillis;
      this.request = request;
      this.cacheResponse = cacheResponse;

      if (cacheResponse != null) {
        this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
        this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
        Headersza headers = cacheResponse.headers();
        for (int i = 0, size = headers.size(); i < size; i++) {
          String fieldName = headers.name(i);
          String value = headers.value(i);
          if ("Date".equalsIgnoreCase(fieldName)) {
            servedDate = HttpDate.parse(value);
            servedDateString = value;
          } else if ("Expires".equalsIgnoreCase(fieldName)) {
            expires = HttpDate.parse(value);
          } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
            lastModified = HttpDate.parse(value);
            lastModifiedString = value;
          } else if ("ETag".equalsIgnoreCase(fieldName)) {
            etag = value;
          } else if ("Age".equalsIgnoreCase(fieldName)) {
            ageSeconds = HttpHeaders.parseSeconds(value, -1);
          }
        }
      }
    }


    public CacheStrategyq get() {
      CacheStrategyq candidate = getCandidate();

      if (candidate.networkRequest != null && request.cacheControl().onlyIfCached()) {
        
        return new CacheStrategyq(null, null);
      }

      return candidate;
    }


    private CacheStrategyq getCandidate() {
      
      if (cacheResponse == null) {
        return new CacheStrategyq(request, null);
      }

      
      if (request.isHttps() && cacheResponse.handshake() == null) {
        return new CacheStrategyq(request, null);
      }

      
      
      
      if (!isCacheable(cacheResponse, request)) {
        return new CacheStrategyq(request, null);
      }

      CacheControlz requestCaching = request.cacheControl();
      if (requestCaching.noCache() || hasConditions(request)) {
        return new CacheStrategyq(request, null);
      }

      long ageMillis = cacheResponseAge();
      long freshMillis = computeFreshnessLifetime();

      if (requestCaching.maxAgeSeconds() != -1) {
        freshMillis = Math.min(freshMillis, SECONDS.toMillis(requestCaching.maxAgeSeconds()));
      }

      long minFreshMillis = 0;
      if (requestCaching.minFreshSeconds() != -1) {
        minFreshMillis = SECONDS.toMillis(requestCaching.minFreshSeconds());
      }

      long maxStaleMillis = 0;
      CacheControlz responseCaching = cacheResponse.cacheControl();
      if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
        maxStaleMillis = SECONDS.toMillis(requestCaching.maxStaleSeconds());
      }

      if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
        Responseza.Builder builder = cacheResponse.newBuilder();
        if (ageMillis + minFreshMillis >= freshMillis) {
          builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
        }
        long oneDayMillis = 24 * 60 * 60 * 1000L;
        if (ageMillis > oneDayMillis && isFreshnessLifetimeHeuristic()) {
          builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
        }
        return new CacheStrategyq(null, builder.build());
      }

      
      
      String conditionName;
      String conditionValue;
      if (etag != null) {
        conditionName = "If-None-Match";
        conditionValue = etag;
      } else if (lastModified != null) {
        conditionName = "If-Modified-Since";
        conditionValue = lastModifiedString;
      } else if (servedDate != null) {
        conditionName = "If-Modified-Since";
        conditionValue = servedDateString;
      } else {
        return new CacheStrategyq(request, null); 
      }

      Headersza.Builder conditionalRequestHeaders = request.headers().newBuilder();
      Internalq.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);

      Requestza conditionalRequest = request.newBuilder()
          .headers(conditionalRequestHeaders.build())
          .build();
      return new CacheStrategyq(conditionalRequest, cacheResponse);
    }


    private long computeFreshnessLifetime() {
      CacheControlz responseCaching = cacheResponse.cacheControl();
      if (responseCaching.maxAgeSeconds() != -1) {
        return SECONDS.toMillis(responseCaching.maxAgeSeconds());
      } else if (expires != null) {
        long servedMillis = servedDate != null
            ? servedDate.getTime()
            : receivedResponseMillis;
        long delta = expires.getTime() - servedMillis;
        return delta > 0 ? delta : 0;
      } else if (lastModified != null
          && cacheResponse.request().url().query() == null) {
        
        
        
        
        long servedMillis = servedDate != null
            ? servedDate.getTime()
            : sentRequestMillis;
        long delta = servedMillis - lastModified.getTime();
        return delta > 0 ? (delta / 10) : 0;
      }
      return 0;
    }


    private long cacheResponseAge() {
      long apparentReceivedAge = servedDate != null
          ? Math.max(0, receivedResponseMillis - servedDate.getTime())
          : 0;
      long receivedAge = ageSeconds != -1
          ? Math.max(apparentReceivedAge, SECONDS.toMillis(ageSeconds))
          : apparentReceivedAge;
      long responseDuration = receivedResponseMillis - sentRequestMillis;
      long residentDuration = nowMillis - receivedResponseMillis;
      return receivedAge + responseDuration + residentDuration;
    }


    private boolean isFreshnessLifetimeHeuristic() {
      return cacheResponse.cacheControl().maxAgeSeconds() == -1 && expires == null;
    }


    private static boolean hasConditions(Requestza request) {
      return request.header("If-Modified-Since") != null || request.header("If-None-Match") != null;
    }
  }
}
