package com.aidtom.framework.elastic.config;

import com.aidtom.framework.elastic.ElasticJobConfig;
import com.aidtom.framework.elastic.ElasticJobExecutor;
import com.aidtom.framework.elastic.ElasticJobTask;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class ElasticJobAutoRegister implements DisposableBean {

    private final CoordinatorRegistryCenter registryCenter;

    private volatile boolean initialized = false;

    public ElasticJobAutoRegister(CoordinatorRegistryCenter registryCenter, Map<String, ElasticJobExecutor> shardingJobExecutors) {
        this.registryCenter = registryCenter;
        if (shardingJobExecutors != null && !shardingJobExecutors.isEmpty()) {
            log.info("init elastic-job registry center");
            initialized = true;
            this.registryCenter.init();
            shardingJobExecutors.forEach(this::registerJob);
        }
    }

    private void registerJob(String beanName, ElasticJobExecutor shardingJobExecutor) {
        final ElasticJobConfig jobConfig = shardingJobExecutor.getElasticJobConfig();
        log.info("register sharding job to elastic-job registry center. bean={} jobConfig={}", beanName, jobConfig);
        // 定义作业核心配置
        JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder(jobConfig.getJobName(), jobConfig.getCron(), jobConfig.getShardingTotal())
                .build();
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, shardingJobExecutor.getClass().getCanonicalName());
        // 定义Lite作业根配置
        LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig)
                .overwrite(shardingJobExecutor.overwrite())
                .build();
        final JobScheduler jobScheduler = new JobScheduler(registryCenter, simpleJobRootConfig) {
            @Override
            protected Optional<ElasticJob> createElasticJobInstance() {
                return Optional.of((SimpleJob) shardingContext -> shardingJobExecutor.execute(ElasticJobTask.of(jobConfig.getJobName(), shardingContext.getShardingItem(), shardingContext.getShardingTotalCount(), System.currentTimeMillis())));
            }
        };
        jobScheduler.init();
    }


    @Override
    public void destroy() throws Exception {
        if (!initialized) {
            return;
        }
        try {
            registryCenter.close();
        } catch (Exception e) {
            log.warn("close elastic-job registry center error.", e);
        }
    }

}
