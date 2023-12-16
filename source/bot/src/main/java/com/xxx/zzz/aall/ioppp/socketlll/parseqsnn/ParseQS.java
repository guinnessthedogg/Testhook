package com.xxx.zzz.aall.ioppp.socketlll.parseqsnn;


import java.util.HashMap;
import java.util.Map;

import com.xxx.zzz.aall.ioppp.socketlll.globalsnn.Globalqd;

public class ParseQS {

    private ParseQS() {}

    public static String encode(Map<String, String> obj) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> entry : obj.entrySet()) {
            if (str.length() > 0) str.append("&");
            str.append(Globalqd.encodeURIComponent(entry.getKey())).append("=")
                    .append(Globalqd.encodeURIComponent(entry.getValue()));
        }
        return str.toString();
    }

    public static Map<String, String> decode(String qs) {
        Map<String, String> qry = new HashMap<String, String>();
        String[] pairs = qs.split("&");
        for (String _pair : pairs) {
            String[] pair = _pair.split("=");
            qry.put(Globalqd.decodeURIComponent(pair[0]),
                    pair.length > 1 ? Globalqd.decodeURIComponent(pair[1]) : "");
        }
        return qry;
    }
}
