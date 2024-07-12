 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneHostCollectQueryService.java.
 // File size 15066.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.cscene.dto.element.ElementHistogramInfo;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.TimeHistogramInfo;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhResult;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.filemodify.WazuhFileModifyService;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.registrymodify.WazuhRegistryModifyService;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName CsceneHostCollectQueryService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class CsceneHostCollectQueryService {

    @Resource
    private CsceneInstanceService csceneInstanceService;

    @Resource
    private WazuhFileModifyService wazuhFileModifyService;

    @Resource
    private WazuhRegistryModifyService wazuhRegistryModifyService;

    /**
     * getFileModifyData
     *
     * @param from
     * @param size
     * @param csceneId
     * @param nodeIds
     * @param queryStartAt
     * @param queryEndAt
     * @param levels
     * @param filePath
     * @return
     */
    public ListObjServiceResult<CollectionHostEventBO> getFileModifyData(int from, int size, String csceneId, List<String> nodeIds, LocalDateTime queryStartAt, LocalDateTime queryEndAt, List<Integer> levels, String filePath) {
        CheckResult checkResult = this.checkAndGetInfo(csceneId, nodeIds);
        if (checkResult.getCheckEnum() != CheckEnum.SUCCESS) {
            return new ListObjServiceResult<>(new ArrayList<>(), 0);
        }

        BatchSceneHandler sceneHandler = checkResult.getSceneHandler();
        String sceneId = checkResult.getSceneId();

        LocalDateTime startTime = queryStartAt != null ? queryStartAt : sceneHandler.getEarliestStartTime();
        LocalDateTime endTime = queryEndAt != null ? queryEndAt : sceneHandler.getLatestSceneRunningTime();

        List<String> agentNames = checkResult.getAgentNames();
        WazuhResult<FileEventModel> result = wazuhFileModifyService.queryFileModifyData(from, size, agentNames, startTime, endTime, levels, filePath);

        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getCloudScenePluginHandler(sceneId);
        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        for (FileEventModel fileEventModel : result.getList()) {
            CollectionHostEventBO hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(csceneId);
            this.writeBaseInfo(fileEventModel, hostEvent);
            this.writeNodeInfo(fileEventModel.getAgentName(), sceneHandler, cloudScenePluginHandler, hostEvent);
            hostEvent.setAppName("");
            hostEvents.add(hostEvent);
        }
        return new ListObjServiceResult<>(hostEvents, (int) result.getTotal());
    }

    /**
     * getRegistryModifyData
     *
     * @param from
     * @param size
     * @param csceneId
     * @param nodeIds
     * @param queryStartAt
     * @param queryEndAt
     * @param levels
     * @param filePath
     * @return
     */
    public ListObjServiceResult<CollectionHostEventBO> getRegistryModifyData(int from, int size, String csceneId, List<String> nodeIds, LocalDateTime queryStartAt, LocalDateTime queryEndAt, List<Integer> levels, String filePath) {
        CheckResult checkResult = this.checkAndGetInfo(csceneId, nodeIds);
        if (checkResult.getCheckEnum() != CheckEnum.SUCCESS) {
            return new ListObjServiceResult<>(new ArrayList<>(), 0);
        }

        BatchSceneHandler sceneHandler = checkResult.getSceneHandler();
        String sceneId = checkResult.getSceneId();
        LocalDateTime startTime = queryStartAt != null ? queryStartAt : sceneHandler.getEarliestStartTime();
        LocalDateTime endTime = queryEndAt != null ? queryEndAt : sceneHandler.getLatestSceneRunningTime();
        List<String> agentNames = checkResult.getAgentNames();
        WazuhResult<FileEventModel> result = wazuhRegistryModifyService.queryRegistryModifyData(from, size, agentNames, startTime, endTime, levels, filePath);

        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getCloudScenePluginHandler(sceneId);
        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        for (FileEventModel fileEventModel : result.getList()) {
            CollectionHostEventBO hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(csceneId);
            this.writeBaseInfo(fileEventModel, hostEvent);
            this.writeNodeInfo(fileEventModel.getAgentName(), sceneHandler, cloudScenePluginHandler, hostEvent);
            hostEvent.setAppName("");
            hostEvents.add(hostEvent);
        }
        return new ListObjServiceResult<>(hostEvents, (int) result.getTotal());
    }

    /**
     * aggsRegistryModifyData
     *
     * @param csceneId
     * @param nodeIds
     * @param queryStartAt
     * @param queryEndAt
     * @param intervalMinutes
     * @param bucketSize
     * @return
     */
    public List<ElementHistogramInfo> aggsRegistryModifyData(String csceneId, List<String> nodeIds, LocalDateTime queryStartAt, LocalDateTime queryEndAt, Integer intervalMinutes, Integer bucketSize) {
        if (Objects.isNull(intervalMinutes) || Objects.isNull(bucketSize)) {
            log.error("intervalMinutes:{}, bucketSize:{} should not be null!", intervalMinutes, bucketSize);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        CheckResult checkResult = this.checkAndGetInfo(csceneId, nodeIds);
        if (checkResult.getCheckEnum() != CheckEnum.SUCCESS) {
            return new ArrayList<>();
        }

        List<String> agentNames = checkResult.getAgentNames();
        LocalDateTime startTime = queryStartAt != null ? queryStartAt : checkResult.getSceneStartTime();
        LocalDateTime endTime = queryEndAt != null ? queryEndAt : checkResult.getLatestSceneRunningTime();

        LocalDateTime minBoundsTime = endTime.minusMinutes((long) intervalMinutes * (bucketSize));
        LocalDateTime maxBoundsTime = endTime.minusMinutes(1);
        String minBounds = TimeUtils.localTime2yyyyMMddHHmm(minBoundsTime);
        String maxBounds = TimeUtils.localTime2yyyyMMddHHmm(maxBoundsTime);
        List<TimeHistogramInfo> data = wazuhRegistryModifyService.aggsDateHistogram(agentNames, startTime, endTime, intervalMinutes + "m", minBounds, maxBounds);
        int num = data.size();
        if (num > bucketSize) {
            data = data.subList(num - bucketSize, num);
        }
        List<ElementHistogramInfo> result = new ArrayList<>();
        for (TimeHistogramInfo info : data) {
            ElementHistogramInfo histogramInfo = new ElementHistogramInfo();
            histogramInfo.setCount((int) info.getCount());
            histogramInfo.setTime(info.getTime());
            result.add(histogramInfo);
        }
        return result;
    }

    /**
     * aggsFileModifyData
     *
     * @param csceneId
     * @param nodeIds
     * @param queryStartAt
     * @param queryEndAt
     * @param intervalMinutes
     * @param bucketSize
     * @return
     */
    public List<ElementHistogramInfo> aggsFileModifyData(String csceneId, List<String> nodeIds, LocalDateTime queryStartAt, LocalDateTime queryEndAt, Integer intervalMinutes, Integer bucketSize) {
        if (Objects.isNull(intervalMinutes) || Objects.isNull(bucketSize)) {
            log.error("intervalMinutes:{}, bucketSize:{} should not be null!", intervalMinutes, bucketSize);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        CheckResult checkResult = this.checkAndGetInfo(csceneId, nodeIds);
        if (checkResult.getCheckEnum() != CheckEnum.SUCCESS) {
            return new ArrayList<>();
        }

        List<String> agentNames = checkResult.getAgentNames();
        LocalDateTime startTime = queryStartAt != null ? queryStartAt : checkResult.getSceneStartTime();
        LocalDateTime endTime = queryEndAt != null ? queryEndAt : checkResult.getLatestSceneRunningTime();

        LocalDateTime minBoundsTime = endTime.minusMinutes((long) intervalMinutes * (bucketSize));
        LocalDateTime maxBoundsTime = endTime.minusMinutes(1);
        String minBounds = TimeUtils.localTime2yyyyMMddHHmm(minBoundsTime);
        String maxBounds = TimeUtils.localTime2yyyyMMddHHmm(maxBoundsTime);
        List<TimeHistogramInfo> data = wazuhFileModifyService.aggsDateHistogram(agentNames, startTime, endTime, intervalMinutes + "m", minBounds, maxBounds);
        int num = data.size();
        if (num > bucketSize) {
            data = data.subList(num - bucketSize, num);
        }
        List<ElementHistogramInfo> result = new ArrayList<>();
        for (TimeHistogramInfo info : data) {
            ElementHistogramInfo histogramInfo = new ElementHistogramInfo();
            histogramInfo.setCount((int) info.getCount());
            histogramInfo.setTime(info.getTime());
            result.add(histogramInfo);
        }
        return result;
    }

    /**
     * writeNodeInfo
     *
     * @param nodeEid
     * @param sceneHandler
     * @param cloudScenePluginHandler
     * @param hostEvent
     */
    private void writeNodeInfo(String nodeEid, BatchSceneHandler sceneHandler, CloudScenePluginHandler cloudScenePluginHandler, CollectionHostEventBO hostEvent) {
        SceneElementBO srcElement = sceneHandler.getNodeByEid(nodeEid);
        if (srcElement != null) {
            hostEvent.setNodeId(srcElement.getId());
            hostEvent.setNodeName(srcElement.getName());
            hostEvent.setSystemType(srcElement.getSystemType());
            var item = SystemTypeUtils.getSystemTypeItem(srcElement.getSystemType());
            hostEvent.setSystemTypeName(item.getName());
            if (cloudScenePluginHandler != null) {
                var cloudElement = cloudScenePluginHandler.getElement(srcElement.getId());
                if (cloudElement != null) {
                    hostEvent.setVirtualType(cloudElement.getVirtualType());
                }
            }
        }
    }

    /**
     * writeBaseInfo
     *
     * @param fileEventModel
     * @param hostEventBO
     */
    private void writeBaseInfo(FileEventModel fileEventModel, CollectionHostEventBO hostEventBO) {
        hostEventBO.setType(fileEventModel.getFileEventType().name());
        if (StringUtils.isEmpty(fileEventModel.getAction())) {
            hostEventBO.setEventDesc(fileEventModel.getMessage());
        } else {
            hostEventBO.setEventDesc(fileEventModel.getAction());
        }
        hostEventBO.setTime(fileEventModel.getUpdateTime());
        hostEventBO.setDescription(fileEventModel.getDescription());
        hostEventBO.setLevel(fileEventModel.getLevel());
        hostEventBO.setCatagory(fileEventModel.getCatagory());
        hostEventBO.setContent(fileEventModel.getContent());
        hostEventBO.setPath(fileEventModel.getFilePath());
        hostEventBO.setFileSize(fileEventModel.getSize());
        hostEventBO.setMd5Before(fileEventModel.getMd5Before());
        hostEventBO.setMd5After(fileEventModel.getMd5After());
        hostEventBO.setScrollId(fileEventModel.getScrollId());
    }

    /**
     * checkAndGetInfo
     *
     * @param csceneId
     * @param nodeIds
     * @return
     */
    private CheckResult checkAndGetInfo(String csceneId, List<String> nodeIds) {
        String sceneId = csceneInstanceService.getSceneId(csceneId);
        if (StringUtils.isEmpty(sceneId)) {
            return new CheckResult(CheckEnum.SCENE_NOT_EXISTS);
        }

        BatchSceneHandler sceneHandler = new BatchSceneHandler(Arrays.asList(sceneId), nodeIds);
        CollectionScenePluginHandler collectionScenePluginHandler = sceneHandler.getCollectionScenePluginHandler(sceneId);
        // 无采集插件 直接返回
        if (collectionScenePluginHandler == null) {
            return new CheckResult(CheckEnum.COLLECTION_PLUGIN_NOT_EXISTS);
        }

        Map<String, SceneElementBO> agentNameToElement = sceneHandler.getAllEid2ElementMap();
        List<String> agentNames = new ArrayList<>(agentNameToElement.keySet());
        if (CollectionUtil.isEmpty(agentNames)) {
            return new CheckResult(CheckEnum.AGENT_NAMES_NOT_EXISTS);
        }

        LocalDateTime startTime = sceneHandler.getEarliestStartTime();
        LocalDateTime latestSceneRunningTime = sceneHandler.getLatestSceneRunningTime();
        return new CheckResult(sceneId, sceneHandler, startTime, latestSceneRunningTime, agentNames);
    }

    @Getter
    @Setter
    private static class CheckResult {
        private CheckEnum checkEnum;
        private String sceneId;
        private BatchSceneHandler sceneHandler;
        private LocalDateTime sceneStartTime;
        private LocalDateTime latestSceneRunningTime;
        private List<String> agentNames;

        public CheckResult(CheckEnum checkEnum) {
            this.checkEnum = checkEnum;
        }

        public CheckResult(String sceneId, BatchSceneHandler sceneHandler, LocalDateTime sceneStartTime, LocalDateTime latestSceneRunningTime, List<String> agentNames) {
            this.checkEnum = CheckEnum.SUCCESS;
            this.sceneHandler = sceneHandler;
            this.sceneStartTime = sceneStartTime;
            this.latestSceneRunningTime = latestSceneRunningTime;
            this.agentNames = agentNames;
            this.sceneId = sceneId;
        }
    }

    private enum CheckEnum {
        SUCCESS, SCENE_NOT_EXISTS, AGENT_NAMES_NOT_EXISTS, COLLECTION_PLUGIN_NOT_EXISTS
    }

}
