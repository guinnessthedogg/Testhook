package com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn;


import java.nio.ByteBuffer;


class Budsfferq {

    private Budsfferq() {}

    public static byte[] concat(byte[][] list) {
        int length = 0;
        for (byte[] buf : list) {
            length += buf.length;
        }
        return concat(list, length);
    }

    public static byte[] concat(byte[][] list, int length) {
        if (list.length == 0) {
            return new byte[0];
        } else if (list.length == 1) {
            return list[0];
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (byte[] buf : list) {
            buffer.put(buf);
        }

        return buffer.array();
    }
}
