package icu.yeguo.yeguoapiinterface.exception;

import icu.yeguo.yeguoapiinterface.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.ConnectException;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(ConnectException.class)
    public Response<Object> handleConnectException(ConnectException ex) {
        log.info("全局异常处理器：{}",ex.getMessage());
        Response<Object> resp = new Response<>();
        resp.setCode(500);
        resp.setResult(null);
        resp.setMsg(ex.getMessage());
        return resp;
    }

}
