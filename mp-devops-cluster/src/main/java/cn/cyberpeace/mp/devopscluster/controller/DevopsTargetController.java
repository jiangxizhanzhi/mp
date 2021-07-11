package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.TypeConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.CommonTargetNameEnum;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.request.TargetMultiAsyncRequest;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.service.upload.TargetIconUploadService;
import cn.cyberpeace.mp.devopscluster.thread.ITask;
import cn.cyberpeace.mp.devopscluster.utils.MultiThreadUtils;
import cn.cyberpeace.mp.devopscluster.vo.DeviceTargetByTypeVO;
import cn.cyberpeace.mp.devopscluster.vo.DeviceTargetVO;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.TargetSceneVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.devopscluster.vo.convert.DeviceVoConverter;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
@RequiredArgsConstructor
public class DevopsTargetController {

    private final TargetConverter targetConverter;

    private final TypeConverter typeConverter;

    private final DevopsClusterConfig devopsClusterConfig;

    private final DeviceVoConverter deviceVoConverter;

    private final ITask asyncTargetTask;

    private final InitRegisterService initRegisterService;

    private final TargetIconUploadService targetIconUploadService;

    private final DevopsTargetService devopsTargetService;

    
    @ApiOperation("查询FTP信息")
    @GetMapping("/ftp-info")
    public Result<String> getFtpAddress(){
        String formatUrl = devopsClusterConfig.getCpIp();
        DevopsClusterConfig config = BeanCommon.getBean(DevopsClusterConfig.class);
        if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
            if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                formatUrl = config.getPublicNetProxyIp();
            }
        }
        return new Result<>(formatUrl);
    }

    
    @ApiOperation("查询所有靶标信息")
    @GetMapping("/list")
    public ServiceResult<TargetSceneVO> list(TargetSceneRequest request) {
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
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

        List<TargetSceneVO> targetSceneVOS = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList) {
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<Device> secondDeviceList = targetSceneConvertModel.getList();
            typeConvertModels.forEach(typeConvertModel -> {
                TargetSceneVO targetSceneVO = new TargetSceneVO();
                targetSceneVO.setFirstTypeId(firstId);
                targetSceneVO.setFirstTypeName(first.getName());
                targetSceneVO.setSecondTypeId(typeConvertModel.getId());
                targetSceneVO.setSecondTypeName(typeConvertModel.getName());
                targetSceneVO.setDeviceList(secondDeviceList.stream()
                        .filter(s -> s.getSecondCategory().equals(typeConvertModel.getId()))
                        .collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(targetSceneVO.getDeviceList())) {
                    targetSceneVOS.add(targetSceneVO);
                }
            });
        }
        return new Result(new ListResult<>(targetSceneVOS));
    }

    
    @ApiOperation("查询所有靶标信息(带过滤条件)")
    @GetMapping("/listAll")
    public ServiceResult<Device> listAll(TargetSceneRequest request){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(category)).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(request.getSecondCategoryList())) {
            secondTypeList = secondTypeList.stream().filter(v -> request.getSecondCategoryList().contains(v.getId())).collect(Collectors.toList());
        }

        List<Device> deviceList = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<Device> secondDeviceList = targetSceneConvertModel.getList();
            deviceList.addAll(secondDeviceList);
        }
        return new Result(new ListResult<>(deviceList));
    }


    
    @ApiOperation("查询所有靶标部分信息")
    @GetMapping("/list-part-info")
    public ServiceResult<DeviceTargetByTypeVO> listPartInfo(TargetSceneRequest request){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
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

        List<DeviceTargetByTypeVO> targetScenes = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<DeviceTargetPO> targetSceneConvertModel = targetConverter.page(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<DeviceTargetPO> deviceTargetPOList = targetSceneConvertModel.getList();

            List<DeviceTargetVO> deviceTargetVOList = deviceTargetPOList.stream().map(v -> {
                DeviceTargetVO vo = new DeviceTargetVO();
                vo.setUuid(v.getUuid());
                vo.setName(v.getName());
                vo.setLogo(v.getLogo());
                vo.setRole(v.getRole());
                vo.setCategory(v.getMainCategory());
                vo.setSecondCategory(v.getSubCategory());
                vo.setThirdCategory(v.getThirdCategory());
                vo.setImageType(v.getImageType());
                return vo;
            }).collect(Collectors.toList());
            typeConvertModels.forEach(typeConvertModel -> {
                DeviceTargetByTypeVO targetScene = new DeviceTargetByTypeVO();
                targetScene.setFirstTypeId(firstId);
                targetScene.setFirstTypeName(first.getName());
                targetScene.setSecondTypeId(typeConvertModel.getId());
                targetScene.setSecondTypeName(typeConvertModel.getName());
                targetScene.setDeviceList(deviceTargetVOList.stream()
                        .filter(vo -> vo.getSecondCategory().equals(typeConvertModel.getId()))
                        .collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(targetScene.getDeviceList())) {
                    targetScenes.add(targetScene);
                }
            });
        }
        return new Result(new ListResult<>(targetScenes));
    }

    @ApiOperation("根据UUID查询靶标信息")
    @ApiImplicitParam(name = "uuid", value = "靶标id", dataTypeClass = String.class)
    @GetMapping("/get-by-uuid")
    public Result<DeviceVO> findByUuid(@RequestParam String uuid){
        Device device = targetConverter.findByUuid(uuid);
        DeviceVO deviceVO = deviceVoConverter.sourceToTarget(device);
        return new Result<>(deviceVO);
    }

    @ApiOperation("分页查询靶标信息")
    @GetMapping("/page")
    public PageResult<DeviceVO> page(TargetSceneRequest request){
        BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
        if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<Device> deviceList = targetSceneConvertModel.getList();

        List<DeviceVO> deviceVOList = deviceVoConverter.sourceToTarget(deviceList);

        return new PageResult<>(deviceVOList,targetSceneConvertModel.getTotal());
    }

    @ApiOperation("查询常用靶标信息")
    @GetMapping("/common-targets")
    public ServiceResult<DeviceVO> getCommonTargets(TargetSceneRequest request){
        List<Device> deviceList = targetConverter.getCommonTargets("");
        List<DeviceVO> deviceVOList = deviceVoConverter.sourceToTarget(deviceList);

        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(locale, Locale.CHINA)) {
            
            deviceVOList.forEach(device -> {
                device.setImage(device.getName());
                device.setName(
                        Objects.nonNull(CommonTargetNameEnum.map.get(device.getName())) ?
                                CommonTargetNameEnum.map.get(device.getName()).toValue() : device.getName());
            });
        }

        if (Objects.nonNull(request.getImageType())) {
            deviceVOList = deviceVOList.stream().filter(v -> v.getImageType().equals(request.getImageType().toValue())).collect(Collectors.toList());
        }
        if (Objects.nonNull(request.getRole())) {
            deviceVOList = deviceVOList.stream().filter(deviceVO -> deviceVO.getRole().equals(request.getRole())).collect(Collectors.toList());
        }
        return new Result(new ListResult(deviceVOList));
    }

    @ApiOperation("批量同步靶标数据")
    @GetMapping("/multi-async")
    public ServiceResult multiAsync(TargetMultiAsyncRequest request){
        int deleteNum = devopsTargetService.deleteAllTarget();
        log.info("删除靶标数量：{}",deleteNum);
        List<Device> deviceList = targetConverter.multiAsync(request.getOffset(), request.getLimit());
        MultiThreadUtils multiThreadUtils = MultiThreadUtils.newInstance(request.getThreadCount());

        ServiceResult serviceResult = multiThreadUtils.execute(deviceList, new HashMap<>(), asyncTargetTask);

        return serviceResult.asResult();
    }

    @ApiOperation("注册MP地址")
    @ApiImplicitParam(name = "url", value = "地址", dataTypeClass = String.class)
    @GetMapping("/register")
    public ServiceResult register(@RequestParam(required = false) String url){
        initRegisterService.register(url);
        return ResultCommon.success();
    }

    @ApiOperation("上传靶标图标")
    @PostMapping("/upload/icon")
    @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class)
    @ResponseBody
    public ServiceResult<FileResponseData> uploadIcon(MultipartFile file){
        return targetIconUploadService.build(file).upload();
    }


    @ApiOperation("校验靶标是否存在")
    @PostMapping("/check")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = TargetSceneRequest.class)
    @ResponseBody
    public ServiceResult<List<String>> check(@RequestBody TargetSceneRequest request){
        request.setPage(null);
        request.setPerPage(null);
        List<String> nameList = targetConverter.findByImagePageDTO(new ImagePageDTO(request));
        return new Result(nameList);
    }

}
