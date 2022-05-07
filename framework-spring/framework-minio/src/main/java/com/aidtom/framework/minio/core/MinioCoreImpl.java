package com.aidtom.framework.minio.core;

import cn.hutool.core.date.DateUtil;
import com.aidtom.framework.minio.MinioAutoProperties;
import com.aidtom.framework.minio.core.model.ObjectInfo;
import com.aidtom.framework.minio.core.model.ObjectItem;
import com.google.common.collect.Maps;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Minio core 实现
 *
 * @author tom
 * @date 2022/4/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioCoreImpl implements MinioCore {
    //@Resource
    private final MinioClient minioClient;

    //@Resource
    private final MinioAutoProperties minioAutoProperties;

    /**
     * 桶占位符
     */
    private static final String BUCKET_PARAM = "${bucket}";
    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只写
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";


    @Override
    public Boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("检查桶是否存在失败!", e);
        }
    }

    @Override
    public void createBucket(String bucketName) {
        if (!this.bucketExists(bucketName)) {
            try {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } catch (Exception e) {
                throw new RuntimeException("创建桶失败!", e);
            }
        }
    }

    @Override
    public String putObject(MultipartFile file) {
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(Objects.requireNonNull(file.getOriginalFilename()));
        // 开始上传
        this.putMultipartFile(minioAutoProperties.getBucket(), fileName, file);
        return minioAutoProperties.getUrl() + "/" + minioAutoProperties.getBucket() + "/" + fileName;
    }

    @Override
    public String putObject(String objectName, InputStream inputStream, String contentType) {
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(objectName);
        // 开始上传
        this.putInputStream(minioAutoProperties.getBucket(), fileName, inputStream, contentType);
        return minioAutoProperties.getUrl() + "/" + minioAutoProperties.getBucket() + "/" + fileName;
    }

    @Override
    public String putObject(String objectName, byte[] bytes, String contentType) {
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(objectName);
        // 开始上传
        this.putBytes(minioAutoProperties.getBucket(), fileName, bytes, contentType);
        return minioAutoProperties.getUrl() + "/" + minioAutoProperties.getBucket() + "/" + fileName;
    }

    @Override
    public void putBucketPolicy(String bucket, String policy) {
        try {
            switch (policy) {
                case "read-only":
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_ONLY.replace(BUCKET_PARAM, bucket)).build());
                    break;
                case "write-only":
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(WRITE_ONLY.replace(BUCKET_PARAM, bucket)).build());
                    break;
                case "read-write":
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("putBucketPolicy error. ", e);
            throw new RuntimeException("设置桶策略失败");
        }
    }

    @Override
    public void putBucketTags(String bucket, Map<String, String> tags) {
        try {
            minioClient.setBucketTags(SetBucketTagsArgs.builder().bucket(bucket).tags(tags).build());
        } catch (Exception e) {
            throw new RuntimeException("设置桶标签异常", e);
        }
    }

    @Override
    public String putObject(String objectName, MultipartFile file) {
        // 给文件名添加时间戳防止重复
        objectName = getFileName(objectName);
        // 开始上传
        this.putMultipartFile(minioAutoProperties.getBucket(), objectName, file);
        return minioAutoProperties.getUrl() + "/" + minioAutoProperties.getBucket() + "/" + objectName;
    }

    @Override
    public String putObject(String bucketName, String objectName, MultipartFile file) {
        // 先创建桶
        this.createBucket(bucketName);
        // 给文件名添加时间戳防止重复
        objectName = getFileName(objectName);
        // 开始上传
        this.putMultipartFile(bucketName, objectName, file);
        return minioAutoProperties.getUrl() + "/" + bucketName + "/" + objectName;
    }

    @Override
    public String putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        // 先创建桶
        this.createBucket(bucketName);
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(objectName);
        // 开始上传
        this.putInputStream(bucketName, fileName, inputStream, contentType);
        return minioAutoProperties.getUrl() + "/" + bucketName + "/" + fileName;
    }

    @Override
    public String putObject(String bucketName, String objectName, byte[] bytes, String contentType) {
        // 先创建桶
        this.createBucket(bucketName);
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(objectName);
        // 开始上传
        this.putBytes(bucketName, fileName, bytes, contentType);
        return minioAutoProperties.getUrl() + "/" + bucketName + "/" + fileName;
    }

    @Override
    public String putObject(String objectName, File file, String contentType) {
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(objectName);
        // 开始上传
        this.putFile(minioAutoProperties.getBucket(), fileName, file, contentType);
        return minioAutoProperties.getUrl() + "/" + minioAutoProperties.getBucket() + "/" + fileName;
    }

    @Override
    public String putObject(String bucketName, String objectName, File file, String contentType) {
        // 先创建桶
        this.createBucket(bucketName);
        // 给文件名添加时间戳防止重复
        String fileName = getFileName(objectName);
        // 开始上传
        this.putFile(bucketName, fileName, file, contentType);
        return minioAutoProperties.getUrl() + "/" + bucketName + "/" + fileName;
    }

    @Override
    public void putObjectTags(String bucket, String objectName, Map<String, String> tags) {
        try {
            minioClient.setObjectTags(SetObjectTagsArgs.builder().bucket(bucket).object(objectName).tags(tags).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("设置对象标签异常", e);
        }
    }

    @Override
    public void setObjectRetention(String bucketName, String objectName, long hour) {
        try {
            Retention retention = new Retention(RetentionMode.COMPLIANCE, ZonedDateTime.now().plusHours(hour));
            minioClient.setObjectRetention(SetObjectRetentionArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .config(retention)
                    .bypassGovernanceMode(true)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("设置对象保留异常.", e);
        }
    }

    @Override
    public void setBucketEncryption(String bucketName) {
        try {
            minioClient.setBucketEncryption(SetBucketEncryptionArgs.builder()
                    .bucket(bucketName)
                    .config(SseConfiguration.newConfigWithSseS3Rule())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("设置桶加密方式异常", e);
        }
    }

    @Override
    public Boolean checkFileIsExist(String objectName) {
        return this.checkFileIsExist(minioAutoProperties.getBucket(), objectName);
    }

    @Override
    public Boolean checkFolderIsExist(String folderName) {
        return this.checkFolderIsExist(minioAutoProperties.getBucket(), folderName);
    }

    @Override
    public Boolean checkFileIsExist(String bucketName, String objectName) {
        boolean exist = true;
        try {
            minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectName).build()
            );
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    @Override
    public Boolean checkFolderIsExist(String bucketName, String folderName) {
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket(minioAutoProperties.getBucket())
                            .prefix(folderName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && folderName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    @Override
    public InputStream getObject(String objectName) {
        return this.getObject(minioAutoProperties.getBucket(), objectName);
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return minioClient
                    .getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("根据文件名获取流失败!", e);
        }
    }

    @Override
    public InputStream getObjectByUrl(String url) {
        try {
            return new URL(url).openStream();
        } catch (IOException e) {
            throw new RuntimeException("根据URL获取流失败!", e);
        }
    }

    @Override
    public String getShareFiledUrl(String bucket, String objectKey, int expires) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET).bucket(bucket)
                    .object(objectKey)
                    .expiry(expires, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("获取签名文件Url异常.", e);
        }
    }


    @Override
    public String getUploadFilePutUrl(String bucketName, String dir, String fileName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .object(dir + fileName)
                    .expiry(expiry, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("获取签名上传文件Url异常.", e);
        }
    }

    @Override
    public Map<String, String> getUploadFilePostUrl(String bucketName, String dir, String fileName, int expiry) {
        try {
            String rootPath = StringUtils.EMPTY;
            if (StringUtils.isNotBlank(dir)) {
                rootPath = dir.endsWith("/") ? dir : dir + "/";
            }
            rootPath = rootPath + DateUtil.format(new Date(), "yyyyMMdd") + "/" + fileName;

            PostPolicy policy = new PostPolicy(bucketName, ZonedDateTime.now().plusMinutes(expiry));
            //设置一个参数key，值为上传对象的名称
            policy.addEqualsCondition("key", rootPath);
            //设置上传文件的大小 1kiB to 10MiB.
            //policy.addContentLengthRangeCondition(1024, 10 * 1024 * 1024);
            Map<String, String> map = minioClient.getPresignedPostFormData(policy);
            map.put("key", rootPath);
            return map;
        } catch (Exception e) {
            throw new RuntimeException("获取签名上传文件Url异常.", e);
        }
    }

    @Override
    public ObjectInfo getObjectInfo(String bucket, String object) {
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(object).build());
            if (statObjectResponse != null) {
                return ObjectInfo.builder().bucket(statObjectResponse.bucket())
                        .object(statObjectResponse.object())
                        .objectSize(statObjectResponse.size())
                        .etag(statObjectResponse.etag())
                        .contentType(statObjectResponse.contentType())
                        .userMetadata(statObjectResponse.userMetadata())
                        .lastModifiedTime(statObjectResponse.lastModified())
                        .build();
            } else {
                return ObjectInfo.builder().build();
            }
        } catch (Exception e) {
            throw new RuntimeException("获取对象信息异常.", e);
        }
    }

    @Override
    public Map<String, String> getObjectTags(String bucket, String object) {
        try {
            Tags objectTags = minioClient.getObjectTags(GetObjectTagsArgs.builder().bucket(bucket).object(object).build());
            return objectTags.get();
        } catch (Exception e) {
            throw new RuntimeException("获取对象标签异常", e);
        }
    }

    @Override
    public Map<String, String> getBucketTags(String bucket) {
        try {
            Tags bucketTags = minioClient.getBucketTags(GetBucketTagsArgs.builder().bucket(bucket).build());
            return bucketTags.get();
        } catch (Exception e) {
            throw new RuntimeException("获取桶标签异常", e);
        }
    }

    @Override
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException("获取全部存储桶失败!", e);
        }
    }

    @Override
    public List<ObjectItem> getBucketObjects(String bucketName, String prefix, String startAfter, Integer max) {
        ListObjectsArgs.Builder bucket = ListObjectsArgs.builder().bucket(bucketName);
        if (StringUtils.isNotBlank(prefix)) {
            bucket.prefix(prefix);
        }
        if (StringUtils.isNotBlank(startAfter)) {
            bucket.startAfter(startAfter);
        }
        if (max != null) {
            bucket.maxKeys(max);
        }

        List<ObjectItem> objectItems = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(bucket.build());
        Optional.ofNullable(results).orElse(Collections.emptyList()).forEach(itemResult -> {
            try {
                Item item = itemResult.get();
                ObjectItem build = ObjectItem.builder().objectName(item.objectName())
                        .objectSize(item.size())
                        .lastModified(item.lastModified())
                        .userMetadata(item.userMetadata())
                        .build();
                objectItems.add(build);
            } catch (Exception e) {
                log.error("getBucketObjects error. ", e);
            }
        });
        return objectItems;
    }

    @Override
    public Optional<Bucket> getBucket(String bucketName) {
        try {
            return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
        } catch (Exception e) {
            throw new RuntimeException("根据存储桶名称获取信息失败!", e);
        }
    }

    @Override
    public SseConfiguration getBucketEncryption(String bucketName) {
        SseConfiguration bucketEncryption = null;
        try {
            bucketEncryption = minioClient.getBucketEncryption(GetBucketEncryptionArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("获取桶加密异常.", e);
        }
        return bucketEncryption;
    }

    @Override
    public void removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("根据存储桶名称删除桶失败!", e);
        }
    }

    @Override
    public void removeObject(String objectName) {
        this.removeObject(minioAutoProperties.getBucket(), objectName);
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("删除文件失败!", e);
        }
    }

    @Override
    public Map<String, String> removeObjects(String bucketName, List<String> objectNames) {
        Map<String, String> errorMap = Maps.newHashMap();

        List<DeleteObject> objects = Optional.ofNullable(objectNames).orElse(Collections.emptyList()).stream()
                .map(objectName -> new DeleteObject(objectName))
                .collect(Collectors.toList());

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
        Optional.ofNullable(results).orElse(Collections.emptyList()).forEach(deleteErrorResult -> {
            try {
                DeleteError error = deleteErrorResult.get();
                errorMap.putIfAbsent(error.objectName(), error.message());
            } catch (Exception e) {
                log.error("removeObjects error. ", e);
            }
        });
        return errorMap;
    }

    @Override
    public void deleteBucketTags(String bucketName) {
        try {
            minioClient.deleteBucketTags(DeleteBucketTagsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("删除桶标签异常.", e);
        }
    }

    @Override
    public void deleteObjectTags(String bucketName, String objectName) {
        try {
            minioClient.deleteObjectTags(DeleteObjectTagsArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("删除对象文件标签异常.", e);
        }
    }

    /**
     * 上传MultipartFile通用方法
     *
     * @param bucketName 桶名称
     * @param objectName 文件名
     * @param file       文件
     */
    private void putMultipartFile(String bucketName, String objectName, MultipartFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("文件流获取错误", e);
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(file.getContentType())
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件流上传错误", e);
        }
    }

    /**
     * 上传InputStream通用方法
     *
     * @param bucketName  桶名称
     * @param objectName  文件名
     * @param inputStream 文件流
     */
    private void putInputStream(String bucketName, String objectName, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件流上传错误", e);
        }
    }

    /**
     * 上传 bytes 通用方法
     *
     * @param bucketName 桶名称
     * @param objectName 文件名
     * @param bytes      字节编码
     */
    private void putBytes(String bucketName, String objectName, byte[] bytes, String contentType) {
        // 字节转文件流
        InputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件流上传错误", e);
        }
    }

    /**
     * 上传 file 通用方法
     *
     * @param bucketName
     * @param objectName
     * @param file
     * @param contentType
     */
    private void putFile(String bucketName, String objectName, File file, String contentType) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(fileInputStream, fileInputStream.available(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件上传错误", e);
        }
    }

    /**
     * 生成唯一ID
     *
     * @param objectName
     * @return
     */
    private static String getFileName(String objectName) {
        // 获取文件前缀,已最后一个点进行分割
        String filePrefix = objectName.substring(0, objectName.lastIndexOf("."));
        // 获取文件后缀,已最后一个点进行分割
        String fileSuffix = objectName.substring(objectName.lastIndexOf(".") + 1);
        // 组成唯一文件名
        return String.format("%s_%s.%s", filePrefix, System.currentTimeMillis(), fileSuffix);
    }
}
