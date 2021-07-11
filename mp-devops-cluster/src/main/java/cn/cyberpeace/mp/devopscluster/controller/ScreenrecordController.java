package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScreenrecordService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordConfigRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.ScreenRecordRestoreConfigVO;
import cn.cyberpeace.mp.devopscluster.vo.ScreenRecordUsedSpaceVO;
import cn.cyberpeace.mp.devopscluster.vo.ScreenrecordVO;
import cn.cyberpeace.mp.devopscluster.vo.SpaceStaticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Api(tags = "录屏文件管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/screenrecord")
@RequiredArgsConstructor
public class ScreenrecordController {

    private final ScreenrecordService screenrecordService;

    private final CsceneInstanceMapper csceneInstanceMapper;

    @ApiOperation("分页查询录屏文件")
    @GetMapping("/page")
    public ListObjServiceResult<ScreenrecordVO> pageScreenrecord(ScreenrecordQueryParams queryParams) {
        String sceneId = null;
        if (StringUtils.isNotEmpty(queryParams.getCsceneId())) {
            var csceneModel = csceneInstanceMapper.get(queryParams.getCsceneId());
            sceneId = csceneModel == null ? null : csceneModel.getScene();
        }
        return screenrecordService.pageScreenrecord(queryParams, sceneId);
    }

    @ApiOperation("按时间删除录屏文件")
    @ApiImplicitParam(name = "request", value = "删除请求体", dataTypeClass = ScreenrecordDeleteRequest.class)
    @DeleteMapping("/time_delete")
    public ServiceResult<Boolean> timeDelete(@RequestBody @Validated ScreenrecordDeleteRequest request) {
        return screenrecordService.removeRecordfile(request);
    }

    @ApiOperation("批量删除录屏文件")
    @ApiImplicitParam(name = "request", value = "删除请求体", dataTypeClass = ScreenrecordBatchDeleteRequest.class)
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

    @ApiOperation("获取截屏文件URL")
    @ApiImplicitParam(name = "fileName", value = "文件名", dataTypeClass = String.class)
    @GetMapping("/download")
    public ServiceResult<String> getScreenRecordUrl(@RequestParam String fileName) {
        return screenrecordService.getScreenRecordUrl(fileName);
    }

    @ApiOperation("查询录屏空间上限配置")
    @GetMapping("/get_restore_limit_config")
    public ServiceResult<ScreenRecordRestoreConfigVO> getRestoreLimitConfig() {
        return screenrecordService.getRestoreLimitConfig().asResult();
    }

    @ApiOperation("更新录屏空间上限配置")
    @PostMapping("/update_restore_limit_config")
    public ServiceResult<ScreenRecordRestoreConfigVO> updateRestoreLimitConfig(@RequestBody ScreenrecordConfigRequest request) {
        screenrecordService.updateRestoreLimitConfig(request);
        var result = new ScreenRecordRestoreConfigVO();
        result.setFileLimitSpace(request.getFileLimitSpace());
        result.setFileLimitTime(request.getFileLimitTime());
        return new ServiceResult<>(result).asResult();
    }

}
