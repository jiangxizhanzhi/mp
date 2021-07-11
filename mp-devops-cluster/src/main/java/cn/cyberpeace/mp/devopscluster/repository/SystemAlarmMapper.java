package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.vo.AlarmCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
@Repository
@ModelClass(SystemAlarmModel.class)
public interface SystemAlarmMapper extends BaseModelMapper<SystemAlarmModel> {

    
    List<AlarmCountVO> todayAlarmCount(@Param("currentTime")LocalDateTime currentTime);

}
