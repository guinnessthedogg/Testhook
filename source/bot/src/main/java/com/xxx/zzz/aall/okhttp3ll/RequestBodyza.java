
package com.xxx.zzz.aall.okhttp3ll;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;

public abstract class RequestBodyza {

  public abstract @Nullableq
  MediaTypeza contentType();


  public long contentLength() throws IOException {
    return -1;
  }


  public abstract void writeTo(BufferedSinkzaqds sink) throws IOException;


  public static RequestBodyza create(@Nullableq MediaTypeza contentType, String content) {
    Charset charset = Utilaq.UTF_8;
    if (contentType != null) {
      charset = contentType.charset();
      if (charset == null) {
        charset = Utilaq.UTF_8;
        contentType = MediaTypeza.parse(contentType + "; charset=utf-8");
      }
    }
    byte[] bytes = content.getBytes(charset);
    return create(contentType, bytes);
  }


  public static RequestBodyza create(
          final @Nullableq MediaTypeza contentType, final ByteStringzaq content) {
    return new RequestBodyza() {
      @Override public @Nullableq
      MediaTypeza contentType() {
        return contentType;
      }

      @Override public long contentLength() throws IOException {
        return content.size();
      }

      @Override public void writeTo(BufferedSinkzaqds sink) throws IOException {
        sink.write(content);
      }
    };
  }


  public static RequestBodyza create(final @Nullableq MediaTypeza contentType, final byte[] content) {
    return create(contentType, content, 0, content.length);
  }


  public static RequestBodyza create(final @Nullableq MediaTypeza contentType, final byte[] content,
                                     final int offset, final int byteCount) {
    if (content == null) throw new NullPointerException("content == null");
    Utilaq.checkOffsetAndCount(content.length, offset, byteCount);
    return new RequestBodyza() {
      @Override public @Nullableq
      MediaTypeza contentType() {
        return contentType;
      }

      @Override public long contentLength() {
        return byteCount;
      }

      @Override public void writeTo(BufferedSinkzaqds sink) throws IOException {
        sink.write(content, offset, byteCount);
      }
    };
  }


  public static RequestBodyza create(final @Nullableq MediaTypeza contentType, final File file) {
    if (file == null) throw new NullPointerException("content == null");

    return new RequestBodyza() {
      @Override public @Nullableq
      MediaTypeza contentType() {
        return contentType;
      }

      @Override public long contentLength() {
        return file.length();
      }

      @Override public void writeTo(BufferedSinkzaqds sink) throws IOException {
        Sourcezaq source = null;
        try {
          source = Okiozaq.source(file);
          sink.writeAll(source);
        } finally {
          Utilaq.closeQuietly(source);
        }
      }
    };
  }
}
