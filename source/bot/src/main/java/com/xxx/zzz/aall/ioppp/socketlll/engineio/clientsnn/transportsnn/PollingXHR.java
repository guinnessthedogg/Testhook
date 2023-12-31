package com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.transportsnn;


import com.xxx.zzz.aall.ioppp.socketlll.emitterbb.Emitterq;
import com.xxx.zzz.aall.ioppp.socketlll.engineio.clientsnn.Transportqdasa;
import com.xxx.zzz.aall.ioppp.socketlll.threadnnn.EventThreadz;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.okhttp3ll.Callzadasd;
import com.xxx.zzz.aall.okhttp3ll.Callbackza;
import com.xxx.zzz.aall.okhttp3ll.HttpUrlza;
import com.xxx.zzz.aall.okhttp3ll.MediaTypeza;
import com.xxx.zzz.aall.okhttp3ll.OkHttpClientza;
import com.xxx.zzz.aall.okhttp3ll.RequestBodyza;
import com.xxx.zzz.aall.okhttp3ll.Requestza;
import com.xxx.zzz.aall.okhttp3ll.Responseza;
import com.xxx.zzz.aall.okhttp3ll.ResponseBodyza;

public class PollingXHR extends Pollingq {

    private static final Logger logger = Logger.getLogger(PollingXHR.class.getName());

    private static boolean LOGGABLE_FINE = logger.isLoggable(Level.FINE);

    public PollingXHR(Transportqdasa.Options opts) {
        super(opts);
    }

    protected Request request() {
        return this.request(null);
    }

    protected Request request(Request.Options opts) {
        if (opts == null) {
            opts = new Request.Options();
        }
        opts.uri = this.uri();
        opts.callFactory = this.callFactory;

        Request req = new Request(opts);

        final PollingXHR self = this;
        req.on(Request.EVENT_REQUEST_HEADERS, new Emitterq.Listener() {
            @Override
            public void call(Object... args) {

                self.emit(Transportqdasa.EVENT_REQUEST_HEADERS, args[0]);
            }
        }).on(Request.EVENT_RESPONSE_HEADERS, new Emitterq.Listener() {
            @Override
            public void call(final Object... args) {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        self.emit(Transportqdasa.EVENT_RESPONSE_HEADERS, args[0]);
                    }
                });
            }
        });
        return req;
    }

    @Override
    protected void doWrite(byte[] data, final Runnable fn) {
        this.doWrite((Object) data, fn);
    }

    @Override
    protected void doWrite(String data, final Runnable fn) {
        this.doWrite((Object) data, fn);
    }

    private void doWrite(Object data, final Runnable fn) {
        Request.Options opts = new Request.Options();
        opts.method = "POST";
        opts.data = data;
        Request req = this.request(opts);
        final PollingXHR self = this;
        req.on(Request.EVENT_SUCCESS, new Emitterq.Listener() {
            @Override
            public void call(Object... args) {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        fn.run();
                    }
                });
            }
        });
        req.on(Request.EVENT_ERROR, new Emitterq.Listener() {
            @Override
            public void call(final Object... args) {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        Exception err = args.length > 0 && args[0] instanceof Exception ? (Exception)args[0] : null;
                        self.onError("xhr post error", err);
                    }
                });
            }
        });
        req.create();
    }

    @Override
    protected void doPoll() {
        logger.fine("xhr poll");
        Request req = this.request();
        final PollingXHR self = this;
        req.on(Request.EVENT_DATA, new Emitterq.Listener() {
            @Override
            public void call(final Object... args) {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        Object arg = args.length > 0 ? args[0] : null;
                        if (arg instanceof String) {
                            self.onData((String)arg);
                        } else if (arg instanceof byte[]) {
                            self.onData((byte[])arg);
                        }
                    }
                });
            }
        });
        req.on(Request.EVENT_ERROR, new Emitterq.Listener() {
            @Override
            public void call(final Object... args) {
                EventThreadz.exec(new Runnable() {
                    @Override
                    public void run() {
                        Exception err = args.length > 0 && args[0] instanceof Exception ? (Exception) args[0] : null;
                        self.onError("xhr poll error", err);
                    }
                });
            }
        });
        req.create();
    }

    public static class Request extends Emitterq {

        public static final String EVENT_SUCCESS = "success";
        public static final String EVENT_DATA = "data";
        public static final String EVENT_ERROR = "error";
        public static final String EVENT_REQUEST_HEADERS = "requestHeaders";
        public static final String EVENT_RESPONSE_HEADERS = "responseHeaders";

        private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
        private static final String TEXT_CONTENT_TYPE = "text/plain;charset=UTF-8";

        private static final MediaTypeza BINARY_MEDIA_TYPE = MediaTypeza.parse(BINARY_CONTENT_TYPE);
        private static final MediaTypeza TEXT_MEDIA_TYPE = MediaTypeza.parse(TEXT_CONTENT_TYPE);

        private String method;
        private String uri;

        private Object data;

        private Callzadasd.Factory callFactory;
        private Responseza response;
        private Callzadasd requestCall;

        public Request(Options opts) {
            this.method = opts.method != null ? opts.method : "GET";
            this.uri = opts.uri;
            this.data = opts.data;
            this.callFactory = opts.callFactory != null ? opts.callFactory : new OkHttpClientza();
        }

        public void create() {
            final Request self = this;
            if (LOGGABLE_FINE) logger.fine(String.format("xhr open %s: %s", this.method, this.uri));
            Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

            if ("POST".equals(this.method)) {
                if (this.data instanceof byte[]) {
                    headers.put("Content-type", new LinkedList<String>(Collections.singletonList(BINARY_CONTENT_TYPE)));
                } else {
                    headers.put("Content-type", new LinkedList<String>(Collections.singletonList(TEXT_CONTENT_TYPE)));
                }
            }

            headers.put("Accept", new LinkedList<String>(Collections.singletonList("*/*")));

            this.onRequestHeaders(headers);

            if (LOGGABLE_FINE) {
                logger.fine(String.format("sending xhr with url %s | data %s", this.uri,
                        this.data instanceof byte[] ? Arrays.toString((byte[]) this.data) : this.data));
            }

            Requestza.Builder requestBuilder = new Requestza.Builder();
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                for (String v : header.getValue()){
                    requestBuilder.addHeader(header.getKey(), v);
                }
            }
            RequestBodyza body = null;
            if (this.data instanceof byte[]) {
                body = RequestBodyza.create(BINARY_MEDIA_TYPE, (byte[])this.data);
            } else if (this.data instanceof String) {
                body = RequestBodyza.create(TEXT_MEDIA_TYPE, (String)this.data);
            }

            Requestza request = requestBuilder
                    .url(HttpUrlza.parse(self.uri))
                    .method(self.method, body)
                    .build();

            requestCall = callFactory.newCall(request);
            requestCall.enqueue(new Callbackza() {
                @Override
                public void onFailure(Callzadasd call, IOException e) {
                    self.onError(e);
                }

                @Override
                public void onResponse(Callzadasd call, Responseza response) throws IOException {
                    self.response = response;
                    self.onResponseHeaders(response.headers().toMultimap());

                    try {
                        if (response.isSuccessful()) {
                            self.onLoad();
                        } else {
                            self.onError(new IOException(Integer.toString(response.code())));
                        }
                    } finally {
                        response.close();
                    }
                }
            });
        }

        private void onSuccess() {
            this.emit(EVENT_SUCCESS);
        }

        private void onData(String data) {
            this.emit(EVENT_DATA, data);
            this.onSuccess();
        }

        private void onData(byte[] data) {
            this.emit(EVENT_DATA, data);
            this.onSuccess();
        }

        private void onError(Exception err) {
            this.emit(EVENT_ERROR, err);
        }

        private void onRequestHeaders(Map<String, List<String>> headers) {
            this.emit(EVENT_REQUEST_HEADERS, headers);
        }

        private void onResponseHeaders(Map<String, List<String>> headers) {
            this.emit(EVENT_RESPONSE_HEADERS, headers);
        }

        private void onLoad() {
            ResponseBodyza body = response.body();
            String contentType = body.contentType().toString();

            try {
                if (BINARY_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
                    this.onData(body.bytes());
                } else {
                    this.onData(body.string());
                }
            } catch (IOException e) {
                this.onError(e);
            }
        }

        public static class Options {

            public String uri;
            public String method;
            public Object data;
            public Callzadasd.Factory callFactory;
        }
    }
}
