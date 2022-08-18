package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.BO.*;
import cn.cyberpeace.mp.situationaware.request.FlowQueryRequest;
import cn.cyberpeace.mp.situationaware.request.PageFlowRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @GetMapping("/detail")
    @ApiOperation(value = "查询一条流量记录的详细信息", notes = "")
    public ServiceResult<FlowDetailInfo> queryFlowDetailInfo(FlowQueryRequest flowQueryRequest) {
        FlowDetailInfo flowDetailInfo = new FlowDetailInfo();

        flowDetailInfo.setSourceIp("192.168.1.1");
        flowDetailInfo.setTargetIp("192.168.1.18");
        flowDetailInfo.setTime(LocalDateTime.of(2022,8,18,15,10,30));
        flowDetailInfo.setType("ssh");
        flowDetailInfo.setSourcePort("8080");

        return new ServiceResult<>(flowDetailInfo).asResult();
    }

    @GetMapping("/page")
    @ApiOperation(value = "筛选流量", notes = "")
    public ListObjServiceResult<List<TeamFlowInfo>> queryFlowPageEvents(PageFlowRequest request) {
        request.ensure();
        List<TeamFlowInfo> teamFlowInfos = new ArrayList<>();
        TeamFlowInfo teamFlowInfo = new TeamFlowInfo();

        teamFlowInfo.setSourceIp("192.168.1.1");
        teamFlowInfo.setTargetIp("192.168.1.18");
        teamFlowInfo.setTime(LocalDateTime.of(2022,8,18,15,10,30));
        teamFlowInfo.setProtocol("ssh");
        teamFlowInfo.setSourcePort("8080");

        for (int i= 0; i < 20; i ++) {
            teamFlowInfos.add(teamFlowInfo);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(List.of(teamFlowInfos),1)).asResult();
    }

}
