package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDownloadUrlDto;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDownloadUrlRequest;
import cn.cyberpeace.mp.cscene.dto.log.LogFileVO;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeOSType;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 15:29
 */
@Slf4j
@Service
public class LogFileService {

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    //public static WazuhControl wazuhControl = WazuhControlCommon.buildWazuhControl();


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

        if (!csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId())) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }

        List<String> subElementIds = pageRequest.getElementIds().subList(0, 1);
        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(pageRequest.getSceneId(), subElementIds);

        if (CollectionUtil.isEmpty(agentNames)) {
            return ResultUtils.emptyListObjServiceResult();
        }

        String agentName = agentNames.get(0);
        String elementId = subElementIds.get(0);

        String osType = getOsType(pageRequest.getSceneId(), elementId);

        //List<WazuhControl.QueryLogCollectFileData> allLogFiles = wazuhControl.queryLogCollectFiles(agentName, osType);
        List<WazuhControl.QueryLogCollectFileData> allLogFiles = Collections.emptyList();
        if (CollectionUtil.isEmpty(allLogFiles)) {
            return ResultUtils.emptyListObjServiceResult();
        }

        // 假分页处理
        int skip = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
        List<WazuhControl.QueryLogCollectFileData> pagedLogFiles = allLogFiles.stream()
                .skip(skip)
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());

        List<LogFileVO> logFileVOS = convertLogFiles(pagedLogFiles, elementId, agentName, osType);

        PageInfo<LogFileVO> pageInfo = new PageInfo();
        pageInfo.setList(logFileVOS);
        pageInfo.setTotal(allLogFiles.size());

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    /**
     * 查询节点osType
     * @param sceneId
     * @param elementId
     * @return
     */
    private String getOsType(String sceneId, String elementId) {
        BaseSceneElementRequest sceneEleReq = BaseSceneElementRequest.builder()
                .sceneId(sceneId)
                .elementIds(Arrays.asList(elementId)).build();

        ElementMonitorBasicInfo eleBasicInfo = sceneElementMonitorService.getElementMonitorInfo(sceneEleReq, false);
        String systemType = eleBasicInfo.getSystemType();

        // process osType
        ServiceResult<Map<String, Object>> result = commonConfigService.getSystemTypeToAgentSupport();
        Map<String, Object> map = result.getSource();
        Object osTypeCode = map.get(systemType);
        String osType = ProbeOSType.getByCode((Integer) osTypeCode);
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

//        Map<String, WazuhControl.ControlFileResult> resultMap = wazuhControl.controlFile(agentName, osType, uploadParams, true, 10, 3000);
        return null;
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
                    log.error("date string format:{}", f.getModifyTime());
                    LogFileVO dto =
                            LogFileVO.builder()
                                    .path(f.getPath())
                                    .size(f.getSize())
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

}
