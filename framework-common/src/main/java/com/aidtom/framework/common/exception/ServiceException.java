package com.aidtom.framework.common.exception;

import com.aidtom.framework.common.core.IResultCode;

/**
 * 业务类service异常类，所有的业务类抛此异常
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
public class ServiceException extends RuntimeException {
    /**
     * 错误编码
     */
    private IResultCode resultCode;
    /**
     * 错误消息
     */
    private String errorMessage;

    public ServiceException() {
    }

    public ServiceException(IResultCode resultCode, String errorMessage) {
        super(errorMessage);
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
    }

    public ServiceException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.errorMessage = resultCode.getMessage();
    }

    public IResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(IResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
