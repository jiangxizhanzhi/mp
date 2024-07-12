 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RabbitmqCsceneSceneReporter.java.
 // File size 2683.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneBO;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName RabbitmqCsceneSceneReporter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SceneReporter
 */
@Slf4j
@Component
public class RabbitmqCsceneSceneReporter implements SceneReporter {

    public static final String MESSAGE_TYPE_SCENE = "scene";

    public static final String MESSAGE_TYPE_SCENE_ELEMENT = "scene_element";

    @Autowired
    private CsceneSceneReportRabbitmqSender messageSender;

    @Autowired
    private CsceneAppConfig csceneAppConfig;

    /**
     * reportScene
     *
     * @param scene
     */
    @Override
    public void reportScene(CollectedSceneBO scene) {
        CollectedCsceneBO cscene = CollectedCsceneBO.fromCollectedSceneBO(scene);
        cscene.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        cscene.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("scene message, type :{}, content :{}, context:{}", MESSAGE_TYPE_SCENE, JsonCommon.stringify(cscene), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(cscene, MESSAGE_TYPE_SCENE, null, csceneAppConfig.getSceneMessageTimeout());
    }

    /**
     * reportElement
     *
     * @param sceneElement
     */
    @Override
    public void reportElement(CollectedSceneElementBO sceneElement) {
        CollectedCsceneElementBO csceneElement = CollectedCsceneElementBO.fromCollectedSceneElementBO(sceneElement);
        csceneElement.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        csceneElement.setCallAction(CsceneContextHolder.getCallAction());
        log.debug("scene message, type :{}, content :{}, context:{}", MESSAGE_TYPE_SCENE_ELEMENT, JsonCommon.stringify(csceneElement), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(csceneElement, MESSAGE_TYPE_SCENE_ELEMENT, null, csceneAppConfig.getSceneMessageTimeout());
    }
}
