package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetPageModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetSceneConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CommonTargetNameEnum;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devops.cluster.request.TargetMultiAsyncRequest;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devops.cluster.service.impl.TargetIconUploadService;
import cn.cyberpeace.mp.devops.cluster.thread.ITask;
import cn.cyberpeace.mp.devops.cluster.utils.MultiThreadUtils;
import cn.cyberpeace.mp.devops.cluster.vo.DeviceTargetByTypeVO;
import cn.cyberpeace.mp.devops.cluster.vo.DeviceTargetVO;
import cn.cyberpeace.mp.devops.cluster.vo.DeviceVO;
import cn.cyberpeace.mp.devops.cluster.vo.TargetSceneVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.ListResult;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.common.Result;
import cn.cyberpeace.mp.devops.cluster.vo.convert.DeviceVoConverter;
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
    public ServiceResult<TargetSceneVO> list(TargetSceneRequest request){
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

                TargetSceneVO targetSceneVO = new TargetSceneVO();

                targetSceneVO.setFirstTypeId(firstId);
                targetSceneVO.setFirstTypeName(first.getName());
                Integer secondId = second.getId();
                targetSceneVO.setSecondTypeId(secondId);
                targetSceneVO.setSecondTypeName(second.getName());
                targetSceneVO.setDeviceList(secondDeviceList);
                targetSceneVOS.add(targetSceneVO);
            }
        }
        return new Result(new ListResult<>(targetSceneVOS));
    }

    /**
     * 查询所有靶标信息
     * @param request
     * @return
     */
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

            for (TypeConvertModel second : typeConvertModels){
                request.setCategory(firstId);
                request.setSecondCategory(second.getId());
                request.setPage(null);
                request.setPerPage(null);
                TargetPageModel targetSceneConvertModel = targetConverter.page(request);
                if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getDeviceTargetPOS())){
                    continue;
                }
                List<DeviceTargetPO> deviceTargetPOList = targetSceneConvertModel.getDeviceTargetPOS();

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
                DeviceTargetByTypeVO targetScene = new DeviceTargetByTypeVO();
                targetScene.setFirstTypeId(firstId);
                targetScene.setFirstTypeName(first.getName());
                Integer secondId = second.getId();
                targetScene.setSecondTypeId(secondId);
                targetScene.setSecondTypeName(second.getName());
                targetScene.setDeviceList(deviceTargetVOList);
                targetScenes.add(targetScene);
            }
        }
        return new Result(new ListResult<>(targetScenes));
    }

    @ApiOperation("根据UUID查询靶标信息")
    @GetMapping("/get-by-uuid")
    public Result<DeviceVO> findByUuid(@RequestParam String uuid){
        Device device = targetConverter.findByUuid(uuid);
        DeviceVO deviceVO = deviceVoConverter.deviceToDeviceVO(device);
        return new Result<>(deviceVO);
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
    public ServiceResult<DeviceVO> getCommonTargets(){
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
