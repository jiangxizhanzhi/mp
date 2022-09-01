package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BaseModelBulkCreateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelBOService<T, ?>> {

    S getModelBOService();

    @ApiOperation("批量创建数据")
    @PostMapping("/bulk_create")
    default ServiceResult<Integer> bulkCreate(@RequestBody List<T> bos) {
        return getModelBOService().bulkCreateBO(bos).asResult();
    }

}
