package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.ExceptionUtil;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.controller.request.AreaUpdateRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneActionRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneAuthorizeRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneCreateInstanceRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneSyncRequest;
import cn.cyberpeace.mp.cscene.dto.scene.CsceneLoadDto;
import cn.cyberpeace.mp.cscene.model.AzureVmOsDisk;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.AzureVmOsDiskQueryParams;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.cscene.service.param.CsceneQueryParams;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.CsceneLogUtils;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigAreaMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        CsceneDataModifier.handlerEnableRate(csceneBO);
        CsceneDataModifier.checkFlowNum(csceneBO);
        //check cidr not same when enable collection
        CsceneDataModifier.checkSameCidrForCpcollection(csceneBO);
        CsceneDataModifier.checkVlanId(csceneBO);
        CsceneDataModifier.check(csceneBO);
        CsceneDataModifier.checkIngressAndEgress(csceneBO);
        CsceneDataModifier.correctQuickCreateSupport(csceneBO);
        CsceneDataModifier.checkFirewallRules(csceneBO);
        CsceneDataModifier.handleAccessModePath(csceneBO);
        CsceneDataModifier.checkCloudElement(csceneBO);
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
            @ApiImplicitParam(name = "area_id", value = "区域id", dataTypeClass = String.class)
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id, @RequestParam(value = "area_id", required = false) String areaId, @RequestParam(required = false, name = "whole") Boolean whole) {
        if (Boolean.TRUE.equals(whole)) {
            areaId = null;
        } else if (areaId == null) {
            areaId = "";
        }
        var result = modelService.getById(id, areaId, false, null);
        var dataBO = result.getSource().getSceneConfig();
        String sceneId = dataBO.getTopology().getScene().getEid();
        result.getSource().getSceneConfig().getTopology()
                .setOutAreaElementNum(this.modelService.getOutAreaElementNum(sceneId, areaId, dataBO));
        return result.asResult();
    }

    @ApiOperation("导出指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/export", params = "id")
    public ServiceResult<CsceneBO> export(@RequestParam String id) {
        return modelService.export(id, null).asResult();
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

    @ApiOperation("拓扑回显")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/completion_topo")
    public ServiceResult<CsceneBO> completionTopo(@RequestBody CsceneBO csceneBO) {
        correctRequestCsceneParamsForSync(csceneBO);
        // 场景模板对象 转成 场景实例对象
        String configContent = JsonCommon.snakeStringify(csceneBO.getSceneConfig());
        SceneDataBO sceneDataBO = JsonCommon.snakeParse(configContent, SceneDataBO.class);
        sceneDataBO.getTopology().setScene(new SceneBO());

//        因场景初始化会分配ip 先查出所有已有ip 业务处理时把分配的ip去除
        Set<String> existingIpSet = sceneDataBO.getTopology().getElements()
                .stream()
                .flatMap(element -> element.getPorts().stream())
                .map(BaseSceneElementPortBO::getIp)
                .filter(ip -> ip != null && !ip.isBlank())
                .collect(Collectors.toSet());

        // 对场景实例对象先保存再进行初始化
        // 必须先保存 否则无法进行初始化
        SceneHandler instanceHandler = new SceneHandler(sceneDataBO, null);
        instanceHandler.save();
        instanceHandler.initiate();
        instanceHandler.completionTopo(existingIpSet);
        instanceHandler.save();

        // 把实例转回成模板
        String instanceContent = JsonCommon.snakeStringify(instanceHandler.toData());
        SceneConfigDataBO sceneConfigDataBO = JsonCommon.snakeParse(instanceContent, SceneConfigDataBO.class);

        CsceneBO tmp = new CsceneBO();
        sceneConfigDataBO.getTopology().setScene(new SceneConfigBO());
        tmp.setSceneConfig(sceneConfigDataBO);
        correctRequestCsceneParamsForSync(tmp);
        SceneConfigHandler newConfigHandler = new SceneConfigHandler(sceneConfigDataBO, null);
        newConfigHandler.save();

        SceneConfigDataBO returnData = newConfigHandler.toData();
        returnData.getTopology().setScene(csceneBO.getSceneConfig().getTopology().getScene());
        csceneBO.setSceneConfig(newConfigHandler.toData());
        return new ServiceResult<>(csceneBO).asResult();
    }

    /**
     * @param csceneLoadDto
     * @return
     */
    @ApiOperation("添加载荷")
    @ApiImplicitParam(name = "csceneLoadDto", value = "请求体", dataTypeClass = CsceneLoadDto.class)
    @PostMapping("/add_load")
    public ServiceResult<Object> addLoad(@RequestBody CsceneLoadDto csceneLoadDto) {
        return modelService.addLoad(csceneLoadDto).asResult();
    }

    @ApiOperation("虚拟机大小列表")
    @GetMapping("/vm_size_list")
    public ListObjServiceResult<Map> vmSizeList(CsceneInstanceStatQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return modelService.vmSizeList(queryParams).asResult();
    }

    @ApiOperation("根据名字查网卡数")
    @GetMapping("/find_nic_by_name")
    public ServiceResult<String> findNicByName(AzureVmOsDiskQueryParams queryParams) {
        return modelService.findNicByName(queryParams).asResult();
    }

    @ApiOperation("虚拟机系统磁盘类型列表")
    @GetMapping("/vm_os_disk_type_list")
    public ListObjServiceResult<AzureVmOsDisk> vmOsDiskTypeList(CsceneInstanceStatQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return modelService.vmOsDiskTypeList(queryParams).asResult();
    }

    @ApiOperation("虚拟机系统磁盘大小列表")
    @GetMapping("/vm_os_disk_size_list")
    public ListObjServiceResult<AzureVmOsDisk> vmOsDiskSizeList(AzureVmOsDiskQueryParams queryParams) {
        return modelService.vmOsDiskSizeList(queryParams).asResult();
    }

    @ApiOperation("根据区域获取指定id场景模板")
    @GetMapping("/get_by_area")
    public ServiceResult<CsceneBO> getByArea(@RequestParam String id, @RequestParam(required = false, name = "area_id") String areaId) {
        if (areaId == null) {
            areaId = StringUtils.EMPTY;
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getSceneConfig();
        var csceneBO = getByAreaId(sceneId, areaId);
        var roleMapper = BeanCommon.getBean(CsceneRoleMapper.class);
        var list = roleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, id)
                                           .orderBy(BaseCsceneRoleModel::getPk));
        if (list != null) {
            csceneBO.setRoles(convertToRoleBOs(list, CsceneRoleBO.class));
        } else {
            csceneBO.setRoles(new ArrayList<>());
        }
        return new ServiceResult<>(csceneBO).asResult();
    }

    @ApiOperation(value = "syncWithOutCheck 保存（创建/更新）场景模板", notes = "请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/sync_with_out_check")
    public ServiceResult<CsceneBO> syncWithOutCheck(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        var roles = csceneBO.getRoles();
        ServiceResult<CsceneBO> result = modelService.sync(csceneBO, null, true).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }

        try {
            CsceneRoleMapper mapper = BeanCommon.getBean(CsceneRoleMapper.class);
            mapper.filterDelete(new HumanCondition().filterEqual(CsceneRoleModel::getCscene, csceneBO.getId()));
            if (CollectionUtils.isNotEmpty(roles)) {
                List<CsceneRoleModel> roleObjs = convertToRoleObjs(roles, null);
                roleObjs.forEach(r -> r.setCscene(csceneBO.getId()));
                mapper.bulkCreate(roleObjs).getSuccessCount();
            }
        } catch (Exception e) {
            log.error("failed to save roles , cscene id {} , exception is {}", csceneBO.getId(), ExceptionUtil.stacktraceToString(e));
        }

        var topo = result.getData().getSceneConfig().getTopology();
        return this.getByArea(csceneBO.getId(), topo.getAreaId());
    }

    @ApiOperation(value = "创建场景模板")
    @ApiImplicitParam(name = "createTemplateBO", value = "请求体", dataTypeClass = CreateTemplateBO.class)
    @PostMapping("/create_template")
    public ServiceResult<CsceneBO> syncTemplate(@RequestBody CreateTemplateBO createTemplateBO) {
        var csceneBO = modelService.getById(createTemplateBO.getId(), null, false, null).getSource();
        csceneBO.setName(createTemplateBO.getName());
        csceneBO.setLogo(createTemplateBO.getLogo());
        csceneBO.getSceneConfig().getTopology().getScene().setName(createTemplateBO.getName());
        csceneBO.getSceneConfig().getTopology().getScene().setDescription(createTemplateBO.getDescription());
        csceneBO.setType(CsceneModel.Type.TEMPLATE);
        csceneBO.setId(null);
        csceneBO.getSceneConfig().getTopology().getScene().setEid(null);
        CsceneDataModifier.removeEidWhenCreate(csceneBO);
        modelService.sync(csceneBO, null, false);
        return new ServiceResult<>(csceneBO).asResult();
    }

    @ApiOperation(value = "导入场景模板")
    @PostMapping("/import_template")
    public ServiceResult<CsceneBO> importTemplate(ImportTemplateBO importTemplateBO) {
        InputStream inputStream = null;
        try {
            inputStream = importTemplateBO.getFile().getInputStream();
            var bytes = inputStream.readAllBytes();
            CsceneBO csceneBO = JsonCommon.snakeParse(new String(bytes, StandardCharsets.UTF_8), CsceneBO.class);

            String id = this.modelService.get(importTemplateBO.getId()).getSource().getObj().getSceneConfig();
            SceneConfigService sceneConfigService = BeanCommon.getBean(SceneConfigService.class);
            var originSceneConfig = sceneConfigService.get(id, false, null, null).getSource();
            var templateSceneConfig = csceneBO.getSceneConfig();

            int currentNum = originSceneConfig.getTopology().getElements().size();
            int templateNum = templateSceneConfig.getTopology().getElements().size();
            int limit = BeanCommon.getBean(CsceneAppConfig.class).getSceneEditNodeLimit();
            if (currentNum + templateNum > limit) {
                throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_LIMIT.format(String.valueOf(limit), String.valueOf(currentNum), String.valueOf(templateNum)));
            }

            this.removeId(templateSceneConfig);
            this.updateOldId(templateSceneConfig);
            this.modifyAreaId(templateSceneConfig, importTemplateBO.getAreaId());
            templateSceneConfig.getTopology().setAreaId(importTemplateBO.getAreaId());
            CsceneDataModifier.handlerPosition(templateSceneConfig);
            this.mergeSceneConfig(originSceneConfig, templateSceneConfig);
            sceneConfigService.sync(originSceneConfig, false, null);
            var res = sceneConfigService.get(id, true, importTemplateBO.getAreaId(), null).getSource();
            res.getTopology()
                    .setOutAreaElementNum(this.modelService.getOutAreaElementNum(id, importTemplateBO.getAreaId(), res));
            var resultBO = new CsceneBO();
            resultBO.setSceneConfig(res);
            CsceneDataModifier.correctReturnCsceneData(resultBO);
            return new ServiceResult<>(resultBO).asResult();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询场景拓扑区域树形结构")
    @ApiImplicitParam(name = "id", value = "场景拓扑id", dataTypeClass = String.class)
    @GetMapping("/tree")
    public ServiceResult<SceneAreaTreeBO> getSceneConfigAreaTree(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return new ServiceResult<>(new SceneAreaTreeBO()).asResult();
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getSceneConfig();
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, null);
        SceneAreaTreeBO sceneAreaTree = new SceneAreaTreeBO();
        SceneConfigDataBO result = sceneConfigHandler.toData();
        if (result != null && result.getTopology() != null && CollectionUtils.isNotEmpty(result.getTopology().getAreas())) {
            var areas = result.getTopology().getAreas();
            Map<String, List<SceneConfigAreaBO>> parentId2Children = new HashMap<>();
            areas.stream()
                    .filter(sceneConfigAreaBO -> StringUtils.isNotEmpty(sceneConfigAreaBO.getArea()))
                    .forEach(sceneConfigAreaBO -> {
                        if (CollectionUtils.isEmpty(parentId2Children.get(sceneConfigAreaBO.getArea()))) {
                            parentId2Children.put(sceneConfigAreaBO.getArea(), new ArrayList<>());
                        }
                        parentId2Children.get(sceneConfigAreaBO.getArea()).add(sceneConfigAreaBO);
                    });
            areas.stream()
                    .filter(sceneConfigAreaBO -> StringUtils.isEmpty(sceneConfigAreaBO.getArea()))
                    .forEach(sceneConfigAreaBO -> {
                        if (sceneConfigAreaBO.getType() == BaseSceneAreaModel.Type.LAYER) {
                            var area = getTreeArea(sceneConfigAreaBO, parentId2Children);
                            sceneAreaTree.getAreaList().add(area);
                        } else {
                            if (CollectionUtils.isNotEmpty(parentId2Children.get(sceneConfigAreaBO.getId()))) {
                                parentId2Children.get(sceneConfigAreaBO.getId()).stream()
                                        .filter(a -> a.getType() == BaseSceneAreaModel.Type.LAYER)
                                        .forEach(sceneConfigAreaBO1 -> {
                                            var area = getTreeArea(sceneConfigAreaBO1, parentId2Children);
                                            sceneAreaTree.getAreaList().add(area);
                                        });
                            }
                        }
                    });
        }
        return new ServiceResult<>(sceneAreaTree).asResult();
    }

    @ApiOperation("更新区域显示参数")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = AreaUpdateRequest.class)
    @PostMapping("/update_area")
    public ServiceResult<Boolean> updateArea(@RequestBody AreaUpdateRequest request) {

        String id = this.modelService.get(request.getSceneId()).getSource().getObj().getSceneConfig();

        var sceneConfigAreaMapper = BeanCommon.getBean(SceneConfigAreaMapper.class);
        SceneConfigAreaModel sceneConfigAreaModel = sceneConfigAreaMapper.first(new HumanCondition().select(SceneConfigAreaModel::getId)
                                                                                        .filterEqual(SceneConfigAreaModel::getScene, id)
                                                                                        .filterEqual(SceneConfigAreaModel::getNodeId, request.getAreaId()));
        if (sceneConfigAreaModel == null) {
            log.error("scene {} ,  area {} not find , ", request.getSceneId(), request.getAreaId());
            return new ServiceResult<>(true).asResult();
        }
        sceneConfigAreaModel.setDisplayContext(request.getDisplayContext());
        return new ServiceResult<>(sceneConfigAreaMapper.update(sceneConfigAreaModel)).asResult();
    }

    @ApiOperation("更新场景拓扑(模板拖拽)")
    @PostMapping("/sync_with_template")
    public ServiceResult<CsceneBO> syncWithTemplate(@RequestBody CsceneSyncRequest request) {
        String id = this.modelService.get(request.getId()).getSource().getObj().getSceneConfig();
        String templateId = this.modelService.get(request.getTemplateId()).getSource().getObj().getSceneConfig();

        SceneConfigService sceneConfigService = BeanCommon.getBean(SceneConfigService.class);

        var originSceneConfig = sceneConfigService.get(id, false, null, null).getSource();
        var templateSceneConfig = sceneConfigService.get(templateId, false, null, null).getSource();

        int currentNum = originSceneConfig.getTopology().getElements().size();
        int templateNum = templateSceneConfig.getTopology().getElements().size();
        int limit = BeanCommon.getBean(CsceneAppConfig.class).getSceneEditNodeLimit();
        if (currentNum + templateNum > limit) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_LIMIT.format(String.valueOf(limit), String.valueOf(currentNum), String.valueOf(templateNum)));
        }

        this.removeId(templateSceneConfig);
        this.updateOldId(templateSceneConfig);
        this.modifyAreaId(templateSceneConfig, request.getAreaId());
        templateSceneConfig.getTopology().setAreaId(request.getAreaId());
        CsceneDataModifier.handlerPosition(templateSceneConfig);
        this.mergeSceneConfig(originSceneConfig, templateSceneConfig);
        sceneConfigService.sync(originSceneConfig, false, null);
        var res = sceneConfigService.get(id, true, request.getAreaId(), null).getSource();
        var csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(res);
        res.getTopology().setOutAreaElementNum(this.modelService.getOutAreaElementNum(id, request.getAreaId(), res));
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return new ServiceResult<>(csceneBO).asResult();
    }

    private CsceneBO getByAreaId(String sceneId, String areaId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, areaId, true, null);
        SceneConfigDataBO dataBO = sceneConfigHandler.toData();
        if (areaId != null) {
            int num = this.modelService.getOutAreaElementNum(sceneId, areaId, dataBO);
            dataBO.getTopology().setOutAreaElementNum(num);
        }
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(dataBO);
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    private List<CsceneRoleBO> convertToRoleBOs(List<CsceneRoleModel> roleObjs, Class<CsceneRoleBO> roleBOClass) {
        List<CsceneRoleBO> roleBOS = new ArrayList<>();
        if (roleObjs == null) {
            return roleBOS;
        }
        Map<String, CsceneRoleBO> roleBOMap = new HashMap<>();
        Map<String, String> roleParentMap = new HashMap<>();
        for (CsceneRoleModel roleObj : roleObjs) {
            CsceneRoleBO roleBO = new CsceneRoleBO();
            roleBO.fromModel(roleObj);
            roleBOMap.put(roleObj.getId(), roleBO);
            roleParentMap.put(roleObj.getId(), roleObj.getParent());
            if (StringUtils.isEmpty(roleObj.getParent())) {
                roleBOS.add(roleBO);
            }
        }
        for (Map.Entry<String, CsceneRoleBO> entry : roleBOMap.entrySet()) {
            String roleId = entry.getKey();
            CsceneRoleBO roleBO = entry.getValue();
            String parentId = roleParentMap.getOrDefault(roleId, null);
            CsceneRoleBO parentRoleBO = roleBOMap.getOrDefault(parentId, null);
            if (parentRoleBO == null) {
                continue;
            }

            if (parentRoleBO.getChildren() == null) {
                parentRoleBO.setChildren(new ArrayList<>());
            }
            parentRoleBO.getChildren().add(roleBO);
        }

        return roleBOS;
    }

    private List<CsceneRoleModel> convertToRoleObjs(List<CsceneRoleBO> roleBOs, String parentId) {
        List<CsceneRoleModel> roleObjs = new ArrayList<>();
        if (roleBOs == null) {
            return roleObjs;
        }

        for (CsceneRoleBO roleBO : roleBOs) {
            var roleObj = roleBO.toModel();
            roleObj.setParent(parentId);
            roleObj.initDefault(BaseModel::getId);
            roleObjs.add(roleObj);

            var children = roleBO.getChildren();
            if (children != null) {
                roleObjs.addAll(convertToRoleObjs(children, roleObj.getId()));
            }
        }
        return roleObjs;
    }

    private void modifyAreaId(SceneConfigDataBO sceneConfigDataBO, String areaId) {
        if (Objects.nonNull(sceneConfigDataBO) && Objects.nonNull(sceneConfigDataBO.getTopology())) {
            for (SceneConfigAreaBO sceneConfigAreaBO : sceneConfigDataBO.getTopology().getAreas()) {
                // 只需要改变模板中的第一层拓扑
                if (StringUtils.isEmpty(sceneConfigAreaBO.getArea())) {
                    sceneConfigAreaBO.setArea(areaId);
                }
            }
            for (SceneConfigElementBO sceneConfigElementBO : sceneConfigDataBO.getTopology().getElements()) {
                if (StringUtils.isEmpty(sceneConfigElementBO.getArea())) {
                    sceneConfigElementBO.setArea(areaId);
                }
            }
        }
    }

    private void removeId(SceneConfigDataBO sceneConfigDataBO) {
        if (Objects.isNull(sceneConfigDataBO)) {
            return;
        }
        if (sceneConfigDataBO.getTopology() != null) {
            var topology = sceneConfigDataBO.getTopology();
            topology.getScene().setEid(null);
            topology.getElements().forEach(sceneElementBO -> {
                sceneElementBO.setEid(null);
                if (CollectionUtils.isEmpty(sceneElementBO.getPorts())) {
                    return;
                }
                sceneElementBO.getPorts().forEach(p -> p.setEid(null));
            });
            topology.getAreas().forEach(sceneAreaBO -> sceneAreaBO.setEid(null));
            topology.getConnections().forEach(sceneElementPortConnectionBO -> sceneElementPortConnectionBO.setEid(null));
            topology.getNodeConnections().forEach(sceneNodeConnectionBO -> sceneNodeConnectionBO.setEid(null));
            topology.getPieces().forEach(scenePieceBO -> scenePieceBO.setEid(null));
        }
        if (sceneConfigDataBO.getPlugins() != null) {
            sceneConfigDataBO.getPlugins().values().forEach(scenePluginDataBO -> {
                if (Objects.nonNull(scenePluginDataBO.getScene())) {
                    scenePluginDataBO.getScene().remove("id");
                }
                if (Objects.nonNull(scenePluginDataBO.getElements()) && CollectionUtils.isNotEmpty(scenePluginDataBO.getElements().values())) {
                    scenePluginDataBO.getElements().values().forEach(map -> map.remove("id"));
                }
                if (Objects.nonNull(scenePluginDataBO.getElementPorts()) && CollectionUtils.isNotEmpty(scenePluginDataBO.getElementPorts().values())) {
                    scenePluginDataBO.getElementPorts().values().forEach(map -> map.remove("id"));
                }
            });
        }
    }

    private void updateOldId(SceneConfigDataBO sceneConfigDataBO) {
        if (Objects.isNull(sceneConfigDataBO)) {
            return;
        }
        // 构建新老id的映射

        if (Objects.nonNull(sceneConfigDataBO.getTopology())) {
            var topology = sceneConfigDataBO.getTopology();
            // 构建area的新老id映射
            Map<String, String> idMap = new HashMap<>();
            idMap.put("","");
            for (SceneConfigAreaBO sceneConfigAreaBO : topology.getAreas()) {
                if (StringUtils.isNotEmpty(sceneConfigAreaBO.getId())) {
                    String nodeId = TextUtils.uuid();
                    idMap.put(sceneConfigAreaBO.getId(), nodeId);
                }
            }

            // 构建element的新老id映射
            for (SceneConfigElementBO sceneConfigElementBO : topology.getElements()) {
                if (StringUtils.isNotEmpty(sceneConfigElementBO.getId())) {
                    String oldNodeId = sceneConfigElementBO.getId();
                    String nodeId = TextUtils.uuid();
                    idMap.put(oldNodeId, nodeId);
                    if (CollectionUtils.isNotEmpty(sceneConfigElementBO.getPorts())) {
                        for (var p : sceneConfigElementBO.getPorts()) {
                            idMap.put(p.getId(), p.getId().replace(oldNodeId, nodeId));
                        }
                    }
                }
            }

            // 替换area为新的id
            for (SceneConfigAreaBO a : topology.getAreas()) {
                a.setId(idMap.get(a.getId()));
                a.setArea(idMap.get(a.getArea()));
            }

            // 替换element为新的id
            for (SceneConfigElementBO e : topology.getElements()) {
                String oldNodeId = e.getId();
                String newNodeId = idMap.get(oldNodeId);
                String oldAreaId = e.getArea();
                String newAreaId = idMap.get(oldAreaId);
                e.setId(newNodeId);
                e.setArea(newAreaId);
                if (e.getOuterPosition2() != null && !e.getOuterPosition2().isEmpty()) {
                    Map<String, String> newOuterPosition2 = new HashMap<>();
                    for (Map.Entry<String, String> entry : e.getOuterPosition2().entrySet()) {
                        String area = entry.getKey();
                        String position2 = entry.getValue();
                        newOuterPosition2.put(idMap.get(area), position2);
                    }
                    e.setOuterPosition2(newOuterPosition2);
                }
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (var p : e.getPorts()) {
                        p.setId(idMap.get(p.getId()));
                    }
                }
            }

            Map<String, String> connectionIdMap = new HashMap<>();
            for (SceneConfigElementPortConnectionBO c : topology.getConnections()) {
                String nodeId = TextUtils.uuid();
                connectionIdMap.put(c.getId(), nodeId);
                c.setId(nodeId);
                c.setSource(idMap.get(c.getSource()));
                c.setTarget(idMap.get(c.getTarget()));
            }

            Map<String,String> nodeConnectionIdMap = new HashMap<>();
            for (SceneConfigAreaNodeConnectionBO nc : topology.getNodeConnections()) {
                String nodeId = TextUtils.uuid();
                nodeConnectionIdMap.put(nc.getId(), nodeId);
                nc.setId(nodeId);

                String sourceNodeId = parseNodeId(nc.getSourceNodeId());
                String newSourceNodeId = idMap.get(sourceNodeId);
                String targetNodeId = parseNodeId(nc.getTargetNodeId());
                String newTargetNodeId = idMap.get(targetNodeId);

                nc.setSource(nc.getSource().replace(sourceNodeId, newSourceNodeId));
                nc.setTarget(nc.getTarget().replace(targetNodeId, newTargetNodeId));
            }

            if (sceneConfigDataBO.getPlugins() != null) {
                for (ScenePluginDataBO scenePluginDataBO : sceneConfigDataBO.getPlugins().values()) {
                    if (Objects.nonNull(scenePluginDataBO.getElements()) && !scenePluginDataBO.getElements()
                            .isEmpty()) {
                        var elementMap = scenePluginDataBO.getElements();
                        for (Map.Entry<String, String> entry : idMap.entrySet()) {
                            String oldId = entry.getKey();
                            String newId = entry.getValue();
                            var value = elementMap.remove(oldId);
                            if (value != null) {
                                elementMap.put(newId, value);
                            }
                        }
                    }
                    if (Objects.nonNull(scenePluginDataBO.getElementPorts()) && !scenePluginDataBO.getElementPorts()
                            .isEmpty()) {
                        var portMap = scenePluginDataBO.getElementPorts();
                        for (Map.Entry<String, String> entry : idMap.entrySet()) {
                            String oldId = entry.getKey();
                            String newId = entry.getValue();
                            var value = portMap.remove(oldId);
                            if (value != null) {
                                portMap.put(newId, value);
                            }
                        }
                    }
                }
            }
        }
    }

    private String parseNodeId(String input){
        String regex = "port_(.+)_layer_[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String extracted = matcher.group(1);
            return extracted;
        }
        return input;
    }

    private void mergeSceneConfig(SceneConfigDataBO originSceneConfig, SceneConfigDataBO extraSceneConfig) {
        if (Objects.nonNull(originSceneConfig) && Objects.nonNull(extraSceneConfig)) {
            if (Objects.nonNull(originSceneConfig.getTopology()) && Objects.nonNull(extraSceneConfig.getTopology())) {
                originSceneConfig.getTopology().getElements().addAll(extraSceneConfig.getTopology().getElements());
                originSceneConfig.getTopology().getAreas().addAll(extraSceneConfig.getTopology().getAreas());
                originSceneConfig.getTopology().getConnections().addAll(extraSceneConfig.getTopology().getConnections());
                originSceneConfig.getTopology().getNodeConnections().addAll(extraSceneConfig.getTopology().getNodeConnections());
            }
            if (Objects.nonNull(originSceneConfig.getPlugins()) && Objects.nonNull(extraSceneConfig.getPlugins())) {
                extraSceneConfig.getPlugins().forEach(((s, scenePluginDataBO) -> {
                    if (originSceneConfig.getPlugins().containsKey(s)) {
                        originSceneConfig.getPlugins().get(s).getElements().putAll(scenePluginDataBO.getElements());
                        originSceneConfig.getPlugins().get(s).getElementPorts().putAll(scenePluginDataBO.getElementPorts());
                    } else {
                        originSceneConfig.getPlugins().put(s, scenePluginDataBO);
                    }
                }));
            }
        }
    }

    private SceneAreaTreeBO.Area getTreeArea(SceneConfigAreaBO sceneConfigAreaBO, Map<String, List<SceneConfigAreaBO>> parentId2Children) {
        SceneAreaTreeBO.Area area = new SceneAreaTreeBO.Area();
        area.setId(sceneConfigAreaBO.getId());
        area.setName(sceneConfigAreaBO.getName());
        area.setDescription(sceneConfigAreaBO.getDescription());
        area.setDisplayContext(sceneConfigAreaBO.getDisplayContext());
        processChildren(area.getId(), area, parentId2Children);
        return area;
    }

    private void processChildren(String parentId, SceneAreaTreeBO.Area area, Map<String, List<SceneConfigAreaBO>> parentId2Children) {
        if (CollectionUtils.isNotEmpty(parentId2Children.get(parentId))) {
            parentId2Children.get(parentId).forEach(sceneConfigAreaBO -> {
                if (sceneConfigAreaBO.getType() == BaseSceneAreaModel.Type.LAYER) {
                    SceneAreaTreeBO.Area child = new SceneAreaTreeBO.Area();
                    child.setLabel(parentId);
                    child.setId(sceneConfigAreaBO.getId());
                    child.setName(sceneConfigAreaBO.getName());
                    child.setDescription(sceneConfigAreaBO.getDescription());
                    child.setDisplayContext(sceneConfigAreaBO.getDisplayContext());
                    processChildren(child.getId(), child, parentId2Children);
                    area.getChildren().add(child);
                } else {
                    processChildren(sceneConfigAreaBO.getId(), area, parentId2Children);
                }
            });
        }
    }
}
