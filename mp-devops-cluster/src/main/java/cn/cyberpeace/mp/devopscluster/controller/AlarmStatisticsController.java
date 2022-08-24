package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description 告警统计信息controller
 * @Author qiaokang
 * @Date 2022-07-19 16:17:26
 */
@Slf4j
@Api(tags = "告警统计信息接口")
//@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/alarm_statistics")
public class AlarmStatisticsController extends BaseModelController<AlarmStatisticsModel, QueryParams, AlarmStatisticsService> {

    @Autowired
    public AlarmStatisticsController(AlarmStatisticsService alarmStatisticsService) {
        setModelService(alarmStatisticsService);
    }

}
