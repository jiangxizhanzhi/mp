package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.controller.BaseRemoteFileUploadController;
import cn.cyberpeace.mp.base.controller.response.FdfsFileUploadResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.MarkdownFileService;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(value = "markdown共用接口", tags = "COMMON-markdown共用接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/markdown")
@Data
public class MarkdownFileController {

    private MarkdownFileService markdownFileService;

    @Autowired
    public MarkdownFileController(MarkdownFileService markdownFileService) {
        setMarkdownFileService(markdownFileService);
    }

    /**
     * markdown上传图片
     * @return 文件id
     */
    @ApiOperation("上传文件")
    @PostMapping("/image_upload")
    public ServiceResult<FdfsFileUploadResponse> uploadImage(@RequestParam(value = "file") MultipartFile file)
            throws IOException {
        var uploadResult = getMarkdownFileService().uploadImage(file.getInputStream(),
                FileUtil.getSuffix(file.getOriginalFilename()));
        return BaseRemoteFileUploadController.toFileUploadResponseResult(uploadResult, FdfsFileUploadResponse.class)
                .asResult();
    }

    /**
     * markdown上传文件
     * @return 文件id
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    public ServiceResult<FdfsFileUploadResponse> uploadFile(@RequestParam(value = "file") MultipartFile file)
            throws IOException {
        var uploadResult = getMarkdownFileService().uploadFile(file.getInputStream(),
                FileUtil.getSuffix(file.getOriginalFilename()));
        return BaseRemoteFileUploadController.toFileUploadResponseResult(uploadResult, FdfsFileUploadResponse.class)
                .asResult();
    }

}
