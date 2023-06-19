package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;

import java.util.List;

/**
 * @Description 流量包service
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
public interface FlowPcapService extends BaseModelService<FlowPcapModel, QueryParams> {

    /**
     * 统计流量包总数
     * @return
     */
    int countAll();

    /**
     * 删除所有数据
     * @return
     */
    int deleteAll();

    /**
     * 根据flowId查询
     * @param flowId
     * @return
     */
    FlowPcapModel getByFlowId(String flowId);

    /**
     * 查询所有流量包
     * @return
     */
    List<FlowPcapModel> listAll();

}

