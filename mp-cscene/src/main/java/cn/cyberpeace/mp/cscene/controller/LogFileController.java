package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.CscenePluginDataRecordService;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileDownloadUrlDto;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileDownloadUrlRequest;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Api(value = "节点日志文件Api", tags = "节点日志文件Api")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/logfile")
public class LogFileController {

    @Autowired
    private CsceneInstanceService instanceService;

    @Autowired
    private LogFileService logFileService;

    @Autowired
    private CscenePluginDataRecordService dataRecordService;

    
    @ApiOperation(value = "分页查询日志文件")
    @GetMapping("/page")
    public ListObjServiceResult<LogFileVO> pageLogFiles(LogFileSceneElementPageRequest pageRequest) {
        log.info("{}", pageRequest);
        if (StringUtils.isNotEmpty(pageRequest.getStartTime()) || StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            var instance = instanceService.get(pageRequest.getSceneId()).getSource().getObj();
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
            } else {
                startTime = instance.getCreateTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            TimeListPageInfoQueryParams params = new TimeListPageInfoQueryParams();
            params.setSceneIds(List.of(instance.getScene()));
            params.setNodeIds(pageRequest.getElementIds());
            var dataModels = dataRecordService.getData(params, CollectionPluginSceneDataModel.Type.LOGFile, startTime,
                    endTime);
            var logs = dataModels.stream().map(d -> {
                return JsonCommon.parse(d.getData(), LogFileVO.class);
            }).collect(Collectors.toList());
            Set<String> exists = new HashSet<>();
            List<LogFileVO> finalResult = new ArrayList<>();
            for (var log : logs) {
                if (!exists.contains(log.getPath())) {
                    exists.add(log.getPath());
                    finalResult.add(log);
                }
            }
            return new ListObjServiceResult(new ListObjResult(finalResult, finalResult.size()).asResult()).asResult();
        } else {
            ListObjServiceResult<LogFileVO> result = logFileService.pageByElementId(pageRequest);
            return result;
        }
    }


    
    @ApiOperation(value = "查询日志原始文件下载地址")
    @GetMapping("/download_urls")
    public ListObjServiceResult<LogFileDownloadUrlDto> getDownloadUrl(LogFileDownloadUrlRequest request) {
        List<LogFileDownloadUrlDto> downloadUrlDtos = logFileService.getDownloadUrls(request);
        return new ListObjServiceResult(new ListObjResult(downloadUrlDtos, downloadUrlDtos.size())).asResult();

    }

}
