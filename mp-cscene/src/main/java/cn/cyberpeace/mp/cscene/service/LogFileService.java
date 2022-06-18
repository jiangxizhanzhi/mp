package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDownloadUrlDto;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDownloadUrlRequest;
import cn.cyberpeace.mp.cscene.dto.log.LogFileVO;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeOSType;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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
    private CommonConfigService commonConfigService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

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

        List<ElementMonitorBasicInfo> allElements = sceneMonitorService.getAllSceneElements(pageRequest.getSceneId(), false);
        Map<String, String> agentNames2ElementIdMap = csceneCommonService.getAgentNames2ElementIdMap(pageRequest.getSceneId(), pageRequest.getElementIds());

        List<CompletableFuture<List<LogFileVO>>> cfList = new ArrayList<>();
        agentNames2ElementIdMap.forEach((agentName, elementId) -> {
            ElementMonitorBasicInfo ebasicInfo = allElements.stream().filter(element -> element.getId().equals(elementId)).findFirst().orElse(null);
            if (Objects.nonNull(ebasicInfo)) {
                String systemType = ebasicInfo.getSystemType();
                CompletableFuture<List<LogFileVO>> cf = CompletableFuture.supplyAsync(() -> queryLogCollectFiles(agentName, elementId, systemType));
                cfList.add(cf);
            }
        });


        List<LogFileVO> allLogFileVOs = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(cfList)) {
            CompletableFuture[] cfArray = cfList.toArray(new CompletableFuture[0]);
            CompletableFuture<List<List<LogFileVO>>> finalCF = CompletableFuture.allOf(cfArray).thenApply(v -> cfList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            List<List<LogFileVO>> result = finalCF.join();
            result.forEach(list -> allLogFileVOs.addAll(list));
        }

        if (CollectionUtil.isEmpty(allLogFileVOs)) {
            return ResultUtils.emptyListObjServiceResult();
        }

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
        ServiceResult<Map<String, Object>> result = commonConfigService.getSystemTypeToAgentSupport();
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
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(date1, df);
        return dateTime;
    }

}
