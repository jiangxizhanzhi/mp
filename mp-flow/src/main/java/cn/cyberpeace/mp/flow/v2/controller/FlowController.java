package cn.cyberpeace.mp.flow.v2.controller;

import cn.cyberpeace.mp.base.controller.*;
import cn.cyberpeace.mp.base.controller.stereotype.ModelBOClass;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.v2.bo.FlowBO;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.service.FlowService;
import cn.cyberpeace.mp.flow.v2.service.param.FlowQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "流量信息管理接口", tags = "流量信息管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flows")
@ModelBOClass(FlowBO.class)
public class FlowController implements
        BaseModelListBOController<FlowBO, FlowModel, FlowQueryParams, FlowService>,
        BaseModelGetBOController<FlowBO, FlowModel, FlowService>,
        BaseModelCreateBOController<FlowBO, FlowModel, FlowService>,
        BaseModelUpdateBOController<FlowBO, FlowModel, FlowService>,
        BaseModelBulkDeleteBOController<FlowBO, FlowModel, FlowService> {

    @Getter
    @Setter
    private FlowService modelService;

    @Autowired
    public FlowController(FlowService modelService) {
        setModelService(modelService);
    }

    @ApiOperation("新增流量信息")
    @Override
    public ObjServiceResult<FlowBO> create(@Validated FlowBO bo, String[] fields) {
        bo.paramCheck(false);
        ObjServiceResult<FlowModel> objServiceResult = this.getModelService().create(bo.toModel(), fields);

        FlowBO resultBo = new FlowBO();
        resultBo = resultBo.fromModel(objServiceResult.getSource().getObj());
        return new ObjServiceResult<>(resultBo, null).asResult();
    }

    @ApiOperation("更新流量信息")
    @Override
    public ObjServiceResult<FlowBO> update(String id, @Validated FlowBO bo, String[] fields) {
        bo.setId(id);
        bo.paramCheck(true);
        ObjServiceResult<FlowModel> objServiceResult = this.getModelService().update(bo.toModel(), fields);

        FlowBO resultBo = new FlowBO();
        resultBo = resultBo.fromModel(objServiceResult.getSource().getObj());
        return new ObjServiceResult<>(resultBo, null).asResult();
    }

}
