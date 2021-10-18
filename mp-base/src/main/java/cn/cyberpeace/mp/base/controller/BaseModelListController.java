package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 通用的基于单表查询列表的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelListController<M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>>
        extends BaseModelListControllerBase {

    S getModelService();

    /**
     * 通用查询方法
     * @param queryParams 查询参数
     * @return 列表查询结果
     */
    @ApiOperation("获取分页数据")
    @GetMapping
    default ListObjServiceResult<M> list(Q queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().list(queryParams).asResult();
    }
}
