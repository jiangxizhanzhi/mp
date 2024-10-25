 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceController.java.
 // File size 74938.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller;

 import cn.cyberpeace.mp.agent.netconf.model.NetConfAccessInfo;
 import cn.cyberpeace.mp.agent.netconf.service.NetconfTaskServiceImpl;
 import cn.cyberpeace.mp.agent.snmp.model.Command;
 import cn.cyberpeace.mp.agent.snmp.model.CommandConfig;
 import cn.cyberpeace.mp.agent.snmp.model.SnmpAccessInfo;
 import cn.cyberpeace.mp.agent.snmp.service.SnmpTaskServiceImpl;
 import cn.cyberpeace.mp.base.annotation.AuthTarget;
 import cn.cyberpeace.mp.base.common.BeanCommon;
 import cn.cyberpeace.mp.base.common.JsonCommon;
 import cn.cyberpeace.mp.base.constant.BaseErrorCode;
 import cn.cyberpeace.mp.base.constant.TargetEnum;
 import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
 import cn.cyberpeace.mp.base.controller.common.ResultCommon;
 import cn.cyberpeace.mp.base.exception.MpRuntimeException;
 import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
 import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
 import cn.cyberpeace.mp.base.service.result.ServiceResult;
 import cn.cyberpeace.mp.base.util.HttpUtils;
 import cn.cyberpeace.mp.base.util.ReentrantLockUtils;
 import cn.cyberpeace.mp.common.bo.OptionBO;
 import cn.cyberpeace.mp.common.service.OptionService;
 import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
 import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
 import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
 import cn.cyberpeace.mp.cscene.bo.CsceneInstanceStat;
 import cn.cyberpeace.mp.cscene.bo.CsceneUpdateBO;
 import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
 import cn.cyberpeace.mp.cscene.bo.ElementModifyBO;
 import cn.cyberpeace.mp.cscene.bo.SceneAreaTreeBO;
 import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
 import cn.cyberpeace.mp.cscene.constant.CsceneConstant;
 import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
 import cn.cyberpeace.mp.cscene.controller.request.AddNodeNetwork;
 import cn.cyberpeace.mp.cscene.controller.request.ConnectSceneNode;
 import cn.cyberpeace.mp.cscene.controller.request.ConnectSceneNodeResult;
 import cn.cyberpeace.mp.cscene.controller.request.CreateNode;
 import cn.cyberpeace.mp.cscene.controller.request.CreateNodePort;
 import cn.cyberpeace.mp.cscene.controller.request.CreateNodePortAccessMode;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceFlowMirrorRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeAccessModeActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeCommandActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeConnectionActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeDelSnapshotRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeFlowActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeFlowRecordsRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeFlowsActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeProbeActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeSnapshotRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneInstancesActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.CsceneStrategyRequest;
 import cn.cyberpeace.mp.cscene.controller.request.DeleteNode;
 import cn.cyberpeace.mp.cscene.controller.request.DeleteNodeCommand;
 import cn.cyberpeace.mp.cscene.controller.request.DeleteNodeNetwork;
 import cn.cyberpeace.mp.cscene.controller.request.DisConnectSceneNode;
 import cn.cyberpeace.mp.cscene.controller.request.ElementSnapshotActionRequest;
 import cn.cyberpeace.mp.cscene.controller.request.QueryNode;
 import cn.cyberpeace.mp.cscene.controller.request.UpdateNodeCommand;
 import cn.cyberpeace.mp.cscene.dto.scene.ElementCommandExecResult;
 import cn.cyberpeace.mp.cscene.dto.scene.SceneLinkStatusInfo;
 import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
 import cn.cyberpeace.mp.cscene.response.CommonCollectRes;
 import cn.cyberpeace.mp.cscene.response.SoftVersionInfo;
 import cn.cyberpeace.mp.cscene.service.CsceneCommonCollectService;
 import cn.cyberpeace.mp.cscene.service.CsceneFileService;
 import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
 import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
 import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceQueryParams;
 import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
 import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
 import cn.cyberpeace.mp.cscene.util.CsceneLogUtils;
 import cn.cyberpeace.mp.log.enums.OpTypeEnums;
 import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
 import cn.cyberpeace.mp.scene.core.bo.SceneAreaBO;
 import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
 import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
 import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
 import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
 import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
 import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
 import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
 import cn.cyberpeace.mp.sceneplugincloud.bo.CloudElementAccessMode;
 import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
 import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
 import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
 import cn.cyberpeace.mp.sceneplugincloud.model.CsceneElementSoftVersionModel;
 import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
 import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
 import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetConnectionUrlParam;
 import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
 import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
 import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
 import cn.cyberpeace.mp.sceneplugincloud.repository.CsceneElementSoftVersionMapper;
 import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneConnectionCommon;
 import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
 import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
 import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventTypeBO;
 import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
 import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
 import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
 import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
 import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
 import cn.cyberpeace.mp.sceneplugincollection.service.param.QueryCollectionDataCountParams;
 import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
 import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
 import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
 import io.swagger.annotations.Api;
 import io.swagger.annotations.ApiImplicitParam;
 import io.swagger.annotations.ApiImplicitParams;
 import io.swagger.annotations.ApiOperation;
 import io.swagger.annotations.ApiParam;
 import lombok.Data;
 import lombok.Getter;
 import lombok.Setter;
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
 import org.springframework.web.bind.annotation.ResponseBody;
 import org.springframework.web.bind.annotation.RestController;
 import org.springframework.web.multipart.MultipartFile;

 import javax.annotation.Resource;
 import javax.servlet.http.HttpServletResponse;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

/**
 * @ClassName CsceneInstanceController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelListControllerBase
 */
@Api(value = "场景实例", tags = "CSCENE-场景实例")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene_instances")
@Data
@AuthTarget(TargetEnum.NO_AUTH)
public class CsceneInstanceController implements BaseModelListControllerBase {

    private CsceneInstanceService modelService;

    private CsceneSpecialService csceneSpecialService;

    private CsceneFileService csceneFileService;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @Autowired
    private CsceneCommonCollectService commonCollectService;

    /**
     * formatActionRequest
     *
     * @param request
     * @param paramId
     * @return
     */
    private CsceneInstanceActionRequest formatActionRequest(CsceneInstanceActionRequest request, String paramId) {
        if (request == null) {
            request = new CsceneInstanceActionRequest();
        }
        if (!StringUtils.isEmpty(paramId)) {
            request.setId(paramId);
        }

        CsceneContextBO context = request.getContext();
        return request;
    }

    /**
     * formatNodeActionRequest
     *
     * @param request
     * @param paramId
     * @param paramNodeId
     * @return
     */
    private CsceneInstanceNodeActionRequest formatNodeActionRequest(CsceneInstanceNodeActionRequest request, String paramId,
                                                                    String paramNodeId) {
        if (request == null) {
            request = new CsceneInstanceNodeActionRequest();
        }
        request = (CsceneInstanceNodeActionRequest) formatActionRequest(request, paramId);
        if (!StringUtils.isEmpty(paramNodeId)) {
            request.setNodeId(paramNodeId);
        }
        return request;
    }

    /**
     * formatNodeFlowActionRequest
     *
     * @param request
     * @param paramId
     * @param paramNodeId
     * @param paramFlowId
     * @param sourceIp
     * @return
     */
    private CsceneInstanceNodeFlowActionRequest formatNodeFlowActionRequest(
            CsceneInstanceNodeFlowActionRequest request, String paramId, String paramNodeId, String paramFlowId, String sourceIp) {
        if (request == null) {
            request = new CsceneInstanceNodeFlowActionRequest();
        }
        request = (CsceneInstanceNodeFlowActionRequest) formatNodeActionRequest(request, paramId, paramNodeId);
        if (!StringUtils.isEmpty(paramFlowId)) {
            request.setFlowId(paramFlowId);
        }
        if (!StringUtils.isEmpty(sourceIp)) {
            request.setSourceIp(sourceIp);
        }
        return request;
    }

    /**
     * formatNodeProbeActionRequest
     *
     * @param request
     * @param paramId
     * @param paramNodeId
     * @param paramProbeId
     * @return
     */
    private CsceneInstanceNodeProbeActionRequest formatNodeProbeActionRequest(
            CsceneInstanceNodeProbeActionRequest request, String paramId, String paramNodeId, String paramProbeId) {
        if (request == null) {
            request = new CsceneInstanceNodeProbeActionRequest();
        }
        request = (CsceneInstanceNodeProbeActionRequest) formatNodeActionRequest(request, paramId, paramNodeId);
        if (!StringUtils.isEmpty(paramProbeId)) {
            request.setProbeId(paramProbeId);
        }
        return request;
    }

    /**
     * formatNodeConnectionActionRequest
     *
     * @param request
     * @param paramId
     * @param paramNodeId
     * @param paramConnectionId
     * @return
     */
    private CsceneInstanceNodeConnectionActionRequest formatNodeConnectionActionRequest(
            CsceneInstanceNodeConnectionActionRequest request, String paramId, String paramNodeId,
            String paramConnectionId) {
        if (request == null) {
            request = new CsceneInstanceNodeConnectionActionRequest();
        }
        request = (CsceneInstanceNodeConnectionActionRequest) formatNodeActionRequest(request, paramId, paramNodeId);
        if (!StringUtils.isEmpty(paramConnectionId)) {
            request.setConnectionId(paramConnectionId);
        }
        return request;
    }

    /**
     * formatNodeAccessModeActionRequest
     *
     * @param request
     * @param paramId
     * @param paramNodeId
     * @param paramAccessModeId
     * @return
     */
    private CsceneInstanceNodeAccessModeActionRequest formatNodeAccessModeActionRequest(
            CsceneInstanceNodeAccessModeActionRequest request, String paramId, String paramNodeId,
            String paramAccessModeId) {
        if (request == null) {
            request = new CsceneInstanceNodeAccessModeActionRequest();
        }
        request = (CsceneInstanceNodeAccessModeActionRequest) formatNodeActionRequest(request, paramId, paramNodeId);
        if (!StringUtils.isEmpty(paramAccessModeId)) {
            request.setAccessModeId(paramAccessModeId);
        }
        if (StringUtils.isBlank(request.getId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format("id"));
        }
        return request;
    }

    /**
     * CsceneInstanceController
     *
     * @param csceneInstanceService
     * @param csceneSpecialService
     * @param csceneFileService
     */
    @Autowired
    public CsceneInstanceController(CsceneInstanceService csceneInstanceService,
                                    CsceneSpecialService csceneSpecialService,
                                    CsceneFileService csceneFileService
    ) {
        setModelService(csceneInstanceService);
        setCsceneSpecialService(csceneSpecialService);
        setCsceneFileService(csceneFileService);
    }

    /**
     * list
     *
     * @param queryParams
     * @return
     */
    @AuthTarget(TargetEnum.CSCENE_INSTANCE_GROUP)
    @ApiOperation("获取分页数据")
    @GetMapping
    public ListObjServiceResult<CsceneInstanceBO> list(CsceneInstanceQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().listCsceneInstanceBO(queryParams, new CsceneContextBO(queryParams.getAccessUserId(), null)).asResult();
    }

    /**
     * listStat
     *
     * @param queryParams
     * @return
     */
    @AuthTarget({TargetEnum.FRONT_ALL, TargetEnum.INSTANCE_MONITORING})
    @ApiOperation("获取分页的聚合数据")
    @GetMapping("/list_stat")
    public ListObjServiceResult<CsceneInstanceStat> listStat(CsceneInstanceStatQueryParams queryParams) {
        handleQueryParams("listStat", queryParams);
        return csceneSpecialService.listCsceneInstanceStats(queryParams).asResult();
    }

    /**
     * get
     *
     * @param id
     * @param accessUserId
     * @param areaId
     * @return
     */
    @AuthTarget(TargetEnum.CSCENE_INSTANCE_GROUP)
    @ApiOperation("获取指定id场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "accessUserId", value = "场景访问用户id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneInstanceBO> get(@RequestParam String id,
                                               @RequestParam(required = false) String accessUserId,@RequestParam(required = false,name = "area_id") String areaId) {
        if (areaId == null) {
            areaId = "";
        }
        return modelService.getById(id, areaId, new CsceneContextBO(accessUserId, null)).asResult();
    }

    /**
     * getSceneConfigAreaTree
     *
     * @param id
     * @return
     */
    @ApiOperation("查询场景拓扑区域树形结构")
    @ApiImplicitParam(name = "id", value = "场景拓扑id", dataTypeClass = String.class)
    @GetMapping("/tree")
    public ServiceResult<SceneAreaTreeBO> getSceneConfigAreaTree(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return new ServiceResult<>(new SceneAreaTreeBO()).asResult();
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getScene();
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        SceneAreaTreeBO sceneAreaTree = new SceneAreaTreeBO();
        SceneDataBO result = sceneHandler.toData();
        if (result != null && result.getTopology() != null && CollectionUtils.isNotEmpty(result.getTopology().getAreas())) {
            var areas = result.getTopology().getAreas();
            Map<String, List<SceneAreaBO>> parentId2Children = new HashMap<>();
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

    /**
     * getTreeArea
     *
     * @param sceneConfigAreaBO
     * @param parentId2Children
     * @return
     */
    private SceneAreaTreeBO.Area getTreeArea(SceneAreaBO sceneConfigAreaBO, Map<String, List<SceneAreaBO>> parentId2Children) {
        SceneAreaTreeBO.Area area = new SceneAreaTreeBO.Area();
        area.setId(sceneConfigAreaBO.getId());
        area.setName(sceneConfigAreaBO.getName());
        area.setDescription(sceneConfigAreaBO.getDescription());
        area.setDisplayContext(sceneConfigAreaBO.getDisplayContext());
        processChildren(area.getId(), area, parentId2Children);
        return area;
    }

    /**
     * processChildren
     *
     * @param parentId
     * @param area
     * @param parentId2Children
     */
    private void processChildren(String parentId, SceneAreaTreeBO.Area area, Map<String, List<SceneAreaBO>> parentId2Children) {
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

    /**
     * getByArea
     *
     * @param id
     * @param areaId
     * @return
     */
    @ApiOperation("查询场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "area_id", value = "场景实例区域id", dataTypeClass = String.class)
    })
    @GetMapping("get_by_area")
    public ServiceResult<CsceneInstanceBO> getByArea(@RequestParam String id, @RequestParam(required = false,name = "area_id") String areaId) {
        if (areaId == null) {
            areaId = StringUtils.EMPTY;
        }
        String sceneId = this.modelService.get(id).getSource().getObj().getScene();
        var csceneBO = getByAreaId(sceneId, areaId);
        return new ServiceResult<>(csceneBO).asResult();
    }

    /**
     * getByAreaId
     *
     * @param sceneId
     * @param areaId
     * @return
     */
    private CsceneInstanceBO getByAreaId(String sceneId, String areaId) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, areaId, true, null);
        SceneDataBO dataBO = sceneHandler.toData();
        CsceneInstanceBO csceneBO = new CsceneInstanceBO();
        csceneBO.setScene(dataBO);
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    /**
     * delete
     *
     * @param id
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.CSCENE_INSTANCE_GROUP)
    @ApiOperation("删除场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @DeleteMapping
    public ServiceResult<Boolean> delete(@RequestParam(required = false) String id,
                                         @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        ServiceResult<Boolean> result = modelService.deleteById(request.getId(), true, request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.DELETE_SCENE, request.getContext());
        return result;
    }

    /**
     * bulkDelete
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.INSTANCE_MONITORING)
    @ApiOperation("批量删除场景实例")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstancesActionRequest.class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody CsceneInstancesActionRequest request) {
        ServiceResult<Integer> result = modelService.bulkDeleteByIds(request.getIds(), true, request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(Arrays.asList(request.getIds()), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_DELETE_SCENES, request.getContext());
        return result;
    }

    /**
     * clear
     *
     * @param id
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.INSTANCE_MONITORING)
    @ApiOperation("清理场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @DeleteMapping(value = "/clear")
    public ServiceResult<Boolean> clear(@RequestParam(required = false) String id,
                                        @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.CLEAR_SCENE, request.getContext());
        ServiceResult<Boolean> result = modelService.clearById(request.getId(), request.getContext()).asResult();
        return result;
    }

    /**
     * bulkClear
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.INSTANCE_MONITORING)
    @ApiOperation("批量清理场景实例")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstancesActionRequest.class)
    @DeleteMapping("/bulk_clear")
    public ServiceResult<Integer> bulkClear(@RequestBody CsceneInstancesActionRequest request) {
        CsceneLogUtils.recordSceneInstanceLog(Arrays.asList(request.getIds()), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_CLEAR_SCENE, request.getContext());
        ServiceResult<Integer> result = modelService.bulkClearByIds(request.getIds(), request.getContext()).asResult();
        return result;
    }

    /**
     * startFlow
     *
     * @param id
     * @param nodeId
     * @param flowId
     * @param sourceIp
     * @param request
     * @return
     */
    @ApiOperation("场景实例启动节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "sourceIp", value = "源ip", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowActionRequest.class),
    })
    @PostMapping(value = "/start_flow")
    public ServiceResult<Boolean> startFlow(@RequestParam(required = false) String id,
                                            @RequestParam(required = false) String nodeId,
                                            @RequestParam(required = false) String flowId,
                                            @RequestParam(required = false) String sourceIp,
                                            @RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        request = formatNodeFlowActionRequest(request, id, nodeId, flowId, sourceIp);
        ServiceResult<Boolean> result = modelService.startFlow(request.getId(), request.getNodeId(), request.getFlowId(), request.getSourceIp(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.START, CsceneLogUtils.START_FLOW, request.getContext());
        return result;
    }

    /**
     * stopFlow
     *
     * @param id
     * @param nodeId
     * @param flowId
     * @param request
     * @return
     */
    @ApiOperation("场景实例停止节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowActionRequest.class),
    })
    @PostMapping(value = "/stop_flow")
    public ServiceResult<Boolean> stopFlow(@RequestParam(required = false) String id,
                                           @RequestParam(required = false) String nodeId,
                                           @RequestParam(required = false) String flowId,
                                           @RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        request = formatNodeFlowActionRequest(request, id, nodeId, flowId, null);
        ServiceResult<Boolean> result = modelService.stopFlow(request.getId(), request.getNodeId(), request.getFlowId(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.STOP, CsceneLogUtils.STOP_FLOW, request.getContext());
        return result;
    }

    /**
     * updateFlowParams
     *
     * @param request
     * @return
     */
    @ApiOperation("场景更新流量任务参数")
    @ApiImplicitParam(name = "request", value = "更新数据请求体", dataTypeClass = CsceneInstanceNodeFlowActionRequest.class)
    @PostMapping(value = "/update_flow_params")
    public ServiceResult<Boolean> updateFlowParams(@RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        if (request == null || request.getId() == null || request.getNodeId() == null || request.getFlowId() == null) {
            return ResultCommon.fail(BaseErrorCode.PARAMS_INVALID);
        }
        ServiceResult<Boolean> result = modelService.updateFlowParams(request.getId(), request.getNodeId(),
                request.getFlowId(), request.getSourceIp(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_FLOW, request.getContext());
        return result;
    }

    /**
     * startFlows
     *
     * @param request
     * @return
     */
    @ApiOperation("场景实例批量启动节点流量")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowsActionRequest.class)
    @PostMapping(value = "/start_flows")
    public ServiceResult<Boolean> startFlows(@RequestBody CsceneInstanceNodeFlowsActionRequest request) {
        ServiceResult<Boolean> result = modelService.startFlows(request.getId(), request.getNodeId(), request.getFlows(),
                request.getContext(), request.getRunType()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), null, OpTypeEnums.START, CsceneLogUtils.BATCH_START_FLOW, request.getContext());
        return result;
    }

    /**
     * stopFlows
     *
     * @param request
     * @return
     */
    @ApiOperation("场景实例批量停止节点流量")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowsActionRequest.class)
    @PostMapping(value = "/stop_flows")
    public ServiceResult<Boolean> stopFlows(@RequestBody CsceneInstanceNodeFlowsActionRequest request) {
        ServiceResult<Boolean> result = modelService.stopFlows(request.getId(), request.getNodeId(), request.getFlows(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), null, OpTypeEnums.STOP, CsceneLogUtils.BATCH_STOP_FLOW, request.getContext());
        return result;
    }

    /**
     * queryFlowRecords
     *
     * @param request
     * @return
     */
    @AuthTarget({TargetEnum.GROUP_TRAINING, TargetEnum.RANGE_TEST, TargetEnum.RANGE_TEST_SELF, TargetEnum.RED_VS_BLUE})
    @ApiOperation("场景实例查询节点流量执行记录")
    @ApiImplicitParam(name = "request", value = "查询数据请求体", dataTypeClass = CsceneInstanceNodeFlowRecordsRequest.class)
    @PostMapping(value = "/query_flow_records")
    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(@RequestBody CsceneInstanceNodeFlowRecordsRequest request) {
        return modelService.queryFlowRecords(request.getId(), request.getNodeId(), request.getFlowId(),
                request.getContext(), request.getIsBatch()).asResult();
    }

    /**
     * startProbe
     *
     * @param id
     * @param nodeId
     * @param probeId
     * @param request
     * @return
     */
    @ApiOperation("场景实例启动节点探针")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeProbeActionRequest.class),
    })
    @PostMapping(value = "/start_probe")
    public ServiceResult<Probe> startProbe(@RequestParam(required = false) String id,
                                           @RequestParam(required = false) String nodeId,
                                           @RequestParam(required = false) String probeId,
                                           @RequestBody(required = false) CsceneInstanceNodeProbeActionRequest request) {
        request = formatNodeProbeActionRequest(request, id, nodeId, probeId);
        return modelService.startProbe(request.getId(), request.getNodeId(), request.getProbeId(),
                request.getContext()).asResult();
    }

    /**
     * stopProbe
     *
     * @param id
     * @param nodeId
     * @param probeId
     * @param request
     * @return
     */
    @ApiOperation("场景实例停止节点探针")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeProbeActionRequest.class),
    })
    @PostMapping(value = "/stop_probe")
    public ServiceResult<Probe> stopProbe(@RequestParam(required = false) String id,
                                          @RequestParam(required = false) String nodeId,
                                          @RequestParam(required = false) String probeId,
                                          @RequestBody(required = false) CsceneInstanceNodeProbeActionRequest request) {
        request = formatNodeProbeActionRequest(request, id, nodeId, probeId);
        return modelService.stopProbe(request.getId(), request.getNodeId(), request.getProbeId(),
                request.getContext()).asResult();
    }

    /**
     * getConnectionUrl
     *
     * @param csceneId
     * @param nodeId
     * @param protocol
     * @param response
     * @return
     */
    @ApiOperation("场景实例获取节点接入链接(vm-no_vnc container-console)")
    @GetMapping("/get_connection_url")
    public ServiceResult<String> getConnectionUrl(@RequestParam String csceneId, @RequestParam String nodeId,
                                                  @RequestParam String protocol, HttpServletResponse response) {
        return modelService.getConnectionUrl(csceneId, nodeId, protocol, response).asResult();
    }

    /**
     * getRemoteConnection
     *
     * @param id
     * @param nodeId
     * @param accessModeId
     * @param request
     * @param response
     * @return
     */
    @AuthTarget(TargetEnum.CSCENE_INSTANCE_CONNECT_GROUP)
    @ApiOperation("场景实例获取远程连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "accessModeId", value = "接入方式id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeAccessModeActionRequest.class),
    })
    @PostMapping(value = "/get_connection")
    public ServiceResult<NodeConnectionBO> getRemoteConnection(@RequestParam(required = false) String id,
                                                               @RequestParam(required = false) String nodeId,
                                                               @RequestParam(required = false) String accessModeId,
                                                               @RequestBody(required = false) CsceneInstanceNodeAccessModeActionRequest request,
                                                               HttpServletResponse response) {
        request = formatNodeAccessModeActionRequest(request, id, nodeId, accessModeId);
        id = request.getId();
        nodeId = request.getNodeId();
        accessModeId = request.getAccessModeId();
        String sceneId = getModelService().getSceneId(id);
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var element = pluginHandler.getElement(nodeId).getSrcElement();

        // 需要使用 Guacamole 生成链接的协议
        Set<String> guacamoleProtocol = new HashSet<>();
        guacamoleProtocol.add(CloudElementAccessMode.Protocol.SSH);
        guacamoleProtocol.add(CloudElementAccessMode.Protocol.RDP);
        guacamoleProtocol.add(CloudElementAccessMode.Protocol.VNC);
        guacamoleProtocol.add(CloudElementAccessMode.Protocol.COM);
        guacamoleProtocol.add(CloudElementAccessMode.Protocol.COM.toUpperCase());

        if (element != null && CollectionUtils.isNotEmpty(element.getPorts())) {
            for (var p : element.getPorts()) {
                var pluginPort = pluginHandler.getPort(p.getId());
                if (pluginPort != null && CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                    for (var a : pluginPort.getAccessModes()) {
                        if (StringUtils.equals(a.getId(), accessModeId)) {
                            if (guacamoleProtocol.contains(a.getProtocol())) {
                                return modelService.getRemoteConnection(request.getId(), request.getNodeId(), request.getAccessModeId(), request.getContext())
                                        .asResult();
                            } else {
                                NodeConnectionBO nodeConnectionBO = new NodeConnectionBO();
                                nodeConnectionBO.setConnectionUrl(a.getConnectionUrl());
                                nodeConnectionBO.setNodeName(element.getName());
                                nodeConnectionBO.setNodeId(nodeId);
                                nodeConnectionBO.setProtocol(a.getProtocol());
                                GetConnectionUrlParam param = new GetConnectionUrlParam();
                                param.setVmId(pluginHandler.getElement(element.getId()).getResourceId());
                                param.setConsoleType(a.getProtocol());
                                String url = CloudSceneConnectionCommon.getConnectionUrl(pluginHandler.getElement(element.getId()).getImageType(),param, response).getData();
                                nodeConnectionBO.setConnectionUrl(url);
                                return new ServiceResult<>(nodeConnectionBO).asResult();
                            }
                        }
                    }
                }
            }
        }
        return new ServiceResult<>(new NodeConnectionBO()).asResult();
    }

    /**
     * getRemoteTtyConnection
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("获取容器console连接,仅支持非neutron网络的容器")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping(value = "/get_tty_connection")
    public ServiceResult<NodeConnectionBO> getRemoteTtyConnection(@RequestParam(required = false) String id,
                                                                  @RequestParam(required = false) String nodeId,
                                                                  @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        return modelService.getRemoteTtyConnection(request.getId(), request.getNodeId()).asResult();
    }

    /**
     * enableRecording
     *
     * @param id
     * @param nodeId
     * @param connectionId
     * @param request
     * @return
     */
    @ApiOperation("场景实例连接开启录屏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "connectionId", value = "连接id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeConnectionActionRequest.class),
    })
    @PostMapping(value = "/enable_recording")
    public ServiceResult<NodeConnectionBO> enableRecording(@RequestParam(required = false) String id,
                                                           @RequestParam(required = false) String nodeId,
                                                           @RequestParam(required = false) String connectionId,
                                                           @RequestBody(required = false) CsceneInstanceNodeConnectionActionRequest request) {
        request = formatNodeConnectionActionRequest(request, id, nodeId, connectionId);
        return modelService.enableRecording(request.getId(), request.getNodeId(), request.getConnectionId(),
                request.getContext()).asResult();
    }

    /**
     * disableRecording
     *
     * @param id
     * @param nodeId
     * @param connectionId
     * @param request
     * @return
     */
    @ApiOperation("场景实例连接关闭录屏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "connectionId", value = "连接id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeConnectionActionRequest.class),
    })
    @PostMapping(value = "/disable_recording")
    public ServiceResult<NodeConnectionBO> disableRecording(@RequestParam(required = false) String id,
                                                            @RequestParam(required = false) String nodeId,
                                                            @RequestParam(required = false) String connectionId,
                                                            @RequestBody(required = false) CsceneInstanceNodeConnectionActionRequest request) {
        request = formatNodeConnectionActionRequest(request, id, nodeId, connectionId);
        return modelService.disableRecording(request.getId(), request.getNodeId(), request.getConnectionId(),
                request.getContext()).asResult();
    }

    /**
     * getNodeSessions
     *
     * @param ids
     * @param nodeIds
     * @param userIds
     * @param type
     * @return
     */
    @ApiOperation("场景实例获取会话列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "场景实例id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeIds", value = "节点id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "userIds", value = "用户id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "type", value = "类型", dataTypeClass = String.class),
    })
    @GetMapping(value = "/node_sessions", params = "ids")
    public ServiceResult<List<UserNodeSessionInfo>> getNodeSessions(@RequestParam List<String> ids,
                                                                    @RequestParam(required = false) List<String> nodeIds,
                                                                    @RequestParam(required = false) List<String> userIds,
                                                                    @RequestParam(required = false) String type) {
        return modelService.getNodeSessions(ids, nodeIds, userIds, type).asResult();
    }

    /**
     * listRecordings
     *
     * @param ids
     * @param nodeIds
     * @param userIds
     * @param queryParams
     * @return
     */
    @AuthTarget({TargetEnum.GROUP_TRAINING, TargetEnum.RANGE_TEST})
    @ApiOperation("场景实例获取分页录屏列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "场景实例id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeIds", value = "场景节点id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "userIds", value = "用户id", allowMultiple = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_recordings", params = "ids")
    public ListObjServiceResult<NodeRecordingBO> listRecordings(@RequestParam List<String> ids,
                                                                @RequestParam(required = false) List<String> nodeIds,
                                                                @RequestParam(required = false) List<String> userIds,
                                                                RecordingQueryParams queryParams) {
        handleQueryParams("listRecordings", queryParams);
        return modelService.listRecordings(ids, nodeIds, userIds, queryParams).asResult();
    }

    /**
     * listFlows
     *
     * @param id
     * @param queryParams
     * @return
     */
    @ApiOperation("场景实例获取分页流量列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_flows", params = "id")
    public ListObjServiceResult<CsceneCollectionFlowBO> listFlows(@RequestParam String id, FlowQueryParams queryParams) {
        handleQueryParams("listFlows", queryParams);
        return modelService.listFlows(id, queryParams).asResult();
    }

    /**
     * listHostStates
     *
     * @param id
     * @param queryParams
     * @return
     */
    @ApiOperation("场景实例获取分页主机采集数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_host_states", params = "id")
    public ListObjServiceResult<CollectionHostStateBO> listHostStates(@RequestParam String id,
                                                                      HostStateQueryParams queryParams) {
        handleQueryParams("listHostStates", queryParams);
        return modelService.listHostStates(id, queryParams).asResult();
    }

    /**
     * listHostEvents
     *
     * @param id
     * @param queryParams
     * @return
     */
    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("查询场景实例事件列表（分页列表）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_host_events", params = "id")
    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(@RequestParam String id,
                                                                      HostEventQueryParams queryParams) {
        handleQueryParams("listHostEvents", queryParams);
        return modelService.listHostEvents(id, queryParams).asResult();
    }

    /**
     * queryCollectionDataCount
     *
     * @param params
     * @return
     */
    @AuthTarget({TargetEnum.GROUP_TRAINING, TargetEnum.RANGE_TEST_SELF, TargetEnum.RED_VS_BLUE})
    @ApiOperation("查询场景实例复盘时各类别采集数据的总数")
    @GetMapping(value = "/query_data_count")
    public ServiceResult<Map<String, Integer>> queryCollectionDataCount(QueryCollectionDataCountParams params) {
        return getModelService().queryCollectionDataCount(params).asResult();
    }

    /**
     * listHostEvents
     *
     * @return
     */
    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("查询场景实例事件类型")
    @GetMapping(value = "/list_host_events_type")
    public ServiceResult<List<CollectionHostEventTypeBO>> listHostEvents() {
        List<CollectionHostEventTypeBO> result = new ArrayList<>();
        result.add(new CollectionHostEventTypeBO(FileEventType.PROCESS.name(), 1));
        result.add(new CollectionHostEventTypeBO(FileEventType.PORT.name(), 2));
        result.add(new CollectionHostEventTypeBO(FileEventType.FILE.name(), 4));
        result.add(new CollectionHostEventTypeBO(FileEventType.USERS.name(), 5));
        result.add(new CollectionHostEventTypeBO(FileEventType.REGISTRY.name(), 6));
        return new ServiceResult<>(result).asResult();
    }


    /**
     * scrollHostEvents
     *
     * @param id
     * @param scrollId
     * @param queryParams
     * @return
     */
    @ApiOperation("查询场景实例事件列表（滚动分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "scrollId", value = "scrollId", dataTypeClass = String.class),
    })
    @GetMapping(value = "/scroll_host_events")
    public ListObjServiceResult<CollectionHostEventBO> scrollHostEvents(@RequestParam String id,
                                                                        @RequestParam String scrollId,
                                                                        HostEventQueryParams queryParams) {
        log.info("scrollId:{}", scrollId);
        return modelService.scrollHostEvents(id, scrollId, queryParams).asResult();
    }

    /**
     * getProbeCollectionData
     *
     * @param id
     * @param requestId
     * @param probeId
     * @param queryParams
     * @return
     */
    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("场景实例获取探针采集数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "requestId", value = "请求参数", dataTypeClass = String.class),
    })
    @GetMapping(value = "/get_probe_collection_data", params = "id")
    public ListObjServiceResult<CollectionHostEventBO> getProbeCollectionData(@RequestParam String id,
                                                                              @RequestParam(required = false) String requestId,
                                                                              @RequestParam(required = false) String probeId,
                                                                              HostEventQueryParams queryParams) {
        handleQueryParams("getProbeCollectionData", queryParams);
        return modelService.getProbeCollectionData(requestId, id, probeId, queryParams, null).asResult();
    }

    /**
     * statDeviceReferCsceneInstances
     *
     * @param images
     * @return
     */
    @ApiOperation("统计靶标对应引用该靶标的场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "images", value = "靶标名称", dataTypeClass = String[].class, allowMultiple = true),
    })
    @GetMapping("/stat_device_refer_cscene_instances")
    public ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> statDeviceReferCsceneInstances(
            @RequestParam String[] images) {
        return csceneSpecialService.statDeviceReferCsceneInstances(images).asResult();
    }

    /**
     * instanceModify
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.RED_BLUE)
    @ApiOperation("更新场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "更新数据请求体", dataTypeClass = CsceneUpdateBO.class)
    })
    @PostMapping(value = "/modify")
    public ServiceResult<CsceneInstanceBO> instanceModify(@RequestBody(required = true) CsceneUpdateBO request) {
        log.info("modify_instance params,request:{}", JsonCommon.snakeStringify(request));
        CsceneDataModifier.removeFilePathPrefix(request);
        ServiceResult<CsceneInstanceBO> result = modelService.instanceModify(request).asResult();
        return result;
    }

    /**
     * pauseScene
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation("暂停场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/pause")
    public ServiceResult<Boolean> pauseScene(@RequestParam(required = false) String id,
                                             @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        ServiceResult<Boolean> result = modelService.pause(request.getId(), request.getContext());
        return result.asResult();
    }

    /**
     * unpauseScene
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation("(从暂停)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpauseScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        if (ReentrantLockUtils.tryLock("pause" + id)) {
            try {
                request = formatActionRequest(request, id);
                ServiceResult<Boolean> result = modelService.unpause(request.getId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_UNPAUSE, request.getContext());
                return result;
            } finally {
                ReentrantLockUtils.unLock("pause" + id);
            }
        }else {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        }
    }

    /**
     * suspendScene
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation("挂起场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/suspend")
    public ServiceResult<Boolean> suspendScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        ServiceResult<Boolean> result = modelService.suspend(request.getId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_SUSPEND, request.getContext());
        return result;
    }

    /**
     * resumeScene
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation("(从挂起)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/resume")
    public ServiceResult<Boolean> resumeScene(@RequestParam(required = false) String id,
                                              @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        ServiceResult<Boolean> result = modelService.resume(request.getId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_RESUME, request.getContext());
        return result;
    }

    /**
     * restartElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("重启节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/restart_element")
    public ServiceResult<Boolean> restartElement(@RequestParam(required = false) String id,
                                                 @RequestParam(value = "node_id", required = false) String nodeId,
                                                 @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        modelService.unSupportedOperation(request.getId(), request.getNodeId());
        ServiceResult<Boolean> result = modelService.restartElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_RESTART, request.getContext());
        return result;
    }

    /**
     * startElement
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.RED_VS_BLUE_WEB_FOLDER)
    @ApiOperation("节点开机")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/start_element")
    public ServiceResult<Boolean> startElement(@RequestBody(required = true) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request,request.getId(),request.getNodeId());
        modelService.unSupportedOperation(request.getId(), request.getNodeId());
        ServiceResult<Boolean> result = modelService.startElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        return result;
    }

    /**
     * stopElement
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.RED_VS_BLUE_WEB_FOLDER)
    @ApiOperation("节点关机")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/stop_element")
    public ServiceResult<Boolean> stopElement(@RequestBody(required = true) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request,request.getId(),request.getNodeId());
        modelService.unSupportedOperation(request.getId(), request.getNodeId());
        ServiceResult<Boolean> result = modelService.stopElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        return result;
    }

    /**
     * pauseElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("暂停节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/pause_element")
    public ServiceResult<Boolean> pauseElement(@RequestParam(required = false) String id,
                                               @RequestParam(value = "node_id", required = false) String nodeId,
                                               @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        ServiceResult<Boolean> result = modelService.pauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_ELEMENT_PAUSE, request.getContext());
        return result;
    }

    /**
     * unpauseElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("从暂停状态恢复节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/unpause_element")
    public ServiceResult<Boolean> unpauseElement(@RequestParam(required = false) String id,
                                                 @RequestParam(value = "node_id", required = false) String nodeId,
                                                 @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        ServiceResult<Boolean> result = modelService.unpauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_ELEMENT_UNPAUSE, request.getContext());
        return result;
    }

    /**
     * startElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("启动节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/create_element")
    public ServiceResult<Boolean> startElement(@RequestParam(required = false) String id,
                                                  @RequestParam(value = "node_id", required = false) String nodeId,
                                                  @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        ServiceResult<Boolean> result = modelService.createElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_RECREATE, request.getContext());
        return result;
    }

    /**
     * recreateElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("重建节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/recreate_element")
    public ServiceResult<Boolean> recreateElement(@RequestParam(required = false) String id,
                                                  @RequestParam(value = "node_id", required = false) String nodeId,
                                                  @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        modelService.unSupportedOperation(request.getId(), request.getNodeId());
        ServiceResult<Boolean> result =
                modelService.recreateElement(request.getId(), request.getNodeId(), request.getImage(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_RECREATE, request.getContext());
        return result;
    }

    /**
     * recreateElement
     *
     * @param elementModifyBO
     * @return
     */
    @AuthTarget(TargetEnum.RED_BLUE)
    @ApiOperation("更新节点")
    @PostMapping("/modify_element")
    public ServiceResult<ElementModifyBO> recreateElement(@RequestBody ElementModifyBO elementModifyBO) {
        log.info("modify_element params,request:{}", JsonCommon.snakeStringify(elementModifyBO));
        CsceneDataModifier.removeFilePathPrefix(elementModifyBO);
        modelService.modifyElement(elementModifyBO,elementModifyBO.getContext());
        return new ServiceResult<>(new ElementModifyBO()).asResult();
    }

    /**
     * recreateElement
     *
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.RED_VS_BLUE_WEB_FOLDER)
    @ApiOperation("删除节点")
    @PostMapping("/delete_element")
    public ServiceResult<ElementModifyBO> recreateElement(@RequestBody CsceneInstanceNodeActionRequest request) {
        modelService.deleteElement(request.getId(), request.getNodeId(), request.getContext());
        return new ServiceResult<>(new ElementModifyBO()).asResult();
    }

    /**
     * suspendElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("挂起节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/suspend_element")
    public ServiceResult<Boolean> suspendElement(@RequestParam(required = false) String id,
                                                 @RequestParam(value = "node_id", required = false) String nodeId,
                                                 @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        ServiceResult<Boolean> result = modelService.suspendElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_ELEMENT_SUSPEND, request.getContext());
        return result;
    }


    /**
     * resumeElement
     *
     * @param id
     * @param nodeId
     * @param request
     * @return
     */
    @ApiOperation("从挂起状态恢复节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/resume_element")
    public ServiceResult<Boolean> resumeElement(@RequestParam(required = false) String id,
                                                @RequestParam(value = "node_id", required = false) String nodeId,
                                                @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        ServiceResult<Boolean> result = modelService.resumeElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_ELEMENT_RESUME, request.getContext());
        return result;
    }

    /**
     * linkStatus
     *
     * @param id
     * @return
     */
    @AuthTarget(TargetEnum.CSCENE_INSTANCE_CONNECT_GROUP)
    @ApiOperation("场景链路状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @GetMapping("/link_status")
    public ServiceResult<SceneLinkStatusInfo> linkStatus(String id) {
        return modelService.linkStatus(id).asResult();
    }

    /**
     * snapshotElement
     *
     * @param request
     * @return
     */
    @ApiOperation("节点快照")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeSnapshotRequest.class)
    @PostMapping("/snapshot_element")
    public ServiceResult<Boolean> snapshotElement(@RequestBody(required = false) CsceneInstanceNodeSnapshotRequest request) {
        if (StringUtils.isNotEmpty(request.getSnapshotName())) {
            request.setSnapshotName(request.getSnapshotName().trim());
        }
        ServiceResult<Boolean> result = modelService.snapshotElement(request.getId(), request.getNodeId(), request.getSnapshotName(), request.getDescription(), request.getContext()).asResult();
//        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_SNAPSHOT, request.getContext());
        return result;
    }

    /**
     * deleteElementSnapshot
     *
     * @param request
     * @return
     */
    @ApiOperation("删除节点快照")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstanceNodeDelSnapshotRequest.class)
    @PostMapping("/delete_snapshot")
    public ServiceResult<Boolean> deleteElementSnapshot(@RequestBody(required = false) CsceneInstanceNodeDelSnapshotRequest request) {
        return modelService.deleteElementSnapshot(request.getId(), request.getNodeId(), request.getSnapshotId()).asResult();
    }

    /**
     * elementSnapshotAction
     *
     * @param request
     * @return
     */
    @ApiOperation("节点快照操作")
    @PostMapping("/snapshot_action")
    public ServiceResult<Boolean> elementSnapshotAction(@RequestBody ElementSnapshotActionRequest request) {
        return modelService.elementSnapshotAction(request.getId(), request.getNodeId(), request.getSnapshotId(),
                request.getAction(), null).asResult();
    }

    /**
     * execElement
     *
     * @param request
     * @return
     */
    @ApiOperation("节点命令执行")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeCommandActionRequest.class)
    @PostMapping("/exec_element")
    public ServiceResult<ElementCommandExecResult> execElement(@RequestBody CsceneInstanceNodeCommandActionRequest request) {
        return modelService.execElement(request.getId(), request.getNodeId(), request.getCommand(), request.getExecUser(), request.getContext()).asResult();
    }

    /**
     * uploadElementFile
     *
     * @param id
     * @param nodeId
     * @param file
     * @param filePath
     * @param filename
     * @param destPath
     * @return
     */
    @ApiOperation("节点文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class),
            @ApiImplicitParam(name = "file_path", value = "文件路径", dataTypeClass = String.class),
            @ApiImplicitParam(name = "filename", value = "文件名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "dest_path", value = "目标路径", dataTypeClass = String.class),
    })
    @PostMapping("/upload_element_file")
    public ServiceResult<Boolean> uploadElementFile(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "node_id") String nodeId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "file_path", required = false) String filePath,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "dest_path") String destPath) throws IOException {
        if (file != null && !file.isEmpty()) {
            return modelService.uploadElementFile(id, nodeId, file.getInputStream(), filename, destPath, null).asResult();
        } else {
            return modelService.uploadElementFile(id, nodeId, filePath, filename, destPath, null).asResult();
        }
    }

    /**
     * getSceneStatus
     *
     * @return
     */
    @AuthTarget(TargetEnum.INSTANCE_MONITORING)
    @GetMapping("/status")
    @ApiOperation(value = "查询靶场监控的场景实例状态")
    public ServiceResult<List<OptionBO>> getSceneStatus() {
        return optionService.getIntOptions(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_STATUS);
    }

    /**
     * getSceneType
     *
     * @return
     */
    @AuthTarget({TargetEnum.FRONT_ALL, TargetEnum.INSTANCE_MONITORING})
    @GetMapping("/types")
    @ApiOperation(value = "查询靶场监控的场景实例类型")
    public ServiceResult<List<OptionBO>> getSceneType() {
        return optionService.getIntOptions(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_TYPE);
    }

    /**
     * getAgreementList
     *
     * @return
     */
    @GetMapping("/agreement")
    @ApiOperation(value = "查询协议列表")
    public ServiceResult<List<String>> getAgreementList() {
        return new ServiceResult<>(Arrays.asList("syslog", "kafka", "restapi", "webservice")).asResult();
    }

    /**
     * getCommonCollect
     *
     * @param sceneId
     * @param nodeId
     * @param agreement
     * @return
     */
    @GetMapping("/common_collect")
    @ApiOperation(value = "查询通用协议采集")
    public ServiceResult<List<CommonCollectRes>> getCommonCollect(
            @ApiParam(name = "scene_id", value = "场景id", required = true) @RequestParam("scene_id") String sceneId,
            @ApiParam(name = "node_id", value = "节点id") @RequestParam(value = "node_id", required = false) String nodeId,
            @ApiParam(name = "agreement", value = "协议") @RequestParam(value = "agreement", required = false) String agreement
            ) {
        return new ServiceResult<>(commonCollectService.getCollect(sceneId, nodeId, agreement)).asResult();
    }

    @GetMapping("/get_soft_version_info")
    @ApiOperation(value = "查看节点软件版本信息")
    public ServiceResult<SoftVersionInfoData> getSoftVersionInfo(@RequestParam("id")String id, @RequestParam("node_id")String nodeId) {
        List<SoftVersionInfo> result = new ArrayList<>();
        SoftVersionInfoData softVersionInfoData = new SoftVersionInfoData();
        softVersionInfoData.setSofts(result);
        softVersionInfoData.setUpgradeResult(false);
        var mapper = BeanCommon.getBean(CsceneElementSoftVersionMapper.class);
        var defaultModels = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, "default"));
        if (defaultModels.isEmpty()) {
            return new ServiceResult<>(softVersionInfoData).asResult();
        }

        for (CsceneElementSoftVersionModel d : defaultModels) {
            SoftVersionInfo softVersionInfo = new SoftVersionInfo();
            softVersionInfo.setSoft(d.getSoft());
            softVersionInfo.setOriginVersion(d.getVersion());
            softVersionInfo.setNewVersion(d.getVersion());
            result.add(softVersionInfo);
        }

        String sceneId = getModelService().getSceneId(id);
        var elementMapper = BeanCommon.getBean(SceneElementMapper.class);
        var element = elementMapper.first(new HumanCondition().filterEqual(BaseSceneElementModel::getScene, sceneId)
                                                  .filterEqual(BaseSceneElementModel::getNodeId, nodeId));
        var models = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, element.getId()));
        Map<String, String> softVersion = new HashMap<>();
        models.forEach(m -> {
            softVersion.put(m.getSoft(), m.getVersion());
        });

        for (var r : result) {
            r.setNewVersion(softVersion.getOrDefault(r.getSoft(), r.getOriginVersion()));
            r.setUpgradeResult(!StringUtils.equals(r.getNewVersion(), r.getOriginVersion()));
        }
        for (var s : result) {
            if (!StringUtils.equals(s.getOriginVersion(), s.getNewVersion())) {
                softVersionInfoData.setUpgradeResult(true);
            }
        }
        Collections.sort(result, Comparator.comparing(SoftVersionInfo::getSoft));

        // 点击过 `检测软件版本` 按钮之后，需要加个标记
        var flags = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, element.getId() + "_flag"));
        if (CollectionUtils.isEmpty(flags)) {
            CsceneElementSoftVersionModel flag = new CsceneElementSoftVersionModel();
            flag.setSoft("");
            flag.setVersion("");
            flag.setEid(element.getId() + "_flag");
            mapper.save(flag);
        }
        return new ServiceResult<>(softVersionInfoData).asResult();
    }

    @GetMapping("/get_soft_version_info_v2")
    @ApiOperation(value = "查看节点软件版本信息v2")
    public ServiceResult<SoftVersionInfoData> getSoftVersionInfoV2(@RequestParam("id") String id, @RequestParam("node_id") String nodeId) {
        List<SoftVersionInfo> result = new ArrayList<>();
        SoftVersionInfoData softVersionInfoData = new SoftVersionInfoData();
        softVersionInfoData.setSofts(result);
        softVersionInfoData.setUpgradeResult(false);
        var mapper = BeanCommon.getBean(CsceneElementSoftVersionMapper.class);
        var defaultModels = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, "default"));
        if (defaultModels.isEmpty()) {
            return new ServiceResult<>(softVersionInfoData).asResult();
        }

        for (CsceneElementSoftVersionModel d : defaultModels) {
            SoftVersionInfo softVersionInfo = new SoftVersionInfo();
            softVersionInfo.setSoft(d.getSoft());
            softVersionInfo.setOriginVersion(d.getVersion());
            softVersionInfo.setNewVersion("");
            softVersionInfo.setUpgradeResult(null);
            result.add(softVersionInfo);
        }

        String sceneId = getModelService().getSceneId(id);
        var elementMapper = BeanCommon.getBean(SceneElementMapper.class);
        var element = elementMapper.first(new HumanCondition().filterEqual(BaseSceneElementModel::getScene, sceneId)
                                                  .filterEqual(BaseSceneElementModel::getNodeId, nodeId));
        // 查询下是否有点击过 `检测软件版本` 按钮的标记 , 有的话，则需要和机器中的软件版本做对比
        var flags = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, element.getId() + "_flag"));
        if (CollectionUtils.isNotEmpty(flags)) {
            var models = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, element.getId()));
            Map<String, String> softVersion = new HashMap<>();
            models.forEach(m -> {
                softVersion.put(m.getSoft(), m.getVersion());
            });

            for (var r : result) {
                r.setNewVersion(softVersion.getOrDefault(r.getSoft(), r.getOriginVersion()));
                r.setUpgradeResult(!StringUtils.equals(r.getNewVersion(), r.getOriginVersion()));
            }
        }

        Collections.sort(result, Comparator.comparing(SoftVersionInfo::getSoft));
        return new ServiceResult<>(softVersionInfoData).asResult();
    }

    @ApiOperation("创建指定节点")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CreateNode.class)
    @PostMapping("/create_node")
    public ServiceResult<CreateNode> createNode(@RequestBody CreateNode request) {
        VirtualMachine virtualMachine = new VirtualMachine();
        //创建trex虚机
        VirtualMachineCreateData createData = new VirtualMachineCreateData();
        createData.setName(request.getName());
        createData.setTemplateId(request.getImageUuid());
        createData.setCpu(request.getCpu());
        createData.setRam(request.getRam());
        createData.setDisk(request.getDisk());
        createData.setMainCloud(true);
        createData.setOsType(SystemTypeCommandUtils.OS_TYPE_LINUX);

        List<VirtualNic> virtualNics = new ArrayList<>();
        for (var port : request.getPorts()) {
            VirtualNic nic = new VirtualNic();
            nic.setFixedIp(port.getIp());
            nic.setNetworkId(port.getNetworkId());
            virtualNics.add(nic);
        }
        createData.setNetwork(virtualNics);

        VirtualMachineCreateData node = virtualMachine.create(createData);
        getModelService().createSceneBusinessElement(node.getId(), request);
        request.setResourceId(node.getId());
        return new ServiceResult<>(request).asResult();
    }

    @ApiOperation("删除指定节点")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = DeleteNode.class)
    @PostMapping("/delete_node")
    public ServiceResult<Boolean> deleteNode(@RequestBody DeleteNode request) {
        VirtualMachineDeleteData deleteData = new VirtualMachineDeleteData();
        var node = getModelService().getSceneBusinessElement(request.getSceneId(), request.getId());
        deleteData.setVms(Arrays.asList(node.getResourceId()));

        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.delete(deleteData);

        getModelService().deleteSceneBusinessElement(request.getSceneId(), request.getId());
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation(value = "外部节点接入场景", notes = "指定外部节点接入场景内,将外部节点接入到portId指定端口所在的网络")
    @ApiImplicitParam(name = "connectSceneNode", value = "请求体", dataTypeClass = ConnectSceneNode.class)
    @PostMapping("/connect_scene_node")
    public ServiceResult<ConnectSceneNodeResult> connectSceneNode(@RequestBody ConnectSceneNode connectSceneNode) {
        var res = getModelService().connectSceneNode(connectSceneNode);
        return new ServiceResult<>(res).asResult();
    }

    @ApiOperation(value = "外部节点断开场景", notes = "断开外部节点和场景网络的连接")
    @ApiImplicitParam(name = "disConnectSceneNode", value = "请求体", dataTypeClass = DisConnectSceneNode.class)
    @PostMapping("/disconnect_scene_node")
    public ServiceResult<Boolean> disConnectSceneNode(@RequestBody DisConnectSceneNode disConnectSceneNode) {
        getModelService().disConnectSceneNode(disConnectSceneNode);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("更新节点指定功能的初始化命令")
    @ApiImplicitParam(name = "updateNodeCommand", value = "请求体", dataTypeClass = UpdateNodeCommand.class)
    @PostMapping("/update_node_command")
    public ServiceResult<Boolean> updateNodeCommand(@RequestBody UpdateNodeCommand updateNodeCommand) {
        getModelService().updateSceneElementCommand(updateNodeCommand);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("给节点加一个网络")
    @ApiImplicitParam(name = "addNodeNetwork", value = "请求体", dataTypeClass = AddNodeNetwork.class)
    @PostMapping("/add_node_network")
    public ServiceResult<Boolean> addNodeNetwork(@RequestBody AddNodeNetwork addNodeNetwork) {
        getModelService().addNodeNetwork(addNodeNetwork);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("给节点删除一个网络")
    @ApiImplicitParam(name = "deleteNodeNetwork", value = "请求体", dataTypeClass = DeleteNodeNetwork.class)
    @PostMapping("/delete_node_network")
    public ServiceResult<Boolean> deleteNodeNetwork(@RequestBody DeleteNodeNetwork deleteNodeNetwork) {
        getModelService().deleteNodeNetwork(deleteNodeNetwork);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("删除节点指定功能的初始化命令")
    @ApiImplicitParam(name = "deleteNodeCommand", value = "请求体", dataTypeClass = DeleteNodeCommand.class)
    @PostMapping("/delete_node_command")
    public ServiceResult<Boolean> deleteNodeCommand(@RequestBody DeleteNodeCommand deleteNodeCommand) {
        getModelService().deleteSceneElementCommand(deleteNodeCommand);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("查询节点信息")
    @GetMapping("/query_node_info")
    public ServiceResult<CreateNode> queryNodeNetInfo(@RequestBody QueryNode request) {
        // 先在业务创建的节点中查询，如果没有再到场景创建的节点中查询
        var node = getModelService().getSceneBusinessElement(request.getSceneId(), request.getNodeId());
        if (node == null) {
            String id = getModelService().getSceneId(request.getSceneId());
            var sceneHandler = new SceneHandler(id, null, false, new SceneContextBO());
            CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler("cpcloud");
            var element = sceneHandler.getElement(request.getNodeId());
            var cloudElement = pluginHandler.getElement(request.getNodeId());

            CreateNode createNode = new CreateNode();
            createNode.setSceneId(request.getSceneId());
            createNode.setResourceId(cloudElement.getResourceId());
            createNode.setId(element.getId());
            createNode.setName(element.getName());
            createNode.setImage(cloudElement.getImage());
            createNode.setImageType(cloudElement.getImageType());
            createNode.setImageUuid(cloudElement.getImageUuid());
            createNode.setDisk(element.getDisk());
            createNode.setRam(element.getRam());
            createNode.setCpu(element.getCpu());

            List<CreateNodePort> ports = new ArrayList<>();
            for (var port : element.getPorts()) {
                CreateNodePort nodePort = new CreateNodePort();
                nodePort.setIp(port.getIp());
                nodePort.setId(port.getId());
                nodePort.setEid(port.getEid());

                var cloudPort = pluginHandler.getPort(port.getId());
                nodePort.setNetworkId(cloudPort.getAuxiliaryNetworkId());

                List<CreateNodePortAccessMode> accessModes = new ArrayList<>();
                for (var accessMode : cloudPort.getAccessModes()) {
                    CreateNodePortAccessMode nodeAccessMode = new CreateNodePortAccessMode();
                    nodeAccessMode.setId(accessMode.getId());
                    nodeAccessMode.setIp(port.getIp());
                    nodeAccessMode.setPort(accessMode.getPort());
                    nodeAccessMode.setUsername(accessMode.getUsername());
                    nodeAccessMode.setPassword(accessMode.getPassword());
                    accessModes.add(nodeAccessMode);
                }
                nodePort.setAccessMode(accessModes);
                ports.add(nodePort);
            }
            createNode.setPorts(ports);
            node = createNode;
        }
        return new ServiceResult<>(node).asResult();
    }

    @ApiOperation("查询节点流量发生信息")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = QueryNode.class)
    @GetMapping("/query_node_flow_info")
    public ServiceResult<FlowPluginSceneElementBO> queryNodeFlowInfo(@RequestBody QueryNode request) {
        return new ServiceResult<>(new FlowPluginSceneElementBO()).asResult();
    }

    @ApiOperation("创建流量镜像")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceFlowMirrorRequest.class)
    @PostMapping("/create_flow_mirror")
    public ServiceResult<Boolean> createFlowMirror(@RequestBody CsceneInstanceFlowMirrorRequest request) {
        var result = modelService.createFlowMirror(request.getId(), request.getIp());
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("删除流量镜像")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceFlowMirrorRequest.class)
    @PostMapping("/delete_flow_mirror")
    public ServiceResult<Boolean> deleteFlowMirror(@RequestBody CsceneInstanceFlowMirrorRequest request) {
        var result = modelService.deleteFlowMirror(request.getId());
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("安全策略统配")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneStrategyRequest.class)
    @PostMapping("/send_strategy")
    public ServiceResult<SendStrategyResult> sendStrategy(@RequestBody CsceneStrategyRequest request) {
        if (request.getConfigCommand().getProtocol() == 0) {
            SnmpAccessInfo accessInfo = new SnmpAccessInfo();
            accessInfo.setCommunity(request.getConfigCommand().getCommunity());
            accessInfo.setVersion("v2c");
            accessInfo.setPort(request.getConfigCommand().getPort());
            accessInfo.setHostIp(request.getConfigCommand().getIp());

            var config = new CommandConfig();
            var command = new Command();
            command.setParams(new ArrayList<>());
            command.setCommand(request.getConfigCommand().getContent());
            config.setCommands(Arrays.asList(command));

            BeanCommon.getBean(SnmpTaskServiceImpl.class).execTask(accessInfo, config);
        } else if (request.getConfigCommand().getProtocol() == 1) {
            // netconf
            NetConfAccessInfo accessInfo = new NetConfAccessInfo();
            accessInfo.setUsername(request.getConfigCommand().getUser());
            accessInfo.setPassword(request.getConfigCommand().getPassword());
            accessInfo.setPort(request.getConfigCommand().getPort());
            accessInfo.setHostIp(request.getConfigCommand().getIp());

            var conf = new CommandConfig();
            var command = new Command();
            command.setParams(new ArrayList<>());
            command.setCommand(request.getConfigCommand().getContent());
            conf.setCommands(Arrays.asList(command));
            BeanCommon.getBean(NetconfTaskServiceImpl.class).execTask(accessInfo, conf);

        } else if (request.getConfigCommand().getProtocol() == 2  || request.getConfigCommand().getProtocol() == 3) {
            // restconf
            String ip = request.getConfigCommand().getIp();
            String port = request.getConfigCommand().getPort();
            Map<String, Object> content = JsonCommon.parse(request.getConfigCommand().getContent(), Map.class);
            try {
                HttpUtils.sendPatch("http://" + ip + ":" + port + "/restconf/data/car", JsonCommon.stringify(content));
            } catch (IOException e) {
                log.error("restconf连接错误", e);
            }
        }

        var m = this.getModelService().get(request.getId()).getSource().getObj();
        String sceneId = m.getScene();
        SceneHandler handler = new SceneHandler(sceneId, null, null);
        handler.getElement(request.getNodeId())
                .setConfigCommand(JsonCommon.beanToSnakeMap(request.getConfigCommand()));
        handler.save();

        return new ServiceResult<>(new SendStrategyResult()).asResult();
    }

    @Getter
    @Setter
    public static class SendStrategyResult {
        private boolean success;
        private String output;
    }

    @Getter
    @Setter
    private static class SoftVersionInfoData {
        private List<SoftVersionInfo> softs;
        private Boolean upgradeResult;
    }
}
