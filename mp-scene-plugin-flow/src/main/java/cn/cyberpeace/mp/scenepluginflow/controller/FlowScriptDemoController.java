 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowScriptDemoController.java.
 // File size 2259.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import cn.cyberpeace.mp.flow.response.QueryFlowScriptDemoResponse;
import cn.cyberpeace.mp.flow.service.FlowScriptDemoService;
import cn.cyberpeace.mp.flow.service.impl.FlowScriptDemoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FlowScriptDemoController
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelController
 */
@Slf4j
@Api(tags = "流量脚本模板接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scriptdemo")
@AuthTarget(TargetEnum.NO_AUTH)
public class FlowScriptDemoController extends BaseModelController<FlowScriptDemoModel, QueryParams, FlowScriptDemoService> {

    @Autowired
    public FlowScriptDemoController(FlowScriptDemoService flowScriptDemoService) {
        setModelService(flowScriptDemoService);
    }

    @Autowired
    private FlowScriptDemoServiceImpl flowScriptDemoService;

    /**
     * listFlowScriptDemos
     *
     * @param type
     * @return
     */
    @ApiOperation("查询脚本模板信息")
    @ApiImplicitParam(name = "type", value = "类型", dataTypeClass = Integer.class)
    @GetMapping("/list")
    public ServiceResult<QueryFlowScriptDemoResponse> listFlowScriptDemos(@RequestParam(required = false) Integer type) {
        return flowScriptDemoService.listScriptDemos(type);
    }

}
