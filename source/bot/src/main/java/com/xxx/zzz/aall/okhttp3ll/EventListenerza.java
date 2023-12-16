
package com.xxx.zzz.aall.okhttp3ll;

import java.net.InetAddress;
import java.util.List;


abstract class EventListenerza {
  public static final EventListenerza NONE = new EventListenerza() {
  };

  static Factory factory(final EventListenerza listener) {
    return new Factory() {
      public EventListenerza create(Callzadasd call) {
        return listener;
      }
    };
  }

  public void fetchStart(Callzadasd call) {
  }

  public void dnsStart(Callzadasd call, String domainName) {
  }

  public void dnsEnd(Callzadasd call, String domainName, List<InetAddress> inetAddressList,
                     Throwable throwable) {
  }

  public void connectStart(Callzadasd call, InetAddress address, int port) {
  }

  public void secureConnectStart(Callzadasd call) {
  }

  public void secureConnectEnd(Callzadasd call, Handshakeza handshake,
                               Throwable throwable) {
  }

  public void connectEnd(Callzadasd call, InetAddress address, int port, String protocol,
                         Throwable throwable) {
  }

  public void requestHeadersStart(Callzadasd call) {
  }

  public void requestHeadersEnd(Callzadasd call, Throwable throwable) {
  }

  public void requestBodyStart(Callzadasd call) {
  }

  public void requestBodyEnd(Callzadasd call, Throwable throwable) {
  }

  public void responseHeadersStart(Callzadasd call) {
  }

  public void responseHeadersEnd(Callzadasd call, Throwable throwable) {
  }

  public void responseBodyStart(Callzadasd call) {
  }

  public void responseBodyEnd(Callzadasd call, Throwable throwable) {
  }

  public void fetchEnd(Callzadasd call, Throwable throwable) {
  }

  public interface Factory {
    EventListenerza create(Callzadasd call);
  }
}
