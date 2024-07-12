 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelBulkUpdateBOController.java.
 // File size 1339.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName BaseModelBulkUpdateBOController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Deprecated
public interface BaseModelBulkUpdateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 批量更新数据
     *
     * @param bos
     * @return
     */
    @ApiOperation("批量更新数据")
    @ApiImplicitParam(name = "bos", value = "更新数据请求体", dataTypeClass = List.class)
    @PatchMapping("/bulk_update")
    default ServiceResult<Integer> bulkUpdate(@RequestBody List<T> bos) {
        return getModelService().<T>bulkUpdateBO(bos, ControllerCommon.getModelBOClass(getClass())).asResult();
    }

}
