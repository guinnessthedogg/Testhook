package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.sqlbb;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;
import com.xxx.zzz.aall.gsonllll.googlepp.reflectsbb.TypeTokenq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonWriterq;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

class SqlTimestampTypeAdapterqqpo extends TypeAdapterqdscvvf<Timestamp> {
  static final TypeAdapterFactoryqqeeqw FACTORY = new TypeAdapterFactoryqqeeqw() {
    @SuppressWarnings("unchecked")
    @Override public <T> TypeAdapterqdscvvf<T> create(Gsonq gson, TypeTokenq<T> typeToken) {
      if (typeToken.getRawType() == Timestamp.class) {
        final TypeAdapterqdscvvf<Date> dateTypeAdapter = gson.getAdapter(Date.class);
        return (TypeAdapterqdscvvf<T>) new SqlTimestampTypeAdapterqqpo(dateTypeAdapter);
      } else {
        return null;
      }
    }
  };

  private final TypeAdapterqdscvvf<Date> dateTypeAdapter;

  private SqlTimestampTypeAdapterqqpo(TypeAdapterqdscvvf<Date> dateTypeAdapter) {
    this.dateTypeAdapter = dateTypeAdapter;
  }

  @Override
  public Timestamp read(JsonReaderq in) throws IOException {
    Date date = dateTypeAdapter.read(in);
    return date != null ? new Timestamp(date.getTime()) : null;
  }

  @Override
  public void write(JsonWriterq out, Timestamp value) throws IOException {
    dateTypeAdapter.write(out, value);
  }
}
