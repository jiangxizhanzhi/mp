package cn.cyberpeace.mp.cscene;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneDataMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
public class CscenePluginDataRecordService {

    @Resource
    private CollectionPluginSceneDataMapper collectionPluginSceneDataMapper;

    @Resource
    private CsceneService csceneService;

    @Resource
    private ScenePluginCollectionSceneService collectionSceneService;

    @Autowired
    private LogFileService logFileService;

    @Scheduled(initialDelay = 120, fixedRate = 120, timeUnit = TimeUnit.SECONDS)
    public void action() {
        var instances = csceneService.getRunningCscenes();
        for (var instance : instances) {
            if (instance.getReferenceType() == null || BaseCsceneModel.ReferenceType.READ_VS_BLUE != instance.getReferenceType() || BaseCsceneModel.ReferenceType.TEAM_TRAINING != instance.getReferenceType()) {
                continue;
            }
            try {
                SceneHandler sceneHandler = new SceneHandler(instance.getScene(), null, null);
                CollectionScenePluginHandler collectPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                if (collectPlugin == null) {
                    continue;
                }
                List<CollectionPluginSceneDataModel> result = new ArrayList<>();
                for (var element : collectPlugin.getElementMap().values()) {
                    var ports = collectionSceneService.listPagePorts(element.getSrcElement().getEid());
                    if (CollectionUtils.isNotEmpty(ports)) {
                        var models = ports.stream().map(p -> {
                            CollectionPluginSceneDataModel model = new CollectionPluginSceneDataModel();
                            model.setType(CollectionPluginSceneDataModel.Type.PORT);
                            model.setScene(instance.getScene());
                            model.setSceneElement(element.getSrcElement().getId());
                            model.setData(JsonCommon.stringify(p));
                            return model;
                        }).collect(Collectors.toList());
                        result.addAll(models);
                    }
                    var netifaces = collectionSceneService.listNetifaces(element.getSrcElement().getEid());
                    if (CollectionUtils.isNotEmpty(netifaces)) {
                        var models = netifaces.stream().map(n -> {
                            CollectionPluginSceneDataModel model = new CollectionPluginSceneDataModel();
                            model.setType(CollectionPluginSceneDataModel.Type.NETIFACE);
                            model.setScene(instance.getScene());
                            model.setSceneElement(element.getSrcElement().getId());
                            model.setData(JsonCommon.stringify(n));
                            return model;
                        }).collect(Collectors.toList());
                        result.addAll(models);
                    }
                    BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
                    request.setSceneId(instance.getId());
                    request.setElementIds(List.of(element.getSrcElement().getId()));
                    request.setPage(1);
                    request.setPageSize(10);
                    var logfiles = logFileService.pageByElementId(request).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(logfiles)) {
                        var models = logfiles.stream().map(n -> {
                            CollectionPluginSceneDataModel model = new CollectionPluginSceneDataModel();
                            model.setType(CollectionPluginSceneDataModel.Type.LOGFile);
                            model.setScene(instance.getScene());
                            model.setSceneElement(element.getSrcElement().getId());
                            model.setData(JsonCommon.stringify(n));
                            return model;
                        }).collect(Collectors.toList());
                        result.addAll(models);
                    }
                }
                if (CollectionUtils.isEmpty(result)) {
                    continue;
                }
                collectionPluginSceneDataMapper.bulkCreate(result);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public List<CollectionPluginSceneDataModel> getData(TimeListPageInfoQueryParams params, int type,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        String sceneId = params.getSceneIds().get(0);
        String nodeId = params.getNodeIds().get(0);
        return collectionPluginSceneDataMapper.list(new HumanCondition()
                .filterEqual(CollectionPluginSceneDataModel::getScene, sceneId)
                .filterEqual(CollectionPluginSceneDataModel::getSceneElement, nodeId)
                .filterEqual(CollectionPluginSceneDataModel::getType, type)
                .filterGreaterThan(CollectionPluginSceneDataModel::getCreateTime, startTime)
                .filterLessThan(CollectionPluginSceneDataModel::getCreateTime, endTime)
                .pagination(1, 10)
                .reverseOrderBy(CollectionPluginSceneDataModel::getCreateTime)
        );
    }
}
