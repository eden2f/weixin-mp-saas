package com.anyshare.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.MDC;

import static com.anyshare.web.aspect.TraceIdFilter.TRACE_ID;


/**
 * This is Description
 *
 * @author Eden
 * @date 2020/07/19
 */
@Data
@AllArgsConstructor
public class RetResult<T> {

    private Integer code;

    private String msg;

    private T data;

    private String traceId;

    public RetResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.traceId = MDC.get(TRACE_ID);
    }


    public static <T> RetResult<T> success(T t) {
        return new RetResult<>(RetCode.SUCCESS.getCode(), "ok", t);
    }


    public static <T> RetResult<T> success() {
        return new RetResult<>(RetCode.SUCCESS.getCode(), "ok", null);
    }


    public static <T> RetResult<T> fail() {
        return new RetResult<>(RetCode.FAIL.getCode(), "fail", null);
    }

    public static <T> RetResult<T> fail(String msg) {
        return new RetResult<>(RetCode.FAIL.getCode(), msg, null);
    }


    public static <T> RetResult<T> fail(Integer code, String msg) {
        return new RetResult<>(code, msg, null);
    }


}
