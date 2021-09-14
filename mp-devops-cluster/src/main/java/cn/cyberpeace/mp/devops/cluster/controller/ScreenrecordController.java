package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devops.cluster.response.ScreenRecordUsedSpaceResponse;
import cn.cyberpeace.mp.devops.cluster.response.ScreenrecordResponse;
import cn.cyberpeace.mp.devops.cluster.service.impl.ScreenrecordServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 录屏文件管理controller
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Slf4j
@Api(tags = "录屏文件管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/screenrecord")
public class ScreenrecordController {

    @Autowired
    private ScreenrecordServiceImpl screenrecordService;

    @ApiOperation("分页查询录屏文件")
    @GetMapping("/page")
    public ListObjServiceResult<ScreenrecordResponse> pageScreenrecord(ScreenrecordQueryParams queryParams) {
        return screenrecordService.pageScreenrecord(queryParams);
    }

    @ApiOperation("按时间删除录屏文件")
    @DeleteMapping("/time_delete")
    public ServiceResult<Boolean> timeDelete(@RequestBody @Validated ScreenrecordDeleteRequest request) {
        return screenrecordService.removeRecordfile(request);
    }

    @ApiOperation("批量删除录屏文件")
    @DeleteMapping("/batch_delete")
    public ServiceResult<Boolean> batchDelete(@RequestBody @Validated ScreenrecordBatchDeleteRequest request) {
        return screenrecordService.batchDeleteScreenRecord(request);
    }

    @ApiOperation("查询已用空间")
    @GetMapping("/used_space")
    public ServiceResult<ScreenRecordUsedSpaceResponse> queryUsedSpace() {
        return screenrecordService.queryUsedSpace();
    }

}
