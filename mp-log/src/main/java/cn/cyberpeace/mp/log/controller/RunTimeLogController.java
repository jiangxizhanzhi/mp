package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
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
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.request.BasePageRequest;
import cn.cyberpeace.mp.log.request.LogFileDeleteRequest;
import cn.cyberpeace.mp.log.request.RunTimeLogDownloadRequest;
import cn.cyberpeace.mp.log.service.LogFileServiceFactory;
import cn.cyberpeace.mp.log.service.MpOperateLogService;
import cn.cyberpeace.mp.log.service.RuntimeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 **/
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

    @ApiOperation("批量删除日志")
    @ApiImplicitParam(name = "requests", value = "删除数据请求体", dataTypeClass = LogFileDeleteRequest[].class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Boolean> bulkDelete(@RequestBody LogFileDeleteRequest[] requests) {
        Integer delete_num = requests.length;
        Integer rel_delete_num = 0;
        var result = new ServiceResult<Boolean>();
        if (delete_num == 0) {
            result.setCode(BaseErrorCode.ERROR.getCode());
            return result;
        }

        // 使用 groupingBy 根据枚举值进行分组
        Map<LogReportTypeEnum, List<LogFileDeleteRequest>> req_list = Arrays.stream(requests)
                .collect(Collectors.groupingBy(LogFileDeleteRequest::getLog_report_type_enum));

        // 遍历每个分组，通过factory调用删除方法
        for (Map.Entry<LogReportTypeEnum, List<LogFileDeleteRequest>> entry : req_list.entrySet()) {
            LogReportTypeEnum type = entry.getKey();
            List<LogFileDeleteRequest> list = entry.getValue();
            rel_delete_num += logFileServiceFactory.deleteLog(type, list);
        }

        //校验删除个数
        if (rel_delete_num != delete_num) {
            result.setData(false);
            result.setCode(LogErrorCode.DELETE_LOG_FILE_ERROR.getCode());
            result.setDetail(String.format("%s files need to be deleted, but %s files are actually deleted", delete_num, rel_delete_num));
        } else {
            result.setData(true);
        }
        // 返回处理结果
        return result;
    }
}
