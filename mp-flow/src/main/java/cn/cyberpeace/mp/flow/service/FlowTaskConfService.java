 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskConfService.java.
 // File size 849.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;

import java.util.List;

/**
 * @ClassName FlowTaskConfService
 * @Description 流量任务配置service
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowTaskConfService extends BaseModelService<FlowTaskConfModel, QueryParams> {

    /**
     * 根据taskId查询配置信息
     *
     * @param taskId
     * @return
     */
    FlowTaskConfModel findByTaskId(String taskId);

    /**
     * 根据taskId集合查询
     *
     * @param taskIds
     * @return
     */
    List<FlowTaskConfModel> listByTaskIds(List<String> taskIds);

}

