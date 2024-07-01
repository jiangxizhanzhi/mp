package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseRemoteFileUploadController;
import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.MarkdownFileService;
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

@Deprecated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/markdown")
@Data
@AuthTarget(TargetEnum.ADMIN)
public class MarkdownFileController {

    private MarkdownFileService markdownFileService;

    /**
     * MarkdownFileController
     *
     * @param markdownFileService
     */
    @Autowired
    public MarkdownFileController(MarkdownFileService markdownFileService) {
        setMarkdownFileService(markdownFileService);
    }

    /**
     * markdown上传图片
     *
     * @param file
     * @return 文件id
     */
    @ApiOperation("上传图片")
    @PostMapping("/image_upload")
    public ServiceResult<FsFileUploadResponse> uploadImage(@RequestParam(value = "file") MultipartFile file)
            throws IOException {
        ServiceResult<String> uploadResult = getMarkdownFileService().uploadImage(file.getInputStream(),
                FileUtil.getSuffix(file.getOriginalFilename()));
        return BaseRemoteFileUploadController.toFileUploadResponseResult(uploadResult, FsFileUploadResponse.class)
                .asResult();
    }

    /**
     * markdown上传文件
     *
     * @param file
     * @return 文件id
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    public ServiceResult<FsFileUploadResponse> uploadFile(@RequestParam(value = "file") MultipartFile file)
            throws IOException {
        ServiceResult<String> uploadResult = getMarkdownFileService().uploadFile(file.getInputStream(),
                FileUtil.getSuffix(file.getOriginalFilename()));
        return BaseRemoteFileUploadController.toFileUploadResponseResult(uploadResult, FsFileUploadResponse.class)
                .asResult();
    }

}
