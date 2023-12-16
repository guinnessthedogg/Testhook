

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.sqlbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


final class SqlTimeTypeAdapterqqbgfbv extends TypeAdapterqdscvvf<Time> {
  static final TypeAdapterFactoryqqeeqw FACTORY = new TypeAdapterFactoryqqeeqw() {
    @SuppressWarnings("unchecked")
    @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
      return typeToken.getRawType() == Time.class ? (TypeAdapterqdscvvf<T>) new SqlTimeTypeAdapterqqbgfbv() : null;
    }
  };

  private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

  private SqlTimeTypeAdapterqqbgfbv() {
  }

  @Override public Time read(JsonReaderq in) throws IOException {
    if (in.peek() == JsonTokenq.NULL) {
      in.nextNull();
      return null;
    }
    String s = in.nextString();
    try {
      synchronized (this) {
        Date date = format.parse(s);
        return new Time(date.getTime());
      }
    } catch (ParseException e) {
      throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as SQL Time; at path " + in.getPreviousPath(), e);
    }
  }

  @Override public void write(JsonWriterq out, Time value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    String timeString;
    synchronized (this) {
      timeString = format.format(value);
    }
    out.value(timeString);
  }
}
