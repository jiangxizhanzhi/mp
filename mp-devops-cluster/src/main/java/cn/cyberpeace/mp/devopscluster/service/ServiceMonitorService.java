package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devopscluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.ServiceRestartStatusDto;
import cn.cyberpeace.mp.devopscluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devopscluster.enums.AlarmTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devopscluster.enums.MonitorStateEnum;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devopscluster.repository.SystemAlarmMapper;
import cn.cyberpeace.mp.devopscluster.request.BizServicePageRequest;
import cn.cyberpeace.mp.devopscluster.request.CloudPlatformPageRequest;
import cn.cyberpeace.mp.devopscluster.vo.BizServiceMonitorVO;
import cn.cyberpeace.mp.devopscluster.vo.CloudPlatformMonitorVO;
import cn.cyberpeace.mp.devopscluster.vo.ServerMonitorVO;
import cn.cyberpeace.mp.devopscluster.vo.SystemAlarmVO;
import cn.cyberpeace.mp.situationaware.config.ServerMonitorConfig;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import cn.cyberpeace.mp.situationaware.service.ServiceMonitorDataCollectionService;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ServiceMonitorService {

    private static final List<String> CUSTOM_SORT_ARR = Arrays.asList("uptime", "+uptime", "-uptime", "name", "+name", "-name", "state", "+state", "-state");

    private final HypervisorConverter hypervisorConverter;

    private final ServiceMonitorDataCollectionService serviceMonitorDataCollectionService;

    private final SystemAlarmMapper systemAlarmMapper;

    private final MonitorScriptControlService monitorScriptControlService;

    private final ImagesConverter imagesConverter;


    
    public ListObjResult<ServiceMonitorDto> bizServicePage(BizServicePageRequest request) {
        List<String> hostNameList = getActiveHostNameList(request.getHostName());
        if (CollectionUtils.isEmpty(hostNameList)) {
            return new ListObjResult<>(Lists.newArrayList(), 0);
        }

        List<ServiceMonitorDto> serviceMonitorDtos = serviceMonitorDataCollectionService.queryBizServiceMonitorData(hostNameList);
        
        processRestartingStatus(serviceMonitorDtos);
        
        processRestartRestrict(serviceMonitorDtos, ServerMonitorConfig.businessSrvMonitors);
        this.changeServiceName(serviceMonitorDtos);

        sortMonitorDtos(serviceMonitorDtos, request.getSort());

        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size()).asResult();
        } else {
            return new ListObjResult<ServiceMonitorDto>(Lists.newArrayList(), serviceMonitorDtos.size()).asResult();
        }
    }

    
    public ListObjResult<ServiceMonitorDto> cpPage(CloudPlatformPageRequest request) {
        List<String> hostNameList = getActiveHostNameList(request.getHostName());
        if (CollectionUtils.isEmpty(hostNameList)) {
            return new ListObjResult<>(Lists.newArrayList(), 0);
        }

        List<ServiceMonitorDto> serviceMonitorDtos = serviceMonitorDataCollectionService.queryCPServiceMonitorData(hostNameList);
        
        processRestartingStatus(serviceMonitorDtos);
        
        processRestartRestrict(serviceMonitorDtos, ServerMonitorConfig.cpCloudSrvMonitors);
        this.changeServiceName(serviceMonitorDtos);

        sortMonitorDtos(serviceMonitorDtos, request.getSort());

        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size()).asResult();
        } else {
            return new ListObjResult<ServiceMonitorDto>(Lists.newArrayList(), serviceMonitorDtos.size()).asResult();
        }
    }

    
    private void changeServiceName(List<ServiceMonitorDto> serviceMonitorDtos) {
        if (CollectionUtils.isNotEmpty(serviceMonitorDtos)) {
            serviceMonitorDtos.forEach(dto -> dto.setName(MessageCommon.get(dto.getName())));
        }
    }

    
    public SystemAlarmVO getSystemAlarm() {
        CloudPlatformMonitorVO cloudPlatformMonitorVO = new CloudPlatformMonitorVO();
        BizServiceMonitorVO bizServiceMonitorVO = new BizServiceMonitorVO();
        ServerMonitorVO serverMonitorVO = new ServerMonitorVO();

        
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

        
        List<String> hostNameList = new ArrayList<>();
        BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO());
        if (CollectionUtils.isNotEmpty(modelPage.getList())) {
            
            CloudPlatformPageResult<ServiceStatus> serviceStatusModel = imagesConverter.listServiceStatus();
            modelPage.getList().forEach(model -> {
                
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

            
            List<ServiceMonitorDto> cpMonitorDtos = serviceMonitorDataCollectionService.queryCPServiceMonitorData(hostNameList);
            processRestartingStatus(cpMonitorDtos);

            if (CollectionUtils.isNotEmpty(cpMonitorDtos)) {
                cloudPlatformMonitorVO.setCloudPlatformMonitors(cpMonitorDtos);
                int cpTotalCount = cpMonitorDtos.size();
                int cpErrorCount = (int) cpMonitorDtos.stream().filter(cpDto -> !MonitorStateEnum.RUNNING.getDesc().equals(cpDto.getState()) && !MonitorStateEnum.RESTARTING.getDesc().equals(cpDto.getState())).count();
                cloudPlatformMonitorVO.setTotalCount(cpTotalCount);
                cloudPlatformMonitorVO.setErrorCount(cpErrorCount);
            }

            
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



    
    private void processRestartingStatus(List<ServiceMonitorDto> serviceMonitorDtos) {
        TimedCache<String, ServiceRestartStatusDto> timedCache = monitorScriptControlService.getStatusCache();
        if (CollUtil.isEmpty(timedCache)) {
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
                    
                    timedCache.remove(cacheKey);
                } else {
                    
                    dto.setState(MonitorStateEnum.RESTARTING.getDesc());
                    dto.setUptime(0L);
                }
            }
        }
    }


    private List<String> getActiveHostNameList(String hostName) {
        List<String> hostNameList = new ArrayList<>();

        BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO());
        
        List<String> activeHostNames = modelPage.getList().stream()
                .filter(m -> Objects.equals(CloudPlatformEnum.UP.getState(), m.getState()))
                .map(HypervisorBO::getName)
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(activeHostNames)) {
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


    
    private void processRestartRestrict(List<ServiceMonitorDto> serviceMonitorDtos, List<ServerMonitorConfig.MonitorProbe> monitorProbes) {
        List<String> allRestrictSrvs = new ArrayList<>();
        monitorProbes.forEach(p -> {
            if (CollUtil.isNotEmpty(p.getRestartRestrictList())) {
                allRestrictSrvs.addAll(p.getRestartRestrictList());
            }
        });

        for (ServiceMonitorDto dto : serviceMonitorDtos) {
            dto.setDisableRestart(Boolean.FALSE);

            if (Objects.equals(MonitorStateEnum.RESTARTING.getDesc(), dto.getState())) {
                
                dto.setDisableRestart(Boolean.TRUE);
            }

            if (CollUtil.isNotEmpty(allRestrictSrvs)) {
                if (Objects.equals(MonitorStateEnum.RUNNING.getDesc(), dto.getState()) && allRestrictSrvs.contains(dto.getName())) {
                    
                    dto.setDisableRestart(Boolean.TRUE);
                }
            }
        }
    }

    private void sortMonitorDtos(List<ServiceMonitorDto> serviceMonitorDtos, String sort) {
        if (StringUtils.isBlank(sort)) {
            normalSort(serviceMonitorDtos);
        } else {
            
        }
    }

    
    private void normalSort(List<ServiceMonitorDto> serviceMonitorDtos) {
        
        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getDisableRestart));

        
        serviceMonitorDtos.sort(new Comparator<ServiceMonitorDto>() {
            @Override
            public int compare(ServiceMonitorDto o1, ServiceMonitorDto o2) {
                return MonitorStateEnum.valueOf(o1.getState()).ordinal() - MonitorStateEnum.valueOf(o2.getState()).ordinal() ;
            }
        });

        
        serviceMonitorDtos.sort(new Comparator<ServiceMonitorDto>() {
            @Override
            public int compare(ServiceMonitorDto o1, ServiceMonitorDto o2) {
                if (Objects.equals(MonitorStateEnum.RESTARTING.getDesc(), o1.getState()) && Objects.equals(MonitorStateEnum.RESTARTING.getDesc(), o2.getState())) {
                    TimedCache<String, ServiceRestartStatusDto> timedCache = monitorScriptControlService.getStatusCache();
                    String o1CacheKey = o1.getHostname() + "_" + o1.getServiceName();
                    String o2CacheKey = o2.getHostname() + "_" + o2.getServiceName();
                    ServiceRestartStatusDto o1StatusDto = timedCache.get(o1CacheKey);
                    ServiceRestartStatusDto o2StatusDto = timedCache.get(o2CacheKey);
                    if (Objects.nonNull(o1StatusDto) && Objects.nonNull(o2StatusDto)) {
                        if (o1StatusDto.getRestartTime().isBefore(o2StatusDto.getRestartTime())) {
                            return 1;
                        } else if (o1StatusDto.getRestartTime().isAfter(o2StatusDto.getRestartTime())) {
                            return -1;
                        }
                    }
                }
                return 0;
            }
        });
    }


    private void customSort(List<ServiceMonitorDto> serviceMonitorDtos, String sort) {
        if (StringUtils.isBlank(sort)) {
            return;
        }

        if (!CUSTOM_SORT_ARR.contains(sort)) {
            return;
        }

        Comparator comparator = null;

        if (sort.contains("uptime")) {
            comparator = new Comparator<ServiceMonitorDto>() {
                @Override
                public int compare(ServiceMonitorDto o1, ServiceMonitorDto o2) {
                    if (Objects.nonNull(o1.getUptime()) && Objects.nonNull(o2.getUptime())) {
                        if (o1.getUptime() > o2.getUptime()) {
                            return 1;
                        } else if (o1.getUptime() < o2.getUptime()) {
                            return -1;
                        }
                    }
                    return 0;
                }
            };

            if (isReversed(sort, "uptime")) {
                comparator.reversed();
            }
        }


        if (sort.contains("name")) {
            comparator = new Comparator<ServiceMonitorDto>() {
                @Override
                public int compare(ServiceMonitorDto o1, ServiceMonitorDto o2) {
                    if (StringUtils.isNotBlank(o1.getName()) && StringUtils.isNotBlank(o2.getName())) {
                        return o1.getName().compareTo(o2.getName());
                    } else {
                        return 0;
                    }
                }
            };

            if (isReversed(sort, "name")) {
                comparator.reversed();
            }
        }


        if (sort.contains("state")) {
            comparator = new Comparator<ServiceMonitorDto>() {
                @Override
                public int compare(ServiceMonitorDto o1, ServiceMonitorDto o2) {
                    if (StringUtils.isNotBlank(o1.getState()) && StringUtils.isNotBlank(o2.getState())) {
                        return o1.getState().compareTo(o2.getState());
                    } else {
                        return 0;
                    }
                }
            };

            if (isReversed(sort, "state")) {
                comparator.reversed();
            }
        }

        if (Objects.nonNull(comparator)) {
            serviceMonitorDtos.sort(comparator);
        }
    }


    private boolean isReversed(String sortStr, String sortField) {
        String symbol = StringUtils.substringBefore(sortStr, sortField);
        return !Objects.equals("-", symbol);
    }

}
