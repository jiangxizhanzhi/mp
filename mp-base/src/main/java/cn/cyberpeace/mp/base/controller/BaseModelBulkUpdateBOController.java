package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BaseModelBulkUpdateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelBOService<T, ?>> {

    S getModelBOService();

    @ApiOperation("批量更新数据")
    @PatchMapping("/bulk_update")
    default ServiceResult<Integer> bulkUpdate(@RequestBody List<T> bos) {
        return getModelBOService().bulkUpdateBO(bos).asResult();
    }

}
