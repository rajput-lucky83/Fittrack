package com.fittrack.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Tiny JSON writer. We only need arrays of strings/numbers for the charts
 * and the live activity feed, so a library would be overkill.
 */
public final class JsonUtil {

    private JsonUtil() { }

    public static String quote(String s) {
        if (s == null) return "\"\"";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': break;
                case '\t': sb.append("\\t"); break;
                // < and > are escaped so the text is safe inside a <script> tag too
                case '<':  sb.append("\\u003c"); break;
                case '>':  sb.append("\\u003e"); break;
                default:   sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    public static String stringArray(Collection<String> items) {
        StringBuilder sb = new StringBuilder("[");
        Iterator<String> it = items.iterator();
        while (it.hasNext()) {
            sb.append(quote(it.next()));
            if (it.hasNext()) sb.append(',');
        }
        return sb.append(']').toString();
    }

    public static String numberArray(Collection<? extends Number> items) {
        StringBuilder sb = new StringBuilder("[");
        Iterator<? extends Number> it = items.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(',');
        }
        return sb.append(']').toString();
    }
}
