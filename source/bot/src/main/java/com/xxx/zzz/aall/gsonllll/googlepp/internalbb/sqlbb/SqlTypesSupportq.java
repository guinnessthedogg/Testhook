package com.xxx.zzz.aall.gsonllll.googlepp.internalbb.sqlbb;

import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.bindbb.DefaultaDateaTypeaAdapterqqdsa;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;

import java.sql.Timestamp;
import java.util.Date;


public final class SqlTypesSupportq {

  public static final boolean SUPPORTS_SQL_TYPES;

  public static final DefaultaDateaTypeaAdapterqqdsa.DateType<? extends Date> DATE_DATE_TYPE;
  public static final DefaultaDateaTypeaAdapterqqdsa.DateType<? extends Date> TIMESTAMP_DATE_TYPE;

  public static final TypeAdapterFactoryqqeeqw DATE_FACTORY;
  public static final TypeAdapterFactoryqqeeqw TIME_FACTORY;
  public static final TypeAdapterFactoryqqeeqw TIMESTAMP_FACTORY;

  static {
    boolean sqlTypesSupport;
    try {
      Class.forName("java.sql.Date");
      sqlTypesSupport = true;
    } catch (ClassNotFoundException classNotFoundException) {
      sqlTypesSupport = false;
    }
    SUPPORTS_SQL_TYPES = sqlTypesSupport;

    if (SUPPORTS_SQL_TYPES) {
      DATE_DATE_TYPE = new DefaultaDateaTypeaAdapterqqdsa.DateType<java.sql.Date>(java.sql.Date.class) {
        @Override protected java.sql.Date deserialize(Date date) {
          return new java.sql.Date(date.getTime());
        }
      };
      TIMESTAMP_DATE_TYPE = new DefaultaDateaTypeaAdapterqqdsa.DateType<Timestamp>(Timestamp.class) {
        @Override protected Timestamp deserialize(Date date) {
          return new Timestamp(date.getTime());
        }
      };

      DATE_FACTORY = SqlaDateaTypeaAdapterqqgvf.FACTORY;
      TIME_FACTORY = SqlTimeTypeAdapterqqbgfbv.FACTORY;
      TIMESTAMP_FACTORY = SqlTimestampTypeAdapterqqpo.FACTORY;
    } else {
      DATE_DATE_TYPE = null;
      TIMESTAMP_DATE_TYPE = null;

      DATE_FACTORY = null;
      TIME_FACTORY = null;
      TIMESTAMP_FACTORY = null;
    }
  }

  private SqlTypesSupportq() {
  }
}
