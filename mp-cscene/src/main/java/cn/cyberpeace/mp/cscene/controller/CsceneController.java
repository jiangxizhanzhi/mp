package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.*;
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
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(value = "场景模板", tags = "CSCENE-场景模板")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@Data
@AuthTarget({TargetEnum.CT_AD_LIST, TargetEnum.CT_CFS_LIST, TargetEnum.CT_OPERATION_LIST})
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

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("授权场景模板")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneAuthorizeRequest.class)
    @PostMapping("/authorize")
    public ServiceResult<Boolean> authorize(@RequestBody CsceneAuthorizeRequest request) {
        return modelService.csceneAuthorize(request.getTargetIds(), request.getCsceneIds(), request.getAuthType()).asResult();
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("获取指定场景模板授权信息")
    @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class)
    @GetMapping("/get_authorize")
    public ServiceResult<CsceneAuthorizeBO> getAuthorize(@RequestParam String id) {
        return modelService.getCsceneAuthorizeInfo(id).asResult();
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @GetMapping
    public ListObjServiceResult<CsceneBO> list(CsceneQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        ListObjServiceResult<CsceneBO> result = getModelService().listCsceneBO(queryParams, false, null).asResult();
        getModelService().setSharedUser(result.getData().getList(), queryParams);
        return result;
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation("获取指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id) {
        return modelService.getById(id, false, null).asResult();
    }

    @ApiOperation("获取指定id场景模板")
    @GetMapping("/get_by_area")
    public ServiceResult<CsceneBO> getByArea(@RequestParam String id, @RequestParam(required = false,name = "area_id") String areaId) {
        if (areaId == null) {
            areaId = StringUtils.EMPTY;
        }

        String sceneId = this.modelService.get(id).getSource().getObj().getSceneConfig();

        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, areaId, true, null);
        SceneConfigDataBO dataBO = sceneConfigHandler.toData();

        SceneConfigHandler out = new SceneConfigHandler(sceneId, areaId, true, true);
        out.reloadOut();
        SceneConfigDataBO extraSceneConfig = out.toData();

        this.mergeSceneConfig(dataBO, extraSceneConfig);
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(dataBO);
        return new ServiceResult<>(csceneBO).asResult();
    }

    @ApiOperation("查询场景拓扑区域树形结构")
    @ApiImplicitParam(name = "id", value = "场景拓扑id", dataTypeClass = String.class)
    @GetMapping("/tree")
    public ServiceResult<SceneAreaTreeBO> getSceneConfigAreaTree(@RequestParam String id) {
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

    @ApiOperation("导出指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/export", params = "id")
    public ServiceResult<CsceneBO> export(@RequestParam String id) {
        return modelService.export(id, null).asResult();
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
    @ApiOperation(value = "保存（创建/更新）场景模板", notes = "请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/sync")
    public ServiceResult<CsceneBO> sync(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));

        var a = JsonCommon.parse(getSTR(),CsceneBO.class);
        System.out.println(JsonCommon.snakeStringify(a));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        ServiceResult<CsceneBO> result = modelService.sync(csceneBO, null, true).asResult();
        if (csceneBO.getType() != null && csceneBO.getType() == CsceneModel.Type.TEMPLATE) {
            if (isUpdate) {
                CsceneLogUtils.recordSceneConfigLog(csceneBO.getId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_SCENE_CONFIG, csceneBO.getContext());
            } else {
                CsceneLogUtils.recordSceneConfigLog(result.getData(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, csceneBO.getContext());
            }
        }
        return result;
    }

    private String getSTR(){
        return "{\"id\":\"78e2869c39b14fc7a59ab887be600d29\",\"createUser\":null,\"createUsername\":null,\"createName\":null,\"roles\":null,\"name\":null,\"enableRecording\":null,\"supportPartialStart\":false,\"type\":null,\"authType\":null,\"sharedUsers\":null,\"referenceType\":null,\"referenceMetaData\":null,\"logo\":\"/api/mp/file/481/078/603290810c30808bd4724c5b5d4b3e08.png\",\"sceneConfig\":{\"topology\":{\"scene\":{\"eid\":null,\"name\":\"测试层节点\",\"description\":\"\",\"file\":\"\",\"displayContext\":\"{\\\"active\\\":\\\"background\\\",\\\"enable_flow_collect\\\":false,\\\"enable_flow_mirror\\\":false,\\\"dns_nameservers\\\":[],\\\"showLineLabels\\\":false,\\\"backgroundList\\\":[{\\\"key\\\":\\\"background\\\",\\\"value\\\":\\\"\\\"},{\\\"key\\\":\\\"background2\\\",\\\"value\\\":\\\"\\\"},{\\\"key\\\":\\\"background3\\\",\\\"value\\\":\\\"\\\"}],\\\"simpleModeToEdge\\\":true,\\\"graphTranslate\\\":{\\\"tx\\\":0,\\\"ty\\\":0},\\\"graphZoom\\\":1,\\\"showGraphToolbar\\\":true,\\\"showNodeLimitTip\\\":true,\\\"layoutType\\\":\\\"none\\\",\\\"backgroundType\\\":\\\"background-image\\\",\\\"backgroundColor\\\":\\\"var(--bg-color-1)\\\",\\\"showGrid\\\":true}\",\"createTime\":null,\"updateTime\":null},\"areaId\":\"\",\"areas\":[{\"eid\":null,\"id\":\"be4e62e9-d8ca-4115-b26b-b98c5bbd0d5b\",\"area\":null,\"icon2\":null,\"position2\":\"{\\\"origin\\\":{\\\"x\\\":540,\\\"y\\\":140},\\\"width\\\":40,\\\"height\\\":40}\",\"icon3\":null,\"position3\":null,\"name\":\"层节点\",\"description\":null,\"displayContext\":null,\"open\":null,\"type\":1}],\"elements\":[{\"eid\":\"c48045b3b807427a8f325048dc5512fe\",\"id\":\"b320120c-e6ad-47e3-badf-6c18ed064917\",\"area\":null,\"icon2\":\"/api/mp/file/639/162/85ce8f4bcad3948c390f774d2916fee2.png\",\"position2\":\"{\\\"x\\\":380,\\\"y\\\":280}\",\"icon3\":\"\",\"position3\":null,\"name\":\"交换机\",\"description\":null,\"displayContext\":\"{\\\"size\\\":40,\\\"zIndex\\\":2,\\\"text\\\":{\\\"fontSize\\\":12,\\\"fill\\\":\\\"var(--neutral9)\\\",\\\"refY\\\":53,\\\"transform\\\":\\\"rotate(0)\\\",\\\"fontWeight\\\":400,\\\"text\\\":\\\"交换机\\\",\\\"opacity\\\":1,\\\"class\\\":\\\"node-label original\\\"}}\",\"type\":1,\"file\":\"\",\"systemType\":\"other\",\"systemArchitecture\":\"other\",\"systemLanguage\":\"other\",\"cpu\":0,\"ram\":0,\"disk\":0,\"initCommands\":[\"\"],\"maxPortNumber\":0,\"ports\":[{\"eid\":null,\"id\":\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_1\",\"name\":\"E0/1\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null},{\"eid\":null,\"id\":\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_2\",\"name\":\"E0/2\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null},{\"eid\":null,\"id\":\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_3\",\"name\":\"E0/3\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null},{\"eid\":null,\"id\":\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_4\",\"name\":\"E0/4\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null}],\"nodeCommand\":\"\",\"isAuxiliary\":null},{\"eid\":\"e8cd9d41a504456c9fafcffd034b1633\",\"id\":\"f053bc2e-3e37-4dae-a9c6-8922343620ec\",\"area\":null,\"icon2\":\"/api/mp/file/639/162/85ce8f4bcad3948c390f774d2916fee2.png\",\"position2\":\"{\\\"x\\\":360,\\\"y\\\":140}\",\"icon3\":\"\",\"position3\":null,\"name\":\"交换机\",\"description\":null,\"displayContext\":\"{\\\"size\\\":40,\\\"zIndex\\\":2,\\\"text\\\":{\\\"fontSize\\\":12,\\\"fill\\\":\\\"var(--neutral9)\\\",\\\"refY\\\":53,\\\"transform\\\":\\\"rotate(0)\\\",\\\"fontWeight\\\":400,\\\"text\\\":\\\"交换机\\\",\\\"opacity\\\":1,\\\"class\\\":\\\"node-label original\\\"}}\",\"type\":1,\"file\":\"\",\"systemType\":\"other\",\"systemArchitecture\":\"other\",\"systemLanguage\":\"other\",\"cpu\":0,\"ram\":0,\"disk\":0,\"initCommands\":[\"\"],\"maxPortNumber\":0,\"ports\":[{\"eid\":null,\"id\":\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_1\",\"name\":\"E0/1\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null},{\"eid\":null,\"id\":\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_2\",\"name\":\"E0/2\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null},{\"eid\":null,\"id\":\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_3\",\"name\":\"E0/3\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null},{\"eid\":null,\"id\":\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_4\",\"name\":\"E0/4\",\"type\":null,\"macAddr\":null,\"ip\":\"\",\"netmask\":null,\"gateway\":null,\"dhcp\":null,\"dns\":null,\"egress\":-1,\"ingress\":-1,\"delay\":-1,\"loss\":-1,\"accessModes\":[],\"order\":null,\"isAuxiliary\":null}],\"nodeCommand\":\"\",\"isAuxiliary\":null}],\"connections\":[{\"eid\":null,\"id\":\"3d6e1ca5-42cf-4689-8bdb-c2427a004765\",\"source\":\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_1\",\"target\":\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_1\",\"name\":\"{\\\"source\\\":\\\"E0/1\\\",\\\"target\\\":\\\"E0/1\\\"}\",\"unidirectional\":false,\"displayContext\":\"{\\\"line\\\":{\\\"stroke\\\":\\\"var(--neutral7)\\\",\\\"zIndex\\\":1,\\\"strokeWidth\\\":1,\\\"strokeDasharray\\\":0,\\\"animationDirections\\\":\\\"uniDirectional\\\",\\\"opacity\\\":1},\\\"labels\\\":{\\\"body\\\":{\\\"fill\\\":\\\"rgba(255,255,255,1)\\\",\\\"refWidth\\\":\\\"100%\\\",\\\"rx\\\":0,\\\"ry\\\":0,\\\"refHeight\\\":\\\"100%\\\",\\\"refX\\\":\\\"0%\\\",\\\"refY\\\":\\\"0%\\\",\\\"opacity\\\":1},\\\"text\\\":{\\\"fill\\\":\\\"rgba(0,0,0,1)\\\",\\\"fontSize\\\":12,\\\"fontWeight\\\":400,\\\"opacity\\\":1},\\\"positionSource\\\":0.3,\\\"positionTarget\\\":0.7}}\",\"type\":\"straight\",\"vertices\":[]}],\"nodeConnections\":[{\"eid\":null,\"id\":\"35c87424-0b01-446c-a2d2-9dfbe0c6e285\",\"source\":\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_layer_1\",\"target\":\"port_be4e62e9-d8ca-4115-b26b-b98c5bbd0d5b_layer_1\",\"name\":null,\"displayContext\":\"{\\\"line\\\":{\\\"stroke\\\":\\\"var(--neutral7)\\\",\\\"zIndex\\\":1,\\\"strokeWidth\\\":1,\\\"strokeDasharray\\\":\\\"10, 5\\\",\\\"animationDirections\\\":\\\"uniDirectional\\\",\\\"targetMarker\\\":null,\\\"sourceMarker\\\":null,\\\"class\\\":\\\"edge-line original\\\",\\\"style\\\":{\\\"animation\\\":\\\"\\\"},\\\"opacity\\\":1},\\\"labels\\\":{\\\"body\\\":{\\\"fill\\\":\\\"rgba(255,255,255,1)\\\",\\\"refWidth\\\":\\\"100%\\\",\\\"rx\\\":0,\\\"ry\\\":0,\\\"refHeight\\\":\\\"100%\\\",\\\"refX\\\":0,\\\"refY\\\":0,\\\"opacity\\\":1},\\\"text\\\":{\\\"fill\\\":\\\"rgba(0,0,0,1)\\\",\\\"fontSize\\\":12,\\\"fontWeight\\\":400,\\\"opacity\\\":1},\\\"positionSource\\\":0.3,\\\"positionTarget\\\":0.7}}\"}],\"pieces\":[]},\"plugins\":{\"cpcloud\":{\"scene\":{\"dns_nameservers\":[]},\"elements\":{\"b320120c-e6ad-47e3-badf-6c18ed064917\":{\"resource_id\":\"\",\"role\":22,\"image_type\":\"simple\",\"image_uuid\":\"66e6244c-90d1-48d6-a615-0762af79e4f2\",\"is_build_in\":true,\"image\":\"Switch Simple\",\"platform\":\"\",\"electric_port_number\":4,\"electric_port_type\":\"E\",\"optical_port_number\":0,\"optical_port_type\":\"sfp\",\"init_support\":false,\"firewall_rules\":[],\"static_routes\":[],\"category\":3,\"category2\":50,\"category3\":0,\"cidr\":\"192.168.2.0/24\",\"gateway\":\"192.168.2.254\",\"dns_nameservers\":[\"8.8.8.8\",\"114.114.114.114\"],\"vlan_id\":\"\",\"dhcp\":true,\"allocation_pools\":[],\"wan_port_number\":0,\"wan_port_type\":\"E\",\"lan_port_number\":0,\"lan_port_type\":\"E\",\"quick_create_params\":[],\"ospf_router_id\":\"\",\"rip_loopback\":\"\",\"iso_attach_filename\":\"\",\"iso_tmpdir_uuid\":\"\",\"virtual_usbs\":[],\"entity_usbs\":[],\"docker_params\":{\"environment\":{},\"environment_str\":\"\"}},\"f053bc2e-3e37-4dae-a9c6-8922343620ec\":{\"resource_id\":\"\",\"role\":22,\"image_type\":\"simple\",\"image_uuid\":\"66e6244c-90d1-48d6-a615-0762af79e4f2\",\"is_build_in\":true,\"image\":\"Switch Simple\",\"platform\":\"\",\"electric_port_number\":4,\"electric_port_type\":\"E\",\"optical_port_number\":0,\"optical_port_type\":\"sfp\",\"init_support\":false,\"firewall_rules\":[],\"static_routes\":[],\"category\":3,\"category2\":50,\"category3\":0,\"cidr\":\"192.168.1.0/24\",\"gateway\":\"192.168.1.254\",\"dns_nameservers\":[\"8.8.8.8\",\"114.114.114.114\"],\"vlan_id\":\"\",\"dhcp\":true,\"allocation_pools\":[],\"wan_port_number\":0,\"wan_port_type\":\"E\",\"lan_port_number\":0,\"lan_port_type\":\"E\",\"quick_create_params\":[],\"ospf_router_id\":\"\",\"rip_loopback\":\"\",\"iso_attach_filename\":\"\",\"iso_tmpdir_uuid\":\"\",\"virtual_usbs\":[],\"entity_usbs\":[],\"docker_params\":{\"environment\":{},\"environment_str\":\"\"}}},\"elementPorts\":{\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_1\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_2\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_3\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_b320120c-e6ad-47e3-badf-6c18ed064917_electrical_4\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_1\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_2\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_3\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"},\"port_f053bc2e-3e37-4dae-a9c6-8922343620ec_electrical_4\":{\"access_modes\":[],\"firewall_rules\":[],\"resource_id\":\"\"}}}}},\"testScenes\":null,\"appScenes\":null,\"context\":{\"accessUserId\":\"3f655d87112f4ae094bbaf70830e20aa\",\"passThrough\":null},\"resourceOccupiedNums\":null,\"updateTime\":null,\"totalCpu\":null,\"totalRam\":null,\"totalDisk\":null}";
    }

    @ApiOperation(value = "业务服务端保存（创建/更新）场景模板", notes = "不会校验节点数量,请忽略test_scenes和app_scenes字段")
    @ApiImplicitParam(name = "csceneBO", value = "请求体", dataTypeClass = CsceneBO.class)
    @PostMapping("/server_api/sync")
    public ServiceResult<CsceneBO> serverApiSync(@RequestBody CsceneBO csceneBO) {
        log.info("scene sync,params is {}", JsonCommon.stringify(csceneBO));
        boolean isUpdate = csceneBO != null && StringUtils.isNotEmpty(csceneBO.getId());
        correctRequestCsceneParamsForSync(csceneBO);
        ServiceResult<CsceneBO> result = modelService.sync(csceneBO, null, false).asResult();
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
        ServiceResult<CsceneBO> result = modelService.clone(request.getId(), null).asResult();
        CsceneLogUtils.recordSceneConfigLog(request.getId(), OpTypeEnums.CREATE, CsceneLogUtils.CREATE_SCENE_CONFIG, request.getContext());
        return result;
    }

    @AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
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
        ServiceResult<Boolean> result = modelService.deleteById(request.getId(), true, false, null).asResult();
        CsceneLogUtils.recordSceneConfigLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.DELETE_SCENE_CONFIG, request.getContext());
        return result;
    }

    @ApiOperation("批量删除场景模板")
    @ApiImplicitParam(name = "ids", value = "删除数据请求体", dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        log.info("delete cscene configs , cscene ids is {}", JsonCommon.stringify(ids));
        ServiceResult<Integer> result = modelService.bulkDeleteByIds(ids, true, false, null).asResult();
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
        ServiceResult<CsceneInstanceBO> result = modelService.createTestInstance(request.getId(), false, request.toCsceneInstanceCreateContextBO()).asResult();
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
        ServiceResult<CsceneInstanceBO> result = modelService.createAppInstance(request.getId(), false, request.toCsceneInstanceCreateContextBO()).asResult();
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
}
