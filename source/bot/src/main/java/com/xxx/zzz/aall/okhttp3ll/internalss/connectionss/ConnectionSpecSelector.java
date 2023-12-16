
package com.xxx.zzz.aall.okhttp3ll.internalss.connectionss;

import com.xxx.zzz.aall.okhttp3ll.internalss.Internalq;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.UnknownServiceException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSocket;

import com.xxx.zzz.aall.okhttp3ll.ConnectionSpecza;


public final class ConnectionSpecSelector {

  private final List<ConnectionSpecza> connectionSpecs;
  private int nextModeIndex;
  private boolean isFallbackPossible;
  private boolean isFallback;

  public ConnectionSpecSelector(List<ConnectionSpecza> connectionSpecs) {
    this.nextModeIndex = 0;
    this.connectionSpecs = connectionSpecs;
  }


  public ConnectionSpecza configureSecureSocket(SSLSocket sslSocket) throws IOException {
    ConnectionSpecza tlsConfiguration = null;
    for (int i = nextModeIndex, size = connectionSpecs.size(); i < size; i++) {
      ConnectionSpecza connectionSpec = connectionSpecs.get(i);
      if (connectionSpec.isCompatible(sslSocket)) {
        tlsConfiguration = connectionSpec;
        nextModeIndex = i + 1;
        break;
      }
    }

    if (tlsConfiguration == null) {
      
      
      
      throw new UnknownServiceException(
          "Unable to find acceptable protocols. isFallback=" + isFallback
              + ", modes=" + connectionSpecs
              + ", supported protocols=" + Arrays.toString(sslSocket.getEnabledProtocols()));
    }

    isFallbackPossible = isFallbackPossible(sslSocket);

    Internalq.instance.apply(tlsConfiguration, sslSocket, isFallback);

    return tlsConfiguration;
  }


  public boolean connectionFailed(IOException e) {
    
    isFallback = true;

    if (!isFallbackPossible) {
      return false;
    }

    
    if (e instanceof ProtocolException) {
      return false;
    }

    
    
    
    if (e instanceof InterruptedIOException) {
      return false;
    }

    
    
    if (e instanceof SSLHandshakeException) {
      
      
      if (e.getCause() instanceof CertificateException) {
        return false;
      }
    }
    if (e instanceof SSLPeerUnverifiedException) {
      
      return false;
    }

    
    
    return (e instanceof SSLHandshakeException || e instanceof SSLProtocolException);
  }


  private boolean isFallbackPossible(SSLSocket socket) {
    for (int i = nextModeIndex; i < connectionSpecs.size(); i++) {
      if (connectionSpecs.get(i).isCompatible(socket)) {
        return true;
      }
    }
    return false;
  }
}
