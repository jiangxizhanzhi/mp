package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneAuthorizeBO;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.CsceneActionRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneAuthorizeRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneCreateInstanceRequest;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneQueryParams;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.CsceneLogUtils;
import cn.cyberpeace.mp.cscene.util.LogFileUtil;
import cn.cyberpeace.mp.cscene.util.ZipUtils;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(value = "场景模板", tags = "CSCENE-场景模板")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@Data
public class CsceneController implements BaseModelListControllerBase {

    private CsceneService modelService;

    private CsceneSpecialService csceneSpecialService;

    private BaseAppConfig baseAppConfig;

    private CsceneActionRequest formatActionRequest(CsceneActionRequest request, String paramId) {
        if (request == null) {
            request = new CsceneActionRequest();
        }
        if (!StringUtils.isEmpty(paramId)) {
            request.setId(paramId);
        }
        return request;
    }

    private CsceneCreateInstanceRequest formatCreateInstanceRequest(CsceneCreateInstanceRequest request, String paramId) {
        if (request == null) {
            request = new CsceneCreateInstanceRequest();
        }
        request = (CsceneCreateInstanceRequest) formatActionRequest(request, paramId);
        return request;
    }

    private void correctRequestCsceneParamsForSync(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        CsceneDataModifier.removeEidWhenCreate(csceneBO);
        CsceneDataModifier.removeFilePathPrefix(csceneBO);
        CsceneDataModifier.assembleInitCommands(csceneBO);
        CsceneDataModifier.handlerInputEnableRecording(csceneBO);
        CsceneDataModifier.checkFlowNum(csceneBO);
        CsceneDataModifier.checkVlanId(csceneBO);
        CsceneDataModifier.check(csceneBO);
        CsceneDataModifier.checkIngressAndEgress(csceneBO);
        CsceneDataModifier.correctQuickCreateSupport(csceneBO);
        CsceneDataModifier.checkFirewallRules(csceneBO);
        CsceneDataModifier.handlerGns3Router(csceneBO);
        CsceneDataModifier.handleAccessModePath(csceneBO);
    }

    private void correctRequestCsceneParamsForAppInstance(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        CsceneDataModifier.removeFilePathPrefix(csceneBO);
        CsceneDataModifier.assembleInitCommands(csceneBO);
        CsceneDataModifier.handlerInputEnableRecording(csceneBO);
        CsceneDataModifier.removeUnUsePlugin(csceneBO);
    }

    @Autowired
    public CsceneController(CsceneService csceneService, CsceneSpecialService csceneSpecialService, BaseAppConfig baseAppConfig) {
        setModelService(csceneService);
        setCsceneSpecialService(csceneSpecialService);
        this.baseAppConfig = baseAppConfig;
    }

    @ApiOperation("授权场景模板")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneAuthorizeRequest.class)
    @PostMapping("/authorize")
    public ServiceResult<Boolean> authorize(@RequestBody CsceneAuthorizeRequest request) {
        return modelService.csceneAuthorize(request.getTargetIds(), request.getCsceneIds(), request.getAuthType()).asResult();
    }

    @ApiOperation("获取指定场景模板授权信息")
    @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class)
    @GetMapping("/get_authorize")
    public ServiceResult<CsceneAuthorizeBO> getAuthorize(@RequestParam String id) {
        return modelService.getCsceneAuthorizeInfo(id).asResult();
    }

    @GetMapping
    public ListObjServiceResult<CsceneBO> list(CsceneQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        var result = getModelService().listCsceneBO(queryParams, false, null).asResult();
        getModelService().setSharedUser(result.getData().getList(), queryParams);
        return result;
    }

    @ApiOperation("获取指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id) {
        return modelService.getById(id, false, null).asResult();
    }

    @ApiOperation("导出指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/export", params = "id")
    public ServiceResult<CsceneBO> export(@RequestParam String id) {
        return modelService.export(id, null).asResult();
    }

    @ApiOperation(value = "批量导入场景", notes = "")
    @ApiImplicitParam(name = "file", value = "脚本文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/list_import")
    @ResponseBody
    public void upload(MultipartFile file) throws Exception {
        String zipFileName = TextUtils.uuid();
        File zipFile = new File(zipFileName);
        String contentDir = TextUtils.uuid();
        File contentFile = new File(contentDir);

        FileOutputStream outputStream = null;
        List<CsceneBO> csceneBOS = new ArrayList<>();
        try {
            // 文件先写入本地
            outputStream = new FileOutputStream(zipFile);
            outputStream.write(file.getBytes());
            // 解压
            ZipUtils.unzip(zipFile.getName(), contentDir);
            for (var f : contentFile.listFiles()) {
                var csceneStr = FileUtil.readString(f, "UTF8");
                var bo = JsonCommon.parse(csceneStr, CsceneBO.class);
                String sceneName = f.getName().substring(0, f.getName().indexOf(".json"));
                bo.setName(LogFileUtil.renameNumSuffixFile(sceneName));
                bo.getSceneConfig().getTopology().getScene().setName(sceneName);
                // 导入场景应用类型默认为漏洞挖掘
                bo.setAppType(new String[]{"1000"});
                csceneBOS.add(bo);
            }
            for (var csceneBO : csceneBOS) {
                sync(csceneBO);
            }
        } finally {
            if (outputStream != null) {
                IOUtils.close(outputStream);
            }
            if (zipFile.exists()) {
                FileUtils.delete(zipFile);
            }
            if (contentFile.exists()) {
                FileUtils.deleteDirectory(contentFile);
            }
        }
    }

    @ApiOperation("批量导出场景")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "场景模板ids", dataTypeClass = String[].class),
    })
    @GetMapping(value = "/list_export", params = "ids")
    public void export(HttpServletResponse response, @RequestParam String[] ids) throws Exception {
        List<CsceneBO> csceneBOS = new ArrayList<>();
        for (String id : ids) {
            var bo = modelService.export(id, null).getSource();
            bo.setBizType(null);
            csceneBOS.add(bo);
        }
        String dir = TextUtils.uuid();
        File file = new File(dir);
        file.mkdir();
        for (var cs : csceneBOS) {
            File content = LogFileUtil.getFinalFileName(new File(dir + "/" + cs.getName() + ".json"));
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(content);
                cs.setId(null);
                cs.getSceneConfig().getTopology().getScene().setEid(null);
                outputStream.write(JsonCommon.stringify(cs).getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                IoUtil.close(outputStream);
            }
        }
        String targetDir = TextUtils.uuid();
        File targetFile = new File(targetDir);
        targetFile.mkdir();
        ZipUtils.zip(dir, targetDir + "/scene.zip");

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            response.setContentType("application/octet-stream");
//            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + "拓扑模板" + ".zip");

//            String fileName = "拓扑模板.zip";
//            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
//            response.setHeader("Content-disposition", "attachment;filename="+fileNameURL+";"+"filename*=utf-8''"+fileNameURL);

            response.addHeader("Content-Disposition", "attachment;filename=" + new String("拓扑模板.zip".getBytes("UTF-8"), "ISO8859-1"));

            byte[] buffer = new byte[4096];
            fis = new FileInputStream(targetDir + "/scene.zip");
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            log.error("failed to export scenes, ids is {}", StringUtils.join(ids, ","), e);
        } finally {
            FileInputStream finalFis = fis;
            BufferedInputStream finalBis = bis;
            //删除文件
            if (finalFis != null) {
                IOUtils.close(finalFis);
            }
            if (finalBis != null) {
                IOUtils.close(finalBis);
            }
            FileUtils.deleteDirectory(file);
            FileUtils.deleteDirectory(targetFile);
        }
    }

    @ApiOperation(value = "保存（创建/更新）场景模板", notes = "请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/sync")
    public ServiceResult<CsceneBO> sync(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        var result = modelService.sync(csceneBO, null, true).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }
        return result;
    }

    @ApiOperation(value = "业务服务端保存（创建/更新）场景模板", notes = "不会校验节点数量,请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/server_api/sync")
    public ServiceResult<CsceneBO> serverApiSync(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        var result = modelService.sync(csceneBO, null, false).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }
        return result;
    }

    @ApiOperation("克隆指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneActionRequest.class)
    })
    @PostMapping(value = "/clone")
    public ServiceResult<CsceneBO> clone(@RequestParam(required = false) String id,
                                         @RequestBody(required = false) CsceneActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.clone(request.getId(), null).asResult();
        CsceneLogUtils.recordSceneConfigLog(request.getId(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, request.getContext());
        return result;
    }

    @ApiOperation("删除场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneActionRequest.class)
    })
    @DeleteMapping
    public ServiceResult<Boolean> delete(@RequestParam(required = false) String id,
                                         @RequestBody(required = false) CsceneActionRequest request) {
        log.info("delete cscene config , cscene id is {}", id);
        request = formatActionRequest(request, id);
        var result = modelService.deleteById(request.getId(), true, false, null).asResult();
        CsceneLogUtils.recordSceneConfigLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.DELETE_SCENE_CONFIG, request.getContext());
        return result;
    }

    @ApiOperation("批量删除场景模板")
    @ApiImplicitParam(name = "ids", value = "删除数据请求体", dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        log.info("delete cscene configs , cscene ids is {}", JsonCommon.stringify(ids));
        var result = modelService.bulkDeleteByIds(ids, true, false, null).asResult();
        CsceneLogUtils.recordSceneConfigLog(Arrays.asList(ids), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_DELETE_SCENE_CONFIGS, null);
        return result;
    }

    @ApiOperation("创建场景模板测试实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "新增数据请求体", dataTypeClass = CsceneCreateInstanceRequest.class)
    })
    @PostMapping(value = "/test_instance")
    public ServiceResult<CsceneInstanceBO> createTestInstance(
            @RequestParam(required = false) String id, @RequestBody(required = false) CsceneCreateInstanceRequest request) {
        log.info("test_instance params,id:{},request:{}", id, JsonCommon.stringify(request));
        request = formatCreateInstanceRequest(request, id);
        correctRequestCsceneParamsForAppInstance(request.getCscene());
        var result = modelService.createTestInstance(request.getId(), false, request.toCsceneInstanceCreateContextBO()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(result == null ? null : result.getData(), OpTypeEnums.START, CsceneLogUtils.START_SCENE, request.getContext());
        return result;
    }

    @ApiOperation("创建场景模板应用实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "新增数据请求体", dataTypeClass = CsceneCreateInstanceRequest.class)
    })
    @PostMapping(value = "/app_instance")
    public ServiceResult<CsceneInstanceBO> createAppInstance(
            @RequestParam(required = false) String id, @RequestBody(required = false) CsceneCreateInstanceRequest request) {
        log.info("app_instance params,id:{},request:{}", id, JsonCommon.stringify(request));
        request = formatCreateInstanceRequest(request, id);
        correctRequestCsceneParamsForAppInstance(request.getCscene());
        var result = modelService.createAppInstance(request.getId(), false, request.toCsceneInstanceCreateContextBO()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(result == null ? null : result.getData(), OpTypeEnums.START, CsceneLogUtils.START_SCENE, request.getContext());
        return result;
    }

    @ApiOperation("获取场景模板统计数据")
    @ApiImplicitParam(name = "ids", value = "请求参数", dataTypeClass = String[].class)
    @GetMapping("/stat")
    public ServiceResult<List<CsceneStat>> statCscenes(@RequestParam String[] ids) {
        return csceneSpecialService.statCscenes(ids).asResult();
    }

    @ApiOperation("统计靶标对应引用该靶标的场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "images", value = "靶标名称", dataTypeClass = String[].class, allowMultiple = true),
    })
    @GetMapping("/stat_device_refer_cscenes")
    public ServiceResult<Map<String, List<DeviceReferCsceneStat>>> statDeviceReferCscenes(
            @RequestParam String[] images) {
        return csceneSpecialService.statDeviceReferCscenes(images).asResult();
    }
}
