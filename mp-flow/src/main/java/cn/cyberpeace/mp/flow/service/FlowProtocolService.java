 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowProtocolService.java.
 // File size 888.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;

import java.util.List;

/**
 * @ClassName FlowProtocolService
 * @Description 流量协议service
 * @Author qiaokang
 * @Date 2023-04-27 09:22:12
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowProtocolService extends BaseModelService<FlowProtocolModel, QueryParams> {

    /**
     * 根据流量id查询
     *
     * @param flowId
     * @return
     */
    List<FlowProtocolModel> selectByFlowId(String flowId);

    List<String> listFlowProtocols();

    /**
     * 根据流量id删除
     *
     * @param flowId
     */
    void deleteByFlowId(String flowId);

    /**
     * 删除所有数据
     *
     * @return
     */
    int deleteAll();

}

