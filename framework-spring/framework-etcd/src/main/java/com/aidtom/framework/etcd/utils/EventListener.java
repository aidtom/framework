package com.aidtom.framework.etcd.utils;

/**
 * @author tom
 */
public interface EventListener<K, V> {

    void onChange(EventTypeEnum type, EventValue<K, V> oldVal, EventValue<K, V> newVal, Exception e);
}
