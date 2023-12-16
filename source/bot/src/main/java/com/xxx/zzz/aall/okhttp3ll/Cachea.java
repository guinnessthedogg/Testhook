
package com.xxx.zzz.aall.okhttp3ll;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.CacheRequestq;
import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.CacheStrategyq;
import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.DiskLruCacheq;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpHeaders;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.HttpMethod;
import com.xxx.zzz.aall.okhttp3ll.internalss.httpnn.StatusLine;
import com.xxx.zzz.aall.okhttp3ll.internalss.ioss.FileSystema;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okhttp3ll.internalss.cachenn.InternalCacheq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.ForwardingSinkzaq;
import com.xxx.zzz.aall.okioss.ForwardingSourcezaq;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;


public final class Cachea implements Closeable, Flushable {
  private static final int VERSION = 201105;
  private static final int ENTRY_METADATA = 0;
  private static final int ENTRY_BODY = 1;
  private static final int ENTRY_COUNT = 2;

  final InternalCacheq internalCache = new InternalCacheq() {
    @Override public Responseza get(Requestza request) throws IOException {
      return Cachea.this.get(request);
    }

    @Override public CacheRequestq put(Responseza response) throws IOException {
      return Cachea.this.put(response);
    }

    @Override public void remove(Requestza request) throws IOException {
      Cachea.this.remove(request);
    }

    @Override public void update(Responseza cached, Responseza network) {
      Cachea.this.update(cached, network);
    }

    @Override public void trackConditionalCacheHit() {
      Cachea.this.trackConditionalCacheHit();
    }

    @Override public void trackResponse(CacheStrategyq cacheStrategy) {
      Cachea.this.trackResponse(cacheStrategy);
    }
  };

  final DiskLruCacheq cache;


  int writeSuccessCount;
  int writeAbortCount;
  private int networkCount;
  private int hitCount;
  private int requestCount;

  public Cachea(File directory, long maxSize) {
    this(directory, maxSize, FileSystema.SYSTEM);
  }

  Cachea(File directory, long maxSize, FileSystema fileSystem) {
    this.cache = DiskLruCacheq.create(fileSystem, directory, VERSION, ENTRY_COUNT, maxSize);
  }

  public static String key(HttpUrlza url) {
    return ByteStringzaq.encodeUtf8(url.toString()).md5().hex();
  }

  @Nullableq
  Responseza get(Requestza request) {
    String key = key(request.url());
    DiskLruCacheq.Snapshot snapshot;
    Entry entry;
    try {
      snapshot = cache.get(key);
      if (snapshot == null) {
        return null;
      }
    } catch (IOException e) {
      
      return null;
    }

    try {
      entry = new Entry(snapshot.getSource(ENTRY_METADATA));
    } catch (IOException e) {
      Utilaq.closeQuietly(snapshot);
      return null;
    }

    Responseza response = entry.response(snapshot);

    if (!entry.matches(request, response)) {
      Utilaq.closeQuietly(response.body());
      return null;
    }

    return response;
  }

  @Nullableq
  CacheRequestq put(Responseza response) {
    String requestMethod = response.request().method();

    if (HttpMethod.invalidatesCache(response.request().method())) {
      try {
        remove(response.request());
      } catch (IOException ignored) {
        
      }
      return null;
    }
    if (!requestMethod.equals("GET")) {
      
      
      
      return null;
    }

    if (HttpHeaders.hasVaryAll(response)) {
      return null;
    }

    Entry entry = new Entry(response);
    DiskLruCacheq.Editor editor = null;
    try {
      editor = cache.edit(key(response.request().url()));
      if (editor == null) {
        return null;
      }
      entry.writeTo(editor);
      return new CacheRequestImpl(editor);
    } catch (IOException e) {
      abortQuietly(editor);
      return null;
    }
  }

  void remove(Requestza request) throws IOException {
    cache.remove(key(request.url()));
  }

  void update(Responseza cached, Responseza network) {
    Entry entry = new Entry(network);
    DiskLruCacheq.Snapshot snapshot = ((CacheResponseBody) cached.body()).snapshot;
    DiskLruCacheq.Editor editor = null;
    try {
      editor = snapshot.edit(); 
      if (editor != null) {
        entry.writeTo(editor);
        editor.commit();
      }
    } catch (IOException e) {
      abortQuietly(editor);
    }
  }

  private void abortQuietly(@Nullableq DiskLruCacheq.Editor editor) {
    
    try {
      if (editor != null) {
        editor.abort();
      }
    } catch (IOException ignored) {
    }
  }


  public void initialize() throws IOException {
    cache.initialize();
  }


  public void delete() throws IOException {
    cache.delete();
  }


  public void evictAll() throws IOException {
    cache.evictAll();
  }


  public Iterator<String> urls() throws IOException {
    return new Iterator<String>() {
      final Iterator<DiskLruCacheq.Snapshot> delegate = cache.snapshots();

      @Nullableq
      String nextUrl;
      boolean canRemove;

      @Override public boolean hasNext() {
        if (nextUrl != null) return true;

        canRemove = false; 
        while (delegate.hasNext()) {
          DiskLruCacheq.Snapshot snapshot = delegate.next();
          try {
            BufferedSourcezaqdfs metadata = Okiozaq.buffer(snapshot.getSource(ENTRY_METADATA));
            nextUrl = metadata.readUtf8LineStrict();
            return true;
          } catch (IOException ignored) {
            
            
          } finally {
            snapshot.close();
          }
        }

        return false;
      }

      @Override public String next() {
        if (!hasNext()) throw new NoSuchElementException();
        String result = nextUrl;
        nextUrl = null;
        canRemove = true;
        return result;
      }

      @Override public void remove() {
        if (!canRemove) throw new IllegalStateException("remove() before next()");
        delegate.remove();
      }
    };
  }

  public synchronized int writeAbortCount() {
    return writeAbortCount;
  }

  public synchronized int writeSuccessCount() {
    return writeSuccessCount;
  }

  public long size() throws IOException {
    return cache.size();
  }

  public long maxSize() {
    return cache.getMaxSize();
  }

  @Override public void flush() throws IOException {
    cache.flush();
  }

  @Override public void close() throws IOException {
    cache.close();
  }

  public File directory() {
    return cache.getDirectory();
  }

  public boolean isClosed() {
    return cache.isClosed();
  }

  synchronized void trackResponse(CacheStrategyq cacheStrategy) {
    requestCount++;

    if (cacheStrategy.networkRequest != null) {
      
      networkCount++;
    } else if (cacheStrategy.cacheResponse != null) {
      
      hitCount++;
    }
  }

  synchronized void trackConditionalCacheHit() {
    hitCount++;
  }

  public synchronized int networkCount() {
    return networkCount;
  }

  public synchronized int hitCount() {
    return hitCount;
  }

  public synchronized int requestCount() {
    return requestCount;
  }

  private final class CacheRequestImpl implements CacheRequestq {
    private final DiskLruCacheq.Editor editor;
    private Sinkzaq cacheOut;
    private Sinkzaq body;
    boolean done;

    CacheRequestImpl(final DiskLruCacheq.Editor editor) {
      this.editor = editor;
      this.cacheOut = editor.newSink(ENTRY_BODY);
      this.body = new ForwardingSinkzaq(cacheOut) {
        @Override public void close() throws IOException {
          synchronized (Cachea.this) {
            if (done) {
              return;
            }
            done = true;
            writeSuccessCount++;
          }
          super.close();
          editor.commit();
        }
      };
    }

    @Override public void abort() {
      synchronized (Cachea.this) {
        if (done) {
          return;
        }
        done = true;
        writeAbortCount++;
      }
      Utilaq.closeQuietly(cacheOut);
      try {
        editor.abort();
      } catch (IOException ignored) {
      }
    }

    @Override public Sinkzaq body() {
      return body;
    }
  }

  private static final class Entry {

    private static final String SENT_MILLIS = Platformq.get().getPrefix() + "-Sent-Millis";


    private static final String RECEIVED_MILLIS = Platformq.get().getPrefix() + "-Received-Millis";

    private final String url;
    private final Headersza varyHeaders;
    private final String requestMethod;
    private final Protocolza protocol;
    private final int code;
    private final String message;
    private final Headersza responseHeaders;
    private final @Nullableq
    Handshakeza handshake;
    private final long sentRequestMillis;
    private final long receivedResponseMillis;


    Entry(Sourcezaq in) throws IOException {
      try {
        BufferedSourcezaqdfs source = Okiozaq.buffer(in);
        url = source.readUtf8LineStrict();
        requestMethod = source.readUtf8LineStrict();
        Headersza.Builder varyHeadersBuilder = new Headersza.Builder();
        int varyRequestHeaderLineCount = readInt(source);
        for (int i = 0; i < varyRequestHeaderLineCount; i++) {
          varyHeadersBuilder.addLenient(source.readUtf8LineStrict());
        }
        varyHeaders = varyHeadersBuilder.build();

        StatusLine statusLine = StatusLine.parse(source.readUtf8LineStrict());
        protocol = statusLine.protocol;
        code = statusLine.code;
        message = statusLine.message;
        Headersza.Builder responseHeadersBuilder = new Headersza.Builder();
        int responseHeaderLineCount = readInt(source);
        for (int i = 0; i < responseHeaderLineCount; i++) {
          responseHeadersBuilder.addLenient(source.readUtf8LineStrict());
        }
        String sendRequestMillisString = responseHeadersBuilder.get(SENT_MILLIS);
        String receivedResponseMillisString = responseHeadersBuilder.get(RECEIVED_MILLIS);
        responseHeadersBuilder.removeAll(SENT_MILLIS);
        responseHeadersBuilder.removeAll(RECEIVED_MILLIS);
        sentRequestMillis = sendRequestMillisString != null
            ? Long.parseLong(sendRequestMillisString)
            : 0L;
        receivedResponseMillis = receivedResponseMillisString != null
            ? Long.parseLong(receivedResponseMillisString)
            : 0L;
        responseHeaders = responseHeadersBuilder.build();

        if (isHttps()) {
          String blank = source.readUtf8LineStrict();
          if (blank.length() > 0) {
            throw new IOException("expected \"\" but was \"" + blank + "\"");
          }
          String cipherSuiteString = source.readUtf8LineStrict();
          CipherSuiteza cipherSuite = CipherSuiteza.forJavaName(cipherSuiteString);
          List<Certificate> peerCertificates = readCertificateList(source);
          List<Certificate> localCertificates = readCertificateList(source);
          TlsVersionza tlsVersion = !source.exhausted()
              ? TlsVersionza.forJavaName(source.readUtf8LineStrict())
              : TlsVersionza.SSL_3_0;
          handshake = Handshakeza.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
        } else {
          handshake = null;
        }
      } finally {
        in.close();
      }
    }

    Entry(Responseza response) {
      this.url = response.request().url().toString();
      this.varyHeaders = HttpHeaders.varyHeaders(response);
      this.requestMethod = response.request().method();
      this.protocol = response.protocol();
      this.code = response.code();
      this.message = response.message();
      this.responseHeaders = response.headers();
      this.handshake = response.handshake();
      this.sentRequestMillis = response.sentRequestAtMillis();
      this.receivedResponseMillis = response.receivedResponseAtMillis();
    }

    public void writeTo(DiskLruCacheq.Editor editor) throws IOException {
      BufferedSinkzaqds sink = Okiozaq.buffer(editor.newSink(ENTRY_METADATA));

      sink.writeUtf8(url)
          .writeByte('\n');
      sink.writeUtf8(requestMethod)
          .writeByte('\n');
      sink.writeDecimalLong(varyHeaders.size())
          .writeByte('\n');
      for (int i = 0, size = varyHeaders.size(); i < size; i++) {
        sink.writeUtf8(varyHeaders.name(i))
            .writeUtf8(": ")
            .writeUtf8(varyHeaders.value(i))
            .writeByte('\n');
      }

      sink.writeUtf8(new StatusLine(protocol, code, message).toString())
          .writeByte('\n');
      sink.writeDecimalLong(responseHeaders.size() + 2)
          .writeByte('\n');
      for (int i = 0, size = responseHeaders.size(); i < size; i++) {
        sink.writeUtf8(responseHeaders.name(i))
            .writeUtf8(": ")
            .writeUtf8(responseHeaders.value(i))
            .writeByte('\n');
      }
      sink.writeUtf8(SENT_MILLIS)
          .writeUtf8(": ")
          .writeDecimalLong(sentRequestMillis)
          .writeByte('\n');
      sink.writeUtf8(RECEIVED_MILLIS)
          .writeUtf8(": ")
          .writeDecimalLong(receivedResponseMillis)
          .writeByte('\n');

      if (isHttps()) {
        sink.writeByte('\n');
        sink.writeUtf8(handshake.cipherSuite().javaName())
            .writeByte('\n');
        writeCertList(sink, handshake.peerCertificates());
        writeCertList(sink, handshake.localCertificates());
        sink.writeUtf8(handshake.tlsVersion().javaName()).writeByte('\n');
      }
      sink.close();
    }

    private boolean isHttps() {
      return url.startsWith("https://");
    }

    private List<Certificate> readCertificateList(BufferedSourcezaqdfs source) throws IOException {
      int length = readInt(source);
      if (length == -1) return Collections.emptyList(); 

      try {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        List<Certificate> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
          String line = source.readUtf8LineStrict();
          Bufferzaq bytes = new Bufferzaq();
          bytes.write(ByteStringzaq.decodeBase64(line));
          result.add(certificateFactory.generateCertificate(bytes.inputStream()));
        }
        return result;
      } catch (CertificateException e) {
        throw new IOException(e.getMessage());
      }
    }

    private void writeCertList(BufferedSinkzaqds sink, List<Certificate> certificates)
        throws IOException {
      try {
        sink.writeDecimalLong(certificates.size())
            .writeByte('\n');
        for (int i = 0, size = certificates.size(); i < size; i++) {
          byte[] bytes = certificates.get(i).getEncoded();
          String line = ByteStringzaq.of(bytes).base64();
          sink.writeUtf8(line)
              .writeByte('\n');
        }
      } catch (CertificateEncodingException e) {
        throw new IOException(e.getMessage());
      }
    }

    public boolean matches(Requestza request, Responseza response) {
      return url.equals(request.url().toString())
          && requestMethod.equals(request.method())
          && HttpHeaders.varyMatches(response, varyHeaders, request);
    }

    public Responseza response(DiskLruCacheq.Snapshot snapshot) {
      String contentType = responseHeaders.get("Content-Type");
      String contentLength = responseHeaders.get("Content-Length");
      Requestza cacheRequest = new Requestza.Builder()
          .url(url)
          .method(requestMethod, null)
          .headers(varyHeaders)
          .build();
      return new Responseza.Builder()
          .request(cacheRequest)
          .protocol(protocol)
          .code(code)
          .message(message)
          .headers(responseHeaders)
          .body(new CacheResponseBody(snapshot, contentType, contentLength))
          .handshake(handshake)
          .sentRequestAtMillis(sentRequestMillis)
          .receivedResponseAtMillis(receivedResponseMillis)
          .build();
    }
  }

  static int readInt(BufferedSourcezaqdfs source) throws IOException {
    try {
      long result = source.readDecimalLong();
      String line = source.readUtf8LineStrict();
      if (result < 0 || result > Integer.MAX_VALUE || !line.isEmpty()) {
        throw new IOException("expected an int but was \"" + result + line + "\"");
      }
      return (int) result;
    } catch (NumberFormatException e) {
      throw new IOException(e.getMessage());
    }
  }

  private static class CacheResponseBody extends ResponseBodyza {
    final DiskLruCacheq.Snapshot snapshot;
    private final BufferedSourcezaqdfs bodySource;
    private final @Nullableq
    String contentType;
    private final @Nullableq
    String contentLength;

    CacheResponseBody(final DiskLruCacheq.Snapshot snapshot,
        String contentType, String contentLength) {
      this.snapshot = snapshot;
      this.contentType = contentType;
      this.contentLength = contentLength;

      Sourcezaq source = snapshot.getSource(ENTRY_BODY);
      bodySource = Okiozaq.buffer(new ForwardingSourcezaq(source) {
        @Override public void close() throws IOException {
          snapshot.close();
          super.close();
        }
      });
    }

    @Override public MediaTypeza contentType() {
      return contentType != null ? MediaTypeza.parse(contentType) : null;
    }

    @Override public long contentLength() {
      try {
        return contentLength != null ? Long.parseLong(contentLength) : -1;
      } catch (NumberFormatException e) {
        return -1;
      }
    }

    @Override public BufferedSourcezaqdfs source() {
      return bodySource;
    }
  }
}
