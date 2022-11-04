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
import cn.cyberpeace.mp.flow.v2.bo.CountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.NodeBO;
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

import java.util.ArrayList;
import java.util.List;

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
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PostMapping
    public ObjServiceResult<FlowGeneratorBO> create(@RequestBody FlowGeneratorBO bo,
                                                    @RequestParam(required = false) String[] fields) {
        return getModelService().createFlowGenerator(bo, ControllerCommon.convertValidFields(fields)).asResult();
    }

    @ApiOperation("仪表注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PostMapping("/register")
    public ObjServiceResult<FlowGeneratorBO> register(@RequestBody FlowGeneratorBO bo,
                                                      @RequestParam(required = false) String[] fields) {
        return getModelService().registerFlowGenerator(bo, ControllerCommon.convertValidFields(fields)).asResult();
    }

    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    @Override
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

    @ApiOperation("批量注销仪表")
    @ApiImplicitParam(name = "deleteData", value = "删除数据请求体", dataTypeClass = BaseBulkDeleteData.class)
    @DeleteMapping("/bulk_cancel")
    public ServiceResult<Integer> bulkCancel(@RequestBody BaseBulkDeleteData deleteData) {
        return getModelService().bulkCancelFlowGenerator(deleteData.getIds()).asResult();
    }

    @ApiOperation("统计仪表数量")
    @GetMapping("/statistics")
    public ServiceResult<List<CountBO>> statistics() {
        return getModelService().statisticsFlowGenerator().asResult();
    }

    @ApiOperation("查询流量发生目标节点")
    @GetMapping("/nodes")
    public ServiceResult<List<NodeBO>> getNodes() {
        List<NodeBO> nodeBOList = new ArrayList<>();
        NodeBO nodeBO1 = new NodeBO();
        nodeBO1.setName("target_1");
        nodeBO1.setResourceId("9345f211-264b-4fa2-84ea-97338b430454");
        nodeBOList.add(nodeBO1);

        NodeBO nodeBO2 = new NodeBO();
        nodeBO2.setName("target_2");
        nodeBO2.setResourceId("97e0fc80-5fb1-4f1a-bcca-64b0ecec43ee");
        nodeBOList.add(nodeBO2);

        NodeBO nodeBO3 = new NodeBO();
        nodeBO3.setName("target_3");
        nodeBO3.setResourceId("28de2fde-ce4d-479b-a176-407437637dd1");
        nodeBOList.add(nodeBO3);

        NodeBO nodeBO4 = new NodeBO();
        nodeBO4.setName("target_4");
        nodeBO4.setResourceId("4f792ffe-41b8-4320-b58f-955ae0d647ba");
        nodeBOList.add(nodeBO4);
        return new ServiceResult<>(nodeBOList).asResult();
    }
}
