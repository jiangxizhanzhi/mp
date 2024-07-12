 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelCreateBOController.java.
 // File size 1600.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName BaseModelCreateBOController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Deprecated
public interface BaseModelCreateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelService<M, ?>> {

    /**
     * getModelService
     *
     * @return
     */
    S getModelService();

    /**
     * 创建数据
     *
     * @param bo
     * @param fields
     * @return
     */
    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass=String[].class),
    })
    @PostMapping
    default ObjServiceResult<T> create(@RequestBody T bo, @RequestParam(required = false) String[] fields) {
        return getModelService().<T>createBO(bo, ControllerCommon.convertValidFields(fields), ControllerCommon.getModelBOClass(getClass())).asResult();
    }

}
