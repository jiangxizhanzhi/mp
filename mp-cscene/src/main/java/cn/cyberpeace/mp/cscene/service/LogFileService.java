package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.log.DownloadUrlRequest;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDownloadUrlDto;
import cn.cyberpeace.mp.cscene.dto.log.LogFileDto;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeOSType;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private WazuhControl wazuhControl = WazuhControlCommon.buildWazuhControl();


    /**
     * 分页查询日志文件列表
     * （假分页）
     * @param pageRequest
     * @return
     */
    public ListObjServiceResult<LogFileDto> pageByElementId(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);
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

        BaseSceneElementRequest sceneEleReq = BaseSceneElementRequest.builder()
                .sceneId(pageRequest.getSceneId())
                .elementIds(subElementIds).build();

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

        List<WazuhControl.QueryLogCollectFileData> allLogFiles = wazuhControl.queryLogCollectFiles(agentName, osType);
        if (CollectionUtil.isEmpty(allLogFiles)) {
            return ResultUtils.emptyListObjServiceResult();
        }

        // 假分页处理
        int skip = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
        List<WazuhControl.QueryLogCollectFileData> pagedLogFiles = allLogFiles.stream()
                .skip(skip)
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());

        List<LogFileDto> logFileDtos = convertLogFiles(pagedLogFiles, elementId, agentName, osType);

        PageInfo<LogFileDto> pageInfo = new PageInfo();
        pageInfo.setList(logFileDtos);
        pageInfo.setTotal(allLogFiles.size());

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    /**
     * 触发自定义探针脚本，执行上传指定日志文件到文件服务器
     * 并生成下载url返回
     * @return
     */
    public List<LogFileDownloadUrlDto> getDownloadUrls(DownloadUrlRequest request) {


//        wazuhControl.controlFile()
        return null;
    }


    /**
     * 转换WazuhControl.QueryLogCollectFileData到LogFileDto
     * @param logFiles
     * @param elementId
     * @param agentName
     * @return
     */
    private List<LogFileDto> convertLogFiles(List<WazuhControl.QueryLogCollectFileData> logFiles, String elementId, String agentName, String osType) {
        if (CollectionUtil.isEmpty(logFiles)) {
            return Collections.emptyList();
        }

        List<LogFileDto> logFileDtos = new ArrayList<>();
        logFiles.forEach(logFile -> {
            List<WazuhControl.QueryLogCollectRelatedFileInfo> relatedFiles = logFile.getRelatedFiles();

            if (CollectionUtil.isNotEmpty(relatedFiles)) {

                List<LogFileDto> subDtos = relatedFiles.stream().map(f -> {
                    log.error("date string format:{}", f.getModifyTime());
                    LogFileDto dto =
                            LogFileDto.builder()
                                    .path(f.getPath())
                                    .size(f.getSize())
                                    .elementId(elementId)
                                    .agentName(agentName)
                                    .osType(osType)
                                    .build();
                    return dto;
                }).collect(Collectors.toList());

                logFileDtos.addAll(subDtos);
            }
        });

        return logFileDtos;
    }

}
