package com.aidtom.framework.minio.demo;

import cn.hutool.core.io.IoUtil;
import com.aidtom.framework.minio.MinioAutoProperties;
import com.aidtom.framework.minio.core.MinioCore;
import com.aidtom.framework.minio.core.MinioCoreImpl;
import com.aidtom.framework.minio.core.model.ObjectItem;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
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
        minioAutoProperties.setUrl("http://127.0.0.1:9000");
        minioAutoProperties.setAccessKey("minioadmin");
        minioAutoProperties.setSecretKey("minioadmin");
        minioAutoProperties.setBucket("test");

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

        try {
            //test/pdf/20220507/mdc.pdf
            InputStream inputStream = minioCore.getObject("test", "pdf/20220507/mdc.pdf");
            //InputStream inputStream = minioCore.getObjectByUrl("http://10.254.50.6:9000/test/pdf/20220507/mdc.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=QTI34S8GT5KVG6MWCEN1%2F20220511%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220511T101900Z&X-Amz-Expires=604800&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJRVEkzNFM4R1Q1S1ZHNk1XQ0VOMSIsImV4cCI6MTY1MjI2NzkyOCwicGFyZW50IjoibWluaW9hZG1pbiJ9.JMYc4Wi0g-HbvX_dngIii82yYObz6Lc-F4tUHo68skOOPZ8SKyClRCah6TsCDsSuqPZj8G1aLFqLx-RGjwtBmQ&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=4ba1f847dcc68db6cf721d224482193212cdfe26e6cac556ccebdede7166c8e9");
            FileOutputStream outputStream = new FileOutputStream("/Users/tanghaihua/project/framework/framework-spring/framework-minio/src/main/java/com/aidtom/framework/minio/demo/mdc.pdf");
            long copy = IoUtil.copy(inputStream, outputStream);
            System.out.println(copy);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


/*        Map<String, String> map = minioCore.getUploadFilePostUrl("test", "pdf", "mdc.pdf", 5);
        System.out.println(map);*/

        //minioCore.putObjectTags("test", "20.jpg", tags);

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
