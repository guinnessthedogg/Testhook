
package com.xxx.zzz.aall.okhttp3ll.internalss.wssss;

import java.io.IOException;
import java.net.ProtocolException;

import com.xxx.zzz.aall.okioss.ByteStringzaq;

public final class WebSocketProtocol {

  static final String ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";




  static final int B0_FLAG_FIN = 0b10000000;

  static final int B0_FLAG_RSV1 = 0b01000000;

  static final int B0_FLAG_RSV2 = 0b00100000;

  static final int B0_FLAG_RSV3 = 0b00010000;

  static final int B0_MASK_OPCODE = 0b00001111;

  static final int OPCODE_FLAG_CONTROL = 0b00001000;


  static final int B1_FLAG_MASK = 0b10000000;

  static final int B1_MASK_LENGTH = 0b01111111;

  static final int OPCODE_CONTINUATION = 0x0;
  static final int OPCODE_TEXT = 0x1;
  static final int OPCODE_BINARY = 0x2;

  static final int OPCODE_CONTROL_CLOSE = 0x8;
  static final int OPCODE_CONTROL_PING = 0x9;
  static final int OPCODE_CONTROL_PONG = 0xa;


  static final long PAYLOAD_BYTE_MAX = 125L;

  static final long CLOSE_MESSAGE_MAX = PAYLOAD_BYTE_MAX - 2;

  static final int PAYLOAD_SHORT = 126;

  static final long PAYLOAD_SHORT_MAX = 0xffffL;

  static final int PAYLOAD_LONG = 127;


  static final int CLOSE_CLIENT_GOING_AWAY = 1001;

  static final int CLOSE_PROTOCOL_EXCEPTION = 1002;

  static final int CLOSE_NO_STATUS_CODE = 1005;

  static final int CLOSE_ABNORMAL_TERMINATION = 1006;

  static void toggleMask(byte[] buffer, long byteCount, byte[] key, long frameBytesRead) {
    int keyLength = key.length;
    for (int i = 0; i < byteCount; i++, frameBytesRead++) {
      int keyIndex = (int) (frameBytesRead % keyLength);
      buffer[i] = (byte) (buffer[i] ^ key[keyIndex]);
    }
  }

  static String closeCodeExceptionMessage(int code) {
    if (code < 1000 || code >= 5000) {
      return "Code must be in range [1000,5000): " + code;
    } else if ((code >= 1004 && code <= 1006) || (code >= 1012 && code <= 2999)) {
      return "Code " + code + " is reserved and may not be used.";
    } else {
      return null;
    }
  }

  static void validateCloseCode(int code) {
    String message = closeCodeExceptionMessage(code);
    if (message != null) throw new IllegalArgumentException(message);
  }

  public static String acceptHeader(String key) {
    return ByteStringzaq.encodeUtf8(key + WebSocketProtocol.ACCEPT_MAGIC).sha1().base64();
  }

  private WebSocketProtocol() {
    throw new AssertionError("No instances.");
  }
}
