package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
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

    private static final String RUNNNING = "RUNNNING";

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
        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size());
        } else {
            return new ListObjResult<>(Lists.newArrayList(), serviceMonitorDtos.size());
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
        int offset = (request.getPage() - 1) * request.getPerPage();
        if (offset < serviceMonitorDtos.size()) {
            int toIndex = Math.min(offset + request.getPerPage(), serviceMonitorDtos.size());
            return new ListObjResult<>(serviceMonitorDtos.subList(offset, toIndex), serviceMonitorDtos.size());
        } else {
            return new ListObjResult<>(Lists.newArrayList(), serviceMonitorDtos.size());
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

        // 服务器监控
        List<String> hostNameList = new ArrayList<>();
        BasePageBO<HypervisorBO> modelPage = hypervisorConverter.page(new BasePageDTO(100, 1));
        if (CollectionUtils.isNotEmpty(modelPage.getList())) {
            modelPage.getList().forEach(model -> hostNameList.add(model.getName()));

            int totalCount = modelPage.getTotal();
            int errorCount = (int )modelPage.getList().stream().filter(model -> "".equals(model.getStatus())).count();
            serverMonitorVO.setServerMonitors(modelPage.getList());
            serverMonitorVO.setErrorCount(errorCount);
            serverMonitorVO.setTotalCount(totalCount);

            // 云平台服务监控
            List<ServiceMonitorDto> cpMonitorDtos = serviceMonitorDataCollectionService.queryCPServiceMonitorData(hostNameList);
            if (CollectionUtils.isNotEmpty(cpMonitorDtos)) {
                cloudPlatformMonitorVO.setCloudPlatformMonitors(cpMonitorDtos);
                int cpTotalCount = cpMonitorDtos.size();
                int cpErrorCount = (int) cpMonitorDtos.stream().filter(cpDto -> !RUNNNING.equals(cpDto.getState())).count();
                cloudPlatformMonitorVO.setTotalCount(cpTotalCount);
                cloudPlatformMonitorVO.setErrorCount(cpErrorCount);
            }

            // 业务服务监控
            List<ServiceMonitorDto> bizMonitorDtos = serviceMonitorDataCollectionService.queryBizServiceMonitorData(hostNameList);
            if (CollectionUtils.isNotEmpty(bizMonitorDtos)) {
                cloudPlatformMonitorVO.setCloudPlatformMonitors(bizMonitorDtos);
                int bizTotalCount = bizMonitorDtos.size();
                int bizErrorCount = (int) bizMonitorDtos.stream().filter(bizDto -> !RUNNNING.equals(bizDto.getState())).count();
                cloudPlatformMonitorVO.setTotalCount(bizTotalCount);
                cloudPlatformMonitorVO.setErrorCount(bizErrorCount);
            }
        } else {
            cloudPlatformMonitorVO.setCloudPlatformMonitors(new ArrayList<>());
            bizServiceMonitorVO.setBizServiceMonitors(new ArrayList<>());
            serverMonitorVO.setServerMonitors(new ArrayList<>());
        }

        return new SystemAlarmVO(cloudPlatformMonitorVO, bizServiceMonitorVO, serverMonitorVO);
    }

}
