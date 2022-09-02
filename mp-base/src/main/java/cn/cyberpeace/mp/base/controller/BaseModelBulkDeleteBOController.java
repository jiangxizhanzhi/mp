package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.controller.request.BaseBulkDeleteData;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseModelBulkDeleteBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    @ApiOperation("批量删除数据")
    @ApiImplicitParam(name = "deleteData", value = "删除数据请求体", dataTypeClass = BaseBulkDeleteData.class)
    @DeleteMapping("/bulk_delete")
    default ServiceResult<Integer> bulkDelete(@RequestBody BaseBulkDeleteData deleteData) {
        return getModelService().<T>bulkDeleteBO(deleteData.getIds(), ControllerCommon.getModelBOClass(getClass())).asResult();
    }

}
