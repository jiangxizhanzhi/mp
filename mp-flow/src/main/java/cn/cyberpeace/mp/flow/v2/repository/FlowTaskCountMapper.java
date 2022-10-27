package cn.cyberpeace.mp.flow.v2.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowTaskCountModel.class)
public interface FlowTaskCountMapper extends BaseModelMapper<FlowTaskCountModel> {
}
