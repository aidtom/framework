package com.aidtom.framework.elastic.config;

import com.aidtom.framework.zookeeper.config.ZookeeperClientManager;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ConfigurationProperties("elastic-job")
@Data
@Validated
public class ElasticJobConfigurationProperties {

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private ElasticJobZkConfig zookeeper;

    public CoordinatorRegistryCenter buildRegistryCenter(Environment environment,
                                                         ZookeeperClientManager zookeeperClientManager) {
        if (StringUtils.isNotBlank(zookeeper.getCluster())) {
            CuratorFramework client = zookeeperClientManager.getClient(zookeeper.getCluster());
            return new ElasticZookeeperRegistryCenter(client, zookeeper.getConnectionTimeout());
        }

        final String namespace = Stream
                .<Supplier<String>>of(
                        () -> zookeeper.getNamespace(),
                        () -> environment.getProperty("spring.application.name")
                )
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("can't found namespace of elastic-job ZK setting. set 'elastic-job.zookeeper.namespace' or 'spring.application.name'"));

        final String zkServers = String.join(",", zookeeper.getServers());

        final ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(zkServers, namespace);
        zkConfig.setBaseSleepTimeMilliseconds(Math.toIntExact(zookeeper.getBaseSleep().toMillis()));
        zkConfig.setMaxSleepTimeMilliseconds(Math.toIntExact(zookeeper.getMaxSleep().toMillis()));
        zkConfig.setMaxRetries(zookeeper.getMaxRetries());
        zkConfig.setSessionTimeoutMilliseconds(Math.toIntExact(zookeeper.getSessionTimeout().toMillis()));
        zkConfig.setConnectionTimeoutMilliseconds(Math.toIntExact(zookeeper.getConnectionTimeout().toMillis()));
        zkConfig.setDigest(zookeeper.getDigest());
        
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(zkConfig.getServerLists())
                .retryPolicy(new ExponentialBackoffRetry(zkConfig.getBaseSleepTimeMilliseconds(), zkConfig.getMaxRetries(), zkConfig.getMaxSleepTimeMilliseconds()))
                .namespace(zkConfig.getNamespace());
        if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
            builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
        }

        if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
            builder.connectionTimeoutMs(zkConfig.getConnectionTimeoutMilliseconds());
        }

        if (!Strings.isNullOrEmpty(zkConfig.getDigest())) {
            builder.authorization("digest", zkConfig.getDigest().getBytes(Charsets.UTF_8)).aclProvider(new ACLProvider() {
                public List<ACL> getDefaultAcl() {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }

                public List<ACL> getAclForPath(String path) {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }
            });
        }

        return new ElasticZookeeperRegistryCenter(builder.build(), zookeeper.getConnectionTimeout());
    }
}
