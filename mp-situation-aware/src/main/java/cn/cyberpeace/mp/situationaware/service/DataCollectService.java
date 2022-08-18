package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.*;

import java.util.List;
import java.util.Map;

/**
 * 主机数据采集信息Service
 */
public interface DataCollectService {

    /**
     * 查询主机文件变更事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEventModel> queryFileModifyEvents(WazuhEventRequest request);

    /**
     * 查询主机注册表变更事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEventModel> queryRegistryModifyEvents(WazuhEventRequest request);

    /**
     * 查询主机采集事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEventModel> queryFileEvents(WazuhEventRequest request);

    /**
     * 滚动查询主机采集事件
     * @param request
     * @param requestId 请求id
     * @return
     */
    ListObjServiceResult<FileEventModel> scrollFileEvents(WazuhEventRequest request,String requestId);

    /**
     * 查询自定义探针采集数据
     * @param request
     * @param probeIds 自定义探针id集合
     * @return
     */
    ListObjServiceResult<WazuhProbeCommonModel> queryCustomProbeData(WazuhEventRequest request,List<String> probeIds);

    /**
     * 分页查询漏洞信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(WazuhEventRequest request);

    /**
     *
     * @param request
     * @param filedName
     * @param filedPath
     * @return
     */
    ServiceResult<List<WazuhVulnerabilityInfoCountModel>> queryVulnerabilityCountInfo(WazuhEventRequest request, String filedName, String filedPath);

    /**
     * 查询指定时间段内每个主机事件总数
     * @param request
     * @return
     */
    ServiceResult<Map<String, Long>> aggsCountByAgentNamesBucket(WazuhEventRequest request);

    /**
     * 查询指定节点查询事件总数
     * @param request
     * @return
     */
    ServiceResult<Long> countByAgentNames(WazuhEventRequest request);
}
