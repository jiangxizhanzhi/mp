package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowActuatorQueryParams;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowActuatorModel.class)
public interface FlowActuatorMapper extends BaseModelMapper<FlowActuatorModel> {

    int countForPage(FlowActuatorQueryParams queryParams);

    List<FlowActuatorResponse> page(FlowActuatorQueryParams queryParams);
}
