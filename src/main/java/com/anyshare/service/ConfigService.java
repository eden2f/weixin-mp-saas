package com.anyshare.service;

import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface ConfigService {

    void openapiConfigAdd(OpenapiConfigAddReq req) throws WxErrorException;
}
