package com.anyshare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Getter
@AllArgsConstructor
public enum DelStatus {

    VALID(0),

    DELETED(1);

    private int code;
}
