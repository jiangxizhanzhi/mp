package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface FlowScriptParamService extends BaseModelService<FlowScriptParamModel, QueryParams> {

    
    List<FlowScriptParamModel> selectByFlowId(String flowId);

    
    List<FlowScriptParamModel> selectByFlowIds(List<String> flowIds);

    
    int deleteByFlowId(@Param("flowId") String flowId);

    
    int deleteByFlowIds(List<String> flowIds);

}

