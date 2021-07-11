package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.constant.LogErrorCode;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.RunTimeLogDTO;
import cn.cyberpeace.mp.log.request.BasePageRequest;
import cn.cyberpeace.mp.log.request.RunTimeLogDownloadRequest;
import cn.cyberpeace.mp.log.service.LogFileServiceFactory;
import cn.cyberpeace.mp.log.service.RuntimeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@Slf4j
@RequestMapping(ControllerConstant.BASE_API_PATH + "/runtime_log")
@Api(value = "日志模块", tags = "日志模块")
@RequiredArgsConstructor
public class RunTimeLogController {

    private final RuntimeLogService runTimeLogService;

    private final LogFileServiceFactory logFileServiceFactory;

    @javax.annotation.Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @GetMapping("/logs")
    @ApiOperation(value = "分页查询运行时日志", notes = "")
    public ListObjServiceResult<RunTimeLogDTO> queryLogs(BasePageRequest basePageRequest) {
        return new ListObjServiceResult(runTimeLogService.page(basePageRequest)).asResult();
    }

    @GetMapping("/download")
    @ApiOperation(value = "日志下载", notes = "")
    public ResponseEntity<Resource> downloadLogFiles(HttpServletResponse response,
                                                     RunTimeLogDownloadRequest request) {
        InputStreamResource resource = null;
        LogFileDownloadBO bo = null;
        try {
            bo = logFileServiceFactory.downLoad(new LogDownloadDTO(request));
            resource = new InputStreamResource(bo.getInputStream());
        } catch (Exception e) {
            log.error("download log error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(LogErrorCode.DOWNLOAD_LOG_FILE_ERROR);
        }

        var httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + bo.getDownLoadFileName());
        response.setHeader("Content-Type", bo.getContentType());
        return ResponseEntity.ok().headers(httpHeaders).body(resource);
    }


    @GetMapping("/types")
    @ApiOperation(value = "查询运行日志类型")
    public ServiceResult<List<OptionBO>> getTypes() {
        return optionService.getStrOptions(LogConstant.MP_COMMON_OPTION_TYPE_RUNTIME_LOG_TYPE);
    }
}
