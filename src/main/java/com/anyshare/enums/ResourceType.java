package com.anyshare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : Eden
 * @date : 2021/4/5
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResourceType {

    /**
     * 资源类型(0: 测试; 1: 微信公众号图文; 2: 分享内容)
     */
    TEST(0),

    WEIXIN_ARTICLE(0),

    SHARE_RESOURCE(1);

    private final int code;
}
