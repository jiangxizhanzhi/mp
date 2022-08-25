package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collection.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.collection.BO.FlowDetailInfo;
import cn.cyberpeace.mp.collection.BO.TeamFlowInfo;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collectionapi.vo.FlowDetailVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamFlowVO;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @Resource
    private FlowInfoService flowInfoService;

    @GetMapping("/detail")
    @ApiOperation(value = "查询一条流量的详细信息", notes = "")
    public ServiceResult<FlowDetailVO> queryFlowDetailInfo(FlowQueryRequest flowQueryRequest) {

        var flowDetailInfo = flowInfoService.queryFlowDetail(flowQueryRequest).getSource();

        if (Objects.isNull(flowDetailInfo) || StringUtils.isEmpty(flowDetailInfo.getSourceIp()) || StringUtils.isEmpty(flowDetailInfo.getTargetIp())) {
            return new ServiceResult<>(new FlowDetailVO(), BaseErrorCode.PARAMS_INVALID).asResult();
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
            teamFlow.setTargetIp(teamFlowInfo.getTargetIp());
            teamFlow.setSourcePort(teamFlowInfo.getSourcePort());
            teamFlowVOList.add(teamFlow);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(teamFlowVOList, teamFlowInfoListObjResult.getTotal())).asResult();
    }

}
