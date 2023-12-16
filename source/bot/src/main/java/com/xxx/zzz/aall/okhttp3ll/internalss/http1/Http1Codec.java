
package com.xxx.zzz.aall.okhttp3ll.internalss.http1;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;

import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.ResponseBodyza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.RealConnection;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpCodec;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpHeaders;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RealResponseBody;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RequestLine;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ForwardingTimeoutzaq;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;
import com.xxx.zzz.aall.okioss.Timeoutzaq;


public final class Http1Codec implements HttpCodec {
  private static final int STATE_IDLE = 0;
  private static final int STATE_OPEN_REQUEST_BODY = 1;
  private static final int STATE_WRITING_REQUEST_BODY = 2;
  private static final int STATE_READ_RESPONSE_HEADERS = 3;
  private static final int STATE_OPEN_RESPONSE_BODY = 4;
  private static final int STATE_READING_RESPONSE_BODY = 5;
  private static final int STATE_CLOSED = 6;

  
  final OkHttpClientza client;
  
  final StreamAllocation streamAllocation;

  final BufferedSourcezaqdfs source;
  final BufferedSinkzaqds sink;
  int state = STATE_IDLE;

  public Http1Codec(OkHttpClientza client, StreamAllocation streamAllocation, BufferedSourcezaqdfs source,
                    BufferedSinkzaqds sink) {
    this.client = client;
    this.streamAllocation = streamAllocation;
    this.source = source;
    this.sink = sink;
  }

  @Override public Sinkzaq createRequestBody(Requestza request, long contentLength) {
    if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {

      return newChunkedSink();
    }

    if (contentLength != -1) {

      return newFixedLengthSink(contentLength);
    }

    throw new IllegalStateException(
        "Cannot stream a request body without chunked encoding or a known content length!");
  }

  @Override public void cancel() {
    RealConnection connection = streamAllocation.connection();
    if (connection != null) connection.cancel();
  }

  
  @Override public void writeRequestHeaders(Requestza request) throws IOException {
    String requestLine = RequestLine.get(
        request, streamAllocation.connection().route().proxy().type());
    writeRequest(request.headers(), requestLine);
  }

  @Override public ResponseBodyza openResponseBody(Responseza response) throws IOException {
    Sourcezaq source = getTransferStream(response);
    return new RealResponseBody(response.headers(), Okiozaq.buffer(source));
  }

  private Sourcezaq getTransferStream(Responseza response) throws IOException {
    if (!HttpHeaders.hasBody(response)) {
      return newFixedLengthSource(0);
    }

    if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
      return newChunkedSource(response.request().url());
    }

    long contentLength = HttpHeaders.contentLength(response);
    if (contentLength != -1) {
      return newFixedLengthSource(contentLength);
    }




    return newUnknownLengthSource();
  }

  
  public boolean isClosed() {
    return state == STATE_CLOSED;
  }

  @Override public void flushRequest() throws IOException {
    sink.flush();
  }

  @Override public void finishRequest() throws IOException {
    sink.flush();
  }

  
  public void writeRequest(Headersza headers, String requestLine) throws IOException {
    if (state != STATE_IDLE) throw new IllegalStateException("state: " + state);
    sink.writeUtf8(requestLine).writeUtf8("\r\n");
    for (int i = 0, size = headers.size(); i < size; i++) {
      sink.writeUtf8(headers.name(i))
          .writeUtf8(": ")
          .writeUtf8(headers.value(i))
          .writeUtf8("\r\n");
    }
    sink.writeUtf8("\r\n");
    state = STATE_OPEN_REQUEST_BODY;
  }

  @Override public Responseza.Builder readResponseHeaders(boolean expectContinue) throws IOException {
    if (state != STATE_OPEN_REQUEST_BODY && state != STATE_READ_RESPONSE_HEADERS) {
      throw new IllegalStateException("state: " + state);
    }

    try {
      StatusLine statusLine = StatusLine.parse(source.readUtf8LineStrict());

      Responseza.Builder responseBuilder = new Responseza.Builder()
          .protocol(statusLine.protocol)
          .code(statusLine.code)
          .message(statusLine.message)
          .headers(readHeaders());

      if (expectContinue && statusLine.code == StatusLine.HTTP_CONTINUE) {
        return null;
      }

      state = STATE_OPEN_RESPONSE_BODY;
      return responseBuilder;
    } catch (EOFException e) {

      IOException exception = new IOException("unexpected end of stream on " + streamAllocation);
      exception.initCause(e);
      throw exception;
    }
  }

  
  public Headersza readHeaders() throws IOException {
    Headersza.Builder headers = new Headersza.Builder();

    for (String line; (line = source.readUtf8LineStrict()).length() != 0; ) {
      Internalq.instance.addLenient(headers, line);
    }
    return headers.build();
  }

  public Sinkzaq newChunkedSink() {
    if (state != STATE_OPEN_REQUEST_BODY) throw new IllegalStateException("state: " + state);
    state = STATE_WRITING_REQUEST_BODY;
    return new ChunkedSink();
  }

  public Sinkzaq newFixedLengthSink(long contentLength) {
    if (state != STATE_OPEN_REQUEST_BODY) throw new IllegalStateException("state: " + state);
    state = STATE_WRITING_REQUEST_BODY;
    return new FixedLengthSink(contentLength);
  }

  public Sourcezaq newFixedLengthSource(long length) throws IOException {
    if (state != STATE_OPEN_RESPONSE_BODY) throw new IllegalStateException("state: " + state);
    state = STATE_READING_RESPONSE_BODY;
    return new FixedLengthSource(length);
  }

  public Sourcezaq newChunkedSource(HttpUrlza url) throws IOException {
    if (state != STATE_OPEN_RESPONSE_BODY) throw new IllegalStateException("state: " + state);
    state = STATE_READING_RESPONSE_BODY;
    return new ChunkedSource(url);
  }

  public Sourcezaq newUnknownLengthSource() throws IOException {
    if (state != STATE_OPEN_RESPONSE_BODY) throw new IllegalStateException("state: " + state);
    if (streamAllocation == null) throw new IllegalStateException("streamAllocation == null");
    state = STATE_READING_RESPONSE_BODY;
    streamAllocation.noNewStreams();
    return new UnknownLengthSource();
  }

  
  void detachTimeout(ForwardingTimeoutzaq timeout) {
    Timeoutzaq oldDelegate = timeout.delegate();
    timeout.setDelegate(Timeoutzaq.NONE);
    oldDelegate.clearDeadline();
    oldDelegate.clearTimeout();
  }

  
  private final class FixedLengthSink implements Sinkzaq {
    private final ForwardingTimeoutzaq timeout = new ForwardingTimeoutzaq(sink.timeout());
    private boolean closed;
    private long bytesRemaining;

    FixedLengthSink(long bytesRemaining) {
      this.bytesRemaining = bytesRemaining;
    }

    @Override public Timeoutzaq timeout() {
      return timeout;
    }

    @Override public void write(Bufferzaq source, long byteCount) throws IOException {
      if (closed) throw new IllegalStateException("closed");
      Utilaq.checkOffsetAndCount(source.size(), 0, byteCount);
      if (byteCount > bytesRemaining) {
        throw new ProtocolException("expected " + bytesRemaining
            + " bytes but received " + byteCount);
      }
      sink.write(source, byteCount);
      bytesRemaining -= byteCount;
    }

    @Override public void flush() throws IOException {
      if (closed) return;
      sink.flush();
    }

    @Override public void close() throws IOException {
      if (closed) return;
      closed = true;
      if (bytesRemaining > 0) throw new ProtocolException("unexpected end of stream");
      detachTimeout(timeout);
      state = STATE_READ_RESPONSE_HEADERS;
    }
  }

  
  private final class ChunkedSink implements Sinkzaq {
    private final ForwardingTimeoutzaq timeout = new ForwardingTimeoutzaq(sink.timeout());
    private boolean closed;

    ChunkedSink() {
    }

    @Override public Timeoutzaq timeout() {
      return timeout;
    }

    @Override public void write(Bufferzaq source, long byteCount) throws IOException {
      if (closed) throw new IllegalStateException("closed");
      if (byteCount == 0) return;

      sink.writeHexadecimalUnsignedLong(byteCount);
      sink.writeUtf8("\r\n");
      sink.write(source, byteCount);
      sink.writeUtf8("\r\n");
    }

    @Override public synchronized void flush() throws IOException {
      if (closed) return;
      sink.flush();
    }

    @Override public synchronized void close() throws IOException {
      if (closed) return;
      closed = true;
      sink.writeUtf8("0\r\n\r\n");
      detachTimeout(timeout);
      state = STATE_READ_RESPONSE_HEADERS;
    }
  }

  private abstract class AbstractSource implements Sourcezaq {
    protected final ForwardingTimeoutzaq timeout = new ForwardingTimeoutzaq(source.timeout());
    protected boolean closed;

    @Override public Timeoutzaq timeout() {
      return timeout;
    }

    
    protected final void endOfInput(boolean reuseConnection) throws IOException {
      if (state == STATE_CLOSED) return;
      if (state != STATE_READING_RESPONSE_BODY) throw new IllegalStateException("state: " + state);

      detachTimeout(timeout);

      state = STATE_CLOSED;
      if (streamAllocation != null) {
        streamAllocation.streamFinished(!reuseConnection, Http1Codec.this);
      }
    }
  }

  
  private class FixedLengthSource extends AbstractSource {
    private long bytesRemaining;

    FixedLengthSource(long length) throws IOException {
      bytesRemaining = length;
      if (bytesRemaining == 0) {
        endOfInput(true);
      }
    }

    @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
      if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      if (closed) throw new IllegalStateException("closed");
      if (bytesRemaining == 0) return -1;

      long read = source.read(sink, Math.min(bytesRemaining, byteCount));
      if (read == -1) {
        endOfInput(false);
        throw new ProtocolException("unexpected end of stream");
      }

      bytesRemaining -= read;
      if (bytesRemaining == 0) {
        endOfInput(true);
      }
      return read;
    }

    @Override public void close() throws IOException {
      if (closed) return;

      if (bytesRemaining != 0 && !Utilaq.discard(this, DISCARD_STREAM_TIMEOUT_MILLIS, MILLISECONDS)) {
        endOfInput(false);
      }

      closed = true;
    }
  }

  
  private class ChunkedSource extends AbstractSource {
    private static final long NO_CHUNK_YET = -1L;
    private final HttpUrlza url;
    private long bytesRemainingInChunk = NO_CHUNK_YET;
    private boolean hasMoreChunks = true;

    ChunkedSource(HttpUrlza url) {
      this.url = url;
    }

    @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
      if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      if (closed) throw new IllegalStateException("closed");
      if (!hasMoreChunks) return -1;

      if (bytesRemainingInChunk == 0 || bytesRemainingInChunk == NO_CHUNK_YET) {
        readChunkSize();
        if (!hasMoreChunks) return -1;
      }

      long read = source.read(sink, Math.min(byteCount, bytesRemainingInChunk));
      if (read == -1) {
        endOfInput(false);
        throw new ProtocolException("unexpected end of stream");
      }
      bytesRemainingInChunk -= read;
      return read;
    }

    private void readChunkSize() throws IOException {

      if (bytesRemainingInChunk != NO_CHUNK_YET) {
        source.readUtf8LineStrict();
      }
      try {
        bytesRemainingInChunk = source.readHexadecimalUnsignedLong();
        String extensions = source.readUtf8LineStrict().trim();
        if (bytesRemainingInChunk < 0 || (!extensions.isEmpty() && !extensions.startsWith(";"))) {
          throw new ProtocolException("expected chunk size and optional extensions but was \""
              + bytesRemainingInChunk + extensions + "\"");
        }
      } catch (NumberFormatException e) {
        throw new ProtocolException(e.getMessage());
      }
      if (bytesRemainingInChunk == 0L) {
        hasMoreChunks = false;
        HttpHeaders.receiveHeaders(client.cookieJar(), url, readHeaders());
        endOfInput(true);
      }
    }

    @Override public void close() throws IOException {
      if (closed) return;
      if (hasMoreChunks && !Utilaq.discard(this, DISCARD_STREAM_TIMEOUT_MILLIS, MILLISECONDS)) {
        endOfInput(false);
      }
      closed = true;
    }
  }

  
  private class UnknownLengthSource extends AbstractSource {
    private boolean inputExhausted;

    UnknownLengthSource() {
    }

    @Override public long read(Bufferzaq sink, long byteCount)
        throws IOException {
      if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      if (closed) throw new IllegalStateException("closed");
      if (inputExhausted) return -1;

      long read = source.read(sink, byteCount);
      if (read == -1) {
        inputExhausted = true;
        endOfInput(true);
        return -1;
      }
      return read;
    }

    @Override public void close() throws IOException {
      if (closed) return;
      if (!inputExhausted) {
        endOfInput(false);
      }
      closed = true;
    }
  }
}
