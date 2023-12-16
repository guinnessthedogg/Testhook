
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xxx.zzz.aall.okhttp3ll.internalss.Utilaq;
import com.xxx.zzz.aall.okioss.Bufferzaq;
import com.xxx.zzz.aall.okioss.BufferedSourcezaqdfs;
import com.xxx.zzz.aall.okioss.ByteStringzaq;
import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;


final class Hpacka {
  private static final int PREFIX_4_BITS = 0x0f;
  private static final int PREFIX_5_BITS = 0x1f;
  private static final int PREFIX_6_BITS = 0x3f;
  private static final int PREFIX_7_BITS = 0x7f;

  static final Headera[] STATIC_HEADER_TABLE = new Headera[] {
      new Headera(Headera.TARGET_AUTHORITY, ""),
      new Headera(Headera.TARGET_METHOD, "GET"),
      new Headera(Headera.TARGET_METHOD, "POST"),
      new Headera(Headera.TARGET_PATH, "/"),
      new Headera(Headera.TARGET_PATH, "/index.html"),
      new Headera(Headera.TARGET_SCHEME, "http"),
      new Headera(Headera.TARGET_SCHEME, "https"),
      new Headera(Headera.RESPONSE_STATUS, "200"),
      new Headera(Headera.RESPONSE_STATUS, "204"),
      new Headera(Headera.RESPONSE_STATUS, "206"),
      new Headera(Headera.RESPONSE_STATUS, "304"),
      new Headera(Headera.RESPONSE_STATUS, "400"),
      new Headera(Headera.RESPONSE_STATUS, "404"),
      new Headera(Headera.RESPONSE_STATUS, "500"),
      new Headera("accept-charset", ""),
      new Headera("accept-encoding", "gzip, deflate"),
      new Headera("accept-language", ""),
      new Headera("accept-ranges", ""),
      new Headera("accept", ""),
      new Headera("access-control-allow-origin", ""),
      new Headera("age", ""),
      new Headera("allow", ""),
      new Headera("authorization", ""),
      new Headera("cache-control", ""),
      new Headera("content-disposition", ""),
      new Headera("content-encoding", ""),
      new Headera("content-language", ""),
      new Headera("content-length", ""),
      new Headera("content-location", ""),
      new Headera("content-range", ""),
      new Headera("content-type", ""),
      new Headera("cookie", ""),
      new Headera("date", ""),
      new Headera("etag", ""),
      new Headera("expect", ""),
      new Headera("expires", ""),
      new Headera("from", ""),
      new Headera("host", ""),
      new Headera("if-match", ""),
      new Headera("if-modified-since", ""),
      new Headera("if-none-match", ""),
      new Headera("if-range", ""),
      new Headera("if-unmodified-since", ""),
      new Headera("last-modified", ""),
      new Headera("link", ""),
      new Headera("location", ""),
      new Headera("max-forwards", ""),
      new Headera("proxy-authenticate", ""),
      new Headera("proxy-authorization", ""),
      new Headera("range", ""),
      new Headera("referer", ""),
      new Headera("refresh", ""),
      new Headera("retry-after", ""),
      new Headera("server", ""),
      new Headera("set-cookie", ""),
      new Headera("strict-transport-security", ""),
      new Headera("transfer-encoding", ""),
      new Headera("user-agent", ""),
      new Headera("vary", ""),
      new Headera("via", ""),
      new Headera("www-authenticate", "")
  };

  private Hpacka() {
  }


  static final class Reader {

    private final List<Headera> headerList = new ArrayList<>();
    private final BufferedSourcezaqdfs source;

    private final int headerTableSizeSetting;
    private int maxDynamicTableByteCount;


    Headera[] dynamicTable = new Headera[8];

    int nextHeaderIndex = dynamicTable.length - 1;
    int headerCount = 0;
    int dynamicTableByteCount = 0;

    Reader(int headerTableSizeSetting, Sourcezaq source) {
      this(headerTableSizeSetting, headerTableSizeSetting, source);
    }

    Reader(int headerTableSizeSetting, int maxDynamicTableByteCount, Sourcezaq source) {
      this.headerTableSizeSetting = headerTableSizeSetting;
      this.maxDynamicTableByteCount = maxDynamicTableByteCount;
      this.source = Okiozaq.buffer(source);
    }

    int maxDynamicTableByteCount() {
      return maxDynamicTableByteCount;
    }

    private void adjustDynamicTableByteCount() {
      if (maxDynamicTableByteCount < dynamicTableByteCount) {
        if (maxDynamicTableByteCount == 0) {
          clearDynamicTable();
        } else {
          evictToRecoverBytes(dynamicTableByteCount - maxDynamicTableByteCount);
        }
      }
    }

    private void clearDynamicTable() {
      Arrays.fill(dynamicTable, null);
      nextHeaderIndex = dynamicTable.length - 1;
      headerCount = 0;
      dynamicTableByteCount = 0;
    }

    
    private int evictToRecoverBytes(int bytesToRecover) {
      int entriesToEvict = 0;
      if (bytesToRecover > 0) {

        for (int j = dynamicTable.length - 1; j >= nextHeaderIndex && bytesToRecover > 0; j--) {
          bytesToRecover -= dynamicTable[j].hpackSize;
          dynamicTableByteCount -= dynamicTable[j].hpackSize;
          headerCount--;
          entriesToEvict++;
        }
        System.arraycopy(dynamicTable, nextHeaderIndex + 1, dynamicTable,
            nextHeaderIndex + 1 + entriesToEvict, headerCount);
        nextHeaderIndex += entriesToEvict;
      }
      return entriesToEvict;
    }

    
    void readHeaders() throws IOException {
      while (!source.exhausted()) {
        int b = source.readByte() & 0xff;
        if (b == 0x80) {
          throw new IOException("index == 0");
        } else if ((b & 0x80) == 0x80) {
          int index = readInt(b, PREFIX_7_BITS);
          readIndexedHeader(index - 1);
        } else if (b == 0x40) {
          readLiteralHeaderWithIncrementalIndexingNewName();
        } else if ((b & 0x40) == 0x40) {
          int index = readInt(b, PREFIX_6_BITS);
          readLiteralHeaderWithIncrementalIndexingIndexedName(index - 1);
        } else if ((b & 0x20) == 0x20) {
          maxDynamicTableByteCount = readInt(b, PREFIX_5_BITS);
          if (maxDynamicTableByteCount < 0
              || maxDynamicTableByteCount > headerTableSizeSetting) {
            throw new IOException("Invalid dynamic table size update " + maxDynamicTableByteCount);
          }
          adjustDynamicTableByteCount();
        } else if (b == 0x10 || b == 0) {
          readLiteralHeaderWithoutIndexingNewName();
        } else {
          int index = readInt(b, PREFIX_4_BITS);
          readLiteralHeaderWithoutIndexingIndexedName(index - 1);
        }
      }
    }

    public List<Headera> getAndResetHeaderList() {
      List<Headera> result = new ArrayList<>(headerList);
      headerList.clear();
      return result;
    }

    private void readIndexedHeader(int index) throws IOException {
      if (isStaticHeader(index)) {
        Headera staticEntry = STATIC_HEADER_TABLE[index];
        headerList.add(staticEntry);
      } else {
        int dynamicTableIndex = dynamicTableIndex(index - STATIC_HEADER_TABLE.length);
        if (dynamicTableIndex < 0 || dynamicTableIndex > dynamicTable.length - 1) {
          throw new IOException("Header index too large " + (index + 1));
        }
        headerList.add(dynamicTable[dynamicTableIndex]);
      }
    }


    private int dynamicTableIndex(int index) {
      return nextHeaderIndex + 1 + index;
    }

    private void readLiteralHeaderWithoutIndexingIndexedName(int index) throws IOException {
      ByteStringzaq name = getName(index);
      ByteStringzaq value = readByteString();
      headerList.add(new Headera(name, value));
    }

    private void readLiteralHeaderWithoutIndexingNewName() throws IOException {
      ByteStringzaq name = checkLowercase(readByteString());
      ByteStringzaq value = readByteString();
      headerList.add(new Headera(name, value));
    }

    private void readLiteralHeaderWithIncrementalIndexingIndexedName(int nameIndex)
        throws IOException {
      ByteStringzaq name = getName(nameIndex);
      ByteStringzaq value = readByteString();
      insertIntoDynamicTable(-1, new Headera(name, value));
    }

    private void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
      ByteStringzaq name = checkLowercase(readByteString());
      ByteStringzaq value = readByteString();
      insertIntoDynamicTable(-1, new Headera(name, value));
    }

    private ByteStringzaq getName(int index) {
      if (isStaticHeader(index)) {
        return STATIC_HEADER_TABLE[index].name;
      } else {
        return dynamicTable[dynamicTableIndex(index - STATIC_HEADER_TABLE.length)].name;
      }
    }

    private boolean isStaticHeader(int index) {
      return index >= 0 && index <= STATIC_HEADER_TABLE.length - 1;
    }

    
    private void insertIntoDynamicTable(int index, Headera entry) {
      headerList.add(entry);

      int delta = entry.hpackSize;
      if (index != -1) {
        delta -= dynamicTable[dynamicTableIndex(index)].hpackSize;
      }


      if (delta > maxDynamicTableByteCount) {
        clearDynamicTable();
        return;
      }


      int bytesToRecover = (dynamicTableByteCount + delta) - maxDynamicTableByteCount;
      int entriesEvicted = evictToRecoverBytes(bytesToRecover);

      if (index == -1) {
        if (headerCount + 1 > dynamicTable.length) { 
          Headera[] doubled = new Headera[dynamicTable.length * 2];
          System.arraycopy(dynamicTable, 0, doubled, dynamicTable.length, dynamicTable.length);
          nextHeaderIndex = dynamicTable.length - 1;
          dynamicTable = doubled;
        }
        index = nextHeaderIndex--;
        dynamicTable[index] = entry;
        headerCount++;
      } else { 
        index += dynamicTableIndex(index) + entriesEvicted;
        dynamicTable[index] = entry;
      }
      dynamicTableByteCount += delta;
    }

    private int readByte() throws IOException {
      return source.readByte() & 0xff;
    }

    int readInt(int firstByte, int prefixMask) throws IOException {
      int prefix = firstByte & prefixMask;
      if (prefix < prefixMask) {
        return prefix; 
      }

      
      int result = prefixMask;
      int shift = 0;
      while (true) {
        int b = readByte();
        if ((b & 0x80) != 0) { 
          result += (b & 0x7f) << shift;
          shift += 7;
        } else {
          result += b << shift; 
          break;
        }
      }
      return result;
    }

    
    ByteStringzaq readByteString() throws IOException {
      int firstByte = readByte();
      boolean huffmanDecode = (firstByte & 0x80) == 0x80; 
      int length = readInt(firstByte, PREFIX_7_BITS);

      if (huffmanDecode) {
        return ByteStringzaq.of(Huffmana.get().decode(source.readByteArray(length)));
      } else {
        return source.readByteString(length);
      }
    }
  }

  static final Map<ByteStringzaq, Integer> NAME_TO_FIRST_INDEX = nameToFirstIndex();

  private static Map<ByteStringzaq, Integer> nameToFirstIndex() {
    Map<ByteStringzaq, Integer> result = new LinkedHashMap<>(STATIC_HEADER_TABLE.length);
    for (int i = 0; i < STATIC_HEADER_TABLE.length; i++) {
      if (!result.containsKey(STATIC_HEADER_TABLE[i].name)) {
        result.put(STATIC_HEADER_TABLE[i].name, i);
      }
    }
    return Collections.unmodifiableMap(result);
  }

  static final class Writer {
    private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;

    
    private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;

    private final Bufferzaq out;
    private final boolean useCompression;

    
    private int smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
    private boolean emitDynamicTableSizeUpdate;

    int headerTableSizeSetting;
    int maxDynamicTableByteCount;

    
    Headera[] dynamicTable = new Headera[8];
    
    int nextHeaderIndex = dynamicTable.length - 1;
    int headerCount = 0;
    int dynamicTableByteCount = 0;

    Writer(Bufferzaq out) {
      this(SETTINGS_HEADER_TABLE_SIZE, true, out);
    }

    Writer(int headerTableSizeSetting, boolean useCompression, Bufferzaq out) {
      this.headerTableSizeSetting = headerTableSizeSetting;
      this.maxDynamicTableByteCount = headerTableSizeSetting;
      this.useCompression = useCompression;
      this.out = out;
    }

    private void clearDynamicTable() {
      Arrays.fill(dynamicTable, null);
      nextHeaderIndex = dynamicTable.length - 1;
      headerCount = 0;
      dynamicTableByteCount = 0;
    }

    
    private int evictToRecoverBytes(int bytesToRecover) {
      int entriesToEvict = 0;
      if (bytesToRecover > 0) {
        
        for (int j = dynamicTable.length - 1; j >= nextHeaderIndex && bytesToRecover > 0; j--) {
          bytesToRecover -= dynamicTable[j].hpackSize;
          dynamicTableByteCount -= dynamicTable[j].hpackSize;
          headerCount--;
          entriesToEvict++;
        }
        System.arraycopy(dynamicTable, nextHeaderIndex + 1, dynamicTable,
            nextHeaderIndex + 1 + entriesToEvict, headerCount);
        Arrays.fill(dynamicTable, nextHeaderIndex + 1, nextHeaderIndex + 1 + entriesToEvict, null);
        nextHeaderIndex += entriesToEvict;
      }
      return entriesToEvict;
    }

    private void insertIntoDynamicTable(Headera entry) {
      int delta = entry.hpackSize;

      
      if (delta > maxDynamicTableByteCount) {
        clearDynamicTable();
        return;
      }

      
      int bytesToRecover = (dynamicTableByteCount + delta) - maxDynamicTableByteCount;
      evictToRecoverBytes(bytesToRecover);

      if (headerCount + 1 > dynamicTable.length) { 
        Headera[] doubled = new Headera[dynamicTable.length * 2];
        System.arraycopy(dynamicTable, 0, doubled, dynamicTable.length, dynamicTable.length);
        nextHeaderIndex = dynamicTable.length - 1;
        dynamicTable = doubled;
      }
      int index = nextHeaderIndex--;
      dynamicTable[index] = entry;
      headerCount++;
      dynamicTableByteCount += delta;
    }

    
    
    void writeHeaders(List<Headera> headerBlock) throws IOException {
      if (emitDynamicTableSizeUpdate) {
        if (smallestHeaderTableSizeSetting < maxDynamicTableByteCount) {
          
          writeInt(smallestHeaderTableSizeSetting, PREFIX_5_BITS, 0x20);
        }
        emitDynamicTableSizeUpdate = false;
        smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
        writeInt(maxDynamicTableByteCount, PREFIX_5_BITS, 0x20);
      }

      for (int i = 0, size = headerBlock.size(); i < size; i++) {
        Headera header = headerBlock.get(i);
        ByteStringzaq name = header.name.toAsciiLowercase();
        ByteStringzaq value = header.value;
        int headerIndex = -1;
        int headerNameIndex = -1;

        Integer staticIndex = NAME_TO_FIRST_INDEX.get(name);
        if (staticIndex != null) {
          headerNameIndex = staticIndex + 1;
          if (headerNameIndex > 1 && headerNameIndex < 8) {
            
            
            
            
            if (Utilaq.equal(STATIC_HEADER_TABLE[headerNameIndex - 1].value, value)) {
              headerIndex = headerNameIndex;
            } else if (Utilaq.equal(STATIC_HEADER_TABLE[headerNameIndex].value, value)) {
              headerIndex = headerNameIndex + 1;
            }
          }
        }

        if (headerIndex == -1) {
          for (int j = nextHeaderIndex + 1, length = dynamicTable.length; j < length; j++) {
            if (Utilaq.equal(dynamicTable[j].name, name)) {
              if (Utilaq.equal(dynamicTable[j].value, value)) {
                headerIndex = j - nextHeaderIndex + STATIC_HEADER_TABLE.length;
                break;
              } else if (headerNameIndex == -1) {
                headerNameIndex = j - nextHeaderIndex + STATIC_HEADER_TABLE.length;
              }
            }
          }
        }

        if (headerIndex != -1) {
          
          writeInt(headerIndex, PREFIX_7_BITS, 0x80);
        } else if (headerNameIndex == -1) {
          
          out.writeByte(0x40);
          writeByteString(name);
          writeByteString(value);
          insertIntoDynamicTable(header);
        } else if (name.startsWith(Headera.PSEUDO_PREFIX) && !Headera.TARGET_AUTHORITY.equals(name)) {
          
          
          writeInt(headerNameIndex, PREFIX_4_BITS, 0);
          writeByteString(value);
        } else {
          
          writeInt(headerNameIndex, PREFIX_6_BITS, 0x40);
          writeByteString(value);
          insertIntoDynamicTable(header);
        }
      }
    }

    
    void writeInt(int value, int prefixMask, int bits) {
      
      if (value < prefixMask) {
        out.writeByte(bits | value);
        return;
      }

      
      out.writeByte(bits | prefixMask);
      value -= prefixMask;

      
      while (value >= 0x80) {
        int b = value & 0x7f;
        out.writeByte(b | 0x80);
        value >>>= 7;
      }
      out.writeByte(value);
    }

    void writeByteString(ByteStringzaq data) throws IOException {
      if (useCompression && Huffmana.get().encodedLength(data) < data.size()) {
        Bufferzaq huffmanBuffer = new Bufferzaq();
        Huffmana.get().encode(data, huffmanBuffer);
        ByteStringzaq huffmanBytes = huffmanBuffer.readByteString();
        writeInt(huffmanBytes.size(), PREFIX_7_BITS, 0x80);
        out.write(huffmanBytes);
      } else {
        writeInt(data.size(), PREFIX_7_BITS, 0);
        out.write(data);
      }
    }

    void setHeaderTableSizeSetting(int headerTableSizeSetting) {
      this.headerTableSizeSetting = headerTableSizeSetting;
      int effectiveHeaderTableSize = Math.min(headerTableSizeSetting,
          SETTINGS_HEADER_TABLE_SIZE_LIMIT);

      if (maxDynamicTableByteCount == effectiveHeaderTableSize) return; 

      if (effectiveHeaderTableSize < maxDynamicTableByteCount) {
        smallestHeaderTableSizeSetting = Math.min(smallestHeaderTableSizeSetting,
            effectiveHeaderTableSize);
      }
      emitDynamicTableSizeUpdate = true;
      maxDynamicTableByteCount = effectiveHeaderTableSize;
      adjustDynamicTableByteCount();
    }

    private void adjustDynamicTableByteCount() {
      if (maxDynamicTableByteCount < dynamicTableByteCount) {
        if (maxDynamicTableByteCount == 0) {
          clearDynamicTable();
        } else {
          evictToRecoverBytes(dynamicTableByteCount - maxDynamicTableByteCount);
        }
      }
    }
  }

  
  static ByteStringzaq checkLowercase(ByteStringzaq name) throws IOException {
    for (int i = 0, length = name.size(); i < length; i++) {
      byte c = name.getByte(i);
      if (c >= 'A' && c <= 'Z') {
        throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + name.utf8());
      }
    }
    return name;
  }
}
