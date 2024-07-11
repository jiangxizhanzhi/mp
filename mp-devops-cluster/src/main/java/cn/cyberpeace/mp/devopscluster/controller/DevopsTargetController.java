package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
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

/**
 * @ClassName DevopsTargetController
 * @Description 靶标管理类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.IMAGE_MANAGE)
public class DevopsTargetController {

    private final TargetConverter targetConverter;

    private final TypeConverter typeConverter;

    private final DevopsClusterConfig devopsClusterConfig;

    private final DeviceVoConverter deviceVoConverter;

    private final ITask asyncTargetTask;

    private final InitRegisterService initRegisterService;

    private final TargetIconUploadService targetIconUploadService;

    private final DevopsTargetService devopsTargetService;

    /**
     * 查询FTP信息
     *
     * @return
     */
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

    /**
     * 查询所有靶标信息
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_TARGET_GROUP)
    @ApiOperation("查询所有靶标信息")
    @GetMapping("/list")
    public ServiceResult<TargetSceneVO> list(TargetSceneRequest request) {
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            List<TypeConvertModel> list = new ArrayList<>();
            for (TypeConvertModel v : firstTypeList) {
                if (v.getId().equals(category)) {
                    list.add(v);
                }
            }
            firstTypeList = list;
        }
        Integer secondCategory = request.getSecondCategory();
        if (Objects.nonNull(secondCategory)) {
            List<TypeConvertModel> list = new ArrayList<>();
            for (TypeConvertModel v : secondTypeList) {
                if (v.getId().equals(secondCategory)) {
                    list.add(v);
                }
            }
            secondTypeList = list;
        }

        List<TargetSceneVO> targetSceneVOS = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList) {
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = new ArrayList<>();
            for (TypeConvertModel v : secondTypeList) {
                if (v.getL1ParentId().equals(firstId)) {
                    typeConvertModels.add(v);
                }
            }
            List<Integer> secondCategories = new ArrayList<>();
            for (TypeConvertModel model : typeConvertModels) {
                Integer id = model.getId();
                secondCategories.add(id);
            }
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<Device> secondDeviceList = targetSceneConvertModel.getList();
            for (TypeConvertModel typeConvertModel : typeConvertModels) {
                TargetSceneVO targetSceneVO = new TargetSceneVO();
                targetSceneVO.setFirstTypeId(firstId);
                targetSceneVO.setFirstTypeName(first.getName());
                targetSceneVO.setSecondTypeId(typeConvertModel.getId());
                targetSceneVO.setSecondTypeName(typeConvertModel.getName());
                List<Device> list = new ArrayList<>();
                for (Device s : secondDeviceList) {
                    if (s.getSecondCategory().equals(typeConvertModel.getId())) {
                        list.add(s);
                    }
                }
                targetSceneVO.setDeviceList(list);
                if (CollectionUtils.isNotEmpty(targetSceneVO.getDeviceList())) {
                    targetSceneVOS.add(targetSceneVO);
                }
            }
        }
        return new Result(new ListResult<>(targetSceneVOS));
    }

    /**
     * 查询所有靶标信息
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_TARGET_GROUP)
    @ApiOperation("查询所有靶标信息(带过滤条件)")
    @GetMapping("/listAll")
    public ServiceResult<Device> listAll(TargetSceneRequest request){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            List<TypeConvertModel> list = new ArrayList<>();
            for (TypeConvertModel v : firstTypeList) {
                if (v.getId().equals(category)) {
                    list.add(v);
                }
            }
            firstTypeList = list;
        }

        if (CollectionUtils.isNotEmpty(request.getSecondCategoryList())) {
            List<TypeConvertModel> list = new ArrayList<>();
            for (TypeConvertModel v : secondTypeList) {
                if (request.getSecondCategoryList().contains(v.getId())) {
                    list.add(v);
                }
            }
            secondTypeList = list;
        }

        List<Device> deviceList = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = new ArrayList<>();
            for (TypeConvertModel v : secondTypeList) {
                if (v.getL1ParentId().equals(firstId)) {
                    typeConvertModels.add(v);
                }
            }
            List<Integer> secondCategories = new ArrayList<>();
            for (TypeConvertModel model : typeConvertModels) {
                Integer id = model.getId();
                secondCategories.add(id);
            }
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
            deviceList.forEach(device -> device.setAttackMockDeviceFlag(request.getAttackMockDeviceFlag()));
        }
        return new Result(new ListResult<>(deviceList));
    }


    /**
     * 查询所有靶标信息
     *
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
            List<TypeConvertModel> list = new ArrayList<>();
            for (TypeConvertModel v : firstTypeList) {
                if (v.getId().equals(category)) {
                    list.add(v);
                }
            }
            firstTypeList = list;
        }
        Integer secondCategory = request.getSecondCategory();
        if (Objects.nonNull(secondCategory)) {
            List<TypeConvertModel> list = new ArrayList<>();
            for (TypeConvertModel v : secondTypeList) {
                if (v.getId().equals(secondCategory)) {
                    list.add(v);
                }
            }
            secondTypeList = list;
        }

        List<DeviceTargetByTypeVO> targetScenes = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = new ArrayList<>();
            for (TypeConvertModel convertModel : secondTypeList) {
                if (convertModel.getL1ParentId().equals(firstId)) {
                    typeConvertModels.add(convertModel);
                }
            }
            List<Integer> secondCategories = new ArrayList<>();
            for (TypeConvertModel model : typeConvertModels) {
                Integer id = model.getId();
                secondCategories.add(id);
            }
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<DeviceTargetPO> targetSceneConvertModel = targetConverter.page(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<DeviceTargetPO> deviceTargetPOList = targetSceneConvertModel.getList();

            List<DeviceTargetVO> deviceTargetVOList = new ArrayList<>();
            for (DeviceTargetPO v : deviceTargetPOList) {
                DeviceTargetVO deviceTargetVO = getDeviceTargetVO(v);
                deviceTargetVOList.add(deviceTargetVO);
            }
            for (TypeConvertModel typeConvertModel : typeConvertModels) {
                DeviceTargetByTypeVO targetScene = new DeviceTargetByTypeVO();
                targetScene.setFirstTypeId(firstId);
                targetScene.setFirstTypeName(first.getName());
                targetScene.setSecondTypeId(typeConvertModel.getId());
                targetScene.setSecondTypeName(typeConvertModel.getName());
                List<DeviceTargetVO> list = new ArrayList<>();
                for (DeviceTargetVO vo : deviceTargetVOList) {
                    if (vo.getSecondCategory().equals(typeConvertModel.getId())) {
                        list.add(vo);
                    }
                }
                targetScene.setDeviceList(list);
                if (CollectionUtils.isNotEmpty(targetScene.getDeviceList())) {
                    targetScenes.add(targetScene);
                }
            }
        }
        return new Result(new ListResult<>(targetScenes));
    }

    /**
     * @param v
     * @return
     */
    private static DeviceTargetVO getDeviceTargetVO(DeviceTargetPO v) {
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
    }

    /**
     * 根据UUID查询靶标信息
     *
     * @param uuid
     * @return
     */
    @ApiOperation("根据UUID查询靶标信息")
    @ApiImplicitParam(name = "uuid", value = "靶标id", dataTypeClass = String.class)
    @GetMapping("/get-by-uuid")
    public Result<DeviceVO> findByUuid(@RequestParam String uuid){
        Device device = targetConverter.findByUuid(uuid);
        DeviceVO deviceVO = deviceVoConverter.sourceToTarget(device);
        return new Result<>(deviceVO);
    }

    /**
     * 分页查询靶标信息
     *
     * @param request
     * @return
     */
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

    /**
     * 查询常用靶标信息
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_TARGET_GROUP)
    @ApiOperation("查询常用靶标信息")
    @GetMapping("/common-targets")
    public ServiceResult<DeviceVO> getCommonTargets(TargetSceneRequest request){
        List<Device> deviceList = targetConverter.getCommonTargets("");
        List<DeviceVO> deviceVOList = deviceVoConverter.sourceToTarget(deviceList);

        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(locale, Locale.CHINA)) {
            // 常用靶标名称国际化处理
            for (DeviceVO device : deviceVOList) {
                device.setImage(device.getName());
                device.setName(
                        Objects.nonNull(CommonTargetNameEnum.map.get(device.getName())) ?
                                CommonTargetNameEnum.map.get(device.getName()).toValue() : device.getName());
            }
        }

        if (Objects.nonNull(request.getImageType())) {
            List<DeviceVO> list = new ArrayList<>();
            for (DeviceVO v : deviceVOList) {
                if (v.getImageType().equals(request.getImageType().toValue())) {
                    list.add(v);
                }
            }
            deviceVOList = list;
        }
        if (Objects.nonNull(request.getRole())) {
            List<DeviceVO> list = new ArrayList<>();
            for (DeviceVO deviceVO : deviceVOList) {
                if (deviceVO.getRole().equals(request.getRole())) {
                    list.add(deviceVO);
                }
            }
            deviceVOList = list;
        }
        return new Result(new ListResult(deviceVOList));
    }

    /**
     * 批量同步靶标数据
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("批量同步靶标数据")
    @GetMapping("/multi-async")
    public ServiceResult multiAsync(TargetMultiAsyncRequest request){
        int deleteNum = devopsTargetService.deleteAllTarget();
        log.info("删除靶标数量：{}",deleteNum);
        List<Device> deviceList = targetConverter.multiAsync(request.getPage(), request.getPerPage());
        MultiThreadUtils multiThreadUtils = MultiThreadUtils.newInstance(request.getThreadCount());

        ServiceResult serviceResult = multiThreadUtils.execute(deviceList, new HashMap<>(), asyncTargetTask);

        return serviceResult.asResult();
    }

    /**
     * 注册MP地址
     *
     * @param url
     * @return 统一服务返回结果
     */
    @ApiOperation("注册MP地址")
    @ApiImplicitParam(name = "url", value = "地址", dataTypeClass = String.class)
    @GetMapping("/register")
    public ServiceResult register(@RequestParam(required = false) String url){
        initRegisterService.register(url);
        return ResultCommon.success();
    }

    /**
     * 上传靶标图标
     *
     * @param file
     * @return
     */
    @ApiOperation("上传靶标图标")
    @PostMapping("/upload/icon")
    @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class)
    @ResponseBody
    public ServiceResult<FileResponseData> uploadIcon(MultipartFile file){
        return targetIconUploadService.build(file).upload();
    }


    /**
     * 校验靶标是否存在
     *
     * @param request
     * @return
     */
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
