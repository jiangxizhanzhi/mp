package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseRemoteFileUploadController;
import cn.cyberpeace.mp.base.controller.response.FdfsFileUploadResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CsceneFileService;
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

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 */

@Api(value = "场景文件", tags = "CSCENE-场景文件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/file")
@Data
public class CsceneFileController {

    @Autowired
    private CsceneFileService csceneFileService;

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景中的附件")
    @PostMapping("/attachment_upload")
    public ServiceResult<FdfsFileUploadResponse> uploaAttactmentdElementAttactment(@RequestParam(value = "file") MultipartFile file) throws IOException {
        var uploadResult = csceneFileService.uploadAttachment(file.getInputStream(), FileUtil.getSuffix(file.getOriginalFilename()));
        return BaseRemoteFileUploadController.toFileUploadResponseResult(uploadResult, FdfsFileUploadResponse.class)
                .asResult();
    }
}
