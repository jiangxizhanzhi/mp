 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowScriptParamMapper.java.
 // File size 1393.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName FlowScriptParamMapper
 * @Description 流量脚本参数mapper
 * @Author qiaokang
 * @Date 2021-11-25 15:00:45
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowScriptParamModel.class)
public interface FlowScriptParamMapper extends BaseModelMapper<FlowScriptParamModel> {

    /**
     * 根据流量id查询
     * @param flowId
     * @return
     */
    List<FlowScriptParamModel> selectByFlowId(@Param("flowId") String flowId);

    /**
     * 批量查询流量参数信息
     * @param flowIds
     * @return
     */
    List<FlowScriptParamModel> selectByFlowIds(@Param("flowIds") List<String> flowIds);

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
    int deleteByFlowIds(@Param("flowIds") List<String> flowIds);

}
