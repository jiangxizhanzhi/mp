 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelBulkUpdateBOController.java.
 // File size 1105.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BaseModelBulkUpdateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    @ApiOperation("批量更新数据")
    @ApiImplicitParam(name = "bos", value = "更新数据请求体", dataTypeClass = List.class)
    @PatchMapping("/bulk_update")
    default ServiceResult<Integer> bulkUpdate(@RequestBody List<T> bos) {
        return getModelService().<T>bulkUpdateBO(bos, ControllerCommon.getModelBOClass(getClass())).asResult();
    }

}
