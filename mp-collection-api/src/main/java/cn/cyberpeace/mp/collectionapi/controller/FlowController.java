package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collectionapi.constant.CollectionApiConstant;
import cn.cyberpeace.mp.collectionapi.request.AttackAggTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackTargetTopRequest;
import cn.cyberpeace.mp.collectionapi.service.FlowQueryService;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopVO;
import cn.cyberpeace.mp.collectionapi.vo.TrendVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamTopVO;
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;

@RestController
@RequestMapping("/flow")
@Api(value = "", tags = "")
public class FlowController {

    @Resource
    private FlowQueryService flowQueryService;

    @Resource
    private BaseAppConfig baseAppConfig;

    @GetMapping("/attack_trend")
    @ApiOperation(value = "总体攻击趋势(统计十分钟内的攻击次数)")
    public ServiceResult<TrendVO> attackTrend() {
        // 默认分六段
        return flowQueryService.queryAttackTrend().asResult();
    }

    @GetMapping("/attack_level_trend")
    @ApiOperation(value = "攻击告警级别趋势(每十分钟统计一次)")
    public ListObjServiceResult<TrendVO> attackLevelTrend() {
        // 默认分六段
        return flowQueryService.queryLevelTrend().asResult();
    }

    @GetMapping("/team_attack_trend")
    @ApiOperation(value = "战队的攻击趋势(统计六小时数据,每五分钟统计一次)")
    public ListObjServiceResult<TrendVO> teamAttackTrend(@RequestBody AttackFlowBaseRequest request) {
        return flowQueryService.teamAttackTrend(request).asResult();
    }

    @GetMapping("/attack_total_count")
    @ApiOperation(value = "总攻击次数")
    public ServiceResult<Long> attackTotalCount() {
        return flowQueryService.getAttackTotalCount().asResult();
    }

    @GetMapping("/attack_flow_list")
    @ApiOperation(value = "攻击流量列表")
    public ListObjServiceResult<FlowVO> attackFlowList(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return flowQueryService.attackFlowList(pageNum, pageSize).asResult();
    }

    @GetMapping("/agg_attack_flow")
    @ApiOperation(value = "获取指定ip集合(队伍 or 设备)攻击次数与流量大小")
    public ListObjServiceResult<AggAttackFlowVO> getAttackFlow(@RequestBody AttackFlowBaseRequest request) {
        return flowQueryService.getAttackFlow(request).asResult();
    }

    @GetMapping("/attack_top")
    @ApiOperation(value = "全局攻防榜，获取4种top数据,(降序)默认全部")
    public ListObjServiceResult<AttackFlowTopVO> getAttackTop(@RequestBody AttackFlowTopRequest request) {
        return flowQueryService.getAttackTop(request).asResult();
    }

    @GetMapping("/attack_agg_top")
    @ApiOperation(value = "全局攻防榜，获取战队top聚合数据,(降序)默认全部")
    public ListObjServiceResult<TeamAttackCountVO> getTeamAttackAggTop(@RequestBody AttackAggTopRequest request) {
        return flowQueryService.getTeamAttackAggTop(request).asResult();
    }

    @GetMapping("/attack_target_top")
    @ApiOperation(value = "选手攻击榜，获取攻击目标top数据,(降序)默认全部")
    public ListObjServiceResult<TeamTopVO> getAttackTargetTop(@RequestBody AttackTargetTopRequest request) {
        return flowQueryService.getAttackTargetTop(request).asResult();
    }

    @GetMapping("/attack_type_top")
    @ApiOperation(value = "选手攻击榜，获取攻击手法top数据,(降序)默认全部")
    public ListObjServiceResult<TeamTopVO> getAttackTypeTop(@RequestBody AttackAggTopRequest request) {
        return flowQueryService.getAttackTypeTop(request).asResult();
    }

    @PostMapping("/update_multiple")
    @ApiOperation(value = "更新攻击次数放大倍数")
    public ServiceResult<Boolean> updateMultiple(@RequestParam String multiple) {
        CollectionApiConstant.multiple = BigDecimal.valueOf(Double.valueOf(multiple));
        String logDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getHomeDir(), "multiple");
        File file = new File(logDir);
        if (!file.exists()) {
            file.mkdir();
        }
        {
            // 先删除旧的
            File valueFile = new File(logDir + FileUtil.FILE_SEPARATOR + "value");
            if (valueFile.exists()) {
                valueFile.delete();
            }
        }
        File valueFile = new File(logDir + FileUtil.FILE_SEPARATOR + "value");
        FileUtil.writeBytes(multiple.getBytes(), valueFile);
        return new ServiceResult<>(true).asResult();
    }
}
