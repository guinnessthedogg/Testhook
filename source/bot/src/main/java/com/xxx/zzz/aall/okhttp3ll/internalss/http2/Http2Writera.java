
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import static java.util.logging.Level.FINE;
import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.format;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.CONNECTION_PREFACE;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.FLAG_ACK;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.FLAG_END_HEADERS;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.FLAG_END_STREAM;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.FLAG_NONE;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.INITIAL_MAX_FRAME_SIZE;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_CONTINUATION;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_DATA;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_GOAWAY;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_HEADERS;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_PING;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_PUSH_PROMISE;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_RST_STREAM;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_SETTINGS;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.TYPE_WINDOW_UPDATE;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.frameLog;
import static com.xxx.zzz.aall.okhttp3ll.internalss.http2.Http2a.illegalArgument;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;


final class Http2Writera implements Closeable {
  private static final Logger logger = Logger.getLogger(Http2a.class.getName());

  private final BufferedSinkzaqds sink;
  private final boolean client;
  private final Bufferzaq hpackBuffer;
  private int maxFrameSize;
  private boolean closed;

  final Hpacka.Writer hpackWriter;

  Http2Writera(BufferedSinkzaqds sink, boolean client) {
    this.sink = sink;
    this.client = client;
    this.hpackBuffer = new Bufferzaq();
    this.hpackWriter = new Hpacka.Writer(hpackBuffer);
    this.maxFrameSize = INITIAL_MAX_FRAME_SIZE;
  }

  public synchronized void connectionPreface() throws IOException {
    if (closed) throw new IOException("closed");
    if (!client) return;
    if (logger.isLoggable(FINE)) {
      logger.fine(format(">> CONNECTION %s", CONNECTION_PREFACE.hex()));
    }
    sink.write(CONNECTION_PREFACE.toByteArray());
    sink.flush();
  }

  
  public synchronized void applyAndAckSettings(Settingsua peerSettings) throws IOException {
    if (closed) throw new IOException("closed");
    this.maxFrameSize = peerSettings.getMaxFrameSize(maxFrameSize);
    if (peerSettings.getHeaderTableSize() != -1) {
      hpackWriter.setHeaderTableSizeSetting(peerSettings.getHeaderTableSize());
    }
    int length = 0;
    byte type = TYPE_SETTINGS;
    byte flags = FLAG_ACK;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    sink.flush();
  }

  
  public synchronized void pushPromise(int streamId, int promisedStreamId,
      List<Headera> requestHeaders) throws IOException {
    if (closed) throw new IOException("closed");
    hpackWriter.writeHeaders(requestHeaders);

    long byteCount = hpackBuffer.size();
    int length = (int) Math.min(maxFrameSize - 4, byteCount);
    byte type = TYPE_PUSH_PROMISE;
    byte flags = byteCount == length ? FLAG_END_HEADERS : 0;
    frameHeader(streamId, length + 4, type, flags);
    sink.writeInt(promisedStreamId & 0x7fffffff);
    sink.write(hpackBuffer, length);

    if (byteCount > length) writeContinuationFrames(streamId, byteCount - length);
  }

  public synchronized void flush() throws IOException {
    if (closed) throw new IOException("closed");
    sink.flush();
  }

  public synchronized void synStream(boolean outFinished, int streamId,
      int associatedStreamId, List<Headera> headerBlock) throws IOException {
    if (closed) throw new IOException("closed");
    headers(outFinished, streamId, headerBlock);
  }

  public synchronized void synReply(boolean outFinished, int streamId,
      List<Headera> headerBlock) throws IOException {
    if (closed) throw new IOException("closed");
    headers(outFinished, streamId, headerBlock);
  }

  public synchronized void headers(int streamId, List<Headera> headerBlock)
      throws IOException {
    if (closed) throw new IOException("closed");
    headers(false, streamId, headerBlock);
  }

  public synchronized void rstStream(int streamId, ErrorCodeq errorCode)
      throws IOException {
    if (closed) throw new IOException("closed");
    if (errorCode.httpCode == -1) throw new IllegalArgumentException();

    int length = 4;
    byte type = TYPE_RST_STREAM;
    byte flags = FLAG_NONE;
    frameHeader(streamId, length, type, flags);
    sink.writeInt(errorCode.httpCode);
    sink.flush();
  }

  
  public int maxDataLength() {
    return maxFrameSize;
  }

  
  public synchronized void data(boolean outFinished, int streamId, Bufferzaq source, int byteCount)
      throws IOException {
    if (closed) throw new IOException("closed");
    byte flags = FLAG_NONE;
    if (outFinished) flags |= FLAG_END_STREAM;
    dataFrame(streamId, flags, source, byteCount);
  }

  void dataFrame(int streamId, byte flags, Bufferzaq buffer, int byteCount) throws IOException {
    byte type = TYPE_DATA;
    frameHeader(streamId, byteCount, type, flags);
    if (byteCount > 0) {
      sink.write(buffer, byteCount);
    }
  }

  
  public synchronized void settings(Settingsua settings) throws IOException {
    if (closed) throw new IOException("closed");
    int length = settings.size() * 6;
    byte type = TYPE_SETTINGS;
    byte flags = FLAG_NONE;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    for (int i = 0; i < Settingsua.COUNT; i++) {
      if (!settings.isSet(i)) continue;
      int id = i;
      if (id == 4) {
        id = 3;
      } else if (id == 7) {
        id = 4;
      }
      sink.writeShort(id);
      sink.writeInt(settings.get(i));
    }
    sink.flush();
  }

  
  public synchronized void ping(boolean ack, int payload1, int payload2) throws IOException {
    if (closed) throw new IOException("closed");
    int length = 8;
    byte type = TYPE_PING;
    byte flags = ack ? FLAG_ACK : FLAG_NONE;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    sink.writeInt(payload1);
    sink.writeInt(payload2);
    sink.flush();
  }

  
  public synchronized void goAway(int lastGoodStreamId, ErrorCodeq errorCode, byte[] debugData)
      throws IOException {
    if (closed) throw new IOException("closed");
    if (errorCode.httpCode == -1) throw illegalArgument("errorCode.httpCode == -1");
    int length = 8 + debugData.length;
    byte type = TYPE_GOAWAY;
    byte flags = FLAG_NONE;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    sink.writeInt(lastGoodStreamId);
    sink.writeInt(errorCode.httpCode);
    if (debugData.length > 0) {
      sink.write(debugData);
    }
    sink.flush();
  }

  
  public synchronized void windowUpdate(int streamId, long windowSizeIncrement) throws IOException {
    if (closed) throw new IOException("closed");
    if (windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL) {
      throw illegalArgument("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: %s",
          windowSizeIncrement);
    }
    int length = 4;
    byte type = TYPE_WINDOW_UPDATE;
    byte flags = FLAG_NONE;
    frameHeader(streamId, length, type, flags);
    sink.writeInt((int) windowSizeIncrement);
    sink.flush();
  }

  public void frameHeader(int streamId, int length, byte type, byte flags) throws IOException {
    if (logger.isLoggable(FINE)) logger.fine(frameLog(false, streamId, length, type, flags));
    if (length > maxFrameSize) {
      throw illegalArgument("FRAME_SIZE_ERROR length > %d: %d", maxFrameSize, length);
    }
    if ((streamId & 0x80000000) != 0) throw illegalArgument("reserved bit set: %s", streamId);
    writeMedium(sink, length);
    sink.writeByte(type & 0xff);
    sink.writeByte(flags & 0xff);
    sink.writeInt(streamId & 0x7fffffff);
  }

  @Override public synchronized void close() throws IOException {
    closed = true;
    sink.close();
  }

  private static void writeMedium(BufferedSinkzaqds sink, int i) throws IOException {
    sink.writeByte((i >>> 16) & 0xff);
    sink.writeByte((i >>> 8) & 0xff);
    sink.writeByte(i & 0xff);
  }

  private void writeContinuationFrames(int streamId, long byteCount) throws IOException {
    while (byteCount > 0) {
      int length = (int) Math.min(maxFrameSize, byteCount);
      byteCount -= length;
      frameHeader(streamId, length, TYPE_CONTINUATION, byteCount == 0 ? FLAG_END_HEADERS : 0);
      sink.write(hpackBuffer, length);
    }
  }

  void headers(boolean outFinished, int streamId, List<Headera> headerBlock) throws IOException {
    if (closed) throw new IOException("closed");
    hpackWriter.writeHeaders(headerBlock);

    long byteCount = hpackBuffer.size();
    int length = (int) Math.min(maxFrameSize, byteCount);
    byte type = TYPE_HEADERS;
    byte flags = byteCount == length ? FLAG_END_HEADERS : 0;
    if (outFinished) flags |= FLAG_END_STREAM;
    frameHeader(streamId, length, type, flags);
    sink.write(hpackBuffer, length);

    if (byteCount > length) writeContinuationFrames(streamId, byteCount - length);
  }
}
