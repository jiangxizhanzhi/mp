package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.service.field.FlowActuatorFields;
import cn.cyberpeace.mp.flow.bo.FlowActuatorQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FlowActuatorService extends BaseModelServiceImpl<FlowActuatorModel, FlowActuatorMapper, FlowActuatorFields, FlowActuatorQueryParams> {

    private FlowActuatorMapper flowActuatorMapper;

    private ICloudClient cloudClient;

    @Autowired
    public FlowActuatorService(FlowActuatorMapper flowActuatorMapper, ICloudClient cloudClient) {
        super();
        super.setModelMapper(flowActuatorMapper);
        this.flowActuatorMapper = flowActuatorMapper;
        this.cloudClient = cloudClient;
    }

    public FlowActuatorResponse getFlowActuator(String actuatorId) {
        var res = super.get(actuatorId);
        var model = res.asResult().getData();
        FlowActuatorResponse response = new FlowActuatorResponse();
        response.setTitle(model.getTitle());
        response.setIp(model.getIp());
        response.setTargetSubnet(model.getNetworkId());
        response.setStatus(model.getStatus());
        response.setCreateTime(TimeUtils.localDateTimeToString(model.getCreateTime()));
        return response;
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

    public FlowActuatorResponse createFlowActuator(String title) {
        // 提交创建请求
        var instance = cloudClient.createVirtualMachine();
        String instanceId = instance.getId();
        FlowActuatorModel actuatorModel = new FlowActuatorModel();
        actuatorModel.setStatus(0);
        actuatorModel.setTitle(title);
        actuatorModel.setCsceneId(instanceId);
        actuatorModel.setInstanceId(instanceId);
        actuatorModel.setNetworkId(instance.getNetworks().get(0).getNetworkId());
        actuatorModel.setIp(instance.getNetworks().get(0).getFixedIp());
        flowActuatorMapper.save(actuatorModel);
        var res = this.convert2FlowActuatorResponse(actuatorModel);
        return res;
    }

    public void deleteFlowActuator(String actuatorId) {
        FlowActuatorModel model = flowActuatorMapper.get(actuatorId);
        cloudClient.deleteVirtualMachine(model.getInstanceId());
        flowActuatorMapper.delete(actuatorId);
    }

    private FlowActuatorResponse convert2FlowActuatorResponse(FlowActuatorModel model) {
        FlowActuatorResponse response = new FlowActuatorResponse();
        response.setId(model.getId());
        response.setTitle(model.getTitle());
        response.setIp(model.getIp());
        response.setTargetSubnet(model.getNetworkId());
        response.setStatus(model.getStatus());
        response.setCreateTime(TimeUtils.localDateTimeToString(model.getCreateTime()));
        return response;
    }
}
