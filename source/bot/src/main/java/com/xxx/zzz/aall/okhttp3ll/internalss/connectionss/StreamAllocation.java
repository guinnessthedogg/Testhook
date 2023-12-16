
package com.xxx.zzz.aall.okhttp3ll.internalss.connectionss;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import com.xxx.zzz.aall.okhttp3ll.Addressq;
import com.xxx.zzz.aall.okhttp3ll.ConnectionPoolza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Routeza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpCodec;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.ErrorCodeq;
import com.xxx.zzz.aall.okhttp3ll.internalss.http2.StreamResetExceptiona;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;

import com.xxx.zzz.aall.okhttp3ll.internalss.http2.ConnectionShutdownExceptionq;


public final class StreamAllocation {
  public final Addressq address;
  private Routeza route;
  private final ConnectionPoolza connectionPool;
  private final Object callStackTrace;

  
  private final RouteSelector routeSelector;
  private int refusedStreamCount;
  private RealConnection connection;
  private boolean released;
  private boolean canceled;
  private HttpCodec codec;

  public StreamAllocation(ConnectionPoolza connectionPool, Addressq address, Object callStackTrace) {
    this.connectionPool = connectionPool;
    this.address = address;
    this.routeSelector = new RouteSelector(address, routeDatabase());
    this.callStackTrace = callStackTrace;
  }

  public HttpCodec newStream(OkHttpClientza client, boolean doExtensiveHealthChecks) {
    int connectTimeout = client.connectTimeoutMillis();
    int readTimeout = client.readTimeoutMillis();
    int writeTimeout = client.writeTimeoutMillis();
    boolean connectionRetryEnabled = client.retryOnConnectionFailure();

    try {
      RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout,
          writeTimeout, connectionRetryEnabled, doExtensiveHealthChecks);
      HttpCodec resultCodec = resultConnection.newCodec(client, this);

      synchronized (connectionPool) {
        codec = resultCodec;
        return resultCodec;
      }
    } catch (IOException e) {
      throw new RouteException(e);
    }
  }

  
  private RealConnection findHealthyConnection(int connectTimeout, int readTimeout,
      int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks)
      throws IOException {
    while (true) {
      RealConnection candidate = findConnection(connectTimeout, readTimeout, writeTimeout,
          connectionRetryEnabled);

      
      synchronized (connectionPool) {
        if (candidate.successCount == 0) {
          return candidate;
        }
      }

      
      
      if (!candidate.isHealthy(doExtensiveHealthChecks)) {
        noNewStreams();
        continue;
      }

      return candidate;
    }
  }

  
  private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout,
      boolean connectionRetryEnabled) throws IOException {
    Routeza selectedRoute;
    synchronized (connectionPool) {
      if (released) throw new IllegalStateException("released");
      if (codec != null) throw new IllegalStateException("codec != null");
      if (canceled) throw new IOException("Canceled");

      
      RealConnection allocatedConnection = this.connection;
      if (allocatedConnection != null && !allocatedConnection.noNewStreams) {
        return allocatedConnection;
      }

      
      Internalq.instance.get(connectionPool, address, this, null);
      if (connection != null) {
        return connection;
      }

      selectedRoute = route;
    }

    
    if (selectedRoute == null) {
      selectedRoute = routeSelector.next();
    }

    RealConnection result;
    synchronized (connectionPool) {
      if (canceled) throw new IOException("Canceled");

      
      
      Internalq.instance.get(connectionPool, address, this, selectedRoute);
      if (connection != null) {
        route = selectedRoute;
        return connection;
      }

      
      
      route = selectedRoute;
      refusedStreamCount = 0;
      result = new RealConnection(connectionPool, selectedRoute);
      acquire(result);
    }

    
    result.connect(connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled);
    routeDatabase().connected(result.route());

    Socket socket = null;
    synchronized (connectionPool) {
      
      Internalq.instance.put(connectionPool, result);

      
      
      if (result.isMultiplexed()) {
        socket = Internalq.instance.deduplicate(connectionPool, address, this);
        result = connection;
      }
    }
    Utilaq.closeQuietly(socket);

    return result;
  }

  public void streamFinished(boolean noNewStreams, HttpCodec codec) {
    Socket socket;
    synchronized (connectionPool) {
      if (codec == null || codec != this.codec) {
        throw new IllegalStateException("expected " + this.codec + " but was " + codec);
      }
      if (!noNewStreams) {
        connection.successCount++;
      }
      socket = deallocate(noNewStreams, false, true);
    }
    Utilaq.closeQuietly(socket);
  }

  public HttpCodec codec() {
    synchronized (connectionPool) {
      return codec;
    }
  }

  private RouteDatabase routeDatabase() {
    return Internalq.instance.routeDatabase(connectionPool);
  }

  public synchronized RealConnection connection() {
    return connection;
  }

  public void release() {
    Socket socket;
    synchronized (connectionPool) {
      socket = deallocate(false, true, false);
    }
    Utilaq.closeQuietly(socket);
  }

  
  public void noNewStreams() {
    Socket socket;
    synchronized (connectionPool) {
      socket = deallocate(true, false, false);
    }
    Utilaq.closeQuietly(socket);
  }

  
  private Socket deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
    assert (Thread.holdsLock(connectionPool));

    if (streamFinished) {
      this.codec = null;
    }
    if (released) {
      this.released = true;
    }
    Socket socket = null;
    if (connection != null) {
      if (noNewStreams) {
        connection.noNewStreams = true;
      }
      if (this.codec == null && (this.released || connection.noNewStreams)) {
        release(connection);
        if (connection.allocations.isEmpty()) {
          connection.idleAtNanos = System.nanoTime();
          if (Internalq.instance.connectionBecameIdle(connectionPool, connection)) {
            socket = connection.socket();
          }
        }
        connection = null;
      }
    }
    return socket;
  }

  public void cancel() {
    HttpCodec codecToCancel;
    RealConnection connectionToCancel;
    synchronized (connectionPool) {
      canceled = true;
      codecToCancel = codec;
      connectionToCancel = connection;
    }
    if (codecToCancel != null) {
      codecToCancel.cancel();
    } else if (connectionToCancel != null) {
      connectionToCancel.cancel();
    }
  }

  public void streamFailed(IOException e) {
    Socket socket;
    boolean noNewStreams = false;

    synchronized (connectionPool) {
      if (e instanceof StreamResetExceptiona) {
        StreamResetExceptiona streamResetException = (StreamResetExceptiona) e;
        if (streamResetException.errorCode == ErrorCodeq.REFUSED_STREAM) {
          refusedStreamCount++;
        }
        
        
        if (streamResetException.errorCode != ErrorCodeq.REFUSED_STREAM || refusedStreamCount > 1) {
          noNewStreams = true;
          route = null;
        }
      } else if (connection != null
          && (!connection.isMultiplexed() || e instanceof ConnectionShutdownExceptionq)) {
        noNewStreams = true;

        
        if (connection.successCount == 0) {
          if (route != null && e != null) {
            routeSelector.connectFailed(route, e);
          }
          route = null;
        }
      }
      socket = deallocate(noNewStreams, false, true);
    }

    Utilaq.closeQuietly(socket);
  }

  
  public void acquire(RealConnection connection) {
    assert (Thread.holdsLock(connectionPool));
    if (this.connection != null) throw new IllegalStateException();

    this.connection = connection;
    connection.allocations.add(new StreamAllocationReference(this, callStackTrace));
  }

  
  private void release(RealConnection connection) {
    for (int i = 0, size = connection.allocations.size(); i < size; i++) {
      Reference<StreamAllocation> reference = connection.allocations.get(i);
      if (reference.get() == this) {
        connection.allocations.remove(i);
        return;
      }
    }
    throw new IllegalStateException();
  }

  
  public Socket releaseAndAcquire(RealConnection newConnection) {
    assert (Thread.holdsLock(connectionPool));
    if (codec != null || connection.allocations.size() != 1) throw new IllegalStateException();

    
    Reference<StreamAllocation> onlyAllocation = connection.allocations.get(0);
    Socket socket = deallocate(true, false, false);

    
    this.connection = newConnection;
    newConnection.allocations.add(onlyAllocation);

    return socket;
  }

  public boolean hasMoreRoutes() {
    return route != null || routeSelector.hasNext();
  }

  @Override public String toString() {
    RealConnection connection = connection();
    return connection != null ? connection.toString() : address.toString();
  }

  public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
    
    public final Object callStackTrace;

    StreamAllocationReference(StreamAllocation referent, Object callStackTrace) {
      super(referent);
      this.callStackTrace = callStackTrace;
    }
  }
}
