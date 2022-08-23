package cn.cyberpeace.mp.collection.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.BO.*;

import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.service.FlowService;
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
//        FlowDetailInfo flowDetailInfo = new FlowDetailInfo();
//        flowDetailInfo.setSourceIp("192.168.1.1");
//        flowDetailInfo.setTargetIp("192.168.1.18");
//        flowDetailInfo.setTime(LocalDateTime.of(2022,8,18,15,10,30));
//        flowDetailInfo.setProtocol("ssh");
//        flowDetailInfo.setSourcePort("8080");
//        return new ServiceResult<>(flowDetailInfo).asResult();
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
