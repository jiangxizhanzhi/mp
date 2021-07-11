package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public interface BaseRemoteFileDownloadController<S extends BaseRemoteFileService> {

    S getRemoteFileService();

    
    @ApiOperation("下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "filename", value = "文件名称", dataTypeClass = String.class),
    })
    @GetMapping(value = "/file_download", params = "id")
    default void download(@RequestParam String id, @RequestParam(required = false) String filename,
                          HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(filename)) {
            filename = FileUtil.getName(id);
        }

        response.reset();
        
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20").replace("%2B", "+");
        response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\"", encodedFilename));
        response.setContentType("application/octet-stream;charset=utf-8");
        var os = response.getOutputStream();
        getRemoteFileService().download(id, os);
    }

}
