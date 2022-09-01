package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

public interface BaseModelListBOController<T extends ModelBO<M, T>, M extends BaseModel, Q extends QueryParams, S extends BaseModelBOService<T, Q>>
        extends BaseModelListControllerBase {

    S getModelBOService();

    @ApiOperation("获取分页数据")
    @GetMapping
    default ListObjServiceResult<T> list(Q queryParams) {
        handleQueryParams("list", queryParams);
        return getModelBOService().listBO(queryParams).asResult();
    }
}
