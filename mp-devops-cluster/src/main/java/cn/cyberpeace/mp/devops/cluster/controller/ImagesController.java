package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.ImagesPageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 镜像管理
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/images")
@Api(tags = "镜像管理")
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
    public ListObjServiceResult<ImagesPageResponse> page(ImagePageRequest request){
        imagesConverter.page(request);
        return null;
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public void add(@RequestBody ImageAddRequest request){
        imagesConverter.add(request);
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PostMapping("/update")
    public void update(@RequestBody ImageUpdateRequest request){
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
    @ApiOperation("授权")
    @PostMapping("/auth")
    public void auth(@RequestBody ImagesAuthRequest request){
        imagesConverter.auth(request);
    }

    /**
     * 导出
     */
    @ApiOperation("导出")
    @PostMapping("/export")
    public void export(@RequestBody ImagesExportRequest request){
        imagesConverter.export(request);
    }
}
