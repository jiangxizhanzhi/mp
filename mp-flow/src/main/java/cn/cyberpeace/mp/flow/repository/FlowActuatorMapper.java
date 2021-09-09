package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowActuatorModel.class)
public interface FlowActuatorMapper extends BaseModelMapper<FlowActuatorModel> {


}
