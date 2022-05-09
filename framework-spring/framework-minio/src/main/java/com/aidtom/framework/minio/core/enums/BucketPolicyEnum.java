package com.aidtom.framework.minio.core.enums;

import com.aidtom.framework.minio.core.constants.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限枚举
 *
 * @author tanghaihua
 * @date 2022/5/9
 */
@Getter
@AllArgsConstructor
public enum BucketPolicyEnum {
    READ_ONLY("read-only", "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + ConstantUtil.BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + ConstantUtil.BUCKET_PARAM + "/*\"]}]}"),
    WRITE_ONLY("write-only", "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + ConstantUtil.BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + ConstantUtil.BUCKET_PARAM + "/*\"]}]}"),
    READ_WRITE("read-write", "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + ConstantUtil.BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + ConstantUtil.BUCKET_PARAM + "/*\"]}]}");

    /**
     * 权限编码
     */
    private String authCode;
    /**
     * 权限详情
     */
    private String authDetail;
}
