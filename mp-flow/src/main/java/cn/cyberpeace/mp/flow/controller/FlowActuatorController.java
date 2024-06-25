package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.*;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.request.FlowActuatorRequest;
import cn.cyberpeace.mp.flow.request.ListSceneRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowActuatorQueryParams;
import cn.cyberpeace.mp.flow.response.*;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 流量发生器管理
 */
@Slf4j
@Api(tags = "流量发生器接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/actuator")
@AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST, TargetEnum.TRAFFIC_SCHEME_LIST})
public class FlowActuatorController extends BaseModelController<FlowActuatorModel, FlowActuatorQueryParams, FlowActuatorService>{

    @Resource(name = "flowActuatorServiceImpl")
    private FlowActuatorService flowActuatorService;

    /**
     * 模糊搜索场景名称
     */
    @AuthTarget({TargetEnum.STATIC_TRAFFIC_LIST, TargetEnum.FLOW_GROUP})
    @ApiOperation("模糊搜索场景信息")
    @PostMapping("/list_scene")
    public ListObjServiceResult<SceneInstanceResponse> listSceneByFuzzy(@RequestBody ListSceneRequest request) {
        // 默认查询20条信息上线
        int limit = 20;
        if (null != request && null != request.getLimit() && request.getLimit() > 0) {
            limit = request.getLimit();
        }
        return flowActuatorService.listSceneByFuzzy(null == request? null : request.getName(), limit);
    }

    /**
     * 分页查询流量发生器信息
     */
    @AuthTarget({TargetEnum.STATIC_TRAFFIC_LIST, TargetEnum.FLOW_GROUP})
    @ApiOperation("分页查询流量信息")
    @GetMapping("/page")
    public ListObjServiceResult<FlowActuatorResponse> page(FlowActuatorQueryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            // 默认按时间降序
            queryParams.setOrders(new String[]{"-createTime", "-title"});
        }
        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        return flowActuatorService.page(queryParams);
    }

    /**
     * 启动
     *
     * @param request 流量发生器id列表
     */
    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
    @ApiOperation("启动流量发生器")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.START, title = "启动流量发生器", target = OpTargetEnums.FLOW_ACTUATOR)
    @PostMapping("/batch_start")
    public ServiceResult batchStart(@RequestBody FlowActuatorRequest request) {
        // 参数校验
        if (Objects.isNull(request.getIds())) {
            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
        }
        // 启动流量发生器
        flowActuatorService.batchStart(request.getIds());
        return ResultCommon.success();
    }

    /**
     * 暂停
     *
     * @param request 流量发生器id列表
     */
    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
    @ApiOperation("暂停流量发生器")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.STOP, title = "暂停流量发生器", target = OpTargetEnums.FLOW_ACTUATOR)
    @PostMapping("/batch_pause")
    public ServiceResult batchPause(@RequestBody FlowActuatorRequest request) {
        // 参数校验
        if (Objects.isNull(request.getIds())) {
            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
        }
        // 暂停流量发生器
        flowActuatorService.batchPause(request.getIds());
        return ResultCommon.success();
    }

    /**
     * 重启
     *
     * @param request 流量发生器id列表
     */
    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
    @ApiOperation("重启流量发生器")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.REBOOT, title = "重启流量发生器", target = OpTargetEnums.FLOW_ACTUATOR)
    @PostMapping("/batch_reboot")
    public ServiceResult batchReboot(@RequestBody FlowActuatorRequest request) {
        // 参数校验
        if (Objects.isNull(request.getIds())) {
            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
        }
        // 重启流量发生器
        flowActuatorService.batchReboot(request.getIds());
        return ResultCommon.success();
    }
}
