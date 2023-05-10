package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;

/**
 * @Description 流量任务配置service
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
public interface FlowTaskConfService extends BaseModelService<FlowTaskConfModel, QueryParams> {

    /**
     * 根据taskId查询配置信息
     * @param taskId
     * @return
     */
    FlowTaskConfModel findByTaskId(String taskId);

}

