package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.vo.AlarmCountVO;

import java.util.List;

/**
 * @Description 告警信息service
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 */
public interface SystemAlarmService extends BaseModelService<SystemAlarmModel, QueryParams> {

    /**
     * 处理系统告警
     * @param systemAlarmModels
     */
    void processSystemAlarm(List<SystemAlarmModel> systemAlarmModels);

    /**
     * 当天告警数量统计
     * @return
     */
    List<AlarmCountVO> todayAlarmCount();

}

