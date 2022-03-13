package com.anyshare.web.dto.weixin;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Eden
 * @date 2021/2/5 10:35
 */
@Data
public class OpenapiConfigAddReq {

    @NotBlank(message = "appTag 不能为空")
    private String appTag;

    @NotBlank(message = "appId 不能为空")
    private String appId;

    @NotBlank(message = "secret 不能为空")
    private String secret;

    @NotBlank(message = "token 不能为空")
    private String token;

    @NotBlank(message = "aesKey 不能为空")
    private String aesKey;

    private String verifyKey = "";

    private String verifyValue = "";
}
