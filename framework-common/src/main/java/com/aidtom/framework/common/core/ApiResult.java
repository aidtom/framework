package com.aidtom.framework.common.core;

import com.aidtom.framework.common.core.enums.GlobalCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 接口返回结果封装
 *
 * @author tanghaihua
 * @date 2020/11/16
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class ApiResult<T> implements Serializable {
    /**
     * 状态码
     */
    private String code = GlobalCode.SUCCESS.getCode();
    private String message = GlobalCode.SUCCESS.getMessage();

    /**
     * 正确结果
     */
    private T data;

    public static ApiResult custom(String code, String msg) {
        ApiResult apiResult = ApiResult.builder()
                .code(code)
                .message(msg)
                .build();

        return apiResult;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return GlobalCode.SUCCESS.getCode().equals(code);
    }

    /**
     * 构造正常结果
     */
    public static ApiResult success() {
        return success(null);
    }

    /**
     * 构造正常结果
     */
    public static <T> ApiResult<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 构造正常结果
     */
    public static <T> ApiResult<T> success(String message) {
        return success(null, message);
    }

    /**
     * 构造正常结果
     */
    public static <T> ApiResult<T> success(T data, String message) {
        ApiResult apiResult = ApiResult.builder()
                .code(GlobalCode.SUCCESS.getCode())
                .data(data)
                .message(message)
                .build();

        return apiResult;
    }

    /**
     * 构造异常结果
     *
     * @param resultCode 异常码枚举
     */
    public static ApiResult fail(IResultCode resultCode) {
        return ApiResult.builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .build();
    }

    /**
     * 构造异常结果
     *
     * @param message 异常信息
     */
    public static ApiResult fail(String message) {
        return ApiResult.builder()
                .code(GlobalCode.FAILURE.getCode())
                .message(message)
                .build();
    }


    /**
     * 动态错误消息定义，适用于参数校验需要返回具体参数名等场景
     *
     * @param resultCode 错误码
     * @param message    动态错误消息，返回结果中的实际错误消息将是resultCode.getMessage() + ": " + message
     */
    public static ApiResult fail(IResultCode resultCode, String message) {
        String separator = StringUtils.isEmpty(resultCode.getMessage()) || StringUtils.isEmpty(message) ? "" : ":";
        return ApiResult.builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage() + separator + message)
                .build();
    }

    /**
     * 构造异常结果
     *
     * @param data 对象
     */
    public static <T> ApiResult<T> fail(T data) {
        return fail(GlobalCode.FAILURE, data);
    }

    /**
     * 构造异常结果
     *
     * @param resultCode 异常码枚举
     * @param data       对象
     */
    public static <T> ApiResult<T> fail(IResultCode resultCode, T data) {
        ApiResult apiResult = ApiResult.builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(data)
                .build();

        return apiResult;
    }
}
