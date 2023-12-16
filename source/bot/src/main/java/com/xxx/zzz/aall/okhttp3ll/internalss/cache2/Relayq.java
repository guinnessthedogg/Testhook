
package com.xxx.zzz.aall.okhttp3ll.internalss.cache2;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;
import com.xxx.zzz.aall.okioss.Timeoutzaq;




final class Relayq {
  private static final int SOURCE_UPSTREAM = 1;
  private static final int SOURCE_FILE = 2;

  static final ByteStringzaq PREFIX_CLEAN = ByteStringzaq.encodeUtf8("OkHttp cache v1\n");
  static final ByteStringzaq PREFIX_DIRTY = ByteStringzaq.encodeUtf8("OkHttp DIRTY :(\n");
  private static final long FILE_HEADER_SIZE = 32L;


  RandomAccessFile file;


  Thread upstreamReader;


  Sourcezaq upstream;


  final Bufferzaq upstreamBuffer = new Bufferzaq();


  long upstreamPos;


  boolean complete;


  private final ByteStringzaq metadata;


  final Bufferzaq buffer = new Bufferzaq();


  final long bufferMaxSize;


  int sourceCount;

  private Relayq(RandomAccessFile file, Sourcezaq upstream, long upstreamPos, ByteStringzaq metadata,
                 long bufferMaxSize) {
    this.file = file;
    this.upstream = upstream;
    this.complete = upstream == null;
    this.upstreamPos = upstreamPos;
    this.metadata = metadata;
    this.bufferMaxSize = bufferMaxSize;
  }


  public static Relayq edit(
          File file, Sourcezaq upstream, ByteStringzaq metadata, long bufferMaxSize) throws IOException {
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    Relayq result = new Relayq(randomAccessFile, upstream, 0L, metadata, bufferMaxSize);


    randomAccessFile.setLength(0L);
    result.writeHeader(PREFIX_DIRTY, -1L, -1L);

    return result;
  }


  public static Relayq read(File file) throws IOException {
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    FileOperatorq fileOperator = new FileOperatorq(randomAccessFile.getChannel());


    Bufferzaq header = new Bufferzaq();
    fileOperator.read(0, header, FILE_HEADER_SIZE);
    ByteStringzaq prefix = header.readByteString(PREFIX_CLEAN.size());
    if (!prefix.equals(PREFIX_CLEAN)) throw new IOException("unreadable cache file");
    long upstreamSize = header.readLong();
    long metadataSize = header.readLong();


    Bufferzaq metadataBuffer = new Bufferzaq();
    fileOperator.read(FILE_HEADER_SIZE + upstreamSize, metadataBuffer, metadataSize);
    ByteStringzaq metadata = metadataBuffer.readByteString();


    return new Relayq(randomAccessFile, null, upstreamSize, metadata, 0L);
  }

  private void writeHeader(
          ByteStringzaq prefix, long upstreamSize, long metadataSize) throws IOException {
    Bufferzaq header = new Bufferzaq();
    header.write(prefix);
    header.writeLong(upstreamSize);
    header.writeLong(metadataSize);
    if (header.size() != FILE_HEADER_SIZE) throw new IllegalArgumentException();

    FileOperatorq fileOperator = new FileOperatorq(file.getChannel());
    fileOperator.write(0, header, FILE_HEADER_SIZE);
  }

  private void writeMetadata(long upstreamSize) throws IOException {
    Bufferzaq metadataBuffer = new Bufferzaq();
    metadataBuffer.write(metadata);

    FileOperatorq fileOperator = new FileOperatorq(file.getChannel());
    fileOperator.write(FILE_HEADER_SIZE + upstreamSize, metadataBuffer, metadata.size());
  }

  void commit(long upstreamSize) throws IOException {

    writeMetadata(upstreamSize);
    file.getChannel().force(false);


    writeHeader(PREFIX_CLEAN, upstreamSize, metadata.size());
    file.getChannel().force(false);


    synchronized (Relayq.this) {
      complete = true;
    }

    Utilaq.closeQuietly(upstream);
    upstream = null;
  }

  boolean isClosed() {
    return file == null;
  }

  public ByteStringzaq metadata() {
    return metadata;
  }


  public Sourcezaq newSource() {
    synchronized (Relayq.this) {
      if (file == null) return null;
      sourceCount++;
    }

    return new RelaySource();
  }

  class RelaySource implements Sourcezaq {
    private final Timeoutzaq timeout = new Timeoutzaq();


    private FileOperatorq fileOperator = new FileOperatorq(file.getChannel());


    private long sourcePos;


    @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
      if (fileOperator == null) throw new IllegalStateException("closed");

      long upstreamPos;
      int source;

      selectSource:
      synchronized (Relayq.this) {

        while (sourcePos == (upstreamPos = Relayq.this.upstreamPos)) {
          
          if (complete) return -1L;

          
          if (upstreamReader != null) {
            timeout.waitUntilNotified(Relayq.this);
            continue;
          }

          
          upstreamReader = Thread.currentThread();
          source = SOURCE_UPSTREAM;
          break selectSource;
        }

        long bufferPos = upstreamPos - buffer.size();

        
        if (sourcePos < bufferPos) {
          source = SOURCE_FILE;
          break selectSource;
        }

        
        long bytesToRead = Math.min(byteCount, upstreamPos - sourcePos);
        buffer.copyTo(sink, sourcePos - bufferPos, bytesToRead);
        sourcePos += bytesToRead;
        return bytesToRead;
      }

      
      if (source == SOURCE_FILE) {
        long bytesToRead = Math.min(byteCount, upstreamPos - sourcePos);
        fileOperator.read(FILE_HEADER_SIZE + sourcePos, sink, bytesToRead);
        sourcePos += bytesToRead;
        return bytesToRead;
      }

      
      
      try {
        long upstreamBytesRead = upstream.read(upstreamBuffer, bufferMaxSize);

        
        if (upstreamBytesRead == -1L) {
          commit(upstreamPos);
          return -1L;
        }

        
        long bytesRead = Math.min(upstreamBytesRead, byteCount);
        upstreamBuffer.copyTo(sink, 0, bytesRead);
        sourcePos += bytesRead;

        
        fileOperator.write(
            FILE_HEADER_SIZE + upstreamPos, upstreamBuffer.clone(), upstreamBytesRead);

        synchronized (Relayq.this) {
          
          buffer.write(upstreamBuffer, upstreamBytesRead);
          if (buffer.size() > bufferMaxSize) {
            buffer.skip(buffer.size() - bufferMaxSize);
          }

          
          Relayq.this.upstreamPos += upstreamBytesRead;
        }

        return bytesRead;
      } finally {
        synchronized (Relayq.this) {
          upstreamReader = null;
          Relayq.this.notifyAll();
        }
      }
    }

    @Override public Timeoutzaq timeout() {
      return timeout;
    }

    @Override public void close() throws IOException {
      if (fileOperator == null) return; 
      fileOperator = null;

      RandomAccessFile fileToClose = null;
      synchronized (Relayq.this) {
        sourceCount--;
        if (sourceCount == 0) {
          fileToClose = file;
          file = null;
        }
      }

      if (fileToClose != null) {
        Utilaq.closeQuietly(fileToClose);
      }
    }
  }
}
