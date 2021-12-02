package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrendSceneElementRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/30 13:41
 */
@Slf4j
@Service
public class FlowMonitorService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;


    /**
     * 查询单个节点机器流量趋势
     *
     * @param trendRequest
     * @return
     */
    public List<FlowTrend> getElementFlowTrend(FlowTrendSceneElementRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processFlowTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    /**
     * 查询场景机器流量趋势
     *
     * @param trendRequest
     * @return
     */
    public List<FlowTrend> getSceneFlowTrend(FlowTrendSceneElementRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processFlowTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    public FlowRiskCount getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = CollectionUtil.toList(baseRequest.getSceneId());

        List<String> nodeIds = null;
        if (!CollectionUtil.isEmpty(baseRequest.getElementIds())) {
            nodeIds = CollectionUtil.toList(baseRequest.getElementIds().get(0));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now;
        LocalDateTime startTime = now.minusMinutes(10);

        // todo 转好

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByLevel(sceneIds, nodeIds, startTime, endTime);
        Map<String, Integer> map = mapResult.getSource();

        // 风险等级 1,2,3,4 数字越低，级别越高
        FlowRiskCount riskCount = new FlowRiskCount();
        riskCount.setHighCount(map.get("1") == null ? 0 : map.get("1"));
        riskCount.setMediumCount(map.get("2") == null ? 0 : map.get("2"));
        Integer low1 = map.get("3") == null ? 0 : map.get("3");
        Integer low2 = map.get("4") == null ? 0 : map.get("4");
        riskCount.setLowCount(low1 + low2);
        return riskCount;
    }


    /**
     * 流量分页接口包装
     * 根据攻击类型拆分
     *
     * @param id
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams) {
        ListObjServiceResult<CollectionFlowBO> flowBOResult = csceneInstanceService.listFlows(id, queryParams);
        List<CollectionFlowBO> flowBOS = flowBOResult.getSource().getList();

        List<FlowAttackDto> flowAttackDtos = new ArrayList<>();

        for (CollectionFlowBO flowBO : flowBOS) {
            for (AttackInfo attackInfo : flowBO.getAttackInfos()) {
                FlowAttackDto flowAttackDto = new FlowAttackDto();
                BeanUtils.copyProperties(flowBO, flowAttackDto);
                flowAttackDto.setAttackInfo(attackInfo);
                flowAttackDtos.add(flowAttackDto);
            }
        }

        List<FlowAttackDto> finalList = flowAttackDtos.subList(0, 1000);

        return new ListObjServiceResult(new ListObjResult(finalList, finalList.size()));
    }


    private List<FlowTrend> processFlowTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            startTime = endTime.minusHours(1);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        // too 转好
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> flowResult = scenePluginCollectionSceneService.timeIntervalSumFlowSize(sceneIds, elementIds, startTime, endTime, segmentCount);

        List<IntervalSizeBO<LocalDateTime, Long>> intervalFlowSizeBOs = flowResult.getSource();

        intervalFlowSizeBOs.sort(Comparator.comparing(IntervalSizeBO::getEnd));

        List<FlowTrend> flowTrends = new ArrayList<>();

        for (int i = 0; i < intervalFlowSizeBOs.size(); i++) {
            FlowTrend ft = new FlowTrend();
            ft.setTime(intervalFlowSizeBOs.get(i).getEnd());
            ft.setDataSize(intervalFlowSizeBOs.get(i).getSize());
            flowTrends.add(ft);
        }

        return flowTrends;
    }

}
