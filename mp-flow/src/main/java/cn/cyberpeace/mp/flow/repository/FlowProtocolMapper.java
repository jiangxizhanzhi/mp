package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(FlowProtocolModel.class)
public interface FlowProtocolMapper extends BaseModelMapper<FlowProtocolModel> {
	
}
