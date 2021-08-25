package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.ImagesPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    public ImagesController(ImagesConverter imagesConverter) {
        this.imagesConverter = imagesConverter;
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
            ImagesPageResponse response = ImagesPageResponse.builder().uuid(v.getUuid()).name(v.getName())
                    .mainCategory(v.getMainCategory()).subCategory(v.getSubCategory()).imageType(v.getImageType())
                    .systemType(Objects.nonNull(extSystemInfo) ? extSystemInfo.getSystemType() : null)
                    .flavor(Objects.nonNull(extVm) ? extVm.getFlavor() : "")
                    .description(v.getDescription()).build();
            return response;
        }).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody ImageRequest request){
       Boolean result = imagesConverter.add(request);
       return new Result<>(result);
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PostMapping("/update")
    public void update(@RequestBody ImageRequest request){
        imagesConverter.update(request);
    }

    /**
     * 预览
     */
    @ApiOperation("预览")
    @PostMapping("/look")
    public void look(@RequestBody ImagesLookRequest request){
        imagesConverter.look(request);
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


    @ApiOperation(value = "上传", notes = "")
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
       String result = imagesConverter.upload(file);
       return new Result<>(result);
    }

    /**
     * 导出
     */
    @ApiOperation("导出")
    @PostMapping("/export")
    public void export(@RequestBody ImagesExportRequest request){
        imagesConverter.export(request);
    }

    /**
     * 导出
     */
    @ApiOperation("创建虚拟机/容器")
    @PostMapping("/{id}/create")
    public void create(@PathVariable("id") String uuid){
      imagesConverter.create(uuid);
    }
}
