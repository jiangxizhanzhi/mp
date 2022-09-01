package cn.cyberpeace.mp.collection.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.collection.core.model.WazuhHostState;
import cn.cyberpeace.mp.collection.core.model.WazuhNetworkConnection;
import cn.cyberpeace.mp.collection.core.model.WazuhProcess;
import cn.cyberpeace.mp.collection.core.model.WazuhSystemUser;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.core.request.WazuhHostMonitorPageRequest;
import cn.cyberpeace.mp.collection.core.request.wazuh.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.core.clientmanager.ClientManagerCommon;
import cn.cyberpeace.mp.collection.request.HostMonitorBaseRequest;
import cn.cyberpeace.mp.collection.request.HostMonitorPageRequest;
import cn.cyberpeace.mp.collection.service.HostMonitorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostMonitorServiceImpl implements HostMonitorService {

    @Override
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest request) {
        var result = ClientManagerCommon.clientManager.pagePorts(request);
        return new ListObjServiceResult<>(new ListObjResult<>(result.getObjs(), result.getTotal()));
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request) {
        var result = ClientManagerCommon.clientManager.pageNetifaces(request);
        return new ListObjServiceResult<>(new ListObjResult<>(result.getObjs(), result.getTotal()));
    }

    @Override
    public ServiceResult<List<OS>> getOS(String agentName) {
        var result = ClientManagerCommon.clientManager.getOS(agentName);
        return new ServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhHostState> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.queryHostStateEventsWithTimeRange(pageRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhNetworkConnection> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.queryNetConnEventsWithTimeRange(pageRequest);
        // 分页处理
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int total = result.size();
        result = result.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Override
    public ListObjServiceResult<WazuhProcess> queryProcessesWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.queryProcessesWithTimeRange(pageRequest);
        // 分页处理
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int total = result.size();
        result = result.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Override
    public ListObjServiceResult<WazuhSystemUser> querySystemUserWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.querySystemUserWithTimeRange(pageRequest);
        List<WazuhSystemUser> list = result.getValues().stream().peek(wazuhSystemUser -> wazuhSystemUser.setType("value")).collect(Collectors.toList());
        list.addAll(result.getAdded().stream().peek(wazuhSystemUser -> wazuhSystemUser.setType("add")).collect(Collectors.toList()));
        list.addAll(result.getDeletedPrev().stream().peek(wazuhSystemUser -> wazuhSystemUser.setType("delete")).collect(Collectors.toList()));
        list.addAll(result.getModified().stream().peek(wazuhSystemUser -> wazuhSystemUser.setType("modify")).collect(Collectors.toList()));
        // 分页处理
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int total = list.size();
        list = list.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(list, total));
    }

    @Override
    public ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.sumEnvVarChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.sumSysUsersChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumProcessesChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.sumProcessesChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(HostMonitorBaseRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        BeanUtils.copyProperties(request, pageRequest);
        var result = ClientManagerCommon.clientManager.sumNetConnsChangedCountByTimeRange(pageRequest);
        return new ServiceResult<>(result);
    }
}
