package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.HostStateTrendSceneElementRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
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
            // todo 1201 内存MB，磁盘GB转换
//            hs.setMemoryFree(Integer.valueOf(hostStateBO.getMemoryFree()));
//            hs.setMemoryTotal(Integer.valueOf(hostStateBO.getMemoryTotal()));
//            hs.setDiskSpace(hostStateBO.getDiskSpace());
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
    public List<HostState> getElementHostStateTrend(HostStateTrendSceneElementRequest trendRequest) {
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
    public List<HostState> getSceneHostStateTrend(HostStateTrendSceneElementRequest trendRequest) {
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
            startTime = endTime.minusHours(1);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIds, elementIds, startTime, endTime, segmentCount);
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgHostMemoryPercent(sceneIds, elementIds, startTime, endTime, segmentCount);

        List<IntervalSizeBO<LocalDateTime, Long>> intervalCpuSizeBOs = cpuResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Long>> intervalMemSizeBOs = memResult.getSource();

        intervalCpuSizeBOs.sort(Comparator.comparing(IntervalSizeBO::getEnd));
        intervalMemSizeBOs.sort(Comparator.comparing(IntervalSizeBO::getEnd));

        List<HostState> hostStates = new ArrayList<>();

        for (int i = 0; i < intervalCpuSizeBOs.size(); i++) {
            HostState hs = new HostState();
            hs.setTime(intervalCpuSizeBOs.get(i).getEnd());
            hs.setCpuPercent(Double.valueOf(intervalCpuSizeBOs.get(i).getSize()));
            hs.setMemoryPercent(Double.valueOf(intervalMemSizeBOs.get(i).getSize()));
            // todo 1201 内存MB，磁盘GB转换
            hostStates.add(hs);
        }

        return hostStates;
    }

}
