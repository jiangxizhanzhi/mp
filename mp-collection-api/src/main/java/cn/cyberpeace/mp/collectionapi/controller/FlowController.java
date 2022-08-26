package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collectionapi.BO.FlowTarget;
import cn.cyberpeace.mp.collectionapi.util.GroupUtil;
import cn.cyberpeace.mp.collectionapi.vo.TeamFlowVO;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @Resource
    private FlowInfoService flowInfoService;

    @GetMapping("/detail")
    @ApiOperation(value = "查询一条流量的详细信息", notes = "")
    public ServiceResult<Map<String, Object>> queryFlowDetailInfo(FlowQueryRequest flowQueryRequest) {

        var flowDetailInfo = flowInfoService.queryFlowDetail(flowQueryRequest).getSource();
        if (CollectionUtil.isEmpty(flowDetailInfo)) {
            Map<String, Object> flowMap = new HashMap<>();
            return new ServiceResult<>(flowMap).asResult();
        }
        return new ServiceResult<>(flowDetailInfo).asResult();
    }

    @GetMapping("/page")
    @ApiOperation(value = "筛选战队流量信息", notes = "")
    public ListObjServiceResult<TeamFlowVO> queryFlowPageEvents(PageFlowRequest request) {
        request.ensure();
        if (CollectionUtil.isEmpty(request.getAgentIps()) || StringUtils.isEmpty(request.getTeamId())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<TeamFlowVO>(), 0), BaseErrorCode.PARAMS_INVALID).asResult();
        }
        var flowTarget = this.getTargetIps(request.getAgentIps(), request.getTeamId());
        if (!CollectionUtil.isEmpty(request.getAgentIps())) {
            request.setAgentIps(flowTarget.getTargetIps());
        }
        var teamFlowInfoListObjResult = flowInfoService.queryFlowPage(request).getSource();

        if (CollectionUtil.isEmpty(teamFlowInfoListObjResult.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<TeamFlowVO>(), 0)).asResult();
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
            teamFlow.setTargetIp(flowTarget.getTarget2Node().get(teamFlowInfo.getTargetIp()));
            teamFlow.setSourcePort(teamFlowInfo.getSourcePort());
            teamFlowVOList.add(teamFlow);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(teamFlowVOList, teamFlowInfoListObjResult.getTotal())).asResult();
    }

    private FlowTarget getTargetIps(List<String> agentIps, String teamId) {
        var group = GroupUtil.getGroup(teamId);
        FlowTarget flowTarget = new FlowTarget();
        List<String> targetIps = new ArrayList<>();
        Map<String, String> target2Node = new HashMap<>();
        if (Objects.isNull(group)) {
            return flowTarget;
        }

        group.getNodes().forEach(node -> {
            if (agentIps.contains(node.getNodeIp())) {
                targetIps.addAll(node.getTargetIps());
                node.getTargetIps().forEach(t -> {
                    target2Node.put(t, node.getNodeIp());
                });
            }
        });
        flowTarget.setTargetIps(targetIps);
        flowTarget.setTarget2Node(target2Node);

        return flowTarget;
    }

}
