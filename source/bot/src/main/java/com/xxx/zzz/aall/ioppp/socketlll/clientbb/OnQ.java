package com.xxx.zzz.aall.ioppp.socketlll.clientbb;

import com.xxx.zzz.aall.ioppp.socketlll.emitterbb.Emitterq;

public class OnQ {

    private OnQ() {}

    public static Handle on(final Emitterq obj, final String ev, final Emitterq.Listener fn) {
        obj.on(ev, fn);
        return new Handle() {
            @Override
            public void destroy() {
                obj.off(ev, fn);
            }
        };
    }

    public static interface Handle {

        public void destroy();
    }
}
