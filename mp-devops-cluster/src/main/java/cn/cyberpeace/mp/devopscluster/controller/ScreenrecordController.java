package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScreenrecordService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.ScreenRecordUsedSpaceVO;
import cn.cyberpeace.mp.devopscluster.vo.ScreenrecordVO;
import cn.cyberpeace.mp.devopscluster.vo.SpaceStaticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description 录屏文件管理controller
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Slf4j
@Api(tags = "录屏文件管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/screenrecord")
@RequiredArgsConstructor
public class ScreenrecordController {

    private final ScreenrecordService screenrecordService;

    @ApiOperation("分页查询录屏文件")
    @ApiImplicitParam(name = "queryParams", value = "删除请求体")
    @GetMapping("/page")
    public ListObjServiceResult<ScreenrecordVO> pageScreenrecord(ScreenrecordQueryParams queryParams) {
        return screenrecordService.pageScreenrecord(queryParams);
    }

    @ApiOperation("按时间删除录屏文件")
    @ApiImplicitParam(name = "request", value = "删除请求体")
    @DeleteMapping("/time_delete")
    public ServiceResult<Boolean> timeDelete(@RequestBody @Validated ScreenrecordDeleteRequest request) {
        return screenrecordService.removeRecordfile(request);
    }

    @ApiOperation("批量删除录屏文件")
    @ApiImplicitParam(name = "request", value = "删除请求体")
    @DeleteMapping("/batch_delete")
    public ServiceResult<Boolean> batchDelete(@RequestBody @Validated ScreenrecordBatchDeleteRequest request) {
        return screenrecordService.batchDeleteScreenRecord(request);
    }

    @ApiOperation("查询已用空间")
    @GetMapping("/used_space")
    public ServiceResult<ScreenRecordUsedSpaceVO> queryUsedSpace() {
        return screenrecordService.queryUsedSpace();
    }

    @ApiOperation("查询资源用量情况")
    @GetMapping("/space_statics")
    public ServiceResult<SpaceStaticsVO> querySpaceStatics() {
        return screenrecordService.querySpaceStatics();
    }

    @ApiOperation("下载录屏文件")
    @ApiImplicitParam(name = "fileName", value = "文件名")
    @GetMapping("/download")
    public void download(@RequestParam String fileName, HttpServletResponse response) {
        screenrecordService.download(fileName, response);
    }


}
