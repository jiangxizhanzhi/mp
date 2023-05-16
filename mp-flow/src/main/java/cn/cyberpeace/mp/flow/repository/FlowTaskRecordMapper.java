package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticalCondition;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
	
}
