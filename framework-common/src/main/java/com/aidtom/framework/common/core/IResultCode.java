package com.aidtom.framework.common.core;

/**
 * 系统异常码枚举基础接口
 * 各个子模块中定义的异常码枚举需实现该接口
 *
 * @author tanghaihua
 * @date 2020/11/25 16:25
 */

public interface IResultCode {
    /**
     * 获取状态码
     */
    String getCode();

    /**
     * 获取返回信息
     */
    String getMessage();
}
