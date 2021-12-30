package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.FlavorService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.InjectFileConverter;
import cn.cyberpeace.mp.devops.cluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.*;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.ImageCheckPortResponse;
import cn.cyberpeace.mp.devops.cluster.response.ImagesByIdResponse;
import cn.cyberpeace.mp.devops.cluster.response.ImagesByUuidsResponse;
import cn.cyberpeace.mp.devops.cluster.response.ImagesPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class ImagesController {

    private ImagesConverter imagesConverter;

    private SnapshotConverter snapshotConverter;

    private FlavorService flavorService;

    private ScenariosService scenariosService;

    private InjectFileConverter injectFileConverter;

    private ImagesService imagesService;

    @Autowired
    public ImagesController(ImagesConverter imagesConverter, SnapshotConverter snapshotConverter,
                            FlavorService flavorService, ImagesService imagesService,
                            ScenariosService scenariosService, InjectFileConverter injectFileConverter) {
        this.imagesConverter = imagesConverter;
        this.snapshotConverter = snapshotConverter;
        this.flavorService = flavorService;
        this.scenariosService = scenariosService;
        this.injectFileConverter = injectFileConverter;
        this.imagesService = imagesService;
    }

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<ImagesPageResponse> page(ImagePageRequest request){
        ImageConvertPageModel model = imagesConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<ImagesPageResponse> list = model.getModelList().stream().map(v -> {
            ExtSystemInfo extSystemInfo = v.getExtSystemInfo();
            ExtVm extVm = v.getExtVm();
            ExtImageInfo extImageInfo = v.getExtImageInfo();
            ImagesPageResponse response = ImagesPageResponse.builder().uuid(v.getUuid()).name(v.getName())
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
            return response;
        }).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public ServiceResult add(@Valid @RequestBody ImageRequest request){
        validImageRequest(request);
        Device device = imagesConverter.add(request);
        if (Objects.isNull(device)){
            return new Result();
        }
        return device.isSuccess() ? ServiceResult.successful() : ServiceResult.fail(device.getMessageZh());
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PostMapping("/update")
    public ServiceResult update(@Valid @RequestBody ImageRequest request){
        validImageRequest(request);
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Device device = imagesConverter.update(request);
        if (Objects.isNull(device)){
            return new Result();
        }
        return device.isSuccess() ? ServiceResult.successful() : ServiceResult.fail(device.getMessageZh());
    }

    /**
     * 根据ID查询
     */
    @ApiOperation("根据ID查询")
    @GetMapping("/search/{id}")
    public Result<ImagesByIdResponse> findById(@PathVariable("id") String uuid){
        ImageConvertModel model = imagesConverter.findById(uuid);
        ImagesByIdResponse imagesByIdResponse = new ImagesByIdResponse();
        imagesByIdResponse.setUuid(model.getUuid());
        imagesByIdResponse.setName(model.getName());
        imagesByIdResponse.setCategory(model.getMainCategory());
        imagesByIdResponse.setSecondCategory(model.getSubCategory());
        imagesByIdResponse.setThirdCategory(model.getThirdCategory());
        imagesByIdResponse.setRole(model.getRole());
        imagesByIdResponse.setLogo(model.getLogo());
        imagesByIdResponse.setIsInternal(model.getIsInternal());
        imagesByIdResponse.setIsImport(model.getIsImport());
        ExtVm extVm = model.getExtVm();
        imagesByIdResponse.setAccessProtocolList(Objects.nonNull(extVm) ? extVm.getAccessProtocols() : null);
        ExtDocker extDocker = model.getExtDocker();
        if (model.getImageType().equals("container")){
            imagesByIdResponse.setAccessProtocolList(Objects.nonNull(extDocker) ? extDocker.getAccessProtocols() : null);
        }
        imagesByIdResponse.setImageType(model.getImageType());
        imagesByIdResponse.setDescription(model.getDescription());
        imagesByIdResponse.setExtImageInfo(model.getExtImageInfo());
        imagesByIdResponse.setExtGateway(model.getExtGateway());
        imagesByIdResponse.setExtVm(extVm);
        imagesByIdResponse.setExtSystemInfo(model.getExtSystemInfo());
        imagesByIdResponse.setExtDocker(extDocker);
        return new Result<>(imagesByIdResponse);
    }


    /**
     * 根据UUID集合查询
     */
    @ApiOperation("根据UUID集合查询")
    @PostMapping("/search/uuids")
    public Result<List<ImagesByUuidsResponse>> findByIds(@RequestBody ImageByUuidsRequest request){
        List<ImageTargetConvertModel> models = imagesConverter.findByUuids(request.getUuids());
        List<ImagesByUuidsResponse> responseList = models.stream().map(model -> {
            ImagesByUuidsResponse response = new ImagesByUuidsResponse();
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
        Boolean result = imagesConverter.delete(request);
        return result ? ServiceResult.successful() : ServiceResult.fail("");
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
        BaseCloudPlatformModel model = imagesConverter.export(uuid,imageName);
        return model.isSuccess() ? new Result<>(model.getUrl()) : ServiceResult.fail(model.getMessage());
    }

    /**
     * 创建虚拟机/容器
     */
    @ApiOperation("编辑镜像")
    @PostMapping("/{id}/create")
    public ServiceResult create(@PathVariable("id") String uuid){
        BaseCloudPlatformModel model = imagesConverter.create(uuid);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 删除虚拟机/容器
     */
    @ApiOperation("释放镜像机器")
    @PostMapping("/delete-instance")
    public ServiceResult deleteInstance(@RequestBody ImagesDeleteInstanceRequest request){
        BaseCloudPlatformModel model = imagesConverter.delete(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 重启镜像机器
     */
    @ApiOperation("重启镜像机器")
    @PostMapping("/reboot")
    public ServiceResult reboot(@RequestBody ImagesRebootInstanceRequest request){
        BaseCloudPlatformModel model = imagesConverter.reboot(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 查询快照历史
     */
    @ApiOperation("查询快照历史")
    @GetMapping("/{id}/snapshot/list")
    public Result<List<SnapshotConvertModel>> listSnapshot(@PathVariable("id") String uuid){
        SnapshotListConvertModel convertModel = snapshotConverter.list(uuid);
        if (Objects.isNull(convertModel)){
            return new Result();
        }
        return new Result(convertModel.getModelList());
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
        BaseCloudPlatformModel model = snapshotConverter.merge(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 创建快照
     */
    @ApiOperation("创建快照")
    @PostMapping("/snapshot/create")
    public ServiceResult createSnapshot(@RequestBody SnapshotRequest request){
        BaseCloudPlatformModel model = snapshotConverter.create(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 文件注入
     */
    @ApiOperation("文件注入")
    @PostMapping("/inject-file")
    public ServiceResult injectFile(@Valid @RequestBody ImageInjectFileRequest request){
        BaseCloudPlatformModel model = scenariosService.injectFile(new DeviceInjectFileCreate(request.getDeviceUuid(),
                request.getFileUrl(),request.getFileName(),request.getFileSize()));
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
    }

    /**
     * 查看文件注入列表
     */
    @ApiOperation("查看文件注入列表")
    @GetMapping("/inject-file")
    public Result<InjectFileConvertModel> findInjectFileList(ImageInjectFilePageRequest request){
        InjectFileListConvertModel list = injectFileConverter.list(request.getDeviceUuid());
        if (Objects.isNull(list)){
            return new Result();
        }
        return new Result(list.getModelList());
    }

    /**
     * 删除文件注入
     */
    @ApiOperation("删除文件注入")
    @DeleteMapping("/inject-file")
    public ServiceResult deleteInjectFile(@RequestBody ImageInjectFileDeleteRequest request){
        BaseCloudPlatformModel model = injectFileConverter.delete(request);
        if (Objects.isNull(model)){
            return ServiceResult.fail("");
        }
        return ControllerConstant.code.equals(model.getCode()) ? ServiceResult.successful() : ServiceResult.fail(model.getMessage());
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
        FlavorCloudPlatformModel page = flavorService.page(getParams);
        if (Objects.isNull(page) ||  Objects.isNull(page.getData())){
            return new Result();
        }
        List<Flavor> flavorList = page.getData().getList().stream().sorted(Comparator.comparing(Flavor::getDisk))
                .sorted(Comparator.comparing(Flavor::getRam))
                .sorted(Comparator.comparing(Flavor::getCpu)).collect(Collectors.toList());
        return new Result<>(flavorList);
    }

    @ApiOperation("根据设备ID获取登录链接信息")
    @GetMapping("/connection")
    public Result<List<TargetConnection>> connection(@RequestParam String uuid){
        ImageConnectionCloudPlatform connection = imagesService.connection(uuid);
        if (Objects.isNull(connection)){
            return new Result<>();
        }
        return new Result<>(connection.getItems());
    }

    @ApiOperation("同步镜像数据")
    @PostMapping("/async")
    public ServiceResult async(@RequestBody ImageAsyncRequest request){
        Integer result = imagesConverter.async(request);
        return result > 0 ? ServiceResult.successful() : ServiceResult.fail("");
    }

    @ApiOperation("检验IP+端口是否可连接")
    @GetMapping("/check-access-protocols-connectable")
    public Result<ImageCheckPortResponse> checkAccessProtocolsConnectable(ImageCheckPortRequest request){
        List<ImageCheckPortRequest.ImageCheckPort> list = request.getList();
        List<ImageCheckPortResponse.ImageCheckPort> imageCheckPorts = Lists.newArrayList();
        for (ImageCheckPortRequest.ImageCheckPort imageCheckPort : list) {
            ImageCheckPortResponse.ImageCheckPort checkPort = new ImageCheckPortResponse.ImageCheckPort();
            boolean pass = NetworkUtils.checkPortConnectable(imageCheckPort.getHost(), imageCheckPort.getPort(), 1000);
            checkPort.setPort(imageCheckPort.getPort());
            checkPort.setConnectionId(imageCheckPort.getConnectionId());
            checkPort.setHost(imageCheckPort.getHost());
            checkPort.setPass(pass);
            imageCheckPorts.add(checkPort);
        }
        return new Result(new ImageCheckPortResponse(imageCheckPorts));
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
