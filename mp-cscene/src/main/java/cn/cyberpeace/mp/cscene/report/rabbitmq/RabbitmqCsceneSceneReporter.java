package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneBO;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneReporter;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RabbitmqCsceneSceneReporter implements SceneReporter {

    public static final String MESSAGE_TYPE_SCENE = "scene";

    public static final String MESSAGE_TYPE_SCENE_ELEMENT = "scene_element";

    private static final String MESSAGE_TYPE_SCENE_ELEMENT_LOAD_SUCCESS = "load_success";

    @Autowired
    private CsceneSceneReportRabbitmqSender messageSender;

    @Autowired
    private CsceneAppConfig csceneAppConfig;

    @Override
    public void reportScene(CollectedSceneBO scene) {
        var cscene = CollectedCsceneBO.fromCollectedSceneBO(scene);
        cscene.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        cscene.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("scene message, type :{}, content :{}, context:{}", MESSAGE_TYPE_SCENE, JsonCommon.stringify(cscene), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(cscene, MESSAGE_TYPE_SCENE, null, csceneAppConfig.getSceneMessageTimeout());
    }

    @Override
    public void reportElement(CollectedSceneElementBO sceneElement) {
        var csceneElement = CollectedCsceneElementBO.fromCollectedSceneElementBO(sceneElement);
        csceneElement.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        csceneElement.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("scene message, type :{}, content :{}, context:{}", MESSAGE_TYPE_SCENE_ELEMENT, JsonCommon.stringify(csceneElement), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(csceneElement, MESSAGE_TYPE_SCENE_ELEMENT, null, csceneAppConfig.getSceneMessageTimeout());
    }

    @Override
    public void reportLoadSuccess(CollectedSceneElementBO sceneElement, String loadToolId) {
        var csceneElement = CollectedCsceneElementBO.fromCollectedSceneElementBO(sceneElement);
        csceneElement.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        csceneElement.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("load message, type :{}, loadToolId :{}", MESSAGE_TYPE_SCENE_ELEMENT_LOAD_SUCCESS, JsonCommon.stringify(loadToolId), JsonCommon.stringify(TransferContext.getContextMap()));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("load_tool_id", loadToolId);
        jsonObject.put("cscene_id", CsceneContextHolder.getCsceneInstanceId());
        FormatMessage<CollectedSceneElementBO> collectedSceneElementBOFormatMessage = new FormatMessage<>(sceneElement, MESSAGE_TYPE_SCENE_ELEMENT_LOAD_SUCCESS, null);
        Map<String, Object> context = collectedSceneElementBOFormatMessage.getContext();
        messageSender.send(jsonObject, MESSAGE_TYPE_SCENE_ELEMENT_LOAD_SUCCESS, context, csceneAppConfig.getSceneMessageTimeout());
    }
}
