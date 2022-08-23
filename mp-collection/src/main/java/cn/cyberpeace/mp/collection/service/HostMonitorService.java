package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.model.*;
import cn.cyberpeace.mp.collection.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.Port;
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
    ListObjServiceResult<WazuhHostStateModel> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机网络连接信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhNetworkConnectionModel> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机进程信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhProcessModel> queryProcessesWithTimeRange(HostMonitorPageRequest request);

    /**
     * 查询主机系统用户信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhSystemUserModel> querySystemUserWithTimeRange(HostMonitorPageRequest request);

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
