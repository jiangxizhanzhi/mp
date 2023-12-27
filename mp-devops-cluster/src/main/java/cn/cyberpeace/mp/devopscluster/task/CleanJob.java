package cn.cyberpeace.mp.devopscluster.task;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.ScreenshotMapper;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.ImagesDeleteRequest;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanJob {

    public static String SCREENSHOT_CONFIG = "screenshot_del_conf";

    public static String IMAGE_DEL_CONFIG = "image_del_config";

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private ScreenshotMapper screenshotMapper;

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    @Resource
    private ImagesConverter imagesConverter;

    //    @Scheduled(cron = "0 0/1 * * * ?")
    public void execute() {
        // 清除截屏
        try {
            this.doClearScreenshot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 清除靶标
        try {
            this.doClearImages();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 清除拓扑模板
        try {
            this.doClearSceneConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doClearScreenshot() {
        var model = sysConfigService.getByKeyName(SCREENSHOT_CONFIG);
        if (model == null) {
            return;
        }
        Integer months = Integer.parseInt(model.getValue());
        LocalDateTime time = LocalDateTime.now().minusMonths(months);
        var cond = new HumanCondition().filterLessThan(ScreenshotModel::getCreateTime, time);
        screenshotMapper.filterDelete(cond);
    }

    private void doClearImages() {
        var model = sysConfigService.getByKeyName(IMAGE_DEL_CONFIG);
        if (model == null) {
            return;
        }
        var delConfig = JsonCommon.parse(model.getValue(), ImageDelConfig.class);
        var createTime = LocalDateTime.now().minusDays(30);
        var cond = new HumanCondition()
                .filterLessThan(DeviceTargetPO::getCreateTime, createTime)
                .filterOrStart()
                .orLessThan(DeviceTargetPO::getReferenceNums, delConfig.getReferenceNums())
                .orLessThan(DeviceTargetPO::getUseDuration, delConfig.getUseDuration())
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

    private void doClearSceneConfig() {
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
                    || csceneHandler.getCscene().getSceneConfig().getTopology().getElements().size() == 0) {
                csceneHandler.delete(true);
            }
        }
    }

    @Getter
    @Setter
    public static class ImageDelConfig {
        private Integer referenceNums;
        private Integer useDuration;
    }
}