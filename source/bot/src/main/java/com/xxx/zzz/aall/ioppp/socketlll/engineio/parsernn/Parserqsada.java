package com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn;


import com.xxx.zzz.aall.ioppp.socketlll.utf8nnn.UTF8z;
import com.xxx.zzz.aall.ioppp.socketlll.utf8nnn.UTF8Exceptionz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parserqsada {

    private static final int MAX_INT_CHAR_LENGTH = String.valueOf(Integer.MAX_VALUE).length();

    public static final int PROTOCOL = 3;

    private static final Map<String, Integer> packets = new HashMap<String, Integer>() {{
        put(Packeastq.OPEN, 0);
        put(Packeastq.CLOSE, 1);
        put(Packeastq.PING, 2);
        put(Packeastq.PONG, 3);
        put(Packeastq.MESSAGE, 4);
        put(Packeastq.UPGRADE, 5);
        put(Packeastq.NOOP, 6);
    }};

    private static final Map<Integer, String> packetslist = new HashMap<Integer, String>();
    static {
        for (Map.Entry<String, Integer> entry : packets.entrySet()) {
            packetslist.put(entry.getValue(), entry.getKey());
        }
    }

    private static Packeastq<String> err = new Packeastq<String>(Packeastq.ERROR, "parser error");

    private static UTF8z.Options utf8Options = new UTF8z.Options();
    static  {
        utf8Options.strict = false;
    }


    private Parserqsada() {}

    public static void encodePacket(Packeastq packet, EncodeCallback callback) throws UTF8Exceptionz {
        encodePacket(packet, false, callback);
    }

    public static void encodePacket(Packeastq packet, boolean utf8encode, EncodeCallback callback) throws UTF8Exceptionz {
        if (packet.data instanceof byte[]) {
            @SuppressWarnings("unchecked")
            Packeastq<byte[]> packetToEncode = packet;
            @SuppressWarnings("unchecked")
            EncodeCallback<byte[]> callbackToEncode = callback;
            encodeByteArray(packetToEncode, callbackToEncode);
            return;
        }

        String encoded = String.valueOf(packets.get(packet.type));

        if (null != packet.data) {
            encoded += utf8encode ? UTF8z.encode(String.valueOf(packet.data), utf8Options) : String.valueOf(packet.data);
        }

        @SuppressWarnings("unchecked")
        EncodeCallback<String> tempCallback = callback;
        tempCallback.call(encoded);
    }

    private static void encodeByteArray(Packeastq<byte[]> packet, EncodeCallback<byte[]> callback) {
        byte[] data = packet.data;
        byte[] resultArray = new byte[1 + data.length];
        resultArray[0] = packets.get(packet.type).byteValue();
        System.arraycopy(data, 0, resultArray, 1, data.length);
        callback.call(resultArray);
    }

    public static Packeastq<String> decodePacket(String data) {
        return decodePacket(data, false);
    }

    public static Packeastq<String> decodePacket(String data, boolean utf8decode) {
        if (data == null) {
            return err;
        }

        int type;
        try {
            type = Character.getNumericValue(data.charAt(0));
        } catch (IndexOutOfBoundsException e) {
            type = -1;
        }

        if (utf8decode) {
            try {
                data = UTF8z.decode(data, utf8Options);
            } catch (UTF8Exceptionz e) {
                return err;
            }
        }

        if (type < 0 || type >= packetslist.size()) {
            return err;
        }

        if (data.length() > 1) {
            return new Packeastq<String>(packetslist.get(type), data.substring(1));
        } else {
            return new Packeastq<String>(packetslist.get(type));
        }
    }

    public static Packeastq<byte[]> decodePacket(byte[] data) {
        int type = data[0];
        byte[] intArray = new byte[data.length - 1];
        System.arraycopy(data, 1, intArray, 0, intArray.length);
        return new Packeastq<byte[]>(packetslist.get(type), intArray);
    }

    public static void encodePayload(Packeastq[] packets, EncodeCallback callback) throws UTF8Exceptionz {
        for (Packeastq packet : packets) {
            if (packet.data instanceof byte[]) {
                @SuppressWarnings("unchecked")
                EncodeCallback<byte[]> _callback = (EncodeCallback<byte[]>) callback;
                encodePayloadAsBinary(packets, _callback);
                return;
            }
        }

        if (packets.length == 0) {
            callback.call("0:");
            return;
        }

        final StringBuilder result = new StringBuilder();

        for (Packeastq packet : packets) {
            encodePacket(packet, false, new EncodeCallback() {
                @Override
                public void call(Object message) {
                    result.append(setLengthHeader((String)message));
                }
            });
        }

        callback.call(result.toString());
    }

    private static String setLengthHeader(String message) {
        return message.length() + ":" + message;
    }

    private static void encodePayloadAsBinary(Packeastq[] packets, EncodeCallback<byte[]> callback) throws UTF8Exceptionz {
        if (packets.length == 0) {
            callback.call(new byte[0]);
            return;
        }

        final ArrayList<byte[]> results = new ArrayList<byte[]>(packets.length);

        for (Packeastq packet : packets) {
            encodeOneBinaryPacket(packet, new EncodeCallback<byte[]>() {
                @Override
                public void call(byte[] data) {
                    results.add(data);
                }
            });
        }

        callback.call(Budsfferq.concat(results.toArray(new byte[results.size()][])));
    }

    private static void encodeOneBinaryPacket(Packeastq p, final EncodeCallback<byte[]> doneCallback) throws UTF8Exceptionz {
        encodePacket(p, true, new EncodeCallback() {
            @Override
            public void call(Object packet) {
                if (packet instanceof String) {
                    String encodingLength = String.valueOf(((String) packet).length());
                    byte[] sizeBuffer = new byte[encodingLength.length() + 2];

                    sizeBuffer[0] = (byte)0;
                    for (int i = 0; i < encodingLength.length(); i ++) {
                        sizeBuffer[i + 1] = (byte)Character.getNumericValue(encodingLength.charAt(i));
                    }
                    sizeBuffer[sizeBuffer.length - 1] = (byte)255;
                    doneCallback.call(Budsfferq.concat(new byte[][] {sizeBuffer, stringToByteArray((String)packet)}));
                    return;
                }

                String encodingLength = String.valueOf(((byte[])packet).length);
                byte[] sizeBuffer = new byte[encodingLength.length() + 2];
                sizeBuffer[0] = (byte)1;
                for (int i = 0; i < encodingLength.length(); i ++) {
                    sizeBuffer[i + 1] = (byte)Character.getNumericValue(encodingLength.charAt(i));
                }
                sizeBuffer[sizeBuffer.length - 1] = (byte)255;
                doneCallback.call(Budsfferq.concat(new byte[][] {sizeBuffer, (byte[])packet}));
            }
        });
    }

    public static void decodePayload(String data, DecodePayloadCallback<String> callback) {
        if (data == null || data.length() == 0) {
            callback.call(err, 0, 1);
            return;
        }

        StringBuilder length = new StringBuilder();
        for (int i = 0, l = data.length(); i < l; i++) {
            char chr = data.charAt(i);

            if (':' != chr) {
                length.append(chr);
                continue;
            }

            int n;
            try {
                n = Integer.parseInt(length.toString());
            } catch (NumberFormatException e) {
                callback.call(err, 0, 1);
                return;
            }

            String msg;
            try {
                msg = data.substring(i + 1, i + 1 + n);
            } catch (IndexOutOfBoundsException e) {
                callback.call(err, 0, 1);
                return;
            }

            if (msg.length() != 0) {
                Packeastq<String> packet = decodePacket(msg, false);
                if (err.type.equals(packet.type) && err.data.equals(packet.data)) {
                    callback.call(err, 0, 1);
                    return;
                }

                boolean ret = callback.call(packet, i + n, l);
                if (!ret) {
                    return;
                }
            }

            i += n;
            length = new StringBuilder();
        }

        if (length.length() > 0) {
            callback.call(err, 0, 1);
        }
    }

    public static void decodePayload(byte[] data, DecodePayloadCallback callback) {
        ByteBuffer bufferTail = ByteBuffer.wrap(data);
        List<Object> buffers = new ArrayList<Object>();

        while (bufferTail.capacity() > 0) {
            StringBuilder strLen = new StringBuilder();
            boolean isString = (bufferTail.get(0) & 0xFF) == 0;
            for (int i = 1; ; i++) {
                int b = bufferTail.get(i) & 0xFF;
                if (b == 255) break;

                if (strLen.length() > MAX_INT_CHAR_LENGTH) {
                    callback.call(err, 0, 1);
                    return;
                }
                strLen.append(b);
            }

            bufferTail.position(strLen.length() + 1);
            bufferTail = bufferTail.slice();

            int msgLength = Integer.parseInt(strLen.toString());

            bufferTail.position(1);
            bufferTail.limit(msgLength + 1);
            byte[] msg = new byte[bufferTail.remaining()];
            bufferTail.get(msg);
            if (isString) {
                buffers.add(byteArrayToString(msg));
            } else {
                buffers.add(msg);
            }
            bufferTail.clear();
            bufferTail.position(msgLength + 1);
            bufferTail = bufferTail.slice();
        }

        int total = buffers.size();
        for (int i = 0; i < total; i++) {
            Object buffer = buffers.get(i);
            if (buffer instanceof String) {
                @SuppressWarnings("unchecked")
                DecodePayloadCallback<String> tempCallback = callback;
                tempCallback.call(decodePacket((String)buffer, true), i, total);
            } else if (buffer instanceof byte[]) {
                @SuppressWarnings("unchecked")
                DecodePayloadCallback<byte[]> tempCallback = callback;
                tempCallback.call(decodePacket((byte[])buffer), i, total);
            }
        }
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.appendCodePoint(b & 0xFF);
        }
        return builder.toString();
    }

    private static byte[] stringToByteArray(String string) {
        int len = string.length();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte)Character.codePointAt(string, i);
        }
        return bytes;
    }

    public static interface EncodeCallback<T> {

        public void call(T data);
    }


    public static interface DecodePayloadCallback<T> {

        public boolean call(Packeastq<T> packet, int index, int total);
    }
}
