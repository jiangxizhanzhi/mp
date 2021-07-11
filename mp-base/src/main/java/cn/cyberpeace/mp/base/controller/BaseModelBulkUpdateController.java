package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface BaseModelBulkUpdateController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    
    @ApiOperation("批量更新数据")
    @ApiImplicitParam(name = "objs", value = "更新数据请求体", dataTypeClass = List.class)
    @PatchMapping("/bulk_update")
    default ServiceResult<Integer> bulkUpdate(@RequestBody List<M> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

}
