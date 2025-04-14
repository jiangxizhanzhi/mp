package cn.cyberpeace.mp.cscene.service.multirange;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.scene.core.bo.HfdyServerVpnConfig;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 子靶场态势监控相关接口
 *
 */
public interface SubRangeMonitorInterface {
    Map<String, HfdyServerVpnConfig> getIdentity(String currentSceneId);

    String getAuthorizationHeader(String rangeName);

    Map<String, String> getRangeMapCscene(String identity);

    List<ElementMonitorBasicInfo> listAllElementBasicInfo(String rangeMapScene, SceneAllElementsQueryRequest request);

    OS getElementOs(BaseSceneElementRequest baseRequest);

    ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest);

    ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest);

    ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams);
    ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams);

    ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams);

    List<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams, boolean allFlag);

    ListObjServiceResult<CollectionHostEventBO> getFileModifyData(ElementMonitorRequest pageRequest, String path);

    ListObjServiceResult<CollectionHostEventBO> getRegistryModifyData(ElementMonitorRequest pageRequest, String path);

    ListObjServiceResult<CollectionHostEventBO> listHostEvents(String id, HostEventQueryParams queryParams);

    public ListObjServiceResult<ConnectionState> pageNetworkConnections(BaseSceneElementPageRequest pageRequest);
}
