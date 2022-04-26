package com.aidtom.framework.etcd;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Etcd 配置文件
 * @author tom
 */
@Data
@Accessors(chain = true)
public class EtcdProperties {

    protected String[] endPoints;
    protected String user;
    protected String password;
    protected String authority;
    protected Long keepAliveTime;
    protected Long keepAliveTimeout;
    protected Long retryDelay;
    protected Long retryMaxDelay;
    protected Boolean keepAliveWithoutCalls;
    protected String retryMaxDuration;
    protected Integer connectTimeout;
    protected Boolean discovery;

}
