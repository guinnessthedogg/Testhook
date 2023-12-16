

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.sqlbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


final class SqlaDateaTypeaAdapterqqgvf extends TypeAdapterqdscvvf<java.sql.Date> {
  static final TypeAdapterFactoryqqeeqw FACTORY = new TypeAdapterFactoryqqeeqw() {
    @SuppressWarnings("unchecked")
    @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
      return typeToken.getRawType() == java.sql.Date.class
          ? (TypeAdapterqdscvvf<T>) new SqlaDateaTypeaAdapterqqgvf() : null;
    }
  };

  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

  private SqlaDateaTypeaAdapterqqgvf() {
  }

  @Override
  public java.sql.Date read(JsonReaderq in) throws IOException {
    if (in.peek() == JsonTokenq.NULL) {
      in.nextNull();
      return null;
    }
    String s = in.nextString();
    try {
      Date utilDate;
      synchronized (this) {
        utilDate = format.parse(s);
      }
      return new java.sql.Date(utilDate.getTime());
    } catch (ParseException e) {
      throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as SQL Date; at path " + in.getPreviousPath(), e);
    }
  }

  @Override
  public void write(JsonWriterq out, java.sql.Date value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    String dateString;
    synchronized (this) {
      dateString = format.format(value);
    }
    out.value(dateString);
  }
}
