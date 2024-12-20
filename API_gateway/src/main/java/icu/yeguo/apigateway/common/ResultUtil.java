package icu.yeguo.apigateway.common;


public class ResultUtil {
    public static  Result<Object> error (int code,String message){
        return Result.builder().code(code).msg(message).build();
    }
}
