package com.peiliping.web.server.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class MapX<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private volatile boolean  lock             = false;

    @Override
    @Deprecated
    public V put(K key, V value) {
        if (lock)
            return null;
        return super.put(key, value);
    }

    @Override
    @Deprecated
    public void putAll(Map<? extends K, ? extends V> m) {
        if (lock)
            return;
        super.putAll(m);
    }

    public MapX<K, V> add(K l, V r) {
        if (lock)
            return MapX.this;
        super.put(l, r);
        return MapX.this;
    }

    public MapX<K, V> add(Pair<K, V> pair) {
        if (lock)
            return MapX.this;
        if (pair != null) {
            super.put(pair.getKey(), pair.getValue());
        }
        return MapX.this;
    }

    public MapX<K, V> LockModify() {
        lock = true;
        return MapX.this;
    }

    public static <K, V> MapX<K, V> newMapX(K l, V r) {
        return (new MapX<K, V>()).add(l, r);
    }

    public static MapX<String, Object> newMapXSO(String l, Object v) {
        return (new MapX<String, Object>()).add(l, v);
    }

    public static MapX<String, Object> newMapXSO() {
        return new MapX<String, Object>();
    }
}
