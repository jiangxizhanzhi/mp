package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetSceneConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CommonTargetNameEnum;
import cn.cyberpeace.mp.devops.cluster.request.TargetMultiAsyncRequest;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devops.cluster.response.CommonTargetResponse;
import cn.cyberpeace.mp.devops.cluster.response.TargetScene;
import cn.cyberpeace.mp.devops.cluster.response.TargetSceneResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devops.cluster.service.impl.TargetIconUploadService;
import cn.cyberpeace.mp.devops.cluster.thread.ITask;
import cn.cyberpeace.mp.devops.cluster.utils.MultiThreadUtils;
import cn.cyberpeace.mp.devops.cluster.vo.DeviceVO;
import cn.cyberpeace.mp.devops.cluster.vo.convert.DeviceVoConverter;
import cn.hutool.core.date.StopWatch;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 靶标管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
@RequiredArgsConstructor
public class DevopsTargetController {

    private final TargetConverter targetConverter;

    private final TypeConverter typeConverter;

    private final IpAddressConfig ipAddressConfig;

    private final DeviceVoConverter deviceVoConverter;

    private final ITask asyncTargetTask;

    private final InitRegisterService initRegisterService;

    private final TargetIconUploadService targetIconUploadService;

    private final DevopsTargetService devopsTargetService;

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
    public ServiceResult<TargetSceneResponse> list(TargetSceneRequest request){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        stopWatch.stop();
        System.out.println("findL1AndL2AndL3Type花费时间："+ stopWatch.getTotalTimeSeconds());
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(category)).collect(Collectors.toList());
        }
        Integer secondCategory = request.getSecondCategory();
        if (Objects.nonNull(secondCategory)) {
            secondTypeList = secondTypeList.stream().filter(v -> v.getId().equals(secondCategory)).collect(Collectors.toList());
        }

        List<TargetScene> targetScenes = new ArrayList<>();
        StopWatch stopWatch2 = new StopWatch();
        stopWatch2.start();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());

            for (TypeConvertModel second : typeConvertModels){
                request.setCategory(firstId);
                request.setSecondCategory(second.getId());
                request.setPage(null);
                request.setPerPage(null);
                TargetSceneConvertModel targetSceneConvertModel = targetConverter.allTarget(request);
                if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getDeviceList())){
                    continue;
                }
                List<Device> secondDeviceList = targetSceneConvertModel.getDeviceList();

                TargetScene targetScene = new TargetScene();

                targetScene.setFirstTypeId(firstId);
                targetScene.setFirstTypeName(first.getName());
                Integer secondId = second.getId();
                targetScene.setSecondTypeId(secondId);
                targetScene.setSecondTypeName(second.getName());
                targetScene.setDeviceList(secondDeviceList);
                targetScenes.add(targetScene);
            }
        }
        stopWatch2.stop();
        System.out.println("allTarget花费时间："+ stopWatch2.getTotalTimeSeconds());
        return new Result<>(new TargetSceneResponse(targetScenes));
    }

    @ApiOperation("分页查询靶标信息")
    @GetMapping("/page")
    public PageResult<DeviceVO> page(TargetSceneRequest request){
        TargetSceneConvertModel targetSceneConvertModel = targetConverter.allTarget(request);
        if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getDeviceList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<Device> deviceList = targetSceneConvertModel.getDeviceList();

        List<DeviceVO> deviceVOList = deviceVoConverter.devicesToDeviceVOs(deviceList);

        return new PageResult<>(deviceVOList,targetSceneConvertModel.getTotal());
    }

    @ApiOperation("查询常用靶标信息")
    @GetMapping("/common-targets")
    public ServiceResult<CommonTargetResponse> getCommonTargets(){
        List<Device> deviceList = targetConverter.getCommonTargets("");
        List<DeviceVO> deviceVOList = deviceVoConverter.devicesToDeviceVOs(deviceList);

        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(locale, Locale.CHINA)) {
            // 常用靶标名称国际化处理
            deviceVOList.forEach(device -> {
                device.setImage(device.getName());
                device.setName(
                        Objects.nonNull(CommonTargetNameEnum.map.get(device.getName())) ?
                                CommonTargetNameEnum.map.get(device.getName()).toValue() : device.getName());
            });
        }

        return new Result(new CommonTargetResponse(deviceVOList));
    }

    @ApiOperation("批量同步靶标数据")
    @GetMapping("/multi-async")
    public ServiceResult multiAsync(TargetMultiAsyncRequest request){
        devopsTargetService.deleteAllTarget();
        List<Device> deviceList = targetConverter.multiAsync(request.getOffset(), request.getLimit());
        MultiThreadUtils multiThreadUtils = MultiThreadUtils.newInstance(request.getThreadCount());

        ServiceResult serviceResult = multiThreadUtils.execute(deviceList, new HashMap<>(), asyncTargetTask);

        return serviceResult.asResult();
    }

    @ApiOperation("注册MP地址")
    @GetMapping("/register")
    public ServiceResult register(@RequestParam(required = false) String url){
        initRegisterService.register(url);
        return ServiceResult.successful();
    }

    @ApiOperation("上传靶标图标")
    @PostMapping("/upload/icon")
    @ResponseBody
    public ServiceResult<FileResponseData> uploadIcon(MultipartFile file){
        return targetIconUploadService.build(file).upload();
    }

}
