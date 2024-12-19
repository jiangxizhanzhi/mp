package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.sceneimport.SceneExportRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.MoundRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.SceneImportRequest;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ExportResult;
import cn.cyberpeace.mp.aggregation.service.ResourceCenterService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    private static String fromISO2utf8(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        return new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private static List<String> fromISO2utf8(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<String> result = new ArrayList<>();
        for (var s : list) {
            result.add(fromISO2utf8(s));
        }
        return result;
    }

    @ApiOperation("导入资源")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneImportRequest.class)
    @PostMapping("/import_resource_center")
    public ServiceResult<String> importResourceCenter(@RequestBody SceneImportRequest request) {
        log.info("导入场景开始 , 输入参数为: {}", JsonCommon.stringify(request));
        String envPath = request.getPath();
        log.info(" 路径: {}", envPath);
        log.info(" 路径 (iso_8859 -> utf8): {}", new String(envPath.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));

        request.setPath(fromISO2utf8(request.getPath()));
        request.setImagePath(fromISO2utf8(request.getImagePath()));
        request.setSceneFilePath(fromISO2utf8(request.getSceneFilePath()));
        request.setImagePathList(fromISO2utf8(request.getImagePathList()));

        log.info("转换后 , 输入参数为: {}", JsonCommon.stringify(request));

        if (StringUtils.isEmpty(request.getPath())) {
            return ResultCommon.fail(BaseErrorCode.PARAMS_INVALID);
        }
        ServiceResult<CsceneBO> serviceResult = resourceCenterService.importByYmlConfig(request.getPath(), request.getImagePath(), request.getImagePathList(), request.getSceneFilePath());
        return ResultCommon.success(serviceResult.getSource().getId());
    }

    @ApiOperation("场景导出")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneExportRequest.class)
    @PostMapping("/export_scene")
    public ServiceResult<ExportResult> exportScene(@RequestBody SceneExportRequest request) {
        log.info("导出场景开始 , 输入参数为: {}", JsonCommon.stringify(request));
        String envPath = request.getEnvPath();
        log.info("env 路径: {}", envPath);
        log.info("env 路径 (iso_8859 -> utf8): {}", new String(envPath.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));

        request.setSceneNumber(fromISO2utf8(request.getSceneNumber()));
        request.setImagePath(fromISO2utf8(request.getImagePath()));
        request.setEnvPath(fromISO2utf8(request.getEnvPath()));
        request.setSingleImagePath(fromISO2utf8(request.getSingleImagePath()));

        String sceneNumber = request.getSceneNumber() == null ? "" : request.getSceneNumber();
        boolean disableImageExport = request.getDisableImageExport() == null ? false : request.getDisableImageExport();
        return ResultCommon.success(resourceCenterService.exportScene(
                request.getId(), request.getEnvPath(), request.getImagePath(), request.getSingleImagePath(), sceneNumber, disableImageExport
        ).getSource());
    }

    @ApiOperation("挂载")
    @PostMapping("/mount")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = MoundRequest.class)
    public ServiceResult<Boolean> mount(@RequestBody MoundRequest request) {
        if (StringUtils.isEmpty(request.getHost()) || StringUtils.isEmpty(request.getPath())) {
            return ResultCommon.fail(BaseErrorCode.PARAMS_INVALID);
        }
        return this.resourceCenterService.mount(request.getHost(), request.getPath()).asResult();
    }

    @ApiOperation("取消挂载")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = MoundRequest.class)
    @PostMapping("/umount")
    public ServiceResult<Boolean> umount(@RequestBody MoundRequest request) {
        if (StringUtils.isEmpty(request.getPath())) {
            return ResultCommon.fail(BaseErrorCode.PARAMS_INVALID);
        }
        return this.resourceCenterService.umount(request.getPath()).asResult();
    }
}
