package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.request.BizServicePageRequest;
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
 * @Description: 业务服务监控服务类
 * @Author qiaokang
 * @Date：2022/7/15 14:27
 */
@Service
@RequiredArgsConstructor
public class BizServiceMonitorService {

    private final HypervisorConverter hypervisorConverter;

    private final ServiceMonitorDataCollectionService serviceMonitorDataCollectionService;

    /**
     * 分页查询业务服务监控数据
     * @param request
     * @return
     */
    public ListObjResult<ServiceMonitorDto> page(BizServicePageRequest request) {
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

}
