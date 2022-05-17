package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowPortModel;

import java.util.List;

public interface FlowPortService extends BaseModelService<FlowPortModel, QueryParams>  {

    void batchCreatePorts(List<FlowNetworkInfo> networkInfos, FlowActuatorModel flowActuatorModel);

}
