package com.aidtom.framework.minio.core.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * 对象信息模型
 *
 * @author tanghaihua
 * @date 2022/4/27
 */
@ToString
@Builder
@Data
@Accessors(chain = true)
public class ObjectItem {
    private String objectName;
    private long objectSize;
    private Map<String, String> userMetadata;
    private ZonedDateTime lastModified;
}
