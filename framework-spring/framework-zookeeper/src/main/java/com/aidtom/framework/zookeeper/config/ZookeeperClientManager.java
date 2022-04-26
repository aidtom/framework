package com.aidtom.framework.zookeeper.config;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author tom
 */
public interface ZookeeperClientManager {

    CuratorFramework getClient(String cluster);
}
