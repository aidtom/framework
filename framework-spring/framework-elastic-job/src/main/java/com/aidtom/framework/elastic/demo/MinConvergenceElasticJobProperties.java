
package com.aidtom.framework.elastic.demo;

import com.aidtom.framework.elastic.ElasticJobConfig;
import lombok.Data;


/**
 * 分钟级收配置
 *
 * @author tanghaihua
 * @date 2022/3/14
 */
@Data
public class MinConvergenceElasticJobProperties implements ElasticJobConfig {
    private String jobName;
    private String cron;
    private int shardingTotal;


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getShardingTotal() {
        return shardingTotal;
    }

    public void setShardingTotal(int shardingTotal) {
        this.shardingTotal = shardingTotal;
    }
}

