package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmDistributionRequest;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmQueryParams;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmTrendRequest;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devopscluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devopscluster.vo.AlarmDistributionVo;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsTrendVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;
import cn.cyberpeace.mp.devopscluster.vo.SystemAlarmVO;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


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

    
    @ApiOperation("分页查询告警信息")
    @GetMapping("/page")
    public ListObjServiceResult<SystemAlarmModel> page(AlarmQueryParams queryParams) {
        queryParams.ensure();
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            
            String updateTime = BeanUtils.getBeanGetterFieldName(SystemAlarmModel::getUpdateTime);
            updateTime = StrUtil.toUnderlineCase(updateTime);
            queryParams.setOrders(new String[]{"-" + updateTime});
        }
        
        if (ArrayUtil.isEmpty(queryParams.getAlarmStatus())) {
            queryParams.setAlarmStatus(new Integer[]{AlarmStatusEnum.ENABLED.toValue()});
        }

        ListObjServiceResult<SystemAlarmModel> listResult = getModelService().listSystemAlarm(queryParams).asResult();
        List<SystemAlarmModel> systemAlarmModels = listResult.getData().getList();
        this.changeServiceName(systemAlarmModels);
        return listResult;
    }

    
    private void changeServiceName(List<SystemAlarmModel> systemAlarmModels) {
        if (CollectionUtils.isNotEmpty(systemAlarmModels)) {
            systemAlarmModels.forEach(model -> {
                String originServerName = model.getServername();
                String newName = MessageCommon.get(model.getServername());
                model.setServername(newName);
                model.setAlarmMsg(StringUtils.replaceOnce(model.getAlarmMsg(), originServerName, newName));
            });
        }
    }

    
    @ApiOperation("系统告警统计信息")
    @GetMapping("/statistics")
    public ServiceResult<SystemAlarmVO> getSystemAlarm(@RequestParam(required = false) Integer days) {
        SystemAlarmVO systemAlarm = serviceMonitorService.getSystemAlarm();

        if (Objects.isNull(days) || days < 1) {
            days = 30;
        }

        
        List<AlarmStatisticsVO> serviceAlarmList = alarmStatisticsService.serviceAlarmStatistics(days);
        if (CollectionUtils.isNotEmpty(serviceAlarmList)) {
            systemAlarm.setServiceAlarmList(serviceAlarmList);
        }

        
        List<AlarmStatisticsVO> serverAlarmList = alarmStatisticsService.serverAlarmStatistics(days);
        if (CollectionUtils.isNotEmpty(serverAlarmList)) {
            systemAlarm.setServerAlarmList(serverAlarmList);
        }
        return ResultCommon.success(systemAlarm);
    }


    
    @ApiOperation("系统告警趋势")
    @GetMapping("/trend")
    public ServiceResult<List<AlarmStatisticsTrendVO>> getSystemAlarmTrend(AlarmTrendRequest trendRequest) {
        var result = alarmStatisticsService.alarmTrend(trendRequest.getTypes(), trendRequest.getUnit(),
                trendRequest.getCount(), trendRequest.getStartTime(), trendRequest.getEndTime());
        return ResultCommon.success(result);
    }

    
    @ApiOperation("系统告警分布")
    @GetMapping("/distribution")
    public ServiceResult<AlarmDistributionVo> getSystemAlarmDistribution(AlarmDistributionRequest request) {
        
        
        
        if (Objects.isNull(request.getEndTime())) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            request.setEndTime(now);
        }
        if (Objects.isNull(request.getStartTime())) {
            request.setStartTime(LocalDateTime.of(request.getEndTime().toLocalDate().minusWeeks(1), LocalTime.MAX));
        }
        return getModelService().getSystemAlarmDistribution(request);
    }

}
