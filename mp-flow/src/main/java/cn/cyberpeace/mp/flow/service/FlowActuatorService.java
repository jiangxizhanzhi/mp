package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.flow.bo.StartFlowActuatorParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.response.ActuatorSceneName;
import cn.cyberpeace.mp.flow.response.ActuatorStatistic;
import cn.cyberpeace.mp.flow.response.ActuatorTaskCount;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.service.field.FlowActuatorFields;
import cn.cyberpeace.mp.flow.bo.FlowActuatorQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FlowActuatorService extends BaseModelServiceImpl<FlowActuatorModel, FlowActuatorMapper, FlowActuatorFields, FlowActuatorQueryParams> {

    private FlowActuatorMapper flowActuatorMapper;

    private ICloudClient cloudClient;

    private FlowCsceneService flowCsceneService ;

    @Autowired
    public FlowActuatorService(FlowActuatorMapper flowActuatorMapper, ICloudClient cloudClient,FlowCsceneService flowCsceneService) {
        super();
        super.setModelMapper(flowActuatorMapper);
        this.flowActuatorMapper = flowActuatorMapper;
        this.cloudClient = cloudClient;
        this.flowCsceneService = flowCsceneService;
    }

    public FlowActuatorResponse getFlowActuator(String actuatorId) {
        var res = super.get(actuatorId);
        var model = res.asResult().getData();
        FlowActuatorResponse response = new FlowActuatorResponse();
        response.setName(model.getTitle());
        response.setIp(model.getIp());
        response.setNics(model.getNetworkId());
        response.setStatus(model.getStatus());
        response.setCreateTime(TimeUtils.localDateTimeToString(model.getCreateTime()));
        return response;
    }

    public List<ActuatorTaskCount> taskCount() {
        return flowActuatorMapper.countTask();
    }

    public List<ActuatorStatistic> getFlowActuatorStatistics() {
        return flowActuatorMapper.countByType();
    }

    public List<ActuatorSceneName> getFlowActuatorSceneNames() {
        return flowActuatorMapper.groupByScene();
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowActuatorQueryParams queryParams) {
        if (StringUtils.isNotEmpty(queryParams.getSceneId())) {
            humanCondition.filterEqual(FlowActuatorModel::getCsceneInstanceId, queryParams.getSceneId());
        }
        if (queryParams.getStatus() != null) {
            humanCondition.filterEqual(FlowActuatorModel::getStatus, queryParams.getStatus());
        }
    }

    public ListObjServiceResult<FlowActuatorResponse> getFlowActuatorList(FlowActuatorQueryParams params) {
        if (params.getOrders() == null || params.getOrders().length == 0) {
            params.setOrders(new String[]{"-createTime"});
        }
        var result = super.list(params);
        int total = result.sourceTotal();
        if (total == 0) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        } else {
            List<FlowActuatorModel> objs = result.sourceObjs();
            List<FlowActuatorResponse> responses = new ArrayList<>();
            for (var m : objs) {
                FlowActuatorResponse response = this.convert2FlowActuatorResponse(m);
                responses.add(response);
            }
            ListObjServiceResult<FlowActuatorResponse> res = new ListObjServiceResult<>(new ListObjResult<>(responses, total));
            return res;
        }
    }

    public FlowActuatorResponse createFlowActuator(StartFlowActuatorParams params) {
        // 提交创建请求
        var instance = cloudClient.createVirtualMachine();
        String resourceId = instance.getId();
        FlowActuatorModel actuatorModel = new FlowActuatorModel();
        actuatorModel.setStatus(0);
//        actuatorModel.setTitle(params.getActuatorName());
        actuatorModel.setCsceneId(params.getCsceneId());
        actuatorModel.setCsceneInstanceId(params.getCsceneInstanceId());
        actuatorModel.setResourceId(resourceId);
        actuatorModel.setNetworkId(instance.getNetwork().get(0).getNetworkId());
        actuatorModel.setIp(instance.getNetwork().get(0).getFixedIp());
        flowActuatorMapper.save(actuatorModel);
        var res = this.convert2FlowActuatorResponse(actuatorModel);
        return res;
    }

    public void deleteFlowActuator(String actuatorId) {
        FlowActuatorModel model = flowActuatorMapper.get(actuatorId);
        cloudClient.deleteVirtualMachine(model.getResourceId());
        flowActuatorMapper.delete(actuatorId);
    }

    private FlowActuatorResponse convert2FlowActuatorResponse(FlowActuatorModel model) {
        FlowActuatorResponse response = new FlowActuatorResponse();
        response.setId(model.getId());
        response.setName(model.getTitle());
        response.setIp(model.getIp());
        response.setNics(model.getNetworkId());
        response.setStatus(model.getStatus());
        response.setCreateTime(TimeUtils.localDateTimeToString(model.getCreateTime()));
        return response;
    }
}
