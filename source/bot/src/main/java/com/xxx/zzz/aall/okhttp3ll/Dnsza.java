
package com.xxx.zzz.aall.okhttp3ll;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;


public interface Dnsza {
  
  Dnsza SYSTEM = new Dnsza() {
    @Override public List<InetAddress> lookup(String hostname) throws UnknownHostException {
      if (hostname == null) throw new UnknownHostException("hostname == null");
      return Arrays.asList(InetAddress.getAllByName(hostname));
    }
  };

  
  List<InetAddress> lookup(String hostname) throws UnknownHostException;
}
