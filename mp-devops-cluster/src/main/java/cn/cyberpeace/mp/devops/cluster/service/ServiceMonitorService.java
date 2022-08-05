package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ServiceRestartStatusDto;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.enums.MonitorStateEnum;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devops.cluster.repository.SystemAlarmMapper;
import cn.cyberpeace.mp.devops.cluster.request.BizServicePageRequest;
import cn.cyberpeace.mp.devops.cluster.request.CloudPlatformPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.BizServiceMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.CloudPlatformMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.ServerMonitorVO;
import cn.cyberpeace.mp.devops.cluster.vo.SystemAlarmVO;
import cn.cyberpeace.mp.situationaware.config.MonitorConfig;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import cn.cyberpeace.mp.situationaware.service.ServiceMonitorDataCollectionService;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 服务监控服务类
 * @Author qiaokang
 * @Date：2022/7/15 14:27
 */
@Service
@RequiredArgsConstructor
public class ServiceMonitorService {

    private final HypervisorConverter hypervisorConverter;

    private final ServiceMonitorDataCollectionService serviceMonitorDataCollectionService;

    private final SystemAlarmMapper systemAlarmMapper;

    private final MonitorScriptControlService monitorScriptControlService;

    private final MonitorConfig monitorConfig;

    private final ImagesConverter imagesConverter;

    /**
     * 分页查询业务服务监控数据
     * @param request
     * @return
     */
    public ListObjResult<ServiceMonitorDto> bizServicePage(BizServicePageRequest request) {
        List<String> hostNameList = getActiveHostNameList(request.getHostName());
        if (CollectionUtils.isEmpty(hostNameList)) {
            return new ListObjResult<>(Lists.newArrayList(), 0);
        }

        List<ServiceMonitorDto> serviceMonitorDtos = serviceMonitorDataCollectionService.queryBizServiceMonitorData(hostNameList);
        // 处理重启状态
        processRestartingStatus(serviceMonitorDtos);
        // 处理重启禁用状态
        processRestartRestrict(serviceMonitorDtos, monitorConfig.getBizSrvMonitors());

        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getState));
        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getDisableRestart));
        this.changeServiceName(serviceMonitorDtos);
        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size()).asResult();
        } else {
            return new ListObjResult<ServiceMonitorDto>(Lists.newArrayList(), serviceMonitorDtos.size()).asResult();
        }
    }

    /**
     * 分页查询云平台服务监控数据
     * @param request
     * @return
     */
    public ListObjResult<ServiceMonitorDto> cpPage(CloudPlatformPageRequest request) {
        List<String> hostNameList = getActiveHostNameList(request.getHostName());
        if (CollectionUtils.isEmpty(hostNameList)) {
            return new ListObjResult<>(Lists.newArrayList(), 0);
        }

        List<ServiceMonitorDto> serviceMonitorDtos = serviceMonitorDataCollectionService.queryCPServiceMonitorData(hostNameList);
        // 处理重启状态
        processRestartingStatus(serviceMonitorDtos);
        // 处理重启禁用状态
        processRestartRestrict(serviceMonitorDtos, monitorConfig.getCpSrvMonitors());

        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getState));
        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getDisableRestart));
        this.changeServiceName(serviceMonitorDtos);
        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size()).asResult();
        } else {
            return new ListObjResult<ServiceMonitorDto>(Lists.newArrayList(), serviceMonitorDtos.size()).asResult();
        }
    }

    /**
     * 替换服务名称
     * @param serviceMonitorDtos
     */
    private void changeServiceName(List<ServiceMonitorDto> serviceMonitorDtos) {
        if (CollectionUtils.isNotEmpty(serviceMonitorDtos)) {
            serviceMonitorDtos.forEach(dto -> dto.setName(MessageCommon.get(dto.getName())));
        }
    }

    /**
     * 系统告警
     * @return
     */
    public SystemAlarmVO getSystemAlarm() {
        CloudPlatformMonitorVO cloudPlatformMonitorVO = new CloudPlatformMonitorVO();
        BizServiceMonitorVO bizServiceMonitorVO = new BizServiceMonitorVO();
        ServerMonitorVO serverMonitorVO = new ServerMonitorVO();

        // 查询当前生效的告警信息
        List<SystemAlarmModel> enabledAlarms =
                systemAlarmMapper.list(new HumanCondition().filterEqual(SystemAlarmModel::getAlarmStatus,
                        AlarmStatusEnum.ENABLED.toValue()));
        if (CollectionUtils.isNotEmpty(enabledAlarms)) {
            Long cpAlarmCount = enabledAlarms.stream().filter(alarm -> alarm.getAlarmType() == AlarmTypeEnum.CP.toValue()).count();
            Long crAlarmCount = enabledAlarms.stream().filter(alarm -> alarm.getAlarmType() == AlarmTypeEnum.CR.toValue()).count();
            Long serverAlarmCount = enabledAlarms.stream().filter(alarm -> alarm.getAlarmType() == AlarmTypeEnum.SERVER.toValue()).count();
            cloudPlatformMonitorVO.setAlarmCount(cpAlarmCount.intValue());
            bizServiceMonitorVO.setAlarmCount(crAlarmCount.intValue());
            serverMonitorVO.setAlarmCount(serverAlarmCount.intValue());
        }

        // 服务器监控
        List<String> hostNameList = new ArrayList<>();
        BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO(100, 1));
        if (CollectionUtils.isNotEmpty(modelPage.getList())) {
            // 获取所有物理主机服务状态
            CloudPlatformPageResult<ServiceStatus> serviceStatusModel = imagesConverter.listServiceStatus();
            modelPage.getList().forEach(model -> {
                // 忽略DOWN状态的节点查询
                if (Objects.equals(CloudPlatformEnum.UP.getState(), model.getState())) {
                    hostNameList.add(model.getName());
                }
                if (CollectionUtils.isNotEmpty(serviceStatusModel.getList())) {
                    serviceStatusModel.getList().forEach(serviceModel -> {
                        if (model.getName().equals(serviceModel.getHost())) {
                            model.setServiceStatus(serviceModel.isStatus());
                        }
                    });
                }
            });

            int totalCount = modelPage.getTotal();
            int errorCount = (int )modelPage.getList().stream().filter(model -> !model.isServiceStatus()).count();
            serverMonitorVO.setServerMonitors(modelPage.getList());
            serverMonitorVO.setErrorCount(errorCount);
            serverMonitorVO.setTotalCount(totalCount);

            // 云平台服务监控
            List<ServiceMonitorDto> cpMonitorDtos = serviceMonitorDataCollectionService.queryCPServiceMonitorData(hostNameList);
            processRestartingStatus(cpMonitorDtos);

            if (CollectionUtils.isNotEmpty(cpMonitorDtos)) {
                cloudPlatformMonitorVO.setCloudPlatformMonitors(cpMonitorDtos);
                int cpTotalCount = cpMonitorDtos.size();
                int cpErrorCount = (int) cpMonitorDtos.stream().filter(cpDto -> !MonitorStateEnum.RUNNING.getDesc().equals(cpDto.getState()) && !MonitorStateEnum.RESTARTING.getDesc().equals(cpDto.getState())).count();
                cloudPlatformMonitorVO.setTotalCount(cpTotalCount);
                cloudPlatformMonitorVO.setErrorCount(cpErrorCount);
            }

            // 业务服务监控
            List<ServiceMonitorDto> bizMonitorDtos = serviceMonitorDataCollectionService.queryBizServiceMonitorData(hostNameList);
            processRestartingStatus(bizMonitorDtos);

            if (CollectionUtils.isNotEmpty(bizMonitorDtos)) {
                bizServiceMonitorVO.setBizServiceMonitors(bizMonitorDtos);
                int bizTotalCount = bizMonitorDtos.size();
                int bizErrorCount = (int) bizMonitorDtos.stream().filter(bizDto -> !MonitorStateEnum.RUNNING.getDesc().equals(bizDto.getState()) && !MonitorStateEnum.RESTARTING.getDesc().equals(bizDto.getState())).count();
                bizServiceMonitorVO.setTotalCount(bizTotalCount);
                bizServiceMonitorVO.setErrorCount(bizErrorCount);
            }
        }

        return new SystemAlarmVO(cloudPlatformMonitorVO, bizServiceMonitorVO, serverMonitorVO);
    }



    /**
     * 如果在重启有效期内，状态为不正常的状态的，统一替换为重启状态
     * @param serviceMonitorDtos
     */
    private void processRestartingStatus(List<ServiceMonitorDto> serviceMonitorDtos) {
        TimedCache<String, ServiceRestartStatusDto> timedCache = monitorScriptControlService.getStatusCache();
        if (CollectionUtil.isEmpty(timedCache)) {
            return;
        }

        for (ServiceMonitorDto dto : serviceMonitorDtos) {
            String cacheKey = dto.getHostname() + "_" + dto.getServiceName();
            ServiceRestartStatusDto statusDto = timedCache.get(cacheKey);
            if (Objects.isNull(statusDto)) {
                continue;
            }
            if (Objects.nonNull(dto.getScanTime()) && Objects.nonNull(statusDto.getRestartTime())) {

                if (dto.getScanTime().isAfter(statusDto.getRestartTime().plusSeconds(5)) && Objects.equals(dto.getState(), MonitorStateEnum.RUNNING.getDesc())) {
                    // 重启之后的状态（重启时间延迟5秒，给脚本反应时间），且为RUNNING状态，则清除缓存
                    timedCache.remove(dto.getName());
                } else {
                    // 否则替换状态为RESTARTING
                    dto.setState(MonitorStateEnum.RESTARTING.getDesc());
                    dto.setUptime(0L);
                }
            }
        }
    }


    private List<String> getActiveHostNameList(String hostName) {
        List<String> hostNameList = new ArrayList<>();

        BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO(100, 1));
        // 忽略DOWN状态的节点查询
        List<String> activeHostNames = modelPage.getList().stream()
                .filter(m -> Objects.equals(CloudPlatformEnum.UP.getState(), m.getState()))
                .map(HypervisorBO::getName)
                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(activeHostNames)) {
            if (StringUtils.isNotBlank(hostName)) {
                if (activeHostNames.contains(hostName)) {
                    hostNameList.add(hostName);
                }
            } else {
                hostNameList.addAll(activeHostNames);
            }
        }

        return hostNameList;
    }


    /**
     * 判断是否禁用重启按钮，该方法需要在processRestartingStatus()方法之后调用
     * 如果某个服务在限制列表中，且状态为RUNNING或者RESTARTING，则禁止重启，既disableRestart=true
     * @param serviceMonitorDtos
     * @param monitorProbes
     */
    private void processRestartRestrict(List<ServiceMonitorDto> serviceMonitorDtos, List<MonitorConfig.MonitorProbe> monitorProbes) {
        List<String> allRestrictSrvs = new ArrayList<>();
        monitorProbes.forEach(p -> {
            if (CollectionUtil.isNotEmpty(p.getRestartRestrictList())) {
                allRestrictSrvs.addAll(p.getRestartRestrictList());
            }
        });

        for (ServiceMonitorDto dto : serviceMonitorDtos) {
            dto.setDisableRestart(Boolean.FALSE);

            if (Objects.equals(MonitorStateEnum.RESTARTING.getDesc(), dto.getState())) {
                // 重启中，重启按钮置灰
                dto.setDisableRestart(Boolean.TRUE);
            }

            if (CollectionUtil.isNotEmpty(allRestrictSrvs)) {
                if (Objects.equals(MonitorStateEnum.RUNNING.getDesc(), dto.getState()) && allRestrictSrvs.contains(dto.getName())) {
                    // 状态为RUNNING，且在限制列表中，则禁止重启，既disableRestart=true
                    dto.setDisableRestart(Boolean.TRUE);
                }
            }
        }
    }

}
