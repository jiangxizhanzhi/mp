package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.request.BaseDeleteData;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface BaseModelDeleteBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelBOService<T, ?>> {

    S getModelBOService();

    @ApiOperation("删除指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    default ServiceResult<Boolean> delete(@PathVariable String id) {
        return getModelBOService().deleteBO(id).asResult();
    }

    @ApiOperation("删除指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据id", dataTypeClass = String.class),
    })
//    @DeleteMapping(params = "id")
    default ServiceResult<Boolean> deleteByParam(@RequestParam String id) {
        return getModelBOService().deleteBO(id).asResult();
    }

//    @DeleteMapping
    default ServiceResult<Boolean> deleteByData(@RequestBody BaseDeleteData deleteData) {
        return getModelBOService().deleteBO(deleteData.getId()).asResult();
    }

}
