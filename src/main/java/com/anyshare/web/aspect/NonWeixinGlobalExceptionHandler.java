package com.anyshare.web.aspect;

import com.anyshare.exception.ServiceException;
import com.anyshare.web.dto.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 该注解定义全局异常处理类
 *
 * @author Eden
 * @date 2020/07/19
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.anyshare.web.controller.nonweixin"})
public class NonWeixinGlobalExceptionHandler {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public RetResult<String> exceptionHandler(MethodArgumentNotValidException e) {
        log.info(String.format("参数错误, e = %s", e), e);
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        String errorMessageStr = StringUtils.join(errorMessages, ",");
        return RetResult.fail(errorMessageStr);
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public RetResult<String> defaultErrorHandler(ServiceException se) {
        log.info(String.format("发生业务异常, e = %s", se), se);
        return RetResult.fail(se.getMsg());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RetResult<String> defaultErrorHandler(Exception e) {
        log.info(String.format("发生未知异常, e = %s", e), e);
        return RetResult.fail("发生未知异常");
    }


}
