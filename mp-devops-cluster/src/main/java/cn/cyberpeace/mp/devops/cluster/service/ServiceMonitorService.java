package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmTypeEnum;
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
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import cn.cyberpeace.mp.situationaware.service.ServiceMonitorDataCollectionService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    private final ImagesConverter imagesConverter;

    /**
     * 分页查询业务服务监控数据
     * @param request
     * @return
     */
    public ListObjResult<ServiceMonitorDto> bizServicePage(BizServicePageRequest request) {
        List<String> hostNameList = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getHostName())) {
            hostNameList.add(request.getHostName());
        } else {
            BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO(100, 1));
            if (CollectionUtils.isNotEmpty(modelPage.getList())) {
                modelPage.getList().forEach(model -> hostNameList.add(model.getName()));
            }
        }

        if (CollectionUtils.isEmpty(hostNameList)) {
            return new ListObjResult<>(Lists.newArrayList(), 0);
        }

        List<ServiceMonitorDto> serviceMonitorDtos = serviceMonitorDataCollectionService.queryBizServiceMonitorData(hostNameList);
        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getState));
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
        List<String> hostNameList = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getHostName())) {
            hostNameList.add(request.getHostName());
        } else {
            BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO(100, 1));
            if (CollectionUtils.isNotEmpty(modelPage.getList())) {
                modelPage.getList().forEach(model -> hostNameList.add(model.getName()));
            }
        }

        if (CollectionUtils.isEmpty(hostNameList)) {
            return new ListObjResult<>(Lists.newArrayList(), 0);
        }

        List<ServiceMonitorDto> serviceMonitorDtos = serviceMonitorDataCollectionService.queryCPServiceMonitorData(hostNameList);
        serviceMonitorDtos.sort(Comparator.comparing(ServiceMonitorDto::getState));
        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size()).asResult();
        } else {
            return new ListObjResult<ServiceMonitorDto>(Lists.newArrayList(), serviceMonitorDtos.size()).asResult();
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
                hostNameList.add(model.getName());
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
            if (CollectionUtils.isNotEmpty(cpMonitorDtos)) {
                cloudPlatformMonitorVO.setCloudPlatformMonitors(cpMonitorDtos);
                int cpTotalCount = cpMonitorDtos.size();
                int cpErrorCount = (int) cpMonitorDtos.stream().filter(cpDto -> !MonitorStateEnum.RUNNING.getDesc().equals(cpDto.getState())).count();
                cloudPlatformMonitorVO.setTotalCount(cpTotalCount);
                cloudPlatformMonitorVO.setErrorCount(cpErrorCount);
            }

            // 业务服务监控
            List<ServiceMonitorDto> bizMonitorDtos = serviceMonitorDataCollectionService.queryBizServiceMonitorData(hostNameList);
            if (CollectionUtils.isNotEmpty(bizMonitorDtos)) {
                bizServiceMonitorVO.setBizServiceMonitors(bizMonitorDtos);
                int bizTotalCount = bizMonitorDtos.size();
                int bizErrorCount = (int) bizMonitorDtos.stream().filter(bizDto -> !MonitorStateEnum.RUNNING.getDesc().equals(bizDto.getState())).count();
                bizServiceMonitorVO.setTotalCount(bizTotalCount);
                bizServiceMonitorVO.setErrorCount(bizErrorCount);
            }
        }

        return new SystemAlarmVO(cloudPlatformMonitorVO, bizServiceMonitorVO, serverMonitorVO);
    }

}
