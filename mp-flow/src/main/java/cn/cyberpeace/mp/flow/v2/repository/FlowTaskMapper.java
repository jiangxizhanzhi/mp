package cn.cyberpeace.mp.flow.v2.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository(value = "v2FlowTaskMapper")
@ModelClass(FlowTaskModel.class)
public interface FlowTaskMapper extends BaseModelMapper<FlowTaskModel>  {
}
