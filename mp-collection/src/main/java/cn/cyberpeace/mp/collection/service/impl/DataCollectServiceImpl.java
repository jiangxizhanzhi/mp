package cn.cyberpeace.mp.collection.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.collection.constant.CollectionConstant;
import cn.cyberpeace.mp.collection.model.*;
import cn.cyberpeace.mp.collection.request.*;
import cn.cyberpeace.mp.collection.service.DataCollectService;
import cn.cyberpeace.mp.collection.core.es.CollectionClient;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DataCollectServiceImpl implements DataCollectService {
    @Resource
    private CollectionClient collectionClient;

    // expires in 10 minutes
    private TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000);

    @Override
    public ListObjServiceResult<FileEventModel> queryFileModifyEvents(SyscheckEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.FILE_MODIFY_GROUP);
        var result = collectionClient.queryFileEvents(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<FileEventModel> queryRegistryModifyEvents(SyscheckEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.REGISTRY_MODIFY_GROUP);
        var result = collectionClient.queryFileEvents(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<FileEventModel> queryFileEvents(DataCollectEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = collectionClient.queryFileEvents(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<FileEventModel> scrollFileEvents(DataCollectEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        ListObjResult<FileEventModel> result;
        String requestId = request.getRequestId();
        if (Objects.isNull(TIMED_CACHE.get(requestId))) {
            result = collectionClient.queryFileEvents(wazuhEventRequest);
            TIMED_CACHE.put(requestId, result.getObjs().get(0).getScrollId());
        } else {
            result = collectionClient.scrollFileEvents(TIMED_CACHE.get(requestId));
        }
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhProbeCommonModel> queryCustomProbeData(CustomDataQueryRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = collectionClient.queryCustomProbeData(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ListObjServiceResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(VulnerabilityEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.VULNERABILITY_GROUP);
        var result = collectionClient.queryVulnerabilityInfo(wazuhEventRequest);
        return new ListObjServiceResult<>(result);
    }

    @Override
    public ServiceResult<List<WazuhVulnerabilityInfoCountModel>> queryVulnerabilityCountInfo(VulnerabilityEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        wazuhEventRequest.setRuleGroups(CollectionConstant.VULNERABILITY_GROUP);
        var result = collectionClient.queryVulnerabilityCountInfo(wazuhEventRequest,
                request.getFiledPath(), request.getFiledName());
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Map<String, Long>> countByAgentNamesBucket(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = collectionClient.countByAgentNamesBucket(wazuhEventRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Long> countByAgentNames(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = collectionClient.countByAgentNames(wazuhEventRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Map<String, Long>> aggsRuleIdsByAgentName(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = collectionClient.aggsRuleIdsByAgentName(wazuhEventRequest);
        return new ServiceResult<>(result);
    }

    @Override
    public ServiceResult<Long> deleteDocByAgentNames(BaseEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        BeanUtils.copyProperties(request, wazuhEventRequest);
        var result = collectionClient.deleteDocByAgentNames(wazuhEventRequest);
        return new ServiceResult<>(result);
    }
}
