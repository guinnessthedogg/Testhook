
package com.xxx.zzz.aall.okhttp3ll.internalss.cache2;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.Okiozaq;


final class FileOperatorq {
  private static final int BUFFER_SIZE = 8192;

  private final byte[] byteArray = new byte[BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
  private final FileChannel fileChannel;

  FileOperatorq(FileChannel fileChannel) {
    this.fileChannel = fileChannel;
  }


  public void write(long pos, Bufferzaq source, long byteCount) throws IOException {
    if (byteCount < 0 || byteCount > source.size()) throw new IndexOutOfBoundsException();

    while (byteCount > 0L) {
      try {

        int toWrite = (int) Math.min(BUFFER_SIZE, byteCount);
        source.read(byteArray, 0, toWrite);
        byteBuffer.limit(toWrite);


        do {
          int bytesWritten = fileChannel.write(byteBuffer, pos);
          pos += bytesWritten;
        } while (byteBuffer.hasRemaining());

        byteCount -= toWrite;
      } finally {
        byteBuffer.clear();
      }
    }
  }


  public void read(long pos, Bufferzaq sink, long byteCount) throws IOException {
    if (byteCount < 0) throw new IndexOutOfBoundsException();

    while (byteCount > 0L) {
      try {

        byteBuffer.limit((int) Math.min(BUFFER_SIZE, byteCount));
        if (fileChannel.read(byteBuffer, pos) == -1) throw new EOFException();
        int bytesRead = byteBuffer.position();


        sink.write(byteArray, 0, bytesRead);
        pos += bytesRead;
        byteCount -= bytesRead;
      } finally {
        byteBuffer.clear();
      }
    }
  }
}
