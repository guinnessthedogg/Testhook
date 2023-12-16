
package com.xxx.zzz.aall.okhttp3ll.internalss.publicsuffix;

import static com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq.closeQuietly;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okhttp3ll.internalss.platformsss.Platformq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.GzipSourcezaq;
import com.xxx.zzz.aall.okioss.Okiozaq;


public final class PublicSuffixDatabase {
  public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";

  private static final byte[] WILDCARD_LABEL = new byte[]{'*'};
  private static final String[] EMPTY_RULE = new String[0];
  private static final String[] PREVAILING_RULE = new String[]{"*"};

  private static final byte EXCEPTION_MARKER = '!';

  private static final PublicSuffixDatabase instance = new PublicSuffixDatabase();

  
  private final AtomicBoolean listRead = new AtomicBoolean(false);

  
  private final CountDownLatch readCompleteLatch = new CountDownLatch(1);

  
  
  
  
  private byte[] publicSuffixListBytes;
  private byte[] publicSuffixExceptionListBytes;

  public static PublicSuffixDatabase get() {
    return instance;
  }

  
  public String getEffectiveTldPlusOne(String domain) {
    if (domain == null) throw new NullPointerException("domain == null");

    
    String unicodeDomain = IDN.toUnicode(domain);
    String[] domainLabels = unicodeDomain.split("\\.");
    String[] rule = findMatchingRule(domainLabels);
    if (domainLabels.length == rule.length && rule[0].charAt(0) != EXCEPTION_MARKER) {
      
      return null;
    }

    int firstLabelOffset;
    if (rule[0].charAt(0) == EXCEPTION_MARKER) {
      
      firstLabelOffset = domainLabels.length - rule.length;
    } else {
      
      firstLabelOffset = domainLabels.length - (rule.length + 1);
    }

    StringBuilder effectiveTldPlusOne = new StringBuilder();
    String[] punycodeLabels = domain.split("\\.");
    for (int i = firstLabelOffset; i < punycodeLabels.length; i++) {
      effectiveTldPlusOne.append(punycodeLabels[i]).append('.');
    }
    effectiveTldPlusOne.deleteCharAt(effectiveTldPlusOne.length() - 1);

    return effectiveTldPlusOne.toString();
  }

  private String[] findMatchingRule(String[] domainLabels) {
    if (!listRead.get() && listRead.compareAndSet(false, true)) {
      readTheList();
    } else {
      try {
        readCompleteLatch.await();
      } catch (InterruptedException ignored) {
      }
    }

    synchronized (this) {
      if (publicSuffixListBytes == null) {
        throw new IllegalStateException("Unable to load " + PUBLIC_SUFFIX_RESOURCE + " resource "
            + "from the classpath.");
      }
    }

    
    byte[][] domainLabelsUtf8Bytes = new byte[domainLabels.length][];
    for (int i = 0; i < domainLabels.length; i++) {
      domainLabelsUtf8Bytes[i] = domainLabels[i].getBytes(Utilaq.UTF_8);
    }

    
    
    String exactMatch = null;
    for (int i = 0; i < domainLabelsUtf8Bytes.length; i++) {
      String rule = binarySearchBytes(publicSuffixListBytes, domainLabelsUtf8Bytes, i);
      if (rule != null) {
        exactMatch = rule;
        break;
      }
    }

    
    
    
    
    
    String wildcardMatch = null;
    if (domainLabelsUtf8Bytes.length > 1) {
      byte[][] labelsWithWildcard = domainLabelsUtf8Bytes.clone();
      for (int labelIndex = 0; labelIndex < labelsWithWildcard.length - 1; labelIndex++) {
        labelsWithWildcard[labelIndex] = WILDCARD_LABEL;
        String rule = binarySearchBytes(publicSuffixListBytes, labelsWithWildcard, labelIndex);
        if (rule != null) {
          wildcardMatch = rule;
          break;
        }
      }
    }

    
    String exception = null;
    if (wildcardMatch != null) {
      for (int labelIndex = 0; labelIndex < domainLabelsUtf8Bytes.length - 1; labelIndex++) {
        String rule = binarySearchBytes(
            publicSuffixExceptionListBytes, domainLabelsUtf8Bytes, labelIndex);
        if (rule != null) {
          exception = rule;
          break;
        }
      }
    }

    if (exception != null) {
      
      exception = "!" + exception;
      return exception.split("\\.");
    } else if (exactMatch == null && wildcardMatch == null) {
      return PREVAILING_RULE;
    }

    String[] exactRuleLabels = exactMatch != null
        ? exactMatch.split("\\.")
        : EMPTY_RULE;

    String[] wildcardRuleLabels = wildcardMatch != null
        ? wildcardMatch.split("\\.")
        : EMPTY_RULE;

    return exactRuleLabels.length > wildcardRuleLabels.length
        ? exactRuleLabels
        : wildcardRuleLabels;
  }

  private static String binarySearchBytes(byte[] bytesToSearch, byte[][] labels, int labelIndex) {
    int low = 0;
    int high = bytesToSearch.length;
    String match = null;
    while (low < high) {
      int mid = (low + high) / 2;
      
      
      while (mid > -1 && bytesToSearch[mid] != '\n') {
        mid--;
      }
      mid++;

      
      int end = 1;
      while (bytesToSearch[mid + end] != '\n') {
        end++;
      }
      int publicSuffixLength = (mid + end) - mid;

      
      
      int compareResult;
      int currentLabelIndex = labelIndex;
      int currentLabelByteIndex = 0;
      int publicSuffixByteIndex = 0;

      boolean expectDot = false;
      while (true) {
        int byte0;
        if (expectDot) {
          byte0 = '.';
          expectDot = false;
        } else {
          byte0 = labels[currentLabelIndex][currentLabelByteIndex] & 0xff;
        }

        int byte1 = bytesToSearch[mid + publicSuffixByteIndex] & 0xff;

        compareResult = byte0 - byte1;
        if (compareResult != 0) break;

        publicSuffixByteIndex++;
        currentLabelByteIndex++;
        if (publicSuffixByteIndex == publicSuffixLength) break;

        if (labels[currentLabelIndex].length == currentLabelByteIndex) {
          
          
          if (currentLabelIndex == labels.length - 1) {
            break;
          } else {
            currentLabelIndex++;
            currentLabelByteIndex = -1;
            expectDot = true;
          }
        }
      }

      if (compareResult < 0) {
        high = mid - 1;
      } else if (compareResult > 0) {
        low = mid + end + 1;
      } else {
        
        int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
        int labelBytesLeft = labels[currentLabelIndex].length - currentLabelByteIndex;
        for (int i = currentLabelIndex + 1; i < labels.length; i++) {
          labelBytesLeft += labels[i].length;
        }

        if (labelBytesLeft < publicSuffixBytesLeft) {
          high = mid - 1;
        } else if (labelBytesLeft > publicSuffixBytesLeft) {
          low = mid + end + 1;
        } else {
          
          match = new String(bytesToSearch, mid, publicSuffixLength, Utilaq.UTF_8);
          break;
        }
      }
    }
    return match;
  }

  private void readTheList() {
    byte[] publicSuffixListBytes = null;
    byte[] publicSuffixExceptionListBytes = null;

    InputStream is = PublicSuffixDatabase.class.getClassLoader().getResourceAsStream(
        PUBLIC_SUFFIX_RESOURCE);

    if (is != null) {
      BufferedSourcezaqdfs bufferedSource = Okiozaq.buffer(new GzipSourcezaq(Okiozaq.source(is)));
      try {
        int totalBytes = bufferedSource.readInt();
        publicSuffixListBytes = new byte[totalBytes];
        bufferedSource.readFully(publicSuffixListBytes);

        int totalExceptionBytes = bufferedSource.readInt();
        publicSuffixExceptionListBytes = new byte[totalExceptionBytes];
        bufferedSource.readFully(publicSuffixExceptionListBytes);
      } catch (IOException e) {
        Platformq.get().log(Platformq.WARN, "Failed to read public suffix list", e);
        publicSuffixListBytes = null;
        publicSuffixExceptionListBytes = null;
      } finally {
        Utilaq.closeQuietly(bufferedSource);
      }
    }

    synchronized (this) {
      this.publicSuffixListBytes = publicSuffixListBytes;
      this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
    }

    readCompleteLatch.countDown();
  }

  
  void setListBytes(byte[] publicSuffixListBytes, byte[] publicSuffixExceptionListBytes) {
    this.publicSuffixListBytes = publicSuffixListBytes;
    this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
    listRead.set(true);
    readCompleteLatch.countDown();
  }
}
