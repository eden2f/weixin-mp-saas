package com.anyshare.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Slf4j
@ControllerAdvice(basePackages = "com.anyshare.web.controller.weixin")
public class WeixinGlobalExceptionHandler {

    /**
     * 处理 Exception 异常
     */
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception e) {
        log.info("发生未知异常, e = " + e, e);
        return "发生未知异常";
    }
}
