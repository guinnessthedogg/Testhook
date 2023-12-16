package com.xxx.zzz.aall.ioppp.socketlll.clientbb;

import com.xxx.zzz.aall.ioppp.socketlll.emitterbb.Emitterq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.ioppp.socketlll.threadnnn.EventThreadz;
import com.xxx.zzz.aall.ioppp.socketlll.parsersnn.Packetqxcbd;
import com.xxx.zzz.aall.ioppp.socketlll.parsersnn.Parserqznvjf;


public class SocketQ extends Emitterq {

    private static final Logger logger = Logger.getLogger(SocketQ.class.getName());


    public static final String EVENT_CONNECT = "connect";

    public static final String EVENT_CONNECTING = "connecting";


    public static final String EVENT_DISCONNECT = "disconnect";


    public static final String EVENT_ERROR = "error";

    public static final String EVENT_MESSAGE = "message";

    public static final String EVENT_CONNECT_ERROR = ManagerQ.EVENT_CONNECT_ERROR;

    public static final String EVENT_CONNECT_TIMEOUT = ManagerQ.EVENT_CONNECT_TIMEOUT;

    public static final String EVENT_RECONNECT = ManagerQ.EVENT_RECONNECT;

    public static final String EVENT_RECONNECT_ERROR = ManagerQ.EVENT_RECONNECT_ERROR;

    public static final String EVENT_RECONNECT_FAILED = ManagerQ.EVENT_RECONNECT_FAILED;

    public static final String EVENT_RECONNECT_ATTEMPT = ManagerQ.EVENT_RECONNECT_ATTEMPT;

    public static final String EVENT_RECONNECTING = ManagerQ.EVENT_RECONNECTING;

    public static final String EVENT_PING = ManagerQ.EVENT_PING;

    public static final String EVENT_PONG = ManagerQ.EVENT_PONG;

    protected static Map<String, Integer> events = new HashMap<String, Integer>() {{
        put(EVENT_CONNECT, 1);
        put(EVENT_CONNECT_ERROR, 1);
        put(EVENT_CONNECT_TIMEOUT, 1);
        put(EVENT_CONNECTING, 1);
        put(EVENT_DISCONNECT, 1);
        put(EVENT_ERROR, 1);
        put(EVENT_RECONNECT, 1);
        put(EVENT_RECONNECT_ATTEMPT, 1);
        put(EVENT_RECONNECT_FAILED, 1);
        put(EVENT_RECONNECT_ERROR, 1);
        put(EVENT_RECONNECTING, 1);
        put(EVENT_PING, 1);
        put(EVENT_PONG, 1);
    }};

     String id;

    private volatile boolean connected;
    private int ids;
    private String nsp;
    private ManagerQ io;
    private String query;
    private Map<Integer, Ackq> acks = new HashMap<Integer, Ackq>();
    private Queue<OnQ.Handle> subs;
    private final Queue<List<Object>> receiveBuffer = new LinkedList<List<Object>>();
    private final Queue<Packetqxcbd<JSONArray>> sendBuffer = new LinkedList<Packetqxcbd<JSONArray>>();

    public SocketQ(ManagerQ io, String nsp, ManagerQ.Options opts) {
        this.io = io;
        this.nsp = nsp;
        if (opts != null) {
            this.query = opts.query;
        }
    }

    private void subEvents() {
        if (this.subs != null) return;

        final ManagerQ io = SocketQ.this.io;
        SocketQ.this.subs = new LinkedList<OnQ.Handle>() {{
            add(OnQ.on(io, ManagerQ.EVENT_OPEN, new Listener() {
                @Override
                public void call(Object... args) {
                    SocketQ.this.onopen();
                }
            }));
            add(OnQ.on(io, ManagerQ.EVENT_PACKET, new Listener() {
                @Override
                public void call(Object... args) {
                    SocketQ.this.onpacket((Packetqxcbd<?>) args[0]);
                }
            }));
            add(OnQ.on(io, ManagerQ.EVENT_CLOSE, new Listener() {
                @Override
                public void call(Object... args) {
                    SocketQ.this.onclose(args.length > 0 ? (String) args[0] : null);
                }
            }));
        }};
    }


    public SocketQ open() {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                if (SocketQ.this.connected) return;

                SocketQ.this.subEvents();
                SocketQ.this.io.open();
                if (ManagerQ.ReadyState.OPEN == SocketQ.this.io.readyState) SocketQ.this.onopen();
                SocketQ.this.emit(EVENT_CONNECTING);
            }
        });
        return this;
    }


    public SocketQ connect() {
        return this.open();
    }


    public SocketQ send(final Object... args) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                SocketQ.this.emit(EVENT_MESSAGE, args);
            }
        });
        return this;
    }


    @Override
    public Emitterq emit(final String event, final Object... args) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                if (events.containsKey(event)) {
                    SocketQ.super.emit(event, args);
                    return;
                }

                Ackq ack;
                Object[] _args;
                int lastIndex = args.length - 1;

                if (args.length > 0 && args[lastIndex] instanceof Ackq) {
                    _args = new Object[lastIndex];
                    for (int i = 0; i < lastIndex; i++) {
                        _args[i] = args[i];
                    }
                    ack = (Ackq) args[lastIndex];
                } else {
                    _args = args;
                    ack = null;
                }

                emit(event, _args, ack);
            }
        });
        return this;
    }


    public Emitterq emit(final String event, final Object[] args, final Ackq ack) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArgs = new JSONArray();
                jsonArgs.put(event);

                if (args != null) {
                    for (Object arg : args) {
                        jsonArgs.put(arg);
                    }
                }

                Packetqxcbd<JSONArray> packet = new Packetqxcbd<JSONArray>(Parserqznvjf.EVENT, jsonArgs);

                if (ack != null) {
                    logger.fine(String.format("emitting packet with ack id %d", ids));
                    SocketQ.this.acks.put(ids, ack);
                    packet.id = ids++;
                }

                if (SocketQ.this.connected) {
                    SocketQ.this.packet(packet);
                } else {
                    SocketQ.this.sendBuffer.add(packet);
                }
            }
        });
        return this;
    }

    private void packet(Packetqxcbd packet) {
        packet.nsp = this.nsp;
        this.io.packet(packet);
    }

    private void onopen() {
        logger.fine("transport is open - connecting");

        if (!"/".equals(this.nsp)) {
            if (this.query != null && !this.query.isEmpty()) {
                Packetqxcbd packet = new Packetqxcbd(Parserqznvjf.CONNECT);
                packet.query = this.query;
                this.packet(packet);
            } else {
                this.packet(new Packetqxcbd(Parserqznvjf.CONNECT));
            }
        }
    }

    private void onclose(String reason) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("close (%s)", reason));
        }
        this.connected = false;
        this.id = null;
        this.emit(EVENT_DISCONNECT, reason);
    }

    private void onpacket(Packetqxcbd<?> packet) {
        if (!this.nsp.equals(packet.nsp)) return;

        switch (packet.type) {
            case Parserqznvjf.CONNECT:
                this.onconnect();
                break;

            case Parserqznvjf.EVENT: {
                @SuppressWarnings("unchecked")
                Packetqxcbd<JSONArray> p = (Packetqxcbd<JSONArray>) packet;
                this.onevent(p);
                break;
            }

            case Parserqznvjf.BINARY_EVENT: {
                @SuppressWarnings("unchecked")
                Packetqxcbd<JSONArray> p = (Packetqxcbd<JSONArray>) packet;
                this.onevent(p);
                break;
            }

            case Parserqznvjf.ACK: {
                @SuppressWarnings("unchecked")
                Packetqxcbd<JSONArray> p = (Packetqxcbd<JSONArray>) packet;
                this.onack(p);
                break;
            }

            case Parserqznvjf.BINARY_ACK: {
                @SuppressWarnings("unchecked")
                Packetqxcbd<JSONArray> p = (Packetqxcbd<JSONArray>) packet;
                this.onack(p);
                break;
            }

            case Parserqznvjf.DISCONNECT:
                this.ondisconnect();
                break;

            case Parserqznvjf.ERROR:
                this.emit(EVENT_ERROR, packet.data);
                break;
        }
    }

    private void onevent(Packetqxcbd<JSONArray> packet) {
        List<Object> args = new ArrayList<Object>(Arrays.asList(toArray(packet.data)));
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("emitting event %s", args));
        }

        if (packet.id >= 0) {
            logger.fine("attaching ack callback to event");
            args.add(this.ack(packet.id));
        }

        if (this.connected) {
            if (args.isEmpty()) return;
            String event = args.remove(0).toString();
            super.emit(event, args.toArray());
        } else {
            this.receiveBuffer.add(args);
        }
    }

    private Ackq ack(final int id) {
        final SocketQ self = this;
        final boolean[] sent = new boolean[] {false};
        return new Ackq() {
            @Override
            public void call(final Object... args) {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        if (sent[0]) return;
                        sent[0] = true;
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine(String.format("sending ack %s", args.length != 0 ? args : null));
                        }

                        JSONArray jsonArgs = new JSONArray();
                        for (Object arg : args) {
                            jsonArgs.put(arg);
                        }

                        Packetqxcbd<JSONArray> packet = new Packetqxcbd<JSONArray>(Parserqznvjf.ACK, jsonArgs);
                        packet.id = id;
                        self.packet(packet);
                    }
                });
            }
        };
    }

    private void onack(Packetqxcbd<JSONArray> packet) {
        Ackq fn = this.acks.remove(packet.id);
        if (fn != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("calling ack %s with %s", packet.id, packet.data));
            }
            fn.call(toArray(packet.data));
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("bad ack %s", packet.id));
            }
        }
    }

    private void onconnect() {
        this.connected = true;
        this.emit(EVENT_CONNECT);
        this.emitBuffered();
    }

    private void emitBuffered() {
        List<Object> data;
        while ((data = this.receiveBuffer.poll()) != null) {
            String event = (String)data.get(0);
            super.emit(event, data.toArray());
        }
        this.receiveBuffer.clear();

        Packetqxcbd<JSONArray> packet;
        while ((packet = this.sendBuffer.poll()) != null) {
            this.packet(packet);
        }
        this.sendBuffer.clear();
    }

    private void ondisconnect() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("server disconnect (%s)", this.nsp));
        }
        this.destroy();
        this.onclose("io server disconnect");
    }

    private void destroy() {
        if (this.subs != null) {

            for (OnQ.Handle sub : this.subs) {
                sub.destroy();
            }
            this.subs = null;
        }

        this.io.destroy(this);
    }


    public SocketQ close() {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                if (SocketQ.this.connected) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(String.format("performing disconnect (%s)", SocketQ.this.nsp));
                    }
                    SocketQ.this.packet(new Packetqxcbd(Parserqznvjf.DISCONNECT));
                }

                SocketQ.this.destroy();

                if (SocketQ.this.connected) {
                    SocketQ.this.onclose("io client disconnect");
                }
            }
        });
        return this;
    }


    public SocketQ disconnect() {
        return this.close();
    }

    public ManagerQ io() {
        return this.io;
    }

    public boolean connected() {
        return this.connected;
    }


    public String id() {
        return this.id;
    }

    private static Object[] toArray(JSONArray array) {
        int length = array.length();
        Object[] data = new Object[length];
        for (int i = 0; i < length; i++) {
            Object v;
            try {
                v = array.get(i);
            } catch (JSONException e) {
                logger.log(Level.WARNING, "An error occured while retrieving data from JSONArray", e);
                v = null;
            }
            data[i] = JSONObject.NULL.equals(v) ? null : v;
        }
        return data;
    }
}

