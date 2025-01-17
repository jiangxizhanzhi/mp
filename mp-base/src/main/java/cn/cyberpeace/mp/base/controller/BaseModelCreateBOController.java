 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelCreateBOController.java.
 // File size 1289.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface BaseModelCreateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass=String[].class),
    })
    @PostMapping
    default ObjServiceResult<T> create(@RequestBody T bo, @RequestParam(required = false) String[] fields) {
        return getModelService().<T>createBO(bo, ControllerCommon.convertValidFields(fields), ControllerCommon.getModelBOClass(getClass())).asResult();
    }

}
