package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description 流量执行任务mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Mapper
@Repository
@ModelClass(FlowTaskTaskModel.class)
public interface FlowTaskTaskMapper extends BaseModelMapper<FlowTaskTaskModel> {
	
}
