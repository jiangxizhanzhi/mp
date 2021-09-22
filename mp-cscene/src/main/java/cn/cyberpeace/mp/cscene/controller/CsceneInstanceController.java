package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.NodeSessionBO;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceDeleteRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.param.InstanceQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "场景实例", tags = "CSCENE-场景实例")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene_instances")
@Data
public class CsceneInstanceController implements BaseModelListControllerBase {

    private CsceneInstanceService modelService;

    @Autowired
    public CsceneInstanceController(CsceneInstanceService csceneInstanceService) {
        setModelService(csceneInstanceService);
    }

    @ApiOperation("获取分页数据")
    @GetMapping
    public ListObjServiceResult<CsceneInstanceBO> list(InstanceQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().listBO(queryParams, new CsceneContextBO(queryParams.getAccessUserId(), null));
    }

    @ApiOperation("获取指定id场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "accessUserId", value = "场景访问用户id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneInstanceBO> get(@RequestParam String id, @RequestParam String accessUserId) {
        return modelService.getById(id, new CsceneContextBO(accessUserId, null)).asResult();
    }

    @ApiOperation("删除场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @DeleteMapping(params = "id")
    public ServiceResult<Boolean> delete(@RequestParam String id,
                                         @RequestBody(required = false) CsceneContextBO context) {
        return modelService.deleteById(id, context).asResult();
    }

    @ApiOperation("批量删除场景实例")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody CsceneInstanceDeleteRequest deleteRequest) {
        return modelService.bulkDeleteByIds(deleteRequest.getIds(), deleteRequest.getContext()).asResult();
    }

    @ApiOperation("清理场景实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "/clear", params = "id")
    public ServiceResult<Boolean> clear(@RequestParam String id,
                                        @RequestBody(required = false) CsceneContextBO context) {
        return modelService.deleteById(id, context).asResult();
    }

    @ApiOperation("批量清理场景实例")
    @DeleteMapping("/bulk_clear")
    public ServiceResult<Integer> bulkClear(@RequestBody CsceneInstanceDeleteRequest deleteRequest) {
        return modelService.bulkDeleteByIds(deleteRequest.getIds(), deleteRequest.getContext()).asResult();
    }

    @ApiOperation("场景实例启动节点流量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flowId", value = "流量id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/start_flow", params = {"id", "nodeId", "flowId"})
    public ServiceResult<Flow> startFlow(@RequestParam String id, @RequestParam String nodeId,
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
    @GetMapping(value = "/stop_flow", params = {"id", "nodeId", "flowId"})
    public ServiceResult<Flow> stopFlow(@RequestParam String id, @RequestParam String nodeId,
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
    @GetMapping(value = "/start_probe", params = {"id", "nodeId", "probeId"})
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
    @GetMapping(value = "/stop_probe", params = {"id", "nodeId", "probeId"})
    public ServiceResult<Probe> stopProbe(@RequestParam String id, @RequestParam String nodeId,
                                          @RequestParam String probeId,
                                          @RequestBody(required = false) CsceneContextBO context) {
        return modelService.stopProbe(id, nodeId, probeId, context).asResult();
    }

    @ApiOperation("场景实例获取会话列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/node_sessions", params = "id")
    public ServiceResult<List<NodeSessionBO>> getNodeSession(@RequestParam String id, @RequestParam String nodeId,
                                                             @RequestBody(required = false) CsceneContextBO context) {
        return null;
    }
}
