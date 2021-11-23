package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.request.BaseBulkDeleteData;
import cn.cyberpeace.mp.base.controller.request.BaseDeleteData;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 通用的基于单表的具有删除能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelBulkDeleteController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 通用批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @ApiOperation("批量删除数据")
    @DeleteMapping("/bulk_delete")
    default ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }

    /**
     * 通用删除
     * @param deleteData 删除数据
     * @return 成功数量
     */
    @ApiOperation("批量删除数据")
    @DeleteMapping("/pro_bulk_delete")
    default ServiceResult<Integer> bulkDelete(@RequestBody BaseBulkDeleteData deleteData) {
        return getModelService().bulkDelete(deleteData.getIds()).asResult();
    }

}
