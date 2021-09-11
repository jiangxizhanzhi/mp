package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 通用的基于单表的具有批量更新能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelBulkUpdateController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 通用批量更新
     * @param objs 更新对象集，需要更新的字段必须一致
     * @return 成功数量
     */
    @ApiOperation("批量更新数据")
    @PatchMapping("/bulk_update")
    default ServiceResult<Integer> bulkUpdate(@RequestBody List<M> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

}
