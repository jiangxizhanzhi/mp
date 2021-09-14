package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotUploadRequest;
import cn.cyberpeace.mp.devops.cluster.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.devops.cluster.response.ScreenshotUsedSpaceResponse;
import cn.cyberpeace.mp.devops.cluster.service.ScreenshotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 截屏文件管理controller
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Slf4j
@Api(tags = "截屏文件管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/screenshot")
public class ScreenshotController extends BaseModelController<ScreenshotModel, ScreenshotQueryParams, ScreenshotService> {

    @Autowired
    public ScreenshotController(ScreenshotService screenshotService) {
        setModelService(screenshotService);
    }

    @ApiOperation("截屏文件上传")
    @PostMapping("/upload")
    public ServiceResult<ScreenshotModel> screenshotUpload(@Validated ScreenshotUploadRequest request) {
        return getModelService().screenshotUpload(request);
    }

    @ApiOperation("查询已用空间")
    @GetMapping("/used_space")
    public ServiceResult<ScreenshotUsedSpaceResponse> queryUsedSpace() {
        return getModelService().queryUsedSpace();
    }

    @ApiOperation("按时间删除截屏文件")
    @DeleteMapping("/time_delete")
    public ServiceResult<Integer> timeDelete(@RequestBody @Validated ScreenshotDeleteRequest request) {
        return getModelService().timeDelete(request);
    }

}
