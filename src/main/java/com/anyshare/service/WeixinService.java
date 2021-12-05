package com.anyshare.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface WeixinService {

    WxMpXmlOutMessage handle(String appTag, WxMpXmlMessage inMessage);

    void materialNewsSynchronizer(String appTag, WxMpMaterialService wxMpMaterialService) throws WxErrorException;

    void reindexEsContent(String appTag);
}
