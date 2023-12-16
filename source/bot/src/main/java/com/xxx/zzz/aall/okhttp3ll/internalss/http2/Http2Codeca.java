
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.ResponseBodyza;
import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;
import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.connectionss.StreamAllocation;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpCodec;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RequestLine;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine;
import com.xxx.zzz.aall.okhttp3ll.Headersza;
import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.RealResponseBody;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.ForwardingSourcezaq;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;


public final class Http2Codeca implements HttpCodec {
  private static final ByteStringzaq CONNECTION = ByteStringzaq.encodeUtf8("connection");
  private static final ByteStringzaq HOST = ByteStringzaq.encodeUtf8("host");
  private static final ByteStringzaq KEEP_ALIVE = ByteStringzaq.encodeUtf8("keep-alive");
  private static final ByteStringzaq PROXY_CONNECTION = ByteStringzaq.encodeUtf8("proxy-connection");
  private static final ByteStringzaq TRANSFER_ENCODING = ByteStringzaq.encodeUtf8("transfer-encoding");
  private static final ByteStringzaq TE = ByteStringzaq.encodeUtf8("te");
  private static final ByteStringzaq ENCODING = ByteStringzaq.encodeUtf8("encoding");
  private static final ByteStringzaq UPGRADE = ByteStringzaq.encodeUtf8("upgrade");

  private static final List<ByteStringzaq> HTTP_2_SKIPPED_REQUEST_HEADERS = Utilaq.immutableList(
      CONNECTION,
      HOST,
      KEEP_ALIVE,
      PROXY_CONNECTION,
      TE,
      TRANSFER_ENCODING,
      ENCODING,
      UPGRADE,
      Headera.TARGET_METHOD,
      Headera.TARGET_PATH,
      Headera.TARGET_SCHEME,
      Headera.TARGET_AUTHORITY);
  private static final List<ByteStringzaq> HTTP_2_SKIPPED_RESPONSE_HEADERS = Utilaq.immutableList(
      CONNECTION,
      HOST,
      KEEP_ALIVE,
      PROXY_CONNECTION,
      TE,
      TRANSFER_ENCODING,
      ENCODING,
      UPGRADE);

  private final OkHttpClientza client;
  final StreamAllocation streamAllocation;
  private final Http2Connectiona connection;
  private Http2Streama stream;

  public Http2Codeca(
          OkHttpClientza client, StreamAllocation streamAllocation, Http2Connectiona connection) {
    this.client = client;
    this.streamAllocation = streamAllocation;
    this.connection = connection;
  }

  @Override public Sinkzaq createRequestBody(Requestza request, long contentLength) {
    return stream.getSink();
  }

  @Override public void writeRequestHeaders(Requestza request) throws IOException {
    if (stream != null) return;

    boolean hasRequestBody = request.body() != null;
    List<Headera> requestHeaders = http2HeadersList(request);
    stream = connection.newStream(requestHeaders, hasRequestBody);
    stream.readTimeout().timeout(client.readTimeoutMillis(), TimeUnit.MILLISECONDS);
    stream.writeTimeout().timeout(client.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
  }

  @Override public void flushRequest() throws IOException {
    connection.flush();
  }

  @Override public void finishRequest() throws IOException {
    stream.getSink().close();
  }

  @Override public Responseza.Builder readResponseHeaders(boolean expectContinue) throws IOException {
    List<Headera> headers = stream.takeResponseHeaders();
    Responseza.Builder responseBuilder = readHttp2HeadersList(headers);
    if (expectContinue && Internalq.instance.code(responseBuilder) == StatusLine.HTTP_CONTINUE) {
      return null;
    }
    return responseBuilder;
  }

  public static List<Headera> http2HeadersList(Requestza request) {
    Headersza headers = request.headers();
    List<Headera> result = new ArrayList<>(headers.size() + 4);
    result.add(new Headera(Headera.TARGET_METHOD, request.method()));
    result.add(new Headera(Headera.TARGET_PATH, RequestLine.requestPath(request.url())));
    String host = request.header("Host");
    if (host != null) {
      result.add(new Headera(Headera.TARGET_AUTHORITY, host));
    }
    result.add(new Headera(Headera.TARGET_SCHEME, request.url().scheme()));

    for (int i = 0, size = headers.size(); i < size; i++) {

      ByteStringzaq name = ByteStringzaq.encodeUtf8(headers.name(i).toLowerCase(Locale.US));
      if (!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name)) {
        result.add(new Headera(name, headers.value(i)));
      }
    }
    return result;
  }

  
  public static Responseza.Builder readHttp2HeadersList(List<Headera> headerBlock) throws IOException {
    StatusLine statusLine = null;
    Headersza.Builder headersBuilder = new Headersza.Builder();
    for (int i = 0, size = headerBlock.size(); i < size; i++) {
      Headera header = headerBlock.get(i);



      if (header == null) {
        if (statusLine != null && statusLine.code == StatusLine.HTTP_CONTINUE) {
          statusLine = null;
          headersBuilder = new Headersza.Builder();
        }
        continue;
      }

      ByteStringzaq name = header.name;
      String value = header.value.utf8();
      if (name.equals(Headera.RESPONSE_STATUS)) {
        statusLine = StatusLine.parse("HTTP/1.1 " + value);
      } else if (!HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
        Internalq.instance.addLenient(headersBuilder, name.utf8(), value);
      }
    }
    if (statusLine == null) throw new ProtocolException("Expected ':status' header not present");

    return new Responseza.Builder()
        .protocol(Protocolza.HTTP_2)
        .code(statusLine.code)
        .message(statusLine.message)
        .headers(headersBuilder.build());
  }

  @Override public ResponseBodyza openResponseBody(Responseza response) throws IOException {
    Sourcezaq source = new StreamFinishingSource(stream.getSource());
    return new RealResponseBody(response.headers(), Okiozaq.buffer(source));
  }

  @Override public void cancel() {
    if (stream != null) stream.closeLater(ErrorCodeq.CANCEL);
  }

  class StreamFinishingSource extends ForwardingSourcezaq {
    StreamFinishingSource(Sourcezaq delegate) {
      super(delegate);
    }

    @Override public void close() throws IOException {
      streamAllocation.streamFinished(false, Http2Codeca.this);
      super.close();
    }
  }
}
