package com.aidtom.framework.minio.core.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * 对象信息模型
 *
 * @author tanghaihua
 * @date 2022/4/27
 */
@Builder
@Data
@Accessors(chain = true)
public class ObjectInfo {
    private String bucket;
    private String object;
    private String etag;
    private Map<String, String> userMetadata;
    private long objectSize;
    private String contentType;
    private ZonedDateTime lastModifiedTime;
}
