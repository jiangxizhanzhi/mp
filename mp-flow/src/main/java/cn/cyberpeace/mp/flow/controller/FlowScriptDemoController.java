package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import cn.cyberpeace.mp.flow.response.QueryFlowScriptDemoResponse;
import cn.cyberpeace.mp.flow.service.FlowScriptDemoService;
import cn.cyberpeace.mp.flow.service.impl.FlowScriptDemoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 流量模板控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量脚本模板接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scriptdemo")
public class FlowScriptDemoController extends BaseModelController<FlowScriptDemoModel, QueryParams, FlowScriptDemoService> {

    @Autowired
    public FlowScriptDemoController(FlowScriptDemoService flowScriptDemoService) {
        setModelService(flowScriptDemoService);
    }

    @Autowired
    private FlowScriptDemoServiceImpl flowScriptDemoService;

    @ApiOperation("查询脚本模板信息")
    @GetMapping("/list")
    public ServiceResult<QueryFlowScriptDemoResponse> listFlowScriptDemos() {
        return flowScriptDemoService.listFlowScriptDemos();
    }

}
