package com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.transportsnn;


import com.xxx.zzz.aall.ioppp.socketlll.emitterbb.Emitterq;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.Transportqdasa;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn.Packeastq;
import com.xxx.zzz.aall.ioppp.socketlll.parseqsnn.ParseQS;
import com.xxx.zzz.aall.ioppp.socketlll.yeastnn.Yeastz;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn.Parserqsada;
import com.xxx.zzz.aall.ioppp.socketlll.threadnnn.EventThreadz;
import com.xxx.zzz.aall.ioppp.socketlll.utf8nnn.UTF8Exceptionz;

abstract public class Pollingq extends Transportqdasa {

    private static final Logger logger = Logger.getLogger(Pollingq.class.getName());

    public static final String NAME = "polling";

    public static final String EVENT_POLL = "poll";
    public static final String EVENT_POLL_COMPLETE = "pollComplete";

    private boolean polling;


    public Pollingq(Options opts) {
        super(opts);
        this.name = NAME;
    }

    protected void doOpen() {
        this.poll();
    }

    public void pause(final Runnable onPause) {
        EventThreadz.exec(new Runnable() {
            @Override
            public void run() {
                final Pollingq self = Pollingq.this;

                Pollingq.this.readyState = ReadyState.PAUSED;

                final Runnable pause = new Runnable() {
                    @Override
                    public void run() {
                        logger.fine("paused");
                        self.readyState = ReadyState.PAUSED;
                        onPause.run();
                    }
                };

                if (Pollingq.this.polling || !Pollingq.this.writable) {
                    final int[] total = new int[]{0};

                    if (Pollingq.this.polling) {
                        logger.fine("we are currently polling - waiting to pause");
                        total[0]++;
                        Pollingq.this.once(EVENT_POLL_COMPLETE, new Emitterq.Listener() {
                            @Override
                            public void call(Object... args) {
                                logger.fine("pre-pause polling complete");
                                if (--total[0] == 0) {
                                    pause.run();
                                }
                            }
                        });
                    }

                    if (!Pollingq.this.writable) {
                        logger.fine("we are currently writing - waiting to pause");
                        total[0]++;
                        Pollingq.this.once(EVENT_DRAIN, new Emitterq.Listener() {
                            @Override
                            public void call(Object... args) {
                                logger.fine("pre-pause writing complete");
                                if (--total[0] == 0) {
                                    pause.run();
                                }
                            }
                        });
                    }
                } else {
                    pause.run();
                }
            }
        });
    }

    private void poll() {
        logger.fine("polling");
        this.polling = true;
        this.doPoll();
        this.emit(EVENT_POLL);
    }

    @Override
    protected void onData(String data) {
        _onData(data);
    }

    @Override
    protected void onData(byte[] data) {
        _onData(data);
    }

    private void _onData(Object data) {
        final Pollingq self = this;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("polling got data %s", data));
        }
        Parserqsada.DecodePayloadCallback callback = new Parserqsada.DecodePayloadCallback() {
            @Override
            public boolean call(Packeastq packet, int index, int total) {
                if (self.readyState == ReadyState.OPENING) {
                    self.onOpen();
                }

                if (Packeastq.CLOSE.equals(packet.type)) {
                    self.onClose();
                    return false;
                }

                self.onPacket(packet);
                return true;
            }
        };

        if (data instanceof String) {
            @SuppressWarnings("unchecked")
            Parserqsada.DecodePayloadCallback<String> tempCallback = callback;
            Parserqsada.decodePayload((String)data, tempCallback);
        } else if (data instanceof byte[]) {
            Parserqsada.decodePayload((byte[])data, callback);
        }

        if (this.readyState != ReadyState.CLOSED) {
            this.polling = false;
            this.emit(EVENT_POLL_COMPLETE);

            if (this.readyState == ReadyState.OPEN) {
                this.poll();
            } else {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("ignoring poll - transport state '%s'", this.readyState));
                }
            }
        }
    }

    protected void doClose() {
        final Pollingq self = this;

        Emitterq.Listener close = new Emitterq.Listener() {
            @Override
            public void call(Object... args) {
                logger.fine("writing close packet");
                try {
                    self.write(new Packeastq[]{new Packeastq(Packeastq.CLOSE)});
                } catch (UTF8Exceptionz err) {
                    throw new RuntimeException(err);
                }
            }
        };

        if (this.readyState == ReadyState.OPEN) {
            logger.fine("transport open - closing");
            close.call();
        } else {


            logger.fine("transport not open - deferring close");
            this.once(EVENT_OPEN, close);
        }
    }

    protected void write(Packeastq[] packets) throws UTF8Exceptionz {
        final Pollingq self = this;
        this.writable = false;
        final Runnable callbackfn = new Runnable() {
            @Override
            public void run() {
                self.writable = true;
                self.emit(EVENT_DRAIN);
            }
        };

        Parserqsada.encodePayload(packets, new Parserqsada.EncodeCallback() {
            @Override
            public void call(Object data) {
                if (data instanceof byte[]) {
                    self.doWrite((byte[])data, callbackfn);
                } else if (data instanceof String) {
                    self.doWrite((String)data, callbackfn);
                } else {
                    logger.warning("Unexpected data: " + data);
                }
            }
        });
    }

    protected String uri() {
        Map<String, String> query = this.query;
        if (query == null) {
            query = new HashMap<String, String>();
        }
        String schema = this.secure ? "https" : "http";
        String port = "";

        if (this.timestampRequests) {
            query.put(this.timestampParam, Yeastz.yeast());
        }

        String derivedQuery = ParseQS.encode(query);

        if (this.port > 0 && (("https".equals(schema) && this.port != 443)
                || ("http".equals(schema) && this.port != 80))) {
            port = ":" + this.port;
        }

        if (derivedQuery.length() > 0) {
            derivedQuery = "?" + derivedQuery;
        }

        boolean ipv6 = this.hostname.contains(":");
        return schema + "://" + (ipv6 ? "[" + this.hostname + "]" : this.hostname) + port + this.path + derivedQuery;
    }

    abstract protected void doWrite(byte[] data, Runnable fn);

    abstract protected void doWrite(String data, Runnable fn);

    abstract protected void doPoll();
}
