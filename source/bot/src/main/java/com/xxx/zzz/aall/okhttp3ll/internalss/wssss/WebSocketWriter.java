
package com.xxx.zzz.aall.okhttp3ll.internalss.wssss;

import java.io.IOException;
import java.util.Random;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Timeoutzaq;


final class WebSocketWriter {
  final boolean isClient;
  final Random random;

  
  final BufferedSinkzaqds sink;
  
  boolean writerClosed;

  final Bufferzaq buffer = new Bufferzaq();
  final FrameSink frameSink = new FrameSink();

  boolean activeWriter;

  final byte[] maskKey;
  final byte[] maskBuffer;

  WebSocketWriter(boolean isClient, BufferedSinkzaqds sink, Random random) {
    if (sink == null) throw new NullPointerException("sink == null");
    if (random == null) throw new NullPointerException("random == null");
    this.isClient = isClient;
    this.sink = sink;
    this.random = random;

    
    maskKey = isClient ? new byte[4] : null;
    maskBuffer = isClient ? new byte[8192] : null;
  }

  
  void writePing(ByteStringzaq payload) throws IOException {
    synchronized (this) {
      writeControlFrameSynchronized(WebSocketProtocol.OPCODE_CONTROL_PING, payload);
    }
  }

  
  void writePong(ByteStringzaq payload) throws IOException {
    synchronized (this) {
      writeControlFrameSynchronized(WebSocketProtocol.OPCODE_CONTROL_PONG, payload);
    }
  }

  
  void writeClose(int code, ByteStringzaq reason) throws IOException {
    ByteStringzaq payload = ByteStringzaq.EMPTY;
    if (code != 0 || reason != null) {
      if (code != 0) {
        WebSocketProtocol.validateCloseCode(code);
      }
      Bufferzaq buffer = new Bufferzaq();
      buffer.writeShort(code);
      if (reason != null) {
        buffer.write(reason);
      }
      payload = buffer.readByteString();
    }

    synchronized (this) {
      try {
        writeControlFrameSynchronized(WebSocketProtocol.OPCODE_CONTROL_CLOSE, payload);
      } finally {
        writerClosed = true;
      }
    }
  }

  private void writeControlFrameSynchronized(int opcode, ByteStringzaq payload) throws IOException {
    assert Thread.holdsLock(this);

    if (writerClosed) throw new IOException("closed");

    int length = payload.size();
    if (length > WebSocketProtocol.PAYLOAD_BYTE_MAX) {
      throw new IllegalArgumentException(
          "Payload size must be less than or equal to " + WebSocketProtocol.PAYLOAD_BYTE_MAX);
    }

    int b0 = WebSocketProtocol.B0_FLAG_FIN | opcode;
    sink.writeByte(b0);

    int b1 = length;
    if (isClient) {
      b1 |= WebSocketProtocol.B1_FLAG_MASK;
      sink.writeByte(b1);

      random.nextBytes(maskKey);
      sink.write(maskKey);

      byte[] bytes = payload.toByteArray();
      WebSocketProtocol.toggleMask(bytes, bytes.length, maskKey, 0);
      sink.write(bytes);
    } else {
      sink.writeByte(b1);
      sink.write(payload);
    }

    sink.flush();
  }

  
  Sinkzaq newMessageSink(int formatOpcode, long contentLength) {
    if (activeWriter) {
      throw new IllegalStateException("Another message writer is active. Did you call close()?");
    }
    activeWriter = true;

    
    frameSink.formatOpcode = formatOpcode;
    frameSink.contentLength = contentLength;
    frameSink.isFirstFrame = true;
    frameSink.closed = false;

    return frameSink;
  }

  void writeMessageFrameSynchronized(int formatOpcode, long byteCount, boolean isFirstFrame,
      boolean isFinal) throws IOException {
    assert Thread.holdsLock(this);

    if (writerClosed) throw new IOException("closed");

    int b0 = isFirstFrame ? formatOpcode : WebSocketProtocol.OPCODE_CONTINUATION;
    if (isFinal) {
      b0 |= WebSocketProtocol.B0_FLAG_FIN;
    }
    sink.writeByte(b0);

    int b1 = 0;
    if (isClient) {
      b1 |= WebSocketProtocol.B1_FLAG_MASK;
    }
    if (byteCount <= WebSocketProtocol.PAYLOAD_BYTE_MAX) {
      b1 |= (int) byteCount;
      sink.writeByte(b1);
    } else if (byteCount <= WebSocketProtocol.PAYLOAD_SHORT_MAX) {
      b1 |= WebSocketProtocol.PAYLOAD_SHORT;
      sink.writeByte(b1);
      sink.writeShort((int) byteCount);
    } else {
      b1 |= WebSocketProtocol.PAYLOAD_LONG;
      sink.writeByte(b1);
      sink.writeLong(byteCount);
    }

    if (isClient) {
      random.nextBytes(maskKey);
      sink.write(maskKey);

      for (long written = 0; written < byteCount; ) {
        int toRead = (int) Math.min(byteCount, maskBuffer.length);
        int read = buffer.read(maskBuffer, 0, toRead);
        if (read == -1) throw new AssertionError();
        WebSocketProtocol.toggleMask(maskBuffer, read, maskKey, written);
        sink.write(maskBuffer, 0, read);
        written += read;
      }
    } else {
      sink.write(buffer, byteCount);
    }

    sink.emit();
  }

  final class FrameSink implements Sinkzaq {
    int formatOpcode;
    long contentLength;
    boolean isFirstFrame;
    boolean closed;

    @Override public void write(Bufferzaq source, long byteCount) throws IOException {
      if (closed) throw new IOException("closed");

      buffer.write(source, byteCount);

      
      boolean deferWrite = isFirstFrame
          && contentLength != -1
          && buffer.size() > contentLength - 8192 ;

      long emitCount = buffer.completeSegmentByteCount();
      if (emitCount > 0 && !deferWrite) {
        synchronized (WebSocketWriter.this) {
          writeMessageFrameSynchronized(formatOpcode, emitCount, isFirstFrame, false );
        }
        isFirstFrame = false;
      }
    }

    @Override public void flush() throws IOException {
      if (closed) throw new IOException("closed");

      synchronized (WebSocketWriter.this) {
        writeMessageFrameSynchronized(formatOpcode, buffer.size(), isFirstFrame, false );
      }
      isFirstFrame = false;
    }

    @Override public Timeoutzaq timeout() {
      return sink.timeout();
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    @Override public void close() throws IOException {
      if (closed) throw new IOException("closed");

      synchronized (WebSocketWriter.this) {
        writeMessageFrameSynchronized(formatOpcode, buffer.size(), isFirstFrame, true );
      }
      closed = true;
      activeWriter = false;
    }
  }
}
