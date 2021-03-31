package com.anyshare.web.dto.weixin;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Eden
 * @date 2021/2/5 10:35
 */
@Data
public class OpenapiConfigUpdateReq {

    @NotBlank(message = "appTag 不能为空")
    private String appTag;

    @NotBlank(message = "appid 不能为空")
    private String appId;

    @NotBlank(message = "secret 不能为空")
    private String secret;

    @NotBlank(message = "token 不能为空")
    private String token;

    @NotBlank(message = "aesKey 不能为空")
    private String aesKey;

    private String verifyKey = "";

    private String verifyValue = "";

    @Valid
    @NotNull(message = "当前的配置(用于验证)")
    private openapiConfigVerify openapiConfigVerify;


    @Data
    public static class openapiConfigVerify {

        @NotBlank(message = "secret 不能为空")
        private String secret;
    }
}
