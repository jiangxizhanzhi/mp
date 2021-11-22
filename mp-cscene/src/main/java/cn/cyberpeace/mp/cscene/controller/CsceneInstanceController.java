package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceDeleteRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceQueryParams;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @DeleteMapping(params = "id")
    public ServiceResult<Boolean> delete(@RequestParam String id,
                                         @RequestBody(required = false) CsceneContextBO context) {
        return modelService.deleteById(id, true, context).asResult();
    }

    @ApiOperation("批量删除场景实例")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody CsceneInstanceDeleteRequest deleteRequest) {
        return modelService.bulkDeleteByIds(deleteRequest.getIds(), true, deleteRequest.getContext()).asResult();
    }

    @ApiOperation("清理场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "/clear", params = "id")
    public ServiceResult<Boolean> clear(@RequestParam String id,
                                        @RequestBody(required = false) CsceneContextBO context) {
        return modelService.clearById(id, context).asResult();
    }

    @ApiOperation("批量清理场景实例")
    @DeleteMapping("/bulk_clear")
    public ServiceResult<Integer> bulkClear(@RequestBody CsceneInstanceDeleteRequest deleteRequest) {
        return modelService.bulkClearByIds(deleteRequest.getIds(), deleteRequest.getContext()).asResult();
    }

    @ApiOperation("场景实例启动节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/start_flow", params = { "id", "nodeId", "flowId" })
    public ServiceResult<Boolean> startFlow(@RequestParam String id, @RequestParam String nodeId,
                                            @RequestParam String flowId,
                                            @RequestBody(required = false) CsceneContextBO context) {
        return modelService.startFlow(id, nodeId, flowId, context).asResult();
    }

    @ApiOperation("场景实例停止节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/stop_flow", params = { "id", "nodeId", "flowId" })
    public ServiceResult<Boolean> stopFlow(@RequestParam String id, @RequestParam String nodeId,
                                           @RequestParam String flowId,
                                           @RequestBody(required = false) CsceneContextBO context) {
        return modelService.stopFlow(id, nodeId, flowId, context).asResult();
    }

    @ApiOperation("场景实例启动节点探针")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/start_probe", params = { "id", "nodeId", "probeId" })
    public ServiceResult<Probe> startProbe(@RequestParam String id, @RequestParam String nodeId,
                                           @RequestParam String probeId,
                                           @RequestBody(required = false) CsceneContextBO context) {
        return modelService.startProbe(id, nodeId, probeId, context).asResult();
    }

    @ApiOperation("场景实例停止节点探针")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "probeId", value = "探针id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/stop_probe", params = { "id", "nodeId", "probeId" })
    public ServiceResult<Probe> stopProbe(@RequestParam String id, @RequestParam String nodeId,
                                          @RequestParam String probeId,
                                          @RequestBody(required = false) CsceneContextBO context) {
        return modelService.stopProbe(id, nodeId, probeId, context).asResult();
    }

    @ApiOperation("场景实例获取会话列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "场景实例id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeIds", value = "节点id", allowMultiple = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/node_sessions", params = "ids")
    public ServiceResult<List<NodeSessionBO>> getNodeSessions(@RequestParam List<String> ids,
                                                              @RequestParam(required = false) List<String> nodeIds) {
        return modelService.getNodeSessions(ids, nodeIds).asResult();
    }

    @ApiOperation("场景实例获取分页录屏列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "场景实例id", allowMultiple = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeIds", value = "场景节点id", allowMultiple = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_recordings", params = "ids")
    public ListObjServiceResult<NodeRecordingBO> listRecordings(@RequestParam List<String> ids,
                                                                @RequestParam(required = false) List<String> nodeIds,
                                                                RecordingQueryParams queryParams) {
        handleQueryParams("listRecordings", queryParams);
        return modelService.listRecordings(ids, nodeIds, queryParams).asResult();
    }

    @ApiOperation("场景实例获取分页流量列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/list_flows", params = "id")
    public ListObjServiceResult<CollectionFlowBO> listFlows(@RequestParam String id, FlowQueryParams queryParams) {
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
    public ServiceResult<Boolean> parseScene(@RequestParam String id, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.pause(id, context).asResult();
    }

    @ApiOperation("(从暂停)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpauseScene(@RequestParam String id, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.unpause(id, context).asResult();
    }

    @ApiOperation("挂起场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/suspend")
    public ServiceResult<Boolean> suspendScene(@RequestParam String id, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.suspend(id, context).asResult();
    }

    @ApiOperation("(从挂起)恢复场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    })
    @PostMapping("/resume")
    public ServiceResult<Boolean> resumeScene(@RequestParam String id, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.resume(id, context).asResult();
    }

    @ApiOperation("重启节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/restart_element")
    public ServiceResult<Boolean> restartElement(@RequestParam String id, @RequestParam("node_id") String nodeId, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.restartElement(id, nodeId, context).asResult();
    }

    @ApiOperation("暂停节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/pause_element")
    public ServiceResult<Boolean> pauseElement(@RequestParam String id, @RequestParam("node_id") String nodeId, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.pauseElement(id, nodeId, context).asResult();
    }

    @ApiOperation("从暂停状态恢复节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/unpause_element")
    public ServiceResult<Boolean> unpauseElement(@RequestParam String id, @RequestParam("node_id") String nodeId, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.unpauseElement(id, nodeId, context).asResult();
    }

    @ApiOperation("重建节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/recreate_element")
    public ServiceResult<Boolean> recreateElement(@RequestParam String id, @RequestParam("node_id") String nodeId, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.recreateElement(id, nodeId, context).asResult();
    }

    @ApiOperation("挂起节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/suspend_element")
    public ServiceResult<Boolean> suspendElement(@RequestParam String id, @RequestParam("node_id") String nodeId, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.suspendElement(id, nodeId, context).asResult();
    }


    @ApiOperation("从挂起状态恢复节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "node_id", value = "节点id", dataTypeClass = String.class)
    })
    @PostMapping("/resume_element")
    public ServiceResult<Boolean> resumeElement(@RequestParam String id, @RequestParam("node_id") String nodeId, @RequestBody(required = false) CsceneContextBO context) {
        return modelService.resumeElement(id, nodeId, context).asResult();
    }
}
