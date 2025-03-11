package cn.cyberpeace.mp.cscene.service.multirange;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;

import java.util.List;

/**
 * 子靶场态势监控相关接口
 *
 */
public interface SubRangeMonitorInterface {
    String getIdentity(String currentSceneId);

    String getAuthorizationHeader();

    String getRangeMapCscene(String identity);

    List<ElementMonitorBasicInfo> listAllElementBasicInfo(String rangeMapScene, SceneAllElementsQueryRequest request);

    OS getElementOs(BaseSceneElementRequest baseRequest);

    ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest);

    ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest);

    ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams);
    ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams);

    ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams);

}
