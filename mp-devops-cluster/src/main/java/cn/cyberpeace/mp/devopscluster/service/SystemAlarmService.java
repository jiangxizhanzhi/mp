 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemAlarmService.java.
 // File size 1666.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmDistributionRequest;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.AlarmCountVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmDistributionVo;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsTrendVO;

import java.util.List;

/**
 * @ClassName SystemAlarmService
 * @Description 告警信息service
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SystemAlarmService extends BaseModelService<SystemAlarmModel, QueryParams> {

    /**
     * 处理系统告警
     *
     * @param systemAlarmModels
     */
    void processSystemAlarm(List<SystemAlarmModel> systemAlarmModels);

    /**
     * 当天告警数量统计
     *
     * @return
     */
    List<AlarmCountVO> todayAlarmCount();

    /**
     * listSystemAlarm
     *
     * @param queryParams
     * @return
     */
    ListObjServiceResult<SystemAlarmModel> listSystemAlarm(AlarmQueryParams queryParams);

    /**
     * getSystemAlarmDistribution
     *
     * @param request
     * @return
     */
    ServiceResult<AlarmDistributionVo> getSystemAlarmDistribution(AlarmDistributionRequest request);

}

