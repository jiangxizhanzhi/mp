package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.cscene.constant.CsceneConstant;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Map;


public class SceneStatusUtil {
    private static Map<Integer, String> en;
    private static Map<Integer, String> zh;

    public static SceneStatus getSceneStatus(Boolean deleted, Integer event, Integer eventStatus) {
        if (en == null || zh == null) {
            en = new HashMap<>();
            zh = new HashMap<>();
            var optionService = (OptionService) BeanCommon.getBean("sysOptionServiceImpl");
            var optionModels = optionService.findByType(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_STATUS);
            optionModels.forEach(optionModel -> {
                Integer status = Integer.parseInt(optionModel.getCode());
                en.put(status, optionModel.getEnName());
                zh.put(status, optionModel.getZhName());
            });
            
            en.put(CsceneInstanceStatQueryParams.SceneStatus.DELETING, "Deleting");
            zh.put(CsceneInstanceStatQueryParams.SceneStatus.DELETING, "正在删除");
        }
        Integer sceneStatus;
        
        if (Boolean.TRUE.equals(deleted) && SceneModel.Event.DELETE != event) {
            sceneStatus = CsceneInstanceStatQueryParams.SceneStatus.DELETING;
        } else {
            sceneStatus = eventStatus2SceneStatus(event, eventStatus);
        }
        String sceneStatusDesc;
        if (LocaleContextHolder.getLocale().getLanguage().equals("zh")) {
            sceneStatusDesc = zh.get(sceneStatus);
        } else {
            sceneStatusDesc = en.get(sceneStatus);
        }
        return new SceneStatus(sceneStatus, sceneStatusDesc);
    }

    
    private static Integer eventStatus2SceneStatus(Integer event, Integer eventStatus) {
        if (EventStatus.ABNORMAL == eventStatus) {
            return CsceneInstanceStatQueryParams.SceneStatus.ERROR;
        }
        if (SceneModel.Event.INITIATE == event) {
            return CsceneInstanceStatQueryParams.SceneStatus.CREATING;
        }
        if ((SceneModel.Event.CREATE == event && (EventStatus.START == eventStatus || EventStatus.IN_PROGRESS == eventStatus))) {
            return CsceneInstanceStatQueryParams.SceneStatus.CREATING;
        }
        if (SceneModel.Event.DEFAULT == event) {
            return CsceneInstanceStatQueryParams.SceneStatus.QUEUE;
        }
        if (SceneModel.Event.CREATE == event && EventStatus.OVER == eventStatus) {
            return CsceneInstanceStatQueryParams.SceneStatus.RUNNING;
        }
        if (SceneModel.Event.UNPAUSE == event && EventStatus.OVER == eventStatus) {
            return CsceneInstanceStatQueryParams.SceneStatus.RUNNING;
        }
        if (SceneModel.Event.RESUME == event && EventStatus.OVER == eventStatus) {
            return CsceneInstanceStatQueryParams.SceneStatus.RUNNING;
        }
        if (SceneModel.Event.START == event || SceneModel.Event.RESTART == event || SceneModel.Event.SNAPSHOT == event ||
                SceneModel.Event.MODIFY == event || SceneModel.Event.STOP == event) {
            return CsceneInstanceStatQueryParams.SceneStatus.RUNNING;
        }
        if (SceneModel.Event.PAUSE == event) {
            return CsceneInstanceStatQueryParams.SceneStatus.PAUSE;
        }
        if (SceneModel.Event.UNPAUSE == event && (EventStatus.START == eventStatus || EventStatus.IN_PROGRESS == eventStatus)) {
            return CsceneInstanceStatQueryParams.SceneStatus.PAUSE;
        }
        if (SceneModel.Event.SUSPEND == event) {
            return CsceneInstanceStatQueryParams.SceneStatus.SUSPEND;
        }
        if (SceneModel.Event.RESUME == event && (EventStatus.START == eventStatus || EventStatus.IN_PROGRESS == eventStatus)) {
            return CsceneInstanceStatQueryParams.SceneStatus.SUSPEND;
        }
        if (SceneModel.Event.DELETE == event) {
            return CsceneInstanceStatQueryParams.SceneStatus.DELETED;
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class SceneStatus {
        private Integer sceneStatus;
        private String sceneStatusDesc;
    }
}
