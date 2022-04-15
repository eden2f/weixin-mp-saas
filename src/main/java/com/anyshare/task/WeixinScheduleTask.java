package com.anyshare.task;

import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;
import com.anyshare.service.ConfigService;
import com.anyshare.service.WeixinService;
import com.anyshare.service.common.AppOpenApiConfigService;
import com.anyshare.web.config.WxMpConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
    @Resource
    private ConfigService configService;
    @Resource
    private AppOpenApiConfigService appOpenApiConfigService;

    private static final Map<String, Integer> WRONG_COUNT_MAP = new HashMap<>();

    /**
     * todo 应该区分共享开关和公众号配置可用开关
     * 三次失败就关闭共享功能
     */
    private static final Integer DRAINAGE_CLOSE_MAX_WRONG_COUNT = 3;


    @Async
    @Scheduled(cron = "0 0 4,16 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void materialNewsSynchronismTask() {
        StopWatch sw = new StopWatch("materialNewsSynchronismTask");
        for (String appTag : WxMpConfig.WX_MP_SERVICE_MAP.keySet()) {
            sw.start(String.format("%s materialNewsSynchronismTask", appTag));
            AppOpenApiConfigPO appOpenApiConfig = appOpenApiConfigService.findByAppTag(appTag);
            if (!appOpenApiConfig.drainageEnable()) {
                log.info("{} 已关闭分流跳过", appTag);
                continue;
            }
            boolean noError = true;
            try {
                WxMpService wxMpService = WxMpConfig.getWxMpServiceByAppTag(appTag);
                WxMpMaterialService wxMpMaterialService = wxMpService.getMaterialService();
                weixinService.materialNewsSynchronizer(appTag, wxMpMaterialService);
            } catch (Exception e) {
                log.info(String.format("同步微信物料发生异常, %s", appTag), e);
                Integer wrongCount = WRONG_COUNT_MAP.getOrDefault(appTag, 0);
                if (null == wrongCount || 0 == wrongCount) {
                    WRONG_COUNT_MAP.put(appTag, 1);
                } else {
                    WRONG_COUNT_MAP.put(appTag, wrongCount + 1);
                }
                if (wrongCount != null && wrongCount >= DRAINAGE_CLOSE_MAX_WRONG_COUNT) {
                    configService.drainageCloseForAdmin(appTag);
                }
                noError = false;
            }
            if (noError) {
                WRONG_COUNT_MAP.remove(appTag);
            }
            sw.stop();
        }
        log.info("完成, 耗时:{}s", sw.getTotalTimeSeconds());
    }
}
