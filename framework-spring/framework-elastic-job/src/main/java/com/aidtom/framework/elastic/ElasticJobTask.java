package com.aidtom.framework.elastic;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author tom
 */
@Data
@RequiredArgsConstructor(staticName = "of")
public class ElasticJobTask {

    private final String jobName;

    private final int shardIndex;

    private final int shardTotal;

    private final long timestamp;
}
