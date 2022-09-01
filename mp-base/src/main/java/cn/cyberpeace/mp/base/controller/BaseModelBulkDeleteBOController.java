package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.request.BaseBulkDeleteData;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseModelBulkDeleteBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelBOService<T, ?>> {

    S getModelBOService();

    @ApiOperation("批量删除数据")
    @DeleteMapping("/bulk_delete")
    default ServiceResult<Integer> bulkDelete(@RequestBody BaseBulkDeleteData deleteData) {
        return getModelBOService().bulkDeleteBO(deleteData.getIds()).asResult();
    }

}
