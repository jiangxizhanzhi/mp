package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.service.FlowScriptParamService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description 流量脚本参数controller
 * @Author qiaokang
 * @Date 2021-11-25 15:00:45
 */
@Slf4j
@Api(tags = "流量脚本参数接口")
//@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scriptparam")
public class FlowScriptParamController extends BaseModelController<FlowScriptParamModel, QueryParams, FlowScriptParamService> {

    @Autowired
    public FlowScriptParamController(FlowScriptParamService flowScriptParamService) {
        setModelService(flowScriptParamService);
    }

}
