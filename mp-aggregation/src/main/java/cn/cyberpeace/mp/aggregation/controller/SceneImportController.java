package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.sceneimport.SceneExportRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.MoundRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.SceneImportRequest;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ExportResult;
import cn.cyberpeace.mp.aggregation.service.ResourceCenterService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Api(value = "场景导入相关接口", tags = "场景导入相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
public class SceneImportController {

    @Resource
    private ResourceCenterService resourceCenterService;

    @ApiOperation("导入资源")
    @PostMapping("/import_resource_center")
    public ServiceResult<String> importResourceCenter(@RequestBody SceneImportRequest request) {
        log.info("start import scene topo , input params is {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getPath())) {
            return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID);
        }
        ServiceResult<CsceneBO> serviceResult = resourceCenterService.importByYmlConfig(request.getPath(), request.getImagePath(), request.getImagePathList(), request.getSceneFilePath());
        return ServiceResult.success(serviceResult.getSource().getId());
    }

    @ApiOperation("场景导出")
    @PostMapping("/export_scene")
    public ServiceResult<ExportResult> exportScene(@RequestBody SceneExportRequest request) {
        return ServiceResult.success(resourceCenterService.exportScene(request.getId(), request.getEnvPath(), request.getImagePath(), request.getSingleImagePath()).getSource());
    }

    @ApiOperation("挂载")
    @PostMapping("/mount")
    public ServiceResult<Boolean> mount(@RequestBody MoundRequest request) {
        log.info("mount nfs , input params is {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getHost()) || StringUtils.isEmpty(request.getPath())) {
            return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID);
        }
        return this.resourceCenterService.mount(request.getHost(), request.getPath()).asResult();
    }

    @ApiOperation("取消挂载")
    @PostMapping("/umount")
    public ServiceResult<Boolean> umount(@RequestBody MoundRequest request) {
        log.info("unmount nfs , input params is {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getPath())) {
            return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID);
        }
        return this.resourceCenterService.umount(request.getPath()).asResult();
    }
}
