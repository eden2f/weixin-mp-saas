package com.anyshare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Optional;

/**
 * 数据隔离标识
 *
 * @author Eden
 * @date 2021/2/3 10:30
 */
@Getter
@AllArgsConstructor
@ToString
public enum AppTag {

    /**
     * 单元测试用
     */
    Test("test"),

    AnyShare("anyShare"),

    Daily("daily");

    private final String code;

    public static final String ANY_SHARE_CODE = "anyShare";
    public static final String DAILY_CODE = "daily";

    public static AppTag getByCode(String code) {
        Optional<AppTag> appTagOptional = Arrays.stream(values()).filter(item -> item.getCode().equals(code)).findFirst();
        return appTagOptional.orElse(null);
    }
}
