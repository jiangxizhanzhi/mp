package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;

import java.util.List;

public interface FlowInfoService extends BaseModelService<FlowInfoModel, FlowInfoQueryParams>  {

    
    List<FlowInfoModel> findByIds(List<String> idList);

}
