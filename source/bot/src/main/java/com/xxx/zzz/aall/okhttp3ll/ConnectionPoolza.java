
package com.xxx.zzz.aall.okhttp3ll;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RealConnection;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;

import java.lang.ref.Reference;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RouteDatabase;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;


public final class ConnectionPoolza {

  private static final Executor executor = new ThreadPoolExecutor(0 ,
      Integer.MAX_VALUE , 60L , TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>(), Utilaq.threadFactory("OkHttp ConnectionPool", true));


  private final int maxIdleConnections;
  private final long keepAliveDurationNs;
  private final Runnable cleanupRunnable = new Runnable() {
    @Override public void run() {
      while (true) {
        long waitNanos = cleanup(System.nanoTime());
        if (waitNanos == -1) return;
        if (waitNanos > 0) {
          long waitMillis = waitNanos / 1000000L;
          waitNanos -= (waitMillis * 1000000L);
          synchronized (ConnectionPoolza.this) {
            try {
              ConnectionPoolza.this.wait(waitMillis, (int) waitNanos);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }
    }
  };

  private final Deque<RealConnection> connections = new ArrayDeque<>();
  final RouteDatabase routeDatabase = new RouteDatabase();
  boolean cleanupRunning;


  public ConnectionPoolza() {
    this(5, 5, TimeUnit.MINUTES);
  }

  public ConnectionPoolza(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
    this.maxIdleConnections = maxIdleConnections;
    this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);

    
    if (keepAliveDuration <= 0) {
      throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
    }
  }


  public synchronized int idleConnectionCount() {
    int total = 0;
    for (RealConnection connection : connections) {
      if (connection.allocations.isEmpty()) total++;
    }
    return total;
  }


  public synchronized int connectionCount() {
    return connections.size();
  }


  @Nullableq
  RealConnection get(Addressq address, StreamAllocation streamAllocation, Routeza route) {
    assert (Thread.holdsLock(this));
    for (RealConnection connection : connections) {
      if (connection.isEligible(address, route)) {
        streamAllocation.acquire(connection);
        return connection;
      }
    }
    return null;
  }


  @Nullableq
  Socket deduplicate(Addressq address, StreamAllocation streamAllocation) {
    assert (Thread.holdsLock(this));
    for (RealConnection connection : connections) {
      if (connection.isEligible(address, null)
          && connection.isMultiplexed()
          && connection != streamAllocation.connection()) {
        return streamAllocation.releaseAndAcquire(connection);
      }
    }
    return null;
  }

  void put(RealConnection connection) {
    assert (Thread.holdsLock(this));
    if (!cleanupRunning) {
      cleanupRunning = true;
      executor.execute(cleanupRunnable);
    }
    connections.add(connection);
  }


  boolean connectionBecameIdle(RealConnection connection) {
    assert (Thread.holdsLock(this));
    if (connection.noNewStreams || maxIdleConnections == 0) {
      connections.remove(connection);
      return true;
    } else {
      notifyAll(); 
      return false;
    }
  }


  public void evictAll() {
    List<RealConnection> evictedConnections = new ArrayList<>();
    synchronized (this) {
      for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
        RealConnection connection = i.next();
        if (connection.allocations.isEmpty()) {
          connection.noNewStreams = true;
          evictedConnections.add(connection);
          i.remove();
        }
      }
    }

    for (RealConnection connection : evictedConnections) {
      Utilaq.closeQuietly(connection.socket());
    }
  }


  long cleanup(long now) {
    int inUseConnectionCount = 0;
    int idleConnectionCount = 0;
    RealConnection longestIdleConnection = null;
    long longestIdleDurationNs = Long.MIN_VALUE;

    
    synchronized (this) {
      for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
        RealConnection connection = i.next();

        
        if (pruneAndGetAllocationCount(connection, now) > 0) {
          inUseConnectionCount++;
          continue;
        }

        idleConnectionCount++;

        
        long idleDurationNs = now - connection.idleAtNanos;
        if (idleDurationNs > longestIdleDurationNs) {
          longestIdleDurationNs = idleDurationNs;
          longestIdleConnection = connection;
        }
      }

      if (longestIdleDurationNs >= this.keepAliveDurationNs
          || idleConnectionCount > this.maxIdleConnections) {
        
        
        connections.remove(longestIdleConnection);
      } else if (idleConnectionCount > 0) {
        
        return keepAliveDurationNs - longestIdleDurationNs;
      } else if (inUseConnectionCount > 0) {
        
        return keepAliveDurationNs;
      } else {
        
        cleanupRunning = false;
        return -1;
      }
    }

    Utilaq.closeQuietly(longestIdleConnection.socket());

    
    return 0;
  }


  private int pruneAndGetAllocationCount(RealConnection connection, long now) {
    List<Reference<StreamAllocation>> references = connection.allocations;
    for (int i = 0; i < references.size(); ) {
      Reference<StreamAllocation> reference = references.get(i);

      if (reference.get() != null) {
        i++;
        continue;
      }

      
      StreamAllocation.StreamAllocationReference streamAllocRef =
          (StreamAllocation.StreamAllocationReference) reference;
      String message = "A connection to " + connection.route().address().url()
          + " was leaked. Did you forget to close a response body?";
      Platformq.get().logCloseableLeak(message, streamAllocRef.callStackTrace);

      references.remove(i);
      connection.noNewStreams = true;

      
      if (references.isEmpty()) {
        connection.idleAtNanos = now - keepAliveDurationNs;
        return 0;
      }
    }

    return references.size();
  }
}
