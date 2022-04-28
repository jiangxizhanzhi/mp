package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.request.AddVmInstanceRequest;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 流量执行控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量执行器接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/actuator")
public class FlowActuatorController extends BaseModelController<FlowActuatorModel, QueryParams, FlowActuatorService> {

    @Autowired
    public FlowActuatorController(FlowActuatorService flowActuatorService) {
        setModelService(flowActuatorService);
    }

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    /**
     * 创建trex虚拟机
     * @return
     */
    @ApiOperation("创建trex虚拟机")
    @PostMapping("/vm/add")
    public ServiceResult<String> addExecutor(@RequestBody @Validated AddVmInstanceRequest addVmInstanceRequest) {
        String uuid = TextUtils.uuid();
        asyncPlatFlowTaskService.addExecutor(uuid, addVmInstanceRequest.getNetworkInfos());
        return ServiceResult.success(uuid);
    }

    /**
     * 删除虚拟机实例
     * @param actuatorId
     * @return
     */
    @ApiOperation("删除虚拟机实例")
    @DeleteMapping("/vm/{actuatorId}")
    public ServiceResult<String> delExecutor(@PathVariable String actuatorId) {
        getModelService().delExecutor(actuatorId);
        return ServiceResult.successful();
    }

}
