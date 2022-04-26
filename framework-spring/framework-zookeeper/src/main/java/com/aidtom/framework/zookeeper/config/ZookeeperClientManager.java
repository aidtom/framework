package com.aidtom.framework.zookeeper.config;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author tanghaihua
 */
public interface ZookeeperClientManager {

    CuratorFramework getClient(String cluster);
}
