
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.xxx.zzz.aall.okioss.AsyncTimeoutzaq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;
import com.xxx.zzz.aall.okioss.Timeoutzaq;


public final class Http2Streama {



  

  long unacknowledgedBytesRead = 0;

  

  long bytesLeftInWriteWindow;

  final int id;
  final Http2Connectiona connection;

  
  private final List<Headera> requestHeaders;

  
  private List<Headera> responseHeaders;

  
  private boolean hasResponseHeaders;

  private final FramingSource source;
  final FramingSink sink;
  final StreamTimeout readTimeout = new StreamTimeout();
  final StreamTimeout writeTimeout = new StreamTimeout();

  
  ErrorCodeq errorCode = null;

  Http2Streama(int id, Http2Connectiona connection, boolean outFinished, boolean inFinished,
               List<Headera> requestHeaders) {
    if (connection == null) throw new NullPointerException("connection == null");
    if (requestHeaders == null) throw new NullPointerException("requestHeaders == null");
    this.id = id;
    this.connection = connection;
    this.bytesLeftInWriteWindow =
        connection.peerSettings.getInitialWindowSize();
    this.source = new FramingSource(connection.okHttpSettings.getInitialWindowSize());
    this.sink = new FramingSink();
    this.source.finished = inFinished;
    this.sink.finished = outFinished;
    this.requestHeaders = requestHeaders;
  }

  public int getId() {
    return id;
  }

  
  public synchronized boolean isOpen() {
    if (errorCode != null) {
      return false;
    }
    if ((source.finished || source.closed)
        && (sink.finished || sink.closed)
        && hasResponseHeaders) {
      return false;
    }
    return true;
  }

  
  public boolean isLocallyInitiated() {
    boolean streamIsClient = ((id & 1) == 1);
    return connection.client == streamIsClient;
  }

  public Http2Connectiona getConnection() {
    return connection;
  }

  public List<Headera> getRequestHeaders() {
    return requestHeaders;
  }

  
  public synchronized List<Headera> takeResponseHeaders() throws IOException {
    if (!isLocallyInitiated()) {
      throw new IllegalStateException("servers cannot read response headers");
    }
    readTimeout.enter();
    try {
      while (responseHeaders == null && errorCode == null) {
        waitForIo();
      }
    } finally {
      readTimeout.exitAndThrowIfTimedOut();
    }
    List<Headera> result = responseHeaders;
    if (result != null) {
      responseHeaders = null;
      return result;
    }
    throw new StreamResetExceptiona(errorCode);
  }

  
  public synchronized ErrorCodeq getErrorCode() {
    return errorCode;
  }

  
  public void sendResponseHeaders(List<Headera> responseHeaders, boolean out) throws IOException {
    assert (!Thread.holdsLock(Http2Streama.this));
    if (responseHeaders == null) {
      throw new NullPointerException("responseHeaders == null");
    }
    boolean outFinished = false;
    synchronized (this) {
      this.hasResponseHeaders = true;
      if (!out) {
        this.sink.finished = true;
        outFinished = true;
      }
    }
    connection.writeSynReply(id, outFinished, responseHeaders);

    if (outFinished) {
      connection.flush();
    }
  }

  public Timeoutzaq readTimeout() {
    return readTimeout;
  }

  public Timeoutzaq writeTimeout() {
    return writeTimeout;
  }

  
  public Sourcezaq getSource() {
    return source;
  }

  
  public Sinkzaq getSink() {
    synchronized (this) {
      if (!hasResponseHeaders && !isLocallyInitiated()) {
        throw new IllegalStateException("reply before requesting the sink");
      }
    }
    return sink;
  }

  
  public void close(ErrorCodeq rstStatusCode) throws IOException {
    if (!closeInternal(rstStatusCode)) {
      return;
    }
    connection.writeSynReset(id, rstStatusCode);
  }

  
  public void closeLater(ErrorCodeq errorCode) {
    if (!closeInternal(errorCode)) {
      return;
    }
    connection.writeSynResetLater(id, errorCode);
  }

  
  private boolean closeInternal(ErrorCodeq errorCode) {
    assert (!Thread.holdsLock(this));
    synchronized (this) {
      if (this.errorCode != null) {
        return false;
      }
      if (source.finished && sink.finished) {
        return false;
      }
      this.errorCode = errorCode;
      notifyAll();
    }
    connection.removeStream(id);
    return true;
  }

  void receiveHeaders(List<Headera> headers) {
    assert (!Thread.holdsLock(Http2Streama.this));
    boolean open = true;
    synchronized (this) {
      hasResponseHeaders = true;
      if (responseHeaders == null) {
        responseHeaders = headers;
        open = isOpen();
        notifyAll();
      } else {
        List<Headera> newHeaders = new ArrayList<>();
        newHeaders.addAll(responseHeaders);
        newHeaders.add(null);
        newHeaders.addAll(headers);
        this.responseHeaders = newHeaders;
      }
    }
    if (!open) {
      connection.removeStream(id);
    }
  }

  void receiveData(BufferedSourcezaqdfs in, int length) throws IOException {
    assert (!Thread.holdsLock(Http2Streama.this));
    this.source.receive(in, length);
  }

  void receiveFin() {
    assert (!Thread.holdsLock(Http2Streama.this));
    boolean open;
    synchronized (this) {
      this.source.finished = true;
      open = isOpen();
      notifyAll();
    }
    if (!open) {
      connection.removeStream(id);
    }
  }

  synchronized void receiveRstStream(ErrorCodeq errorCode) {
    if (this.errorCode == null) {
      this.errorCode = errorCode;
      notifyAll();
    }
  }

  
  private final class FramingSource implements Sourcezaq {
    
    private final Bufferzaq receiveBuffer = new Bufferzaq();

    
    private final Bufferzaq readBuffer = new Bufferzaq();

    
    private final long maxByteCount;

    
    boolean closed;

    
    boolean finished;

    FramingSource(long maxByteCount) {
      this.maxByteCount = maxByteCount;
    }

    @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
      if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);

      long read;
      synchronized (Http2Streama.this) {
        waitUntilReadable();
        checkNotClosed();
        if (readBuffer.size() == 0) return -1;


        read = readBuffer.read(sink, Math.min(byteCount, readBuffer.size()));


        unacknowledgedBytesRead += read;
        if (unacknowledgedBytesRead
            >= connection.okHttpSettings.getInitialWindowSize() / 2) {
          connection.writeWindowUpdateLater(id, unacknowledgedBytesRead);
          unacknowledgedBytesRead = 0;
        }
      }


      synchronized (connection) { 
        connection.unacknowledgedBytesRead += read;
        if (connection.unacknowledgedBytesRead
            >= connection.okHttpSettings.getInitialWindowSize() / 2) {
          connection.writeWindowUpdateLater(0, connection.unacknowledgedBytesRead);
          connection.unacknowledgedBytesRead = 0;
        }
      }

      return read;
    }

    
    private void waitUntilReadable() throws IOException {
      readTimeout.enter();
      try {
        while (readBuffer.size() == 0 && !finished && !closed && errorCode == null) {
          waitForIo();
        }
      } finally {
        readTimeout.exitAndThrowIfTimedOut();
      }
    }

    void receive(BufferedSourcezaqdfs in, long byteCount) throws IOException {
      assert (!Thread.holdsLock(Http2Streama.this));

      while (byteCount > 0) {
        boolean finished;
        boolean flowControlError;
        synchronized (Http2Streama.this) {
          finished = this.finished;
          flowControlError = byteCount + readBuffer.size() > maxByteCount;
        }

        
        if (flowControlError) {
          in.skip(byteCount);
          closeLater(ErrorCodeq.FLOW_CONTROL_ERROR);
          return;
        }

        
        if (finished) {
          in.skip(byteCount);
          return;
        }

        
        long read = in.read(receiveBuffer, byteCount);
        if (read == -1) throw new EOFException();
        byteCount -= read;

        
        synchronized (Http2Streama.this) {
          boolean wasEmpty = readBuffer.size() == 0;
          readBuffer.writeAll(receiveBuffer);
          if (wasEmpty) {
            Http2Streama.this.notifyAll();
          }
        }
      }
    }

    @Override public Timeoutzaq timeout() {
      return readTimeout;
    }

    @Override public void close() throws IOException {
      synchronized (Http2Streama.this) {
        closed = true;
        readBuffer.clear();
        Http2Streama.this.notifyAll();
      }
      cancelStreamIfNecessary();
    }

    private void checkNotClosed() throws IOException {
      if (closed) {
        throw new IOException("stream closed");
      }
      if (errorCode != null) {
        throw new StreamResetExceptiona(errorCode);
      }
    }
  }

  void cancelStreamIfNecessary() throws IOException {
    assert (!Thread.holdsLock(Http2Streama.this));
    boolean open;
    boolean cancel;
    synchronized (this) {
      cancel = !source.finished && source.closed && (sink.finished || sink.closed);
      open = isOpen();
    }
    if (cancel) {
      
      
      
      
      Http2Streama.this.close(ErrorCodeq.CANCEL);
    } else if (!open) {
      connection.removeStream(id);
    }
  }

  
  final class FramingSink implements Sinkzaq {
    private static final long EMIT_BUFFER_SIZE = 16384;

    
    private final Bufferzaq sendBuffer = new Bufferzaq();

    boolean closed;

    
    boolean finished;

    @Override public void write(Bufferzaq source, long byteCount) throws IOException {
      assert (!Thread.holdsLock(Http2Streama.this));
      sendBuffer.write(source, byteCount);
      while (sendBuffer.size() >= EMIT_BUFFER_SIZE) {
        emitFrame(false);
      }
    }

    
    private void emitFrame(boolean outFinished) throws IOException {
      long toWrite;
      synchronized (Http2Streama.this) {
        writeTimeout.enter();
        try {
          while (bytesLeftInWriteWindow <= 0 && !finished && !closed && errorCode == null) {
            waitForIo(); 
          }
        } finally {
          writeTimeout.exitAndThrowIfTimedOut();
        }

        checkOutNotClosed(); 
        toWrite = Math.min(bytesLeftInWriteWindow, sendBuffer.size());
        bytesLeftInWriteWindow -= toWrite;
      }

      writeTimeout.enter();
      try {
        connection.writeData(id, outFinished && toWrite == sendBuffer.size(), sendBuffer, toWrite);
      } finally {
        writeTimeout.exitAndThrowIfTimedOut();
      }
    }

    @Override public void flush() throws IOException {
      assert (!Thread.holdsLock(Http2Streama.this));
      synchronized (Http2Streama.this) {
        checkOutNotClosed();
      }
      while (sendBuffer.size() > 0) {
        emitFrame(false);
        connection.flush();
      }
    }

    @Override public Timeoutzaq timeout() {
      return writeTimeout;
    }

    @Override public void close() throws IOException {
      assert (!Thread.holdsLock(Http2Streama.this));
      synchronized (Http2Streama.this) {
        if (closed) return;
      }
      if (!sink.finished) {
        
        if (sendBuffer.size() > 0) {
          while (sendBuffer.size() > 0) {
            emitFrame(true);
          }
        } else {
          
          connection.writeData(id, true, null, 0);
        }
      }
      synchronized (Http2Streama.this) {
        closed = true;
      }
      connection.flush();
      cancelStreamIfNecessary();
    }
  }

  
  void addBytesToWriteWindow(long delta) {
    bytesLeftInWriteWindow += delta;
    if (delta > 0) Http2Streama.this.notifyAll();
  }

  void checkOutNotClosed() throws IOException {
    if (sink.closed) {
      throw new IOException("stream closed");
    } else if (sink.finished) {
      throw new IOException("stream finished");
    } else if (errorCode != null) {
      throw new StreamResetExceptiona(errorCode);
    }
  }

  
  void waitForIo() throws InterruptedIOException {
    try {
      wait();
    } catch (InterruptedException e) {
      throw new InterruptedIOException();
    }
  }

  
  class StreamTimeout extends AsyncTimeoutzaq {
    @Override protected void timedOut() {
      closeLater(ErrorCodeq.CANCEL);
    }

    @Override protected IOException newTimeoutException(IOException cause) {
      SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
      if (cause != null) {
        socketTimeoutException.initCause(cause);
      }
      return socketTimeoutException;
    }

    public void exitAndThrowIfTimedOut() throws IOException {
      if (exit()) throw newTimeoutException(null );
    }
  }
}
