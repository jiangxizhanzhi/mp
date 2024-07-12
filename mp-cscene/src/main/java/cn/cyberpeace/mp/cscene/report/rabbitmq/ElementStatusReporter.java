 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementStatusReporter.java.
 // File size 1248.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.scene.core.bo.CollectedElementStatusBO;
import cn.cyberpeace.mp.scene.core.scene.StatusReporter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName ElementStatusReporter
 * @Description
 * @Author maojun
 * @Date 2022/3/21
 * @Version 1.0.0
 * @see StatusReporter
 */
@Component("elementStatusReporter")
public class ElementStatusReporter implements StatusReporter<CollectedElementStatusBO> {

    public static final String MESSAGE_TYPE_SCENE_ELEMENT = "element_status_change";

    @Resource
    private CsceneSceneReportRabbitmqSender messageSender;

    @Resource
    private CsceneAppConfig csceneAppConfig;

    /**
     * reportStatus
     *
     * @param sceneStatus
     */
    @Override
    public void reportStatus(CollectedElementStatusBO sceneStatus) {
        sceneStatus.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        messageSender.send(sceneStatus, MESSAGE_TYPE_SCENE_ELEMENT, null, csceneAppConfig.getSceneMessageTimeout());
    }
}
