package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.websocket.SceneTestInstanceProcessHandler;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneBO;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneReporter;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqCsceneSceneReporter implements SceneReporter {

    public static final String MESSAGE_TYPE_SCENE = "scene";

    public static final String MESSAGE_TYPE_SCENE_ELEMENT = "scene_element";

    @Autowired
    private CsceneSceneReportRabbitmqSender messageSender;

    @Autowired
    private CsceneAppConfig csceneAppConfig;

    @Autowired
    private SceneTestInstanceProcessHandler sceneTestInstanceProcessHandler;

    @Override
    public void reportScene(CollectedSceneBO scene) {
        var cscene = CollectedCsceneBO.fromCollectedSceneBO(scene);
        cscene.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        cscene.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("scene message, type :{}, content :{}, context:{}", MESSAGE_TYPE_SCENE, JsonCommon.stringify(cscene), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(cscene, MESSAGE_TYPE_SCENE, null, csceneAppConfig.getSceneMessageTimeout());

        if (scene.getScene().getMultiSceneType() == BaseSceneModel.MultiSceneType.MULTI_SCENE) {
            sceneTestInstanceProcessHandler.pushCsceneInstanceSockets(cscene.getCsceneId(), new ServiceResult<>(new FormatMessage<>(cscene, MESSAGE_TYPE_SCENE, null)).asResult());
        }
    }

    @Override
    public void reportElement(CollectedSceneElementBO sceneElement) {
        var csceneElement = CollectedCsceneElementBO.fromCollectedSceneElementBO(sceneElement);
        csceneElement.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        csceneElement.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("scene message, type :{}, content :{}, context:{}", MESSAGE_TYPE_SCENE_ELEMENT, JsonCommon.stringify(csceneElement), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(csceneElement, MESSAGE_TYPE_SCENE_ELEMENT, null, csceneAppConfig.getSceneMessageTimeout());
        sceneTestInstanceProcessHandler.pushCsceneInstanceSockets(csceneElement.getCsceneId(), new ServiceResult<>(new FormatMessage<>(csceneElement, MESSAGE_TYPE_SCENE, null)).asResult());

    }
}
