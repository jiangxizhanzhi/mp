package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountByReferenceTypeModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneReporter;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.service.taskpool.CsceneReportTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
public class CsceneInstanceService extends BaseModelServiceImpl<CsceneInstanceModel, CsceneInstanceMapper,
        BaseModelFields, QueryParams> {

    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;

    @Autowired
    private RabbitmqCsceneReporter csceneReporter;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    private final CsceneReportTaskPool csceneReportTaskPool = CsceneReportTaskPool.getInstance();

    private List<CsceneInstanceRoleModel> readCsceneInstanceRoles(String[] csceneInstanceIds) {
        if (csceneInstanceIds == null || csceneInstanceIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneInstanceRoleMapper.list(new HumanCondition()
                .filterIn(CsceneInstanceRoleModel::getCscene, csceneInstanceIds));
    }

    @Autowired
    public CsceneInstanceService(CsceneInstanceMapper modelMapper, CsceneInstanceRoleMapper csceneInstanceRoleMapper) {
        setModelMapper(modelMapper);
        setCsceneInstanceRoleMapper(csceneInstanceRoleMapper);
    }

    public ListObjServiceResult<CsceneInstanceBO> listBO(QueryParams queryParams, CsceneContextBO context) {
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

    public ServiceResult<Boolean> deleteById(String id, boolean logic, CsceneContextBO context) {
        var instanceHandler = new CsceneInstanceHandler(id, context);
        var flag = instanceHandler.delete(logic);
        if (flag) {
            CsceneReportTaskPool.getInstance().executeTask(() -> csceneReporter.reportDeleteCsceneInstance(id));
        }
        return new ServiceResult<>(flag);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean logic, CsceneContextBO context) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(deleteById(id, logic, context).getSource())) {
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

    public ServiceResult<List<NodeSessionBO>> getNodeSessions(String id, String nodeId) {
        var csceneInstanceResult = get(id).getSource();
        if (csceneInstanceResult == null) {
            return new ServiceResult<>(new ArrayList<>());
        }
        var csceneInstance = csceneInstanceResult.getObj();
        var sceneId = csceneInstance.getScene();
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(new ArrayList<>());
        }

        return scenePluginCloudSceneService.getSceneRemoteSessions(sceneId,
                StringUtils.isEmpty(nodeId) ? null : new String[]{nodeId}, true, false);
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

    /**
     * 根据引用类型分组统计
     * @param events
     * @return
     */
    public List<CsceneInstanceCountByReferenceTypeModel> countGroupByReferenceType(List<Integer> events){
        return getModelMapper().countGroupByReferenceType(events);
    }
}
