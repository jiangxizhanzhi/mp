package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


public interface BaseModelUpdateController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass=String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    default ObjServiceResult<M> update(@PathVariable String id, @RequestBody M obj,
                                       @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, ControllerCommon.convertValidFields(fields)).asResult();
    }

    
    @ApiOperation("更新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping
    default ObjServiceResult<M> update(@RequestBody M obj,
                                       @RequestParam(required = false) String[] fields) {
        return getModelService().update(obj, ControllerCommon.convertValidFields(fields)).asResult();
    }

    
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping(params = "id")
    default ObjServiceResult<M> updateByParam(@RequestParam String id, @RequestBody M obj,
                                              @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, ControllerCommon.convertValidFields(fields)).asResult();
    }

}
