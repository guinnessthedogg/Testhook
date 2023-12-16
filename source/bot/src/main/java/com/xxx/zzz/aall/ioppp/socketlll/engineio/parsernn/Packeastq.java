package com.xxx.zzz.aall.ioppp.socketlll.engineio.parsernn;


public class Packeastq<T> {

    static final public String OPEN = "open";
    static final public String CLOSE = "close";
    static final public String PING = "ping";
    static final public String PONG = "pong";
    static final public String UPGRADE = "upgrade";
    static final public String MESSAGE = "message";
    static final public String NOOP = "noop";
    static final public String ERROR = "error";

    public String type;
    public T data;


    public Packeastq(String type) {
        this(type, null);
    }

    public Packeastq(String type, T data) {
        this.type = type;
        this.data = data;
    }
}
