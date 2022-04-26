package com.aidtom.framework.etcd.utils;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 基于 Etcd 实现的并发锁
 * @author tom
 */
@Slf4j
@RequiredArgsConstructor
public class ConcurrencyLock {

    private final Client client;
    private final Integer timeoutSec;

    public Long lock(String lockPath, Integer lockHoldSec, Integer ttlOfLease) throws EtcdException {

        if (client == null) {
            throw new EtcdException("etcd client is null.");
        }
        Lock lockClient = client.getLockClient();
        Lease leaseClient = client.getLeaseClient();
        Long leaseId;
        try {
            leaseId = leaseClient.grant(ttlOfLease).get(timeoutSec, TimeUnit.SECONDS).getID();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("get leaseId error.", e);
            throw new EtcdException("create lease id error.", e);
        }
        try {
            lockClient.lock(ByteSequence.from(lockPath, StandardCharsets.UTF_8),  leaseId).get(lockHoldSec, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("lock resource [{}] failed.", lockPath, e);
            leaseClient.revoke(leaseId);
            throw new EtcdException("get etcd lock failed.");
        }

        return leaseId;
    }

    public void unlock(String lockPath, Long leaseId) throws EtcdException {
        Lock lock = client.getLockClient();
        Lease leaseClient = client.getLeaseClient();
        try {
            lock.unlock(ByteSequence.from(lockPath, StandardCharsets.UTF_8)).get(timeoutSec, TimeUnit.SECONDS);
            if (null != leaseId) {
                leaseClient.revoke(leaseId);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("unlock resource [{}] failed.", lockPath, e);
            throw new EtcdException("unlock etcd resource error.", e);
        }
    }
}
