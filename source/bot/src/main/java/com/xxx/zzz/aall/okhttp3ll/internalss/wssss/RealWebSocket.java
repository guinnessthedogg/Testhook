
package com.xxx.zzz.aall.okhttp3ll.internalss.wssss;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.okhttp3ll.Callzadasd;
import com.xxx.zzz.aall.okhttp3ll.Callbackza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.WebSocketzqa;
import com.xxx.zzz.aall.okhttp3ll.WebSocketListenerzaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Okiozaq;

public final class RealWebSocket implements WebSocketzqa, WebSocketReader.FrameCallback {
  private static final List<Protocolza> ONLY_HTTP1 = Collections.singletonList(Protocolza.HTTP_1_1);


  private static final long MAX_QUEUE_SIZE = 16 * 1024 * 1024; 


  private static final long CANCEL_AFTER_CLOSE_MILLIS = 60 * 1000;


  private final Requestza originalRequest;

  final WebSocketListenerzaq listener;
  private final Random random;
  private final String key;


  private Callzadasd call;


  private final Runnable writerRunnable;


  private WebSocketReader reader;

  


  private WebSocketWriter writer;


  private ScheduledExecutorService executor;


  private Streams streams;


  private final ArrayDeque<ByteStringzaq> pongQueue = new ArrayDeque<>();


  private final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque<>();


  private long queueSize;


  private boolean enqueuedClose;


  private ScheduledFuture<?> cancelFuture;


  private int receivedCloseCode = -1;


  private String receivedCloseReason;


  private boolean failed;


  int pingCount;


  int pongCount;

  public RealWebSocket(Requestza request, WebSocketListenerzaq listener, Random random) {
    if (!"GET".equals(request.method())) {
      throw new IllegalArgumentException("Request must be GET: " + request.method());
    }
    this.originalRequest = request;
    this.listener = listener;
    this.random = random;

    byte[] nonce = new byte[16];
    random.nextBytes(nonce);
    this.key = ByteStringzaq.of(nonce).base64();

    this.writerRunnable = new Runnable() {
      @Override public void run() {
        try {
          while (writeOneFrame()) {
          }
        } catch (IOException e) {
          failWebSocket(e, null);
        }
      }
    };
  }

  @Override public Requestza request() {
    return originalRequest;
  }

  @Override public synchronized long queueSize() {
    return queueSize;
  }

  @Override public void cancel() {
    call.cancel();
  }

  public void connect(OkHttpClientza client) {
    client = client.newBuilder()
        .protocols(ONLY_HTTP1)
        .build();
    final int pingIntervalMillis = client.pingIntervalMillis();
    final Requestza request = originalRequest.newBuilder()
        .header("Upgrade", "websocket")
        .header("Connection", "Upgrade")
        .header("Sec-WebSocket-Key", key)
        .header("Sec-WebSocket-Version", "13")
        .build();
    call = Internalq.instance.newWebSocketCall(client, request);
    call.enqueue(new Callbackza() {
      @Override public void onResponse(Callzadasd call, Responseza response) {
        try {
          checkResponse(response);
        } catch (ProtocolException e) {
          failWebSocket(e, response);
          Utilaq.closeQuietly(response);
          return;
        }

        
        StreamAllocation streamAllocation = Internalq.instance.streamAllocation(call);
        streamAllocation.noNewStreams(); 
        Streams streams = streamAllocation.connection().newWebSocketStreams(streamAllocation);

        
        try {
          listener.onOpen(RealWebSocket.this, response);
          String name = "OkHttp WebSocket " + request.url().redact();
          initReaderAndWriter(name, pingIntervalMillis, streams);
          streamAllocation.connection().socket().setSoTimeout(0);
          loopReader();
        } catch (Exception e) {
          failWebSocket(e, null);
        }
      }

      @Override public void onFailure(Callzadasd call, IOException e) {
        failWebSocket(e, null);
      }
    });
  }

  void checkResponse(Responseza response) throws ProtocolException {
    if (response.code() != 101) {
      throw new ProtocolException("Expected HTTP 101 response but was '"
          + response.code() + " " + response.message() + "'");
    }

    String headerConnection = response.header("Connection");
    if (!"Upgrade".equalsIgnoreCase(headerConnection)) {
      throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '"
          + headerConnection + "'");
    }

    String headerUpgrade = response.header("Upgrade");
    if (!"websocket".equalsIgnoreCase(headerUpgrade)) {
      throw new ProtocolException(
          "Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + "'");
    }

    String headerAccept = response.header("Sec-WebSocket-Accept");
    String acceptExpected = ByteStringzaq.encodeUtf8(key + WebSocketProtocol.ACCEPT_MAGIC)
        .sha1().base64();
    if (!acceptExpected.equals(headerAccept)) {
      throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '"
          + acceptExpected + "' but was '" + headerAccept + "'");
    }
  }

  public void initReaderAndWriter(
      String name, long pingIntervalMillis, Streams streams) throws IOException {
    synchronized (this) {
      this.streams = streams;
      this.writer = new WebSocketWriter(streams.client, streams.sink, random);
      this.executor = new ScheduledThreadPoolExecutor(1, Utilaq.threadFactory(name, false));
      if (pingIntervalMillis != 0) {
        executor.scheduleAtFixedRate(
            new PingRunnable(), pingIntervalMillis, pingIntervalMillis, MILLISECONDS);
      }
      if (!messageAndCloseQueue.isEmpty()) {
        runWriter(); 
      }
    }

    reader = new WebSocketReader(streams.client, streams.source, this);
  }


  public void loopReader() throws IOException {
    while (receivedCloseCode == -1) {
      
      reader.processNextFrame();
    }
  }


  boolean processNextFrame() throws IOException {
    try {
      reader.processNextFrame();
      return receivedCloseCode == -1;
    } catch (Exception e) {
      failWebSocket(e, null);
      return false;
    }
  }


  void awaitTermination(int timeout, TimeUnit timeUnit) throws InterruptedException {
    executor.awaitTermination(timeout, timeUnit);
  }


  void tearDown() throws InterruptedException {
    if (cancelFuture != null) {
      cancelFuture.cancel(false);
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
  }

  synchronized int pingCount() {
    return pingCount;
  }

  synchronized int pongCount() {
    return pongCount;
  }

  @Override public void onReadMessage(String text) throws IOException {
    listener.onMessage(this, text);
  }

  @Override public void onReadMessage(ByteStringzaq bytes) throws IOException {
    listener.onMessage(this, bytes);
  }

  @Override public synchronized void onReadPing(ByteStringzaq payload) {
    
    if (failed || (enqueuedClose && messageAndCloseQueue.isEmpty())) return;

    pongQueue.add(payload);
    runWriter();
    pingCount++;
  }

  @Override public synchronized void onReadPong(ByteStringzaq buffer) {
    
    pongCount++;
  }

  @Override public void onReadClose(int code, String reason) {
    if (code == -1) throw new IllegalArgumentException();

    Streams toClose = null;
    synchronized (this) {
      if (receivedCloseCode != -1) throw new IllegalStateException("already closed");
      receivedCloseCode = code;
      receivedCloseReason = reason;
      if (enqueuedClose && messageAndCloseQueue.isEmpty()) {
        toClose = this.streams;
        this.streams = null;
        if (cancelFuture != null) cancelFuture.cancel(false);
        this.executor.shutdown();
      }
    }

    try {
      listener.onClosing(this, code, reason);

      if (toClose != null) {
        listener.onClosed(this, code, reason);
      }
    } finally {
      Utilaq.closeQuietly(toClose);
    }
  }

  

  @Override public boolean send(String text) {
    if (text == null) throw new NullPointerException("text == null");
    return send(ByteStringzaq.encodeUtf8(text), WebSocketProtocol.OPCODE_TEXT);
  }

  @Override public boolean send(ByteStringzaq bytes) {
    if (bytes == null) throw new NullPointerException("bytes == null");
    return send(bytes, WebSocketProtocol.OPCODE_BINARY);
  }

  private synchronized boolean send(ByteStringzaq data, int formatOpcode) {
    
    if (failed || enqueuedClose) return false;

    
    if (queueSize + data.size() > MAX_QUEUE_SIZE) {
      close(WebSocketProtocol.CLOSE_CLIENT_GOING_AWAY, null);
      return false;
    }

    
    queueSize += data.size();
    messageAndCloseQueue.add(new Message(formatOpcode, data));
    runWriter();
    return true;
  }

  synchronized boolean pong(ByteStringzaq payload) {
    
    if (failed || (enqueuedClose && messageAndCloseQueue.isEmpty())) return false;

    pongQueue.add(payload);
    runWriter();
    return true;
  }

  @Override public boolean close(int code, String reason) {
    return close(code, reason, CANCEL_AFTER_CLOSE_MILLIS);
  }

  synchronized boolean close(int code, String reason, long cancelAfterCloseMillis) {
    WebSocketProtocol.validateCloseCode(code);

    ByteStringzaq reasonBytes = null;
    if (reason != null) {
      reasonBytes = ByteStringzaq.encodeUtf8(reason);
      if (reasonBytes.size() > WebSocketProtocol.CLOSE_MESSAGE_MAX) {
        throw new IllegalArgumentException("reason.size() > " + WebSocketProtocol.CLOSE_MESSAGE_MAX + ": " + reason);
      }
    }

    if (failed || enqueuedClose) return false;

    
    enqueuedClose = true;

    
    messageAndCloseQueue.add(new Close(code, reasonBytes, cancelAfterCloseMillis));
    runWriter();
    return true;
  }

  private void runWriter() {
    assert (Thread.holdsLock(this));

    if (executor != null) {
      executor.execute(writerRunnable);
    }
  }


  boolean writeOneFrame() throws IOException {
    WebSocketWriter writer;
    ByteStringzaq pong;
    Object messageOrClose = null;
    int receivedCloseCode = -1;
    String receivedCloseReason = null;
    Streams streamsToClose = null;

    synchronized (RealWebSocket.this) {
      if (failed) {
        return false; 
      }

      writer = this.writer;
      pong = pongQueue.poll();
      if (pong == null) {
        messageOrClose = messageAndCloseQueue.poll();
        if (messageOrClose instanceof Close) {
          receivedCloseCode = this.receivedCloseCode;
          receivedCloseReason = this.receivedCloseReason;
          if (receivedCloseCode != -1) {
            streamsToClose = this.streams;
            this.streams = null;
            this.executor.shutdown();
          } else {
            
            cancelFuture = executor.schedule(new CancelRunnable(),
                ((Close) messageOrClose).cancelAfterCloseMillis, MILLISECONDS);
          }
        } else if (messageOrClose == null) {
          return false; 
        }
      }
    }

    try {
      if (pong != null) {
        writer.writePong(pong);

      } else if (messageOrClose instanceof Message) {
        ByteStringzaq data = ((Message) messageOrClose).data;
        BufferedSinkzaqds sink = Okiozaq.buffer(writer.newMessageSink(
            ((Message) messageOrClose).formatOpcode, data.size()));
        sink.write(data);
        sink.close();
        synchronized (this) {
          queueSize -= data.size();
        }

      } else if (messageOrClose instanceof Close) {
        Close close = (Close) messageOrClose;
        writer.writeClose(close.code, close.reason);

        
        if (streamsToClose != null) {
          listener.onClosed(this, receivedCloseCode, receivedCloseReason);
        }

      } else {
        throw new AssertionError();
      }

      return true;
    } finally {
      Utilaq.closeQuietly(streamsToClose);
    }
  }

  private final class PingRunnable implements Runnable {
    PingRunnable() {
    }

    @Override public void run() {
      writePingFrame();
    }
  }

  void writePingFrame() {
    WebSocketWriter writer;
    synchronized (this) {
      if (failed) return;
      writer = this.writer;
    }

    try {
      writer.writePing(ByteStringzaq.EMPTY);
    } catch (IOException e) {
      failWebSocket(e, null);
    }
  }

  public void failWebSocket(Exception e, Responseza response) {
    Streams streamsToClose;
    synchronized (this) {
      if (failed) return; 
      failed = true;
      streamsToClose = this.streams;
      this.streams = null;
      if (cancelFuture != null) cancelFuture.cancel(false);
      if (executor != null) executor.shutdown();
    }

    try {
      listener.onFailure(this, e, response);
    } finally {
      Utilaq.closeQuietly(streamsToClose);
    }
  }

  static final class Message {
    final int formatOpcode;
    final ByteStringzaq data;

    Message(int formatOpcode, ByteStringzaq data) {
      this.formatOpcode = formatOpcode;
      this.data = data;
    }
  }

  static final class Close {
    final int code;
    final ByteStringzaq reason;
    final long cancelAfterCloseMillis;

    Close(int code, ByteStringzaq reason, long cancelAfterCloseMillis) {
      this.code = code;
      this.reason = reason;
      this.cancelAfterCloseMillis = cancelAfterCloseMillis;
    }
  }

  public abstract static class Streams implements Closeable {
    public final boolean client;
    public final BufferedSourcezaqdfs source;
    public final BufferedSinkzaqds sink;

    public Streams(boolean client, BufferedSourcezaqdfs source, BufferedSinkzaqds sink) {
      this.client = client;
      this.source = source;
      this.sink = sink;
    }
  }

  final class CancelRunnable implements Runnable {
    @Override public void run() {
      cancel();
    }
  }
}
