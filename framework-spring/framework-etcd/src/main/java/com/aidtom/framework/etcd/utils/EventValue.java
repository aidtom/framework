package com.aidtom.framework.etcd.utils;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tom
 */
@Data
@Accessors(chain = true)
public class EventValue<K, V> {
    private String key;
    private long createVersion;
    private long lease;
    private long version;
    private long modVersion;
    private K keyObj;
    private V value;
}
