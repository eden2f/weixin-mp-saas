package com.anyshare.task;

import com.anyshare.service.WeixinService;
import com.anyshare.web.config.WxMpConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * 微信公众号定时任务
 *
 * @author Eden
 * @date 2020/07/25
 */
@Configuration
@EnableScheduling
@Slf4j
public class WeixinScheduleTask {


    @Resource
    private WeixinService weixinService;

    @Async
    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void materialNewsSynchronismTask() throws WxErrorException {
        StopWatch sw = new StopWatch("materialNewsSynchronismTask");
        for (String appTag : WxMpConfig.WX_MP_SERVICE_MAP.keySet()) {
            sw.start(String.format("%s materialNewsSynchronismTask", appTag));
            try {
                WxMpService wxMpService = WxMpConfig.getWxMpServiceByAppTag(appTag);
                WxMpMaterialService wxMpMaterialService = wxMpService.getMaterialService();
                weixinService.materialNewsSynchronism(appTag, wxMpMaterialService);
            } catch (Exception e) {
                log.info(String.format("同步微信物料发生异常, %s", appTag), e);
            }
            sw.stop();
        }
        log.info(" 完成, 耗时:{}s", sw.getTotalTimeSeconds());
    }
}
