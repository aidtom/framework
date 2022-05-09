package com.aidtom.framework.minio.core;

import com.aidtom.framework.minio.core.enums.BucketPolicyEnum;
import com.aidtom.framework.minio.core.model.ObjectInfo;
import com.aidtom.framework.minio.core.model.ObjectItem;
import io.minio.messages.Bucket;
import io.minio.messages.SseConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * minio core
 *
 * @author tom
 * @date 2022/4/26
 */
public interface MinioCore {
    /**
     * 判断桶是否存在
     *
     * @param bucketName bucket名称
     * @return true存在，false不存在
     */
    Boolean bucketExists(String bucketName);

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    void createBucket(String bucketName);

    /**
     * 设置文件桶的策略
     *
     * @param bucket
     * @param bucketPolicyEnum
     */
    void putBucketPolicy(String bucket, BucketPolicyEnum bucketPolicyEnum);

    /**
     * 文件文件桶的标签
     *
     * @param bucket
     * @param tags
     */
    void putBucketTags(String bucket, Map<String, String> tags);

    /**
     * 上传MultipartFile文件到全局默认文件桶中
     *
     * @param file 文件
     * @return 文件对应的URL
     */
    String putObject(MultipartFile file);

    /**
     * 上传文件
     *
     * @param objectName  文件名
     * @param inputStream 文件流
     * @param contentType 文件类型
     * @return 文件url
     */
    String putObject(String objectName, InputStream inputStream, String contentType);

    /**
     * 上传bytes文件
     *
     * @param objectName  文件名
     * @param bytes       字节
     * @param contentType 文件类型
     * @return 文件url
     */
    String putObject(String objectName, byte[] bytes, String contentType);

    /**
     * 上传File文件
     *
     * @param objectName  文件名
     * @param file        文件
     * @param contentType 文件类型
     * @return 文件url
     */
    String putObject(String objectName, File file, String contentType);

    /**
     * 上传MultipartFile文件到全局默认文件桶下的folder文件夹下
     *
     * @param objectName 文件名称, 如果要带文件夹请用 / 分割, 例如 /help/index.html
     * @param file       文件
     * @return 文件对应的URL
     */
    String putObject(String objectName, MultipartFile file);

    /**
     * 上传MultipartFile文件到指定的文件桶下
     *
     * @param bucketName 桶名称
     * @param objectName 文件名称, 如果要带文件夹请用 / 分割
     * @param file       文件
     * @return 文件对应的URL
     */
    String putObject(String bucketName, String objectName, MultipartFile file);

    /**
     * 上传流到指定的文件桶下
     *
     * @param bucketName  桶名称
     * @param objectName  文件名称, 如果要带文件夹请用 / 分割
     * @param inputStream 文件流
     * @param contentType 文件类型, 例如 image/jpeg: jpg图片格式, 详细可看: https://www.runoob.com/http/http-content-type.html
     * @return 文件对应的URL
     */
    String putObject(String bucketName, String objectName, InputStream inputStream, String contentType);

    /**
     * 上传流到指定的文件桶下
     *
     * @param bucketName  桶名称
     * @param objectName  文件名称, 如果要带文件夹请用 / 分割
     * @param bytes       字节
     * @param contentType 文件类型, 例如 image/jpeg: jpg图片格式, 详细可看: https://www.runoob.com/http/http-content-type.html
     * @return 文件对应的URL
     */
    String putObject(String bucketName, String objectName, byte[] bytes, String contentType);

    /**
     * 上传File文件
     *
     * @param bucketName  文件桶
     * @param objectName  文件名
     * @param file        文件
     * @param contentType 文件类型, 例如 image/jpeg: jpg图片格式, 详细可看: https://www.runoob.com/http/http-content-type.html
     * @return 文件对应的URL
     */
    String putObject(String bucketName, String objectName, File file, String contentType);

    /**
     * 设置桶里文件对象标签
     *
     * @param bucketName
     * @param tags
     */
    void putObjectTags(String bucketName, String objectName, Map<String, String> tags);

    /**
     * 设置对象保留配置
     * 合规模式 COMPLIANCE :防止任何会改变或修改对象或其锁定设置的操作。 没有 MinIO 用户可以修改对象或其设置，包括 MinIO root用户。在配置的保留规则持续时间过后，MinIO 会自动解除锁定
     * 治理模式 GOVERNANCE :防止非特权用户进行任何会改变或修改对象或其锁定设置的操作。拥有存储桶或对象权限的用户s3:BypassGovernanceRetention可以修改对象或其锁定设置。在配置的保留规则持续时间过后，MinIO 会自动解除锁定。
     *
     * @param bucketName
     * @param objectName
     */
    void setObjectRetention(String bucketName, String objectName, long hour);

    /**
     * 设置存储桶的加密配置
     *
     * @param bucketName
     */
    void setBucketEncryption(String bucketName);

    /**
     * 判断文件是否存在
     *
     * @param objectName 文件名称, 如果要带文件夹请用 / 分割
     * @return true存在, 反之
     */
    Boolean checkFileIsExist(String objectName);

    /**
     * 判断文件夹是否存在
     *
     * @param folderName 文件夹名称
     * @return true存在, 反之
     */
    Boolean checkFolderIsExist(String folderName);

    /**
     * 判断文件是否存在
     *
     * @param bucketName 桶名称
     * @param objectName 文件名称, 如果要带文件夹请用 / 分割
     * @return true存在, 反之
     */
    Boolean checkFileIsExist(String bucketName, String objectName);

    /**
     * 判断文件夹是否存在
     *
     * @param bucketName 桶名称
     * @param folderName 文件夹名称
     * @return true存在, 反之
     */
    Boolean checkFolderIsExist(String bucketName, String folderName);

    /**
     * 根据文件全路径获取文件流
     *
     * @param objectName 文件名称
     * @return 文件流
     */
    InputStream getObject(String objectName);

    /**
     * 根据文件桶和文件全路径获取文件流
     *
     * @param bucketName 桶名称
     * @param objectName 文件名
     * @return 文件流
     */
    InputStream getObject(String bucketName, String objectName);

    /**
     * 根据url获取文件流
     *
     * @param url 文件对于URL
     * @return 文件流
     */
    InputStream getObjectByUrl(String url);

    /**
     * 获取对象信息
     *
     * @param bucket
     * @param object
     */
    ObjectInfo getObjectInfo(String bucket, String object);

    /**
     * 获取桶文件分享url
     *
     * @param bucketName 桶
     * @param objectKey  文件key
     * @param expires    签名有效时间  单位分
     * @return 文件签名地址
     */
    String getShareFiledUrl(String bucketName, String objectKey, int expires);

    /**
     * 获取直传文件put方式url
     *
     * @param bucketName 桶名
     * @param dir        bucket下目录，开头不加/
     * @param fileName   文件名包含路径
     * @param expiry     过期时间，分
     * @return
     */
    String getUploadFilePutUrl(String bucketName, String dir, String fileName, int expiry);

    /**
     * 获取直传文件post方式url
     *
     * @param bucketName 桶名
     * @param dir        bucket下目录，开头不加/
     * @param fileName   文件名
     * @param expiry     过期时间 单位分
     * @return
     */
    Map<String, String> getUploadFilePostUrl(String bucketName, String dir, String fileName, int expiry);


    /**
     * 获取文件对象标签
     *
     * @param bucket
     * @param object
     * @return
     */
    Map<String, String> getObjectTags(String bucket, String object);

    /**
     * 获取桶标签
     *
     * @param bucket
     * @return
     */
    Map<String, String> getBucketTags(String bucket);

    /**
     * 获取全部bucket
     *
     * @return 所有桶信息
     */
    List<Bucket> getAllBuckets();

    /**
     * 获取桶下所有的对象
     *
     * @param bucketName 桶名
     * @param prefix     前缀
     * @param startAfter 后缀
     * @param max        最大数量
     * @return
     */
    List<ObjectItem> getBucketObjects(String bucketName, String prefix, String startAfter, Integer max);

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     * @return 单个桶信息
     */
    Optional<Bucket> getBucket(String bucketName);

    /**
     * 获取桶的加密配置
     *
     * @param bucketName
     */
    SseConfiguration getBucketEncryption(String bucketName);

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    void removeBucket(String bucketName);

    /**
     * 删除文件
     *
     * @param objectName 文件名称
     */
    void removeObject(String objectName);

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    void removeObject(String bucketName, String objectName);

    /**
     * 批量删除文件
     *
     * @param bucketName
     * @param objectNames
     * @return 删除成功，返回值为空，否则输出删除失败的文件对象
     */
    Map<String, String> removeObjects(String bucketName, List<String> objectNames);

    /**
     * 删除桶标签
     *
     * @param bucketName
     */
    void deleteBucketTags(String bucketName);

    /**
     * 删除对象文件标签
     *
     * @param bucketName
     * @param objectName
     */
    void deleteObjectTags(String bucketName, String objectName);
}
