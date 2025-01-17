 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelBulkCreateController.java.
 // File size 1168.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 通用的基于单表的具有批量创建能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelBulkCreateController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 通用批量创建
     * @param objs 创建对象集
     * @return 成功数量
     */
    @ApiOperation("批量创建数据")
    @ApiImplicitParam(name = "objs", value = "创建数据请求体", dataTypeClass = List.class)
    @PostMapping("/bulk_create")
    default ServiceResult<Integer> bulkCreate(@RequestBody List<M> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

}
