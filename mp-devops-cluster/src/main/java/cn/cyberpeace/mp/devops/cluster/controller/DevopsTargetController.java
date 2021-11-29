package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devops.cluster.response.TargetScene;
import cn.cyberpeace.mp.devops.cluster.response.TargetSceneResponse;
import cn.cyberpeace.mp.devops.cluster.response.TargetUploadResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 靶标管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
public class DevopsTargetController {

    private TargetConverter targetConverter;

    private TypeConverter typeConverter;

    private ImagesService imagesService;

    private IpAddressConfig ipAddressConfig;

    @Autowired
    public DevopsTargetController(TargetConverter targetConverter,TypeConverter typeConverter,
                                  ImagesService imagesService,IpAddressConfig ipAddressConfig) {
        this.targetConverter = targetConverter;
        this.typeConverter = typeConverter;
        this.imagesService = imagesService;
        this.ipAddressConfig = ipAddressConfig;
    }

    /**
     * 查询FTP信息
     * @return
     */
    @ApiOperation("查询FTP信息")
    @GetMapping("/ftp-info")
    public Result<String> getFtpAddress(){
        return new Result<>(ipAddressConfig.getCp());
    }



    /**
     * 查询所有靶标信息
     * @param request
     * @return
     */
    @ApiOperation("查询所有靶标信息")
    @GetMapping("/list")
    public Result<TargetSceneResponse> list(TargetSceneRequest request){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();
        List<Device> deviceList = targetConverter.allTarget(request);
        if (CollectionUtils.isEmpty(deviceList)){
            return new Result<>();
        }
        List<TargetScene> targetScenes = new ArrayList<>();
        if (Objects.nonNull(request.getCategory())) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(request.getCategory())).collect(Collectors.toList());
        }
        if (Objects.nonNull(request.getSecondCategory())) {
            secondTypeList = secondTypeList.stream().filter(v -> v.getId().equals(request.getSecondCategory())).collect(Collectors.toList());
        }
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            for (TypeConvertModel second : typeConvertModels){
                TargetScene targetScene = new TargetScene();
                targetScene.setFirstTypeId(firstId);
                targetScene.setFirstTypeName(first.getName());
                Integer secondId = second.getId();
                List<Device> secondDeviceList = deviceList.stream().filter(device -> Objects.nonNull(device.getSecondCategory())).filter(device -> device.getSecondCategory().equals(secondId)).collect(Collectors.toList());
                targetScene.setSecondTypeId(secondId);
                targetScene.setSecondTypeName(second.getName());
                targetScene.setDeviceList(secondDeviceList);
                targetScenes.add(targetScene);
            }
        }

        return new Result<>(new TargetSceneResponse(targetScenes));
    }

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public ServiceResult<TargetUploadResponse> upload(@RequestParam("type") String type,@RequestParam("file") MultipartFile file){
        Map<String,File> fileList = null;
        try {
            fileList = FileUtils.transferToFileMap(Arrays.asList(file));
        } catch (IOException e) {
           log.error("file transfer error",e);
        }
        BaseCloudPlatformModel model = imagesService.upload(fileList,type);
        if (Objects.isNull(model) || !model.isSuccess()){
            return ServiceResult.fail("file upload error");
        }
        return new Result(new TargetUploadResponse(model.getTmpdirUuid(),model.getFileNames()));
    }

}
