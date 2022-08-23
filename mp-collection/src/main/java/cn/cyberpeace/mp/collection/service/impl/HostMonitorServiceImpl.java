package cn.cyberpeace.mp.collection.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.collection.model.*;
import cn.cyberpeace.mp.collection.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.request.HostMonitorBaseRequest;
import cn.cyberpeace.mp.collection.request.HostMonitorPageRequest;
import cn.cyberpeace.mp.collection.service.HostMonitorService;
import cn.cyberpeace.mp.collection.core.es.CollectionClient;
import cn.cyberpeace.mp.collection.core.wazuh.WazuhClientProxy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostMonitorServiceImpl implements HostMonitorService {
    @Resource
    private CollectionClient collectionClient;


    @Override
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest request) {
        var wazuhClient = WazuhClientProxy.getInstance();
        var result = wazuhClient.pagePorts(request);
        return new ListObjServiceResult<>(new ListObjResult<>(result.getObjs(), result.getTotal()));
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request) {
        var wazuhClient = WazuhClientProxy.getInstance();
        var result = wazuhClient.pageNetifaces(request);
        return new ListObjServiceResult<>(new ListObjResult<>(result.getObjs(), result.getTotal()));
    }

    @Override
    public ServiceResult<List<OS>> getOS(String agentName) {
        var wazuhClient = WazuhClientProxy.getInstance();
        var result = wazuhClient.getOS(agentName);
        return new ServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhHostStateModel> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.queryHostStateEventsWithTimeRange(pageRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhNetworkConnectionModel> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.queryNetConnEventsWithTimeRange(pageRequest);
        // 分页处理
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int total = result.size();
        result = result.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Override
    public ListObjServiceResult<WazuhProcessModel> queryProcessesWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.queryProcessesWithTimeRange(pageRequest);
        // 分页处理
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int total = result.size();
        result = result.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Override
    public ListObjServiceResult<WazuhSystemUserModel> querySystemUserWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.querySystemUserWithTimeRange(pageRequest);
        // 分页处理
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int total = result.size();
        result = result.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Override
    public ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.sumEnvVarChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.sumSysUsersChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumProcessesChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.sumProcessesChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = collectionClient.sumNetConnsChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }
}
