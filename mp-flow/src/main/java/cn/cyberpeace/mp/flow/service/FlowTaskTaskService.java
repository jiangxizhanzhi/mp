package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;

import java.util.List;

/**
 * @Description 流量执行任务service
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
public interface FlowTaskTaskService extends BaseModelService<FlowTaskTaskModel, QueryParams> {

    /**
     * 更新任务状态
     * @param taskId
     * @param taskStatus
     * @return
     */
    int updateTaskStatus(String taskId, int taskStatus);

    /**
     * 批量更新任务状态
     * @param taskIds
     * @param taskStatus
     * @return
     */
    int batchUpdateTaskStatus(List<String> taskIds, int taskStatus);

    /**
     * 根据任务id集合批量查询
     * @param taskIds
     * @return
     */
    List<FlowTaskTaskModel> listByTaskIds(List<String> taskIds);

}

