package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 流量脚本参数service
 * @Author qiaokang
 * @Date 2021-11-25 15:00:45
 */
public interface FlowScriptParamService extends BaseModelService<FlowScriptParamModel, QueryParams> {

    /**
     * 根据流量id查询
     * @param flowId
     * @return
     */
    List<FlowScriptParamModel> selectByFlowId(String flowId);

    /**
     * 批量查询流量参数信息
     * @param flowIds
     * @return
     */
    List<FlowScriptParamModel> selectByFlowIds(List<String> flowIds);

    /**
     * 根据流量id删除
     * @param flowId
     * @return
     */
    int deleteByFlowId(@Param("flowId") String flowId);

    /**
     * 根据流量id批量删除
     * @param flowIds
     * @return
     */
    int deleteByFlowIds(List<String> flowIds);

}

