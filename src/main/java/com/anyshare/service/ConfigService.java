package com.anyshare.service;

import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import com.anyshare.web.dto.weixin.OpenapiConfigDrainageEnableReq;
import com.anyshare.web.dto.weixin.OpenapiConfigUpdateReq;
import com.anyshare.web.dto.weixin.OpenapiReindexSearchContentReq;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.guide.WxMpAddGuideAutoReply;

import java.util.List;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface ConfigService {

    void openapiConfigAdd(OpenapiConfigAddReq req);

    void openapiConfigUpdate(OpenapiConfigUpdateReq req);

    void reindexSearchContent(OpenapiReindexSearchContentReq req);

    void drainageEnable(OpenapiConfigDrainageEnableReq req);

    void drainageCloseForAdmin(String appTag);

    void setGuideConfig(String appTag, String secret, String json) throws WxErrorException;
}
