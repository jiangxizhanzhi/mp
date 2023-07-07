package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;

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

    /**
     * 根据场景id查询节点列表
     * @param sceneId
     * @return
     */
    List<FlowTaskNode> nodeList(String sceneId);

    /**
     * 新增流量任务
     * @param details
     * @param sceneId
     * @param actuatorId
     */
    void addTask(List<AddFlowTaskTaskInfo> details, String sceneId, String actuatorId);

    /**
     * 根据节点id和流量id查询任务
     * @param nodeId
     * @param flowId
     * @return
     */
    List<FlowTaskTaskModel> listByNodeIdAndFlowId(String nodeId, String flowId);

    /**
     * 根据节点id、scendId查询任务
     * @param nodeId
     * @param sceneId
     * @return
     */
    List<FlowTaskTaskModel> listByNodeIdAndSceneId(String nodeId, String sceneId);

    /**
     * 根据节点id、scendId删除任务
     * @param nodeId
     * @param sceneId
     * @return
     */
    void deleteByNodeIdAndSceneId(String nodeId, String sceneId);

    /**
     * 根据场景id和运行状态查询
     * @param nodeId
     * @param taskStatus
     * @return
     */
    List<FlowTaskTaskModel> listBySceneIdAndStatus(String nodeId, int taskStatus);

    /**
     * 根据场景实例id查询
     * @param sceneId
     * @return
     */
    List<FlowTaskTaskModel> listBySceneId(String sceneId);

    /**
     * 条件查询任务
     * @param request
     * @return
     */
    List<FlowTaskTaskModel> listByCondition(FlowTaskPageQueryParams request);

}

