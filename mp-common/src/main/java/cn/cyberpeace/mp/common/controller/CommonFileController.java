package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseRemoteFileUploadController;
import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.impl.CommonFileServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/commonFile")
public class CommonFileController {

    @Autowired
    private CommonFileServiceImpl commonFileService;

    /**
     * 上传文件
     * @return 文件id/url
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    public ServiceResult uploadFile(@RequestParam(value = "file") MultipartFile file,
                                                          @RequestParam(value = "uploadType", required = false) String uploadType)
            throws IOException {
        ServiceResult uploadResult = commonFileService.uploadFile
                (file.getInputStream(),file.getOriginalFilename(), FileUtil.getSuffix(file.getOriginalFilename()), uploadType);
        return uploadResult;
    }

    /**
     * 根据相对地址获取文件名称
     *
     * @return 文件名称
     */
    @ApiOperation("根据相对地址获取文件名称")
    @GetMapping("/getFileNameByRelativeUrl")
    public ServiceResult getFileNameByRelativeUrl(@RequestParam(value = "fileUrl") String fileUrl) {
        String fileName = commonFileService.getFileNameByRelativeUrl(fileUrl);
        return new ServiceResult(fileName, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }
}
