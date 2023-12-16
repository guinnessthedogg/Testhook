
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import static java.util.logging.Level.FINE;
import static com.xxx.zzz.aall.okioss.ByteStringzaq.EMPTY;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;
import com.xxx.zzz.aall.okioss.Timeoutzaq;


final class Http2Readera implements Closeable {
  static final Logger logger = Logger.getLogger(Http2a.class.getName());

  private final BufferedSourcezaqdfs source;
  private final ContinuationSource continuation;
  private final boolean client;


  final Hpacka.Reader hpackReader;


  Http2Readera(BufferedSourcezaqdfs source, boolean client) {
    this.source = source;
    this.client = client;
    this.continuation = new ContinuationSource(this.source);
    this.hpackReader = new Hpacka.Reader(4096, continuation);
  }

  public void readConnectionPreface(Handler handler) throws IOException {
    if (client) {

      if (!nextFrame(true, handler)) {
        throw Http2a.ioException("Required SETTINGS preface not received");
      }
    } else {

      ByteStringzaq connectionPreface = source.readByteString(Http2a.CONNECTION_PREFACE.size());
      if (logger.isLoggable(FINE)) logger.fine(Utilaq.format("<< CONNECTION %s", connectionPreface.hex()));
      if (!Http2a.CONNECTION_PREFACE.equals(connectionPreface)) {
        throw Http2a.ioException("Expected a connection header but was %s", connectionPreface.utf8());
      }
    }
  }

  public boolean nextFrame(boolean requireSettings, Handler handler) throws IOException {
    try {
      source.require(9);
    } catch (IOException e) {
      return false;
    }












    int length = readMedium(source);
    if (length < 0 || length > Http2a.INITIAL_MAX_FRAME_SIZE) {
      throw Http2a.ioException("FRAME_SIZE_ERROR: %s", length);
    }
    byte type = (byte) (source.readByte() & 0xff);
    if (requireSettings && type != Http2a.TYPE_SETTINGS) {
      throw Http2a.ioException("Expected a SETTINGS frame but was %s", type);
    }
    byte flags = (byte) (source.readByte() & 0xff);
    int streamId = (source.readInt() & 0x7fffffff);
    if (logger.isLoggable(FINE)) logger.fine(Http2a.frameLog(true, streamId, length, type, flags));

    switch (type) {
      case Http2a.TYPE_DATA:
        readData(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_HEADERS:
        readHeaders(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_PRIORITY:
        readPriority(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_RST_STREAM:
        readRstStream(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_SETTINGS:
        readSettings(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_PUSH_PROMISE:
        readPushPromise(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_PING:
        readPing(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_GOAWAY:
        readGoAway(handler, length, flags, streamId);
        break;

      case Http2a.TYPE_WINDOW_UPDATE:
        readWindowUpdate(handler, length, flags, streamId);
        break;

      default:

        source.skip(length);
    }
    return true;
  }

  private void readHeaders(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (streamId == 0) throw Http2a.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0");

    boolean endStream = (flags & Http2a.FLAG_END_STREAM) != 0;

    short padding = (flags & Http2a.FLAG_PADDED) != 0 ? (short) (source.readByte() & 0xff) : 0;

    if ((flags & Http2a.FLAG_PRIORITY) != 0) {
      readPriority(handler, streamId);
      length -= 5;
    }

    length = lengthWithoutPadding(length, flags, padding);

    List<Headera> headerBlock = readHeaderBlock(length, padding, flags, streamId);

    handler.headers(endStream, streamId, -1, headerBlock);
  }

  private List<Headera> readHeaderBlock(int length, short padding, byte flags, int streamId)
      throws IOException {
    continuation.length = continuation.left = length;
    continuation.padding = padding;
    continuation.flags = flags;
    continuation.streamId = streamId;



    hpackReader.readHeaders();
    return hpackReader.getAndResetHeaderList();
  }

  private void readData(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (streamId == 0) throw Http2a.ioException("PROTOCOL_ERROR: TYPE_DATA streamId == 0");


    boolean inFinished = (flags & Http2a.FLAG_END_STREAM) != 0;
    boolean gzipped = (flags & Http2a.FLAG_COMPRESSED) != 0;
    if (gzipped) {
      throw Http2a.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA");
    }

    short padding = (flags & Http2a.FLAG_PADDED) != 0 ? (short) (source.readByte() & 0xff) : 0;
    length = lengthWithoutPadding(length, flags, padding);

    handler.data(inFinished, streamId, source, length);
    source.skip(padding);
  }

  private void readPriority(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (length != 5) throw Http2a.ioException("TYPE_PRIORITY length: %d != 5", length);
    if (streamId == 0) throw Http2a.ioException("TYPE_PRIORITY streamId == 0");
    readPriority(handler, streamId);
  }

  private void readPriority(Handler handler, int streamId) throws IOException {
    int w1 = source.readInt();
    boolean exclusive = (w1 & 0x80000000) != 0;
    int streamDependency = (w1 & 0x7fffffff);
    int weight = (source.readByte() & 0xff) + 1;
    handler.priority(streamId, streamDependency, weight, exclusive);
  }

  private void readRstStream(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (length != 4) throw Http2a.ioException("TYPE_RST_STREAM length: %d != 4", length);
    if (streamId == 0) throw Http2a.ioException("TYPE_RST_STREAM streamId == 0");
    int errorCodeInt = source.readInt();
    ErrorCodeq errorCode = ErrorCodeq.fromHttp2(errorCodeInt);
    if (errorCode == null) {
      throw Http2a.ioException("TYPE_RST_STREAM unexpected error code: %d", errorCodeInt);
    }
    handler.rstStream(streamId, errorCode);
  }

  private void readSettings(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (streamId != 0) throw Http2a.ioException("TYPE_SETTINGS streamId != 0");
    if ((flags & Http2a.FLAG_ACK) != 0) {
      if (length != 0) throw Http2a.ioException("FRAME_SIZE_ERROR ack frame should be empty!");
      handler.ackSettings();
      return;
    }

    if (length % 6 != 0) throw Http2a.ioException("TYPE_SETTINGS length %% 6 != 0: %s", length);
    Settingsua settings = new Settingsua();
    for (int i = 0; i < length; i += 6) {
      short id = source.readShort();
      int value = source.readInt();

      switch (id) {
        case 1:
          break;
        case 2: 
          if (value != 0 && value != 1) {
            throw Http2a.ioException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1");
          }
          break;
        case 3: 
          id = 4; 
          break;
        case 4: 
          id = 7; 
          if (value < 0) {
            throw Http2a.ioException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1");
          }
          break;
        case 5: 
          if (value < Http2a.INITIAL_MAX_FRAME_SIZE || value > 16777215) {
            throw Http2a.ioException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s", value);
          }
          break;
        case 6: 
          break; 
        default:
          break; 
      }
      settings.set(id, value);
    }
    handler.settings(false, settings);
  }

  private void readPushPromise(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (streamId == 0) {
      throw Http2a.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0");
    }
    short padding = (flags & Http2a.FLAG_PADDED) != 0 ? (short) (source.readByte() & 0xff) : 0;
    int promisedStreamId = source.readInt() & 0x7fffffff;
    length -= 4; 
    length = lengthWithoutPadding(length, flags, padding);
    List<Headera> headerBlock = readHeaderBlock(length, padding, flags, streamId);
    handler.pushPromise(streamId, promisedStreamId, headerBlock);
  }

  private void readPing(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (length != 8) throw Http2a.ioException("TYPE_PING length != 8: %s", length);
    if (streamId != 0) throw Http2a.ioException("TYPE_PING streamId != 0");
    int payload1 = source.readInt();
    int payload2 = source.readInt();
    boolean ack = (flags & Http2a.FLAG_ACK) != 0;
    handler.ping(ack, payload1, payload2);
  }

  private void readGoAway(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (length < 8) throw Http2a.ioException("TYPE_GOAWAY length < 8: %s", length);
    if (streamId != 0) throw Http2a.ioException("TYPE_GOAWAY streamId != 0");
    int lastStreamId = source.readInt();
    int errorCodeInt = source.readInt();
    int opaqueDataLength = length - 8;
    ErrorCodeq errorCode = ErrorCodeq.fromHttp2(errorCodeInt);
    if (errorCode == null) {
      throw Http2a.ioException("TYPE_GOAWAY unexpected error code: %d", errorCodeInt);
    }
    ByteStringzaq debugData = EMPTY;
    if (opaqueDataLength > 0) { 
      debugData = source.readByteString(opaqueDataLength);
    }
    handler.goAway(lastStreamId, errorCode, debugData);
  }

  private void readWindowUpdate(Handler handler, int length, byte flags, int streamId)
      throws IOException {
    if (length != 4) throw Http2a.ioException("TYPE_WINDOW_UPDATE length !=4: %s", length);
    long increment = (source.readInt() & 0x7fffffffL);
    if (increment == 0) throw Http2a.ioException("windowSizeIncrement was 0", increment);
    handler.windowUpdate(streamId, increment);
  }

  @Override public void close() throws IOException {
    source.close();
  }

  
  static final class ContinuationSource implements Sourcezaq {
    private final BufferedSourcezaqdfs source;

    int length;
    byte flags;
    int streamId;

    int left;
    short padding;

    ContinuationSource(BufferedSourcezaqdfs source) {
      this.source = source;
    }

    @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
      while (left == 0) {
        source.skip(padding);
        padding = 0;
        if ((flags & Http2a.FLAG_END_HEADERS) != 0) return -1;
        readContinuationHeader();
        
      }

      long read = source.read(sink, Math.min(byteCount, left));
      if (read == -1) return -1;
      left -= read;
      return read;
    }

    @Override public Timeoutzaq timeout() {
      return source.timeout();
    }

    @Override public void close() throws IOException {
    }

    private void readContinuationHeader() throws IOException {
      int previousStreamId = streamId;

      length = left = readMedium(source);
      byte type = (byte) (source.readByte() & 0xff);
      flags = (byte) (source.readByte() & 0xff);
      if (logger.isLoggable(FINE)) logger.fine(Http2a.frameLog(true, streamId, length, type, flags));
      streamId = (source.readInt() & 0x7fffffff);
      if (type != Http2a.TYPE_CONTINUATION) throw Http2a.ioException("%s != TYPE_CONTINUATION", type);
      if (streamId != previousStreamId) throw Http2a.ioException("TYPE_CONTINUATION streamId changed");
    }
  }

  static int readMedium(BufferedSourcezaqdfs source) throws IOException {
    return (source.readByte() & 0xff) << 16
        | (source.readByte() & 0xff) << 8
        | (source.readByte() & 0xff);
  }

  static int lengthWithoutPadding(int length, byte flags, short padding)
      throws IOException {
    if ((flags & Http2a.FLAG_PADDED) != 0) length--; 
    if (padding > length) {
      throw Http2a.ioException("PROTOCOL_ERROR padding %s > remaining length %s", padding, length);
    }
    return (short) (length - padding);
  }

  interface Handler {
    void data(boolean inFinished, int streamId, BufferedSourcezaqdfs source, int length)
        throws IOException;

    
    void headers(boolean inFinished, int streamId, int associatedStreamId,
        List<Headera> headerBlock);

    void rstStream(int streamId, ErrorCodeq errorCode);

    void settings(boolean clearPrevious, Settingsua settings);

    
    void ackSettings();

    
    void ping(boolean ack, int payload1, int payload2);

    
    void goAway(int lastGoodStreamId, ErrorCodeq errorCode, ByteStringzaq debugData);

    
    void windowUpdate(int streamId, long windowSizeIncrement);

    
    void priority(int streamId, int streamDependency, int weight, boolean exclusive);

    
    void pushPromise(int streamId, int promisedStreamId, List<Headera> requestHeaders)
        throws IOException;

    
    void alternateService(int streamId, String origin, ByteStringzaq protocol, String host, int port,
                          long maxAge);
  }
}
