package cn.cyberpeace.mp.collection.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.collection.constant.CollectionConstant;
import cn.cyberpeace.mp.collection.core.model.FileEvent;
import cn.cyberpeace.mp.collection.core.model.WazuhProbeCommon;
import cn.cyberpeace.mp.collection.core.model.WazuhVulnerabilityInfo;
import cn.cyberpeace.mp.collection.core.model.WazuhVulnerabilityInfoCount;
import cn.cyberpeace.mp.collection.core.request.WazuhEventRequest;
import cn.cyberpeace.mp.collection.core.clientmanager.ClientManagerCommon;
import cn.cyberpeace.mp.collection.request.*;
import cn.cyberpeace.mp.collection.service.DataCollectService;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DataCollectServiceImpl implements DataCollectService {
    // expires in 10 minutes
    private final TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000L);

    @Override
    public ListObjServiceResult<FileEvent> queryFileModifyEvents(SyscheckEventRequest request) {
        WazuhEventRequest eventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, eventRequest);
        eventRequest.setRuleGroups(CollectionConstant.FILE_MODIFY_GROUP);
        var result = ClientManagerCommon.clientManager.queryFileEvents(eventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<FileEvent> queryRegistryModifyEvents(SyscheckEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.REGISTRY_MODIFY_GROUP);
        var result = ClientManagerCommon.clientManager.queryFileEvents(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<FileEvent> queryFileEvents(DataCollectEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = ClientManagerCommon.clientManager.queryFileEvents(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<FileEvent> scrollFileEvents(DataCollectEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        ListObjResult<FileEvent> result;
        String requestId = request.getRequestId();
        if (Objects.isNull(TIMED_CACHE.get(requestId))) {
            result = ClientManagerCommon.clientManager.queryFileEvents(wazuhEventRequest);
            TIMED_CACHE.put(requestId, result.getObjs().get(0).getScrollId());
        } else {
            result = ClientManagerCommon.clientManager.scrollFileEvents(TIMED_CACHE.get(requestId));
        }
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhProbeCommon> queryCustomProbeData(CustomDataQueryRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = ClientManagerCommon.clientManager.queryCustomProbeData(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhVulnerabilityInfo> queryVulnerabilityInfo(VulnerabilityEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.VULNERABILITY_GROUP);
        var result = ClientManagerCommon.clientManager.queryVulnerabilityInfo(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ServiceResult<List<WazuhVulnerabilityInfoCount>> queryVulnerabilityCountInfo(VulnerabilityEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.VULNERABILITY_GROUP);
        var result = ClientManagerCommon.clientManager
                .queryVulnerabilityCountInfo(wazuhEventRequest, request.getFiledPath(), request.getFiledName());
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Map<String, Long>> countByAgentNamesBucket(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = ClientManagerCommon.clientManager.countByAgentNamesBucket(wazuhEventRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Long> countByAgentNames(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = ClientManagerCommon.clientManager.countByAgentNames(wazuhEventRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Map<String, Long>> aggsRuleIdsByAgentName(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = ClientManagerCommon.clientManager.aggsRuleIdsByAgentName(wazuhEventRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Long> deleteDocByAgentNames(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = ClientManagerCommon.clientManager.deleteDocByAgentNames(wazuhEventRequest);
        return new ServiceResult<>(result);
    }
}
