 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemAlarmMapper.java.
 // File size 974.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName SystemAlarmMapper
 * @Description 告警信息mapper
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(SystemAlarmModel.class)
public interface SystemAlarmMapper extends BaseModelMapper<SystemAlarmModel> {

    /**
     * 当天告警数量统计
     * @return
     */
    List<AlarmCountVO> todayAlarmCount(@Param("currentTime")LocalDateTime currentTime);

}
