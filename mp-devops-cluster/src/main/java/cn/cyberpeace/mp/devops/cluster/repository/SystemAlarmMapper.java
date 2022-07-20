package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devops.cluster.vo.AlarmCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 告警信息mapper
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 */
@Mapper
@Repository
@ModelClass(SystemAlarmModel.class)
public interface SystemAlarmMapper extends BaseModelMapper<SystemAlarmModel> {

    /**
     * 当天告警数量统计
     * @return
     */
    List<AlarmCountVO> todayAlarmCount();

}
