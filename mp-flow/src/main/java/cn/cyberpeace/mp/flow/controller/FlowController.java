package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowExecuteRequest;
import cn.cyberpeace.mp.flow.model.FlowModel;
import cn.cyberpeace.mp.flow.service.FlowService;
import cn.cyberpeace.mp.flow.service.impl.FlowExecuteScheduleMethodImpl;
import cn.cyberpeace.mp.flow.util.FlowExecuteScheduleUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "流量管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowController extends BaseModelController<FlowModel, QueryParams, FlowService> {

    @Autowired
    public FlowController(FlowService flowService) {
        setModelService(flowService);
    }

    @Autowired
    private FlowExecuteScheduleMethodImpl flowExecuteScheduleMethod;

    /**
     * 定时执行流量发生任务
     * @param request
     * @return
     */
    @ApiOperation("定时执行流量发生任务")
    @PostMapping("/execute")
    public ServiceResult flowExecuteSchedule(@RequestBody @Validated FlowExecuteRequest request) {
        FlowExecuteScheduleUtil util = FlowExecuteScheduleUtil.builder().build();
        util.setFlowBaseModel(request);
        util.setMethod(flowExecuteScheduleMethod);
        util.schedule();

        return ServiceResult.successful();
    }

}
