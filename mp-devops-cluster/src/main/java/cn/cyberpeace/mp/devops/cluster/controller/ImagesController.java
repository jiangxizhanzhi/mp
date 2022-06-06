package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.FlavorService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.InjectFileConverter;
import cn.cyberpeace.mp.devops.cluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceInjectFileCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.impl.CustomImageUploadService;
import cn.cyberpeace.mp.devops.cluster.service.impl.InjectFileUploadService;
import cn.cyberpeace.mp.devops.cluster.service.impl.SshPrivateKeyUploadService;
import cn.cyberpeace.mp.devops.cluster.vo.ImagesByIdVO;
import cn.cyberpeace.mp.devops.cluster.vo.ImagesByUuidsVO;
import cn.cyberpeace.mp.devops.cluster.vo.ImagesPageVO;
import cn.cyberpeace.mp.devops.cluster.vo.TargetUploadVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 镜像管理
 */
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

    @Qualifier("fastDfsFileServiceImpl")
    @Autowired
    private FileService fileService;

    @NonNull
    private InjectFileUploadService injectFileUploadService;

    @NonNull
    private SshPrivateKeyUploadService sshPrivateKeyUploadService;

    @NonNull
    private CustomImageUploadService customImageUploadService;

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<ImagesPageVO> page(ImagePageRequest request){
        BasePageBO<ImageConvertModel> model = imagesConverter.page(request);
        List<ImagesPageVO> list = model.getList().stream().map(v -> {
            ExtSystemInfo extSystemInfo = v.getExtSystemInfo();
            ExtVm extVm = v.getExtVm();
            ExtImageInfo extImageInfo = v.getExtImageInfo();
            return ImagesPageVO.builder().uuid(v.getUuid()).name(v.getName())
                    .logo(v.getLogo())
                    .mainCategory(v.getMainCategory()).subCategory(v.getSubCategory()).thirdCategory(v.getThirdCategory())
                    .imageType(v.getImageType())
                    .isImport(v.getIsImport())
                    .isInternal(v.getIsInternal())
                    .systemType(Objects.nonNull(extSystemInfo) ? extSystemInfo.getSystemType() : null)
                    .flavor(Objects.nonNull(extVm) ? extVm.getFlavor() : "")
                    .description(v.getDescription()).imageStatus(Objects.nonNull(extImageInfo) ? extImageInfo.getImageStatus() : null)
                    .authTotal(v.getAuthTotal()).isPublic(v.getIsPublic()).extSystemInfo(v.getExtSystemInfo())
                    .extGateway(v.getExtGateway()).extVm(v.getExtVm()).extImageInfo(v.getExtImageInfo()).extDocker(v.getExtDocker()).build();
        }).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public ServiceResult add(@RequestBody ImageRequest request){
        validImageRequest(request);
        Device device = imagesConverter.add(request);
        if (Objects.isNull(device)){
            return new Result();
        }
        return device.isSuccess() ? ServiceResult.successful() : ServiceResult.fail(device.getMessage());
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PostMapping("/update")
    public ServiceResult update(@RequestBody ImageRequest request){
        validImageRequest(request);
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Device device = imagesConverter.update(request);
        if (Objects.isNull(device)){
            return new Result();
        }
        return device.isSuccess() ? ServiceResult.successful() : ServiceResult.fail(device.getMessage());
    }

    /**
     * 根据UUID查询
     */
    @ApiOperation("根据UUID查询")
    @GetMapping("/search/{uuid}")
    public Result<ImagesByIdVO> findById(@PathVariable("uuid") String uuid){
        ImageConvertModel model = imagesConverter.findById(uuid);
        ImagesByIdVO imagesByIdVO = new ImagesByIdVO();
        imagesByIdVO.setUuid(model.getUuid());
        imagesByIdVO.setName(model.getName());
        imagesByIdVO.setCategory(model.getMainCategory());
        imagesByIdVO.setSecondCategory(model.getSubCategory());
        imagesByIdVO.setThirdCategory(model.getThirdCategory());
        imagesByIdVO.setRole(model.getRole());
        imagesByIdVO.setLogo(model.getLogo());
        imagesByIdVO.setIsInternal(model.getIsInternal());
        imagesByIdVO.setIsImport(model.getIsImport());
        ExtVm extVm = model.getExtVm();
        imagesByIdVO.setAccessProtocolList(Objects.nonNull(extVm) ? extVm.getAccessProtocols() : null);
        ExtDocker extDocker = model.getExtDocker();
        if (model.getImageType().equals("container")){
            imagesByIdVO.setAccessProtocolList(Objects.nonNull(extDocker) ? extDocker.getAccessProtocols() : null);
        }
        imagesByIdVO.setImageType(model.getImageType());
        imagesByIdVO.setDescription(model.getDescription());
        imagesByIdVO.setExtImageInfo(model.getExtImageInfo());
        imagesByIdVO.setExtGateway(model.getExtGateway());
        imagesByIdVO.setExtVm(extVm);
        imagesByIdVO.setExtSystemInfo(model.getExtSystemInfo());
        imagesByIdVO.setExtDocker(extDocker);
        Result<ImagesByIdVO> imagesByIdResponseResult = new Result<>(imagesByIdVO);
        return imagesByIdResponseResult;
    }


    /**
     * 根据UUID集合查询
     */
    @ApiOperation("根据UUID集合查询")
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

    /**
     * 构建
     */
    @ApiOperation("快速构建")
    @PostMapping("/build")
    public ServiceResult build(@RequestBody ImagesBuildRequest request){
        Device device = imagesConverter.build(request);
        if (Objects.isNull(device)){
            return new Result();
        }
        return device.isSuccess() ? ServiceResult.successful() : ServiceResult.fail(device.getMessage());
    }


    /**
     * 删除镜像
     */
    @ApiOperation("删除")
    @DeleteMapping("/page")
    public ServiceResult delete(@RequestBody ImagesDeleteRequest request){
        BaseCloudPlatformResult model = imagesConverter.delete(request);
        return model.isSuccess() ? new Result<>(model.getUrl()) : ServiceResult.fail(model.getMessage());
    }

    /**
     * 授权
     */
    @ApiOperation("授权/批量授权")
    @PostMapping("/auth")
    public ServiceResult auth(@RequestBody ImagesAuthRequest request){
        imagesConverter.auth(request);
        return ServiceResult.successful();
    }

    /**
     * 授权
     */
    @ApiOperation("根据uuid查询授权用户信息")
    @GetMapping("/auth-users")
    public Result<List<UserDto>> authUsers(@RequestParam("uuid") String uuid ){
        List<UserDto> userDtos = imagesConverter.findUsersByUuid(uuid);
        return new Result<>(userDtos);
    }

    /**
     * 导出
     */
    @ApiOperation("导出")
    @GetMapping("/export")
    public ServiceResult<String> export(@RequestParam("id") String uuid,@RequestParam("image_name") String imageName) {
        BaseCloudPlatformResult model = imagesConverter.export(uuid,imageName);
        return model.isSuccess() ? new Result<>(model.getUrl()) : ServiceResult.fail(model.getMessage());
    }

    /**
     * 创建虚拟机/容器
     */
    @ApiOperation("编辑镜像")
    @PostMapping("/{id}/create")
    public ServiceResult create(@PathVariable("id") String uuid){
        BaseCloudPlatformResult model = imagesConverter.create(uuid);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 删除虚拟机/容器
     */
    @ApiOperation("释放镜像机器")
    @PostMapping("/delete-instance")
    public ServiceResult deleteInstance(@RequestBody ImagesDeleteInstanceRequest request){
        BaseCloudPlatformResult model = imagesConverter.delete(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 重启镜像机器
     */
    @ApiOperation("重启镜像机器")
    @PostMapping("/reboot")
    public ServiceResult reboot(@RequestBody ImagesRebootInstanceRequest request){
        BaseCloudPlatformResult model = imagesConverter.reboot(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 查询快照历史
     */
    @ApiOperation("查询快照历史")
    @GetMapping("/{id}/snapshot/list")
    public Result<List<SnapshotConvertModel>> listSnapshot(@PathVariable("id") String uuid){
        BasePageBO<SnapshotConvertModel> convertModel = snapshotConverter.list(uuid);
        if (Objects.isNull(convertModel)){
            return new Result();
        }
        return new Result(convertModel.getList());
    }


    /**
     * 查询快照历史
     */
    @ApiOperation("根据镜像名称或快照名称查询镜像名称和快照历史信息")
    @GetMapping("/snapshot/by-name")
    public Result<DeviceAndSnapshotConvertModel> deviceAndSnapshotByName(@RequestParam String name){
        DeviceAndSnapshotConvertModel convertModel = snapshotConverter.deviceAndSnapshotByName(name);
        return new Result(convertModel);
    }

    /**
     * 合并快照历史
     */
    @ApiOperation("合并快照历史")
    @PostMapping("/snapshot/merge")
    public ServiceResult mergeSnapshot(@RequestBody SnapshotMergeRequest request){
        BaseCloudPlatformResult model = snapshotConverter.merge(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 创建快照
     */
    @ApiOperation("创建快照")
    @PostMapping("/snapshot/create")
    public ServiceResult createSnapshot(@RequestBody SnapshotRequest request){
        BaseCloudPlatformResult model = snapshotConverter.create(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 文件注入
     */
    @ApiOperation("文件注入")
    @PostMapping("/inject-file")
    public ServiceResult injectFile(@Valid @RequestBody ImageInjectFileRequest request){
        BaseCloudPlatformResult model = scenariosService.injectFile(new DeviceInjectFileCreate(request.getDeviceUuid(),
                request.getFileUrl(),request.getFileName(),request.getFileSize()));
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 查看文件注入列表
     */
    @ApiOperation("查看文件注入列表")
    @GetMapping("/inject-file")
    public Result<InjectFileConvertModel> findInjectFileList(ImageInjectFilePageRequest request){
        BasePageBO<InjectFileConvertModel> list = injectFileConverter.list(request.getDeviceUuid());
        if (Objects.isNull(list)){
            return new Result();
        }
        return new Result(list.getList());
    }

    /**
     * 删除文件注入
     */
    @ApiOperation("删除文件注入")
    @DeleteMapping("/inject-file")
    public ServiceResult deleteInjectFile(@RequestBody ImageInjectFileDeleteRequest request){
        BaseCloudPlatformResult model = injectFileConverter.delete(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return BaseCloudPlatformConstant.CODE.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }


    /**
     * 分页查询
     */
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

    @ApiOperation("根据设备ID获取登录链接信息")
    @GetMapping("/connection")
    public Result<List<TargetConnection>> connection(@RequestParam String uuid){
        CloudPlatformPageResult<TargetConnection> connection = imagesService.connection(uuid);
        return new Result<>(connection.getList());
    }

    @ApiOperation("同步镜像数据")
    @PostMapping("/async")
    public ServiceResult async(@RequestBody ImageAsyncRequest request){
        List<Integer> requestIds = request.getIds();
        if (CollectionUtils.isEmpty(requestIds)){
           throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Integer result = imagesConverter.async(request);
        return result > 0 ? ServiceResult.successful() : ServiceResult.fail("");
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
    @GetMapping(value = "/file/download/ssh")
    public void fileDownload(@RequestParam String filePath, HttpServletResponse response) throws MpRuntimeException {
        fileService.downloadFile(filePath, response);
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
     * 验证请求参数
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
}
