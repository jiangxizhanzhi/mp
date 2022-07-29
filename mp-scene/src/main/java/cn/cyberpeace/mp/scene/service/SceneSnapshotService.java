package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/28
 */
@Service
public class SceneSnapshotService {
    private SceneElementSnapshotMapper snapshotMapper;

    @Autowired
    public SceneSnapshotService(SceneElementSnapshotMapper snapshotMapper) {
        this.snapshotMapper = snapshotMapper;
    }

    public ServiceResult<List<SceneElementSnapshotModel>> getElementSnapshots(String sceneConfigId) {
        var list = snapshotMapper.list(new HumanCondition()
                .filterEqual(SceneElementSnapshotModel::getSceneConfig, sceneConfigId)
                .reverseOrderBy(SceneElementSnapshotModel::getPk));
        return new ServiceResult<>(list);
    }

    public ServiceResult<List<SceneElementSnapshotModel>> getElementSnapshots(String sceneConfigId, String nodeId) {
        var list = snapshotMapper.list(new HumanCondition()
                .filterEqual(SceneElementSnapshotModel::getSceneConfig, sceneConfigId)
                .filterEqual(SceneElementSnapshotModel::getSceneElement, nodeId)
                .reverseOrderBy(SceneElementSnapshotModel::getPk));
        return new ServiceResult<>(list);
    }

    public ServiceResult<SceneElementSnapshotModel> getElementSnapshot(String sceneConfigId, String nodeId, String addressContent) {
        var model = snapshotMapper.first(new HumanCondition()
                .filterEqual(SceneElementSnapshotModel::getSceneConfig, sceneConfigId)
                .filterEqual(SceneElementSnapshotModel::getSceneElement, nodeId)
                .filterContains(SceneElementSnapshotModel::getAddress, addressContent)
                .reverseOrderBy(SceneElementSnapshotModel::getPk));
        return new ServiceResult<>(model);
    }

    public ServiceResult<Boolean> deleteElementSnapshot(String snapshotId) {
        boolean deleted = snapshotMapper.delete(snapshotId);
        return new ServiceResult<>(deleted);
    }

}
