package com.anyshare.web.controller.nonweixin;

import com.anyshare.service.ConfigService;
import com.anyshare.web.dto.RetResult;
import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import com.anyshare.web.dto.weixin.OpenapiConfigUpdateReq;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author Eden
 * @date 2020/07/25
 */
@RestController
@Slf4j
@RequestMapping("config")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @PostMapping(value = "weixin/openapi/add")
    public RetResult<Void> openapiConfigAdd(@Valid @RequestBody OpenapiConfigAddReq req) throws WxErrorException {
        configService.openapiConfigAdd(req);
        return RetResult.success();
    }

    @PostMapping(value = "weixin/openapi/update")
    public RetResult<Void> openapiConfigUpdate(@Valid @RequestBody OpenapiConfigUpdateReq req) throws WxErrorException {
        configService.openapiConfigUpdate(req);
        return RetResult.success();
    }
}
