package cn.cyberpeace.mp.devops.cluster.task;

import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.MonitorStateEnum;
import cn.cyberpeace.mp.devops.cluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devops.cluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devops.cluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devops.cluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devops.cluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devops.cluster.vo.AlarmCountVO;
import cn.cyberpeace.mp.devops.cluster.vo.BizServiceMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.CloudPlatformMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.SystemAlarmVO;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SystemAlarmTask {

    private final SystemAlarmService systemAlarmService;

    private final ServiceMonitorService serviceMonitorService;

    private final DevopsConfig devopsConfig;

    private final AlarmStatisticsService alarmStatisticsService;

    private final HostStateService hostStateService;

    private final HypervisorConverter hypervisorConverter;

    // 默认持续时间
    private static final Integer RUNNING_TIME = 0;

    /**
     * 生成告警信息
     */
    @Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60 * 5)
    private void getSystemAlarm() {
        log.info("====>>>>>>get systemAlarm info task start<<<<<========");
        List<SystemAlarmModel> alarmModels = new ArrayList<>();
        SystemAlarmVO systemAlarmVO = serviceMonitorService.getSystemAlarm();

        // 服务器监控
        List<HostState> allcpNodesHostStates = listAllCPNodeHostStates();

        if (CollectionUtils.isNotEmpty(allcpNodesHostStates)) {
            allcpNodesHostStates.forEach(hostState -> {
                    if (Objects.nonNull(hostState.getDiskPercent()) && hostState.getDiskPercent() >= devopsConfig.getDiskAlarmPercent()) {
                        // 磁盘使用率大于阈值，添加告警
                        SystemAlarmModel serverAlarmModel = SystemAlarmModel.builder()
                                .alarmMsg(String.format("%s disk usage is over %s%%", hostState.getAgentName(), devopsConfig.getDiskAlarmPercent()))
                                .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                                .alarmType(AlarmTypeEnum.SERVER.toValue())
                                .hostname(hostState.getAgentName())
                                .servername(hostState.getAgentName())
                                .runningTime(RUNNING_TIME)
                                .build();
                        alarmModels.add(serverAlarmModel);
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
                    // 添加业务服务告警信息
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

        systemAlarmService.processSystemAlarm(alarmModels);

        // 生成告警统计信息
        this.genAlarmStatistics();

        log.info("====>>>>>>get systemAlarm info task end<<<<<========");
    }

    /**
     * 生成告警统计信息
     */
    private void genAlarmStatistics() {
        log.info("====>>>>>>gen alarmStatistics info task start<<<<<========");
        // 当前告警信息
        List<AlarmCountVO> alarmCountVOs = systemAlarmService.todayAlarmCount();
        // 已有告警数量统计信息
        List<AlarmStatisticsModel> alarmStatisticsModels = alarmStatisticsService.todayAlarmStatistics();
        if (CollectionUtils.isNotEmpty(alarmStatisticsModels)) {
            List<Integer> existAlarmTypeList =
                    alarmStatisticsModels.stream().map(AlarmStatisticsModel::getAlarmType).collect(Collectors.toList());
            List<AlarmCountVO> addAlarmCountVOs =
                    alarmCountVOs.stream().filter(alarmCountVO -> !existAlarmTypeList.contains(alarmCountVO.getAlarmType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(addAlarmCountVOs)) {
                this.addAlarmStatistics(addAlarmCountVOs);
            }

            // 更新统计数量
            List<AlarmCountVO> updateAlarmCountVOs =
                    alarmCountVOs.stream().filter(alarmCountVO -> existAlarmTypeList.contains(alarmCountVO.getAlarmType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(updateAlarmCountVOs)) {
                List<AlarmStatisticsModel> updateAlarmStatisticsVOs = new ArrayList<>();
                alarmStatisticsModels.forEach(alarmStatisticsModel -> updateAlarmCountVOs.forEach(updateAlarmCountVO -> {
                    if (alarmStatisticsModel.getAlarmType().equals(updateAlarmCountVO.getAlarmType())) {
                        alarmStatisticsModel.setAlarmCount(updateAlarmCountVO.getAlarmCount());
                        alarmStatisticsModel.setUpdateTime(LocalDateTime.now());
                        updateAlarmStatisticsVOs.add(alarmStatisticsModel);
                    }
                }));

                // 更新告警统计数量信息
                if (CollectionUtils.isNotEmpty(updateAlarmStatisticsVOs)) {
                    alarmStatisticsService.bulkUpdate(updateAlarmStatisticsVOs);
                }
            }
        } else {
            this.addAlarmStatistics(alarmCountVOs);
        }

        log.info("====>>>>>>gen alarmStatistics info task end<<<<<========");
    }

    /**
     * 新增告警统计信息
     * @param alarmCountVOs
     */
    private void addAlarmStatistics(List<AlarmCountVO> alarmCountVOs) {
        // 新增告警统计信息
        List<AlarmStatisticsModel> addModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(alarmCountVOs)) {
            alarmCountVOs.forEach(alarmCountVO -> {
                AlarmStatisticsModel addModel = AlarmStatisticsModel.builder()
                        .alarmType(alarmCountVO.getAlarmType())
                        .alarmCount(alarmCountVO.getAlarmCount())
                        .alarmDate(LocalDate.now())
                        .build();
                addModelList.add(addModel);
            });
        }

        if (CollectionUtils.isNotEmpty(addModelList)) {
            alarmStatisticsService.bulkCreate(addModelList);
        }
    }


    private List<HostState> listAllCPNodeHostStates() {
        BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO());
        List<String> hostNames = modelPage.getList().stream().map(HypervisorBO::getName).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(hostNames)) {
            return Collections.emptyList();
        }
        List<HostState> allHostStates = new ArrayList<>();
        for (String hostName : hostNames) {
            AgentNameBasedRequest request = new AgentNameBasedRequest();
            request.setPage(1);
            request.setPageSize(1);
            request.setAgentNames(Arrays.asList(hostName));
            List<HostState> hostStates  = hostStateService.listHostStatesByAgentName(request);
            if (CollectionUtil.isNotEmpty(hostStates)) {
                allHostStates.add(hostStates.get(0));
            }
        }

        return allHostStates;
    }
}

