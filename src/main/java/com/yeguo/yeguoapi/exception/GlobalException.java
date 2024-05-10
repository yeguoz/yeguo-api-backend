package com.yeguo.yeguoapi.exception;



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

//    @ExceptionHandler(BusinessException.class)
//    public Result businessExceptionHandler(BusinessException e) {
//
//        return null;
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public Result runtimeExceptionHandler(RuntimeException e) {
//
//        return null;
//    }
}
