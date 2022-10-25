package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.bo.AttackTypeBO;
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.bo.FlowAttackBO;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collection.util.MessageCommonUtil;
import cn.cyberpeace.mp.collectionapi.request.AttackAggTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest;
import cn.cyberpeace.mp.collectionapi.util.ContestConfigUtil;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackEquipmentVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopVO;
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
import cn.cyberpeace.mp.collectionapi.service.FlowQueryService;
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

    @Resource
    private FlowQueryService flowQueryService;

    @GetMapping("/flow_attack_info")
    @ApiOperation(value = "查询每个设备的被攻击次数、流量大小、攻击手法占比")
    public ListObjServiceResult<FlowAttackVO> flowAttackInfo() {
        // todo 每个设备可能有多个ip 需要合并数据
        List<FlowAttackBO> bos = flowInfoService.queryFlowAttackInfo(ContestConfigUtil.getIpsByType(null, null));
        Map<String, FlowAttackVO> vosMap = Maps.newHashMap();
        bos.forEach(bo -> {
            String name = ContestConfigUtil.getNameByIp(bo.getIp());
            FlowAttackVO vo = vosMap.computeIfAbsent(name, key -> new FlowAttackVO());
            vo.setEquipment(name);
            vo.setAttackCount(vo.getAttackCount() + bo.getCount());
            vo.setSize(vo.getSize() + bo.getSize());

            // 攻击手法合并
            List<AttackTypeVO> atVOs = vo.getAttackTypes();
            if (atVOs == null) {
                atVOs = Lists.newArrayList();
                vo.setAttackTypes(atVOs);
            }

            List<AttackTypeVO> tempAtVOs = Lists.newArrayList();
            for (AttackTypeBO atBO : bo.getAttackTypes()) {
                boolean merged = false;
                for (AttackTypeVO atVO : atVOs) {
                    if (atVO.getName().equals(atBO.getEnName())) {
                        atVO.setCount(atVO.getCount() + atBO.getCount());
                        merged = true;
                        break;
                    }
                }
                if (!merged) {
                    AttackTypeVO atVO = new AttackTypeVO();
                    atVO.setName(atBO.getEnName());
                    atVO.setCount(atBO.getCount());
                    tempAtVOs.add(atVO);
                }
            }
            atVOs.addAll(tempAtVOs);

            // 攻击手法占比计算
            double total = atVOs.stream().mapToLong(AttackTypeVO::getCount).sum();
            atVOs.forEach(v -> {
                v.setPercent(v.getCount() / total);
            });
        });

        List<FlowAttackVO> vos = vosMap.values().stream().collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
    }

    @GetMapping("/team_attack_count")
    @ApiOperation(value = "每个队伍的攻击次数(累计) top10")
    public ListObjServiceResult<TeamAttackCountVO> teamAttackCount() {
        // todo 每个战队一个ip
        return flowQueryService.queryTeamAttackCountTop10();
    }

    @GetMapping("/attack_type")
    @ApiOperation(value = "所有队伍的攻击类型占比top5")
    public ListObjServiceResult<AttackTypeVO> attackType() {
        return flowQueryService.queryAttackTypeTop5();
    }

    @GetMapping("/attack_trend")
    @ApiOperation(value = "所有队伍的总体攻击趋势(统计十分钟内的攻击次数)")
    public ListObjServiceResult<FlowAttackVO> attackTrend() {
        // 默认分十段
        return flowQueryService.queryAttackTrend();
    }

    @GetMapping("/attack_level_trend")
    @ApiOperation(value = "攻击告警级别趋势(每十分钟统计一次)")
    public ListObjServiceResult<AttackLevelVO> attackLevelTrend() {
        // 默认分十段
        return flowQueryService.queryLevelTrend();
    }

    @GetMapping("/attack_total_count")
    @ApiOperation(value = "总攻击次数，可自定义放大速率")
    public ServiceResult<Long> attackTotalCount(Integer multiple) {
        return new ServiceResult<>();
    }

    @GetMapping("/count_with_ip")
    @ApiOperation(value = "根据ip集合查询总攻击次数")
    public ServiceResult<Long> countWithIp(AttackFlowBaseRequest request) {
        return new ServiceResult<>();
    }

    @GetMapping("/attack_flow_list")
    @ApiOperation(value = "攻击流量列表")
    public ListObjServiceResult<FlowVO> attackFlowList() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("/equipment")
    @ApiOperation(value = "被攻击设备top5")
    public ListObjServiceResult<AttackEquipmentVO> equipment() {
        // todo 每种设备有多个ip 需要合并数据
        return flowQueryService.queryEquipmentTypeTop5();
    }

    @GetMapping("/protocol")
    @ApiOperation(value = "攻击协议top5(应用层)")
    public ListObjServiceResult<AttackProtocolVO> protocol() {
        // todo 只需要应用层协议 过滤后少于五种则再次查询
        return flowQueryService.queryProtocolTop5();
    }

    @GetMapping("/team_flow_agg")
    @ApiOperation(value = "所有战队的流量大小、攻击次数、扰动次数和占比")
    public ListObjServiceResult<TeamFlowAggVO> teamFlowAgg() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("/team_attack_target")
    @ApiOperation(value = "战队攻击目标top5和攻击手法top5")
    public ListObjServiceResult<TeamAttackVO> teamAttackTarget() {
        return new ListObjServiceResult<>();
    }

    @GetMapping("/team_attack_trend")
    @ApiOperation(value = "战队的攻击趋势(统计六小时数据,每五分钟统计一次)")
    public ListObjServiceResult<TeamAttackTrendVO> teamAttackTrend(AttackFlowBaseRequest request) {
        // todo 每个战队一个ip
        return new ListObjServiceResult<>();
    }

    @GetMapping("/equipment_attack_count")
    @ApiOperation(value = "设备被攻击次数")
    public ListObjServiceResult<AttackTargetVO> equipmentAttackCount(@RequestParam(required = false) String type) {
        List<AttackTargetVO> vos = Lists.newArrayList();
        for (int useType = 0; useType < 2; useType++) {
            // 0:商用 1:拟态
            AttackTargetVO vo = new AttackTargetVO();
            vo.setName(type);
            vo.setUseType(useType);
            vo.setCount(flowInfoService.queryEquipmentAttackCount(ContestConfigUtil.getIpsByType(type, useType)));
            vos.add(vo);
        }

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
    }

    @GetMapping("/equipment_attack_count_list")
    @ApiOperation(value = "设备被攻击次数列表")
    public ListObjServiceResult<AttackTargetListVO> equipmentAttackCountInfo() {
        List<AttackTargetListVO> vos = Lists.newArrayList();
        for (int useType = 0; useType < 2; useType++) {
            // 0:商用 1:拟态
            List<String> ips = ContestConfigUtil.getIpsByType(null, useType);
            // 设备被攻击次数
            List<CountBO> bos = flowInfoService.queryEquipmentAttackCountInfo(ips);
            Map<String, Long> typeCountMap = Maps.newHashMap();
            bos.forEach(bo -> {
                String type = ContestConfigUtil.getTpyeByIp(bo.getKey());
                if (type == null) {
                    return;
                }
                Long count = typeCountMap.get(type);
                typeCountMap.put(type, count == null ? bo.getCount() : bo.getCount() + count);
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
            bos = flowInfoService.queryEquipmentAttackTypeTop5(ips);
            double totalAttack = bos.stream().mapToLong(CountBO::getCount).sum();
            List<AttackTypeVO> attackTypes = bos.stream().map(bo -> {
                AttackTypeVO vo = new AttackTypeVO();
                vo.setName(MessageCommonUtil.convertMessage(bo.getKey()));
                vo.setCount(bo.getCount());
                vo.setPercent(bo.getCount() / totalAttack);
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

    @GetMapping("/agg_attack_flow")
    @ApiOperation(value = "获取指定ip集合攻击次数与流量大小")
    public ServiceResult<List<AggAttackFlowVO>> getAttackFlow(AttackAggTopRequest request) {
        return null;
    }

    @GetMapping("/attack_top")
    @ApiOperation(value = "获取top数据,(降序)默认全部")
    public ServiceResult<List<AttackFlowTopVO>> getAttackTop(AttackFlowTopRequest request) {
        return null;
    }

    @GetMapping("/attack_agg_top")
    @ApiOperation(value = "获取top聚合数据,(降序)默认前三")
    public ServiceResult<List<TeamAttackCountVO>> getAttackAggTop(AttackAggTopRequest request) {
        return null;
    }
}
