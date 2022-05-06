package com.aidtom.framework.minio.demo;

import com.aidtom.framework.minio.MinioAutoProperties;
import com.aidtom.framework.minio.core.MinioCore;
import com.aidtom.framework.minio.core.MinioCoreImpl;
import com.aidtom.framework.minio.core.model.ObjectItem;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * demo
 *
 * @author tom
 * @date 2022/4/26
 */
@Slf4j
public class App {
    private static MinioClient minioClient;
    private static MinioAutoProperties minioAutoProperties;

    static {
        minioAutoProperties = new MinioAutoProperties();
        minioAutoProperties.setUrl("");
        minioAutoProperties.setAccessKey("minioadmin");
        minioAutoProperties.setSecretKey("minioadmin");
        minioAutoProperties.setBucket("test1");

        log.info("开始初始化MinioClient, url为{}, accessKey为:{}", minioAutoProperties.getUrl(), minioAutoProperties.getAccessKey());
        minioClient = MinioClient
                .builder()
                .endpoint(minioAutoProperties.getUrl())
                .credentials(minioAutoProperties.getAccessKey(), minioAutoProperties.getSecretKey())
                .build();

        minioClient.setTimeout(
                minioAutoProperties.getConnectTimeout(),
                minioAutoProperties.getWriteTimeout(),
                minioAutoProperties.getReadTimeout()
        );
        // Start detection
        if (minioAutoProperties.isCheckBucket()) {
            log.info("checkBucket为 --{}, 开始检测桶是否存在", minioAutoProperties.isCheckBucket());
            String bucketName = minioAutoProperties.getBucket();
            if (!checkBucket(bucketName, minioClient)) {
                log.info("文件桶[{}]不存在, 开始检查是否可以新建桶", bucketName);
                if (minioAutoProperties.isCreateBucket()) {
                    log.info("createBucket为 --{},开始新建文件桶", minioAutoProperties.isCreateBucket());
                    createBucket(bucketName, minioClient);
                }
            }
            log.info("文件桶[{}]已存在, minio客户端连接成功!", bucketName);
        } else {
            throw new RuntimeException("桶不存在, 请检查桶名称是否正确或者将checkBucket属性改为false");
        }
    }


    public static void main(String[] args) {
        MinioCore minioCore = new MinioCoreImpl(minioClient, minioAutoProperties);

/*        List<Bucket> allBuckets = minioCore.getAllBuckets();
        Optional.ofNullable(allBuckets).orElse(Collections.emptyList()).forEach(bucket -> {
            System.out.println(bucket.name() + ",createTime = " + bucket.creationDate());
        });*/

        /*Map<String, String> tags = new HashMap<>();
        tags.put("test", "测试");
        tags.put("book", "书");
        tags.put("sec", "安全");

        minioCore.putBucketTags("test1", tags);*/

        /*Map<String, String> test1 = minioCore.getBucketTags("test1");
        System.out.println(test1);*/
        Map<String, String> tags = new HashMap<>();
        tags.put("test1", "测试");
        tags.put("pic", "图片");

        //minioCore.putObjectTags("test1", "21_1650943870473.jpg", tags);

/*        Map<String, String> test1 = minioCore.getObjectTags("test1", "21_1650943870473.jpg");
        System.out.println(test1);*/

        /*ObjectInfo test1 = minioCore.getObjectInfo("test1", "21_1650943870473.jpg");

        System.out.println(test1.toString());*/
        /*List<String> objectNames = new ArrayList<>();
        objectNames.add("21_1650943870473.jpg");
        objectNames.add("MDC3.0-介绍_1650941412572.pdf");*/

        /*Map<String, String> map = minioCore.removeObjects("test1", objectNames);
        System.out.println(map);*/

/*        List<ObjectItem> test = minioCore.getBucketObjects("test", "", "介绍.pdf", null);
        Optional.ofNullable(test).orElse(Collections.emptyList()).forEach(objectItem -> {
            System.out.println(objectItem);
        });*/

/*        List<ObjectItem> test = minioCore.getBucketObjects("test", "", "pptx", 1000);
        Optional.ofNullable(test).orElse(Collections.emptyList()).forEach(objectItem -> {
            System.out.println(objectItem.toString());
        });*/

        minioCore.createBucket("test3");
        minioCore.setBucketEncryption("test3");
    }


    private static boolean checkBucket(String bucketName, MinioClient minioClient) {
        boolean isExists = false;
        try {
            isExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("failed to check if the bucket exists", e);
        }
        return isExists;
    }

    private static void createBucket(String bucketName, MinioClient minioClient) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("文件桶[{}]新建成功, minio客户端已连接", bucketName);
        } catch (Exception e) {
            throw new RuntimeException("failed to create default bucket", e);
        }
    }
}
