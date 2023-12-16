package com.xxx.zzz.aall.ioppp.socketlll.parsersnn;

import com.xxx.zzz.aall.ioppp.socketlll.hasbinarynn.HasBinaryq;

import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class IOParserqsc implements Parserqznvjf {

    private static final Logger logger = Logger.getLogger(IOParserqsc.class.getName());

    private static Packetqxcbd<String> error() {
        return new Packetqxcbd<String>(ERROR, "parser error");
    }

    private IOParserqsc() {}

    final public static class Encoder implements Parserqznvjf.Encoder {

        public Encoder() {}

        @Override
        public void encode(Packetqxcbd obj, Callback callback) {
            if ((obj.type == EVENT || obj.type == ACK) && HasBinaryq.hasBinary(obj.data)) {
                obj.type = obj.type == EVENT ? BINARY_EVENT : BINARY_ACK;
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("encoding packet %s", obj));
            }

            if (BINARY_EVENT == obj.type || BINARY_ACK == obj.type) {
                encodeAsBinary(obj, callback);
            } else {
                String encoding = encodeAsString(obj);
                callback.call(new String[] {encoding});
            }
        }

        private String encodeAsString(Packetqxcbd obj) {
            StringBuilder str = new StringBuilder("" + obj.type);

            if (BINARY_EVENT == obj.type || BINARY_ACK == obj.type) {
                str.append(obj.attachments);
                str.append("-");
            }

            if (obj.nsp != null && obj.nsp.length() != 0 && !"/".equals(obj.nsp)) {
                str.append(obj.nsp);
                str.append(",");
            }

            if (obj.id >= 0) {
                str.append(obj.id);
            }

            if (obj.data != null) {
                str.append(obj.data);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("encoded %s as %s", obj, str));
            }
            return str.toString();
        }

        private void encodeAsBinary(Packetqxcbd obj, Callback callback) {
            Binaryqdsa.DeconstructedPacket deconstruction = Binaryqdsa.deconstructPacket(obj);
            String pack = encodeAsString(deconstruction.packet);
            List<Object> buffers = new ArrayList<Object>(Arrays.asList(deconstruction.buffers));

            buffers.add(0, pack);
            callback.call(buffers.toArray());
        }
    }

    final public static class Decoder implements Parserqznvjf.Decoder {

         BinaryReconstructor reconstructor;

        private Callback onDecodedCallback;

        public Decoder() {
            this.reconstructor = null;
        }

        @Override
        public void add(String obj) {
            Packetqxcbd packet = decodeString(obj);
            if (BINARY_EVENT == packet.type || BINARY_ACK == packet.type) {
                this.reconstructor = new BinaryReconstructor(packet);

                if (this.reconstructor.reconPack.attachments == 0) {
                    if (this.onDecodedCallback != null) {
                        this.onDecodedCallback.call(packet);
                    }
                }
            } else {
                if (this.onDecodedCallback != null) {
                    this.onDecodedCallback.call(packet);
                }
            }
        }

        @Override
        public void add(byte[] obj) {
            if (this.reconstructor == null) {
                throw new RuntimeException("got binary data when not reconstructing a packet");
            } else {
                Packetqxcbd packet = this.reconstructor.takeBinaryData(obj);
                if (packet != null) {
                    this.reconstructor = null;
                    if (this.onDecodedCallback != null) {
                        this.onDecodedCallback.call(packet);
                    }
                }
            }
        }

        private static Packetqxcbd decodeString(String str) {
            int i = 0;
            int length = str.length();

            Packetqxcbd<Object> p = new Packetqxcbd<Object>(Character.getNumericValue(str.charAt(0)));

            if (p.type < 0 || p.type > types.length - 1) return error();

            if (BINARY_EVENT == p.type || BINARY_ACK == p.type) {
                if (!str.contains("-") || length <= i + 1) return error();
                StringBuilder attachments = new StringBuilder();
                while (str.charAt(++i) != '-') {
                    attachments.append(str.charAt(i));
                }
                p.attachments = Integer.parseInt(attachments.toString());
            }

            if (length > i + 1 && '/' == str.charAt(i + 1)) {
                StringBuilder nsp = new StringBuilder();
                while (true) {
                    ++i;
                    char c = str.charAt(i);
                    if (',' == c) break;
                    nsp.append(c);
                    if (i + 1 == length) break;
                }
                p.nsp = nsp.toString();
            } else {
                p.nsp = "/";
            }

            if (length > i + 1){
                Character next = str.charAt(i + 1);
                if (Character.getNumericValue(next) > -1) {
                    StringBuilder id = new StringBuilder();
                    while (true) {
                        ++i;
                        char c = str.charAt(i);
                        if (Character.getNumericValue(c) < 0) {
                            --i;
                            break;
                        }
                        id.append(c);
                        if (i + 1 == length) break;
                    }
                    try {
                        p.id = Integer.parseInt(id.toString());
                    } catch (NumberFormatException e){
                        return error();
                    }
                }
            }

            if (length > i + 1){
                try {
                    str.charAt(++i);
                    p.data = new JSONTokener(str.substring(i)).nextValue();
                } catch (JSONException e) {
                    logger.log(Level.WARNING, "An error occured while retrieving data from JSONTokener", e);
                    return error();
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("decoded %s as %s", str, p));
            }
            return p;
        }

        @Override
        public void destroy() {
            if (this.reconstructor != null) {
                this.reconstructor.finishReconstruction();
            }
            this.onDecodedCallback = null;
        }

        @Override
        public void onDecoded (Callback callback) {
            this.onDecodedCallback = callback;
        }
    }


     static class BinaryReconstructor {

        public Packetqxcbd reconPack;

         List<byte[]> buffers;

        BinaryReconstructor(Packetqxcbd packet) {
            this.reconPack = packet;
            this.buffers = new ArrayList<byte[]>();
        }

        public Packetqxcbd takeBinaryData(byte[] binData) {
            this.buffers.add(binData);
            if (this.buffers.size() == this.reconPack.attachments) {
                Packetqxcbd packet = Binaryqdsa.reconstructPacket(this.reconPack,
                        this.buffers.toArray(new byte[this.buffers.size()][]));
                this.finishReconstruction();
                return packet;
            }
            return null;
        }

        public void finishReconstruction () {
            this.reconPack = null;
            this.buffers = new ArrayList<byte[]>();
        }
    }
}


