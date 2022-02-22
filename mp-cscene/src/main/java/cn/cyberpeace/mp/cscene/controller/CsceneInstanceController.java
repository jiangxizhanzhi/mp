package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.*;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceQueryParams;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
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
            CsceneInstanceNodeFlowActionRequest request, String paramId, String paramNodeId, String paramFlowId) {
        if (request == null) {
            request = new CsceneInstanceNodeFlowActionRequest();
        }
        request = (CsceneInstanceNodeFlowActionRequest) formatNodeActionRequest(request, paramId, paramNodeId);
        if (!StringUtils.isEmpty(paramFlowId)) {
            request.setFlowId(paramFlowId);
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
        return modelService.deleteById(request.getId(), true, request.getContext()).asResult();
    }

    @ApiOperation("批量删除场景实例")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody CsceneInstancesActionRequest request) {
        return modelService.bulkDeleteByIds(request.getIds(), true, request.getContext()).asResult();
    }

    @ApiOperation("清理场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "/clear")
    public ServiceResult<Boolean> clear(@RequestParam(required = false) String id,
                                        @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        return modelService.clearById(request.getId(), request.getContext()).asResult();
    }

    @ApiOperation("批量清理场景实例")
    @DeleteMapping("/bulk_clear")
    public ServiceResult<Integer> bulkClear(@RequestBody CsceneInstancesActionRequest request) {
        return modelService.bulkClearByIds(request.getIds(), request.getContext()).asResult();
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
                                            @RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        request = formatNodeFlowActionRequest(request, id, nodeId, flowId);
        return modelService.startFlow(request.getId(), request.getNodeId(), request.getFlowId(),
                request.getContext()).asResult();
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
        request = formatNodeFlowActionRequest(request, id, nodeId, flowId);
        return modelService.stopFlow(request.getId(), request.getNodeId(), request.getFlowId(),
                request.getContext()).asResult();
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
                                                                    @RequestParam(required = false) List<String> userIds) {
        return modelService.getNodeSessions(ids, nodeIds, userIds).asResult();
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

    @ApiOperation("场景实例获取分页主机采集事件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_host_events", params = "id")
    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(@RequestParam String id,
                                                                      HostEventQueryParams queryParams) {
        handleQueryParams("listHostEvents", queryParams);
        return modelService.listHostEvents(id, queryParams).asResult();
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
        return modelService.pause(request.getId(), request.getContext()).asResult();
    }

    @ApiOperation("(从暂停)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpauseScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        return modelService.unpause(request.getId(), request.getContext()).asResult();
    }

    @ApiOperation("挂起场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/suspend")
    public ServiceResult<Boolean> suspendScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        return modelService.suspend(request.getId(), request.getContext()).asResult();
    }

    @ApiOperation("(从挂起)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/resume")
    public ServiceResult<Boolean> resumeScene(@RequestParam(required = false) String id,
                                              @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        return modelService.resume(request.getId(), request.getContext()).asResult();
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
        return modelService.restartElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
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
        return modelService.pauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
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
        return modelService.unpauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
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
        return modelService.recreateElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
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
        return modelService.suspendElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
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
        return modelService.resumeElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
    }
}
