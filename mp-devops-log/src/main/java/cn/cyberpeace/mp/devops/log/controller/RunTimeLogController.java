package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.dto.RunTimeLogDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.devops.log.request.BasePageRequest;
import cn.cyberpeace.mp.devops.log.request.RunTimeLogDownloadRequest;
import cn.cyberpeace.mp.devops.log.service.LogFileServiceFactory;
import cn.cyberpeace.mp.devops.log.service.RuntimeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 *
 **/
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/runtime_log")
@Api(value = "日志模块", tags = "日志模块")
@RequiredArgsConstructor
public class RunTimeLogController {

    private final RuntimeLogService runTimeLogService;

    private final LogFileServiceFactory logFileServiceFactory;


    @GetMapping("/logs")
    @ApiOperation(value = "分页查询运行时日志", notes = "")
    public ListObjServiceResult<RunTimeLogDTO> queryLogs(BasePageRequest basePageRequest) {
        return new ListObjServiceResult(runTimeLogService.page(basePageRequest)).asResult();
    }

    @PostMapping()
    @ApiOperation(value = "日志下载", notes = "")
    @Deprecated
    public ResponseEntity<Resource> downloadLogFiles(HttpServletResponse response,
                                                     @RequestBody List<String> logFileNames) {
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(runTimeLogService.zipDownLoadFile(logFileNames));
        } catch (Exception e) {
            throw new MpRuntimeException("获取文件流失败");
        }

        var httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=log.zip");
        response.setHeader("Content-Type", "application/zip");
        return ResponseEntity.ok().headers(httpHeaders).body(resource);
    }

    @GetMapping("/download")
    @ApiOperation(value = "日志下载", notes = "")
    public ResponseEntity<Resource> downloadLogFiles(HttpServletResponse response,
                                                     RunTimeLogDownloadRequest request) {
        InputStreamResource resource = null;
        String fileName = request.getFileName();
        try {
            resource = new InputStreamResource(logFileServiceFactory.downLoad(request.getType(),Arrays.asList(fileName)));
        } catch (Exception e) {
            throw new MpRuntimeException("获取文件流失败");
        }

        var httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + (LogReportTypeEnum.MP.toValue().equals(request.getType()) && !fileName.endsWith(".zip") ? fileName.concat(".zip") : fileName));
        response.setHeader("Content-Type", "application/zip");
        return ResponseEntity.ok().headers(httpHeaders).body(resource);
    }
}
