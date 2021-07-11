package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowTaskModel.class)
public interface FlowTaskMapper extends BaseModelMapper<FlowTaskModel> {

    
    List<FlowTaskModel> listByFlowIds(@Param("flowIds") List<String> flowIds);

    
    int increaseRunningCount(@Param("taskId") String taskId);

    
    int updateTaskInfo(FlowTaskModel flowTaskModel);

    
    int batchUpdateTaskStatus(@Param("taskStatus") Integer taskStatus, @Param("runCount") Boolean runCount, @Param("taskIds") List<String> taskIds);

}
