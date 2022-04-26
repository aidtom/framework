package com.aidtom.framework.etcd.utils;

import io.etcd.jetcd.ByteSequence;

public interface KeyParser<T> {

    T parser(String keyPrefix, ByteSequence key);
}
