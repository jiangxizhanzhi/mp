package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.service.FlowService;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collectionapi.vo.FlowDetailVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamFlowVO;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @Autowired
    private FlowService flowService;

    @GetMapping("/detail")
    @ApiOperation(value = "查询一条流量的详细信息", notes = "")
    public ServiceResult<FlowDetailVO> queryFlowDetailInfo(FlowQueryRequest flowQueryRequest) {

        var flowDetailInfo = flowService.queryFlowDetail(flowQueryRequest).getSource();

        if (Objects.isNull(flowDetailInfo)) {
            return new ServiceResult<>(new FlowDetailVO());
        }

        FlowDetailVO flowDetailVO = new FlowDetailVO();
        flowDetailVO.setProtocol(flowDetailInfo.getProtocol());
        flowDetailVO.setSize(flowDetailInfo.getSize());
        flowDetailVO.setTime(flowDetailInfo.getTime());
        flowDetailVO.setSourceIp(flowDetailInfo.getSourceIp());
        flowDetailVO.setSourcePort(flowDetailInfo.getSourcePort());
        flowDetailVO.setSourceMac(flowDetailInfo.getSourceMac());
        flowDetailVO.setTargetIp(flowDetailInfo.getTargetIp());
        flowDetailVO.setTargetPort(flowDetailInfo.getTargetPort());
        flowDetailVO.setTargetMac(flowDetailInfo.getTargetMac());
        flowDetailVO.setTeamId(flowQueryRequest.getTeamId());
        return new ServiceResult<>(flowDetailVO).asResult();
    }

    @GetMapping("/page")
    @ApiOperation(value = "筛选战队流量信息", notes = "")
    public ListObjServiceResult<TeamFlowVO> queryFlowPageEvents(PageFlowRequest request) {
        request.ensure();

        var teamFlowInfoListObjResult = flowService.queryFlowPage(request).getSource();

        if (CollectionUtil.isEmpty(teamFlowInfoListObjResult.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        List<TeamFlowVO> teamFlowVOList = new ArrayList<>();
        teamFlowInfoListObjResult.getObjs().forEach(teamFlowInfo -> {
            var teamFlow = new TeamFlowVO();
            teamFlow.setTeamId(request.getTeamId());
            teamFlow.setTime(teamFlowInfo.getTime());
            teamFlow.setDataId(teamFlowInfo.getDataId());
            teamFlow.setProtocol(teamFlowInfo.getProtocol());
            teamFlow.setSize(teamFlowInfo.getSize());
            teamFlow.setSourceIp(teamFlowInfo.getSourceIp());
            teamFlow.setTargetIp(teamFlowInfo.getTargetIp());
            teamFlow.setSourcePort(teamFlowInfo.getSourcePort());
            teamFlowVOList.add(teamFlow);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(teamFlowVOList, teamFlowInfoListObjResult.getTotal())).asResult();
    }

}
