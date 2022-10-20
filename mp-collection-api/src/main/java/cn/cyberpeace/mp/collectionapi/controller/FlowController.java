package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig;
import cn.cyberpeace.mp.collectionapi.vo.AttackEquipmentVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackTargetListVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackTargetVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackTypeVO;
import cn.cyberpeace.mp.collectionapi.vo.FlowAttackVO;
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackProtocolVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackTrendVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamFlowAggVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @Resource
    private FlowInfoService flowInfoService;
    
    @GetMapping("flow_attack_info")
    @ApiOperation(value = "查询每个设备的被攻击次数、流量大小、攻击手法占比")
    public ListObjServiceResult<FlowAttackVO> flowAttackInfo() {
        // todo 每个设备可能有多个ip 需要合并数据
        return new ListObjServiceResult<>();
    }

    @GetMapping("team_attack_count")
    @ApiOperation(value = "每个队伍的攻击次数(累计) top10")
    public ListObjServiceResult<TeamAttackCountVO> teamAttackCount() {
        // todo 每个战队一个ip
        return new ListObjServiceResult<>();
    }

    @GetMapping("attack_type")
    @ApiOperation(value = "所有队伍的攻击类型占比top5")
    public ListObjServiceResult<AttackTypeVO> attackType() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("attack_trend")
    @ApiOperation(value = "所有队伍的总体攻击趋势(每隔十分钟统计一次总攻击次数)")
    public ListObjServiceResult<FlowAttackVO> attackTrend() {
        // 默认分十段
        return new ListObjServiceResult<>();
    }

    @GetMapping("attack_level_trend")
    @ApiOperation(value = "攻击告警级别趋势(每十分钟统计一次)")
    public ListObjServiceResult<AttackLevelVO> attackLevelTrend() {
        // 默认分五段
        return new ListObjServiceResult<>();
    }

    @GetMapping("attack_total_count")
    @ApiOperation(value = "总攻击次数，可自定义放大速率")
    public ListObjServiceResult<FlowAttackVO> attackTotalCount() {
        // 默认分十段
        return new ListObjServiceResult<>();
    }

    @GetMapping("attack_flow_list")
    @ApiOperation(value = "攻击流量列表")
    public ListObjServiceResult<FlowVO> attackFlowList() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("equipment")
    @ApiOperation(value = "被攻击设备top5")
    public ListObjServiceResult<AttackEquipmentVO> equipment() {
        // todo 每种设备有多个ip 需要合并数据
        return new ListObjServiceResult<>();
    }

    @GetMapping("protocol")
    @ApiOperation(value = "攻击协议top5(应用层)")
    public ListObjServiceResult<AttackProtocolVO> protocol() {
        // todo 只需要应用层协议 过滤后少于五种则再次查询
        return new ListObjServiceResult<>();
    }

    @GetMapping("team_flow_agg")
    @ApiOperation(value = "所有战队的流量大小、攻击次数、扰动次数和占比")
    public ListObjServiceResult<TeamFlowAggVO> teamFlowAgg() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("team_attack_target")
    @ApiOperation(value = "战队攻击目标top5和攻击手法top5")
    public ListObjServiceResult<TeamAttackVO> teamAttackTarget() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("team_attack_trend")
    @ApiOperation(value = "战队的攻击趋势(每小时统计)")
    public ListObjServiceResult<TeamAttackTrendVO> teamAttackTrend() {
        // todo 每个战队一个ip
        return new ListObjServiceResult<>();
    }

	@GetMapping("equipment_attack_count")
	@ApiOperation(value = "设备被攻击次数")
	public ListObjServiceResult<AttackTargetVO> equipmentAttackCount(@RequestParam(required = false) String type) {
		List<AttackTargetVO> vos = Lists.newArrayList();
		for (int useType = 0; useType < 2; useType++) {
			// 0:商用 1:拟态
			AttackTargetVO vo = new AttackTargetVO();
			vo.setName(type);
			vo.setUseType(useType);
			vo.setCount(flowInfoService.queryEquipmentAttackCount(ContestConfig.getIpsByType(type, useType)).getSource());
			vos.add(vo);
		}

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	@GetMapping("equipment_attack_count_list")
	@ApiOperation(value = "设备被攻击次数列表")
	public ListObjServiceResult<AttackTargetListVO> equipmentAttackCountInfo() {
		List<AttackTargetListVO> vos = Lists.newArrayList();
		for (int useType = 0; useType < 2; useType++) {
			// 0:商用 1:拟态
			List<String> ips = ContestConfig.getIpsByType(null, useType);
			// 设备被攻击次数
			Map<String, Long> ipCountMap = flowInfoService.queryEquipmentAttackCountInfo(ips);
			Map<String, Long> typeCountMap = Maps.newHashMap();
			ipCountMap.entrySet().forEach(entry -> {
				String type = ContestConfig.getTpyeByIp(entry.getKey());
				if (type == null) {
					return;
				}
				Long count = typeCountMap.get(type);
				typeCountMap.put(type, count == null ? entry.getValue() : entry.getValue() + count);
			});

			final int USE_TYPE = useType;
			List<AttackTargetVO> attackTargets = typeCountMap.entrySet().stream().map(entry -> {
				AttackTargetVO vo = new AttackTargetVO();
				vo.setName(entry.getKey());
				vo.setCount(entry.getValue());
				vo.setUseType(USE_TYPE);
				return vo;
			}).collect(Collectors.toList());

			// 设备被攻击手法top5
			Map<String, Long> attackTypeCountMap = flowInfoService.queryEquipmentAttackTypeTop5(ips);
			double totalAttack = attackTypeCountMap.values().stream().reduce(Long::sum).orElse(0L);
			List<AttackTypeVO> attackTypes = attackTypeCountMap.entrySet().stream().map(entry -> {
				AttackTypeVO vo = new AttackTypeVO();
				vo.setZhName(entry.getKey());
				vo.setCount(entry.getValue());
				vo.setPercent(entry.getValue() / totalAttack);
				return vo;
			}).collect(Collectors.toList());

			AttackTargetListVO vo = new AttackTargetListVO();
			vo.setUseType(useType);
			vo.setAttackTargets(attackTargets);
			vo.setCount(attackTargets.stream().mapToLong(AttackTargetVO::getCount).sum());
			vo.setAttackTypes(attackTypes);
			vos.add(vo);
		}

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}
}
