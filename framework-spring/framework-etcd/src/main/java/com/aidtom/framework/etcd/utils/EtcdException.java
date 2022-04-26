package com.aidtom.framework.etcd.utils;

/**
 * @author tom
 */
public class EtcdException extends Exception {

    public EtcdException(String msg) {
        super(msg);
    }

    public EtcdException(String msg, Throwable e) {
        super(msg, e);
    }
}
