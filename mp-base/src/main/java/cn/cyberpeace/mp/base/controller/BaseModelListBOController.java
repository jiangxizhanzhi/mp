package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName BaseModelListBOController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelListControllerBase
 */
@Deprecated
public interface BaseModelListBOController<T extends ModelBO<M, T>, M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>>
        extends BaseModelListControllerBase {

    /**
     * getModelService
     *
     * @return
     */
    S getModelService();

    /**
     * 获取分页数据
     *
     * @param queryParams
     * @return
     */
    @ApiOperation("获取分页数据")
    @GetMapping
    default ListObjServiceResult<T> list(Q queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().<T>listBO(queryParams, ControllerCommon.getModelBOClass(getClass())).asResult();
    }
}
