package cn.cyberpeace.mp.flow.v2.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowTaskRecordModel.class)
public interface FlowTaskRecordMapper extends BaseModelMapper<FlowTaskRecordModel>  {
    
    Long countStartTask();
    
    List<FlowTaskRecordModel> generatorTaskList();
}
