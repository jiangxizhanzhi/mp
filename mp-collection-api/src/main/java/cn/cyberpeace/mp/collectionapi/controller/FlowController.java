package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.vo.FlowDetailInfo;
import cn.cyberpeace.mp.collection.vo.TeamFlowInfo;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.service.FlowService;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collectionapi.vo.TeamFlowVO;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
