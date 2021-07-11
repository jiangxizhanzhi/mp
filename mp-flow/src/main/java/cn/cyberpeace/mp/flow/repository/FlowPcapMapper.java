package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(FlowPcapModel.class)
public interface FlowPcapMapper extends BaseModelMapper<FlowPcapModel> {
	
}
