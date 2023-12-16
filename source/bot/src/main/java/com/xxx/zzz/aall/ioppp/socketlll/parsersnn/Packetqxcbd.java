package com.xxx.zzz.aall.ioppp.socketlll.parsersnn;


public class Packetqxcbd<T> {

    public int type = -1;
    public int id = -1;
    public String nsp;
    public T data;
    public int attachments;
    public String query;

    public Packetqxcbd() {}

    public Packetqxcbd(int type) {
        this.type = type;
    }

    public Packetqxcbd(int type, T data) {
        this.type = type;
        this.data = data;
    }
}
