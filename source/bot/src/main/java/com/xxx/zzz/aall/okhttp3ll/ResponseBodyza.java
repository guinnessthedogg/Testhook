
package com.xxx.zzz.aall.okhttp3ll;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.UTF_8;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;


public abstract class ResponseBodyza implements Closeable {

  private Reader reader;

  public abstract @Nullableq
  MediaTypeza contentType();


  public abstract long contentLength();

  public final InputStream byteStream() {
    return source().inputStream();
  }

  public abstract BufferedSourcezaqdfs source();


  public final byte[] bytes() throws IOException {
    long contentLength = contentLength();
    if (contentLength > Integer.MAX_VALUE) {
      throw new IOException("Cannot buffer entire body for content length: " + contentLength);
    }

    BufferedSourcezaqdfs source = source();
    byte[] bytes;
    try {
      bytes = source.readByteArray();
    } finally {
      Utilaq.closeQuietly(source);
    }
    if (contentLength != -1 && contentLength != bytes.length) {
      throw new IOException("Content-Length ("
          + contentLength
          + ") and stream length ("
          + bytes.length
          + ") disagree");
    }
    return bytes;
  }


  public final Reader charStream() {
    Reader r = reader;
    return r != null ? r : (reader = new BomAwareReader(source(), charset()));
  }


  public final String string() throws IOException {
    BufferedSourcezaqdfs source = source();
    try {
      Charset charset = Utilaq.bomAwareCharset(source, charset());
      return source.readString(charset);
    } finally {
      Utilaq.closeQuietly(source);
    }
  }

  private Charset charset() {
    MediaTypeza contentType = contentType();
    return contentType != null ? contentType.charset(UTF_8) : UTF_8;
  }

  @Override public void close() {
    Utilaq.closeQuietly(source());
  }


  public static ResponseBodyza create(@Nullableq MediaTypeza contentType, String content) {
    Charset charset = UTF_8;
    if (contentType != null) {
      charset = contentType.charset();
      if (charset == null) {
        charset = UTF_8;
        contentType = MediaTypeza.parse(contentType + "; charset=utf-8");
      }
    }
    Bufferzaq buffer = new Bufferzaq().writeString(content, charset);
    return create(contentType, buffer.size(), buffer);
  }


  public static ResponseBodyza create(final @Nullableq MediaTypeza contentType, byte[] content) {
    Bufferzaq buffer = new Bufferzaq().write(content);
    return create(contentType, content.length, buffer);
  }


  public static ResponseBodyza create(final @Nullableq MediaTypeza contentType,
                                      final long contentLength, final BufferedSourcezaqdfs content) {
    if (content == null) throw new NullPointerException("source == null");
    return new ResponseBodyza() {
      @Override public @Nullableq
      MediaTypeza contentType() {
        return contentType;
      }

      @Override public long contentLength() {
        return contentLength;
      }

      @Override public BufferedSourcezaqdfs source() {
        return content;
      }
    };
  }

  static final class BomAwareReader extends Reader {
    private final BufferedSourcezaqdfs source;
    private final Charset charset;

    private boolean closed;
    private Reader delegate;

    BomAwareReader(BufferedSourcezaqdfs source, Charset charset) {
      this.source = source;
      this.charset = charset;
    }

    @Override public int read(char[] cbuf, int off, int len) throws IOException {
      if (closed) throw new IOException("Stream closed");

      Reader delegate = this.delegate;
      if (delegate == null) {
        Charset charset = Utilaq.bomAwareCharset(source, this.charset);
        delegate = this.delegate = new InputStreamReader(source.inputStream(), charset);
      }
      return delegate.read(cbuf, off, len);
    }

    @Override public void close() throws IOException {
      closed = true;
      if (delegate != null) {
        delegate.close();
      } else {
        source.close();
      }
    }
  }
}
