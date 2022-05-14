package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 通用的远程文件上传controller
 * @param <S> 远程文件操作service
 */
public interface BaseRemoteFileUploadController<S extends BaseRemoteFileService> {

    S getRemoteFileService();

    /**
     * 通用上传接口
     * @return 文件id
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    default ServiceResult<String> upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return getRemoteFileService().upload(file.getInputStream(),
                FileUtil.getSuffix(file.getOriginalFilename())).asResult();
    }

}
