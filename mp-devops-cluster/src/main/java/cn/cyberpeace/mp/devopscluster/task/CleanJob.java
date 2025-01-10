package cn.cyberpeace.mp.devopscluster.task;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.ScreenshotMapper;
import cn.cyberpeace.mp.cscene.response.ScreenshotDeleteConfigResponse;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScreenrecordService;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.ImagesDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CleanJob implements SchedulingConfigurer {

    public static String SCREENSHOT_CONFIG = "screenshot_del_conf";

    public static String IMAGE_DEL_CONFIG = "image_del_config";

    public static String SCENE_DEL_CONFIG = "scene_del_config";

    private static final String CRON_REGEX = "^(\\*|([0-5]?[0-9])) (\\*|([0-5]?[0-9])) (\\*|([0-1]?[0-9]|2[0-3])) (\\*|([1-9]|1[0-9]|2[0-9]|3[0-1])) (\\*|([1-9]|1[0-2])) (\\*|([0-6]))(\\s\\*|\\s([0-1]?[0-9]|2[0-3]))?$";


    // 默认一小时执行一次
    public static String DEFAULT_CRON = "0 0 * * * ?";


    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private ScreenshotMapper screenshotMapper;

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    @Resource
    private ImagesConverter imagesConverter;

    @Resource
    private CsceneMapper csceneMapper ;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        log.info("------定时任务开始执行------");

        // 清除靶标
        SysConfigModel imagesSwitch = getSysConfig(IMAGE_DEL_CONFIG);
        String value = imagesSwitch.getValue();
        if (StringUtils.isNotEmpty(value) && JsonCommon.parse(value, ImageDelConfig.class).getEnable()) {
            ImageDelConfig parse = JsonCommon.parse(value, ImageDelConfig.class);
            taskRegistrar.addTriggerTask(
                    () -> {
                        log.info("------清除靶标---定时任务开始执行------");
                        this.doClearImages(imagesSwitch);
                    },
                    triggerContext -> {
                        DEFAULT_CRON = !isValidCron(parse.getCron()) ? DEFAULT_CRON : parse.getCron();
                        //任务触发，可修改任务的执行周期
                        try {
                            CronTrigger trigger = new CronTrigger(DEFAULT_CRON);
                            return trigger.nextExecutionTime(triggerContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            );
            log.info("------清除靶标---定时任务执行结束------");
        }

        // 清除拓扑模板
        SysConfigModel sceneSwitch = getSysConfig(SCENE_DEL_CONFIG);
        String value1 = imagesSwitch.getValue();
        if (StringUtils.isNotEmpty(value1) && JsonCommon.parse(value1, SceneDelConfig.class).getEnable()) {
            SceneDelConfig parse = JsonCommon.parse(value1, SceneDelConfig.class);
            taskRegistrar.addTriggerTask(
                    () -> {
                        log.info("------清除拓扑模板---定时任务开始执行------");
                        this.doClearSceneConfig(sceneSwitch);
                    },
                    triggerContext -> {
                        DEFAULT_CRON = !isValidCron(parse.getCron()) ? DEFAULT_CRON : parse.getCron();
                        //任务触发，可修改任务的执行周期
                        try {
                            CronTrigger trigger = new CronTrigger(DEFAULT_CRON);
                            return trigger.nextExecutionTime(triggerContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;

                    }
            );
            log.info("------清除拓扑模板---定时任务执行结束------");
        }

        // 清除截图文件
        SysConfigModel screenshotSwitch = getSysConfig(SCREENSHOT_CONFIG);
        String value3 = imagesSwitch.getValue();
        if (StringUtils.isNotEmpty(value3) && JsonCommon.parse(value3, ScreenshotDeleteConfigResponse.class).getEnable()) {
            ScreenshotDeleteConfigResponse parse = JsonCommon.parse(value3, ScreenshotDeleteConfigResponse.class);
            taskRegistrar.addTriggerTask(
                    () -> {
                        log.info("------清除截图文件---定时任务开始执行------");
                        this.doClearScreenshot(screenshotSwitch);
                    },
                    triggerContext -> {
                        DEFAULT_CRON = !isValidCron(parse.getCron()) ? DEFAULT_CRON : parse.getCron();
                        //任务触发，可修改任务的执行周期
                        try {
                            CronTrigger trigger = new CronTrigger(DEFAULT_CRON);
                            return trigger.nextExecutionTime(triggerContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            );
            log.info("------清除截图文件---定时任务执行结束------");
        }
        log.info("------定时任务执行结束------");
    }

    /**
     * 校验cron表达式是否合法
     * @param cron
     * @return
     */
    public static boolean isValidCron(String cron) {
        if (StringUtils.isEmpty(cron)) {
            return false;
        }
        Pattern pattern = Pattern.compile(CRON_REGEX);
        return pattern.matcher(cron).matches();
    }

    //    @Scheduled(cron = "0 0/1 * * * ?")
//    public void execute() {
//        // 清除截屏
//        try {
//            this.doClearScreenshot();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 清除靶标
//        try {
//            this.doClearImages();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 清除拓扑模板
//        try {
//            this.doClearSceneConfig();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void doClearScreenshot(SysConfigModel screenshotSwitch) {
        ScreenshotDeleteConfigResponse parse = JsonCommon.parse(screenshotSwitch.getValue(), ScreenshotDeleteConfigResponse.class);
//        var model = sysConfigService.getByKeyName(SCREENSHOT_CONFIG);
//        if (model == null) {
//            return;
//        }
//        Integer months = Integer.parseInt(model.getValue());
        LocalDateTime time = LocalDateTime.now().minusMonths(Long.parseLong(String.valueOf(parse.getMonths())));
        var cond = new HumanCondition().filterLessThan(ScreenshotModel::getCreateTime, time);
        screenshotMapper.filterDelete(cond);
    }

    private void doClearImages(SysConfigModel model) {
//        var model = sysConfigService.getByKeyName(IMAGE_DEL_CONFIG);
        if (model == null) {
            return;
        }
        var delConfig = JsonCommon.parse(model.getValue(), ImageDelConfig.class);
        var createTime = LocalDateTime.now().minusDays(30);
        var cond = new HumanCondition()
                .filterLessThan(DeviceTargetPO::getCreateTime, createTime)
                .filterOrStart()
                .orEqual(DeviceTargetPO::getReferenceNums, delConfig.getReferenceNums())
                .orEqual(DeviceTargetPO::getUseDuration, delConfig.getUseDuration())
                .filterOrEnd();
        var list = deviceTargetMapper.list(cond);
        if (CollectionUtils.isNotEmpty(list)) {
            for (var m : list) {
                ImagesDeleteRequest request = new ImagesDeleteRequest();
                request.setImageIds(List.of(m.getUuid()));
                try {
                    imagesConverter.delete(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doClearSceneConfig(SysConfigModel model) {
        SceneDelConfig parse = JsonCommon.parse(model.getValue(), SceneDelConfig.class);
        var cscenes = csceneMapper.list(new HumanCondition()
                .filterEqual(CsceneModel::getType, CsceneModel.Type.TEMPLATE)
                .filterEqual(CsceneModel::getDeleted, false)
                .filterLessThan(CsceneModel::getCreateTime, LocalDateTime.now().minusMonths(1))
        );
        if (CollectionUtils.isEmpty(cscenes)) {
            return;
        }
        for (var cscene : cscenes) {
            CsceneHandler csceneHandler = new CsceneHandler(cscene.getId(), null);
            if (csceneHandler.getCscene().getSceneConfig() == null
                    || csceneHandler.getCscene().getSceneConfig().getTopology().getElements().size() <= parse.getNodeNum()) {
                csceneHandler.delete(true);
            }
        }
    }

    private SysConfigModel getSysConfig(String keyName) {
        return sysConfigService.getByKeyName(keyName);
    }

    @Getter
    @Setter
    public static class ImageDelConfig {
        private Integer referenceNums = 0;
        private Integer useDuration = 0;
        @ApiModelProperty(value = "是否开启")
        private Boolean enable = false;
        @ApiModelProperty(value = "定时任务执行时间")
        private String cron = "0 0 * * * ?";
    }

    @Getter
    @Setter
    public static class SceneDelConfig {
        private Integer nodeNum = 0;
        @ApiModelProperty(value = "是否开启")
        private Boolean enable = false;
        @ApiModelProperty(value = "定时任务执行时间")
        private String cron = "0 0 * * * ?";
    }

}