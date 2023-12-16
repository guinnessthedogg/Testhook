
package com.xxx.zzz.aall.okioss;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;


public interface Sinkzaq extends Closeable, Flushable {
  
  void write(Bufferzaq source, long byteCount) throws IOException;

  
  @Override void flush() throws IOException;

  
  Timeoutzaq timeout();

  
  @Override void close() throws IOException;
}
