package cn.cyberpeace.mp.devopscluster.task;

import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterConstant;
import cn.cyberpeace.mp.devopscluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkSubnetBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devopscluster.enums.AlarmTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.MonitorStateEnum;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devopscluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devopscluster.vo.AlarmCountVO;
import cn.cyberpeace.mp.devopscluster.vo.BizServiceMonitorVO;
import cn.cyberpeace.mp.devopscluster.vo.CloudPlatformMonitorVO;
import cn.cyberpeace.mp.devopscluster.vo.SystemAlarmVO;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final DevopsClusterConfig devopsClusterConfig;

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
        List<SystemAlarmModel> alarmModels = new ArrayList<>();
        SystemAlarmVO systemAlarmVO = serviceMonitorService.getSystemAlarm();

        // 服务器监控
        List<HostState> allcpNodesHostStates = listAllCPNodeHostStates();

        if (CollectionUtils.isNotEmpty(allcpNodesHostStates)) {
            allcpNodesHostStates.forEach(hostState -> {
                    if (Objects.nonNull(hostState.getDiskPercent()) && hostState.getDiskPercent() >= devopsClusterConfig.getDiskAlarmPercent()) {
                        // 磁盘使用率大于阈值，添加告警
                        SystemAlarmModel serverAlarmModel = SystemAlarmModel.builder()
                                .alarmMsg(String.format("%s disk usage is over %s%%", hostState.getAgentName(), devopsClusterConfig.getDiskAlarmPercent()))
                                .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                                .alarmType(AlarmTypeEnum.SERVER.toValue())
                                .hostname(hostState.getAgentName())
                                .servername(DevopsClusterConstant.ALARM_SERVER_DISK_USAGE)
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

        // todo 网络监控
        List<NetworkBO> networks = systemAlarmVO.getNetworkMonitorVO().getNetworks();

        if (CollectionUtils.isNotEmpty(networks)) {
            networks.forEach(net -> {
                // 可用IP告警
                if (CollectionUtil.isNotEmpty(net.getSubnets())) {
                    List<NetworkSubnetBO> subnets = net.getSubnets();

                    for (NetworkSubnetBO subnet : subnets) {
                        if (Objects.nonNull(subnet.getFreeIps()) && Objects.nonNull(subnet.getUsedIps())) {
                            int totalIpCnt = subnet.getFreeIps() + subnet.getUsedIps();
                            double ipUsagePercent = new BigDecimal(subnet.getUsedIps()).divide(new BigDecimal(totalIpCnt), 5, RoundingMode.HALF_UP).doubleValue();
                            if (ipUsagePercent * 100 > devopsClusterConfig.getNetIPUsageAlarmPercent()) {
                                // 添加网络IP使用率告警信息
                                SystemAlarmModel networkIPAlarmModel = SystemAlarmModel.builder()
                                        .alarmMsg(String.format("%s network IP usage percent is over %s%%", net.getName(), devopsClusterConfig.getNetIOUsageAlarmPercent()))
                                        .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                                        .alarmType(AlarmTypeEnum.NETWORK_IP_USAGE.toValue())
                                        .servername(DevopsClusterConstant.ALARM_NET_IP_USAGE)
                                        .runningTime(RUNNING_TIME)
                                        .build();
                                alarmModels.add(networkIPAlarmModel);
                            }
                        }
                    }


                    // 网络IO告警
                    BigDecimal uploadSpeed = new BigDecimal(0);
                    if (Objects.nonNull(net.getUploadSpeed())) {
                        uploadSpeed = new BigDecimal(net.getUploadSpeed());
                    }

                    BigDecimal downloadSpeed = new BigDecimal(0);
                    if (Objects.nonNull(net.getDownloadSpeed())) {
                        downloadSpeed = new BigDecimal(net.getDownloadSpeed());
                    }

                    double uploadPercent = uploadSpeed.divide(new BigDecimal(1000 * 1024), 5, RoundingMode.HALF_UP).doubleValue();
                    double downloadPercent = downloadSpeed.divide(new BigDecimal(1000 * 1024), 5, RoundingMode.HALF_UP).doubleValue();
                    if (uploadPercent * 100 > devopsClusterConfig.getNetIOUsageAlarmPercent() || downloadPercent * 100 > devopsClusterConfig.getNetIOUsageAlarmPercent()) {
                        // 添加业务服务告警信息
                        SystemAlarmModel networkAlarmModel = SystemAlarmModel.builder()
                                .alarmMsg(String.format("%s network IO percent is over %s%%", net.getName(), devopsClusterConfig.getNetIOUsageAlarmPercent()))
                                .alarmStatus(AlarmStatusEnum.ENABLED.toValue())
                                .alarmType(AlarmTypeEnum.NETWORK_IO_USAGE.toValue())
                                .servername(DevopsClusterConstant.ALARM_NET_IO_USAGE)
                                .runningTime(RUNNING_TIME)
                                .build();
                        alarmModels.add(networkAlarmModel);
                    }

                }
            });
        }

        systemAlarmService.processSystemAlarm(alarmModels);

        // 生成告警统计信息
        this.genAlarmStatistics();
    }

    /**
     * 生成告警统计信息
     */
    private void genAlarmStatistics() {
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

