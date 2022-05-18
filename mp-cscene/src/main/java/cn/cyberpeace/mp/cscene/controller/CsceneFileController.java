package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CsceneFileService;
import cn.cyberpeace.mp.cscene.vo.SceneFileUploadResponse;
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
    public ServiceResult<SceneFileUploadResponse> attachmentUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalType = new String[]{ "zip" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalType).asResult();
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景中的图标")
    @PostMapping("/icon_upload")
    public ServiceResult<SceneFileUploadResponse> iconUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "png" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传节点秘钥")
    @PostMapping("/private_key_upload")
    public ServiceResult<SceneFileUploadResponse> privateKeyUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "pub" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景模板截图")
    @PostMapping("/scene_template_screenshot_upload")
    public ServiceResult<SceneFileUploadResponse> sceneTemplateScreenshotUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "png" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

}
