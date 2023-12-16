
package com.xxx.zzz.aall.okhttp3ll.internalss.httpnn;

import java.io.IOException;
import java.net.ProtocolException;

import com.xxx.zzz.aall.okhttp3ll.Protocolza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;


public final class StatusLine {

  public static final int HTTP_TEMP_REDIRECT = 307;
  public static final int HTTP_PERM_REDIRECT = 308;
  public static final int HTTP_CONTINUE = 100;

  public final Protocolza protocol;
  public final int code;
  public final String message;

  public StatusLine(Protocolza protocol, int code, String message) {
    this.protocol = protocol;
    this.code = code;
    this.message = message;
  }

  public static StatusLine get(Responseza response) {
    return new StatusLine(response.protocol(), response.code(), response.message());
  }

  public static StatusLine parse(String statusLine) throws IOException {
    
    

    
    int codeStart;
    Protocolza protocol;
    if (statusLine.startsWith("HTTP/1.")) {
      if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
        throw new ProtocolException("Unexpected status line: " + statusLine);
      }
      int httpMinorVersion = statusLine.charAt(7) - '0';
      codeStart = 9;
      if (httpMinorVersion == 0) {
        protocol = Protocolza.HTTP_1_0;
      } else if (httpMinorVersion == 1) {
        protocol = Protocolza.HTTP_1_1;
      } else {
        throw new ProtocolException("Unexpected status line: " + statusLine);
      }
    } else if (statusLine.startsWith("ICY ")) {
      
      protocol = Protocolza.HTTP_1_0;
      codeStart = 4;
    } else {
      throw new ProtocolException("Unexpected status line: " + statusLine);
    }

    
    if (statusLine.length() < codeStart + 3) {
      throw new ProtocolException("Unexpected status line: " + statusLine);
    }
    int code;
    try {
      code = Integer.parseInt(statusLine.substring(codeStart, codeStart + 3));
    } catch (NumberFormatException e) {
      throw new ProtocolException("Unexpected status line: " + statusLine);
    }

    
    
    String message = "";
    if (statusLine.length() > codeStart + 3) {
      if (statusLine.charAt(codeStart + 3) != ' ') {
        throw new ProtocolException("Unexpected status line: " + statusLine);
      }
      message = statusLine.substring(codeStart + 4);
    }

    return new StatusLine(protocol, code, message);
  }

  @Override public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(protocol == Protocolza.HTTP_1_0 ? "HTTP/1.0" : "HTTP/1.1");
    result.append(' ').append(code);
    if (message != null) {
      result.append(' ').append(message);
    }
    return result.toString();
  }
}
