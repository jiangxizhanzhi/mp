package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.impl.CommonFileServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
     *
     * @return 文件id/url
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    public ServiceResult uploadFile(@RequestParam(value = "file") MultipartFile file,
                                    @RequestParam(value = "upload_type", required = false) String uploadType)
            throws IOException {
        ServiceResult uploadResult = commonFileService.uploadFile
                (file.getInputStream(), file.getOriginalFilename(), FileUtil.getSuffix(file.getOriginalFilename()), uploadType);
        return uploadResult;
    }
}
