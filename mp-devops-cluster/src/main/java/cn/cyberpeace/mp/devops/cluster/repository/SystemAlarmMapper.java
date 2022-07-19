package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.devops.cluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description 告警信息mapper
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 */
@Mapper
@Repository
@ModelClass(SystemAlarmModel.class)
public interface SystemAlarmMapper extends BaseModelMapper<SystemAlarmModel> {
	
}
