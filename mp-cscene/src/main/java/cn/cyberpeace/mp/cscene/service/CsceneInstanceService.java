package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsceneInstanceService extends BaseModelServiceImpl<CsceneInstanceModel, CsceneInstanceMapper,
        BaseModelFields, CsceneInstanceQueryParams> {

    @Autowired
    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;

    private List<CsceneInstanceRoleModel> readCsceneInstanceRoles(String[] csceneInstanceIds) {
        if (csceneInstanceIds == null || csceneInstanceIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneInstanceRoleMapper.list(new HumanCondition()
                .filterIn(CsceneInstanceRoleModel::getCscene, csceneInstanceIds));
    }

    @Autowired
    public CsceneInstanceService(CsceneInstanceMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    public ListObjServiceResult<CsceneInstanceBO> listBO(CsceneInstanceQueryParams queryParams, CsceneContextBO context) {
        var listResult = super.list(queryParams);
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        var instanceObjMap = listResult.getSource().getList().stream()
                .collect(Collectors.toMap(BaseModel::getId, x -> x));
        var instanceRoleObjMap = readCsceneInstanceRoles(instanceObjMap.keySet()
                .toArray(new String[0])).stream().collect(Collectors.groupingBy(CsceneInstanceRoleModel::getCscene));
        List<CsceneInstanceBO> instanceBOs = new ArrayList<>();
        instanceObjMap.forEach((instanceId, instanceObj) -> {
            var roleObjs = instanceRoleObjMap.getOrDefault(instanceId, new ArrayList<>());
            var instanceHandler = new CsceneInstanceHandler(instanceObj, roleObjs, context);
            instanceBOs.add(instanceHandler.toData());
        });
        return new ListObjServiceResult<>(new ListObjResult<>(instanceBOs, listResult.getSource().getTotal(),
                listResult.getSource().getFields()));
    }

    public ServiceResult<CsceneInstanceBO> getById(String id, CsceneContextBO context) {
        var instanceHandler = new CsceneInstanceHandler(id, context);
        return new ServiceResult<>(instanceHandler.toData());
    }

    public ServiceResult<Boolean> deleteById(String id, CsceneContextBO context) {
        var instanceHandler = new CsceneInstanceHandler(id, context);
        instanceHandler.delete();
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, CsceneContextBO context) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(deleteById(id, context).getSource())) {
                result += 1;
            }
        }

        if (result < ids.length) {
            log.error("Service bulk delete cscene instance {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    public ServiceResult<Flow> startFlow(String id, String toNodeId, String flowId, CsceneContextBO context) {
        return new ServiceResult<>(null);
    }

    public ServiceResult<Flow> stopFlow(String id, String toNodeId, String flowId, CsceneContextBO context) {
        return new ServiceResult<>(null);
    }

    public ServiceResult<Probe> startProbe(String id, String toNodeId, String probeId, CsceneContextBO context) {
        return new ServiceResult<>(null);
    }

    public ServiceResult<Probe> stopProbe(String id, String toNodeId, String probeId, CsceneContextBO context) {
        return new ServiceResult<>(null);
    }

}
