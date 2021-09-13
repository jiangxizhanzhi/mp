package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.log.request.BasePageRequest;
import cn.cyberpeace.mp.devops.log.response.RunTimeLogResponse;
import cn.cyberpeace.mp.devops.log.service.RunTimeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  *@Desciption 运行日志管理
  *@Author zhangjin
  *@Date 9/13/21 9:41 上午
**/
@RestController
@RequestMapping("/runtime_log")
@Api(value = "日志模块", tags = "日志模块")
public class RunTimeLogController {

    @Autowired
    private RunTimeLogService runTimeLogService;

    @GetMapping("/logs")
    @ApiOperation(value = "分页查询运行时日志", notes = "")
    public ListObjServiceResult<RunTimeLogResponse> queryLogs(@RequestBody BasePageRequest basePageRequest) {
        return new ListObjServiceResult(runTimeLogService.queryRuntimeLog()).asResult();
    }

    @PostMapping("/{logFileName}")
    @ApiOperation(value = "日志下载", notes = "")
    public ResponseEntity<Resource> downloadPcapFile(HttpServletResponse httpServletResponse,
        @PathVariable("logFileName") List<String> logFileNames) {
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new InputStream() {
                @Override public int read() throws IOException {
                    return 0;
                }
            });
        } catch (Exception e) {
            throw new MpRuntimeException("获取文件流失败");
        }

        var httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=log.zip");
        return ResponseEntity.ok().headers(httpHeaders)
            .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }
}
