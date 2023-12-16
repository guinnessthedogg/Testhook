
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


public interface BufferedSinkzaqds extends Sinkzaq {

  Bufferzaq buffer();

  BufferedSinkzaqds write(ByteStringzaq byteString) throws IOException;


  BufferedSinkzaqds write(byte[] source) throws IOException;


  BufferedSinkzaqds write(byte[] source, int offset, int byteCount) throws IOException;


  long writeAll(Sourcezaq source) throws IOException;


  BufferedSinkzaqds write(Sourcezaq source, long byteCount) throws IOException;


  BufferedSinkzaqds writeUtf8(String string) throws IOException;


  BufferedSinkzaqds writeUtf8(String string, int beginIndex, int endIndex) throws IOException;


  BufferedSinkzaqds writeUtf8CodePoint(int codePoint) throws IOException;


  BufferedSinkzaqds writeString(String string, Charset charset) throws IOException;


  BufferedSinkzaqds writeString(String string, int beginIndex, int endIndex, Charset charset)
      throws IOException;


  BufferedSinkzaqds writeByte(int b) throws IOException;


  BufferedSinkzaqds writeShort(int s) throws IOException;


  BufferedSinkzaqds writeShortLe(int s) throws IOException;


  BufferedSinkzaqds writeInt(int i) throws IOException;


  BufferedSinkzaqds writeIntLe(int i) throws IOException;


  BufferedSinkzaqds writeLong(long v) throws IOException;


  BufferedSinkzaqds writeLongLe(long v) throws IOException;


  BufferedSinkzaqds writeDecimalLong(long v) throws IOException;


  BufferedSinkzaqds writeHexadecimalUnsignedLong(long v) throws IOException;


  @Override void flush() throws IOException;


  BufferedSinkzaqds emit() throws IOException;


  BufferedSinkzaqds emitCompleteSegments() throws IOException;


  OutputStream outputStream();
}
