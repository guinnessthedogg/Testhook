
package com.xxx.zzz.aall.okhttp3ll;

import static com.xxx.zzz.aall.okhttp3ll.HttpUrlza.percentDecode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;

import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSinkzaqds;

public final class FormBodyza extends RequestBodyza {
  private static final MediaTypeza CONTENT_TYPE =
      MediaTypeza.parse("application/x-www-form-urlencoded");

  private final List<String> encodedNames;
  private final List<String> encodedValues;

  FormBodyza(List<String> encodedNames, List<String> encodedValues) {
    this.encodedNames = Utilaq.immutableList(encodedNames);
    this.encodedValues = Utilaq.immutableList(encodedValues);
  }


  public int size() {
    return encodedNames.size();
  }

  public String encodedName(int index) {
    return encodedNames.get(index);
  }

  public String name(int index) {
    return HttpUrlza.percentDecode(encodedName(index), true);
  }

  public String encodedValue(int index) {
    return encodedValues.get(index);
  }

  public String value(int index) {
    return HttpUrlza.percentDecode(encodedValue(index), true);
  }

  @Override public MediaTypeza contentType() {
    return CONTENT_TYPE;
  }

  @Override public long contentLength() {
    return writeOrCountBytes(null, true);
  }

  @Override public void writeTo(BufferedSinkzaqds sink) throws IOException {
    writeOrCountBytes(sink, false);
  }


  private long writeOrCountBytes(@Nullableq BufferedSinkzaqds sink, boolean countBytes) {
    long byteCount = 0L;

    Bufferzaq buffer;
    if (countBytes) {
      buffer = new Bufferzaq();
    } else {
      buffer = sink.buffer();
    }

    for (int i = 0, size = encodedNames.size(); i < size; i++) {
      if (i > 0) buffer.writeByte('&');
      buffer.writeUtf8(encodedNames.get(i));
      buffer.writeByte('=');
      buffer.writeUtf8(encodedValues.get(i));
    }

    if (countBytes) {
      byteCount = buffer.size();
      buffer.clear();
    }

    return byteCount;
  }

  public static final class Builder {
    private final List<String> names = new ArrayList<>();
    private final List<String> values = new ArrayList<>();

    public Builder add(String name, String value) {
      names.add(HttpUrlza.canonicalize(name, HttpUrlza.FORM_ENCODE_SET, false, false, true, true));
      values.add(HttpUrlza.canonicalize(value, HttpUrlza.FORM_ENCODE_SET, false, false, true, true));
      return this;
    }

    public Builder addEncoded(String name, String value) {
      names.add(HttpUrlza.canonicalize(name, HttpUrlza.FORM_ENCODE_SET, true, false, true, true));
      values.add(HttpUrlza.canonicalize(value, HttpUrlza.FORM_ENCODE_SET, true, false, true, true));
      return this;
    }

    public FormBodyza build() {
      return new FormBodyza(names, values);
    }
  }
}
