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


public interface SystemAlarmService extends BaseModelService<SystemAlarmModel, QueryParams> {

    
    void processSystemAlarm(List<SystemAlarmModel> systemAlarmModels);

    
    List<AlarmCountVO> todayAlarmCount();

    ListObjServiceResult<SystemAlarmModel> listSystemAlarm(AlarmQueryParams queryParams);

    ServiceResult<AlarmDistributionVo> getSystemAlarmDistribution(AlarmDistributionRequest request);

}

