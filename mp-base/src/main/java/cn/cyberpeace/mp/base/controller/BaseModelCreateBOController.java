package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface BaseModelCreateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelBOService<T, ?>> {

    S getModelBOService();

    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass=String[].class),
    })
    @PostMapping
    default ObjServiceResult<T> create(@RequestBody T bo, @RequestParam(required = false) String[] fields) {
        return getModelBOService().createBO(bo, ControllerCommon.convertValidFields(fields)).asResult();
    }

}
