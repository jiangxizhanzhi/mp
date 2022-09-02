package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

public interface BaseModelListBOController<T extends ModelBO<M, T>, M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>>
        extends BaseModelListControllerBase {

    S getModelService();

    @ApiOperation("获取分页数据")
    @GetMapping
    default ListObjServiceResult<T> list(Q queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().<T>listBO(queryParams, ControllerCommon.getModelBOClass(getClass())).asResult();
    }
}
