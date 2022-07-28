package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneConstant;
import cn.cyberpeace.mp.cscene.controller.request.*;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.scene.ElementCommandExecResult;
import cn.cyberpeace.mp.cscene.dto.scene.SceneLinkStatusInfo;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceQueryParams;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.cscene.util.CsceneLogUtils;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(value = "场景实例", tags = "CSCENE-场景实例")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene_instances")
@Data
public class CsceneInstanceController implements BaseModelListControllerBase {

    private CsceneInstanceService modelService;

    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private OptionService optionService;

    private CsceneInstanceActionRequest formatActionRequest(CsceneInstanceActionRequest request, String paramId) {
        if (request == null) {
            request = new CsceneInstanceActionRequest();
        }
        if (!StringUtils.isEmpty(paramId)) {
            request.setId(paramId);
        }

        var context = request.getContext();
        if (context == null) {
            var user = UserCommon.getCurrentUser();
            if (user != null) {
                context = new CsceneContextBO();
                context.setAccessUserId(user.getId());
                request.setContext(context);
            }
        }

        return request;
    }

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

    private CsceneInstanceNodesActionRequest formatNodesActionRequest(CsceneInstanceNodesActionRequest request,
                                                                      String paramId, List<String> paramNodeIds) {
        if (request == null) {
            request = new CsceneInstanceNodesActionRequest();
        }
        request = (CsceneInstanceNodesActionRequest) formatActionRequest(request, paramId);
        if (!CollectionUtils.isEmpty(paramNodeIds)) {
            request.setNodeIds(paramNodeIds);
        }
        return request;
    }

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
        return request;
    }

    @Autowired
    public CsceneInstanceController(CsceneInstanceService csceneInstanceService,
                                    CsceneSpecialService csceneSpecialService) {
        setModelService(csceneInstanceService);
        setCsceneSpecialService(csceneSpecialService);
    }

    @ApiOperation("获取分页数据")
    @GetMapping
    public ListObjServiceResult<CsceneInstanceBO> list(CsceneInstanceQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().listBO(queryParams, new CsceneContextBO(queryParams.getAccessUserId(), null)).asResult();
    }

    @ApiOperation("获取分页的聚合数据")
    @GetMapping("/list_stat")
    public ListObjServiceResult<CsceneInstanceStat> listStat(CsceneInstanceStatQueryParams queryParams) {
        handleQueryParams("listStat", queryParams);
        return csceneSpecialService.listCsceneInstanceStats(queryParams).asResult();
    }

    @ApiOperation("获取指定id场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "accessUserId", value = "场景访问用户id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneInstanceBO> get(@RequestParam String id,
                                               @RequestParam(required = false) String accessUserId) {
        return modelService.getById(id, new CsceneContextBO(accessUserId, null)).asResult();
    }

    @ApiOperation("删除场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @DeleteMapping
    public ServiceResult<Boolean> delete(@RequestParam(required = false) String id,
                                         @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.deleteById(request.getId(), true, request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.DELETE_SCENE, request.getContext());
        return result;
    }

    @ApiOperation("批量删除场景实例")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody CsceneInstancesActionRequest request) {
        var result = modelService.bulkDeleteByIds(request.getIds(), true, request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(Arrays.asList(request.getIds()), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_DELETE_SCENES, request.getContext());
        return result;
    }

    @ApiOperation("清理场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "/clear")
    public ServiceResult<Boolean> clear(@RequestParam(required = false) String id,
                                        @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.CLEAR_SCENE, request.getContext());
        var result = modelService.clearById(request.getId(), request.getContext()).asResult();
        return result;
    }

    @ApiOperation("批量清理场景实例")
    @DeleteMapping("/bulk_clear")
    public ServiceResult<Integer> bulkClear(@RequestBody CsceneInstancesActionRequest request) {
        CsceneLogUtils.recordSceneInstanceLog(Arrays.asList(request.getIds()), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_CLEAR_SCENE, request.getContext());
        var result = modelService.bulkClearByIds(request.getIds(), request.getContext()).asResult();
        return result;
    }

    @ApiOperation("场景实例启动节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/start_flow")
    public ServiceResult<Boolean> startFlow(@RequestParam(required = false) String id,
                                            @RequestParam(required = false) String nodeId,
                                            @RequestParam(required = false) String flowId,
                                            @RequestParam(required = false) String sourceIp,
                                            @RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        request = formatNodeFlowActionRequest(request, id, nodeId, flowId, sourceIp);
        var result = modelService.startFlow(request.getId(), request.getNodeId(), request.getFlowId(), request.getSourceIp(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.START, CsceneLogUtils.START_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例停止节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/stop_flow")
    public ServiceResult<Boolean> stopFlow(@RequestParam(required = false) String id,
                                           @RequestParam(required = false) String nodeId,
                                           @RequestParam(required = false) String flowId,
                                           @RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        request = formatNodeFlowActionRequest(request, id, nodeId, flowId, null);
        var result = modelService.stopFlow(request.getId(), request.getNodeId(), request.getFlowId(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.STOP, CsceneLogUtils.STOP_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景更新流量任务参数")
    @PostMapping(value = "/update_flow_params")
    public ServiceResult<Boolean> updateFlowParams(@RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        if (request == null || request.getId() == null || request.getNodeId() == null || request.getFlowId() == null) {
            return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID);
        }
        var result = modelService.updateFlowParams(request.getId(), request.getNodeId(),
                request.getFlowId(), request.getSourceIp(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例批量启动节点流量")
    @PostMapping(value = "/start_flows")
    public ServiceResult<Boolean> startFlows(@RequestBody CsceneInstanceNodeFlowsActionRequest request) {
        var result = modelService.startFlows(request.getId(), request.getNodeId(), request.getFlows(),
                request.getContext(), request.getRunType()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), null, OpTypeEnums.START, CsceneLogUtils.BATCH_START_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例批量停止节点流量")
    @PostMapping(value = "/stop_flows")
    public ServiceResult<Boolean> stopFlows(@RequestBody CsceneInstanceNodeFlowsActionRequest request) {
        var result = modelService.stopFlows(request.getId(), request.getNodeId(), request.getFlows(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), null, OpTypeEnums.STOP, CsceneLogUtils.BATCH_STOP_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例查询节点流量执行记录")
    @PostMapping(value = "/query_flow_records")
    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(@RequestBody CsceneInstanceNodeFlowRecordsRequest request) {
        return modelService.queryFlowRecords(request.getId(), request.getNodeId(), request.getFlowId(),
                request.getContext(), request.getIsBatch()).asResult();
    }

    @ApiOperation("场景实例启动节点探针")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
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

    @ApiOperation("场景实例停止节点探针")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
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

    @ApiOperation("场景实例获取远程连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "accessModeId", value = "接入方式id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/get_connection")
    public ServiceResult<NodeConnectionBO> getRemoteConnection(@RequestParam(required = false) String id,
                                                               @RequestParam(required = false) String nodeId,
                                                               @RequestParam(required = false) String accessModeId,
                                                               @RequestBody(required = false) CsceneInstanceNodeAccessModeActionRequest request) {
        request = formatNodeAccessModeActionRequest(request, id, nodeId, accessModeId);
        return modelService.getRemoteConnection(request.getId(), request.getNodeId(), request.getAccessModeId(),
                request.getContext()).asResult();
    }

    @ApiOperation("场景实例连接开启录屏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "connectionId", value = "连接id", dataTypeClass = String.class),
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

    @ApiOperation("场景实例连接关闭录屏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "connectionId", value = "连接id", dataTypeClass = String.class),
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

    @ApiOperation("场景实例获取会话列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "场景实例id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeIds", value = "节点id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "userIds", value = "用户id", allowMultiple = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/node_sessions", params = "ids")
    public ServiceResult<List<UserNodeSessionInfo>> getNodeSessions(@RequestParam List<String> ids,
                                                                    @RequestParam(required = false) List<String> nodeIds,
                                                                    @RequestParam(required = false) List<String> userIds,
                                                                    @RequestParam(required = false) String type) {
        return modelService.getNodeSessions(ids, nodeIds, userIds, type).asResult();
    }

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

    @ApiOperation("场景实例获取分页流量列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_flows", params = "id")
    public ListObjServiceResult<CsceneCollectionFlowBO> listFlows(@RequestParam String id, FlowQueryParams queryParams) {
        handleQueryParams("listFlows", queryParams);
        return modelService.listFlows(id, queryParams).asResult();
    }

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

    @ApiOperation("场景实例获取探针采集数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class)
    })
    @GetMapping(value = "/get_probe_collection_data", params = "id")
    public ListObjServiceResult<CollectionHostEventBO> getProbeCollectionData(@RequestParam String id,
                                                                              @RequestParam(required = false) String requestId,
                                                                              @RequestParam(required = false) String probeId,
                                                                              HostEventQueryParams queryParams) {
        handleQueryParams("getProbeCollectionData", queryParams);
        return modelService.getProbeCollectionData(requestId, id, probeId, queryParams, null).asResult();
    }

    @ApiOperation("统计靶标对应引用该靶标的场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "images", value = "靶标名称", dataTypeClass = String.class, allowMultiple = true),
    })
    @GetMapping("/stat_device_refer_cscene_instances")
    public ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> statDeviceReferCsceneInstances(
            @RequestParam String[] images) {
        return csceneSpecialService.statDeviceReferCsceneInstances(images).asResult();
    }

    @ApiOperation("暂停场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/pause")
    public ServiceResult<Boolean> pauseScene(@RequestParam(required = false) String id,
                                             @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.pause(request.getId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_PAUSE, request.getContext());
        return result;
    }

    @ApiOperation("(从暂停)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpauseScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.unpause(request.getId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_UNPAUSE, request.getContext());
        return result;
    }

    @ApiOperation("挂起场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/suspend")
    public ServiceResult<Boolean> suspendScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.suspend(request.getId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_SUSPEND, request.getContext());
        return result;
    }

    @ApiOperation("(从挂起)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/resume")
    public ServiceResult<Boolean> resumeScene(@RequestParam(required = false) String id,
                                              @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.resume(request.getId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_RESUME, request.getContext());
        return result;
    }

    @ApiOperation("重启节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/restart_element")
    public ServiceResult<Boolean> restartElement(@RequestParam(required = false) String id,
                                                 @RequestParam(value = "node_id", required = false) String nodeId,
                                                 @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        var result = modelService.restartElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_RESTART, request.getContext());
        return result;
    }

    @ApiOperation("暂停节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/pause_element")
    public ServiceResult<Boolean> pauseElement(@RequestParam(required = false) String id,
                                               @RequestParam(value = "node_id", required = false) String nodeId,
                                               @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        var result = modelService.pauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_ELEMENT_PAUSE, request.getContext());
        return result;
    }

    @ApiOperation("从暂停状态恢复节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/unpause_element")
    public ServiceResult<Boolean> unpauseElement(@RequestParam(required = false) String id,
                                                 @RequestParam(value = "node_id", required = false) String nodeId,
                                                 @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        var result = modelService.unpauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_ELEMENT_UNPAUSE, request.getContext());
        return result;
    }

    @ApiOperation("重建节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/recreate_element")
    public ServiceResult<Boolean> recreateElement(@RequestParam(required = false) String id,
                                                  @RequestParam(value = "node_id", required = false) String nodeId,
                                                  @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        var result = modelService.recreateElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_RECREATE, request.getContext());
        return result;
    }

    @ApiOperation("挂起节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/suspend_element")
    public ServiceResult<Boolean> suspendElement(@RequestParam(required = false) String id,
                                                 @RequestParam(value = "node_id", required = false) String nodeId,
                                                 @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        var result = modelService.suspendElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.PAUSE, CsceneLogUtils.SCENE_ELEMENT_SUSPEND, request.getContext());
        return result;
    }


    @ApiOperation("从挂起状态恢复节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/resume_element")
    public ServiceResult<Boolean> resumeElement(@RequestParam(required = false) String id,
                                                @RequestParam(value = "node_id", required = false) String nodeId,
                                                @RequestBody(required = false) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request, id, nodeId);
        var result = modelService.resumeElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.RECOVER, CsceneLogUtils.SCENE_ELEMENT_RESUME, request.getContext());
        return result;
    }

    @ApiOperation("场景链路状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @GetMapping("/link_status")
    public ServiceResult<SceneLinkStatusInfo> linkStatus(String id) {
        return modelService.linkStatus(id).asResult();
    }

    @ApiOperation("节点快照")
    @PostMapping("/snapshot_element")
    public ServiceResult<Boolean> snapshotElement(@RequestBody(required = false) CsceneInstanceNodeSnapshotRequest request) {
        var result = modelService.snapshotElement(request.getId(), request.getNodeId(), request.getSnapshotName(), request.getDescription(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneNodeLog(request.getId(), request.getNodeId(), OpTypeEnums.UPDATE, CsceneLogUtils.SCENE_ELEMENT_SNAPSHOT, request.getContext());
        return result;
    }

    @ApiOperation("删除节点快照")
    @PostMapping("/delete_snapshot")
    public ServiceResult<Boolean> deleteElementSnapshot(@RequestBody(required = false) CsceneInstanceNodeDelSnapshotRequest request) {
        return modelService.deleteElementSnapshot(request.getId(), request.getNodeId(), request.getSnapshotId()).asResult();
    }

    @ApiOperation("节点命令执行")
    @PostMapping("/exec_element")
    public ServiceResult<ElementCommandExecResult> execElement(@RequestBody CsceneInstanceNodeCommandActionRequest request) {
        return modelService.execElement(request.getId(), request.getNodeId(), request.getCommand(), request.getContext()).asResult();
    }

    @ApiOperation("节点文件上传")
    @PostMapping("/upload_element_file")
    public ServiceResult<Boolean> uploadElementFile(@RequestBody CsceneInstanceNodeFileActionRequest request) {
        return modelService.uploadElementFile(request.getId(), request.getNodeId(), request.getFilePath(), request.getFilename(), request.getDestPath(), request.getContext()).asResult();
    }

    @GetMapping("/status")
    @ApiOperation(value = "查询靶场监控的场景实例状态")
    public ServiceResult<OptionBO> getSceneStatus() {
        List<OptionBO> boList = optionService.getOptionsByType(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_STATUS);
        return ServiceResult.success(boList);
    }

    @GetMapping("/types")
    @ApiOperation(value = "查询靶场监控的场景实例类型")
    public ServiceResult<OptionBO> getSceneType() {
        List<OptionBO> boList =
                optionService.getOptionsByType(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_TYPE);
        return ServiceResult.success(boList);
    }
}
