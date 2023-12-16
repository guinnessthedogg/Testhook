
package com.xxx.zzz.aall.okioss;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public final class HashingSourcezaq extends ForwardingSourcezaq {
  private final MessageDigest messageDigest;
  private final Mac mac;

  
  public static HashingSourcezaq md5(Sourcezaq source) {
    return new HashingSourcezaq(source, "MD5");
  }

  
  public static HashingSourcezaq sha1(Sourcezaq source) {
    return new HashingSourcezaq(source, "SHA-1");
  }

  
  public static HashingSourcezaq sha256(Sourcezaq source) {
    return new HashingSourcezaq(source, "SHA-256");
  }

  
  public static HashingSourcezaq hmacSha1(Sourcezaq source, ByteStringzaq key) {
    return new HashingSourcezaq(source, key, "HmacSHA1");
  }

  
  public static HashingSourcezaq hmacSha256(Sourcezaq source, ByteStringzaq key) {
    return new HashingSourcezaq(source, key, "HmacSHA256");
  }

  private HashingSourcezaq(Sourcezaq source, String algorithm) {
    super(source);
    try {
      this.messageDigest = MessageDigest.getInstance(algorithm);
      this.mac = null;
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    }
  }

  private HashingSourcezaq(Sourcezaq source, ByteStringzaq key, String algorithm) {
    super(source);
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

  @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
    long result = super.read(sink, byteCount);

    if (result != -1L) {
      long start = sink.size - result;


      long offset = sink.size;
      Segmentzaq s = sink.head;
      while (offset > start) {
        s = s.prev;
        offset -= (s.limit - s.pos);
      }


      while (offset < sink.size) {
        int pos = (int) (s.pos + start - offset);
        if (messageDigest != null) {
          messageDigest.update(s.data, pos, s.limit - pos);
        } else {
          mac.update(s.data, pos, s.limit - pos);
        }
        offset += (s.limit - s.pos);
        start = offset;
        s = s.next;
      }
    }

    return result;
  }

  
  public ByteStringzaq hash() {
    byte[] result = messageDigest != null ? messageDigest.digest() : mac.doFinal();
    return ByteStringzaq.of(result);
  }
}
