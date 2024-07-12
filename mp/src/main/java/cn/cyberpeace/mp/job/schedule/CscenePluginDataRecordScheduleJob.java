 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CscenePluginDataRecordScheduleJob.java.
 // File size 7770.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneDataMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CscenePluginDataRecordScheduleJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
@Data
public class CscenePluginDataRecordScheduleJob {

    @Resource
    private CollectionPluginSceneDataMapper collectionPluginSceneDataMapper;

    @Resource
    private CsceneService csceneService;

    @Resource
    private ScenePluginCollectionSceneService collectionSceneService;

    @Autowired
    private LogFileService logFileService;

    @MpScheduled(cron = "0 0/2 * * * ?", description = "定时获取主机端口网络连接等信息并记录到表,给回放使用", id = "record_scene_host_net_and_port", initialDelaySeconds = 120)
    public void action() {
        List<CsceneInstanceModel> instances = csceneService.getRunningCscenes();
        for (CsceneInstanceModel instance : instances) {
            if (instance.getReferenceType() == null || (BaseCsceneModel.ReferenceType.READ_VS_BLUE != instance.getReferenceType() && BaseCsceneModel.ReferenceType.TEAM_TRAINING != instance.getReferenceType())) {
                continue;
            }
            try {
                SceneHandler sceneHandler = new SceneHandler(instance.getScene(), null, null);
                CollectionScenePluginHandler collectPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                if (collectPlugin == null) {
                    continue;
                }
                List<CollectionPluginSceneDataModel> result = new ArrayList<>();
                for (CollectionPluginSceneElementBO element : collectPlugin.getElementMap()
                        .values()) {
                    List<Port> ports = collectionSceneService.listPagePorts(element.getSrcElement()
                                                                                    .getEid());
                    if (CollectionUtils.isNotEmpty(ports)) {
                        List<CollectionPluginSceneDataModel> models = new ArrayList<>();
                        for (Port p : ports) {
                            CollectionPluginSceneDataModel collectionPluginSceneDataModel = getCollectionPluginSceneDataModel(instance, element, p);
                            models.add(collectionPluginSceneDataModel);
                        }
                        result.addAll(models);
                    }
                    List<Netiface> netifaces = collectionSceneService.listNetifaces(element.getSrcElement()
                                                                                            .getEid());
                    if (CollectionUtils.isNotEmpty(netifaces)) {
                        List<CollectionPluginSceneDataModel> models = new ArrayList<>();
                        for (Netiface n : netifaces) {
                            CollectionPluginSceneDataModel collectionPluginSceneDataModel = getCollectionPluginSceneDataModel(instance, element, n);
                            models.add(collectionPluginSceneDataModel);
                        }
                        result.addAll(models);
                    }
                    BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
                    request.setSceneId(instance.getId());
                    request.setElementIds(List.of(element.getSrcElement()
                                                          .getId()));
                    request.setPage(1);
                    request.setPageSize(10);
                    List<LogFileVO> logfiles = logFileService.pageByElementId(request)
                            .getSource()
                            .getObjs();
                    if (CollectionUtils.isNotEmpty(logfiles)) {
                        List<CollectionPluginSceneDataModel> models = new ArrayList<>();
                        for (LogFileVO n : logfiles) {
                            CollectionPluginSceneDataModel collectionPluginSceneDataModel = getCollectionPluginSceneDataModel(CollectionPluginSceneDataModel.Type.LOGFile, instance, element, JsonCommon.stringify(n));
                            models.add(collectionPluginSceneDataModel);
                        }
                        result.addAll(models);
                    }
                }
                if (CollectionUtils.isEmpty(result)) {
                    continue;
                }
                collectionPluginSceneDataMapper.bulkCreate(result);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static CollectionPluginSceneDataModel getCollectionPluginSceneDataModel(int LOGFile, CsceneInstanceModel instance, CollectionPluginSceneElementBO element, String n) {
        CollectionPluginSceneDataModel model = new CollectionPluginSceneDataModel();
        model.setType(LOGFile);
        model.setScene(instance.getScene());
        model.setSceneElement(element.getSrcElement()
                                      .getId());
        model.setData(n);
        return model;
    }

    private static CollectionPluginSceneDataModel getCollectionPluginSceneDataModel(CsceneInstanceModel instance, CollectionPluginSceneElementBO element, Netiface n) {
        CollectionPluginSceneDataModel model = new CollectionPluginSceneDataModel();
        model.setType(CollectionPluginSceneDataModel.Type.NETIFACE);
        model.setScene(instance.getScene());
        model.setSceneElement(element.getSrcElement()
                                      .getId());
        model.setData(JsonCommon.stringify(n));
        return model;
    }

    private static CollectionPluginSceneDataModel getCollectionPluginSceneDataModel(CsceneInstanceModel instance, CollectionPluginSceneElementBO element, Port p) {
        CollectionPluginSceneDataModel model = new CollectionPluginSceneDataModel();
        model.setType(CollectionPluginSceneDataModel.Type.PORT);
        model.setScene(instance.getScene());
        model.setSceneElement(element.getSrcElement()
                                      .getId());
        model.setData(JsonCommon.stringify(p));
        return model;
    }

}
