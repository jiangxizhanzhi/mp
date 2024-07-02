package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SceneSnapshotService
 * @Description
 * @Author maojun
 * @Date 2022/7/28
 * @Version 1.0.0
 */
@Service
public class SceneSnapshotService {
    private SceneElementSnapshotMapper snapshotMapper;

    /**
     * SceneSnapshotService
     *
     * @param snapshotMapper
     */
    @Autowired
    public SceneSnapshotService(SceneElementSnapshotMapper snapshotMapper) {
        this.snapshotMapper = snapshotMapper;
    }

    /**
     * getElementSnapshots
     *
     * @param ids
     * @return
     */
    public ServiceResult<List<SceneElementSnapshotModel>> getElementSnapshots(List<String> ids) {
        List<SceneElementSnapshotModel> list = snapshotMapper.list(new HumanCondition()
                .filterIn(SceneElementSnapshotModel::getId, ids.toArray(new String[]{}))
                .reverseOrderBy(SceneElementSnapshotModel::getPk));
        return new ServiceResult<>(list);
    }

    /**
     * deleteElementSnapshot
     *
     * @param snapshotId
     * @return
     */
    public ServiceResult<Boolean> deleteElementSnapshot(String snapshotId) {
        boolean deleted = snapshotMapper.delete(snapshotId);
        return new ServiceResult<>(deleted);
    }

    /**
     * getElementSnapshot
     *
     * @param snapshotId
     * @return
     */
    public ServiceResult<SceneElementSnapshotModel> getElementSnapshot(String snapshotId) {
        SceneElementSnapshotModel result = snapshotMapper.get(snapshotId);
        return new ServiceResult<>(result);
    }

    /**
     * addSnapshot
     *
     * @param id
     * @param sceneId
     * @param nodeId
     * @param address
     * @param name
     * @param sourceName
     * @param description
     * @return
     */
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
