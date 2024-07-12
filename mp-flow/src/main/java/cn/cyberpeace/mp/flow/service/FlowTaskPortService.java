 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskPortService.java.
 // File size 746.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;

/**
 * @ClassName FlowTaskPortService
 * @Description 流量任务网口service
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowTaskPortService extends BaseModelService<FlowTaskPortModel, QueryParams> {

    /**
     * 根据网络id和流量执行器id查询网口信息
     *
     * @param networkId
     * @param actuatorId
     * @return
     */
    FlowTaskPortModel findByNetworkIdAndActuatorId(String networkId, String actuatorId);

}

