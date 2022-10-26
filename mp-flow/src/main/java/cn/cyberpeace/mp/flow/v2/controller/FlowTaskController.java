package cn.cyberpeace.mp.flow.v2.controller;

import cn.cyberpeace.mp.base.controller.*;
import cn.cyberpeace.mp.base.controller.stereotype.ModelBOClass;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBO;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.v2.service.FlowTaskService;
import cn.cyberpeace.mp.flow.v2.service.param.FlowTaskQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "流量任务管理接口", tags = "流量任务管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flow_tasks")
@ModelBOClass(FlowTaskBO.class)
public class FlowTaskController implements
        BaseModelListBOController<FlowTaskBO, FlowTaskModel, FlowTaskQueryParams, FlowTaskService>,
        BaseModelGetBOController<FlowTaskBO, FlowTaskModel, FlowTaskService>,
        BaseModelCreateBOController<FlowTaskBO, FlowTaskModel, FlowTaskService>,
        BaseModelUpdateBOController<FlowTaskBO, FlowTaskModel, FlowTaskService>,
        BaseModelBulkDeleteBOController<FlowTaskBO, FlowTaskModel, FlowTaskService> {

    @Getter
    @Setter
    private FlowTaskService modelService;

    @Autowired
    public FlowTaskController(FlowTaskService modelService) {
        setModelService(modelService);
    }

    @ApiOperation("新增流量任务")
    @Override
    public ObjServiceResult<FlowTaskBO> create(@Validated FlowTaskBO bo, String[] fields) {
        return BaseModelCreateBOController.super.create(bo, fields);
    }
    
    @ApiOperation("根据id获取流量任务")
    @Override
    public ObjServiceResult<FlowTaskBO> get(String id, String[] fields) {
    	ObjServiceResult<FlowTaskBO> boResult = BaseModelGetBOController.super.get(id, fields);
    	getModelService().fillFlowInfo(boResult.getData());
    	
    	return boResult;
    }
}
