package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.vo.FlowDetailInfo;
import cn.cyberpeace.mp.collection.vo.TeamFlowInfo;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.service.FlowService;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @Autowired
    private FlowService flowService;

    @GetMapping("/detail")
    @ApiOperation(value = "查询一条流量的详细信息", notes = "")
    public ServiceResult<FlowDetailInfo> queryFlowDetailInfo(FlowQueryRequest flowQueryRequest) {

        ServiceResult<FlowDetailInfo> flowDetailInfo = flowService.queryFlowDetail(flowQueryRequest);
        return flowDetailInfo;
    }

    @GetMapping("/page")
    @ApiOperation(value = "筛选战队流量信息", notes = "")
    public ListObjServiceResult<TeamFlowInfo> queryFlowPageEvents(PageFlowRequest request) {
        request.ensure();
        ListObjServiceResult<TeamFlowInfo> teamFlowInfos = flowService.queryFlowPage(request);
        return teamFlowInfos;
    }

}
