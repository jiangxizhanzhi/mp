package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SceneSnapshotService {
    private SceneElementSnapshotMapper snapshotMapper;

    @Autowired
    public SceneSnapshotService(SceneElementSnapshotMapper snapshotMapper) {
        this.snapshotMapper = snapshotMapper;
    }

    public ServiceResult<List<SceneElementSnapshotModel>> getElementSnapshots(List<String> ids) {
        var list = snapshotMapper.list(new HumanCondition()
                .filterIn(SceneElementSnapshotModel::getId, ids.toArray(new String[]{}))
                .reverseOrderBy(SceneElementSnapshotModel::getPk));
        return new ServiceResult<>(list);
    }

    public ServiceResult<Boolean> deleteElementSnapshot(String snapshotId) {
        boolean deleted = snapshotMapper.delete(snapshotId);
        return new ServiceResult<>(deleted);
    }

    public ServiceResult<SceneElementSnapshotModel> getElementSnapshot(String snapshotId) {
        var result = snapshotMapper.get(snapshotId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> addSnapshot(String id, String sceneId, String nodeId, String address, String name, String sourceName, String description) {
        SceneElementSnapshotModel elementSnapshotModel = new SceneElementSnapshotModel();
        elementSnapshotModel.setScene(sceneId);
        elementSnapshotModel.setSceneElement(nodeId);
        elementSnapshotModel.setName(name);
        elementSnapshotModel.setDescription(description);
        elementSnapshotModel.setSourceName(sourceName);
        elementSnapshotModel.setId(id);
        elementSnapshotModel.setAddress(address);
        snapshotMapper.create(elementSnapshotModel);
        return new ServiceResult<>(true);
    }

}
