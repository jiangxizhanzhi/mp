package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.HostStateExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "主机状态监控", tags = "主机状态监控")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/hoststate/export")
public class HostStateExportController {

    @Resource
    private HostStateExportService hostStateExportService;


    @ApiOperation(value = "导出cpu 内存 磁盘使用率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "type", value = "导出的数据类型,1:cpu 内存 磁盘,2:文件变更和注册表变更,3:漏洞信息", dataTypeClass = String.class)
    })
    @GetMapping("/file")
    public ResponseEntity<org.springframework.core.io.Resource> exportHost(String id, int type) {
        File file = null;
        if (type == 1) {
            file = hostStateExportService.exportHost(id);
        } else if (type == 2) {
            file = hostStateExportService.exportEvent(id);
        } else if (type == 3) {
            file = hostStateExportService.exportVulnerability(id);
        }
        return downloadFile(file);
    }


    private ResponseEntity<org.springframework.core.io.Resource> downloadFile(File file) {
        try {
            // 创建文件资源对象
            UrlResource resource = new UrlResource(Paths.get(file.getAbsolutePath()).toUri());
            // 返回下载响应
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        } finally {
            this.hostStateExportService.addDeleteFile(file.getName());
        }
    }
}
