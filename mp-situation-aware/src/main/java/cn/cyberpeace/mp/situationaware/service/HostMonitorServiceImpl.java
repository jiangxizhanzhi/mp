package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.service.wazuh.WazuhClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HostMonitorServiceImpl implements HostMonitorService {

    @Resource
    private WazuhClient wazuhService;

    @Override
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest request) {
        return wazuhService.pagePorts(request);
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request) {
        return wazuhService.pageNetifaces(request);
    }

    @Override
    public ServiceResult<List<OS>> getOS(CollectorBaseRequest request) {
        return wazuhService.getOS(request.getAgentName());
    }

    @Override
    public ServiceResult<List<WazuhHostStateModel>> queryHostStateEventsWithTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<List<WazuhNetworkConnectionModel>> queryNetConnEventsWithTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<List<WazuhProcessModel>> queryProcessesWithTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<List<WazuhSystemUserModel>> queryUserChangeEventWithTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<Integer> sumProcessesChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }

    @Override
    public ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return null;
    }
}
