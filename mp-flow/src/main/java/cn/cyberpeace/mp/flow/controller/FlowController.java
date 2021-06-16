package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowModel;
import cn.cyberpeace.mp.flow.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowController extends BaseModelController<FlowModel, FlowService> {

    @Autowired
    public FlowController(FlowService flowService) {
        setModelService(flowService);
    }

}
