package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.core.model.WazuhHostState;
import cn.cyberpeace.mp.collection.core.model.WazuhNetworkConnection;
import cn.cyberpeace.mp.collection.core.model.WazuhProcess;
import cn.cyberpeace.mp.collection.core.model.WazuhSystemUser;
import cn.cyberpeace.mp.collection.core.request.wazuh.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.request.HostMonitorBaseRequest;
import cn.cyberpeace.mp.collection.request.HostMonitorPageRequest;

import java.util.List;

/**
 * 主机基本信息Service
 */
public interface HostMonitorService {

    /**
     * 查询主机端口信息
     * @param request
     * @return
     */
    ListObjServiceResult<Port> pagePorts(CollectorBaseRequest request);

    /**
     * 查询主机网卡信息
     * @param request
     * @return
     */
    ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request);

    /**
     * 查询主机操作系统信息
     * @param agentName
     * @return
     */
    ServiceResult<List<OS>> getOS(String agentName);

    /**
     * 查询主机状态信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhHostState> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机网络连接信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhNetworkConnection> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机进程信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhProcess> queryProcessesWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机系统用户信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhSystemUser> querySystemUserWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机环境变量变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(HostMonitorBaseRequest request);

    /**
     * 查询主机系统用户变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(HostMonitorBaseRequest request);

    /**
     * 查询主机进程变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumProcessesChangedCountByTimeRange(HostMonitorBaseRequest request);

    /**
     * 查询主机网络连接变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(HostMonitorBaseRequest request);
}
