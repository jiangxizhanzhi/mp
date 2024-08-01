package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import cn.cyberpeace.mp.flow.service.FlowScriptDemoService;
import cn.cyberpeace.mp.flow.service.impl.FlowScriptDemoServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: 流量模板控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量脚本模板接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/task")
@AuthTarget(TargetEnum.NO_AUTH)
public class FlowTaskStatusRefreshController extends BaseModelController<FlowScriptDemoModel, QueryParams, FlowScriptDemoService> {

    @Autowired
    public FlowTaskStatusRefreshController(FlowScriptDemoService flowScriptDemoService) {
        setModelService(flowScriptDemoService);
    }

    @Autowired
    private FlowScriptDemoServiceImpl flowScriptDemoService;

    @PostMapping("/refresh_status")
    public ServiceResult<Boolean> listFlowScriptDemos(@RequestBody Map<String, Object> data) {
        log.info("refresh_status , params is {}", JsonCommon.snakeStringify(data));
        return new ServiceResult<>(true).asResult();
    }

}
