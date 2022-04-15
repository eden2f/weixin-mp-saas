package com.anyshare.web.controller.nonweixin;

import com.anyshare.service.ConfigService;
import com.anyshare.web.dto.RetResult;
import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import com.anyshare.web.dto.weixin.OpenapiConfigDrainageEnableReq;
import com.anyshare.web.dto.weixin.OpenapiConfigUpdateReq;
import com.anyshare.web.dto.weixin.OpenapiReindexSearchContentReq;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

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
    public RetResult<Void> openapiConfigAdd(@Valid @RequestBody OpenapiConfigAddReq req) {
        configService.openapiConfigAdd(req);
        return RetResult.success();
    }

    @PostMapping(value = "weixin/openapi/update")
    public RetResult<Void> openapiConfigUpdate(@Valid @RequestBody OpenapiConfigUpdateReq req) {
        configService.openapiConfigUpdate(req);
        return RetResult.success();
    }


    @PostMapping(value = "weixin/reindex/Search")
    public RetResult<Void> reindexSearchContent(@Valid @RequestBody OpenapiReindexSearchContentReq req) {
        configService.reindexSearchContent(req);
        return RetResult.success();
    }

    /**
     * 公众号引流能力开关
     */
    @PostMapping(value = "weixin/drainage/enable")
    public RetResult<Void> dlaorainageEnable(@Valid @RequestBody OpenapiConfigDrainageEnableReq req) {
        configService.drainageEnable(req);
        return RetResult.success();
    }

    /**
     * 设置快捷回复与关注自动回复
     */
    @PostMapping(value = "weixin/guide/setguideconfig")
    public RetResult<Void> setGuideConfig(@RequestParam String appTag,
                                          @RequestParam String secret,
                                          @RequestParam String json) throws WxErrorException {
        configService.setGuideConfig(appTag, secret, json);
        return RetResult.success();
    }
}
