

package com.xxx.zzz.aall.gsonllll.googlepp;

import com.xxx.zzz.aall.gsonllll.googlepp.streamss.JsonReaderq;
import com.xxx.zzz.aall.gsonllll.googlepp.internalbb.LazilyParsedNumberq;
import com.xxx.zzz.aall.gsonllll.googlepp.streamss.MalformedJsonExceptionq;

import java.io.IOException;
import java.math.BigDecimal;


public enum ToNumberPolicyq implements ToNumberStrategyq {


  DOUBLE {
    @Override public Double readNumber(JsonReaderq in) throws IOException {
      return in.nextDouble();
    }
  },


  LAZILY_PARSED_NUMBER {
    @Override public Number readNumber(JsonReaderq in) throws IOException {
      return new LazilyParsedNumberq(in.nextString());
    }
  },


  LONG_OR_DOUBLE {
    @Override public Number readNumber(JsonReaderq in) throws IOException, JsonParseExceptionq {
      String value = in.nextString();
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException longE) {
        try {
          Double d = Double.valueOf(value);
          if ((d.isInfinite() || d.isNaN()) && !in.isLenient()) {
            throw new MalformedJsonExceptionq("JSON forbids NaN and infinities: " + d + "; at path " + in.getPreviousPath());
          }
          return d;
        } catch (NumberFormatException doubleE) {
          throw new JsonParseExceptionq("Cannot parse " + value + "; at path " + in.getPreviousPath(), doubleE);
        }
      }
    }
  },


  BIG_DECIMAL {
    @Override public BigDecimal readNumber(JsonReaderq in) throws IOException {
      String value = in.nextString();
      try {
        return new BigDecimal(value);
      } catch (NumberFormatException e) {
        throw new JsonParseExceptionq("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
      }
    }
  }

}
