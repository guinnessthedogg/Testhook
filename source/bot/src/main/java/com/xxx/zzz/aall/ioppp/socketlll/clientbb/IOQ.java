package com.xxx.zzz.aall.ioppp.socketlll.clientbb;


import com.xxx.zzz.aall.ioppp.socketlll.parsersnn.Parserqznvjf;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.okhttp3ll.Callzadasd;
import com.xxx.zzz.aall.okhttp3ll.WebSocketzqa;


public class IOQ {

    private static final Logger logger = Logger.getLogger(IOQ.class.getName());

    private static final ConcurrentHashMap<String, ManagerQ> managers = new ConcurrentHashMap<String, ManagerQ>();


    public static int protocol = Parserqznvjf.protocol;

    public static void setDefaultOkHttpWebSocketFactory(WebSocketzqa.Factory factory) {
        ManagerQ.defaultWebSocketFactory = factory;
    }

    public static void setDefaultOkHttpCallFactory(Callzadasd.Factory factory) {
        ManagerQ.defaultCallFactory = factory;
    }

    private IOQ() {}

    public static SocketQ socket(String uri) throws URISyntaxException {
        return socket(uri, null);
    }

    public static SocketQ socket(String uri, Options opts) throws URISyntaxException {
        return socket(new URI(uri), opts);
    }

    public static SocketQ socket(URI uri) {
        return socket(uri, null);
    }


    public static SocketQ socket(URI uri, Options opts) {
        if (opts == null) {
            opts = new Options();
        }

        URL parsed = Urlq.parse(uri);
        URI source;
        try {
            source = parsed.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String id = Urlq.extractId(parsed);
        String path = parsed.getPath();
        boolean sameNamespace = managers.containsKey(id)
                && managers.get(id).nsps.containsKey(path);
        boolean newConnection = opts.forceNew || !opts.multiplex || sameNamespace;
        ManagerQ io;

        if (newConnection) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("ignoring socket cache for %s", source));
            }
            io = new ManagerQ(source, opts);
        } else {
            if (!managers.containsKey(id)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("new io instance for %s", source));
                }
                managers.putIfAbsent(id, new ManagerQ(source, opts));
            }
            io = managers.get(id);
        }

        String query = parsed.getQuery();
        if (query != null && (opts.query == null || opts.query.isEmpty())) {
            opts.query = query;
        }

        return io.socket(parsed.getPath(), opts);
    }


    public static class Options extends ManagerQ.Options {

        public boolean forceNew;


        public boolean multiplex = true;
    }
}
