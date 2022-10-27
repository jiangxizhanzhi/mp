package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collectionapi.request.AttackAggTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackTargetTopRequest;
import cn.cyberpeace.mp.collectionapi.service.FlowQueryService;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopListVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackTargetTopVO;
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackTrendVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

	@Resource
	private FlowQueryService flowQueryService;

	@GetMapping("/attack_trend")
	@ApiOperation(value = "所有队伍的总体攻击趋势(统计十分钟内的攻击次数)")
	public ListObjServiceResult<AttackFlowTopVO> attackTrend() {
		// 默认分十段
		return flowQueryService.queryAttackTrend();
	}

	@GetMapping("/attack_level_trend")
	@ApiOperation(value = "攻击告警级别趋势(每十分钟统计一次)")
	public ListObjServiceResult<AttackLevelVO> attackLevelTrend() {
		// 默认分十段
		return flowQueryService.queryLevelTrend();
	}
	
	@GetMapping("/team_attack_trend")
	@ApiOperation(value = "战队的攻击趋势(统计六小时数据,每五分钟统计一次)")
	public ListObjServiceResult<TeamAttackTrendVO> teamAttackTrend(@RequestBody AttackFlowBaseRequest request) {
		// todo 每个战队一个ip
		return flowQueryService.teamAttackTrend(request);
	}

	@GetMapping("/attack_total_count")
	@ApiOperation(value = "总攻击次数，可自定义放大速率")
	public ServiceResult<Long> attackTotalCount(@RequestParam(required = false, defaultValue = "1") Integer multiple) {
		return flowQueryService.getAttackTotalCount(multiple);
	}

	@GetMapping("/attack_flow_list")
	@ApiOperation(value = "攻击流量列表")
	public ListObjServiceResult<FlowVO> attackFlowList(
			@RequestParam(required = false, defaultValue = "1") Integer pageNum,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		return flowQueryService.attackFlowList(pageNum, pageSize);
	}

	@GetMapping("/agg_attack_flow")
	@ApiOperation(value = "获取指定ip集合攻击次数与流量大小")
	public ListObjServiceResult<AggAttackFlowVO> getAttackFlow(@RequestBody AttackFlowBaseRequest request) {
		return flowQueryService.getAttackFlow(request);
	}

	@GetMapping("/attack_top")
	@ApiOperation(value = "获取top数据,(降序)默认全部")
	public ListObjServiceResult<AttackFlowTopVO> getAttackTop(@RequestBody AttackFlowTopRequest request) {
		return flowQueryService.getAttackTop(request);
	}

	@GetMapping("/attack_agg_top")
	@ApiOperation(value = "获取top聚合数据,(降序)默认全部")
	public ListObjServiceResult<TeamAttackCountVO> getTeamAttackAggTop(@RequestBody AttackAggTopRequest request) {
		return flowQueryService.getTeamAttackAggTop(request);
	}

    @GetMapping("/attack_target_top")
    @ApiOperation(value = "获取攻击目标top数据,默认全部")
    public ListObjServiceResult<AttackTargetTopVO> getAttackTargetTop(@RequestBody AttackTargetTopRequest request) {
        return null;
    }

	@GetMapping("/attack_type_top")
	@ApiOperation(value = "获取攻击手法top数据,默认全部")
	public ListObjServiceResult<AttackFlowTopListVO> getAttackTypeTop(@RequestBody AttackTargetTopRequest request) {
		return null;
	}
}
