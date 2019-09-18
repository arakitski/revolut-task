package com.arakitski.revolut.service;

public interface MutexFactory<Key> {
    Object getMutex(Key key);
}
