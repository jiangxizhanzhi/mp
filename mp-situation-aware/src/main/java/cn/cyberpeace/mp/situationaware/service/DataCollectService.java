package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.request.*;

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
    ListObjServiceResult<FileEventModel> queryFileModifyEvents(SyscheckEventRequest request);

    /**
     * 查询主机注册表变更事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEventModel> queryRegistryModifyEvents(SyscheckEventRequest request);

    /**
     * 查询主机采集事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEventModel> queryFileEvents(DataCollectEventRequest request);

    /**
     * 滚动查询主机采集事件
     * @param request
     * @return
     */
    ListObjServiceResult<FileEventModel> scrollFileEvents(DataCollectEventRequest request);

    /**
     * 查询自定义探针采集数据
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhProbeCommonModel> queryCustomProbeData(CustomDataQueryRequest request);

    /**
     * 分页查询漏洞信息
     * @param request
     * @return
     */
    ListObjServiceResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(VulnerabilityEventRequest request);

    /**
     * 查询漏洞数量信息
     * @param request
     * @return
     */
    ServiceResult<List<WazuhVulnerabilityInfoCountModel>> queryVulnerabilityCountInfo(VulnerabilityEventRequest request);

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
