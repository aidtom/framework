package com.aidtom.framework.minio;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Minio 配置属性
 *
 * @author tanghaihua
 * @date 2021/4/26
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "framework.minio")
public class MinioAutoProperties {

    /**
     * 服务地址
     */
    @NotEmpty(message = "minio服务地址不可为空")
    @URL(message = "minio服务地址格式错误")
    private String url;

    /**
     * 认证账户
     */
    @NotEmpty(message = "minio认证账户不可为空")
    private String accessKey;

    /**
     * 认证密码
     */
    @NotEmpty(message = "minio认证密码不可为空")
    private String secretKey;

    /**
     * 桶名称, 优先级最低
     */
    private String bucket;

    /**
     * 桶不在的时候是否新建桶
     */
    private boolean createBucket = true;

    /**
     * 启动的时候检查桶是否存在
     */
    private boolean checkBucket = true;

    /**
     * 设置HTTP连接、写入和读取超时。值为0意味着没有超时
     * HTTP连接超时，以毫秒为单位。
     */
    private long connectTimeout;

    /**
     * 设置HTTP连接、写入和读取超时。值为0意味着没有超时
     * HTTP写超时，以毫秒为单位。
     */
    private long writeTimeout;

    /**
     * 设置HTTP连接、写入和读取超时。值为0意味着没有超时
     * HTTP读取超时，以毫秒为单位。
     */
    private long readTimeout;
}
