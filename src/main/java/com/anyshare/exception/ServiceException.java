package com.anyshare.exception;


import com.anyshare.web.dto.RetCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Eden
 * @date 2021/2/3 10:30
 */
@Getter
@Setter
@ToString
public class ServiceException extends RuntimeException {
    private Integer code;
    private String msg;

    public ServiceException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(String msg) {
        super(msg);
        this.code = RetCode.FAIL.code;
        this.msg = msg;
    }
}
