
package com.xxx.zzz.aall.okhttp3ll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;
import com.xxx.zzz.aall.okioss.ByteStringzaq;


public final class MultipartBodyza extends RequestBodyza {
  
  public static final MediaTypeza MIXED = MediaTypeza.parse("multipart/mixed");

  
  public static final MediaTypeza ALTERNATIVE = MediaTypeza.parse("multipart/alternative");

  
  public static final MediaTypeza DIGEST = MediaTypeza.parse("multipart/digest");

  
  public static final MediaTypeza PARALLEL = MediaTypeza.parse("multipart/parallel");

  
  public static final MediaTypeza FORM = MediaTypeza.parse("multipart/form-data");

  private static final byte[] COLONSPACE = {':', ' '};
  private static final byte[] CRLF = {'\r', '\n'};
  private static final byte[] DASHDASH = {'-', '-'};

  private final ByteStringzaq boundary;
  private final MediaTypeza originalType;
  private final MediaTypeza contentType;
  private final List<Part> parts;
  private long contentLength = -1L;

  MultipartBodyza(ByteStringzaq boundary, MediaTypeza type, List<Part> parts) {
    this.boundary = boundary;
    this.originalType = type;
    this.contentType = MediaTypeza.parse(type + "; boundary=" + boundary.utf8());
    this.parts = Utilaq.immutableList(parts);
  }

  public MediaTypeza type() {
    return originalType;
  }

  public String boundary() {
    return boundary.utf8();
  }

  
  public int size() {
    return parts.size();
  }

  public List<Part> parts() {
    return parts;
  }

  public Part part(int index) {
    return parts.get(index);
  }

  
  @Override public MediaTypeza contentType() {
    return contentType;
  }

  @Override public long contentLength() throws IOException {
    long result = contentLength;
    if (result != -1L) return result;
    return contentLength = writeOrCountBytes(null, true);
  }

  @Override public void writeTo(BufferedSinkzaqds sink) throws IOException {
    writeOrCountBytes(sink, false);
  }

  
  private long writeOrCountBytes(
          @Nullableq BufferedSinkzaqds sink, boolean countBytes) throws IOException {
    long byteCount = 0L;

    Bufferzaq byteCountBuffer = null;
    if (countBytes) {
      sink = byteCountBuffer = new Bufferzaq();
    }

    for (int p = 0, partCount = parts.size(); p < partCount; p++) {
      Part part = parts.get(p);
      Headersza headers = part.headers;
      RequestBodyza body = part.body;

      sink.write(DASHDASH);
      sink.write(boundary);
      sink.write(CRLF);

      if (headers != null) {
        for (int h = 0, headerCount = headers.size(); h < headerCount; h++) {
          sink.writeUtf8(headers.name(h))
              .write(COLONSPACE)
              .writeUtf8(headers.value(h))
              .write(CRLF);
        }
      }

      MediaTypeza contentType = body.contentType();
      if (contentType != null) {
        sink.writeUtf8("Content-Type: ")
            .writeUtf8(contentType.toString())
            .write(CRLF);
      }

      long contentLength = body.contentLength();
      if (contentLength != -1) {
        sink.writeUtf8("Content-Length: ")
            .writeDecimalLong(contentLength)
            .write(CRLF);
      } else if (countBytes) {
        
        byteCountBuffer.clear();
        return -1L;
      }

      sink.write(CRLF);

      if (countBytes) {
        byteCount += contentLength;
      } else {
        body.writeTo(sink);
      }

      sink.write(CRLF);
    }

    sink.write(DASHDASH);
    sink.write(boundary);
    sink.write(DASHDASH);
    sink.write(CRLF);

    if (countBytes) {
      byteCount += byteCountBuffer.size();
      byteCountBuffer.clear();
    }

    return byteCount;
  }

  
  static StringBuilder appendQuotedString(StringBuilder target, String key) {
    target.append('"');
    for (int i = 0, len = key.length(); i < len; i++) {
      char ch = key.charAt(i);
      switch (ch) {
        case '\n':
          target.append("%0A");
          break;
        case '\r':
          target.append("%0D");
          break;
        case '"':
          target.append("%22");
          break;
        default:
          target.append(ch);
          break;
      }
    }
    target.append('"');
    return target;
  }

  public static final class Part {
    public static Part create(RequestBodyza body) {
      return create(null, body);
    }

    public static Part create(@Nullableq Headersza headers, RequestBodyza body) {
      if (body == null) {
        throw new NullPointerException("body == null");
      }
      if (headers != null && headers.get("Content-Type") != null) {
        throw new IllegalArgumentException("Unexpected header: Content-Type");
      }
      if (headers != null && headers.get("Content-Length") != null) {
        throw new IllegalArgumentException("Unexpected header: Content-Length");
      }
      return new Part(headers, body);
    }

    public static Part createFormData(String name, String value) {
      return createFormData(name, null, RequestBodyza.create(null, value));
    }

    public static Part createFormData(String name, @Nullableq String filename, RequestBodyza body) {
      if (name == null) {
        throw new NullPointerException("name == null");
      }
      StringBuilder disposition = new StringBuilder("form-data; name=");
      appendQuotedString(disposition, name);

      if (filename != null) {
        disposition.append("; filename=");
        appendQuotedString(disposition, filename);
      }

      return create(Headersza.of("Content-Disposition", disposition.toString()), body);
    }

    final @Nullableq
    Headersza headers;
    final RequestBodyza body;

    private Part(@Nullableq Headersza headers, RequestBodyza body) {
      this.headers = headers;
      this.body = body;
    }

    public @Nullableq
    Headersza headers() {
      return headers;
    }

    public RequestBodyza body() {
      return body;
    }
  }

  public static final class Builder {
    private final ByteStringzaq boundary;
    private MediaTypeza type = MIXED;
    private final List<Part> parts = new ArrayList<>();

    public Builder() {
      this(UUID.randomUUID().toString());
    }

    public Builder(String boundary) {
      this.boundary = ByteStringzaq.encodeUtf8(boundary);
    }

    
    public Builder setType(MediaTypeza type) {
      if (type == null) {
        throw new NullPointerException("type == null");
      }
      if (!type.type().equals("multipart")) {
        throw new IllegalArgumentException("multipart != " + type);
      }
      this.type = type;
      return this;
    }

    
    public Builder addPart(RequestBodyza body) {
      return addPart(Part.create(body));
    }

    
    public Builder addPart(@Nullableq Headersza headers, RequestBodyza body) {
      return addPart(Part.create(headers, body));
    }

    
    public Builder addFormDataPart(String name, String value) {
      return addPart(Part.createFormData(name, value));
    }

    
    public Builder addFormDataPart(String name, @Nullableq String filename, RequestBodyza body) {
      return addPart(Part.createFormData(name, filename, body));
    }

    
    public Builder addPart(Part part) {
      if (part == null) throw new NullPointerException("part == null");
      parts.add(part);
      return this;
    }

    
    public MultipartBodyza build() {
      if (parts.isEmpty()) {
        throw new IllegalStateException("Multipart body must have at least one part.");
      }
      return new MultipartBodyza(boundary, type, parts);
    }
  }
}
