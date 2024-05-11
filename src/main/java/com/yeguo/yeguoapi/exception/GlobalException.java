package com.yeguo.yeguoapi.exception;

import com.yeguo.yeguoapi.common.Result;
import com.yeguo.yeguoapi.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author yeguo
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(BusinessException.class)
    public <T> Result<T> businessExceptionHandler(BusinessException e) {
        log.error("业务异常-->{}:{}-----{}",e.getClass(),e.getMessage(),e.getDescription());
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public <T> Result<T> runtimeExceptionHandler(RuntimeException e) {
        log.error("运行时异常-->{}:{}",e.getClass(),e.getMessage());
        return ResultUtils.error(e.getMessage());
    }
}
