package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.sceneimport.ImageExportRequest;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ImageExportResult;
import cn.cyberpeace.mp.aggregation.service.ImageResourceService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(value = "镜像导入导出相关接口", tags = "镜像导入导出相关接口")
@Slf4j
@RestController
    @RequestMapping(ControllerConstant.BASE_API_PATH + "/images")
public class ImageResourceController {
    @Resource
    private ImageResourceService imageResourceService;

    @ApiOperation("镜像导出")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = ImageExportRequest.class)
    @PostMapping("/export_image")
    public ServiceResult<ImageExportResult> exportImage(@RequestBody ImageExportRequest request) {
        ImageExportResult result = new ImageExportResult();
        result.setSuccess(imageResourceService.exportImageByUuid(request.getUuid(), request.getImagePath()));
        return new ServiceResult<>(result).asResult();
    }
}
