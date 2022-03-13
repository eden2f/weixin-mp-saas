package com.anyshare.service;

import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import com.anyshare.web.dto.weixin.OpenapiConfigDrainageEnableReq;
import com.anyshare.web.dto.weixin.OpenapiConfigUpdateReq;
import com.anyshare.web.dto.weixin.OpenapiReindexSearchContentReq;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface ConfigService {

    void openapiConfigAdd(OpenapiConfigAddReq req);

    void openapiConfigUpdate(OpenapiConfigUpdateReq req);

    void reindexSearchContent(OpenapiReindexSearchContentReq req);

    void drainageEnable(OpenapiConfigDrainageEnableReq req);
}
