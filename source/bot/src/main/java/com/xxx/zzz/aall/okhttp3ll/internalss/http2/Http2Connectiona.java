
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;
import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.internalss.NamedRunnableq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Okiozaq;


public final class Http2Connectiona implements Closeable {

  static final ExecutorService executor = new ThreadPoolExecutor(0,
      Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
      Utilaq.threadFactory("OkHttp Http2Connection", true));

  
  final boolean client;

  
  final Listener listener;
  final Map<Integer, Http2Streama> streams = new LinkedHashMap<>();
  final String hostname;
  int lastGoodStreamId;
  int nextStreamId;
  boolean shutdown;

  
  private final ExecutorService pushExecutor;

  
  private Map<Integer, Pinga> pings;
  
  final PushObservera pushObserver;
  private int nextPingId;

  

  long unacknowledgedBytesRead = 0;

  

  long bytesLeftInWriteWindow;

  
  Settingsua okHttpSettings = new Settingsua();

  private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16 * 1024 * 1024;

  

  final Settingsua peerSettings = new Settingsua();

  boolean receivedInitialPeerSettings = false;
  final Socket socket;
  final Http2Writera writer;


  final ReaderRunnable readerRunnable;

  Http2Connectiona(Builder builder) {
    pushObserver = builder.pushObserver;
    client = builder.client;
    listener = builder.listener;

    nextStreamId = builder.client ? 1 : 2;
    if (builder.client) {
      nextStreamId += 2;
    }

    nextPingId = builder.client ? 1 : 2;





    if (builder.client) {
      okHttpSettings.set(Settingsua.INITIAL_WINDOW_SIZE, OKHTTP_CLIENT_WINDOW_SIZE);
    }

    hostname = builder.hostname;


    pushExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(),
        Utilaq.threadFactory(Utilaq.format("OkHttp %s Push Observer", hostname), true));
    peerSettings.set(Settingsua.INITIAL_WINDOW_SIZE, Settingsua.DEFAULT_INITIAL_WINDOW_SIZE);
    peerSettings.set(Settingsua.MAX_FRAME_SIZE, Http2a.INITIAL_MAX_FRAME_SIZE);
    bytesLeftInWriteWindow = peerSettings.getInitialWindowSize();
    socket = builder.socket;
    writer = new Http2Writera(builder.sink, client);

    readerRunnable = new ReaderRunnable(new Http2Readera(builder.source, client));
  }

  
  public Protocolza getProtocol() {
    return Protocolza.HTTP_2;
  }

  
  public synchronized int openStreamCount() {
    return streams.size();
  }

  synchronized Http2Streama getStream(int id) {
    return streams.get(id);
  }

  synchronized Http2Streama removeStream(int streamId) {
    Http2Streama stream = streams.remove(streamId);
    notifyAll();
    return stream;
  }

  public synchronized int maxConcurrentStreams() {
    return peerSettings.getMaxConcurrentStreams(Integer.MAX_VALUE);
  }

  
  public Http2Streama pushStream(int associatedStreamId, List<Headera> requestHeaders, boolean out)
      throws IOException {
    if (client) throw new IllegalStateException("Client cannot push requests.");
    return newStream(associatedStreamId, requestHeaders, out);
  }

  
  public Http2Streama newStream(List<Headera> requestHeaders, boolean out) throws IOException {
    return newStream(0, requestHeaders, out);
  }

  private Http2Streama newStream(
          int associatedStreamId, List<Headera> requestHeaders, boolean out) throws IOException {
    boolean outFinished = !out;
    boolean inFinished = false;
    boolean flushHeaders;
    Http2Streama stream;
    int streamId;

    synchronized (writer) {
      synchronized (this) {
        if (shutdown) {
          throw new ConnectionShutdownExceptionq();
        }
        streamId = nextStreamId;
        nextStreamId += 2;
        stream = new Http2Streama(streamId, this, outFinished, inFinished, requestHeaders);
        flushHeaders = !out || bytesLeftInWriteWindow == 0L || stream.bytesLeftInWriteWindow == 0L;
        if (stream.isOpen()) {
          streams.put(streamId, stream);
        }
      }
      if (associatedStreamId == 0) {
        writer.synStream(outFinished, streamId, associatedStreamId, requestHeaders);
      } else if (client) {
        throw new IllegalArgumentException("client streams shouldn't have associated stream IDs");
      } else {
        writer.pushPromise(associatedStreamId, streamId, requestHeaders);
      }
    }

    if (flushHeaders) {
      writer.flush();
    }

    return stream;
  }

  void writeSynReply(int streamId, boolean outFinished, List<Headera> alternating)
      throws IOException {
    writer.synReply(outFinished, streamId, alternating);
  }

  
  public void writeData(int streamId, boolean outFinished, Bufferzaq buffer, long byteCount)
      throws IOException {
    if (byteCount == 0) {
      writer.data(outFinished, streamId, buffer, 0);
      return;
    }

    while (byteCount > 0) {
      int toWrite;
      synchronized (Http2Connectiona.this) {
        try {
          while (bytesLeftInWriteWindow <= 0) {


            if (!streams.containsKey(streamId)) {
              throw new IOException("stream closed");
            }
            Http2Connectiona.this.wait(); 
          }
        } catch (InterruptedException e) {
          throw new InterruptedIOException();
        }

        toWrite = (int) Math.min(byteCount, bytesLeftInWriteWindow);
        toWrite = Math.min(toWrite, writer.maxDataLength());
        bytesLeftInWriteWindow -= toWrite;
      }

      byteCount -= toWrite;
      writer.data(outFinished && byteCount == 0, streamId, buffer, toWrite);
    }
  }

  
  void addBytesToWriteWindow(long delta) {
    bytesLeftInWriteWindow += delta;
    if (delta > 0) Http2Connectiona.this.notifyAll();
  }

  void writeSynResetLater(final int streamId, final ErrorCodeq errorCode) {
    executor.execute(new NamedRunnableq("OkHttp %s stream %d", hostname, streamId) {
      @Override public void execute() {
        try {
          writeSynReset(streamId, errorCode);
        } catch (IOException ignored) {
        }
      }
    });
  }

  void writeSynReset(int streamId, ErrorCodeq statusCode) throws IOException {
    writer.rstStream(streamId, statusCode);
  }

  void writeWindowUpdateLater(final int streamId, final long unacknowledgedBytesRead) {
    executor.execute(new NamedRunnableq("OkHttp Window Update %s stream %d", hostname, streamId) {
      @Override public void execute() {
        try {
          writer.windowUpdate(streamId, unacknowledgedBytesRead);
        } catch (IOException ignored) {
        }
      }
    });
  }

  
  public Pinga ping() throws IOException {
    Pinga ping = new Pinga();
    int pingId;
    synchronized (this) {
      if (shutdown) {
        throw new ConnectionShutdownExceptionq();
      }
      pingId = nextPingId;
      nextPingId += 2;
      if (pings == null) pings = new LinkedHashMap<>();
      pings.put(pingId, ping);
    }
    writePing(false, pingId, 0x4f4b6f6b , ping);
    return ping;
  }

  void writePingLater(
      final boolean reply, final int payload1, final int payload2, final Pinga ping) {
    executor.execute(new NamedRunnableq("OkHttp %s ping %08x%08x",
        hostname, payload1, payload2) {
      @Override public void execute() {
        try {
          writePing(reply, payload1, payload2, ping);
        } catch (IOException ignored) {
        }
      }
    });
  }

  void writePing(boolean reply, int payload1, int payload2, Pinga ping) throws IOException {
    synchronized (writer) {
      
      if (ping != null) ping.send();
      writer.ping(reply, payload1, payload2);
    }
  }

  synchronized Pinga removePing(int id) {
    return pings != null ? pings.remove(id) : null;
  }

  public void flush() throws IOException {
    writer.flush();
  }

  
  public void shutdown(ErrorCodeq statusCode) throws IOException {
    synchronized (writer) {
      int lastGoodStreamId;
      synchronized (this) {
        if (shutdown) {
          return;
        }
        shutdown = true;
        lastGoodStreamId = this.lastGoodStreamId;
      }
      
      
      writer.goAway(lastGoodStreamId, statusCode, Utilaq.EMPTY_BYTE_ARRAY);
    }
  }

  
  @Override public void close() throws IOException {
    close(ErrorCodeq.NO_ERROR, ErrorCodeq.CANCEL);
  }

  void close(ErrorCodeq connectionCode, ErrorCodeq streamCode) throws IOException {
    assert (!Thread.holdsLock(this));
    IOException thrown = null;
    try {
      shutdown(connectionCode);
    } catch (IOException e) {
      thrown = e;
    }

    Http2Streama[] streamsToClose = null;
    Pinga[] pingsToCancel = null;
    synchronized (this) {
      if (!streams.isEmpty()) {
        streamsToClose = streams.values().toArray(new Http2Streama[streams.size()]);
        streams.clear();
      }
      if (pings != null) {
        pingsToCancel = pings.values().toArray(new Pinga[pings.size()]);
        pings = null;
      }
    }

    if (streamsToClose != null) {
      for (Http2Streama stream : streamsToClose) {
        try {
          stream.close(streamCode);
        } catch (IOException e) {
          if (thrown != null) thrown = e;
        }
      }
    }

    if (pingsToCancel != null) {
      for (Pinga ping : pingsToCancel) {
        ping.cancel();
      }
    }

    
    try {
      writer.close();
    } catch (IOException e) {
      if (thrown == null) thrown = e;
    }

    
    try {
      socket.close();
    } catch (IOException e) {
      thrown = e;
    }

    if (thrown != null) throw thrown;
  }

  
  public void start() throws IOException {
    start(true);
  }

  
  void start(boolean sendConnectionPreface) throws IOException {
    if (sendConnectionPreface) {
      writer.connectionPreface();
      writer.settings(okHttpSettings);
      int windowSize = okHttpSettings.getInitialWindowSize();
      if (windowSize != Settingsua.DEFAULT_INITIAL_WINDOW_SIZE) {
        writer.windowUpdate(0, windowSize - Settingsua.DEFAULT_INITIAL_WINDOW_SIZE);
      }
    }
    new Thread(readerRunnable).start(); 
  }

  
  public void setSettings(Settingsua settings) throws IOException {
    synchronized (writer) {
      synchronized (this) {
        if (shutdown) {
          throw new ConnectionShutdownExceptionq();
        }
        okHttpSettings.merge(settings);
        writer.settings(settings);
      }
    }
  }

  public synchronized boolean isShutdown() {
    return shutdown;
  }

  public static class Builder {
    Socket socket;
    String hostname;
    BufferedSourcezaqdfs source;
    BufferedSinkzaqds sink;
    Listener listener = Listener.REFUSE_INCOMING_STREAMS;
    PushObservera pushObserver = PushObservera.CANCEL;
    boolean client;

    
    public Builder(boolean client) {
      this.client = client;
    }

    public Builder socket(Socket socket) throws IOException {
      return socket(socket, ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName(),
          Okiozaq.buffer(Okiozaq.source(socket)), Okiozaq.buffer(Okiozaq.sink(socket)));
    }

    public Builder socket(
            Socket socket, String hostname, BufferedSourcezaqdfs source, BufferedSinkzaqds sink) {
      this.socket = socket;
      this.hostname = hostname;
      this.source = source;
      this.sink = sink;
      return this;
    }

    public Builder listener(Listener listener) {
      this.listener = listener;
      return this;
    }

    public Builder pushObserver(PushObservera pushObserver) {
      this.pushObserver = pushObserver;
      return this;
    }

    public Http2Connectiona build() throws IOException {
      return new Http2Connectiona(this);
    }
  }

  
  class ReaderRunnable extends NamedRunnableq implements Http2Readera.Handler {
    final Http2Readera reader;

    ReaderRunnable(Http2Readera reader) {
      super("OkHttp %s", hostname);
      this.reader = reader;
    }

    @Override protected void execute() {
      ErrorCodeq connectionErrorCode = ErrorCodeq.INTERNAL_ERROR;
      ErrorCodeq streamErrorCode = ErrorCodeq.INTERNAL_ERROR;
      try {
        reader.readConnectionPreface(this);
        while (reader.nextFrame(false, this)) {
        }
        connectionErrorCode = ErrorCodeq.NO_ERROR;
        streamErrorCode = ErrorCodeq.CANCEL;
      } catch (IOException e) {
        connectionErrorCode = ErrorCodeq.PROTOCOL_ERROR;
        streamErrorCode = ErrorCodeq.PROTOCOL_ERROR;
      } finally {
        try {
          close(connectionErrorCode, streamErrorCode);
        } catch (IOException ignored) {
        }
        Utilaq.closeQuietly(reader);
      }
    }

    @Override public void data(boolean inFinished, int streamId, BufferedSourcezaqdfs source, int length)
        throws IOException {
      if (pushedStream(streamId)) {
        pushDataLater(streamId, source, length, inFinished);
        return;
      }
      Http2Streama dataStream = getStream(streamId);
      if (dataStream == null) {
        writeSynResetLater(streamId, ErrorCodeq.PROTOCOL_ERROR);
        source.skip(length);
        return;
      }
      dataStream.receiveData(source, length);
      if (inFinished) {
        dataStream.receiveFin();
      }
    }

    @Override public void headers(boolean inFinished, int streamId, int associatedStreamId,
        List<Headera> headerBlock) {
      if (pushedStream(streamId)) {
        pushHeadersLater(streamId, headerBlock, inFinished);
        return;
      }
      Http2Streama stream;
      synchronized (Http2Connectiona.this) {
        
        if (shutdown) return;

        stream = getStream(streamId);

        if (stream == null) {
          
          if (streamId <= lastGoodStreamId) return;

          
          if (streamId % 2 == nextStreamId % 2) return;

          
          final Http2Streama newStream = new Http2Streama(streamId, Http2Connectiona.this,
              false, inFinished, headerBlock);
          lastGoodStreamId = streamId;
          streams.put(streamId, newStream);
          executor.execute(new NamedRunnableq("OkHttp %s stream %d", hostname, streamId) {
            @Override public void execute() {
              try {
                listener.onStream(newStream);
              } catch (IOException e) {
                Platformq.get().log(Platformq.INFO, "Http2Connection.Listener failure for " + hostname, e);
                try {
                  newStream.close(ErrorCodeq.PROTOCOL_ERROR);
                } catch (IOException ignored) {
                }
              }
            }
          });
          return;
        }
      }

      
      stream.receiveHeaders(headerBlock);
      if (inFinished) stream.receiveFin();
    }

    @Override public void rstStream(int streamId, ErrorCodeq errorCode) {
      if (pushedStream(streamId)) {
        pushResetLater(streamId, errorCode);
        return;
      }
      Http2Streama rstStream = removeStream(streamId);
      if (rstStream != null) {
        rstStream.receiveRstStream(errorCode);
      }
    }

    @Override public void settings(boolean clearPrevious, Settingsua newSettings) {
      long delta = 0;
      Http2Streama[] streamsToNotify = null;
      synchronized (Http2Connectiona.this) {
        int priorWriteWindowSize = peerSettings.getInitialWindowSize();
        if (clearPrevious) peerSettings.clear();
        peerSettings.merge(newSettings);
        applyAndAckSettings(newSettings);
        int peerInitialWindowSize = peerSettings.getInitialWindowSize();
        if (peerInitialWindowSize != -1 && peerInitialWindowSize != priorWriteWindowSize) {
          delta = peerInitialWindowSize - priorWriteWindowSize;
          if (!receivedInitialPeerSettings) {
            addBytesToWriteWindow(delta);
            receivedInitialPeerSettings = true;
          }
          if (!streams.isEmpty()) {
            streamsToNotify = streams.values().toArray(new Http2Streama[streams.size()]);
          }
        }
        executor.execute(new NamedRunnableq("OkHttp %s settings", hostname) {
          @Override public void execute() {
            listener.onSettings(Http2Connectiona.this);
          }
        });
      }
      if (streamsToNotify != null && delta != 0) {
        for (Http2Streama stream : streamsToNotify) {
          synchronized (stream) {
            stream.addBytesToWriteWindow(delta);
          }
        }
      }
    }

    private void applyAndAckSettings(final Settingsua peerSettings) {
      executor.execute(new NamedRunnableq("OkHttp %s ACK Settings", hostname) {
        @Override public void execute() {
          try {
            writer.applyAndAckSettings(peerSettings);
          } catch (IOException ignored) {
          }
        }
      });
    }

    @Override public void ackSettings() {
      
    }

    @Override public void ping(boolean reply, int payload1, int payload2) {
      if (reply) {
        Pinga ping = removePing(payload1);
        if (ping != null) {
          ping.receive();
        }
      } else {
        
        writePingLater(true, payload1, payload2, null);
      }
    }

    @Override public void goAway(int lastGoodStreamId, ErrorCodeq errorCode, ByteStringzaq debugData) {
      if (debugData.size() > 0) { 
      }

      
      Http2Streama[] streamsCopy;
      synchronized (Http2Connectiona.this) {
        streamsCopy = streams.values().toArray(new Http2Streama[streams.size()]);
        shutdown = true;
      }

      
      for (Http2Streama http2Stream : streamsCopy) {
        if (http2Stream.getId() > lastGoodStreamId && http2Stream.isLocallyInitiated()) {
          http2Stream.receiveRstStream(ErrorCodeq.REFUSED_STREAM);
          removeStream(http2Stream.getId());
        }
      }
    }

    @Override public void windowUpdate(int streamId, long windowSizeIncrement) {
      if (streamId == 0) {
        synchronized (Http2Connectiona.this) {
          bytesLeftInWriteWindow += windowSizeIncrement;
          Http2Connectiona.this.notifyAll();
        }
      } else {
        Http2Streama stream = getStream(streamId);
        if (stream != null) {
          synchronized (stream) {
            stream.addBytesToWriteWindow(windowSizeIncrement);
          }
        }
      }
    }

    @Override public void priority(int streamId, int streamDependency, int weight,
        boolean exclusive) {
      
    }

    @Override
    public void pushPromise(int streamId, int promisedStreamId, List<Headera> requestHeaders) {
      pushRequestLater(promisedStreamId, requestHeaders);
    }

    @Override public void alternateService(int streamId, String origin, ByteStringzaq protocol,
        String host, int port, long maxAge) {
      
    }
  }

  
  boolean pushedStream(int streamId) {
    return streamId != 0 && (streamId & 1) == 0;
  }

  
  final Set<Integer> currentPushRequests = new LinkedHashSet<>();

  void pushRequestLater(final int streamId, final List<Headera> requestHeaders) {
    synchronized (this) {
      if (currentPushRequests.contains(streamId)) {
        writeSynResetLater(streamId, ErrorCodeq.PROTOCOL_ERROR);
        return;
      }
      currentPushRequests.add(streamId);
    }
    pushExecutor.execute(new NamedRunnableq("OkHttp %s Push Request[%s]", hostname, streamId) {
      @Override public void execute() {
        boolean cancel = pushObserver.onRequest(streamId, requestHeaders);
        try {
          if (cancel) {
            writer.rstStream(streamId, ErrorCodeq.CANCEL);
            synchronized (Http2Connectiona.this) {
              currentPushRequests.remove(streamId);
            }
          }
        } catch (IOException ignored) {
        }
      }
    });
  }

  void pushHeadersLater(final int streamId, final List<Headera> requestHeaders,
      final boolean inFinished) {
    pushExecutor.execute(new NamedRunnableq("OkHttp %s Push Headers[%s]", hostname, streamId) {
      @Override public void execute() {
        boolean cancel = pushObserver.onHeaders(streamId, requestHeaders, inFinished);
        try {
          if (cancel) writer.rstStream(streamId, ErrorCodeq.CANCEL);
          if (cancel || inFinished) {
            synchronized (Http2Connectiona.this) {
              currentPushRequests.remove(streamId);
            }
          }
        } catch (IOException ignored) {
        }
      }
    });
  }

  
  void pushDataLater(final int streamId, final BufferedSourcezaqdfs source, final int byteCount,
                     final boolean inFinished) throws IOException {
    final Bufferzaq buffer = new Bufferzaq();
    source.require(byteCount); 
    source.read(buffer, byteCount);
    if (buffer.size() != byteCount) throw new IOException(buffer.size() + " != " + byteCount);
    pushExecutor.execute(new NamedRunnableq("OkHttp %s Push Data[%s]", hostname, streamId) {
      @Override public void execute() {
        try {
          boolean cancel = pushObserver.onData(streamId, buffer, byteCount, inFinished);
          if (cancel) writer.rstStream(streamId, ErrorCodeq.CANCEL);
          if (cancel || inFinished) {
            synchronized (Http2Connectiona.this) {
              currentPushRequests.remove(streamId);
            }
          }
        } catch (IOException ignored) {
        }
      }
    });
  }

  void pushResetLater(final int streamId, final ErrorCodeq errorCode) {
    pushExecutor.execute(new NamedRunnableq("OkHttp %s Push Reset[%s]", hostname, streamId) {
      @Override public void execute() {
        pushObserver.onReset(streamId, errorCode);
        synchronized (Http2Connectiona.this) {
          currentPushRequests.remove(streamId);
        }
      }
    });
  }

  
  public abstract static class Listener {
    public static final Listener REFUSE_INCOMING_STREAMS = new Listener() {
      @Override public void onStream(Http2Streama stream) throws IOException {
        stream.close(ErrorCodeq.REFUSED_STREAM);
      }
    };

    
    public abstract void onStream(Http2Streama stream) throws IOException;

    
    public void onSettings(Http2Connectiona connection) {
    }
  }
}
