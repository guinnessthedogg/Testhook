
package com.xxx.zzz.aall.okhttp3ll.internalss;

import com.xxx.zzz.aall.okhttp3ll.Addressq;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;

import com.xxx.zzz.aall.okhttp3ll.Callzadasd;
import com.xxx.zzz.aall.okhttp3ll.ConnectionPoolza;
import com.xxx.zzz.aall.okhttp3ll.ConnectionSpecza;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.Routeza;
import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.InternalCacheq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RealConnection;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RouteDatabase;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;


public abstract class Internalq {

  public static void initializeInstanceForTests() {
    
    new OkHttpClientza();
  }

  public static Internalq instance;

  public abstract void addLenient(Headersza.Builder builder, String line);

  public abstract void addLenient(Headersza.Builder builder, String name, String value);

  public abstract void setCache(OkHttpClientza.Builder builder, InternalCacheq internalCache);

  public abstract RealConnection get(ConnectionPoolza pool, Addressq address,
                                     StreamAllocation streamAllocation, Routeza route);

  public abstract boolean equalsNonHost(Addressq a, Addressq b);

  public abstract Socket deduplicate(
          ConnectionPoolza pool, Addressq address, StreamAllocation streamAllocation);

  public abstract void put(ConnectionPoolza pool, RealConnection connection);

  public abstract boolean connectionBecameIdle(ConnectionPoolza pool, RealConnection connection);

  public abstract RouteDatabase routeDatabase(ConnectionPoolza connectionPool);

  public abstract int code(Responseza.Builder responseBuilder);

  public abstract void apply(ConnectionSpecza tlsConfiguration, SSLSocket sslSocket,
                             boolean isFallback);

  public abstract HttpUrlza getHttpUrlChecked(String url)
      throws MalformedURLException, UnknownHostException;

  public abstract StreamAllocation streamAllocation(Callzadasd call);

  public abstract Callzadasd newWebSocketCall(OkHttpClientza client, Requestza request);
}
