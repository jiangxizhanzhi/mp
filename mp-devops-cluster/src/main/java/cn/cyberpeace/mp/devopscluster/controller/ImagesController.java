 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImagesController.java.
 // File size 35977.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.util.FileClient;
import cn.cyberpeace.mp.cscene.service.CsceneSnapshotService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.QuickCreateDevice;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.*;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.InjectFileConverter;
import cn.cyberpeace.mp.devopscluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.*;
import cn.cyberpeace.mp.devopscluster.dto.ImageInjectFileDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceInjectFileCreate;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.*;
import cn.cyberpeace.mp.devopscluster.service.upload.CustomImageUploadService;
import cn.cyberpeace.mp.devopscluster.service.upload.ISOUploadService;
import cn.cyberpeace.mp.devopscluster.service.upload.InjectFileUploadService;
import cn.cyberpeace.mp.devopscluster.service.upload.SshPrivateKeyUploadService;
import cn.cyberpeace.mp.devopscluster.vo.*;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * @ClassName ImagesController
 * @Description 镜像管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/images")
@Api(tags = "靶标管理-镜像管理")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.IMAGE_MANAGE)
public class ImagesController {

    private final ImagesConverter imagesConverter;

    private final SnapshotConverter snapshotConverter;

    private final FlavorService flavorService;

    private final ScenariosService scenariosService;

    private final InjectFileConverter injectFileConverter;

    private final ImagesService imagesService;

    private final ISOService isoService;

    private final SnapshotService snapshotService;

    private final DevopsClusterConfig devopsClusterConfig;

    private final DynamipsService dynamipsService ;

    @NonNull
    private InjectFileUploadService injectFileUploadService;

    @NonNull
    private SshPrivateKeyUploadService sshPrivateKeyUploadService;

    @NonNull
    private CustomImageUploadService customImageUploadService;

    @NonNull
    private ISOUploadService isoUploadService;

    @NonNull
    private CsceneSpecialService csceneSpecialService;

    @NonNull
    private TargetConverter targetConverter;

    @NonNull
    private CsceneSnapshotService csceneSnapshotService;

    @AuthTarget(TargetEnum.DEVOPS_IMAGE_GROUP)
    @ApiOperation("分页查询")
    @GetMapping("/list_template")
    public PageResult<TemplateVO> page() {
        var res = imagesService.listTemplate();
        return new PageResult<>(res, res.size());
    }

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_IMAGE_GROUP)
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<ImagesPageVO> page(ImagePageRequest request){
        BasePageBO<ImageConvertModel> model = imagesConverter.page(new ImagePageDTO(request));
        List<ImagesPageVO> list = convertBOToPageVO(model);

        return new PageResult<>(list,model.getTotal());
    }

    /**
     * 新增
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = ImageRequest.class)
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult add(@RequestBody ImageRequest request){
        request.setLogo(FsCommon.getFileKey(request.getLogo()));
        if (CollectionUtils.isNotEmpty(request.getAccessProtocolList()) && BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(request.getImageType())) {
            // 容器不支持vnc、spice和com
            Set<String> unsupporteedProtocols = new HashSet<>(Arrays.asList("vnc", "spice", "com", "COM"));
            for (var a : request.getAccessProtocolList()) {
                String protocol = a.getProtocol();
                if (StringUtils.isNotEmpty(protocol) && unsupporteedProtocols.contains(protocol)) {
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("容器不支持配置vnc、spice和com"));
                }
            }
        }
        validImageRequest(request);
        // role值处理
        this.convertRole(request);
        imagesConverter.add(request);
        return ResultCommon.success();
    }

    /**
     * 新增
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("从实体设备新增")
    @ApiImplicitParam(name = "request", value = "新增实装设备请求体", dataTypeClass = RealEquipmentRequest.class)
    @PostMapping("/add_from_entity")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增实装", target = OpTargetEnums.REALEQUIPMENT)
    public ServiceResult addFromEntity(@RequestBody ImageRequest request) {
        request.setExtImageInfo(new ExtImageInfo());
        request.setImageType(BaseCloudPluginSceneElementModel.ImageType.VM);
        request.setExtVm(new ExtVm());
        request.getExtVm().setFlavor("2C/2048M/40G");
        request.getExtVm().setCpu(2);
        request.getExtVm().setRam(2048);
        request.getExtVm().setDisk(40);
        request.setName(request.getName() + "_" + TextUtils.uuid().substring(0, 3));
        this.convertRole(request);
        imagesConverter.add(request);
        var req = new ImagePageDTO();
        req.setName(request.getName());
        var m = BeanCommon.getBean(DeviceTargetMapper.class).page(req).get(0);
        return new ServiceResult(m.getUuid()).asResult();
    }

    /**
     * 新增
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = CopyImageRequest.class)
    @PostMapping("/copy")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult copy(@RequestBody CopyImageRequest request) {
        imagesConverter.copy(request);
        return new ServiceResult(true).asResult();
    }


    /**
     * 新增
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = CopySceneImageRequest.class)
    @PostMapping("/copy_scene")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult copySceneImage(@RequestBody CopySceneImageRequest request) {
        imagesConverter.copy(request);
        return new ServiceResult(true).asResult();
    }

    /**
     * 更新
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("更新")
    @ApiImplicitParam(name = "request", value = "更新信息请求体", dataTypeClass = ImageRequest.class)
    @PostMapping("/update")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "修改镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult update(@RequestBody ImageRequest request){
        request.setLogo(FsCommon.getFileKey(request.getLogo()));
        validImageRequest(request);
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if (CollectionUtils.isNotEmpty(request.getAccessProtocolList()) && BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(request.getImageType())) {
            // 容器不支持vnc、spice和com
            Set<String> unsupporteedProtocols = new HashSet<>(Arrays.asList("vnc", "spice", "com", "COM"));
            for (var a : request.getAccessProtocolList()) {
                String protocol = a.getProtocol();
                if (StringUtils.isNotEmpty(protocol) && unsupporteedProtocols.contains(protocol)) {
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("容器不支持配置vnc、spice和com"));
                }
            }
        }
        // role值处理
        this.convertRole(request);
        imagesConverter.update(request);
        return ResultCommon.success();
    }

    private void convertRole(ImageRequest request){
        // 网络设备/路由器 -> role = 21
        // 网络设备/交换机 -> role = 22
        // 网络设备/防火墙 -> role = 23
        if (request.getCategory() != null && request.getSecondCategory() != null) {
            if (request.getCategory() == 3 && request.getSecondCategory() == 52) {
                request.setRole(23);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 50) {
                request.setRole(22);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 55) {
                request.setRole(22);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 56) {
                request.setRole(22);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 51) {
                request.setRole(21);
            }
        }
    }

    /**
     * 根据UUID查询
     *
     * @param uuid
     * @return
     */
    @ApiOperation("根据UUID查询")
    @ApiImplicitParam(name = "uuid", value = "镜像ID", dataTypeClass = String.class)
    @GetMapping("/search/{uuid}")
    public Result<ImagesByIdVO> findByUuid(@PathVariable("uuid") String uuid) {
        ImageConvertModel model = imagesConverter.findByUuid(uuid);
        ImagesByIdVO imagesByIdVO = new ImagesByIdVO();
        imagesByIdVO.setUuid(model.getUuid());
        imagesByIdVO.setName(model.getName());
        imagesByIdVO.setCategory(model.getMainCategory());
        imagesByIdVO.setSecondCategory(model.getSubCategory());
        imagesByIdVO.setThirdCategory(model.getThirdCategory());
        imagesByIdVO.setRole(model.getRole());
        imagesByIdVO.setLogo(model.getLogo());
        imagesByIdVO.setIsInternal(model.getIsInternal());
        imagesByIdVO.setIsInit(model.getIsInit());
        imagesByIdVO.setIsImport(model.getIsImport());
        ExtVm extVm = model.getExtVm();
        imagesByIdVO.setAccessProtocolList(Objects.nonNull(extVm) ? extVm.getAccessProtocols() : null);
        ExtDocker extDocker = model.getExtDocker();
        if (model.getImageType().equals("container")) {
            imagesByIdVO.setAccessProtocolList(Objects.nonNull(extDocker) ? extDocker.getAccessProtocols() : null);
        }
        imagesByIdVO.setImageType(model.getImageType());
        imagesByIdVO.setDescription(model.getDescription());
        imagesByIdVO.setExtImageInfo(model.getExtImageInfo());
        imagesByIdVO.setExtGateway(model.getExtGateway());
        imagesByIdVO.setExtVm(extVm);
        imagesByIdVO.setExtSystemInfo(model.getExtSystemInfo());
        imagesByIdVO.setExtDocker(extDocker);
        if (Objects.nonNull(extVm) && StringUtils.isNotEmpty(extVm.getIsoTmpdirUuid())) {
            List<String> res = csceneSpecialService.statISOReferCscenes(extVm.getIsoTmpdirUuid()).getSource();
            imagesByIdVO.setAllowChangeIso(CollectionUtils.isEmpty(res));
        } else {
            imagesByIdVO.setAllowChangeIso(true);
        }
        imagesByIdVO.setModelId(model.getModelId());
        imagesByIdVO.setModelCode(model.getModelCode());
        imagesByIdVO.setBehaveSupport(model.getBehaveSupport());
        imagesByIdVO.setExt(model.getExt());
        imagesByIdVO.setSupportCollect(model.getSupportCollect());
        imagesByIdVO.setCanParticipantsUse(model.getCanParticipantsUse());
        imagesByIdVO.setSupportQga(model.getSupportQga());
        imagesByIdVO.setOperateMachine(model.getOperateMachine());
        return new Result<>(imagesByIdVO);
    }


    /**
     * 根据UUID集合查询
     *
     * @param request
     * @return
     */
    @ApiOperation("根据UUID集合查询")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = ImageByUuidsRequest.class)
    @PostMapping("/search/uuids")
    public Result<List<ImagesByUuidsVO>> findByIds(@RequestBody ImageByUuidsRequest request){
        List<ImageTargetConvertModel> models = imagesConverter.findByUuids(request.getUuids());
        List<ImagesByUuidsVO> responseList = new ArrayList<>();
        for (ImageTargetConvertModel model : models) {
            ImagesByUuidsVO imagesByUuidsVO = getImagesByUuidsVO(model);
            responseList.add(imagesByUuidsVO);
        }
        return  new Result<>(responseList);
    }

    /**
     * @param model
     * @return 根据ID查询返回信息
     */
    private static ImagesByUuidsVO getImagesByUuidsVO(ImageTargetConvertModel model) {
        ImagesByUuidsVO response = new ImagesByUuidsVO();
        response.setName(model.getName());
        response.setUuid(model.getUuid());
        return response;
    }

    /**
     * 构建
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("快速构建")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesBuildRequest.class)
    @PostMapping("/build")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "快速构建镜像", target = OpTargetEnums.IMAGE)
    @Deprecated
    public ServiceResult build(@RequestBody ImagesBuildRequest request){
        imagesConverter.build(request);
        return ResultCommon.success();
    }

    /**
     * 查询快速构建靶标列表
     *
     * @param request
     * @return
     */
    @ApiOperation("查询快速构建靶标列表")
    @GetMapping("/quick_create_list")
    public ListObjServiceResult<QuickCreateDevice> quickCreateList(QuickCreatePageRequest request) {
        return imagesConverter.quickCreateList(request).asResult();
    }

    /**
     * 提交快速构建
     *
     * @param request
     * @return
     */
    @ApiOperation("提交快速构建")
    @PostMapping("/quick_create")
    public ServiceResult<Boolean> quickCreate(@RequestBody QuickCreateRequest request) {
        imagesConverter.quickCreate(request);
        return new ServiceResult<>(true).asResult();
    }


    /**
     * 删除镜像
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("删除")
    @ApiImplicitParam(name = "request", value = "删除信息请求体", dataTypeClass = ImagesDeleteRequest.class)
    @DeleteMapping("/page")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult delete(@RequestBody ImagesDeleteRequest request){
        imagesConverter.delete(request);
        return ResultCommon.success();
    }

    /**
     * 授权
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("授权/批量授权")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesAuthRequest.class)
    @PostMapping("/auth")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "授权镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult auth(@RequestBody ImagesAuthRequest request){
        imagesConverter.auth(request);
        return ResultCommon.success();
    }

    /**
     * 授权
     *
     * @param uuid
     * @return
     */
    @ApiOperation("根据uuid查询授权用户信息")
    @ApiImplicitParam(name = "uuid", value = "请求参数", dataTypeClass = String.class)
    @GetMapping("/auth-users")
    public Result<List<UserBo>> authUsers(@RequestParam("uuid") String uuid ){
        List<UserBo> userBos = imagesConverter.findUsersByUuid(uuid);
        return new Result<>(userBos);
    }

    /**
     * 导出
     *
     * @param uuid
     * @param imageName
     * @return
     */
    @ApiOperation("导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "请求参数", dataTypeClass = String.class),
            @ApiImplicitParam(name = "image_name", value = "镜像名称", dataTypeClass = String.class)
    })
    @GetMapping("/export")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.EXPORT, title = "导出镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult<String> export(@RequestParam("id") String uuid, @RequestParam("image_name") String imageName) {
        if (Boolean.FALSE.equals(devopsClusterConfig.getAllowExport())) {
            ImageConvertModel image = imagesConverter.findByUuid(uuid);
            if (Boolean.TRUE.equals(image.getIsImport()) || Boolean.TRUE.equals(image.getIsInit()) ||
                    Boolean.TRUE.equals(image.getIsInternal())) {
                throw new MpRuntimeException(DevopsClusterErrorCode.BUILD_IN_TARGET_NOT_ALLOWED_EXPORT);
            }
        }
        String downloadUrl = imagesConverter.export(uuid, imageName);
        return new Result<>(downloadUrl);
    }

    /**
     * 创建虚拟机/容器
     *
     * @param uuid
     * @return 统一服务返回结果
     */
    @ApiOperation("编辑镜像")
    @PostMapping("/{id}/create")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.START, title = "创建虚拟机/容器", target = OpTargetEnums.IMAGE)
    public ServiceResult create(@PathVariable("id") String uuid){
        imagesConverter.create(uuid);
        return ResultCommon.success();
    }

    /**
     * 删除虚拟机/容器
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("释放镜像机器")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesDeleteInstanceRequest.class)
    @PostMapping("/delete-instance")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.STOP, title = "释放镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult deleteInstance(@RequestBody ImagesDeleteInstanceRequest request){
        imagesConverter.delete(request);
        return ResultCommon.success();
    }

    /**
     * 重启镜像机器
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("重启镜像机器")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesRebootInstanceRequest.class)
    @PostMapping("/reboot")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.RECOVER, title = "重启镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult reboot(@RequestBody ImagesRebootInstanceRequest request){
        imagesConverter.reboot(request);
        return ResultCommon.success();
    }

    /**
     * 查询快照历史
     *
     * @param uuid
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_SNAPSHOT_GROUP)
    @ApiOperation("查询快照历史")
    @ApiImplicitParam(name = "uuid", value = "靶标id", paramType = "path", dataTypeClass = String.class)
    @GetMapping("/{id}/snapshot/list")
    @Deprecated
    public Result<List<SnapshotConvertModel>> listSnapshot(@PathVariable("id") String uuid){
        List<SnapshotConvertModel> list = snapshotConverter.listSnapshot(uuid);
        return new Result(list);
    }


    /**
     * 查询快照历史
     *
     * @param name
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_SNAPSHOT_GROUP)
    @ApiOperation("根据镜像名称或快照名称查询镜像名称和快照历史信息")
    @ApiImplicitParam(name = "name", value = "镜像名称", dataTypeClass = String.class)
    @GetMapping("/snapshot/by-name")
    @Deprecated
    public Result<DeviceAndSnapshotConvertModel> deviceAndSnapshotByName(@RequestParam String name){
        DeviceAndSnapshotConvertModel convertModel = snapshotConverter.deviceAndSnapshotByName(name);
        return new Result(convertModel);
    }

    /**
     * 合并快照历史
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("合并快照历史")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = SnapshotMergeRequest.class)
    @PostMapping("/snapshot/merge")
    @Deprecated
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "合并快照", target = OpTargetEnums.SNAPSHOT)
    public ServiceResult mergeSnapshot(@RequestBody SnapshotMergeRequest request){
//        targetConverter.validTargetCanMerge(Arrays.asList(request.getUuid()));
        BasePageBO<SnapshotConvertModel> res = snapshotConverter.list(request.getUuid());
        snapshotConverter.merge(request);
        if (res != null && CollectionUtils.isNotEmpty(res.getList())) {
            for (SnapshotConvertModel l : res.getList()) {
                csceneSnapshotService.deleteSnapshotRelativeInfoByResource(l.getUuid());
            }
        }
        return ResultCommon.success();
    }

    /**
     * 删除快照
     *
     * @param request
     * @return
     */
    @ApiOperation("删除快照")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = SnapshotDeleteRequest.class)
    @PostMapping("/snapshot/delete")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除快照", target = OpTargetEnums.SNAPSHOT)
    public ServiceResult<Boolean> deleteSnapshot(@RequestBody SnapshotDeleteRequest request) {
        snapshotConverter.delete(request);
        return ResultCommon.success(true);
    }

    /**
     * 创建快照
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("创建快照")
    @ApiImplicitParam(name = "request", value = "创建数据请求体", dataTypeClass = SnapshotRequest.class)
    @PostMapping("/snapshot/create")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "创建快照", target = OpTargetEnums.SNAPSHOT)
    public ServiceResult createSnapshot(@RequestBody SnapshotRequest request){
        snapshotConverter.create(request);
        return ResultCommon.success();
    }

    /**
     * 快照操作
     *
     * @param request
     * @return
     */
    @ApiOperation("快照操作")
    @PostMapping("/snapshot/action")
    public ServiceResult<Boolean> snapshotAction(@RequestBody SnapshotActionRequest request) {
        snapshotConverter.action(request);
        return new ServiceResult<>(true).asResult();
    }

    /**
     * 根据虚拟机id查询快照列表
     *
     * @param request
     * @return
     */
    @ApiOperation("根据虚拟机id查询快照列表")
    @GetMapping("/snapshot/by_vm_id")
    public ListObjServiceResult<Snapshot> getVmSnapshotById(SnapshotPageRequest request) {
        return snapshotService.getVmSnapshotById(request).asResult();
    }

    /**
     * 根据容器id查询快照列表
     *
     * @param request
     * @return
     */
    @ApiOperation("根据容器id查询快照列表")
    @GetMapping("/snapshot/by_container_id")
    public ListObjServiceResult<ContainerSnapshot> getContainerSnapshotById(SnapshotPageRequest request) {
        return snapshotService.getContainerSnapshotById(request).asResult();
    }


    /**
     * 文件注入
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("文件注入")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImageInjectFileRequest.class)
    @PostMapping("/inject-file")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "文件注入", target = OpTargetEnums.IMAGE)
    public ServiceResult injectFile(@Valid @RequestBody ImageInjectFileRequest request){
        scenariosService.injectFile(new DeviceInjectFileCreate(request.getDeviceUuid(),
                request.getFileUrl(),request.getFileName(),request.getFileSize()));
        return ResultCommon.success();
    }

    /**
     * 查看文件注入列表
     *
     * @param request
     * @return
     */
    @ApiOperation("查看文件注入列表")
    @GetMapping("/inject-file")
    public Result<InjectFileConvertModel> findInjectFileList(ImageInjectFilePageRequest request){
        BasePageBO<InjectFileConvertModel> list = injectFileConverter.list(request.getDeviceUuid());
        return new Result(list.getList());
    }

    /**
     * 删除文件注入
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("删除文件注入")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = ImageInjectFileDeleteRequest.class)
    @DeleteMapping("/inject-file")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除文件注入", target = OpTargetEnums.IMAGE)
    public ServiceResult deleteInjectFile(@RequestBody ImageInjectFileDeleteRequest request){
        injectFileConverter.delete(new ImageInjectFileDeleteDTO(request));
        return ResultCommon.success();
    }


    /**
     * 分页查询
     *
     * @return
     */
    @ApiOperation("查询flavor")
    @GetMapping("/page-flavor")
    public Result<List<Flavor>> pageFlavor(){
        BasePageRequest request = new BasePageRequest();
        request.setPerPage(1000);
        String getParams = ParamUtils.wrapperGetParamsByObject(Boolean.TRUE,request);
        CloudPlatformPageResult<Flavor> page = flavorService.page(getParams);
        List<Flavor> listToSort = new ArrayList<>();
        for (Flavor flavor : page.getList()) {
            listToSort.add(flavor);
        }
        listToSort.sort(Comparator.comparing(Flavor::getDisk));
        List<Flavor> toSort = new ArrayList<>();
        for (Flavor flavor : listToSort) {
            toSort.add(flavor);
        }
        toSort.forEach(flavor -> {
            String ram = flavor.getName().split("/")[1];
            flavor.setRam(Integer.parseInt(ram.substring(0, ram.length()-1)));
        });
        toSort.sort(Comparator.comparing(Flavor::getRam));
        List<Flavor> flavorList = new ArrayList<>();
        for (Flavor flavor : toSort) {
            flavorList.add(flavor);
        }
        flavorList.sort(Comparator.comparing(Flavor::getCpu));
        return new Result<>(flavorList);
    }

    /**
     * 查询dynamips 相关的信息
     *
     * @param platform
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_DYNAMIPS_TARGET_GROUP)
    @ApiOperation("查询dynamips 相关的信息")
    @GetMapping("/dynamic_image_info")
    public Result<DynamicImageInfo> getDynamicImageInfo(@RequestParam(required = false) String platform) {
        DynamicImageInfo info = dynamipsService.getInfo(platform);
        return new Result<>(info);
    }

    /**
     * 查询dynamips flavor 相关的信息
     *
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_DYNAMIPS_TARGET_GROUP)
    @ApiOperation("查询dynamips flavor 相关的信息")
    @GetMapping("/dynamic_type_info")
    public Result<Map<String, Object>> getDynamicTypeInfo() {
        Map<String, Object> info = dynamipsService.getTypeInfo();
        return new Result<>(info);
    }

    /**
     * 根据设备ID获取登录链接信息
     *
     * @param uuid
     * @return
     */
    @ApiOperation("根据设备ID获取登录链接信息")
    @ApiImplicitParam(name = "uuid", value = "设备ID", dataTypeClass = String.class)
    @GetMapping("/connection")
    public Result<List<TargetConnection>> connection(@RequestParam String uuid){
        List<TargetConnection> connection = imagesConverter.getConnections(uuid);
        return new Result<>(connection);
    }

    /**
     * 同步镜像数据
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("同步镜像数据")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImageAsyncRequest.class)
    @PostMapping("/async")
    public ServiceResult async(@RequestBody ImageAsyncRequest request){
        List<Integer> requestIds = request.getIds();
        if (CollectionUtils.isEmpty(requestIds)){
            log.error("requestIds is empty");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Integer result = imagesConverter.async(request);
        if (result > 0){
            return ResultCommon.success();
        }else  {
            log.error("result is 0, nothings to do");
            return ResultCommon.fail("result is 0, nothings to do");
        }
    }

    /**
     * 自定义上传镜像方法
     *
     * @param file
     * @param request
     * @return 返回文件路径等信息
     */
    @ApiOperation("自定义上传镜像方法")
    @PostMapping(value = "/custom/upload")
    @ResponseBody
    public ServiceResult<TargetUploadVO> customUpload(MultipartFile file, HttpServletRequest request) {
        customImageUploadService = customImageUploadService.build(file);
        return customImageUploadService.upload();
    }

    /**
     * SSH文件上传方法
     *
     * @param file
     * @param request
     * @return 返回文件路径等信息
     */
    @ApiOperation("SSH文件上传方法")
    @PostMapping(value = "/file/upload/ssh")
    @ResponseBody
    public ServiceResult<FileResponseData> uploadSshFile(MultipartFile file, HttpServletRequest request) {
        sshPrivateKeyUploadService = sshPrivateKeyUploadService.build(file);
        return sshPrivateKeyUploadService.upload();
    }

    /**
     * SSH文件下载方法
     *
     * @param filePath 文件地址
     * @param response
     */
    @ApiOperation("SSH文件下载方法")
    @ApiImplicitParam(name = "filePath", value = "文件路径", dataTypeClass = String.class)
    @GetMapping(value = "/file/download/ssh")
    public void fileDownload(@RequestParam String filePath, HttpServletResponse response) throws MpRuntimeException {
        FileClient.downloadFile(filePath, response);
    }

    /**
     * 文件注入上传方法
     *
     * @param file
     * @param request
     * @return 返回文件路径等信息
     */
    @ApiOperation("文件注入上传方法")
    @PostMapping(value = "/file/upload/inject")
    @ResponseBody
    public ServiceResult<FileResponseData> uploadInjectFile(MultipartFile file, HttpServletRequest request) {
        injectFileUploadService = injectFileUploadService.build(file);
        return injectFileUploadService.upload();
    }

    /**
     * 上传iso文件
     *
     * @param file
     * @param tmpdirUuid
     * @return
     */
    @ApiOperation("上传iso文件")
    @PostMapping(value = "/file/upload/iso")
    public ServiceResult<ISOUploadVO> uploadISOFile(MultipartFile file, String tmpdirUuid) {
        if (StringUtils.isNotEmpty(tmpdirUuid)) {
            this.checkISOReference(tmpdirUuid);
        }
        isoUploadService = isoUploadService.build(file);
        return isoUploadService.upload();
    }

    /**
     * 下载iso文件
     *
     * @param request
     * @return
     */
    @ApiOperation("下载iso文件")
    @GetMapping(value = "/file/download/iso")
    public ServiceResult<String> downloadISOFile(ISOFileRequest request) {
        BaseCloudPlatformResult result = isoService.download(request.getTmpdirUuid(), request.getFileName());
        return new ServiceResult<>(result.getNginxFileUrl()).asResult();
    }

    /**
     * 删除iso文件
     *
     * @param request
     * @return
     */
    @ApiOperation("删除iso文件")
    @DeleteMapping(value = "/file/delete/iso")
    public ServiceResult<Boolean> deleteISOFile(@RequestBody ISOFileRequest request) {
        this.checkISOReference(request.getTmpdirUuid());
        isoService.delete(request.getTmpdirUuid(), request.getFileName());
        return new ServiceResult<>(true).asResult();
    }

    /**
     * 验证请求参数
     *
     * @param request
     */
    private void validImageRequest(ImageRequest request){
        if (StringUtils.isNotBlank(request.getName()) && request.getName().length() > 126){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_NAME_EXCEED_MAX_LENGTH);
        }
        if (StringUtils.isNotBlank(request.getDescription()) && request.getDescription().length() > 1000){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH);
        }
    }

    /**
     * 转换BO to VO
     *
     * @param model
     * @return
     */
    private List<ImagesPageVO> convertBOToPageVO(BasePageBO<ImageConvertModel> model){
        List<ImagesPageVO> list = new ArrayList<>();
        for (ImageConvertModel v : model.getList()) {
            ImagesPageVO imagesPageVO = getImagesPageVO(v);
            list.add(imagesPageVO);
        }
        return list;
    }

    /**
     * @param v
     * @return 镜像分页返回信息
     */
    private ImagesPageVO getImagesPageVO(ImageConvertModel v) {
        ExtSystemInfo extSystemInfo = v.getExtSystemInfo();
        ExtVm extVm = v.getExtVm();
        ExtImageInfo extImageInfo = v.getExtImageInfo();
        ImagesPageVO vo = ImagesPageVO.builder().uuid(v.getUuid()).name(v.getName())
                .logo(v.getLogo())
                .mainCategory(v.getMainCategory()).subCategory(v.getSubCategory()).thirdCategory(v.getThirdCategory())
                .imageType(v.getImageType())
                .isImport(v.getIsImport())
                .isInternal(v.getIsInternal())
                .allowExport(true)
                .systemType(Objects.nonNull(extSystemInfo) ? extSystemInfo.getSystemType() : null)
                .flavor(Objects.nonNull(extVm) ? extVm.getFlavor() : "")
                .description(v.getDescription()).imageStatus(Objects.nonNull(extImageInfo) ? extImageInfo.getImageStatus() : null)
                .authTotal(v.getAuthTotal()).isPublic(v.getIsPublic()).extSystemInfo(v.getExtSystemInfo())
                .extGateway(v.getExtGateway()).extVm(v.getExtVm()).extImageInfo(v.getExtImageInfo()).extDocker(v.getExtDocker()).build();
        if (vo.getExtDocker() != null && vo.getExtDocker().getExtInfo() != null && vo.getExtDocker().getExtInfo() instanceof Map) {
            Map map = (Map) vo.getExtDocker().getExtInfo();
            if (map.containsKey("message")) {
                String result = CloudMessageConvertUtil.getMessage(String.valueOf(map.get("message")));
                if (result != null) {
                    map.put("message", result);
                }
            }
        }
        if (vo.getExtVm() != null && vo.getExtVm().getExtInfo() != null && vo.getExtVm().getExtInfo() instanceof Map) {
            Map map = (Map) vo.getExtVm().getExtInfo();
            if (map.containsKey("message")) {
                String result = CloudMessageConvertUtil.getMessage(String.valueOf(map.get("message")));
                if (result != null) {
                    map.put("message", result);
                }
            }
        }
        // 禁止导出所有靶标
        if (Boolean.FALSE.equals(devopsClusterConfig.getAllowExport())) {
            // 内置靶标不可导出
            if (Boolean.TRUE.equals(v.getIsImport() || Boolean.TRUE.equals(v.getIsInit()) || Boolean.TRUE.equals(v.getIsInternal()))) {
                vo.setAllowExport(false);
            } else if (v.getExtImageInfo() != null && StringUtils.isNotEmpty(v.getExtImageInfo().getBaseImageName())) {
                // 有基础镜像的 以基础镜像为准设置是否可导出
                ImagePageDTO pageDTO = new ImagePageDTO();
                pageDTO.setName(v.getExtImageInfo().getBaseImageName());
                pageDTO.setLikeName(false);
                BasePageBO<ImageConvertModel> result = this.imagesConverter.page(pageDTO);
                List<ImageConvertModel> list = result.getList();
                if (CollectionUtils.isNotEmpty(list)) {
                    ImageConvertModel baseImage = list.get(0);
                    if (Boolean.TRUE.equals(baseImage.getIsImport() || Boolean.TRUE.equals(baseImage.getIsInit()) || Boolean.TRUE.equals(baseImage.getIsInternal()))) {
                        vo.setAllowExport(false);
                    } else if (Boolean.FALSE.equals(v.getSupportExport())) {
                        vo.setAllowExport(false);
                    }
                }
            } else if (Boolean.FALSE.equals(v.getSupportExport())) {
                vo.setAllowExport(false);
            }
        }
        vo.setCreateUserName(v.getCreateUserName());
        return vo;
    }

    /**
     * @param isoTmpdirUuid
     */
    private void checkISOReference(String isoTmpdirUuid) {
        List<String> result = csceneSpecialService.statISOReferCscenes(isoTmpdirUuid).getSource();
        if (CollectionUtils.isNotEmpty(result)) {
            throw new MpRuntimeException(DevopsClusterErrorCode.ISO_REFERENCE_SCENE_WITH_NAME
                    .format(StringUtils.joinWith(",", result.toArray())));
        }
    }
}
