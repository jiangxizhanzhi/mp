package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.core.model.FileEvent;
import cn.cyberpeace.mp.collection.core.model.WazuhProbeCommon;
import cn.cyberpeace.mp.collection.core.model.WazuhVulnerabilityInfo;
import cn.cyberpeace.mp.collection.core.model.WazuhVulnerabilityInfoCount;
import cn.cyberpeace.mp.collection.request.*;

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
    ListObjServiceResult<FileEvent> queryFileModifyEvents(SyscheckEventRequest request);

    /**
     * 查询主机注册表变更事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEvent> queryRegistryModifyEvents(SyscheckEventRequest request);

    /**
     * 查询主机采集事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEvent> queryFileEvents(DataCollectEventRequest request);

    /**
     * 滚动查询主机采集事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEvent> scrollFileEvents(DataCollectEventRequest request);

    /**
     * 查询自定义探针采集数据
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhProbeCommon> queryCustomProbeData(CustomDataQueryRequest request);

    /**
     * 分页查询漏洞信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhVulnerabilityInfo> queryVulnerabilityInfo(VulnerabilityEventRequest request);

    /**
     * 查询漏洞数量信息
     * @param request
     * @return
     */
    ServiceResult<List<WazuhVulnerabilityInfoCount>> queryVulnerabilityCountInfo(VulnerabilityEventRequest request);

    /**
     * 查询指定时间段内每个节点事件总数
     * @param request
     * @return
     */
    ServiceResult<Map<String, Long>> countByAgentNamesBucket(BaseEventRequest request);

    /**
     * 查询指定节点查询事件总数
     * @param request
     * @return
     */
    ServiceResult<Long> countByAgentNames(BaseEventRequest request);

    /**
     * 查询指定时间段内所有节点匹配到的所有rule id
     * @param request
     * @return
     */
    ServiceResult<Map<String, Long>> aggsRuleIdsByAgentName(BaseEventRequest request);

    /**
     * 删除指定时间段内所有节点对应的es数据
     * @param request
     * @return
     */
    ServiceResult<Long> deleteDocByAgentNames(BaseEventRequest request);
}
