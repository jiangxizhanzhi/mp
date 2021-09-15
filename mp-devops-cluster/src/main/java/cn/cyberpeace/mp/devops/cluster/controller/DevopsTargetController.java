package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devops.cluster.response.TargetScene;
import cn.cyberpeace.mp.devops.cluster.response.TargetSceneResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 靶标管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
public class DevopsTargetController {

    private RealEquipmentConverter realEquipmentConverter;

    private TypeConverter typeConverter;

    @Autowired
    public DevopsTargetController(RealEquipmentConverter realEquipmentConverter,TypeConverter typeConverter) {
        this.realEquipmentConverter = realEquipmentConverter;
        this.typeConverter = typeConverter;
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
        List<Device> deviceList = realEquipmentConverter.allTarget(request);
        if (CollectionUtils.isEmpty(deviceList)){
            return new Result<>();
        }
        List<TargetScene> targetScenes = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            TargetScene targetScene = new TargetScene();
            Integer firstId = first.getId();
            targetScene.setFirstTypeId(firstId);
            targetScene.setFirstTypeName(first.getName());
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            for (TypeConvertModel second : typeConvertModels){
                Integer secondId = second.getId();
                List<Device> secondDeviceList = deviceList.stream().filter(device -> Objects.nonNull(device.getSecondCategory())).filter(device -> device.getSecondCategory().equals(secondId)).collect(Collectors.toList());
                targetScene.setSecondTypeId(secondId);
                targetScene.setSecondTypeName(second.getName());
                targetScene.setDeviceList(secondDeviceList);
            }
            targetScenes.add(targetScene);
        }

        return new Result<>(new TargetSceneResponse(targetScenes));
    }

}
