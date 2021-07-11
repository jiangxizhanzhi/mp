package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.cscene.service.CsceneSnapshotService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.DynamicImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DynamipsService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.FlavorService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ISOService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SnapshotService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.InjectFileConverter;
import cn.cyberpeace.mp.devopscluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.DeviceAndSnapshotConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageTargetConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.InjectFileConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImageInjectFileDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceInjectFileCreate;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.request.BasePageRequest;
import cn.cyberpeace.mp.devopscluster.request.ISOFileRequest;
import cn.cyberpeace.mp.devopscluster.request.ImageAsyncRequest;
import cn.cyberpeace.mp.devopscluster.request.ImageByUuidsRequest;
import cn.cyberpeace.mp.devopscluster.request.ImageInjectFileDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ImageInjectFilePageRequest;
import cn.cyberpeace.mp.devopscluster.request.ImageInjectFileRequest;
import cn.cyberpeace.mp.devopscluster.request.ImagePageRequest;
import cn.cyberpeace.mp.devopscluster.request.ImageRequest;
import cn.cyberpeace.mp.devopscluster.request.ImagesAuthRequest;
import cn.cyberpeace.mp.devopscluster.request.ImagesBuildRequest;
import cn.cyberpeace.mp.devopscluster.request.ImagesDeleteInstanceRequest;
import cn.cyberpeace.mp.devopscluster.request.ImagesDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ImagesRebootInstanceRequest;
import cn.cyberpeace.mp.devopscluster.request.SnapshotDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.SnapshotMergeRequest;
import cn.cyberpeace.mp.devopscluster.request.SnapshotRequest;
import cn.cyberpeace.mp.devopscluster.service.upload.CustomImageUploadService;
import cn.cyberpeace.mp.devopscluster.service.upload.ISOUploadService;
import cn.cyberpeace.mp.devopscluster.service.upload.InjectFileUploadService;
import cn.cyberpeace.mp.devopscluster.service.upload.SshPrivateKeyUploadService;
import cn.cyberpeace.mp.devopscluster.vo.ISOUploadVO;
import cn.cyberpeace.mp.devopscluster.vo.ImagesByIdVO;
import cn.cyberpeace.mp.devopscluster.vo.ImagesByUuidsVO;
import cn.cyberpeace.mp.devopscluster.vo.ImagesPageVO;
import cn.cyberpeace.mp.devopscluster.vo.TargetUploadVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import cn.hutool.core.util.URLUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/images")
@Api(tags = "靶标管理-镜像管理")
@RequiredArgsConstructor
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

    @Qualifier("fastDfsFileServiceImpl")
    @Autowired
    private FileService fileService;

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

    
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<ImagesPageVO> page(ImagePageRequest request){
        BasePageBO<ImageConvertModel> model = imagesConverter.page(new ImagePageDTO(request));
        List<ImagesPageVO> list = convertBOToPageVO(model);

        return new PageResult<>(list,model.getTotal());
    }

    
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = ImageRequest.class)
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult add(@RequestBody ImageRequest request){
        request.setLogo(FsCommon.getFileKey(request.getLogo()));
        validImageRequest(request);
        imagesConverter.add(request);
        return ResultCommon.success();
    }

    
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
        imagesConverter.update(request);
        return ResultCommon.success();
    }

    
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
            var res = csceneSpecialService.statISOReferCscenes(extVm.getIsoTmpdirUuid()).getSource();
            imagesByIdVO.setAllowChangeIso(CollectionUtils.isEmpty(res));
        } else {
            imagesByIdVO.setAllowChangeIso(true);
        }
        return new Result<>(imagesByIdVO);
    }


    
    @ApiOperation("根据UUID集合查询")
    @ApiImplicitParam(name = "request", value = "查询信息请求体", dataTypeClass = ImageByUuidsRequest.class)
    @PostMapping("/search/uuids")
    public Result<List<ImagesByUuidsVO>> findByIds(@RequestBody ImageByUuidsRequest request){
        List<ImageTargetConvertModel> models = imagesConverter.findByUuids(request.getUuids());
        List<ImagesByUuidsVO> responseList = models.stream().map(model -> {
            ImagesByUuidsVO response = new ImagesByUuidsVO();
            response.setName(model.getName());
            response.setUuid(model.getUuid());
            return response;
        }).collect(Collectors.toList());
        return  new Result<>(responseList);
    }

    
    @ApiOperation("快速构建")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesBuildRequest.class)
    @PostMapping("/build")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "快速构建镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult build(@RequestBody ImagesBuildRequest request){
        imagesConverter.build(request);
        return ResultCommon.success();
    }


    
    @ApiOperation("删除")
    @ApiImplicitParam(name = "request", value = "删除信息请求体", dataTypeClass = ImagesDeleteRequest.class)
    @DeleteMapping("/page")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult delete(@RequestBody ImagesDeleteRequest request){
        imagesConverter.delete(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("授权/批量授权")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesAuthRequest.class)
    @PostMapping("/auth")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "授权镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult auth(@RequestBody ImagesAuthRequest request){
        imagesConverter.auth(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("根据uuid查询授权用户信息")
    @ApiImplicitParam(name = "uuid", value = "请求参数", dataTypeClass = String.class)
    @GetMapping("/auth-users")
    public Result<List<UserBo>> authUsers(@RequestParam("uuid") String uuid ){
        List<UserBo> userBos = imagesConverter.findUsersByUuid(uuid);
        return new Result<>(userBos);
    }

    
    @ApiOperation("导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "请求参数", dataTypeClass = String.class),
            @ApiImplicitParam(name = "image_name", value = "镜像名称", dataTypeClass = String.class)
    })
    @GetMapping("/export")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.EXPORT, title = "导出镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult<String> export(@RequestParam("id") String uuid, @RequestParam("image_name") String imageName) {
        if (Boolean.FALSE.equals(devopsClusterConfig.getAllowExport())) {
            var image = imagesConverter.findByUuid(uuid);
            if (Boolean.TRUE.equals(image.getIsImport()) || Boolean.TRUE.equals(image.getIsInit()) ||
                    Boolean.TRUE.equals(image.getIsInternal())) {
                throw new MpRuntimeException(DevopsClusterErrorCode.BUILD_IN_TARGET_NOT_ALLOWED_EXPORT);
            }
        }
        String downloadUrl = imagesConverter.export(uuid, imageName);
        DevopsClusterConfig config = BeanCommon.getBean(DevopsClusterConfig.class);
        if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
            if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                var u = URLUtil.url(downloadUrl);
                downloadUrl = StringUtils.replaceOnce(downloadUrl, u.getHost(), config.getPublicNetProxyIp());
            }
        }
        return new Result<>(downloadUrl);
    }

    
    @ApiOperation("编辑镜像")
    @PostMapping("/{id}/create")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.START, title = "创建虚拟机/容器", target = OpTargetEnums.IMAGE)
    public ServiceResult create(@PathVariable("id") String uuid){
        imagesConverter.create(uuid);
        return ResultCommon.success();
    }

    
    @ApiOperation("释放镜像机器")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesDeleteInstanceRequest.class)
    @PostMapping("/delete-instance")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.STOP, title = "释放镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult deleteInstance(@RequestBody ImagesDeleteInstanceRequest request){
        imagesConverter.delete(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("重启镜像机器")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImagesRebootInstanceRequest.class)
    @PostMapping("/reboot")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.RECOVER, title = "重启镜像", target = OpTargetEnums.IMAGE)
    public ServiceResult reboot(@RequestBody ImagesRebootInstanceRequest request){
        imagesConverter.reboot(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("查询快照历史")
    @ApiImplicitParam(name = "uuid", value = "靶标id", paramType = "path", dataTypeClass = String.class)
    @GetMapping("/{id}/snapshot/list")
    public Result<List<SnapshotConvertModel>> listSnapshot(@PathVariable("id") String uuid){
        BasePageBO<SnapshotConvertModel> convertModel = snapshotConverter.list(uuid);
        var list = convertModel.getList();
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> sceneIds = list.stream()
                    .map(m -> m.getSceneId())
                    .filter(m -> m != null)
                    .collect(Collectors.toList());
            var nameMap = csceneSpecialService.getCsceneNameBySceneIds(sceneIds).getSource();
            List<String> uuids = new ArrayList<>();
            for (var m : list) {
                var sceneInfo = nameMap.get(m.getSceneId());
                if(StringUtils.isNotEmpty(m.getUuid())){
                    uuids.add(m.getUuid());
                }
                if (sceneInfo != null) {
                    m.setSceneName(sceneInfo.getName());
                    m.setSceneId(sceneInfo.getId());
                    m.setCsceneInstaceId(sceneInfo.getId());
                    m.setCsceneId(sceneInfo.getCsceneId());
                } else {
                    m.setSceneId(null);
                }
            }
            Set<String> deletingSnapshots = csceneSpecialService.getDeletingSnapshots(uuids).getSource();
            List<SnapshotConvertModel> res = new ArrayList<>();
            list.forEach(s -> {
                if (!deletingSnapshots.contains(s.getUuid())) {
                    res.add(s);
                }
            });
            return new Result(res);
        }
        return new Result(list);
    }


    
    @ApiOperation("根据镜像名称或快照名称查询镜像名称和快照历史信息")
    @ApiImplicitParam(name = "name", value = "镜像名称", dataTypeClass = String.class)
    @GetMapping("/snapshot/by-name")
    public Result<DeviceAndSnapshotConvertModel> deviceAndSnapshotByName(@RequestParam String name){
        DeviceAndSnapshotConvertModel convertModel = snapshotConverter.deviceAndSnapshotByName(name);
        return new Result(convertModel);
    }

    
    @ApiOperation("合并快照历史")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = SnapshotMergeRequest.class)
    @PostMapping("/snapshot/merge")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "合并快照", target = OpTargetEnums.SNAPSHOT)
    public ServiceResult mergeSnapshot(@RequestBody SnapshotMergeRequest request){

        var res = snapshotConverter.list(request.getUuid());
        snapshotConverter.merge(request);
        if (res != null && CollectionUtils.isNotEmpty(res.getList())) {
            res.getList().stream().forEach(l -> {
                csceneSnapshotService.deleteSnapshotRelativeInfoByResource(l.getUuid());
            });
        }
        return ResultCommon.success();
    }

    
    @ApiOperation("删除快照")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = SnapshotDeleteRequest.class)
    @PostMapping("/snapshot/delete")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除快照", target = OpTargetEnums.SNAPSHOT)
    public ServiceResult<Boolean> deleteSnapshot(@RequestBody SnapshotDeleteRequest request) {
        snapshotService.deleteSnapshot(request);
        return ResultCommon.success(true);
    }

    
    @ApiOperation("创建快照")
    @ApiImplicitParam(name = "request", value = "创建数据请求体", dataTypeClass = SnapshotRequest.class)
    @PostMapping("/snapshot/create")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "创建快照", target = OpTargetEnums.SNAPSHOT)
    public ServiceResult createSnapshot(@RequestBody SnapshotRequest request){
        snapshotConverter.create(request);
        return ResultCommon.success();
    }

    
    @ApiOperation("文件注入")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImageInjectFileRequest.class)
    @PostMapping("/inject-file")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "文件注入", target = OpTargetEnums.IMAGE)
    public ServiceResult injectFile(@Valid @RequestBody ImageInjectFileRequest request){
        scenariosService.injectFile(new DeviceInjectFileCreate(request.getDeviceUuid(),
                request.getFileUrl(),request.getFileName(),request.getFileSize()));
        return ResultCommon.success();
    }

    
    @ApiOperation("查看文件注入列表")
    @GetMapping("/inject-file")
    public Result<InjectFileConvertModel> findInjectFileList(ImageInjectFilePageRequest request){
        BasePageBO<InjectFileConvertModel> list = injectFileConverter.list(request.getDeviceUuid());
        return new Result(list.getList());
    }

    
    @ApiOperation("删除文件注入")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = ImageInjectFileDeleteRequest.class)
    @DeleteMapping("/inject-file")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除文件注入", target = OpTargetEnums.IMAGE)
    public ServiceResult deleteInjectFile(@RequestBody ImageInjectFileDeleteRequest request){
        injectFileConverter.delete(new ImageInjectFileDeleteDTO(request));
        return ResultCommon.success();
    }


    
    @ApiOperation("查询flavor")
    @GetMapping("/page-flavor")
    public Result<List<Flavor>> pageFlavor(){
        BasePageRequest request = new BasePageRequest();
        request.setPerPage(1000);
        String getParams = ParamUtils.wrapperGetParamsByObject(Boolean.TRUE,request);
        CloudPlatformPageResult<Flavor> page = flavorService.page(getParams);
        List<Flavor> flavorList = page.getList().stream().sorted(Comparator.comparing(Flavor::getDisk))
                .sorted(Comparator.comparing(Flavor::getRam))
                .sorted(Comparator.comparing(Flavor::getCpu)).collect(Collectors.toList());
        return new Result<>(flavorList);
    }

    @ApiOperation("查询dynamips 相关的信息")
    @GetMapping("/dynamic_image_info")
    public Result<DynamicImageInfo> getDynamicImageInfo(@RequestParam(required = false) String platform) {
        var info = dynamipsService.getInfo(platform);
        return new Result<>(info);
    }

    @ApiOperation("查询dynamips flavor 相关的信息")
    @GetMapping("/dynamic_type_info")
    public Result<Map<String, Object>> getDynamicTypeInfo() {
        var info = dynamipsService.getTypeInfo();
        return new Result<>(info);
    }

    @ApiOperation("根据设备ID获取登录链接信息")
    @ApiImplicitParam(name = "uuid", value = "设备ID", dataTypeClass = String.class)
    @GetMapping("/connection")
    public Result<List<TargetConnection>> connection(@RequestParam String uuid){
        CloudPlatformPageResult<TargetConnection> connection = imagesService.connection(uuid);
        return new Result<>(connection.getList());
    }

    @ApiOperation("同步镜像数据")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = ImageAsyncRequest.class)
    @PostMapping("/async")
    public ServiceResult async(@RequestBody ImageAsyncRequest request){
        List<Integer> requestIds = request.getIds();
        if (CollectionUtils.isEmpty(requestIds)){
           throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Integer result = imagesConverter.async(request);
        return result > 0 ? ResultCommon.success() : ResultCommon.fail("");
    }

    
    @ApiOperation("自定义上传镜像方法")
    @PostMapping(value = "/custom/upload")
    @ResponseBody
    public ServiceResult<TargetUploadVO> customUpload(MultipartFile file, HttpServletRequest request) {
        customImageUploadService = customImageUploadService.build(file);
        return customImageUploadService.upload();
    }

    
    @ApiOperation("SSH文件上传方法")
    @PostMapping(value = "/file/upload/ssh")
    @ResponseBody
    public ServiceResult<FileResponseData> uploadSshFile(MultipartFile file, HttpServletRequest request) {
        sshPrivateKeyUploadService = sshPrivateKeyUploadService.build(file);
        return sshPrivateKeyUploadService.upload();
    }

    
    @ApiOperation("SSH文件下载方法")
    @ApiImplicitParam(name = "filePath", value = "文件路径", dataTypeClass = String.class)
    @GetMapping(value = "/file/download/ssh")
    public void fileDownload(@RequestParam String filePath, HttpServletResponse response) throws MpRuntimeException {
        fileService.downloadFile(filePath, response);
    }

    
    @ApiOperation("文件注入上传方法")
    @PostMapping(value = "/file/upload/inject")
    @ResponseBody
    public ServiceResult<FileResponseData> uploadInjectFile(MultipartFile file, HttpServletRequest request) {
        injectFileUploadService = injectFileUploadService.build(file);
        return injectFileUploadService.upload();
    }

    @ApiOperation("上传iso文件")
    @PostMapping(value = "/file/upload/iso")
    public ServiceResult<ISOUploadVO> uploadISOFile(MultipartFile file, String tmpdirUuid) {
        if (StringUtils.isNotEmpty(tmpdirUuid)) {
            this.checkISOReference(tmpdirUuid);
        }
        isoUploadService = isoUploadService.build(file);
        return isoUploadService.upload();
    }

    @ApiOperation("下载iso文件")
    @GetMapping(value = "/file/download/iso")
    public ServiceResult<String> downloadISOFile(ISOFileRequest request) {
        var result = isoService.download(request.getTmpdirUuid(), request.getFileName());
        return new ServiceResult<>(result.getNginxFileUrl()).asResult();
    }

    @ApiOperation("删除iso文件")
    @DeleteMapping(value = "/file/delete/iso")
    public ServiceResult<Boolean> deleteISOFile(@RequestBody ISOFileRequest request) {
        this.checkISOReference(request.getTmpdirUuid());
        isoService.delete(request.getTmpdirUuid(), request.getFileName());
        return new ServiceResult<>(true).asResult();
    }

    
    private void validImageRequest(ImageRequest request){
        if (StringUtils.isNotBlank(request.getName()) && request.getName().length() > 126){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_NAME_EXCEED_MAX_LENGTH);
        }
        if (StringUtils.isNotBlank(request.getDescription()) && request.getDescription().length() > 1000){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH);
        }
    }

    
    private List<ImagesPageVO> convertBOToPageVO(BasePageBO<ImageConvertModel> model){
        return model.getList().stream().map(v -> {
            ExtSystemInfo extSystemInfo = v.getExtSystemInfo();
            ExtVm extVm = v.getExtVm();
            ExtImageInfo extImageInfo = v.getExtImageInfo();
            var vo = ImagesPageVO.builder().uuid(v.getUuid()).name(v.getName())
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
                    var result = CloudMessageConvertUtil.getMessage(String.valueOf(map.get("message")));
                    if (result != null) {
                        map.put("message", result);
                    }
                }
            }
            if (vo.getExtVm() != null && vo.getExtVm().getExtInfo() != null && vo.getExtVm().getExtInfo() instanceof Map) {
                Map map = (Map) vo.getExtVm().getExtInfo();
                if (map.containsKey("message")) {
                    var result = CloudMessageConvertUtil.getMessage(String.valueOf(map.get("message")));
                    if (result != null) {
                        map.put("message", result);
                    }
                }
            }
            
            if (Boolean.FALSE.equals(devopsClusterConfig.getAllowExport())) {
                
                if (Boolean.TRUE.equals(v.getIsImport() || Boolean.TRUE.equals(v.getIsInit()) || Boolean.TRUE.equals(v.getIsInternal()))) {
                    vo.setAllowExport(false);
                } else if (v.getExtImageInfo() != null && StringUtils.isNotEmpty(v.getExtImageInfo().getBaseImageName())) {
                    
                    ImagePageDTO pageDTO = new ImagePageDTO();
                    pageDTO.setName(v.getExtImageInfo().getBaseImageName());
                    pageDTO.setLikeName(false);
                    var result = this.imagesConverter.page(pageDTO);
                    var list = result.getList();
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
        }).collect(Collectors.toList());
    }

    private void checkISOReference(String isoTmpdirUuid) {
        var result = csceneSpecialService.statISOReferCscenes(isoTmpdirUuid).getSource();
        if (CollectionUtils.isNotEmpty(result)) {
            throw new MpRuntimeException(DevopsClusterErrorCode.ISO_REFERENCE_SCENE_WITH_NAME
                    .format(StringUtils.joinWith(",", result.toArray())));
        }
    }
}
