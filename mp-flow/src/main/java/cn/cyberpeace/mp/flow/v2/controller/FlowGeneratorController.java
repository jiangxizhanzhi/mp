package cn.cyberpeace.mp.flow.v2.controller;

import cn.cyberpeace.mp.base.controller.BaseModelGetBOController;
import cn.cyberpeace.mp.base.controller.BaseModelListBOController;
import cn.cyberpeace.mp.base.controller.BaseModelUpdateBOController;
import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.controller.request.BaseBulkDeleteData;
import cn.cyberpeace.mp.base.controller.stereotype.ModelBOClass;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.model.FlowGeneratorModel;
import cn.cyberpeace.mp.flow.v2.service.FlowGeneratorService;
import cn.cyberpeace.mp.flow.v2.service.param.FlowGeneratorQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "流量发生器管理接口", tags = "流量发生器管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flow_generators")
@ModelBOClass(FlowGeneratorBO.class)
public class FlowGeneratorController implements
        BaseModelListBOController<FlowGeneratorBO, FlowGeneratorModel, FlowGeneratorQueryParams, FlowGeneratorService>,
        BaseModelGetBOController<FlowGeneratorBO, FlowGeneratorModel, FlowGeneratorService>,
        BaseModelUpdateBOController<FlowGeneratorBO, FlowGeneratorModel, FlowGeneratorService> {

    @Getter
    @Setter
    private FlowGeneratorService modelService;

    @Autowired
    public FlowGeneratorController(FlowGeneratorService modelService) {
        setModelService(modelService);
    }

    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass=String[].class),
    })
    @PostMapping
    public ObjServiceResult<FlowGeneratorBO> create(@RequestBody FlowGeneratorBO bo,
                                                    @RequestParam(required = false) String[] fields) {
        return getModelService().createFlowGenerator(bo, ControllerCommon.convertValidFields(fields)).asResult();
    }

    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass=String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    public ObjServiceResult<FlowGeneratorBO> update(@PathVariable String id, @RequestBody FlowGeneratorBO bo,
                                                    @RequestParam(required = false) String[] fields) {
        bo.setId(id);
        return getModelService().updateFlowGenerator(bo, ControllerCommon.convertValidFields(fields)).asResult();
    }

    @ApiOperation("批量删除数据")
    @ApiImplicitParam(name = "deleteData", value = "删除数据请求体", dataTypeClass = BaseBulkDeleteData.class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody BaseBulkDeleteData deleteData) {
        return getModelService().bulkDeleteFlowGenerator(deleteData.getIds()).asResult();
    }

}
