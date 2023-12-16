

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.JavaVersionq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.PreJava9DateFormatProviderq;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.utilssssss.ISO8601Utilsqq;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public final class DateTypeAdapterqq extends TypeAdapterqdscvvf<Date> {
  public static final TypeAdapterFactoryqqeeqw FACTORY = new TypeAdapterFactoryqqeeqw() {
    @SuppressWarnings("unchecked")
    @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
      return typeToken.getRawType() == Date.class ? (TypeAdapterqdscvvf<T>) new DateTypeAdapterqq() : null;
    }
  };


  private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

  public DateTypeAdapterqq() {
    dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
    if (!Locale.getDefault().equals(Locale.US)) {
      dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT));
    }
    if (JavaVersionq.isJava9OrLater()) {
      dateFormats.add(PreJava9DateFormatProviderq.getUSDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT));
    }
  }

  @Override public Date read(JsonReaderq in) throws IOException {
    if (in.peek() == JsonTokenq.NULL) {
      in.nextNull();
      return null;
    }
    return deserializeToDate(in);
  }

  private Date deserializeToDate(JsonReaderq in) throws IOException {
    String s = in.nextString();
    synchronized (dateFormats) {
      for (DateFormat dateFormat : dateFormats) {
        try {
          return dateFormat.parse(s);
        } catch (ParseException ignored) {}
      }
    }
    try {
      return ISO8601Utilsqq.parse(s, new ParsePosition(0));
    } catch (ParseException e) {
      throw new JsonSyntaxExceptionq("Failed parsing '" + s + "' as Date; at path " + in.getPreviousPath(), e);
    }
  }

  @Override public void write(JsonWriterq out, Date value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }

    DateFormat dateFormat = dateFormats.get(0);
    String dateFormatAsString;
    synchronized (dateFormats) {
      dateFormatAsString = dateFormat.format(value);
    }
    out.value(dateFormatAsString);
  }
}
