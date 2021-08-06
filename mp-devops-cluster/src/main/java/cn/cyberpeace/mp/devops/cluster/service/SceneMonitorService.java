package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.request.EventMonitorRequest;
import cn.cyberpeace.mp.devops.cluster.request.FlowMonitorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HostMonitorRequest;

public interface SceneMonitorService {

    /**
     * 场景节点监控分页查询
     * @param request
     * @return
     */
    ServiceResult pageHostMonitor(HostMonitorRequest request);

    /**
     * 场景流量监控分页查询
     * @param request
     * @return
     */
    ServiceResult pageFlowMonitor(FlowMonitorRequest request);


    /**
     * 场景事件监控分页查询
     * @param request
     * @return
     */
    ServiceResult pageEventMonitor(EventMonitorRequest request);

}
