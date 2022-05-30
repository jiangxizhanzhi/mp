package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import cn.cyberpeace.mp.cscene.request.ScreenshotBatchDeleteRequest;
import cn.cyberpeace.mp.cscene.request.ScreenshotDeleteRequest;
import cn.cyberpeace.mp.cscene.request.ScreenshotUploadRequest;
import cn.cyberpeace.mp.cscene.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.cscene.response.ScreenshotUsedSpaceResponse;
import cn.cyberpeace.mp.cscene.service.ScreenshotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description 截屏文件管理controller
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Slf4j
@Api(tags = "截屏文件管理接口")
@RestController
@RequestMapping("/devops/cluster" + "/screenshot")
public class ScreenshotController extends BaseModelController<ScreenshotModel, ScreenshotQueryParams, ScreenshotService> {

    @Autowired
    private FileService fileService;

    @Autowired
    public ScreenshotController(ScreenshotService screenshotService) {
        setModelService(screenshotService);
    }

    @ApiOperation("截屏文件上传")
    @PostMapping("/upload")
    public ServiceResult<ScreenshotModel> screenshotUpload(@Validated ScreenshotUploadRequest request) {
        return getModelService().screenshotUpload(request);
    }

    /**
     * 截屏文件下载
     *
     * @param filePath 文件地址
     * @param response
     */
    @ApiOperation("截屏文件下载")
    @GetMapping(value = "/download")
    public void fileDownload(@RequestParam String filePath, HttpServletResponse response) throws MpRuntimeException {
        fileService.downloadFile(filePath, response);
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

    @ApiOperation("批量删除截屏文件")
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDelete(@RequestBody @Validated ScreenshotBatchDeleteRequest request) {
        return getModelService().batchDelete(request);
    }

}
