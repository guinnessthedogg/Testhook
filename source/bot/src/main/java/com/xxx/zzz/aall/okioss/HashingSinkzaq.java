
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public final class HashingSinkzaq extends ForwardingSinkzaq {
  private final @Nullableq
  MessageDigest messageDigest;
  private final @Nullableq
  Mac mac;

  
  public static HashingSinkzaq md5(Sinkzaq sink) {
    return new HashingSinkzaq(sink, "MD5");
  }

  
  public static HashingSinkzaq sha1(Sinkzaq sink) {
    return new HashingSinkzaq(sink, "SHA-1");
  }

  
  public static HashingSinkzaq sha256(Sinkzaq sink) {
    return new HashingSinkzaq(sink, "SHA-256");
  }

  
  public static HashingSinkzaq sha512(Sinkzaq sink) {
    return new HashingSinkzaq(sink, "SHA-512");
  }

  
  public static HashingSinkzaq hmacSha1(Sinkzaq sink, ByteStringzaq key) {
    return new HashingSinkzaq(sink, key, "HmacSHA1");
  }

  
  public static HashingSinkzaq hmacSha256(Sinkzaq sink, ByteStringzaq key) {
    return new HashingSinkzaq(sink, key, "HmacSHA256");
  }

  
  public static HashingSinkzaq hmacSha512(Sinkzaq sink, ByteStringzaq key) {
    return new HashingSinkzaq(sink, key, "HmacSHA512");
  }

  private HashingSinkzaq(Sinkzaq sink, String algorithm) {
    super(sink);
    try {
      this.messageDigest = MessageDigest.getInstance(algorithm);
      this.mac = null;
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    }
  }

  private HashingSinkzaq(Sinkzaq sink, ByteStringzaq key, String algorithm) {
    super(sink);
    try {
      this.mac = Mac.getInstance(algorithm);
      this.mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
      this.messageDigest = null;
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override public void write(Bufferzaq source, long byteCount) throws IOException {
    Utilzaqq.checkOffsetAndCount(source.size, 0, byteCount);


    long hashedCount = 0;
    for (Segmentzaq s = source.head; hashedCount < byteCount; s = s.next) {
      int toHash = (int) Math.min(byteCount - hashedCount, s.limit - s.pos);
      if (messageDigest != null) {
        messageDigest.update(s.data, s.pos, toHash);
      } else {
        mac.update(s.data, s.pos, toHash);
      }
      hashedCount += toHash;
    }


    super.write(source, byteCount);
  }

  
  public ByteStringzaq hash() {
    byte[] result = messageDigest != null ? messageDigest.digest() : mac.doFinal();
    return ByteStringzaq.of(result);
  }
}
