
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class DispatcherQ {
  private int maxRequests = 64;
  private int maxRequestsPerHost = 5;
  private @Nullableq
  Runnable idleCallback;

  
  private @Nullableq
  ExecutorService executorService;

  
  private final Deque<RealCallzaasas.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

  
  private final Deque<RealCallzaasas.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

  
  private final Deque<RealCallzaasas> runningSyncCalls = new ArrayDeque<>();

  public DispatcherQ(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public DispatcherQ() {
  }

  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(), Utilaq.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }

  
  public synchronized void setMaxRequests(int maxRequests) {
    if (maxRequests < 1) {
      throw new IllegalArgumentException("max < 1: " + maxRequests);
    }
    this.maxRequests = maxRequests;
    promoteCalls();
  }

  public synchronized int getMaxRequests() {
    return maxRequests;
  }

  
  public synchronized void setMaxRequestsPerHost(int maxRequestsPerHost) {
    if (maxRequestsPerHost < 1) {
      throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
    }
    this.maxRequestsPerHost = maxRequestsPerHost;
    promoteCalls();
  }

  public synchronized int getMaxRequestsPerHost() {
    return maxRequestsPerHost;
  }

  
  public synchronized void setIdleCallback(@Nullableq Runnable idleCallback) {
    this.idleCallback = idleCallback;
  }

  synchronized void enqueue(RealCallzaasas.AsyncCall call) {
    if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
      runningAsyncCalls.add(call);
      executorService().execute(call);
    } else {
      readyAsyncCalls.add(call);
    }
  }

  
  public synchronized void cancelAll() {
    for (RealCallzaasas.AsyncCall call : readyAsyncCalls) {
      call.get().cancel();
    }

    for (RealCallzaasas.AsyncCall call : runningAsyncCalls) {
      call.get().cancel();
    }

    for (RealCallzaasas call : runningSyncCalls) {
      call.cancel();
    }
  }

  private void promoteCalls() {
    if (runningAsyncCalls.size() >= maxRequests) return; 
    if (readyAsyncCalls.isEmpty()) return; 

    for (Iterator<RealCallzaasas.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
      RealCallzaasas.AsyncCall call = i.next();

      if (runningCallsForHost(call) < maxRequestsPerHost) {
        i.remove();
        runningAsyncCalls.add(call);
        executorService().execute(call);
      }

      if (runningAsyncCalls.size() >= maxRequests) return; 
    }
  }

  
  private int runningCallsForHost(RealCallzaasas.AsyncCall call) {
    int result = 0;
    for (RealCallzaasas.AsyncCall c : runningAsyncCalls) {
      if (c.host().equals(call.host())) result++;
    }
    return result;
  }

  
  synchronized void executed(RealCallzaasas call) {
    runningSyncCalls.add(call);
  }

  
  void finished(RealCallzaasas.AsyncCall call) {
    finished(runningAsyncCalls, call, true);
  }

  
  void finished(RealCallzaasas call) {
    finished(runningSyncCalls, call, false);
  }

  private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
    int runningCallsCount;
    Runnable idleCallback;
    synchronized (this) {
      if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
      if (promoteCalls) promoteCalls();
      runningCallsCount = runningCallsCount();
      idleCallback = this.idleCallback;
    }

    if (runningCallsCount == 0 && idleCallback != null) {
      idleCallback.run();
    }
  }

  
  public synchronized List<Callzadasd> queuedCalls() {
    List<Callzadasd> result = new ArrayList<>();
    for (RealCallzaasas.AsyncCall asyncCall : readyAsyncCalls) {
      result.add(asyncCall.get());
    }
    return Collections.unmodifiableList(result);
  }

  
  public synchronized List<Callzadasd> runningCalls() {
    List<Callzadasd> result = new ArrayList<>();
    result.addAll(runningSyncCalls);
    for (RealCallzaasas.AsyncCall asyncCall : runningAsyncCalls) {
      result.add(asyncCall.get());
    }
    return Collections.unmodifiableList(result);
  }

  public synchronized int queuedCallsCount() {
    return readyAsyncCalls.size();
  }

  public synchronized int runningCallsCount() {
    return runningAsyncCalls.size() + runningSyncCalls.size();
  }
}
