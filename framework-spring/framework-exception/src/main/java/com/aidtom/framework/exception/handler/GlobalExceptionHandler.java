package com.aidtom.framework.exception.handler;

import com.aidtom.framework.common.core.ApiResult;
import com.aidtom.framework.common.core.enums.GlobalCode;
import com.aidtom.framework.common.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
@ControllerAdvice
public class GlobalExceptionHandler<T> {

    /**
     * 拦截业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResult<T> notFount(ServiceException e) {
        return ApiResult.fail(e.getResultCode(), e.getErrorMessage());
    }

    /**
     * 拦截请求参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResult<T> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ApiResult.fail(GlobalCode.PARAMETER, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 拦截请求参数异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResult<T> bindException(BindException e) {
        return ApiResult.fail(GlobalCode.PARAMETER, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResult<T> serverError(Exception e) {
        return ApiResult.fail(GlobalCode.SYSTEM, GlobalCode.SYSTEM.getMessage());
    }
}
