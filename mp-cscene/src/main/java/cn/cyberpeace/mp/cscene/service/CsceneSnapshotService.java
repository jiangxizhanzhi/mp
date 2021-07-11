package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.dto.scene.SceneInfo;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneSnapshotModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneSnapshotMapper;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.service.SceneSnapshotService;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeviceSnapshotParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeviceSnapshotResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SnapshotDeleteParams;
import cn.cyberpeace.mp.sceneplugincloud.util.SnapshotAddressUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CsceneSnapshotService {

    private CsceneSnapshotMapper csceneSnapshotMapper;

    private SceneSnapshotService sceneSnapshotService;

    private CsceneSpecialService csceneSpecialService;

    private CsceneInstanceMapper csceneInstanceMapper;

    private SceneElementMapper sceneElementMapper;

    @Autowired
    public CsceneSnapshotService(CsceneSnapshotMapper csceneSnapshotMapper, SceneSnapshotService sceneSnapshotService,
                                 CsceneSpecialService csceneSpecialService, CsceneInstanceMapper csceneInstanceMapper,
                                 SceneElementMapper sceneElementMapper) {
        this.csceneSnapshotMapper = csceneSnapshotMapper;
        this.sceneSnapshotService = sceneSnapshotService;
        this.csceneSpecialService = csceneSpecialService;
        this.csceneInstanceMapper = csceneInstanceMapper;
        this.sceneElementMapper = sceneElementMapper;
    }

    public ServiceResult<Boolean> checkSnapshotName(String csceneId, String csceneInstanceId, String nodeId, String name) {
        var sameNameModel = csceneSnapshotMapper.first(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getName, name)
                .filterNotEqual(CsceneSnapshotModel::getSnapshotResourceId, StringUtils.EMPTY)
                .filterEqual(CsceneSnapshotModel::getDeleted, false)
        );
        if (sameNameModel != null) {
            return new ServiceResult<>(false);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<String> saveCsceneSnapshot(String csceneId, String csceneInstanceId, String nodeId, String name) {
        CsceneSnapshotModel model = new CsceneSnapshotModel();
        model.setCsceneId(csceneId);
        model.setCsceneInstanceId(csceneInstanceId);
        model.setNodeId(nodeId);
        model.setName(name);
        model.setSnapshotId(TextUtils.uuid());
        csceneSnapshotMapper.save(model);
        return new ServiceResult<>(model.getSnapshotId());
    }

    public ServiceResult<Boolean> updateCsceneSnapshotRelativeInfo(String snapshotId, String snapshotResourceId, String sourceName) {
        CsceneSnapshotModel snapshotModel = new CsceneSnapshotModel();
        snapshotModel.setSnapshotResourceId(snapshotResourceId);
        snapshotModel.setSourceName(sourceName);
        csceneSnapshotMapper.filterUpdate(snapshotModel, new HumanCondition().filterEqual(CsceneSnapshotModel::getSnapshotId, snapshotId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<CsceneSnapshotModel> getCsceneSnapshotId(String snapshotId) {
        var result = csceneSnapshotMapper.first(
                new HumanCondition()
                        .filterEqual(CsceneSnapshotModel::getSnapshotId, snapshotId)
                        .filterEqual(CsceneSnapshotModel::getDeleted, false)
        );
        return new ServiceResult<>(result);
    }

    public ServiceResult<CsceneSnapshotModel> getCsceneSnapshot(String csceneId, String nodeId, String snapshotResourceId) {
        var result = csceneSnapshotMapper.first(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceId)
                .filterEqual(CsceneSnapshotModel::getDeleted, false)
        );
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<CsceneSnapshotModel>> getCsceneSnapshot(String csceneId) {
        var result = csceneSnapshotMapper.list(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getDeleted, false)
        );
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> logicDeleteCsceneSnapshot(String csceneId, String nodeId, String snapshotResourceId) {
        CsceneSnapshotModel model = new CsceneSnapshotModel();
        model.setDeleted(true);
        csceneSnapshotMapper.filterUpdate(model, new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceId)
        );
        return new ServiceResult<>(true);
    }

    @Transactional
    public ServiceResult<Boolean> deleteCsceneSnapshotRelativeInfo(String csceneId, String nodeId, String snapshotResourceId) {
        var csceneSnapshot = csceneSnapshotMapper.first(new HumanCondition().filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceId));
        if (csceneSnapshot != null) {
            csceneSnapshotMapper.delete(csceneSnapshot.getId());
            sceneSnapshotService.deleteElementSnapshot(csceneSnapshot.getSnapshotId());
        }
        return new ServiceResult<>(true);
    }

    @Transactional
    public ServiceResult<Boolean> deleteSnapshotRelativeInfoByResource(String snapshotResourceId) {
        var csceneSnapshot = csceneSnapshotMapper.first(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceId));
        if (csceneSnapshot != null) {
            csceneSnapshotMapper.delete(csceneSnapshot.getId());
            sceneSnapshotService.deleteElementSnapshot(csceneSnapshot.getSnapshotId());
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> correctSnapshotRelativeInfo() {
        
        var noResourceIds = csceneSnapshotMapper.list(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, StringUtils.EMPTY)
                .filterEqual(CsceneSnapshotModel::getDeleted, false));
        if (CollectionUtils.isNotEmpty(noResourceIds)) {
            LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-30);
            for (CsceneSnapshotModel csceneSnapshot : noResourceIds) {
                if (csceneSnapshot.getCreateTime().isAfter(localDateTime)) {
                    continue;
                }
                try {
                    this.correctCsceneSnapshotResourceId(csceneSnapshot);
                } catch (Exception e) {
                    log.error("failed to correct snapshot data , cscene snapshot id is {}", csceneSnapshot.getId());
                }
            }
        }

        
        var deletedSnapshots = csceneSnapshotMapper.list(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getDeleted, true));
        if (CollectionUtils.isNotEmpty(deletedSnapshots)) {
            LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-5);
            for (CsceneSnapshotModel csceneSnapshot : deletedSnapshots) {
                if (csceneSnapshot.getCreateTime().isAfter(localDateTime)) {
                    continue;
                }
                if (StringUtils.isNotEmpty(csceneSnapshot.getSnapshotId())) {
                    try {
                        var sceneSnapshot = sceneSnapshotService.getElementSnapshot(csceneSnapshot.getSnapshotId()).getSource();
                        if (sceneSnapshot != null) {
                            this.deleteOneSnapshot(csceneSnapshot.getCsceneId(), sceneSnapshot, true);
                        }
                    } catch (Exception e) {
                        log.error("failed to delete snapshot data , cscene snapshot id is {}", csceneSnapshot.getId());
                    }
                }
            }
        }
        return new ServiceResult<>(true);
    }


    private boolean correctCsceneSnapshotResourceId(CsceneSnapshotModel csceneSnapshot) {
        var result = sceneSnapshotService.getElementSnapshot(csceneSnapshot.getSnapshotId()).getSource();
        if (result != null) {
            
            String resourceId = SnapshotAddressUtils.parseAddress(result.getAddress()).getSnapshotUuid();
            this.updateCsceneSnapshotRelativeInfo(csceneSnapshot.getSnapshotId(), resourceId, result.getSourceName());
            return true;
        } else {
            
            SceneInfo sceneInfo = this.getSceneInfo(csceneSnapshot.getCsceneInstanceId());
            if (sceneInfo != null && StringUtils.isNotEmpty(sceneInfo.getSceneId())) {
                SceneHandler sceneHandler = new SceneHandler(sceneInfo.getSceneId(), null, false, null);
                CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                if (cloudPlugin != null) {
                    var node = cloudPlugin.getElement(csceneSnapshot.getNodeId());
                    if (node != null && StringUtils.isNotEmpty(node.getImageUuid())) {
                        
                        DeviceSnapshotParam param = new DeviceSnapshotParam();
                        param.setDeviceUuid(node.getImageUuid());
                        var snapShotResult = new Remote().getHttpClient().get(CloudUrlConstant.SNAPSHOT_LIST, param, DeviceSnapshotResultData.class);
                        DeviceSnapshotResultData.Snapshot cloudSnapshot = null;
                        if (snapShotResult != null && CollectionUtils.isNotEmpty(snapShotResult.getItems())) {
                            for (var m : snapShotResult.getItems()) {
                                if (csceneSnapshot.getName().equals(m.getName()) && !Boolean.TRUE.equals(m.getDeleted())) {
                                    cloudSnapshot = m;
                                }
                            }
                        }
                        if (cloudSnapshot != null) {
                            
                            String snapshotId = csceneSnapshot.getSnapshotId();
                            String sceneId = sceneInfo.getSceneId();
                            String nodeId = csceneSnapshot.getNodeId();
                            String address = SnapshotAddressUtils.buildAddress(cloudSnapshot.getDeviceUuid() + cloudSnapshot.getName(), cloudSnapshot.getUuid());
                            String name = cloudSnapshot.getName();
                            String sourceName = cloudSnapshot.getImageName();
                            String description = cloudSnapshot.getDescription();
                            sceneSnapshotService.addSnapshot(snapshotId, sceneId, nodeId, address, name, sourceName, description);
                            this.updateCsceneSnapshotRelativeInfo(snapshotId, cloudSnapshot.getUuid(), sourceName);
                        } else {
                            
                            csceneSnapshotMapper.delete(csceneSnapshot.getId());
                        }
                    }
                }
            }
        }
        return false;
    }

    public void deleteSceneSnapshots(String csceneId) {
        var csceneSnapshots = this.getCsceneSnapshot(csceneId).getSource();
        if (CollectionUtils.isEmpty(csceneSnapshots)) {
            return;
        }
        var snapshotIds = csceneSnapshots.stream().map(s -> s.getSnapshotId()).collect(Collectors.toList());
        var snapshots = sceneSnapshotService.getElementSnapshots(snapshotIds).getSource();
        if (CollectionUtils.isNotEmpty(snapshots)) {
            Set<String> names = new HashSet<>();
            Set<String> sourceNames = new HashSet<>();
            Map<String, SceneElementSnapshotModel> name2Model = new HashMap<>();
            snapshots.forEach(s -> {
                names.add(s.getName());
                sourceNames.add(s.getSourceName());
                name2Model.put(s.getName(), s);
            });
            Queue<SceneElementSnapshotModel> queue = new LinkedList<>();
            Set<String> queueAdded = new HashSet<>();
            snapshots.forEach(s -> {
                if (!sourceNames.contains(s.getName())) {
                    queue.add(s);
                    queueAdded.add(s.getId());
                }
            });
            int times = 0;
            while (!queue.isEmpty()) {
                if (times > snapshots.size()) {
                    break;
                } else {
                    times++;
                    var snapshot = queue.poll();
                    boolean deleteResult = false;
                    try {
                        deleteResult = this.deleteOneSnapshot(csceneId, snapshot, false);
                    } catch (Exception e) {
                        log.error("faild to delete snapshot , id is {}", snapshot.getId(), e);
                    }
                    if (deleteResult) {
                        var model = name2Model.get(snapshot.getSourceName());
                        if (model != null && !queueAdded.contains(model.getId())) {
                            queue.add(model);
                            queueAdded.add(model.getId());
                        }
                    }
                }
            }
        }
    }

    private boolean deleteOneSnapshot(String csceneId, SceneElementSnapshotModel snapshot, boolean correctMode) {
        String image = SnapshotAddressUtils.parseAddress(snapshot.getAddress()).getImageName();
        var configUsed = csceneSpecialService.statDeviceReferCscenes(new String[]{image}).getSource();
        var configNames = this.getReferCsceneNames(configUsed.get(image), (c) -> c.getName(), 3);
        var info = SnapshotAddressUtils.parseAddress(snapshot.getAddress());
        if (CollectionUtils.isNotEmpty(configNames)) {
            if (correctMode) {
                this.deleteCsceneSnapshotRelativeInfo(csceneId, snapshot.getSceneElement(), info.getSnapshotUuid());
            }
            return false;
        }
        var instanceUsed = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{image}).getSource();
        var instanceNames = this.getReferCsceneNames(instanceUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(instanceNames)) {
            if (correctMode) {
                this.deleteCsceneSnapshotRelativeInfo(csceneId, snapshot.getSceneElement(), info.getSnapshotUuid());
            }
            return false;
        }

        
        SnapshotDeleteParams params = new SnapshotDeleteParams();
        params.setSnapshotName(snapshot.getName());
        params.setDeviceUuid(StringUtils.replaceOnce(info.getImageName(), snapshot.getName(), StringUtils.EMPTY));
        params.setSnapshotUuid(info.getSnapshotUuid());
        
        this.logicDeleteCsceneSnapshot(csceneId, snapshot.getSceneElement(), info.getSnapshotUuid());
        boolean isDeleted = false;
        for (int i = 0; i < 3; i++) {
            var elementModel = sceneElementMapper.list(new HumanCondition()
                    .filterEqual(SceneElementModel::getScene, snapshot.getScene())
                    .filterEqual(SceneElementModel::getNodeId, snapshot.getSceneElement()));
            
            if (CollectionUtils.isEmpty(elementModel)) {
                isDeleted = true;
            }
            if (CollectionUtils.isNotEmpty(elementModel) && elementModel.get(0).getEvent() == SceneElementModel.Event.DELETE &&
                    elementModel.get(0).getEventStatus() == EventStatus.OVER) {
                isDeleted = true;
            }
            if (isDeleted) {
                break;
            } else {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        if (!isDeleted) {
            return false;
        }
        try {
            new Remote().cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DEL_SNAPSHOT_URL, params, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            this.deleteCsceneSnapshotRelativeInfo(csceneId, snapshot.getSceneElement(), info.getSnapshotUuid());
            return true;
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(e.getMessage()) && e.getMessage().equals("snapshot not exists")) {
                this.deleteCsceneSnapshotRelativeInfo(csceneId, snapshot.getSceneElement(), info.getSnapshotUuid());
                return true;
            }
            log.error("failed to delete cloud snapshot , resource info is {}", snapshot.getAddress(), e);
            return false;
        }
    }

    private <T> List<String> getReferCsceneNames(List<T> usingList, Function<T, String> getName, int nameSize) {
        if (CollectionUtils.isEmpty(usingList)) {
            return null;
        }
        Set<String> names = new HashSet<>();
        for (var used : usingList) {
            if (names.size() < nameSize) {
                names.add(getName.apply(used));
            }
        }
        return new ArrayList<>(names);
    }

    private SceneInfo getSceneInfo(String csceneInstanceId) {
        var condition = new HumanCondition().link(CsceneInstanceModel.class, CsceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getCscene, CsceneModel.class, CsceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getId, csceneInstanceId)
                .selectAs(CsceneInstanceModel::getScene, SceneInfo::getSceneId)
                .currentLink(CsceneModel.class)
                .selectAs(CsceneModel::getSceneConfig, SceneInfo::getSceneConfigId);
        var list = csceneInstanceMapper.list(condition, SceneInfo.class);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
