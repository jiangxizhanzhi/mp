package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.CsceneUpdateBO;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.ElementModifyBO;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneConstant;
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceActionRequest;
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
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodesActionRequest;
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstancesActionRequest;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.scene.ElementCommandExecResult;
import cn.cyberpeace.mp.cscene.dto.scene.SceneLinkStatusInfo;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.service.CsceneFileService;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceQueryParams;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.CsceneLogUtils;
import cn.cyberpeace.mp.cscene.util.ReentrantLockUtils;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventTransition;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventTypeBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.QueryCollectionDataCountParams;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
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

    private CsceneFileService csceneFileService;

    @Resource(name = "sysOptionServiceImpl")
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
                                    CsceneSpecialService csceneSpecialService,
                                    CsceneFileService csceneFileService
    ) {
        setModelService(csceneInstanceService);
        setCsceneSpecialService(csceneSpecialService);
        setCsceneFileService(csceneFileService);
    }

    @ApiOperation("获取分页数据")
    @GetMapping
    public ListObjServiceResult<CsceneInstanceBO> list(CsceneInstanceQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().listCsceneInstanceBO(queryParams, new CsceneContextBO(queryParams.getAccessUserId(), null)).asResult();
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
            @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @DeleteMapping
    public ServiceResult<Boolean> delete(@RequestParam(required = false) String id,
                                         @RequestBody(required = false) CsceneInstanceActionRequest request) {
        this.checkBlackList();
        request = formatActionRequest(request, id);
        var result = modelService.deleteById(request.getId(), true, request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.DELETE_SCENE, request.getContext());
        return result;
    }

    @ApiOperation("批量删除场景实例")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstancesActionRequest.class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody CsceneInstancesActionRequest request) {
        this.checkBlackList();
        var result = modelService.bulkDeleteByIds(request.getIds(), true, request.getContext()).asResult();
        CsceneLogUtils.recordSceneInstanceLog(Arrays.asList(request.getIds()), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_DELETE_SCENES, request.getContext());
        return result;
    }

    @ApiOperation("清理场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @DeleteMapping(value = "/clear")
    public ServiceResult<Boolean> clear(@RequestParam(required = false) String id,
                                        @RequestBody(required = false) CsceneInstanceActionRequest request) {
        this.checkBlackList();
        request = formatActionRequest(request, id);
        CsceneLogUtils.recordSceneInstanceLog(request.getId(), OpTypeEnums.DELETE, CsceneLogUtils.CLEAR_SCENE, request.getContext());
        var result = modelService.clearById(request.getId(), request.getContext()).asResult();
        return result;
    }

    @ApiOperation("批量清理场景实例")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstancesActionRequest.class)
    @DeleteMapping("/bulk_clear")
    public ServiceResult<Integer> bulkClear(@RequestBody CsceneInstancesActionRequest request) {
        this.checkBlackList();
        CsceneLogUtils.recordSceneInstanceLog(Arrays.asList(request.getIds()), OpTypeEnums.DELETE, CsceneLogUtils.BATCH_CLEAR_SCENE, request.getContext());
        var result = modelService.bulkClearByIds(request.getIds(), request.getContext()).asResult();
        return result;
    }

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
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowActionRequest.class),
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
    @ApiImplicitParam(name = "request", value = "更新数据请求体", dataTypeClass = CsceneInstanceNodeFlowActionRequest.class)
    @PostMapping(value = "/update_flow_params")
    public ServiceResult<Boolean> updateFlowParams(@RequestBody(required = false) CsceneInstanceNodeFlowActionRequest request) {
        if (request == null || request.getId() == null || request.getNodeId() == null || request.getFlowId() == null) {
            return ResultCommon.fail(BaseErrorCode.PARAMS_INVALID);
        }
        var result = modelService.updateFlowParams(request.getId(), request.getNodeId(),
                request.getFlowId(), request.getSourceIp(), request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), request.getFlowId(), OpTypeEnums.UPDATE, CsceneLogUtils.UPDATE_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例批量启动节点流量")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowsActionRequest.class)
    @PostMapping(value = "/start_flows")
    public ServiceResult<Boolean> startFlows(@RequestBody CsceneInstanceNodeFlowsActionRequest request) {
        var result = modelService.startFlows(request.getId(), request.getNodeId(), request.getFlows(),
                request.getContext(), request.getRunType()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), null, OpTypeEnums.START, CsceneLogUtils.BATCH_START_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例批量停止节点流量")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeFlowsActionRequest.class)
    @PostMapping(value = "/stop_flows")
    public ServiceResult<Boolean> stopFlows(@RequestBody CsceneInstanceNodeFlowsActionRequest request) {
        var result = modelService.stopFlows(request.getId(), request.getNodeId(), request.getFlows(),
                request.getContext()).asResult();
        CsceneLogUtils.recordSceneFlowLog(request.getId(), request.getNodeId(), null, OpTypeEnums.STOP, CsceneLogUtils.BATCH_STOP_FLOW, request.getContext());
        return result;
    }

    @ApiOperation("场景实例查询节点流量执行记录")
    @ApiImplicitParam(name = "request", value = "查询数据请求体", dataTypeClass = CsceneInstanceNodeFlowRecordsRequest.class)
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
                                                               @RequestBody(required = false) CsceneInstanceNodeAccessModeActionRequest request) {
        request = formatNodeAccessModeActionRequest(request, id, nodeId, accessModeId);
        return modelService.getRemoteConnection(request.getId(), request.getNodeId(), request.getAccessModeId(),
                request.getContext()).asResult();
    }

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

    @ApiOperation("查询场景实例复盘时各类别采集数据的总数")
    @GetMapping(value = "/query_data_count")
    public ServiceResult<Map<String, Integer>> queryCollectionDataCount(QueryCollectionDataCountParams params) {
        return getModelService().queryCollectionDataCount(params).asResult();
    }

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

    @ApiOperation("统计靶标对应引用该靶标的场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "images", value = "靶标名称", dataTypeClass = String[].class, allowMultiple = true),
    })
    @GetMapping("/stat_device_refer_cscene_instances")
    public ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> statDeviceReferCsceneInstances(
            @RequestParam String[] images) {
        return csceneSpecialService.statDeviceReferCsceneInstances(images).asResult();
    }

    @ApiOperation("更新场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "更新数据请求体", dataTypeClass = CsceneUpdateBO.class)
    })
    @PostMapping(value = "/modify")
    public ServiceResult<CsceneInstanceBO> instanceModify(@RequestBody(required = true) CsceneUpdateBO request) {
        log.info("modify_instance params,request:{}", JsonCommon.snakeStringify(request));
        CsceneDataModifier.removeFilePathPrefix(request);
        var result = modelService.instanceModify(request).asResult();
        return result;
    }

    @ApiOperation("暂停场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/pause")
    public ServiceResult<Boolean> pauseScene(@RequestParam(required = false) String id,
                                             @RequestBody(required = false) CsceneInstanceActionRequest request) {
        if (ReentrantLockUtils.tryLock("pause" + id)) {
            try {
                request = formatActionRequest(request, id);
                var result = modelService.pause(request.getId(), request.getContext()).asResult();

                return result;
            } finally {
                ReentrantLockUtils.unLock("pause" + id);
            }
        } else {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        }
    }

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
                var result = modelService.unpause(request.getId(), request.getContext()).asResult();

                return result;
            } finally {
                ReentrantLockUtils.unLock("pause" + id);
            }
        }else {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        }
    }

    @ApiOperation("挂起场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/suspend")
    public ServiceResult<Boolean> suspendScene(@RequestParam(required = false) String id,
                                               @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.suspend(request.getId(), request.getContext()).asResult();

        return result;
    }

    @ApiOperation("(从挂起)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceActionRequest.class),
    })
    @PostMapping("/resume")
    public ServiceResult<Boolean> resumeScene(@RequestParam(required = false) String id,
                                              @RequestBody(required = false) CsceneInstanceActionRequest request) {
        request = formatActionRequest(request, id);
        var result = modelService.resume(request.getId(), request.getContext()).asResult();

        return result;
    }

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
        var result = modelService.restartElement(request.getId(), request.getNodeId(), request.getContext()).asResult();

        return result;
    }

    @ApiOperation("节点开机")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/start_element")
    public ServiceResult<Boolean> startElement(@RequestBody(required = true) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request,request.getId(),request.getNodeId());
        modelService.unSupportedOperation(request.getId(), request.getNodeId());
        var result = modelService.startElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        return result;
    }

    @ApiOperation("节点关机")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeActionRequest.class),
    })
    @PostMapping("/stop_element")
    public ServiceResult<Boolean> stopElement(@RequestBody(required = true) CsceneInstanceNodeActionRequest request) {
        request = formatNodeActionRequest(request,request.getId(),request.getNodeId());
        modelService.unSupportedOperation(request.getId(), request.getNodeId());
        var result = modelService.stopElement(request.getId(), request.getNodeId(), request.getContext()).asResult();
        return result;
    }

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
        var result = modelService.pauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();

        return result;
    }

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
        var result = modelService.unpauseElement(request.getId(), request.getNodeId(), request.getContext()).asResult();

        return result;
    }

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
        var result = modelService.createElement(request.getId(), request.getNodeId(), request.getContext()).asResult();

        return result;
    }

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
        var result =
                modelService.recreateElement(request.getId(), request.getNodeId(), request.getImage(), request.getContext()).asResult();

        return result;
    }

    @ApiOperation("更新节点")
    @PostMapping("/modify_element")
    public ServiceResult<ElementModifyBO> recreateElement(@RequestBody ElementModifyBO elementModifyBO) {
        log.info("modify_element params,request:{}", JsonCommon.snakeStringify(elementModifyBO));
        CsceneDataModifier.removeFilePathPrefix(elementModifyBO);
        modelService.modifyElement(elementModifyBO,elementModifyBO.getContext());
        return new ServiceResult<>(new ElementModifyBO()).asResult();
    }

    @ApiOperation("删除节点")
    @PostMapping("/delete_element")
    public ServiceResult<ElementModifyBO> recreateElement(@RequestBody CsceneInstanceNodeActionRequest request) {
        modelService.deleteElement(request.getId(), request.getNodeId(), request.getContext());
        return new ServiceResult<>(new ElementModifyBO()).asResult();
    }

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
        var result = modelService.suspendElement(request.getId(), request.getNodeId(), request.getContext()).asResult();

        return result;
    }


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
        var result = modelService.resumeElement(request.getId(), request.getNodeId(), request.getContext()).asResult();

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
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeSnapshotRequest.class)
    @PostMapping("/snapshot_element")
    public ServiceResult<Boolean> snapshotElement(@RequestBody(required = false) CsceneInstanceNodeSnapshotRequest request) {
        if (StringUtils.isNotEmpty(request.getSnapshotName())) {
            request.setSnapshotName(request.getSnapshotName().trim());
        }
        var result = modelService.snapshotElement(request.getId(), request.getNodeId(), request.getSnapshotName(), request.getDescription(), request.getContext()).asResult();

        return result;
    }

    @ApiOperation("删除节点快照")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = CsceneInstanceNodeDelSnapshotRequest.class)
    @PostMapping("/delete_snapshot")
    public ServiceResult<Boolean> deleteElementSnapshot(@RequestBody(required = false) CsceneInstanceNodeDelSnapshotRequest request) {
        return modelService.deleteElementSnapshot(request.getId(), request.getNodeId(), request.getSnapshotId()).asResult();
    }

    @ApiOperation("节点命令执行")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = CsceneInstanceNodeCommandActionRequest.class)
    @PostMapping("/exec_element")
    public ServiceResult<ElementCommandExecResult> execElement(@RequestBody CsceneInstanceNodeCommandActionRequest request) {
        return modelService.execElement(request.getId(), request.getNodeId(), request.getCommand(), request.getExecUser(), request.getContext()).asResult();
    }

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

    @GetMapping("/status")
    @ApiOperation(value = "查询靶场监控的场景实例状态")
    public ServiceResult<List<OptionBO>> getSceneStatus() {
        return optionService.getIntOptions(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_STATUS);
    }

    @GetMapping("/types")
    @ApiOperation(value = "查询靶场监控的场景实例类型")
    public ServiceResult<List<OptionBO>> getSceneType() {
        return optionService.getIntOptions(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_TYPE);
    }

    private void checkBlackList() {
        if (BeanCommon.getBean(BlacklistModel.class).isEnabled()) {
            
            var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request.getHeader("clientId") == null) {
                throw new MpRuntimeException(AuthErrorCode.DEMO_ACCESS_ERROR);
            }
        }
    }
}
