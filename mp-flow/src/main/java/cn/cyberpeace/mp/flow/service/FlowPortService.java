 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPortService.java.
 // File size 846.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowPortModel;

import java.util.List;

/**
 * @ClassName FlowPortService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowPortService extends BaseModelService<FlowPortModel, QueryParams>  {

    /**
     * batchCreatePorts
     *
     * @param networkInfos
     * @param flowActuatorModel
     * @param externalNetId
     */
    void batchCreatePorts(List<FlowNetworkInfo> networkInfos, FlowActuatorModel flowActuatorModel, String externalNetId);

}
