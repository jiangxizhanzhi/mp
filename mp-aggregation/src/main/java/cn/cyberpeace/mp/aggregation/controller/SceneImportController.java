package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.sceneimport.SceneExportRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.MoundRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.SceneImportRequest;
import cn.cyberpeace.mp.aggregation.request.sceneimport.SceneImportRequestV2;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ExportResult;
import cn.cyberpeace.mp.aggregation.service.ResourceCenterService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Api(value = "场景导入相关接口", tags = "场景导入相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@AuthTarget(TargetEnum.ADMIN)
public class SceneImportController {

    @Resource
    private ResourceCenterService resourceCenterService;

    @ApiOperation("场景导入")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneImportRequest.class)
    @PostMapping("/import_resource_center")
    public ServiceResult<String> importResourceCenter(@RequestBody SceneImportRequest request) {
        log.info("导入场景开始 , 输入参数为: {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getPath())) {
            String detail = "导入场景参数错误,缺少path参数 , 输入参数为: " + JsonCommon.stringify(request);
            log.info(detail);
            return ResultCommon.fail(detail);
        }
        ServiceResult<CsceneBO> serviceResult = resourceCenterService.importByConfig(request.getPath(), request.getImagePath(), request.getImagePathList(), request.getSceneFilePath());
        return ResultCommon.success(serviceResult.getSource().getId());
    }

    @ApiOperation("场景导入")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneImportRequest.class)
    @PostMapping("/import_resource_center_by_content")
    public ServiceResult<String> importResourceCenter(@RequestBody SceneImportRequestV2 request) {
        log.info("导入场景开始 , 输入参数为: {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getContent())) {
            String detail = "导入场景参数错误,缺少 content 参数 , 输入参数为: " + JsonCommon.stringify(request);
            log.info(detail);
            return ResultCommon.fail(detail);
        }
        String filePath = TextUtils.uuid() + "/topo2.yml";
        log.info("content 写入文件: " + filePath);
        File file = new File(filePath);
        String fileDir = file.getParentFile().getAbsolutePath();
        try {
            FileUtils.writeStringToFile(file, request.getContent(), "UTF-8");
            ServiceResult<CsceneBO> serviceResult = resourceCenterService.importByConfig(fileDir, null, new ArrayList<>(), null);
            return ResultCommon.success(serviceResult.getSource().getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (file.exists()) {
                file.delete();
                new File(fileDir).delete();
            }
        }
    }

    @ApiOperation("场景导出")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneExportRequest.class)
    @PostMapping("/export_scene")
    public ServiceResult<ExportResult> exportScene(@RequestBody SceneExportRequest request) {
        try {
            log.info("导出场景开始 , 输入参数为: {}", JsonCommon.stringify(request));
            String sceneNumber = request.getSceneNumber() == null ? "" : request.getSceneNumber();
            boolean disableImageExport = request.getDisableImageExport() == null ? false : request.getDisableImageExport();
            var result = ResultCommon.success(resourceCenterService.exportScene(
                    request.getId(), request.getEnvPath(), request.getImagePath(), request.getSingleImagePath(), sceneNumber, disableImageExport
            ).getSource());
            log.info("导出场景结束 , 结果为: {}", JsonCommon.stringify(result));
            return result;
        }catch (Exception e){
            log.info("导出场景出错", e);
            throw e;
        }
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
