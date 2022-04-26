package com.aidtom.framework.etcd;

import io.etcd.jetcd.Client;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Etcd client 管理中心
 * @author tom
 */
@Data
@Slf4j
@RequiredArgsConstructor
public class EtcdClientManager {

    private Map<String, Client> etcdClientCache = new ConcurrentHashMap<>();

    private final MultiEtcdProperties properties;

    public Client getEtcdClient(String name) {
        if (!etcdClientCache.containsKey(name)) {
            etcdClientCache.computeIfAbsent(name, properties::build);
        }
        return etcdClientCache.get(name);
    }
}
