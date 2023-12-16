
package com.xxx.zzz.aall.okhttp3ll.internalss.http2;

import java.util.Arrays;


public final class Settingsua {
  
  static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;

  
  static final int HEADER_TABLE_SIZE = 1;
  
  static final int ENABLE_PUSH = 2;
  
  static final int MAX_CONCURRENT_STREAMS = 4;
  
  static final int MAX_FRAME_SIZE = 5;
  
  static final int MAX_HEADER_LIST_SIZE = 6;
  
  static final int INITIAL_WINDOW_SIZE = 7;

  
  static final int COUNT = 10;

  
  private int set;

  
  private final int[] values = new int[COUNT];

  void clear() {
    set = 0;
    Arrays.fill(values, 0);
  }

  Settingsua set(int id, int value) {
    if (id >= values.length) {
      return this;
    }

    int bit = 1 << id;
    set |= bit;
    values[id] = value;
    return this;
  }

  
  boolean isSet(int id) {
    int bit = 1 << id;
    return (set & bit) != 0;
  }

  
  int get(int id) {
    return values[id];
  }

  
  int size() {
    return Integer.bitCount(set);
  }

  
  int getHeaderTableSize() {
    int bit = 1 << HEADER_TABLE_SIZE;
    return (bit & set) != 0 ? values[HEADER_TABLE_SIZE] : -1;
  }


  boolean getEnablePush(boolean defaultValue) {
    int bit = 1 << ENABLE_PUSH;
    return ((bit & set) != 0 ? values[ENABLE_PUSH] : defaultValue ? 1 : 0) == 1;
  }

  int getMaxConcurrentStreams(int defaultValue) {
    int bit = 1 << MAX_CONCURRENT_STREAMS;
    return (bit & set) != 0 ? values[MAX_CONCURRENT_STREAMS] : defaultValue;
  }

  int getMaxFrameSize(int defaultValue) {
    int bit = 1 << MAX_FRAME_SIZE;
    return (bit & set) != 0 ? values[MAX_FRAME_SIZE] : defaultValue;
  }

  int getMaxHeaderListSize(int defaultValue) {
    int bit = 1 << MAX_HEADER_LIST_SIZE;
    return (bit & set) != 0 ? values[MAX_HEADER_LIST_SIZE] : defaultValue;
  }

  int getInitialWindowSize() {
    int bit = 1 << INITIAL_WINDOW_SIZE;
    return (bit & set) != 0 ? values[INITIAL_WINDOW_SIZE] : DEFAULT_INITIAL_WINDOW_SIZE;
  }

  
  void merge(Settingsua other) {
    for (int i = 0; i < COUNT; i++) {
      if (!other.isSet(i)) continue;
      set(i, other.get(i));
    }
  }
}
