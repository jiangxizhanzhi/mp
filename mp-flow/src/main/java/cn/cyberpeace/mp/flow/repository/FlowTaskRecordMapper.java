package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 流量任务记录mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Mapper
@Repository
@ModelClass(FlowTaskRecordModel.class)
public interface FlowTaskRecordMapper extends BaseModelMapper<FlowTaskRecordModel> {

    /**
     * 根据任务id分组统计实时速率
     * @param taskIds
     * @param conditions
     * @return
     */
    List<FlowRealTimeRate> realTimeRateByTaskId(@Param("taskIds") List<String> taskIds, @Param("conditions") List<FlowStatisticalCondition> conditions);

    /**
     * 仿真概况
     * @param sceneId
     * @return
     */
    FlowSummary queryFlowSummary(@Param("sceneId") String sceneId);

    /**
     * 流量最开始启动时间
     * @param sceneId
     * @return
     */
    LocalDateTime getFirstTime(@Param("sceneId") String sceneId);

    /**
     * 根据任务id统计总数据
     * @param sceneId
     * @param conditions
     * @return
     */
    List<FlowStatisticsTemp> statisticsTaskId(@Param("sceneId") String sceneId, @Param("conditions") List<FlowStatisticalCondition> conditions);

    /**
     * 查询节点实时速率，按照速率倒序排列
     * @param sceneId
     * @return
     */
    List<FlowNodeTxBps> queryNodeTxBps(@Param("sceneId") String sceneId);

}
