package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmTrendRequest;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devopscluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsTrendVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;
import cn.cyberpeace.mp.devopscluster.vo.SystemAlarmVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Description 告警信息controller
 * @Author qiaokang
 * @Date 2022-07-18 17:35:04
 */
@Slf4j
@Api(tags = "告警信息接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/system_alarm")
public class SystemAlarmController extends BaseModelController<SystemAlarmModel, QueryParams, SystemAlarmService> {

    @Autowired
    public SystemAlarmController(SystemAlarmService systemAlarmService) {
        setModelService(systemAlarmService);
    }

    @Autowired
    private ServiceMonitorService serviceMonitorService;

    @Autowired
    private AlarmStatisticsService alarmStatisticsService;

    /**
     * 分页查询告警信息
     * @param queryParams
     * @return
     */
    @ApiOperation("分页查询告警信息")
    @GetMapping("/page")
    public ListObjServiceResult<SystemAlarmModel> page(QueryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            // 默认按时间降序
            queryParams.setOrders(new String[]{"-updateTime"});
        }
        // 只查询生效中的告警
        queryParams.setSearchFields(new String[]{"alarmStatus"});
        queryParams.setSearch(String.valueOf(AlarmStatusEnum.ENABLED.toValue()));
        ListObjServiceResult<SystemAlarmModel> listResult = getModelService().list(queryParams).asResult();
        List<SystemAlarmModel> systemAlarmModels = listResult.getData().getList();
        this.changeServiceName(systemAlarmModels);
        return listResult;
    }

    /**
     * 替换服务名称
     * @param systemAlarmModels
     */
    private void changeServiceName(List<SystemAlarmModel> systemAlarmModels) {
        if (CollectionUtils.isNotEmpty(systemAlarmModels)) {
            systemAlarmModels.forEach(model -> model.setServername(MessageCommon.get(model.getServername())));
        }
    }

    /**
     * 系统告警统计信息
     * @return
     */
    @ApiOperation("系统告警统计信息")
    @GetMapping("/statistics")
    public ServiceResult<SystemAlarmVO> getSystemAlarm(@RequestParam(required = false) Integer days) {
        SystemAlarmVO systemAlarm = serviceMonitorService.getSystemAlarm();

        if (Objects.isNull(days) || days < 1) {
            days = 30;
        }

        // 服务告警
        List<AlarmStatisticsVO> serviceAlarmList = alarmStatisticsService.serviceAlarmStatistics(days);
        if (CollectionUtils.isNotEmpty(serviceAlarmList)) {
            systemAlarm.setServiceAlarmList(serviceAlarmList);
        }

        // 设备告警
        List<AlarmStatisticsVO> serverAlarmList = alarmStatisticsService.serverAlarmStatistics(days);
        if (CollectionUtils.isNotEmpty(serverAlarmList)) {
            systemAlarm.setServerAlarmList(serverAlarmList);
        }
        return ResultCommon.success(systemAlarm);
    }


    /**
     * 系统告警统计信息
     * @return
     */
    @ApiOperation("系统告警统计信息")
    @GetMapping("/trend")
    public ServiceResult<List<AlarmStatisticsTrendVO>> getSystemAlarmTrend(AlarmTrendRequest trendRequest) {
        // 告警趋势
        //List<AlarmStatisticsVO> serviceAlarmList = alarmStatisticsService.serviceAlarmStatistics(days);
        //if (CollectionUtils.isNotEmpty(serviceAlarmList)) {
        //    systemAlarm.setServiceAlarmList(serviceAlarmList);
        //}

        //Integer[] types = {0, 1};
        var result = alarmStatisticsService.alarmTrend(trendRequest.getTypes(), trendRequest.getUnit(), trendRequest.getCount(), null, null);
        return ResultCommon.success(result);
    }

}
