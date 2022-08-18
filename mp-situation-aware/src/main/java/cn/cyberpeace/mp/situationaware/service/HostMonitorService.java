package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import org.springframework.stereotype.Service;

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
     * @param request
     * @return
     */
    ServiceResult<List<OS>> getOS(CollectorBaseRequest request);

    /**
     * 查询主机状态信息
     * @param request
     * @return
     */
    ServiceResult<List<WazuhHostStateModel>> queryHostStateEventsWithTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机网络连接信息
     * @param request
     * @return
     */
    ServiceResult<List<WazuhNetworkConnectionModel>> queryNetConnEventsWithTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机进程信息
     * @param request
     * @return
     */
    ServiceResult<List<WazuhProcessModel>> queryProcessesWithTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机用户变更信息
     * @param request
     * @return
     */
    ServiceResult<List<WazuhSystemUserModel>> queryUserChangeEventWithTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机环境变量变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机系统用户变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机进程变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumProcessesChangedCountByTimeRange(WazuhMonitorEventsRequest request);

    /**
     * 查询主机网络连接变更数量
     * @param request
     * @return
     */
    ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(WazuhMonitorEventsRequest request);
}
