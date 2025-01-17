 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelListBOController.java.
 // File size 1010.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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
