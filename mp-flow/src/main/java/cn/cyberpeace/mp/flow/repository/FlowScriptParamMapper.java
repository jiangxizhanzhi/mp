package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(FlowScriptParamModel.class)
public interface FlowScriptParamMapper extends BaseModelMapper<FlowScriptParamModel> {

    
    List<FlowScriptParamModel> selectByFlowId(@Param("flowId") String flowId);

    
    List<FlowScriptParamModel> selectByFlowIds(@Param("flowIds") List<String> flowIds);

    
    int deleteByFlowId(@Param("flowId") String flowId);

    
    int deleteByFlowIds(@Param("flowIds") List<String> flowIds);

}
