package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.CsceneSnapshotModel;
import cn.cyberpeace.mp.cscene.repository.CsceneSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/2
 */
@Service
public class CsceneSnapshotService {

    private CsceneSnapshotMapper csceneSnapshotMapper;

    @Autowired
    public CsceneSnapshotService(CsceneSnapshotMapper csceneSnapshotMapper) {
        this.csceneSnapshotMapper = csceneSnapshotMapper;
    }

    public ServiceResult<String> saveCsceneSnapshot(String csceneId, String csceneInstanceId, String nodeId, String name) {
        var sameNameModel = csceneSnapshotMapper.first(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getName, name)
        );
        if (sameNameModel != null) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NAME_DUPLICATED);
        }
        CsceneSnapshotModel model = new CsceneSnapshotModel();
        model.setCsceneId(csceneId);
        model.setCsceneInstanceId(csceneInstanceId);
        model.setNodeId(nodeId);
        model.setName(name);
        model.setSnapshotId(TextUtils.uuid());
        csceneSnapshotMapper.save(model);
        return new ServiceResult<>(model.getSnapshotId());
    }

    public ServiceResult<Boolean> updateCsceneSnapshotResourceId(String snapshotId, String snapshotResourceId, String sourceName) {
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

    public ServiceResult<Boolean> deleteByCscene(String csceneId, String nodeId, String snapshotResourceId) {
        csceneSnapshotMapper.filterDelete(new HumanCondition()
                .filterEqual(CsceneSnapshotModel::getCsceneId, csceneId)
                .filterEqual(CsceneSnapshotModel::getNodeId, nodeId)
                .filterEqual(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceId)
        );
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> deleteByCscene(String csceneId) {
        csceneSnapshotMapper.filterDelete(new HumanCondition().filterEqual(CsceneSnapshotModel::getCsceneId, csceneId));
        return new ServiceResult<>(true);
    }
}
