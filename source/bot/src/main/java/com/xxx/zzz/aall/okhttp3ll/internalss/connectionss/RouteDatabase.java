
package com.xxx.zzz.aall.okhttp3ll.internalss.connectionss;

import com.xxx.zzz.aall.okhttp3ll.Routeza;

import java.util.LinkedHashSet;
import java.util.Set;


public final class RouteDatabase {
  private final Set<Routeza> failedRoutes = new LinkedHashSet<>();


  public synchronized void failed(Routeza failedRoute) {
    failedRoutes.add(failedRoute);
  }


  public synchronized void connected(Routeza route) {
    failedRoutes.remove(route);
  }


  public synchronized boolean shouldPostpone(Routeza route) {
    return failedRoutes.contains(route);
  }
}
