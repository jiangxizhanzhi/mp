package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.request.HostMonitorBaseRequest;
import cn.cyberpeace.mp.situationaware.request.HostMonitorPageRequest;
import cn.cyberpeace.mp.situationaware.service.wazuh.WazuhClient;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

public class HostMonitorServiceImpl implements HostMonitorService {
    private WazuhClient wazuhClient;

    private EsClient esClient;

    public HostMonitorServiceImpl(String esNode, String wazuhUrl, String wazuhUsername, String wazuhPassword) {
        this.esClient = new EsClient(esNode);
        this.wazuhClient = new WazuhClient(wazuhUrl, wazuhUsername, wazuhPassword);
    }

    @Override
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest request) {
        var result = wazuhClient.pagePorts(request);
        return new ListObjServiceResult<>(new ListObjResult<>(result.getObjs(), result.getTotal()));
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request) {
        var result = wazuhClient.pageNetifaces(request);
        return new ListObjServiceResult<>(new ListObjResult<>(result.getObjs(), result.getTotal()));
    }

    @Override
    public ServiceResult<List<OS>> getOS(String agentName) {
        var result = wazuhClient.getOS(agentName);
        return new ServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhHostStateModel> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = esClient.queryHostStateEventsWithTimeRange(pageRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhNetworkConnectionModel> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = esClient.queryNetConnEventsWithTimeRange(pageRequest);
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
        var result = esClient.queryProcessesWithTimeRange(pageRequest);
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
        var result = esClient.querySystemUserWithTimeRange(pageRequest);
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
        var result = esClient.sumEnvVarChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = esClient.sumSysUsersChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumProcessesChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = esClient.sumProcessesChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = esClient.sumNetConnsChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }
}
