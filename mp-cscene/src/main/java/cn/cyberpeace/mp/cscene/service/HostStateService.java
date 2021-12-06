package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 20:19
 */
@Slf4j
@Service
public class HostStateService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneIdTranslationService sceneIdTranslationService;

    /**
     * 查询单个节点机器当前状态
     *
     * @return
     */
    public List<HostState> getHostStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        String sceneId = baseRequest.getSceneId();
        List<String> elementIds = baseRequest.getElementIds();
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setNodeIds(elementIds);
        queryParams.setPage(1);
        queryParams.setPageSize(1);
//
        ListObjServiceResult<CollectionHostStateBO> listObjServiceResult = csceneInstanceService.listHostStates(sceneId, queryParams);

        List<CollectionHostStateBO> hostStateBOS = listObjServiceResult.getSource().getList();

        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(hostStateBO.getCpuPercent());
            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            hs.setDiskSpace(hostStateBO.getDiskSpace());
            hs.setSceneId(hostStateBO.getSceneId());
            hs.setElementId(hostStateBO.getNodeId());
            hs.setTime(hostStateBO.getTime());
            return hs;
        }).collect(Collectors.toList());

        return hostStates;
    }


    /**
     * 查询单个节点机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getElementHostStateTrend(HostStateTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processHostStateTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    /**
     * 查询场景机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getSceneHostStateTrend(HostStateTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processHostStateTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }

    private List<HostState> processHostStateTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔48小时
            startTime = endTime.minusHours(48);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        String intervalSecondStr = intervalSeconds + "";

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgHostMemoryPercent(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalAvgHostDiskPercent(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));

        List<IntervalSizeBO<LocalDateTime, Double>> intervalCpuPercentBOs = cpuResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalMemPercentBOs = memResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalDiskPercentBOs = diskResult.getSource();

        List<HostState> hostStates = new ArrayList<>();
        int count = 0;
        if (Objects.nonNull(intervalCpuPercentBOs)) {
            intervalCpuPercentBOs.sort(Comparator.comparing(IntervalSizeBO::getEnd));
            for (int i = 0; i < intervalCpuPercentBOs.size(); i++) {
                HostState hs = new HostState();
                hs.setTime(intervalCpuPercentBOs.get(i).getEnd());
                hs.setCpuPercent(intervalCpuPercentBOs.get(i).getSize());
                hostStates.add(hs);
            }

        }

        if (Objects.nonNull(intervalMemPercentBOs)) {
            intervalMemPercentBOs.sort(Comparator.comparing(IntervalSizeBO::getEnd));
            count = intervalMemPercentBOs.size();
        }

        // todo diskPercent
       /* if (Objects.nonNull(intervalDiskPercentBOs)) {
            intervalDiskPercentBOs.sort(Comparator.comparing(IntervalSizeBO::getEnd));
            count = intervalDiskPercentBOs.size();
        }*/

        for (int i = 0; i < count; i++) {
            HostState hs = new HostState();

            if (Objects.nonNull(intervalCpuPercentBOs)) {
                hs.setTime(intervalCpuPercentBOs.get(i).getEnd());
                hs.setCpuPercent(intervalCpuPercentBOs.get(i).getSize());
            }
            if (Objects.nonNull(intervalMemPercentBOs)) {
                hs.setTime(intervalMemPercentBOs.get(i).getEnd());
                hs.setMemoryPercent(intervalMemPercentBOs.get(i).getSize());
            }
            hs.setDiskPercent(40.0);
            hostStates.add(hs);
        }

        return hostStates;
    }

}
