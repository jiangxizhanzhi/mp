package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataCollectServiceImpl implements DataCollectService {
    @Override
    public ListObjServiceResult<FileEventModel> queryFileModifyEvents(WazuhEventRequest request) {
        return null;
    }

    @Override
    public ListObjServiceResult<FileEventModel> queryRegistryModifyEvents(WazuhEventRequest request) {
        return null;
    }

    @Override
    public ListObjServiceResult<FileEventModel> queryFileEvents(WazuhEventRequest request) {
        return null;
    }

    @Override
    public ListObjServiceResult<FileEventModel> scrollFileEvents(WazuhEventRequest request, String requestId) {
        return null;
    }

    @Override
    public ListObjServiceResult<WazuhProbeCommonModel> queryCustomProbeData(WazuhEventRequest request, List<String> probeIds) {
        return null;
    }

    @Override
    public ListObjServiceResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(WazuhEventRequest request) {
        return null;
    }

    @Override
    public ServiceResult<List<WazuhVulnerabilityInfoCountModel>> queryVulnerabilityCountInfo(WazuhEventRequest request, String filedName, String filedPath) {
        return null;
    }

    @Override
    public ServiceResult<Map<String, Long>> aggsCountByAgentNamesBucket(WazuhEventRequest request) {
        return null;
    }

    @Override
    public ServiceResult<Long> countByAgentNames(WazuhEventRequest request) {
        return null;
    }
}
