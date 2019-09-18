package com.arakitski.revolut.service;

import java.util.concurrent.ConcurrentHashMap;

public class MutexFactoryImpl<Key> implements MutexFactory<Key> {

    private ConcurrentHashMap<Key, Object> map = new ConcurrentHashMap<>();

    @Override
    public Object getMutex(Key key) {
        return map.compute(key, (k, v) -> v == null ? new Object() : v);
    }
}
