package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.vo.FlowDetailInfo;
import cn.cyberpeace.mp.collection.vo.TeamFlowInfo;

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
    ServiceResult<FlowDetailInfo> queryFlowDetail(FlowQueryRequest request);
}
