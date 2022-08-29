package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.BO.TeamFlowInfo;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 查询流量列表和一条流量的详细信息
 */
public interface FlowInfoService {
    /**
     * 按条件筛选流量，返回符合条件的流量列表
     * @param request
     * @return
     */
    ListObjServiceResult<TeamFlowInfo> queryFlowPage(PageFlowRequest request);

    /**
     * 查询一条流量的详细信息
     * @param request
     * @return
     */
    ServiceResult<Map<String, Object>> queryFlowDetail(FlowQueryRequest request);

    /**
     * 删除从开始时间到结束时间内的文档
     * @param
     * @return
     */
    void deleteDoc(LocalDateTime startTime, LocalDateTime endTime);
}
