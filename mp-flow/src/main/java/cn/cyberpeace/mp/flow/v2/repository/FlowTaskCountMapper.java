package cn.cyberpeace.mp.flow.v2.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskCountQueryBO;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountResultModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowTaskCountModel.class)
public interface FlowTaskCountMapper extends BaseModelMapper<FlowTaskCountModel> {
    List<FlowTaskCountResultModel> countTask(String flowGeneratorId, List<FlowTaskCountQueryBO> list);

    List<FlowTaskCountResultModel> countFlow(String flowGeneratorId, String taskRecordId, List<FlowTaskCountQueryBO> list);
}
