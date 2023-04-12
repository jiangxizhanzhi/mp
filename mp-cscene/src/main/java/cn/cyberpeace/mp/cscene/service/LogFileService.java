package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.bo.ProbeOperateTimeRange;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileDownloadUrlDto;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileDownloadUrlRequest;
import cn.cyberpeace.mp.cscene.service.constant.ProbeOperateConstant;
import cn.cyberpeace.mp.cscene.service.param.ProbeOperateQueryParams;
import cn.cyberpeace.mp.cscene.util.LogFileUtil;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeOSType;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 15:29
 */
@Slf4j
@Service
public class LogFileService {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private CloudCommonConfigService cloudCommonConfigService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Autowired
    private CollectionPluginSceneMapper collectionPluginSceneMapper;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    /**
     * 分页查询日志文件列表
     * （假分页）
     * @param pageRequest
     * @return
     */
    public ListObjServiceResult<LogFileVO> pageByElementId(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        if (StringUtils.isBlank(pageRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        if (Objects.isNull(pageRequest.getFetchIfSceneInstanceShutdown()) || !pageRequest.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId())) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }
        Map<String, List<LogFileVO>> elementId2LogFile = LogFileUtil.getLogFile(pageRequest.getSceneId());
        if (elementId2LogFile == null || elementId2LogFile.isEmpty()) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }
        List<ElementMonitorBasicInfo> allElements = sceneMonitorService.getAllSceneElements(pageRequest.getSceneId(), false);
        Map<String, String> agentNames2ElementIdMap = csceneCommonService.getAgentNames2ElementIdMap(pageRequest.getSceneId(), pageRequest.getElementIds());

        List<LogFileVO> finalAllLogFileVOs = new ArrayList<>();
        agentNames2ElementIdMap.forEach((agentName, elementId) -> {
            if (elementId2LogFile.containsKey(elementId)) {
                finalAllLogFileVOs.addAll(elementId2LogFile.get(elementId));
            }
        });
        List<LogFileVO> allLogFileVOs = finalAllLogFileVOs;
        if (CollectionUtil.isEmpty(allLogFileVOs)) {
            return ResultUtils.emptyListObjServiceResult();
        }
        //原始日志文件数据探针开关处理
        allLogFileVOs = probeOperateHandler(pageRequest,allLogFileVOs);
        Map<String, Integer> elementIdOrderMap = new HashMap<>();
        int order = 0;
        for (ElementMonitorBasicInfo element : allElements) {
            elementIdOrderMap.put(element.getId(), order++);
        }
        // 按照节点排序
        allLogFileVOs.sort(Comparator.comparing(f -> elementIdOrderMap.get(f.getElementId())));

        // 假分页处理
        int skip = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
        List<LogFileVO> pagedLogFileVOs = allLogFileVOs.stream()
                .skip(skip)
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());

        PageInfo<LogFileVO> pageInfo = new PageInfo();
        pageInfo.setList(pagedLogFileVOs);
        pageInfo.setTotal(allLogFileVOs.size());

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    /**
     * @description: 探针开关记录处理
     * @param pageRequest
     * @param allLogFileVOs 
     * @throws
     * @return List<LogFileVO>
     */
    private List<LogFileVO> probeOperateHandler(BaseSceneElementPageRequest pageRequest, List<LogFileVO> allLogFileVOs) {
        //该入口可能为自定义采集包含探针id，且类型为原始日志；也可能为原始日志接口直接调用，需要通过pageRequest中的timerange判断
        List<WazuhEventRequest.TimeRange> timeRanges = pageRequest.getTimeRanges();
        if(CollectionUtils.isNotEmpty(timeRanges)){
            return allLogFileVOs.stream().filter(logFileVO ->
                timeRanges.stream().filter(time -> logFileVO.getCreateTime().isAfter(time.getFrom()) && logFileVO.getCreateTime().isBefore(time.getTo())).findAny().isPresent()
            ).collect(Collectors.toList());
        }
        ProbeOperateQueryParams params = ProbeOperateQueryParams.builder().scene(pageRequest.getSceneId())
            .type(ProbeOperateConstant.PROBEOPERATE_TYPE.ORIGIN_LOG.getValue()).build();
        //查询对应日志类型探针开关操作时间段
        List<ProbeOperateTimeRange> timeRangeList = sceneMonitorService.queryProbeOperateTimeRange(params);
        //文件创建时间在查询范围中即符合条件
        return allLogFileVOs.stream().filter(logFileVO ->
            timeRangeList.stream().filter(time -> logFileVO.getCreateTime().isAfter(time.getFrom()) && logFileVO.getCreateTime().isBefore(time.getTo())).findAny().isPresent()
        ).collect(Collectors.toList());
    }

    public Map<String, List<LogFileVO>> loadLogFile(String csceneId) {
        var cscene = csceneInstanceMapper.get(csceneId);
        if(cscene == null){
            return new ConcurrentHashMap<>();
        }
        String sceneId = cscene.getScene();
        var model = collectionPluginSceneMapper.first(new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId));
        if (model == null || StringUtils.isEmpty(model.getProbeCollector())) {
            return new ConcurrentHashMap<>();
        }
        List<ElementMonitorBasicInfo> allElements = sceneMonitorService.getAllSceneElements(csceneId, false);
        if (allElements == null || allElements.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        Map<String, List<LogFileVO>> result = new ConcurrentHashMap<>();
        List<CompletableFuture<List<LogFileVO>>> cfList = new ArrayList<>();
        List<String> elementIds = new ArrayList<>();
        allElements.forEach(e -> elementIds.add(e.getId()));

        Map<String, String> agentNames2ElementIdMap = csceneCommonService.getAgentNames2ElementIdMap(csceneId, elementIds);
        agentNames2ElementIdMap.forEach((agentName, elementId) -> {
            ElementMonitorBasicInfo ebasicInfo = allElements.stream().filter(element -> element.getId().equals(elementId)).findFirst().orElse(null);
            if (Objects.nonNull(ebasicInfo)) {
                String systemType = ebasicInfo.getSystemType();
                CompletableFuture<List<LogFileVO>> cf = CompletableFuture.supplyAsync(() -> {
                    var files = queryLogCollectFiles(agentName, elementId, systemType);
                    result.put(elementId, files);
                    return files;
                });
                cfList.add(cf);
            }
        });
        if (CollectionUtil.isEmpty(cfList)) {
            return new ConcurrentHashMap<>();
        }
        CompletableFuture[] cfArray = cfList.toArray(new CompletableFuture[0]);
        CompletableFuture<List<List<LogFileVO>>> finalCF = CompletableFuture.allOf(cfArray).thenApply(v -> cfList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        finalCF.join();
        return result;
    }


    private List<LogFileVO> queryLogCollectFiles(String agentName, String elementId, String systemType) {
        log.info("queryLogFile start! thread:{}, agentName:{}, elementId:{}, systemType:{}", Thread.currentThread().getName(), agentName, elementId, systemType);
        String osType = getOsType(systemType);
        List<WazuhControl.QueryLogCollectFileData> fileList = WazuhControlCommon.wazuhControl.queryLogCollectFiles(agentName, osType);
        List<LogFileVO> logFileVOS = convertLogFiles(fileList, elementId, agentName, osType);
        log.info("queryLogFile completed! thread:{}, logFileVOS.size {}, logFileVOS.paths {}", Thread.currentThread().getName(), logFileVOS.size(), logFileVOS.stream().map(LogFileVO::getPath).collect(Collectors.toList()));
        return logFileVOS;
    }


    /**
     * 根据systemType查询节点osType
     * @return
     */
    private String getOsType(String systemType) {
        ServiceResult<Map<String, Object>> result = cloudCommonConfigService.getSystemTypeToAgentSupport();
        Map<String, Object> map = result.getSource();
        Object osTypeCode = map.get(systemType);
        String osType = ProbeOSType.getByCode(Integer.valueOf(osTypeCode.toString()));
        if (Objects.nonNull(osType)) {
            osType = osType.trim().toLowerCase();
        }
        return osType;
    }


    /**
     * 触发自定义探针脚本，执行上传指定日志文件到文件服务器
     * 并生成下载url返回
     * @return
     */
    public List<LogFileDownloadUrlDto> getDownloadUrls(LogFileDownloadUrlRequest request) {
        log.info("downloadurl request:{}", request);
        if (Objects.isNull(request) || CollectionUtil.isEmpty(request.getPaths())) {
            return Collections.emptyList();
        }

        String agentName = request.getAgentName();
        String osType = request.getOsType();
        List<String> paths = request.getPaths();
        List<WazuhControl.ControlFileParam> uploadParams =
            paths.stream().map(path -> {
                WazuhControl.ControlFileParam uploadParam = new WazuhControl.ControlFileParam();
                uploadParam.configUploadParam(path, CollectionSingleton.collectionAppConfig.getWazuhGodfsServer() + "/godfs/upload");
                return uploadParam;
            }).collect(Collectors.toList());

        Map<String, WazuhControl.ControlFileResult> resultMap = WazuhControlCommon.wazuhControl.controlFile(agentName, osType, uploadParams, true, 10, 3000);

        Map<String, LogFileDownloadUrlDto> paramId2DtoMap = new HashMap<>();
        for (WazuhControl.ControlFileParam uploadParam : uploadParams) {
            String paramId = uploadParam.getId();
            String path = uploadParam.getFilepath();
            LogFileDownloadUrlDto dto = LogFileDownloadUrlDto.builder().path(path).name(FileUtil.getName(path)).agentName(agentName).osType(osType).build();
            paramId2DtoMap.put(paramId, dto);
        }

        List<LogFileDownloadUrlDto> dtos = new ArrayList<>();
        resultMap.forEach((paramId, fileResult) -> {
            log.info("key:{}, val:{}", paramId, fileResult);
            String downloadUrl = fileResult.getFileSrc();
            downloadUrl = collectionAppConfig.getWazuhGodfsOuterHttpServer() + downloadUrl;
            LogFileDownloadUrlDto dto = paramId2DtoMap.get(paramId);
            if (Objects.nonNull(dto)) {
                dto.setDownloadUrl(downloadUrl);
                dtos.add(dto);
            }
        });

        log.info("downloadUrlDtos:{}", dtos);
        return dtos;
    }


    /**
     * 转换WazuhControl.QueryLogCollectFileData到LogFileDto
     * @param logFiles
     * @param elementId
     * @param agentName
     * @return
     */
    private List<LogFileVO> convertLogFiles(List<WazuhControl.QueryLogCollectFileData> logFiles, String elementId, String agentName, String osType) {
        if (CollectionUtil.isEmpty(logFiles)) {
            return Collections.emptyList();
        }

        List<LogFileVO> logFileVOS = new ArrayList<>();
        logFiles.forEach(logFile -> {
            List<WazuhControl.QueryLogCollectRelatedFileInfo> relatedFiles = logFile.getRelatedFiles();

            if (CollectionUtil.isNotEmpty(relatedFiles)) {

                List<LogFileVO> subDtos = relatedFiles.stream().map(f -> {
                    LogFileVO dto =
                            LogFileVO.builder()
                                    .path(f.getPath())
                                    .name(FileUtil.getName(f.getPath()))
                                    .size(f.getSize())
                                    .createTime(parseDateTime(f.getCreateTime()))
                                    .modifyTime(parseDateTime(f.getModifyTime()))
                                    .accessTime(parseDateTime(f.getAccessTime()))
                                    .elementId(elementId)
                                    .agentName(agentName)
                                    .osType(osType)
                                    .build();
                    return dto;
                }).collect(Collectors.toList());

                logFileVOS.addAll(subDtos);
            }
        });

        return logFileVOS;
    }


    private LocalDateTime parseDateTime(String datetime) {
        String date1 = StringUtils.substringBeforeLast(datetime, ".");
        date1 = date1.concat("Z");
        return TimeUtils.utcStringToLocalDateTime(date1);
    }

}
