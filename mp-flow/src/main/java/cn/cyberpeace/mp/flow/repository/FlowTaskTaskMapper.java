 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskTaskMapper.java.
 // File size 1408.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName FlowTaskTaskMapper
 * @Description 流量执行任务mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowTaskTaskModel.class)
public interface FlowTaskTaskMapper extends BaseModelMapper<FlowTaskTaskModel> {

    /**
     * 根据场景id查询节点列表
     * @param sceneId
     * @return
     */
    List<FlowTaskNode> nodeList(@Param("sceneId") String sceneId);

    /**
     * 根据场景id分页查询流量任务
     * @param params
     * @return
     */
    List<FlowTaskPageResponse> pageBySceneId(FlowTaskPageQueryParams params);

    /**
     * 根据场景id查询任务数
     * @param params
     * @return
     */
    int countBySceneId(FlowTaskPageQueryParams params);
	
}
