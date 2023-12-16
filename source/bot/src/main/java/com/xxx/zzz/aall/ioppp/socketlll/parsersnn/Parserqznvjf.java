package com.xxx.zzz.aall.ioppp.socketlll.parsersnn;

public interface Parserqznvjf {


    public static final int CONNECT = 0;


    public static final int DISCONNECT = 1;


    public static final int EVENT = 2;


    public static final int ACK = 3;


    public static final int ERROR = 4;


    public static final int BINARY_EVENT = 5;


    public static final int BINARY_ACK = 6;

    public static int protocol = 4;


    public static String[] types = new String[] {
        "CONNECT",
        "DISCONNECT",
        "EVENT",
        "ACK",
        "ERROR",
        "BINARY_EVENT",
        "BINARY_ACK"
    };

    public static interface Encoder {

        public void encode(Packetqxcbd obj, Callback callback);

        public interface Callback {

            public void call(Object[] data);
        }
    }

    public static interface Decoder {

        public void add(String obj);

        public void add(byte[] obj);

        public void destroy();

        public void onDecoded(Callback callback);

        public interface Callback {

            public void call(Packetqxcbd packet);
        }
    }
}


