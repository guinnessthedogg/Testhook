

package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb;

import com.xxx.zzz.aall.gsonllll.googlepp.JsonSyntaxExceptionq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.$Gson$Preconditionsq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.JavaVersionq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.PreJava9DateFormatProviderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.utilssssss.ISO8601Utilsqq;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public final class DefaultaDateaTypeaAdapterqqdsa<T extends Date> extends TypeAdapterqdscvvf<T> {
  private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";

  public static abstract class DateType<T extends Date> {
    public static final DateType<Date> DATE = new DateType<Date>(Date.class) {
      @Override protected Date deserialize(Date date) {
        return date;
      }
    };

    private final Class<T> dateClass;

    protected DateType(Class<T> dateClass) {
      this.dateClass = dateClass;
    }

    protected abstract T deserialize(Date date);

    private final TypeAdapterFactoryqqeeqw createFactory(DefaultaDateaTypeaAdapterqqdsa<T> adapter) {
      return TypeAdaptersqq.newFactory(dateClass, adapter);
    }

    public final TypeAdapterFactoryqqeeqw createAdapterFactory(String datePattern) {
      return createFactory(new DefaultaDateaTypeaAdapterqqdsa<T>(this, datePattern));
    }

    public final TypeAdapterFactoryqqeeqw createAdapterFactory(int style) {
      return createFactory(new DefaultaDateaTypeaAdapterqqdsa<T>(this, style));
    }

    public final TypeAdapterFactoryqqeeqw createAdapterFactory(int dateStyle, int timeStyle) {
      return createFactory(new DefaultaDateaTypeaAdapterqqdsa<T>(this, dateStyle, timeStyle));
    }

    public final TypeAdapterFactoryqqeeqw createDefaultsAdapterFactory() {
      return createFactory(new DefaultaDateaTypeaAdapterqqdsa<T>(this, DateFormat.DEFAULT, DateFormat.DEFAULT));
    }
  }

  private final DateType<T> dateType;


  private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

  private DefaultaDateaTypeaAdapterqqdsa(DateType<T> dateType, String datePattern) {
    this.dateType = $Gson$Preconditionsq.checkNotNull(dateType);
    dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
    if (!Locale.getDefault().equals(Locale.US)) {
      dateFormats.add(new SimpleDateFormat(datePattern));
    }
  }

  private DefaultaDateaTypeaAdapterqqdsa(DateType<T> dateType, int style) {
    this.dateType = $Gson$Preconditionsq.checkNotNull(dateType);
    dateFormats.add(DateFormat.getDateInstance(style, Locale.US));
    if (!Locale.getDefault().equals(Locale.US)) {
      dateFormats.add(DateFormat.getDateInstance(style));
    }
    if (JavaVersionq.isJava9OrLater()) {
      dateFormats.add(PreJava9DateFormatProviderq.getUSDateFormat(style));
    }
  }

  private DefaultaDateaTypeaAdapterqqdsa(DateType<T> dateType, int dateStyle, int timeStyle) {
    this.dateType = $Gson$Preconditionsq.checkNotNull(dateType);
    dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
    if (!Locale.getDefault().equals(Locale.US)) {
      dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }
    if (JavaVersionq.isJava9OrLater()) {
      dateFormats.add(PreJava9DateFormatProviderq.getUSDateTimeFormat(dateStyle, timeStyle));
    }
  }



  @Override
  public void write(JsonWriterq out, Date value) throws IOException {
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

  @Override
  public T read(JsonReaderq in) throws IOException {
    if (in.peek() == JsonTokenq.NULL) {
      in.nextNull();
      return null;
    }
    Date date = deserializeToDate(in);
    return dateType.deserialize(date);
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

  @Override
  public String toString() {
    DateFormat defaultFormat = dateFormats.get(0);
    if (defaultFormat instanceof SimpleDateFormat) {
      return SIMPLE_NAME + '(' + ((SimpleDateFormat) defaultFormat).toPattern() + ')';
    } else {
      return SIMPLE_NAME + '(' + defaultFormat.getClass().getSimpleName() + ')';
    }
  }
}
