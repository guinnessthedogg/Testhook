
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.IOException;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okioss.ByteStringzaq;

public final class Http2a {
  static final ByteStringzaq CONNECTION_PREFACE
      = ByteStringzaq.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");

  
  static final int INITIAL_MAX_FRAME_SIZE = 0x4000;

  static final byte TYPE_DATA = 0x0;
  static final byte TYPE_HEADERS = 0x1;
  static final byte TYPE_PRIORITY = 0x2;
  static final byte TYPE_RST_STREAM = 0x3;
  static final byte TYPE_SETTINGS = 0x4;
  static final byte TYPE_PUSH_PROMISE = 0x5;
  static final byte TYPE_PING = 0x6;
  static final byte TYPE_GOAWAY = 0x7;
  static final byte TYPE_WINDOW_UPDATE = 0x8;
  static final byte TYPE_CONTINUATION = 0x9;

  static final byte FLAG_NONE = 0x0;
  static final byte FLAG_ACK = 0x1;
  static final byte FLAG_END_STREAM = 0x1;
  static final byte FLAG_END_HEADERS = 0x4;
  static final byte FLAG_END_PUSH_PROMISE = 0x4;
  static final byte FLAG_PADDED = 0x8;
  static final byte FLAG_PRIORITY = 0x20;
  static final byte FLAG_COMPRESSED = 0x20;

  
  private static final String[] FRAME_NAMES = new String[] {
      "DATA",
      "HEADERS",
      "PRIORITY",
      "RST_STREAM",
      "SETTINGS",
      "PUSH_PROMISE",
      "PING",
      "GOAWAY",
      "WINDOW_UPDATE",
      "CONTINUATION"
  };

  
  static final String[] FLAGS = new String[0x40];
  static final String[] BINARY = new String[256];
  static {
    for (int i = 0; i < BINARY.length; i++) {
      BINARY[i] = Utilaq.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    FLAGS[FLAG_NONE] = "";
    FLAGS[FLAG_END_STREAM] = "END_STREAM";

    int[] prefixFlags = new int[] {FLAG_END_STREAM};

    FLAGS[FLAG_PADDED] = "PADDED";
    for (int prefixFlag : prefixFlags) {
      FLAGS[prefixFlag | FLAG_PADDED] = FLAGS[prefixFlag] + "|PADDED";
    }

    FLAGS[FLAG_END_HEADERS] = "END_HEADERS";
    FLAGS[FLAG_PRIORITY] = "PRIORITY";
    FLAGS[FLAG_END_HEADERS | FLAG_PRIORITY] = "END_HEADERS|PRIORITY";
    int[] frameFlags = new int[] {
        FLAG_END_HEADERS, FLAG_PRIORITY, FLAG_END_HEADERS | FLAG_PRIORITY
    };

    for (int frameFlag : frameFlags) {
      for (int prefixFlag : prefixFlags) {
        FLAGS[prefixFlag | frameFlag] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag];
        FLAGS[prefixFlag | frameFlag | FLAG_PADDED]
            = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag] + "|PADDED";
      }
    }

    for (int i = 0; i < FLAGS.length; i++) {
      if (FLAGS[i] == null) FLAGS[i] = BINARY[i];
    }
  }

  private Http2a() {
  }

  static IllegalArgumentException illegalArgument(String message, Object... args) {
    throw new IllegalArgumentException(Utilaq.format(message, args));
  }

  static IOException ioException(String message, Object... args) throws IOException {
    throw new IOException(Utilaq.format(message, args));
  }

  
  static String frameLog(boolean inbound, int streamId, int length, byte type, byte flags) {
    String formattedType = type < FRAME_NAMES.length ? FRAME_NAMES[type] : Utilaq.format("0x%02x", type);
    String formattedFlags = formatFlags(type, flags);
    return Utilaq.format("%s 0x%08x %5d %-13s %s", inbound ? "<<" : ">>", streamId, length,
        formattedType, formattedFlags);
  }

  

  static String formatFlags(byte type, byte flags) {
    if (flags == 0) return "";
    switch (type) {
      case TYPE_SETTINGS:
      case TYPE_PING:
        return flags == FLAG_ACK ? "ACK" : BINARY[flags];
      case TYPE_PRIORITY:
      case TYPE_RST_STREAM:
      case TYPE_GOAWAY:
      case TYPE_WINDOW_UPDATE:
        return BINARY[flags];
    }
    String result = flags < FLAGS.length ? FLAGS[flags] : BINARY[flags];

    if (type == TYPE_PUSH_PROMISE && (flags & FLAG_END_PUSH_PROMISE) != 0) {
      return result.replace("HEADERS", "PUSH_PROMISE");
    } else if (type == TYPE_DATA && (flags & FLAG_COMPRESSED) != 0) {
      return result.replace("PRIORITY", "COMPRESSED");
    }
    return result;
  }
}
