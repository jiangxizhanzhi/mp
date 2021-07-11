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


@Mapper
@Repository
@ModelClass(FlowTaskRecordModel.class)
public interface FlowTaskRecordMapper extends BaseModelMapper<FlowTaskRecordModel> {

    
    List<FlowRealTimeRate> realTimeRateByTaskId(@Param("taskIds") List<String> taskIds, @Param("conditions") List<FlowStatisticalCondition> conditions);

    
    FlowSummary queryFlowSummary(@Param("sceneId") String sceneId);

    
    LocalDateTime getFirstTime(@Param("sceneId") String sceneId);

    
    List<FlowStatisticsTemp> statisticsTaskId(@Param("sceneId") String sceneId, @Param("conditions") List<FlowStatisticalCondition> conditions);

    
    List<FlowStatisticsTemp> statisticsTaskIdContainOld(@Param("sceneId") String sceneId, @Param("conditions") List<FlowStatisticalCondition> conditions);

    
    List<FlowNodeTxBps> queryNodeTxBps(@Param("sceneId") String sceneId);

}
