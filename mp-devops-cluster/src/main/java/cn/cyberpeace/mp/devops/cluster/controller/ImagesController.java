package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.model.Flavor;
import cn.cyberpeace.mp.devops.cluster.model.FlavorCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtImageInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.ImagesByIdResponse;
import cn.cyberpeace.mp.devops.cluster.response.ImagesPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.service.FlavorService;
import cn.cyberpeace.mp.devops.cluster.service.TargetLogoService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private TargetLogoService targetLogoService;

    private FlavorService flavorService;

    @Autowired
    public ImagesController(ImagesConverter imagesConverter,SnapshotConverter snapshotConverter,
                            TargetLogoService targetLogoService,FlavorService flavorService) {
        this.imagesConverter = imagesConverter;
        this.snapshotConverter = snapshotConverter;
        this.targetLogoService = targetLogoService;
        this.flavorService = flavorService;
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
                    .mainCategory(v.getMainCategory()).subCategory(v.getSubCategory()).imageType(v.getImageType())
                    .systemType(Objects.nonNull(extSystemInfo) ? extSystemInfo.getSystemType() : null)
                    .flavor(Objects.nonNull(extVm) ? extVm.getFlavor() : "")
                    .description(v.getDescription()).imageStatus(Objects.nonNull(extImageInfo) ? extImageInfo.getImageStatus() : null)
                    .authTotal(v.getAuthTotal()).isPublic(v.getIsPublic()).build();
            return response;
        }).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public ServiceResult add(ImageRequest request){
        String logoUrl = targetLogoService.uploadLogo(request.getLogoFile());
        request.setLogo(logoUrl);
        ImagesUploadRequest uploadRequest = new ImagesUploadRequest();
        String imageUuid = imagesConverter.uploadImage(uploadRequest,request.getImageFile());
        ExtImageInfo extImageInfo = request.getExtImageInfo();
        if (Objects.isNull(extImageInfo)){
            extImageInfo = new ExtImageInfo();
        }
        extImageInfo.setUuid(imageUuid);
        request.setExtImageInfo(extImageInfo);
        Boolean result = imagesConverter.add(request);
       return result ? ServiceResult.successful() : ServiceResult.fail("");
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PostMapping("/update")
    public ServiceResult update(ImageRequest request){
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Boolean result = imagesConverter.update(request);
        return result ? ServiceResult.successful() : ServiceResult.fail("");
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
        imagesByIdResponse.setMainCategory(model.getMainCategory());
        imagesByIdResponse.setSubCategory(model.getSubCategory());
        imagesByIdResponse.setImageType(model.getImageType());
        imagesByIdResponse.setDescription(model.getDescription());
        imagesByIdResponse.setExtImageInfo(model.getExtImageInfo());
        imagesByIdResponse.setExtGateway(model.getExtGateway());
        imagesByIdResponse.setExtVm(model.getExtVm());
        imagesByIdResponse.setExtSystemInfo(model.getExtSystemInfo());
        return new Result<>(imagesByIdResponse);
    }


    /**
     * 构建
     */
    @ApiOperation("快速构建")
    @PostMapping("/build")
    public void build(@RequestBody ImagesBuildRequest request){
        imagesConverter.build(request);
    }


    /**
     * 删除镜像
     */
    @ApiOperation("删除")
    @DeleteMapping("/page")
    public void delete(ImagesDeleteRequest request){
        imagesConverter.delete(request);
    }

    /**
     * 授权
     */
    @ApiOperation("授权/批量授权")
    @PostMapping("/auth")
    public void auth(@RequestBody ImagesAuthRequest request){
        imagesConverter.auth(request);
    }

    /**
     * 导出
     */
    @ApiOperation("导出")
    @GetMapping("/{id}/export")
    public ResponseEntity<Resource> export(@PathVariable("id") String uuid, HttpServletResponse httpServletResponse) {
        HttpResponse httpResponse = imagesConverter.export(uuid);
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new MpRuntimeException("获取文件流失败");
        }

        var httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", resource.getFilename()));
        return ResponseEntity.ok().headers(httpHeaders)
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * 创建虚拟机/容器
     */
    @ApiOperation("创建虚拟机/容器")
    @PostMapping("/{id}/create")
    public void create(@PathVariable("id") String uuid){
      imagesConverter.create(uuid);
    }

    /**
     * 删除虚拟机/容器
     */
    @ApiOperation("删除虚拟机/容器")
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable("id") String uuid){
        imagesConverter.create(uuid);
    }

    /**
     * 查询快照历史
     */
    @ApiOperation("查询快照历史")
    @GetMapping("/{id}/snapshot/list")
    public void listSnapshot(@PathVariable("id") String uuid){
        snapshotConverter.list(uuid);
    }

    /**
     * 合并快照历史
     */
    @ApiOperation("合并快照历史")
    @PostMapping("/{id}/snapshot/merge")
    public void mergeSnapshot(@PathVariable("id") String uuid){
        snapshotConverter.merge(uuid);
    }

    /**
     * 创建快照
     */
    @ApiOperation("创建快照")
    @GetMapping("/snapshot/create")
    public void createSnapshot(@RequestBody SnapshotRequest request){
        snapshotConverter.create(request);
    }

    /**
     * 文件注入
     */
    @ApiOperation("文件注入")
    @PostMapping("/inject-file")
    public void injectFile(@RequestBody ImageInjectFileRequest request){

    }

    /**
     * 查看文件注入列表
     */
    @ApiOperation("查看文件注入列表")
    @GetMapping("/inject-file")
    public void findInjectFileList(@RequestBody ImageInjectFileRequest request){

    }


    /**
     * 分页查询
     */
    @ApiOperation("查询flavor")
    @GetMapping("/page-flavor")
    public Result<List<Flavor>> pageFlavor(){
        FlavorCloudPlatformModel page = flavorService.page("");
        if (Objects.isNull(page) ||  Objects.isNull(page.getData())){
            return new Result();
        }
        return new Result<>(page.getData().getList());
    }
}
