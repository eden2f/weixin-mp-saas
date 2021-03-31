package com.anyshare.web.controller.weixin;

import com.anyshare.service.WeixinService;
import com.anyshare.web.config.WxMpConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is Description
 *
 * @author Eden
 * @date 2020/07/19
 */
@Controller
@Slf4j
@RequestMapping("weixin")
public class WeixinController {

    @Resource
    private WeixinService weixinService;


    @ResponseBody
    @RequestMapping(value = "/{appTag}/{verifyKey}")
    public String weixinMpVerify(@PathVariable(value = "appTag") String appTag,
                                 @PathVariable(value = "verifyKey") String verifyKey) {
        return WxMpConfig.getWxMpVerifyKey(appTag, verifyKey);
    }

    @RequestMapping(value = "/{appTag}")
    public void weixinMpRequest(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "appTag") String appTag) throws IOException {
        bypass(request, response, appTag);
    }


    private void bypass(HttpServletRequest request, HttpServletResponse response, String appTag) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        WxMpService wxMpService = WxMpConfig.getWxMpServiceByAppTag(appTag);

        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            response.getWriter().println("非法请求");
            return;
        }

        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            response.getWriter().println(echostr);
            return;
        }

        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" :
                request.getParameter("encrypt_type");


        if ("raw".equals(encryptType)) {
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            WxMpXmlOutMessage outMessage = weixinService.handle(appTag, inMessage);
            response.getWriter().write(outMessage.toXml());
            return;
        }

        if ("aes".equals(encryptType)) {
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), WxMpConfig.getWxMpConfigStorageByAppTag(appTag), timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = weixinService.handle(appTag, inMessage);
            response.getWriter().write(outMessage.toXml());
            return;
        }

        response.getWriter().println("不可识别的加密类型");
        return;
    }

}
