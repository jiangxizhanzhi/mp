package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName FlowProtocolMapper
 * @Description 流量协议mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:22:12
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowProtocolModel.class)
public interface FlowProtocolMapper extends BaseModelMapper<FlowProtocolModel> {
	
}
