package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.model.CsceneSnapshotModel;
import cn.cyberpeace.mp.cscene.repository.CsceneSnapshotMapper;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.service.SceneSnapshotService;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
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

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/2
 */
@Slf4j
@Service
public class CsceneSnapshotService {

    private CsceneSnapshotMapper csceneSnapshotMapper;

    private SceneSnapshotService sceneSnapshotService;

    private CsceneSpecialService csceneSpecialService;


    @Autowired
    public CsceneSnapshotService(CsceneSnapshotMapper csceneSnapshotMapper, SceneSnapshotService sceneSnapshotService, CsceneSpecialService csceneSpecialService) {
        this.csceneSnapshotMapper = csceneSnapshotMapper;
        this.sceneSnapshotService = sceneSnapshotService;
        this.csceneSpecialService = csceneSpecialService;
    }

    public ServiceResult<Boolean> checkSnapshotName(String csceneId, String csceneInstanceId, String nodeId, String name) {
        var sameNameModel = csceneSnapshotMapper.first(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getName, name)
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
        var result = csceneSnapshotMapper.first(new HumanCondition().filterEqual(CsceneSnapshotModel::getSnapshotId, snapshotId));
        return new ServiceResult<>(result);
    }

    public ServiceResult<CsceneSnapshotModel> getCsceneSnapshot(String csceneId, String nodeId, String snapshotResourceId) {
        var result = csceneSnapshotMapper.first(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceId)
        );
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<CsceneSnapshotModel>> getCsceneSnapshot(String csceneId) {
        var result = csceneSnapshotMapper.list(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
        );
        return new ServiceResult<>(result);
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
            snapshots.forEach(s -> {
                if (!sourceNames.contains(s.getName())) {
                    queue.add(s);
                }
            });
            int times = 0;
            while (!queue.isEmpty()) {
                if (times > snapshots.size()) {
                    break;
                } else {
                    times++;
                    var snapshot = queue.poll();
                    try {
                        this.deleteOneSnapshot(csceneId, snapshot);
                        if (name2Model.containsKey(snapshot.getSourceName())) {
                            queue.add(name2Model.get(snapshot.getSourceName()));
                        }
                    } catch (Exception e) {
                        log.error("faild to delete snapshot , id is {}", snapshot.getId(), e);
                    }
                }
            }
        }
    }

    private boolean deleteOneSnapshot(String csceneId, SceneElementSnapshotModel snapshot) {
        String image = SnapshotAddressUtils.parseAddress(snapshot.getAddress()).getImageName();
        var configUsed = csceneSpecialService.statDeviceReferCscenes(new String[]{ image }).getSource();
        var configNames = this.getReferCsceneNames(configUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(configNames)) {
            return false;
        }

        var instanceUsed = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{ image }).getSource();
        var instanceNames = this.getReferCsceneNames(instanceUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(instanceNames)) {
            return false;
        }

        SnapshotDeleteParams params = new SnapshotDeleteParams();
        params.setSnapshotName(snapshot.getName());
        var info = SnapshotAddressUtils.parseAddress(snapshot.getAddress());
        params.setDeviceUuid(StringUtils.replaceOnce(info.getImageName(), snapshot.getName(), StringUtils.EMPTY));
        params.setSnapshotUuid(info.getSnapshotUuid());
        try {
            new Remote().cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DEL_SNAPSHOT_URL, params, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
        } catch (Exception e) {
            // 删除时已不存在的异常 直接任务删除成功
        }
        this.deleteCsceneSnapshotRelativeInfo(csceneId, snapshot.getSceneElement(), info.getSnapshotUuid());
        return true;
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
}
