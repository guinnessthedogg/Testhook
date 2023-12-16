package com.xxx.zzz.aall.ioppp.socketlll.clientbb;

import com.xxx.zzz.aall.ioppp.socketlll.backobb.Backoffq;
import com.xxx.zzz.aall.ioppp.socketlll.emitterbb.Emitterq;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.Socketq;
import com.xxx.zzz.aall.ioppp.socketlll.parsersnn.IOParserqsc;
import com.xxx.zzz.aall.ioppp.socketlll.parsersnn.Packetqxcbd;
import com.xxx.zzz.aall.ioppp.socketlll.parsersnn.Parserqznvjf;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.ioppp.socketlll.threadnnn.EventThreadz;
import com.xxx.zzz.aall.okhttp3ll.Callzadasd;
import com.xxx.zzz.aall.okhttp3ll.WebSocketzqa;


public class ManagerQ extends Emitterq {

    private static final Logger logger = Logger.getLogger(ManagerQ.class.getName());

     enum ReadyState {
        CLOSED, OPENING, OPEN
    }


    public static final String EVENT_OPEN = "open";


    public static final String EVENT_CLOSE = "close";

    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_ERROR = "error";


    public static final String EVENT_CONNECT_ERROR = "connect_error";


    public static final String EVENT_CONNECT_TIMEOUT = "connect_timeout";


    public static final String EVENT_RECONNECT = "reconnect";


    public static final String EVENT_RECONNECT_ERROR = "reconnect_error";

    public static final String EVENT_RECONNECT_FAILED = "reconnect_failed";

    public static final String EVENT_RECONNECT_ATTEMPT = "reconnect_attempt";

    public static final String EVENT_RECONNECTING = "reconnecting";

    public static final String EVENT_PING = "ping";

    public static final String EVENT_PONG = "pong";


    public static final String EVENT_TRANSPORT = Engine.EVENT_TRANSPORT;

     static WebSocketzqa.Factory defaultWebSocketFactory;
     static Callzadasd.Factory defaultCallFactory;

     ReadyState readyState;

    private boolean _reconnection;
    private boolean skipReconnect;
    private boolean reconnecting;
    private boolean encoding;
    private int _reconnectionAttempts;
    private long _reconnectionDelay;
    private long _reconnectionDelayMax;
    private double _randomizationFactor;
    private Backoffq backoff;
    private long _timeout;
    private Set<SocketQ> connecting = new HashSet<SocketQ>();
    private Date lastPing;
    private URI uri;
    private List<Packetqxcbd> packetBuffer;
    private Queue<OnQ.Handle> subs;
    private Options opts;
     Socketq engine;
    private Parserqznvjf.Encoder encoder;
    private Parserqznvjf.Decoder decoder;


     ConcurrentHashMap<String, SocketQ> nsps;


    public ManagerQ() {
        this(null, null);
    }

    public ManagerQ(URI uri) {
        this(uri, null);
    }

    public ManagerQ(Options opts) {
        this(null, opts);
    }

    public ManagerQ(URI uri, Options opts) {
        if (opts == null) {
            opts = new Options();
        }
        if (opts.path == null) {
            opts.path = "/socket.io";
        }
        if (opts.webSocketFactory == null) {
            opts.webSocketFactory = defaultWebSocketFactory;
        }
        if (opts.callFactory == null) {
            opts.callFactory = defaultCallFactory;
        }
        this.opts = opts;
        this.nsps = new ConcurrentHashMap<String, SocketQ>();
        this.subs = new LinkedList<OnQ.Handle>();
        this.reconnection(opts.reconnection);
        this.reconnectionAttempts(opts.reconnectionAttempts != 0 ? opts.reconnectionAttempts : Integer.MAX_VALUE);
        this.reconnectionDelay(opts.reconnectionDelay != 0 ? opts.reconnectionDelay : 1000);
        this.reconnectionDelayMax(opts.reconnectionDelayMax != 0 ? opts.reconnectionDelayMax : 5000);
        this.randomizationFactor(opts.randomizationFactor != 0.0 ? opts.randomizationFactor : 0.5);
        this.backoff = new Backoffq()
                .setMin(this.reconnectionDelay())
                .setMax(this.reconnectionDelayMax())
                .setJitter(this.randomizationFactor());
        this.timeout(opts.timeout);
        this.readyState = ReadyState.CLOSED;
        this.uri = uri;
        this.encoding = false;
        this.packetBuffer = new ArrayList<Packetqxcbd>();
        this.encoder = opts.encoder != null ? opts.encoder : new IOParserqsc.Encoder();
        this.decoder = opts.decoder != null ? opts.decoder : new IOParserqsc.Decoder();
    }

    private void emitAll(String event, Object... args) {
        this.emit(event, args);
        for (SocketQ socket : this.nsps.values()) {
            socket.emit(event, args);
        }
    }


    private void updateSocketIds() {
        for (Map.Entry<String, SocketQ> entry : this.nsps.entrySet()) {
            String nsp = entry.getKey();
            SocketQ socket = entry.getValue();
            socket.id = this.generateId(nsp);
        }
    }

    private String generateId(String nsp) {
        return ("/".equals(nsp) ? "" : (nsp + "#")) + this.engine.id();
    }

    public boolean reconnection() {
        return this._reconnection;
    }

    public ManagerQ reconnection(boolean v) {
        this._reconnection = v;
        return this;
    }

    public int reconnectionAttempts() {
        return this._reconnectionAttempts;
    }

    public ManagerQ reconnectionAttempts(int v) {
        this._reconnectionAttempts = v;
        return this;
    }

    public final long reconnectionDelay() {
        return this._reconnectionDelay;
    }

    public ManagerQ reconnectionDelay(long v) {
        this._reconnectionDelay = v;
        if (this.backoff != null) {
            this.backoff.setMin(v);
        }
        return this;
    }

    public final double randomizationFactor() {
        return this._randomizationFactor;
    }

    public ManagerQ randomizationFactor(double v) {
        this._randomizationFactor = v;
        if (this.backoff != null) {
            this.backoff.setJitter(v);
        }
        return this;
    }

    public final long reconnectionDelayMax() {
        return this._reconnectionDelayMax;
    }

    public ManagerQ reconnectionDelayMax(long v) {
        this._reconnectionDelayMax = v;
        if (this.backoff != null) {
            this.backoff.setMax(v);
        }
        return this;
    }

    public long timeout() {
        return this._timeout;
    }

    public ManagerQ timeout(long v) {
        this._timeout = v;
        return this;
    }

    private void maybeReconnectOnOpen() {

        if (!this.reconnecting && this._reconnection && this.backoff.getAttempts() == 0) {
            this.reconnect();
        }
    }

    public ManagerQ open(){
        return open(null);
    }


    public ManagerQ open(final OpenCallback fn) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("readyState %s", ManagerQ.this.readyState));
                }
                if (ManagerQ.this.readyState == ReadyState.OPEN || ManagerQ.this.readyState == ReadyState.OPENING) return;

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("opening %s", ManagerQ.this.uri));
                }
                ManagerQ.this.engine = new Engine(ManagerQ.this.uri, ManagerQ.this.opts);
                final Socketq socket = ManagerQ.this.engine;
                final ManagerQ self = ManagerQ.this;
                ManagerQ.this.readyState = ReadyState.OPENING;
                ManagerQ.this.skipReconnect = false;


                socket.on(Engine.EVENT_TRANSPORT, new Listener() {
                    @Override
                    public void call(Object... args) {
                        self.emit(ManagerQ.EVENT_TRANSPORT, args);
                    }
                });

                final OnQ.Handle openSub = OnQ.on(socket, Engine.EVENT_OPEN, new Listener() {
                    @Override
                    public void call(Object... objects) {
                        self.onopen();
                        if (fn != null) fn.call(null);
                    }
                });

                OnQ.Handle errorSub = OnQ.on(socket, Engine.EVENT_ERROR, new Listener() {
                    @Override
                    public void call(Object... objects) {
                        Object data = objects.length > 0 ? objects[0] : null;
                        logger.fine("connect_error");
                        self.cleanup();
                        self.readyState = ReadyState.CLOSED;
                        self.emitAll(EVENT_CONNECT_ERROR, data);
                        if (fn != null) {
                            Exception err = new SocketIOExceptionq("Connection error",
                                    data instanceof Exception ? (Exception) data : null);
                            fn.call(err);
                        } else {

                            self.maybeReconnectOnOpen();
                        }
                    }
                });

                if (ManagerQ.this._timeout >= 0) {
                    final long timeout = ManagerQ.this._timeout;
                    logger.fine(String.format("connection attempt will timeout after %d", timeout));

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            EventThreadz.exec(new Runnable() {
                                @Override
                                public void run() {
                                    logger.fine(String.format("connect attempt timed out after %d", timeout));
                                    openSub.destroy();
                                    socket.close();
                                    socket.emit(Engine.EVENT_ERROR, new SocketIOExceptionq("timeout"));
                                    self.emitAll(EVENT_CONNECT_TIMEOUT, timeout);
                                }
                            });
                        }
                    }, timeout);

                    ManagerQ.this.subs.add(new OnQ.Handle() {
                        @Override
                        public void destroy() {
                            timer.cancel();
                        }
                    });
                }

                ManagerQ.this.subs.add(openSub);
                ManagerQ.this.subs.add(errorSub);

                ManagerQ.this.engine.open();
            }
        });
        return this;
    }

    private void onopen() {
        logger.fine("open");

        this.cleanup();

        this.readyState = ReadyState.OPEN;
        this.emit(EVENT_OPEN);

        final Socketq socket = this.engine;
        this.subs.add(OnQ.on(socket, Engine.EVENT_DATA, new Listener() {
            @Override
            public void call(Object... objects) {
                Object data = objects[0];
                if (data instanceof String) {
                    ManagerQ.this.ondata((String)data);
                } else if (data instanceof byte[]) {
                    ManagerQ.this.ondata((byte[])data);
                }
            }
        }));
        this.subs.add(OnQ.on(socket, Engine.EVENT_PING, new Listener() {
            @Override
            public void call(Object... objects) {
                ManagerQ.this.onping();
            }
        }));
        this.subs.add(OnQ.on(socket, Engine.EVENT_PONG, new Listener() {
            @Override
            public void call(Object... objects) {
                ManagerQ.this.onpong();
            }
        }));
        this.subs.add(OnQ.on(socket, Engine.EVENT_ERROR, new Listener() {
            @Override
            public void call(Object... objects) {
                ManagerQ.this.onerror((Exception)objects[0]);
            }
        }));
        this.subs.add(OnQ.on(socket, Engine.EVENT_CLOSE, new Listener() {
            @Override
            public void call(Object... objects) {
                ManagerQ.this.onclose((String)objects[0]);
            }
        }));
        this.decoder.onDecoded(new Parserqznvjf.Decoder.Callback() {
            @Override
            public void call (Packetqxcbd packet) {
                ManagerQ.this.ondecoded(packet);
            }
        });
    }

    private void onping() {
        this.lastPing = new Date();
        this.emitAll(EVENT_PING);
    }

    private void onpong() {
        this.emitAll(EVENT_PONG,
                null != this.lastPing ? new Date().getTime() - this.lastPing.getTime() : 0);
    }

    private void ondata(String data) {
        this.decoder.add(data);
    }

    private void ondata(byte[] data) {
        this.decoder.add(data);
    }

    private void ondecoded(Packetqxcbd packet) {
        this.emit(EVENT_PACKET, packet);
    }

    private void onerror(Exception err) {
        logger.log(Level.FINE, "error", err);
        this.emitAll(EVENT_ERROR, err);
    }


    public SocketQ socket(final String nsp, Options opts) {
        SocketQ socket = this.nsps.get(nsp);
        if (socket == null) {
            socket = new SocketQ(this, nsp, opts);
            SocketQ _socket = this.nsps.putIfAbsent(nsp, socket);
            if (_socket != null) {
                socket = _socket;
            } else {
                final ManagerQ self = this;
                final SocketQ s = socket;
                socket.on(SocketQ.EVENT_CONNECTING, new Listener() {
                    @Override
                    public void call(Object... args) {
                        self.connecting.add(s);
                    }
                });
                socket.on(SocketQ.EVENT_CONNECT, new Listener() {
                    @Override
                    public void call(Object... objects) {
                        s.id = self.generateId(nsp);
                    }
                });
            }
        }
        return socket;
    }

    public SocketQ socket(String nsp) {
        return socket(nsp, null);
    }

     void destroy(SocketQ socket) {
        this.connecting.remove(socket);
        if (!this.connecting.isEmpty()) return;

        this.close();
    }

     void packet(Packetqxcbd packet) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("writing packet %s", packet));
        }
        final ManagerQ self = this;

        if (packet.query != null && !packet.query.isEmpty() && packet.type == Parserqznvjf.CONNECT) {
            packet.nsp += "?" + packet.query;
        }

        if (!self.encoding) {
            self.encoding = true;
            this.encoder.encode(packet, new Parserqznvjf.Encoder.Callback() {
                @Override
                public void call(Object[] encodedPackets) {
                    for (Object packet : encodedPackets) {
                        if (packet instanceof String) {
                            self.engine.write((String)packet);
                        } else if (packet instanceof byte[]) {
                            self.engine.write((byte[])packet);
                        }
                    }
                    self.encoding = false;
                    self.processPacketQueue();
                }
            });
        } else {
            self.packetBuffer.add(packet);
        }
    }

    private void processPacketQueue() {
        if (!this.packetBuffer.isEmpty() && !this.encoding) {
            Packetqxcbd pack = this.packetBuffer.remove(0);
            this.packet(pack);
        }
    }

    private void cleanup() {
        logger.fine("cleanup");

        OnQ.Handle sub;
        while ((sub = this.subs.poll()) != null) sub.destroy();
        this.decoder.onDecoded(null);

        this.packetBuffer.clear();
        this.encoding = false;
        this.lastPing = null;

        this.decoder.destroy();
    }

     void close() {
        logger.fine("disconnect");
        this.skipReconnect = true;
        this.reconnecting = false;
        if (this.readyState != ReadyState.OPEN) {


            this.cleanup();
        }
        this.backoff.reset();
        this.readyState = ReadyState.CLOSED;
        if (this.engine != null) {
            this.engine.close();
        }
    }

    private void onclose(String reason) {
        logger.fine("onclose");
        this.cleanup();
        this.backoff.reset();
        this.readyState = ReadyState.CLOSED;
        this.emit(EVENT_CLOSE, reason);

        if (this._reconnection && !this.skipReconnect) {
            this.reconnect();
        }
    }

    private void reconnect() {
        if (this.reconnecting || this.skipReconnect) return;

        final ManagerQ self = this;

        if (this.backoff.getAttempts() >= this._reconnectionAttempts) {
            logger.fine("reconnect failed");
            this.backoff.reset();
            this.emitAll(EVENT_RECONNECT_FAILED);
            this.reconnecting = false;
        } else {
            long delay = this.backoff.duration();
            logger.fine(String.format("will wait %dms before reconnect attempt", delay));

            this.reconnecting = true;
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    EventThreadz.exec(new Runnable() {
                        @Override
                        public void run() {
                            if (self.skipReconnect) return;

                            logger.fine("attempting reconnect");
                            int attempts = self.backoff.getAttempts();
                            self.emitAll(EVENT_RECONNECT_ATTEMPT, attempts);
                            self.emitAll(EVENT_RECONNECTING, attempts);


                            if (self.skipReconnect) return;

                            self.open(new OpenCallback() {
                                @Override
                                public void call(Exception err) {
                                    if (err != null) {
                                        logger.fine("reconnect attempt error");
                                        self.reconnecting = false;
                                        self.reconnect();
                                        self.emitAll(EVENT_RECONNECT_ERROR, err);
                                    } else {
                                        logger.fine("reconnect success");
                                        self.onreconnect();
                                    }
                                }
                            });
                        }
                    });
                }
            }, delay);

            this.subs.add(new OnQ.Handle() {
                @Override
                public void destroy() {
                    timer.cancel();
                }
            });
        }
    }

    private void onreconnect() {
        int attempts = this.backoff.getAttempts();
        this.reconnecting = false;
        this.backoff.reset();
        this.updateSocketIds();
        this.emitAll(EVENT_RECONNECT, attempts);
    }


    public static interface OpenCallback {

        public void call(Exception err);
    }


    private static class Engine extends Socketq {

        Engine(URI uri, Options opts) {
            super(uri, opts);
        }
    }

    public static class Options extends Socketq.Options {

        public boolean reconnection = true;
        public int reconnectionAttempts;
        public long reconnectionDelay;
        public long reconnectionDelayMax;
        public double randomizationFactor;
        public Parserqznvjf.Encoder encoder;
        public Parserqznvjf.Decoder decoder;


        public long timeout = 20000;
    }
}
