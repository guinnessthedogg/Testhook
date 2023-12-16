
package com.xxx.zzz.aall.okhttp3ll;

import java.io.IOException;


public interface Callzadasd extends Cloneable {
  
  Requestza request();

  
  Responseza execute() throws IOException;

  
  void enqueue(Callbackza responseCallback);

  
  void cancel();

  
  boolean isExecuted();

  boolean isCanceled();

  
  Callzadasd clone();

  interface Factory {
    Callzadasd newCall(Requestza request);
  }
}
