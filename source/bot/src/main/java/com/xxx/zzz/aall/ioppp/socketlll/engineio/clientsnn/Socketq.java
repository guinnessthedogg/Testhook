package com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn;

import com.xxx.zzz.aall.ioppp.socketlll.emitterbb.Emitterq;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.transportsnn.Pollingq;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.transportsnn.WebdasdSdsaocketqoiui;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn.Packeastq;
import com.xxx.zzz.aall.ioppp.socketlll.parseqsnn.ParseQS;

import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.transportsnn.PollingXHR;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn.Parserqsada;
import com.xxx.zzz.aall.ioppp.socketlll.threadnnn.EventThreadz;
import com.xxx.zzz.aall.okhttp3ll.Callzadasd;
import com.xxx.zzz.aall.okhttp3ll.WebSocketzqa;



public class Socketq extends Emitterq {

    private static final Logger logger = Logger.getLogger(Socketq.class.getName());

    private static final String PROBE_ERROR = "probe error";


    private enum ReadyState {
        OPENING, OPEN, CLOSING, CLOSED;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public static final String EVENT_OPEN = "open";


    public static final String EVENT_CLOSE = "close";


    public static final String EVENT_MESSAGE = "message";


    public static final String EVENT_ERROR = "error";

    public static final String EVENT_UPGRADE_ERROR = "upgradeError";


    public static final String EVENT_FLUSH = "flush";


    public static final String EVENT_DRAIN = "drain";

    public static final String EVENT_HANDSHAKE = "handshake";
    public static final String EVENT_UPGRADING = "upgrading";
    public static final String EVENT_UPGRADE = "upgrade";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_PACKET_CREATE = "packetCreate";
    public static final String EVENT_HEARTBEAT = "heartbeat";
    public static final String EVENT_DATA = "data";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_PONG = "pong";


    public static final String EVENT_TRANSPORT = "transport";


    public static final int PROTOCOL = Parserqsada.PROTOCOL;

    private static boolean priorWebsocketSuccess = false;

    private static WebSocketzqa.Factory defaultWebSocketFactory;
    private static Callzadasd.Factory defaultCallFactory;
    private static OkHttpClientza defaultOkHttpClient;

    private boolean secure;
    private boolean upgrade;
    private boolean timestampRequests;
    private boolean upgrading;
    private boolean rememberUpgrade;
     int port;
    private int policyPort;
    private int prevBufferLen;
    private long pingInterval;
    private long pingTimeout;
    private String id;
     String hostname;
    private String path;
    private String timestampParam;
    private List<String> transports;
    private Map<String, Transportqdasa.Options> transportOptions;
    private List<String> upgrades;
    private Map<String, String> query;
     LinkedList<Packeastq> writeBuffer = new LinkedList<Packeastq>();
     Transportqdasa transport;
    private Future pingTimeoutTimer;
    private Future pingIntervalTimer;
    private WebSocketzqa.Factory webSocketFactory;
    private Callzadasd.Factory callFactory;

    private ReadyState readyState;
    private ScheduledExecutorService heartbeatScheduler;
    private final Listener onHeartbeatAsListener = new Listener() {
        @Override
        public void call(Object... args) {
            Socketq.this.onHeartbeat(args.length > 0 ? (Long)args[0]: 0);
        }
    };

    public Socketq() {
        this(new Options());
    }


    public Socketq(String uri) throws URISyntaxException {
        this(uri, null);
    }

    public Socketq(URI uri) {
        this(uri, null);
    }


    public Socketq(String uri, Options opts) throws URISyntaxException {
        this(uri == null ? null : new URI(uri), opts);
    }

    public Socketq(URI uri, Options opts) {
        this(uri == null ? opts : Options.fromURI(uri, opts));
    }

    public Socketq(Options opts) {
        if (opts.host != null) {
            String hostname = opts.host;
            boolean ipv6 = hostname.split(":").length > 2;
            if (ipv6) {
                int start = hostname.indexOf('[');
                if (start != -1) hostname = hostname.substring(start + 1);
                int end = hostname.lastIndexOf(']');
                if (end != -1) hostname = hostname.substring(0, end);
            }
            opts.hostname = hostname;
        }

        this.secure = opts.secure;

        if (opts.port == -1) {

            opts.port = this.secure ? 443 : 80;
        }

        this.hostname = opts.hostname != null ? opts.hostname : "localhost";
        this.port = opts.port;
        this.query = opts.query != null ?
                ParseQS.decode(opts.query) : new HashMap<String, String>();
        this.upgrade = opts.upgrade;
        this.path = (opts.path != null ? opts.path : "/engine.io").replaceAll("/$", "") + "/";
        this.timestampParam = opts.timestampParam != null ? opts.timestampParam : "t";
        this.timestampRequests = opts.timestampRequests;
        this.transports = new ArrayList<String>(Arrays.asList(opts.transports != null ?
                opts.transports : new String[]{Pollingq.NAME, WebdasdSdsaocketqoiui.NAME}));
        this.transportOptions = opts.transportOptions != null ?
                opts.transportOptions : new HashMap<String, Transportqdasa.Options>();
        this.policyPort = opts.policyPort != 0 ? opts.policyPort : 843;
        this.rememberUpgrade = opts.rememberUpgrade;
        this.callFactory = opts.callFactory != null ? opts.callFactory : defaultCallFactory;
        this.webSocketFactory = opts.webSocketFactory != null ? opts.webSocketFactory : defaultWebSocketFactory;
        if (callFactory == null) {
            if (defaultOkHttpClient == null) {
                defaultOkHttpClient = new OkHttpClientza();
            }
            callFactory = defaultOkHttpClient;
        }
        if (webSocketFactory == null) {
            if (defaultOkHttpClient == null) {
                defaultOkHttpClient = new OkHttpClientza();
            }
            webSocketFactory = defaultOkHttpClient;
        }
    }

    public static void setDefaultOkHttpWebSocketFactory(WebSocketzqa.Factory factory) {
        defaultWebSocketFactory = factory;
    }

    public static void setDefaultOkHttpCallFactory(Callzadasd.Factory factory) {
        defaultCallFactory = factory;
    }


    public Socketq open() {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                String transportName;
                if (Socketq.this.rememberUpgrade && Socketq.priorWebsocketSuccess && Socketq.this.transports.contains(WebdasdSdsaocketqoiui.NAME)) {
                    transportName = WebdasdSdsaocketqoiui.NAME;
                } else if (0 == Socketq.this.transports.size()) {

                    final Socketq self = Socketq.this;
                    EventThreadz.nextTick(new Runnable() {
                        @Override
                        public void run() {
                            self.emit(Socketq.EVENT_ERROR, new EngineIOExceptionq("No transports available"));
                        }
                    });
                    return;
                } else {
                    transportName = Socketq.this.transports.get(0);
                }
                Socketq.this.readyState = ReadyState.OPENING;
                Transportqdasa transport = Socketq.this.createTransport(transportName);
                Socketq.this.setTransport(transport);
                transport.open();
            }
        });
        return this;
    }

    private Transportqdasa createTransport(String name) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("creating transport '%s'", name));
        }
        Map<String, String> query = new HashMap<String, String>(this.query);

        query.put("EIO", String.valueOf(Parserqsada.PROTOCOL));
        query.put("transport", name);
        if (this.id != null) {
            query.put("sid", this.id);
        }


        Transportqdasa.Options options = this.transportOptions.get(name);

        Transportqdasa.Options opts = new Transportqdasa.Options();
        opts.query = query;
        opts.socket = this;

        opts.hostname = options != null ? options.hostname : this.hostname;
        opts.port = options != null ? options.port : this.port;
        opts.secure = options != null ? options.secure : this.secure;
        opts.path = options != null ? options.path : this.path;
        opts.timestampRequests = options != null ? options.timestampRequests : this.timestampRequests;
        opts.timestampParam = options != null ? options.timestampParam : this.timestampParam;
        opts.policyPort = options != null ? options.policyPort : this.policyPort;
        opts.callFactory = options != null ? options.callFactory : this.callFactory;
        opts.webSocketFactory = options != null ? options.webSocketFactory : this.webSocketFactory;

        Transportqdasa transport;
        if (WebdasdSdsaocketqoiui.NAME.equals(name)) {
            transport = new WebdasdSdsaocketqoiui(opts);
        } else if (Pollingq.NAME.equals(name)) {
            transport = new PollingXHR(opts);
        } else {
            throw new RuntimeException();
        }

        this.emit(EVENT_TRANSPORT, transport);

        return transport;
    }

    private void setTransport(Transportqdasa transport) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("setting transport %s", transport.name));
        }
        final Socketq self = this;

        if (this.transport != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("clearing existing transport %s", this.transport.name));
            }
            this.transport.off();
        }

        this.transport = transport;

        transport.on(Transportqdasa.EVENT_DRAIN, new Listener() {
            @Override
            public void call(Object... args) {
                self.onDrain();
            }
        }).on(Transportqdasa.EVENT_PACKET, new Listener() {
            @Override
            public void call(Object... args) {
                self.onPacket(args.length > 0 ? (Packeastq) args[0] : null);
            }
        }).on(Transportqdasa.EVENT_ERROR, new Listener() {
            @Override
            public void call(Object... args) {
                self.onError(args.length > 0 ? (Exception) args[0] : null);
            }
        }).on(Transportqdasa.EVENT_CLOSE, new Listener() {
            @Override
            public void call(Object... args) {
                self.onClose("transport close");
            }
        });
    }

    private void probe(final String name) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("probing transport '%s'", name));
        }
        final Transportqdasa[] transport = new Transportqdasa[] {this.createTransport(name)};
        final boolean[] failed = new boolean[] {false};
        final Socketq self = this;

        Socketq.priorWebsocketSuccess = false;

        final Runnable[] cleanup = new Runnable[1];

        final Listener onTransportOpen = new Listener() {
            @Override
            public void call(Object... args) {
                if (failed[0]) return;

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("probe transport '%s' opened", name));
                }
                Packeastq<String> packet = new Packeastq<String>(Packeastq.PING, "probe");
                transport[0].send(new Packeastq[] {packet});
                transport[0].once(Transportqdasa.EVENT_PACKET, new Listener() {
                    @Override
                    public void call(Object... args) {
                        if (failed[0]) return;

                        Packeastq msg = (Packeastq)args[0];
                        if (Packeastq.PONG.equals(msg.type) && "probe".equals(msg.data)) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(String.format("probe transport '%s' pong", name));
                            }
                            self.upgrading = true;
                            self.emit(EVENT_UPGRADING, transport[0]);
                            if (null == transport[0]) return;
                            Socketq.priorWebsocketSuccess = WebdasdSdsaocketqoiui.NAME.equals(transport[0].name);

                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(String.format("pausing current transport '%s'", self.transport.name));
                            }
                            ((Pollingq)self.transport).pause(new Runnable() {
                                @Override
                                public void run() {
                                    if (failed[0]) return;
                                    if (ReadyState.CLOSED == self.readyState) return;

                                    logger.fine("changing transport and sending upgrade packet");

                                    cleanup[0].run();

                                    self.setTransport(transport[0]);
                                    Packeastq packet = new Packeastq(Packeastq.UPGRADE);
                                    transport[0].send(new Packeastq[]{packet});
                                    self.emit(EVENT_UPGRADE, transport[0]);
                                    transport[0] = null;
                                    self.upgrading = false;
                                    self.flush();
                                }
                            });
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(String.format("probe transport '%s' failed", name));
                            }
                            EngineIOExceptionq err = new EngineIOExceptionq(PROBE_ERROR);
                            err.transport = transport[0].name;
                            self.emit(EVENT_UPGRADE_ERROR, err);
                        }
                    }
                });
            }
        };

        final Listener freezeTransport = new Listener() {
            @Override
            public void call(Object... args) {
                if (failed[0]) return;

                failed[0] = true;

                cleanup[0].run();

                transport[0].close();
                transport[0] = null;
            }
        };


        final Listener onerror = new Listener() {
            @Override
            public void call(Object... args) {
                Object err = args[0];
                EngineIOExceptionq error;
                if (err instanceof Exception) {
                    error = new EngineIOExceptionq(PROBE_ERROR, (Exception)err);
                } else if (err instanceof String) {
                    error = new EngineIOExceptionq("probe error: " + (String)err);
                } else {
                    error = new EngineIOExceptionq(PROBE_ERROR);
                }
                error.transport = transport[0].name;

                freezeTransport.call();

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("probe transport \"%s\" failed because of error: %s", name, err));
                }

                self.emit(EVENT_UPGRADE_ERROR, error);
            }
        };

        final Listener onTransportClose = new Listener() {
            @Override
            public void call(Object... args) {
                onerror.call("transport closed");
            }
        };


        final Listener onclose = new Listener() {
            @Override
            public void call(Object... args) {
                onerror.call("socket closed");
            }
        };


        final Listener onupgrade = new Listener() {
            @Override
            public void call(Object... args) {
                Transportqdasa to = (Transportqdasa)args[0];
                if (transport[0] != null && !to.name.equals(transport[0].name)) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(String.format("'%s' works - aborting '%s'", to.name, transport[0].name));
                    }
                    freezeTransport.call();
                }
            }
        };

        cleanup[0] = new Runnable() {
            @Override
            public void run() {
                transport[0].off(Transportqdasa.EVENT_OPEN, onTransportOpen);
                transport[0].off(Transportqdasa.EVENT_ERROR, onerror);
                transport[0].off(Transportqdasa.EVENT_CLOSE, onTransportClose);
                self.off(EVENT_CLOSE, onclose);
                self.off(EVENT_UPGRADING, onupgrade);
            }
        };

        transport[0].once(Transportqdasa.EVENT_OPEN, onTransportOpen);
        transport[0].once(Transportqdasa.EVENT_ERROR, onerror);
        transport[0].once(Transportqdasa.EVENT_CLOSE, onTransportClose);

        this.once(EVENT_CLOSE, onclose);
        this.once(EVENT_UPGRADING, onupgrade);

        transport[0].open();
    }

    private void onOpen() {
        logger.fine("socket open");
        this.readyState = ReadyState.OPEN;
        Socketq.priorWebsocketSuccess = WebdasdSdsaocketqoiui.NAME.equals(this.transport.name);
        this.emit(EVENT_OPEN);
        this.flush();

        if (this.readyState == ReadyState.OPEN && this.upgrade && this.transport instanceof Pollingq) {
            logger.fine("starting upgrade probes");
            for (String upgrade: this.upgrades) {
                this.probe(upgrade);
            }
        }
    }

    private void onPacket(Packeastq packet) {
        if (this.readyState == ReadyState.OPENING ||
                this.readyState == ReadyState.OPEN ||
                this.readyState == ReadyState.CLOSING) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("socket received: type '%s', data '%s'", packet.type, packet.data));
            }

            this.emit(EVENT_PACKET, packet);
            this.emit(EVENT_HEARTBEAT);

            if (Packeastq.OPEN.equals(packet.type)) {
                try {
                    this.onHandshake(new HandshakeDataq((String)packet.data));
                } catch (JSONException e) {
                    this.emit(EVENT_ERROR, new EngineIOExceptionq(e));
                }
            } else if (Packeastq.PONG.equals(packet.type)) {
                this.setPing();
                this.emit(EVENT_PONG);
            } else if (Packeastq.ERROR.equals(packet.type)) {
                EngineIOExceptionq err = new EngineIOExceptionq("server error");
                err.code = packet.data;
                this.onError(err);
            } else if (Packeastq.MESSAGE.equals(packet.type)) {
                this.emit(EVENT_DATA, packet.data);
                this.emit(EVENT_MESSAGE, packet.data);
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("packet received with socket readyState '%s'", this.readyState));
            }
        }
    }

    private void onHandshake(HandshakeDataq data) {
        this.emit(EVENT_HANDSHAKE, data);
        this.id = data.sid;
        this.transport.query.put("sid", data.sid);
        this.upgrades = this.filterUpgrades(Arrays.asList(data.upgrades));
        this.pingInterval = data.pingInterval;
        this.pingTimeout = data.pingTimeout;
        this.onOpen();

        if (ReadyState.CLOSED == this.readyState) return;
        this.setPing();

        this.off(EVENT_HEARTBEAT, this.onHeartbeatAsListener);
        this.on(EVENT_HEARTBEAT, this.onHeartbeatAsListener);
    }

    private void onHeartbeat(long timeout) {
        if (this.pingTimeoutTimer != null) {
            pingTimeoutTimer.cancel(false);
        }

        if (timeout <= 0) {
            timeout = this.pingInterval + this.pingTimeout;
        }

        final Socketq self = this;
        this.pingTimeoutTimer = this.getHeartbeatScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        if (self.readyState == ReadyState.CLOSED) return;
                        self.onClose("ping timeout");
                    }
                });
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    private void setPing() {
        if (this.pingIntervalTimer != null) {
            pingIntervalTimer.cancel(false);
        }

        final Socketq self = this;
        this.pingIntervalTimer = this.getHeartbeatScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine(String.format("writing ping packet - expecting pong within %sms", self.pingTimeout));
                        }
                        self.ping();
                        self.onHeartbeat(self.pingTimeout);
                    }
                });
            }
        }, this.pingInterval, TimeUnit.MILLISECONDS);
    }


    private void ping() {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                Socketq.this.sendPacket(Packeastq.PING, new Runnable() {
                    @Override
                    public void run() {
                        Socketq.this.emit(EVENT_PING);
                    }
                });
            }
        });
    }

    private void onDrain() {
        for (int i = 0; i < this.prevBufferLen; i++) {
            this.writeBuffer.poll();
        }

        this.prevBufferLen = 0;
        if (0 == this.writeBuffer.size()) {
            this.emit(EVENT_DRAIN);
        } else {
            this.flush();
        }
    }

    private void flush() {
        if (this.readyState != ReadyState.CLOSED && this.transport.writable &&
                !this.upgrading && this.writeBuffer.size() != 0) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("flushing %d packets in socket", this.writeBuffer.size()));
            }
            this.prevBufferLen = this.writeBuffer.size();
            this.transport.send(this.writeBuffer.toArray(new Packeastq[this.writeBuffer.size()]));
            this.emit(EVENT_FLUSH);
        }
    }

    public void write(String msg) {
        this.write(msg, null);
    }

    public void write(String msg, Runnable fn) {
        this.send(msg, fn);
    }

    public void write(byte[] msg) {
        this.write(msg, null);
    }

    public void write(byte[] msg, Runnable fn) {
        this.send(msg, fn);
    }


    public void send(String msg) {
        this.send(msg, null);
    }

    public void send(byte[] msg) {
        this.send(msg, null);
    }


    public void send(final String msg, final Runnable fn) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                Socketq.this.sendPacket(Packeastq.MESSAGE, msg, fn);
            }
        });
    }

    public void send(final byte[] msg, final Runnable fn) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                Socketq.this.sendPacket(Packeastq.MESSAGE, msg, fn);
            }
        });
    }

    private void sendPacket(String type, Runnable fn) {
        this.sendPacket(new Packeastq(type), fn);
    }

    private void sendPacket(String type, String data, Runnable fn) {
        Packeastq<String> packet = new Packeastq<String>(type, data);
        sendPacket(packet, fn);
    }

    private void sendPacket(String type, byte[] data, Runnable fn) {
        Packeastq<byte[]> packet = new Packeastq<byte[]>(type, data);
        sendPacket(packet, fn);
    }

    private void sendPacket(Packeastq packet, final Runnable fn) {
        if (ReadyState.CLOSING == this.readyState || ReadyState.CLOSED == this.readyState) {
            return;
        }

        this.emit(EVENT_PACKET_CREATE, packet);
        this.writeBuffer.offer(packet);
        if (null != fn) {
            this.once(EVENT_FLUSH, new Listener() {
                @Override
                public void call(Object... args) {
                    fn.run();
                }
            });
        }
        this.flush();
    }


    public Socketq close() {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                if (Socketq.this.readyState == ReadyState.OPENING || Socketq.this.readyState == ReadyState.OPEN) {
                    Socketq.this.readyState = ReadyState.CLOSING;

                    final Socketq self = Socketq.this;

                    final Runnable close = new Runnable() {
                        @Override
                        public void run() {
                            self.onClose("forced close");
                            logger.fine("socket closing - telling transport to close");
                            self.transport.close();
                        }
                    };

                    final Listener[] cleanupAndClose = new Listener[1];
                    cleanupAndClose[0] = new Listener() {
                        @Override
                        public void call(Object ...args) {
                            self.off(EVENT_UPGRADE, cleanupAndClose[0]);
                            self.off(EVENT_UPGRADE_ERROR, cleanupAndClose[0]);
                            close.run();
                        }
                    };

                    final Runnable waitForUpgrade = new Runnable() {
                        @Override
                        public void run() {

                            self.once(EVENT_UPGRADE, cleanupAndClose[0]);
                            self.once(EVENT_UPGRADE_ERROR, cleanupAndClose[0]);
                        }
                    };

                    if (Socketq.this.writeBuffer.size() > 0) {
                        Socketq.this.once(EVENT_DRAIN, new Listener() {
                            @Override
                            public void call(Object... args) {
                                if (Socketq.this.upgrading) {
                                    waitForUpgrade.run();
                                } else {
                                    close.run();
                                }
                            }
                        });
                    } else if (Socketq.this.upgrading) {
                        waitForUpgrade.run();
                    } else {
                        close.run();
                    }
                }
            }
        });
        return this;
    }

    private void onError(Exception err) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("socket error %s", err));
        }
        Socketq.priorWebsocketSuccess = false;
        this.emit(EVENT_ERROR, err);
        this.onClose("transport error", err);
    }

    private void onClose(String reason) {
        this.onClose(reason, null);
    }

    private void onClose(String reason, Exception desc) {
        if (ReadyState.OPENING == this.readyState || ReadyState.OPEN == this.readyState || ReadyState.CLOSING == this.readyState) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("socket close with reason: %s", reason));
            }
            final Socketq self = this;


            if (this.pingIntervalTimer != null) {
                this.pingIntervalTimer.cancel(false);
            }
            if (this.pingTimeoutTimer != null) {
                this.pingTimeoutTimer.cancel(false);
            }
            if (this.heartbeatScheduler != null) {
                this.heartbeatScheduler.shutdown();
            }


            this.transport.off(EVENT_CLOSE);


            this.transport.close();


            this.transport.off();


            this.readyState = ReadyState.CLOSED;


            this.id = null;


            this.emit(EVENT_CLOSE, reason, desc);



            self.writeBuffer.clear();
            self.prevBufferLen = 0;
        }
    }

     List<String > filterUpgrades(List<String> upgrades) {
        List<String> filteredUpgrades = new ArrayList<String>();
        for (String upgrade : upgrades) {
            if (this.transports.contains(upgrade)) {
                filteredUpgrades.add(upgrade);
            }
        }


        return filteredUpgrades;
    }

    public String id() {
        return this.id;
    }

    private ScheduledExecutorService getHeartbeatScheduler() {
        if (this.heartbeatScheduler == null || this.heartbeatScheduler.isShutdown()) {
            this.heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return this.heartbeatScheduler;
    }

    public static class Options extends Transportqdasa.Options {


        public String[] transports;


        public boolean upgrade = true;

        public boolean rememberUpgrade;
        public String host;
        public String query;
        public Map<String, Transportqdasa.Options> transportOptions;

        private static Options fromURI(URI uri, Options opts) {
            if (opts == null) {
                opts = new Options();
            }

            opts.host = uri.getHost();
            opts.secure = "https".equals(uri.getScheme()) || "wss".equals(uri.getScheme());
            opts.port = uri.getPort();

            String query = uri.getRawQuery();
            if (query != null) {
                opts.query = query;
            }

            return opts;
        }
    }
}
