package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import cn.cyberpeace.mp.cscene.service.CsceneFileService;
import cn.cyberpeace.mp.cscene.service.ProtocolCollectionService;
import cn.cyberpeace.mp.cscene.vo.MibFileUploadResponse;
import cn.cyberpeace.mp.cscene.vo.SceneFileUploadResponse;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
import java.util.List;
import java.util.Objects;



@Api(value = "场景文件", tags = "CSCENE-场景文件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/file")
@Data
public class CsceneFileController {

    @Autowired
    private CsceneFileService csceneFileService;

    @Autowired
    private ProtocolCollectionService protocolCollectionService;

    
    @ApiOperation("上传场景中的附件")
    @ApiImplicitParam(name = "file", value = "附件", dataTypeClass = MultipartFile.class)
    @PostMapping("/attachment_upload")
    public ServiceResult<SceneFileUploadResponse> attachmentUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalType = new String[]{ "zip" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalType).asResult();
    }

    
    @ApiOperation("上传场景中的执行脚本")
    @ApiImplicitParam(name = "file", value = "脚本文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/script_upload")
    public ServiceResult<SceneFileUploadResponse> scriptUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalType = new String[]{ "sh" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalType).asResult();
    }

    
    @ApiOperation("上传场景中的图标")
    @ApiImplicitParam(name = "file", value = "图标文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/icon_upload")
    public ServiceResult<SceneFileUploadResponse> iconUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "png", "svg", "jpg", "jpeg", "gif" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

    
    @ApiOperation("上传节点秘钥")
    @ApiImplicitParam(name = "file", value = "节点秘钥", dataTypeClass = MultipartFile.class)
    @PostMapping("/private_key_upload")
    public ServiceResult<SceneFileUploadResponse> privateKeyUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "pub", "txt" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

    
    @ApiOperation("上传场景模板截图")
    @ApiImplicitParam(name = "file", value = "场景模板截图", dataTypeClass = MultipartFile.class)
    @PostMapping("/scene_template_screenshot_upload")
    public ServiceResult<SceneFileUploadResponse> sceneTemplateScreenshotUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "png" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }


    @ApiOperation("上传mib文件")
    @ApiImplicitParam(name = "file", value = "mib文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/mib_import")
    public ServiceResult<MibFileUploadResponse> sceneMibFileUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "mib" };
        var fileName = file.getOriginalFilename();
        var inputStream = file.getInputStream();
        var fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        
        ServiceResult<SceneFileUploadResponse> sceneMibFileUpload =
            csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix);
        SceneFileUploadResponse sceneFileUploadResponse = sceneMibFileUpload.getSource();
        
        if(Objects.isNull(sceneFileUploadResponse)){
            return new ServiceResult<>(null, sceneMibFileUpload.getServiceCode(), sceneMibFileUpload.getDetail());
        }
        
        ServiceResult<List<MibFileInfo>> mibFile = protocolCollectionService.importMibFile(file);
        List<MibFileInfo> mibFileInfos = mibFile.getSource();
        
        if(Objects.isNull(mibFileInfos)){
            return new ServiceResult<>(null, mibFile.getServiceCode(), mibFile.getDetail());
        }
        
        MibFileUploadResponse mibFileUploadResponse =
            new MibFileUploadResponse(sceneFileUploadResponse, mibFileInfos);
        return new ServiceResult<>(mibFileUploadResponse).asResult();
    }

}
