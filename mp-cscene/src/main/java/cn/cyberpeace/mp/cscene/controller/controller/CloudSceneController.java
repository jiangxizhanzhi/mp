package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.controller.BaseRemoteFileUploadController;
import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.service.MarkdownFileService;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.service.CloudSceneServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(value = "云场景模板", tags = "云场景模板配置项")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_CLOUD_SCENE_API_PATH + "/config")
public class CloudSceneController {

    @Autowired
    private CloudSceneServiceImpl cloudSceneServiceImpl;

    /**
     * 上传图片
     * @return 文件id
     */
    @ApiOperation("上传图片")
    @PostMapping("/image_upload")
    public ServiceResult<FsFileUploadResponse> uploadImage(@RequestParam(value = "file") MultipartFile file)
            throws IOException {
        ServiceResult uploadResult = cloudSceneServiceImpl.uploadImage
                (file.getInputStream(), FileUtil.getSuffix(file.getOriginalFilename()));
        return BaseRemoteFileUploadController.toFileUploadResponseResult
               (uploadResult, FsFileUploadResponse.class)
               .asResult();
    }

    /**
     * 上传文件
     * @return 文件id
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    public ServiceResult<FsFileUploadResponse> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                                          @RequestParam(value = "uploadType", required = false) String uploadType)
            throws IOException {
        ServiceResult uploadResult = cloudSceneServiceImpl.uploadFile
                (file.getInputStream(), FileUtil.getSuffix(file.getOriginalFilename()), uploadType);
        return BaseRemoteFileUploadController.toFileUploadResponseResult
               (uploadResult, FsFileUploadResponse.class)
               .asResult();
    }

}
