
package com.xxx.zzz.aall.okioss;

import java.io.Closeable;
import java.io.IOException;


public interface Sourcezaq extends Closeable {
  
  long read(Bufferzaq sink, long byteCount) throws IOException;

  
  Timeoutzaq timeout();

  
  @Override void close() throws IOException;
}
