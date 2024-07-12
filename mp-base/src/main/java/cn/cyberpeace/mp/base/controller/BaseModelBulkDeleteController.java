 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelBulkDeleteController.java.
 // File size 1787.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.request.BaseBulkDeleteData;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 通用的基于单表的具有删除能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
@Deprecated
public interface BaseModelBulkDeleteController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    /**
     * getModelService
     *
     * @return
     */
    S getModelService();

    /**
     * 通用批量删除
     *
     * @param ids 数据id集
     * @return 成功数量
     */
    @ApiOperation("批量删除数据")
    @ApiImplicitParam(name = "ids", value = "删除数据id列表", dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    default ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }

    /**
     * 通用删除
     *
     * @param deleteData 删除数据
     * @return 成功数量
     */
    @ApiOperation("批量删除数据")
    @ApiImplicitParam(name = "deleteData", value = "删除数据请求体", dataTypeClass = BaseBulkDeleteData.class)
    @DeleteMapping("/pro_bulk_delete")
    default ServiceResult<Integer> bulkDelete(@RequestBody BaseBulkDeleteData deleteData) {
        return getModelService().bulkDelete(deleteData.getIds()).asResult();
    }

}
