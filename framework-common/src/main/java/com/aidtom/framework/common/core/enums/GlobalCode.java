package com.aidtom.framework.common.core.enums;

import com.aidtom.framework.common.core.IResultCode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统级别编码维护
 * <p>
 * 建议：后续各个服务定义异常编码可以规定好【2代表服务，01/02/03代表模块，001/002/003代表具体错误编码】：例如：
 *
 * @author tom
 * @date 2020/11/25 16:25
 */

public enum GlobalCode implements IResultCode {
    /**
     * 成功
     */
    SUCCESS("10000", "请求成功"),

    /**
     * 失败"
     */
    FAILURE("10001", "请求失败，请稍后重试"),

    /**
     * 参数校验失败
     */
    PARAMETER("10002", "参数校验异常"),

    /**
     * 系统异常
     */
    SYSTEM("10003", "系统异常");

    private String code;
    private String message;

    private GlobalCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }


    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return Status
     */
    public static GlobalCode getByCode(String code) {
        for (GlobalCode _enum : values()) {
            if (_enum.getCode().equals(code)) {
                return _enum;
            }
        }
        return null;
    }

    /**
     * 获取全部枚举
     *
     * @return List<Status>
     */
    public static List<GlobalCode> getAllEnum() {
        List<GlobalCode> list = new java.util.ArrayList<GlobalCode>(values().length);
        for (GlobalCode _enum : values()) {
            list.add(_enum);
        }
        return list;
    }

    /**
     * 获取全部枚举值
     *
     * @return List<String>
     */
    public static List<String> getAllEnumCode() {
        List<String> list = new java.util.ArrayList<>(values().length);
        for (GlobalCode _enum : values()) {
            list.add(_enum.getCode());
        }
        return list;
    }

    /**
     * 通过code获取msg
     *
     * @param code 枚举值
     * @return
     */
    public static String getDescByCode(String code) {
        if (code == null) {
            return null;
        }
        GlobalCode _enum = getByCode(code);
        if (_enum == null) {
            return null;
        }
        return _enum.getMessage();
    }

    public static Map<String, String> mapping() {
        Map<String, String> map = new LinkedHashMap<>();
        for (GlobalCode type : values()) {
            map.put(type.getCode(), type.getMessage());
        }
        return map;
    }
}
