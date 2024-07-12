 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowActuatorService.java.
 // File size 1795.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowActuatorQueryParams;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.response.SceneInstanceResponse;

import java.util.List;

/**
 * @ClassName FlowActuatorService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowActuatorService extends BaseModelService<FlowActuatorModel, FlowActuatorQueryParams> {


    /**
     * delExecutor
     *
     * @param actuatorId
     */
    void delExecutor(String actuatorId);

    /**
     * 分页获取流量发生器信息
     *
     * @author erZhu
     * @date 2024/6/24 10:09
     */
    ListObjServiceResult<FlowActuatorResponse> page(FlowActuatorQueryParams queryParams);

    /**
     * 批量启动流量发生器
     *
     * @author erZhu
     * @date 2024/6/24 11:24
     */
    void batchStart(List<String> actuatorIds);

    /**
     * 批量暂停流量发生器
     *
     * @author erZhu
     * @date 2024/6/24 11:24
     */
    void batchPause(List<String> actuatorIds);

    /**
     * 批量重启流量发生器
     *
     * @author erZhu
     * @date 2024/6/24 11:24
     */
    void batchReboot(List<String> actuatorIds);

    /**
     * 模糊查询场景实例列表
     *
     * @author erZhu
     * @date 2024/6/25 10:17
     */
    ListObjServiceResult<SceneInstanceResponse> listSceneByFuzzy(String name, int limit);
}
