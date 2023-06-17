package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;

import java.util.List;

/**
 * @Description 流量协议service
 * @Author qiaokang
 * @Date 2023-04-27 09:22:12
 */
public interface FlowProtocolService extends BaseModelService<FlowProtocolModel, QueryParams> {

    /**
     * 根据流量id查询
     * @param flowId
     * @return
     */
    List<FlowProtocolModel> selectByFlowId(String flowId);

    /**
     * 根据流量id删除
     * @param flowId
     */
    void deleteByFlowId(String flowId);

    /**
     * 删除所有数据
     * @return
     */
    int deleteAll();

}

