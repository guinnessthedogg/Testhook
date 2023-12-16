
package com.xxx.zzz.aall.okhttp3ll.internalss.wssss;

import static java.lang.Integer.toHexString;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;


final class WebSocketReader {
  public interface FrameCallback {
    void onReadMessage(String text) throws IOException;
    void onReadMessage(ByteStringzaq bytes) throws IOException;
    void onReadPing(ByteStringzaq buffer);
    void onReadPong(ByteStringzaq buffer);
    void onReadClose(int code, String reason);
  }

  final boolean isClient;
  final BufferedSourcezaqdfs source;
  final FrameCallback frameCallback;

  boolean closed;

  
  int opcode;
  long frameLength;
  long frameBytesRead;
  boolean isFinalFrame;
  boolean isControlFrame;
  boolean isMasked;

  final byte[] maskKey = new byte[4];
  final byte[] maskBuffer = new byte[8192];

  WebSocketReader(boolean isClient, BufferedSourcezaqdfs source, FrameCallback frameCallback) {
    if (source == null) throw new NullPointerException("source == null");
    if (frameCallback == null) throw new NullPointerException("frameCallback == null");
    this.isClient = isClient;
    this.source = source;
    this.frameCallback = frameCallback;
  }

  
  void processNextFrame() throws IOException {
    readHeader();
    if (isControlFrame) {
      readControlFrame();
    } else {
      readMessageFrame();
    }
  }

  private void readHeader() throws IOException {
    if (closed) throw new IOException("closed");

    
    int b0;
    long timeoutBefore = source.timeout().timeoutNanos();
    source.timeout().clearTimeout();
    try {
      b0 = source.readByte() & 0xff;
    } finally {
      source.timeout().timeout(timeoutBefore, TimeUnit.NANOSECONDS);
    }

    opcode = b0 & WebSocketProtocol.B0_MASK_OPCODE;
    isFinalFrame = (b0 & WebSocketProtocol.B0_FLAG_FIN) != 0;
    isControlFrame = (b0 & WebSocketProtocol.OPCODE_FLAG_CONTROL) != 0;

    
    if (isControlFrame && !isFinalFrame) {
      throw new ProtocolException("Control frames must be final.");
    }

    boolean reservedFlag1 = (b0 & WebSocketProtocol.B0_FLAG_RSV1) != 0;
    boolean reservedFlag2 = (b0 & WebSocketProtocol.B0_FLAG_RSV2) != 0;
    boolean reservedFlag3 = (b0 & WebSocketProtocol.B0_FLAG_RSV3) != 0;
    if (reservedFlag1 || reservedFlag2 || reservedFlag3) {
      
      throw new ProtocolException("Reserved flags are unsupported.");
    }

    int b1 = source.readByte() & 0xff;

    isMasked = (b1 & WebSocketProtocol.B1_FLAG_MASK) != 0;
    if (isMasked == isClient) {
      
      throw new ProtocolException(isClient
          ? "Server-sent frames must not be masked."
          : "Client-sent frames must be masked.");
    }

    
    frameLength = b1 & WebSocketProtocol.B1_MASK_LENGTH;
    if (frameLength == WebSocketProtocol.PAYLOAD_SHORT) {
      frameLength = source.readShort() & 0xffffL; 
    } else if (frameLength == WebSocketProtocol.PAYLOAD_LONG) {
      frameLength = source.readLong();
      if (frameLength < 0) {
        throw new ProtocolException(
            "Frame length 0x" + Long.toHexString(frameLength) + " > 0x7FFFFFFFFFFFFFFF");
      }
    }
    frameBytesRead = 0;

    if (isControlFrame && frameLength > WebSocketProtocol.PAYLOAD_BYTE_MAX) {
      throw new ProtocolException("Control frame must be less than " + WebSocketProtocol.PAYLOAD_BYTE_MAX + "B.");
    }

    if (isMasked) {
      
      source.readFully(maskKey);
    }
  }

  private void readControlFrame() throws IOException {
    Bufferzaq buffer = new Bufferzaq();
    if (frameBytesRead < frameLength) {
      if (isClient) {
        source.readFully(buffer, frameLength);
      } else {
        while (frameBytesRead < frameLength) {
          int toRead = (int) Math.min(frameLength - frameBytesRead, maskBuffer.length);
          int read = source.read(maskBuffer, 0, toRead);
          if (read == -1) throw new EOFException();
          WebSocketProtocol.toggleMask(maskBuffer, read, maskKey, frameBytesRead);
          buffer.write(maskBuffer, 0, read);
          frameBytesRead += read;
        }
      }
    }

    switch (opcode) {
      case WebSocketProtocol.OPCODE_CONTROL_PING:
        frameCallback.onReadPing(buffer.readByteString());
        break;
      case WebSocketProtocol.OPCODE_CONTROL_PONG:
        frameCallback.onReadPong(buffer.readByteString());
        break;
      case WebSocketProtocol.OPCODE_CONTROL_CLOSE:
        int code = WebSocketProtocol.CLOSE_NO_STATUS_CODE;
        String reason = "";
        long bufferSize = buffer.size();
        if (bufferSize == 1) {
          throw new ProtocolException("Malformed close payload length of 1.");
        } else if (bufferSize != 0) {
          code = buffer.readShort();
          reason = buffer.readUtf8();
          String codeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(code);
          if (codeExceptionMessage != null) throw new ProtocolException(codeExceptionMessage);
        }
        frameCallback.onReadClose(code, reason);
        closed = true;
        break;
      default:
        throw new ProtocolException("Unknown control opcode: " + toHexString(opcode));
    }
  }

  private void readMessageFrame() throws IOException {
    int opcode = this.opcode;
    if (opcode != WebSocketProtocol.OPCODE_TEXT && opcode != WebSocketProtocol.OPCODE_BINARY) {
      throw new ProtocolException("Unknown opcode: " + toHexString(opcode));
    }

    Bufferzaq message = new Bufferzaq();
    readMessage(message);

    if (opcode == WebSocketProtocol.OPCODE_TEXT) {
      frameCallback.onReadMessage(message.readUtf8());
    } else {
      frameCallback.onReadMessage(message.readByteString());
    }
  }

  
  void readUntilNonControlFrame() throws IOException {
    while (!closed) {
      readHeader();
      if (!isControlFrame) {
        break;
      }
      readControlFrame();
    }
  }

  
  private void readMessage(Bufferzaq sink) throws IOException {
    while (true) {
      if (closed) throw new IOException("closed");

      if (frameBytesRead == frameLength) {
        if (isFinalFrame) return; 

        readUntilNonControlFrame();
        if (opcode != WebSocketProtocol.OPCODE_CONTINUATION) {
          throw new ProtocolException("Expected continuation opcode. Got: " + toHexString(opcode));
        }
        if (isFinalFrame && frameLength == 0) {
          return; 
        }
      }

      long toRead = frameLength - frameBytesRead;

      long read;
      if (isMasked) {
        toRead = Math.min(toRead, maskBuffer.length);
        read = source.read(maskBuffer, 0, (int) toRead);
        if (read == -1) throw new EOFException();
        WebSocketProtocol.toggleMask(maskBuffer, read, maskKey, frameBytesRead);
        sink.write(maskBuffer, 0, (int) read);
      } else {
        read = source.read(sink, toRead);
        if (read == -1) throw new EOFException();
      }

      frameBytesRead += read;
    }
  }
}
