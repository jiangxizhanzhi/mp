package cn.cyberpeace.mp.devops.cluster.task;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.MonitorStateEnum;
import cn.cyberpeace.mp.devops.cluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devops.cluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devops.cluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devops.cluster.vo.BizServiceMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.CloudPlatformMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.ServerMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.SystemAlarmVO;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class SystemAlarmTask {

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Autowired
    private ServiceMonitorService serviceMonitorService;

    @Autowired
    private DevopsConfig devopsConfig;

    // 默认持续时间
    private static final Integer RUNNING_TIME = 10;

    @Scheduled(fixedRate = 1000 * 60 * 5)
    private void getSystemAlarm() {
        log.info("====>>>>>>get systemAlarm info task start<<<<<========");
        List<SystemAlarmModel> alarmModels = new ArrayList<>();
        SystemAlarmVO systemAlarmVO = serviceMonitorService.getSystemAlarm();

        // 服务器监控
        ServerMonitorVO serverMonitorVO = systemAlarmVO.getServerMonitorVO();
        List<HypervisorBO> serverMonitors = serverMonitorVO.getServerMonitors();
        if (CollectionUtils.isNotEmpty(serverMonitors)) {
            serverMonitors.forEach(serverMonitor -> {
                if (Objects.nonNull(serverMonitor.getDisk()) && Objects.nonNull(serverMonitor.getDiskUsed())) {
                    BigDecimal diskPercent = BigDecimal.valueOf(serverMonitor.getDiskUsed())
                            .divide(BigDecimal.valueOf(serverMonitor.getDisk()),2, RoundingMode.DOWN)
                            .multiply(BigDecimal.valueOf(100));

                    if (diskPercent.intValue() > devopsConfig.getDiskAlarmPercent()) {
                        // 磁盘使用率大于80%，添加告警
                        SystemAlarmModel serverAlarmModel = SystemAlarmModel.builder()
                                .alarmMsg(String.format("%s disk usage is over %s%%", serverMonitor.getName(), devopsConfig.getDiskAlarmPercent()))
                                .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                                .alarmType(AlarmTypeEnum.SERVER.toValue())
                                .hostname(serverMonitor.getName())
                                .servername(serverMonitor.getName())
                                .runningTime(RUNNING_TIME)
                                .build();
                        alarmModels.add(serverAlarmModel);
                    }
                }
            });
        }

        // 云平台服务监控
        CloudPlatformMonitorVO cpMonitorVO = systemAlarmVO.getCloudPlatformMonitorVO();
        List<ServiceMonitorDto> cpMonitors = cpMonitorVO.getCloudPlatformMonitors();
        if (CollectionUtils.isNotEmpty(cpMonitors)) {
            cpMonitors.forEach(cpMonitor -> {
                if (!MonitorStateEnum.RUNNING.getDesc().equals(cpMonitor.getState())) {
                    // 添加云平台服务告警信息
                    SystemAlarmModel cpAlarmModel = SystemAlarmModel.builder()
                            .alarmMsg(String.format("%s status is error", cpMonitor.getName()))
                            .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                            .alarmType(AlarmTypeEnum.CP.toValue())
                            .hostname(cpMonitor.getHostname())
                            .servername(cpMonitor.getName())
                            .runningTime(RUNNING_TIME)
                            .build();
                    alarmModels.add(cpAlarmModel);
                }
            });
        }

        // 业务服务监控
        BizServiceMonitorVO crMonitorVO = systemAlarmVO.getBizServiceMonitorVO();
        List<ServiceMonitorDto> crMonitors = crMonitorVO.getBizServiceMonitors();
        if (CollectionUtils.isNotEmpty(crMonitors)) {
            crMonitors.forEach(crMonitor -> {
                if (!MonitorStateEnum.RUNNING.getDesc().equals(crMonitor.getState())) {
                    // 添加云平台服务告警信息
                    SystemAlarmModel crAlarmModel = SystemAlarmModel.builder()
                            .alarmMsg(String.format("%s status is error", crMonitor.getName()))
                            .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                            .alarmType(AlarmTypeEnum.CR.toValue())
                            .hostname(crMonitor.getHostname())
                            .servername(crMonitor.getName())
                            .runningTime(RUNNING_TIME)
                            .build();
                    alarmModels.add(crAlarmModel);
                }
            });
        }

        if (CollectionUtils.isNotEmpty(alarmModels)) {
            // 更新告警信息
            systemAlarmService.processSystemAlarm(alarmModels);
        } else {
            log.info("no systemAlarm info to process");
        }

        log.info("====>>>>>>get systemAlarm info task end<<<<<========");
    }
}
