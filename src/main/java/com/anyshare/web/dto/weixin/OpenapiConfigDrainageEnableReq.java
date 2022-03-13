package com.anyshare.web.dto.weixin;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Eden
 * @date 2021/2/5 10:35
 */
@Data
public class OpenapiConfigDrainageEnableReq {

    @NotBlank(message = "appTag 不能为空")
    private String appTag;

    @NotNull(message = "开关 trye / false")
    private boolean enable;

    @NotBlank(message = "secret 不能为空,用于认证")
    private String secret;
}
