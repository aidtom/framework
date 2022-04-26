package com.aidtom.framework.exception.aspect;

import com.aidtom.framework.common.core.ApiResult;
import com.aidtom.framework.common.core.enums.GlobalCode;
import com.aidtom.framework.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 参数校验+日志输入输出
 *
 * @author tanghaihua
 * @date 2020/11/25 17:35
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class ControllerAspect {
    /**
     * 出入参切入点，注意，这里得根据业务包路径来设定
     */
    @Pointcut("execution(* com.aidtom.framework.*.*.controller..*.*(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public ApiResult doBaseReq(ProceedingJoinPoint joinPoint) throws Throwable {
        return doAround(joinPoint);
    }

    /**
     * @Description: controller 统一参数校验, 返回结果封装
     * 打印入口出口
     * @Param: [joinPoint, request]
     **/
    private ApiResult doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ApiResult res = null;
        boolean finished = false;

        ApiResult result = null;
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        try {
            result = (ApiResult) joinPoint.proceed();
            // response
            log.debug("request end[类名:{}-方法名:{}],出参:{}", className, methodName, result);
        } catch (ServiceException e) {
            log.error("request start[类名:{}-方法名:{}],入参:{}", className, methodName, joinPoint.getArgs());
            res = ApiResult.fail(e.getResultCode(), e.getErrorMessage());
            finished = true;
        } catch (Exception e) {
            log.error("request start[类名:{}-方法名:{}],入参:{}", className, methodName, joinPoint.getArgs());
            res = ApiResult.fail(GlobalCode.SYSTEM);
            finished = true;
        }
        if (!finished) {
            res = result;
        }
        return res;
    }
}
