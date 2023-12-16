
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public interface BufferedSourcezaqdfs extends Sourcezaq {

  Bufferzaq buffer();


  boolean exhausted() throws IOException;


  void require(long byteCount) throws IOException;


  boolean request(long byteCount) throws IOException;


  byte readByte() throws IOException;


  short readShort() throws IOException;


  short readShortLe() throws IOException;


  int readInt() throws IOException;


  int readIntLe() throws IOException;


  long readLong() throws IOException;


  long readLongLe() throws IOException;


  long readDecimalLong() throws IOException;


  long readHexadecimalUnsignedLong() throws IOException;


  void skip(long byteCount) throws IOException;


  ByteStringzaq readByteString() throws IOException;


  ByteStringzaq readByteString(long byteCount) throws IOException;


  int select(Ofdsptionszaq options) throws IOException;


  byte[] readByteArray() throws IOException;


  byte[] readByteArray(long byteCount) throws IOException;


  int read(byte[] sink) throws IOException;


  void readFully(byte[] sink) throws IOException;


  int read(byte[] sink, int offset, int byteCount) throws IOException;


  void readFully(Bufferzaq sink, long byteCount) throws IOException;


  long readAll(Sinkzaq sink) throws IOException;


  String readUtf8() throws IOException;


  String readUtf8(long byteCount) throws IOException;


  @Nullableq
  String readUtf8Line() throws IOException;


  String readUtf8LineStrict() throws IOException;


  String readUtf8LineStrict(long limit) throws IOException;


  int readUtf8CodePoint() throws IOException;


  String readString(Charset charset) throws IOException;


  String readString(long byteCount, Charset charset) throws IOException;


  long indexOf(byte b) throws IOException;


  long indexOf(byte b, long fromIndex) throws IOException;


  long indexOf(byte b, long fromIndex, long toIndex) throws IOException;


  long indexOf(ByteStringzaq bytes) throws IOException;


  long indexOf(ByteStringzaq bytes, long fromIndex) throws IOException;


  long indexOfElement(ByteStringzaq targetBytes) throws IOException;


  long indexOfElement(ByteStringzaq targetBytes, long fromIndex) throws IOException;


  boolean rangeEquals(long offset, ByteStringzaq bytes) throws IOException;


  boolean rangeEquals(long offset, ByteStringzaq bytes, int bytesOffset, int byteCount)
      throws IOException;


  InputStream inputStream();
}
