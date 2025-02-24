// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowActuatorMapper.java.
// File size 956.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.response.ActuatorSceneName;
import cn.cyberpeace.mp.flow.response.ActuatorStatistic;
import cn.cyberpeace.mp.flow.response.ActuatorTaskCount;
import cn.cyberpeace.mp.flow.response.RateCount;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FlowActuatorMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowActuatorModel.class)
public interface FlowActuatorMapper extends BaseModelMapper<FlowActuatorModel> {
    List<ActuatorSceneName> groupByScene();

    List<ActuatorStatistic> countByType();

    List<ActuatorTaskCount> countTask();

    RateCount totalRate(LocalDateTime startTime, LocalDateTime endTime);
}
