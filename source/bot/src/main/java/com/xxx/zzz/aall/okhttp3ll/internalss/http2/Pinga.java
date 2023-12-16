
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


final class Pinga {
  private final CountDownLatch latch = new CountDownLatch(1);
  private long sent = -1;
  private long received = -1;

  Pinga() {
  }

  void send() {
    if (sent != -1) throw new IllegalStateException();
    sent = System.nanoTime();
  }

  void receive() {
    if (received != -1 || sent == -1) throw new IllegalStateException();
    received = System.nanoTime();
    latch.countDown();
  }

  void cancel() {
    if (received != -1 || sent == -1) throw new IllegalStateException();
    received = sent - 1;
    latch.countDown();
  }


  public long roundTripTime() throws InterruptedException {
    latch.await();
    return received - sent;
  }


  public long roundTripTime(long timeout, TimeUnit unit) throws InterruptedException {
    if (latch.await(timeout, unit)) {
      return received - sent;
    } else {
      return -2;
    }
  }
}
