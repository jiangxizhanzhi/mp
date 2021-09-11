package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通用的基于单表的具有创建能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelCreateController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 通用创建
     * @param obj 数据对象
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass=String[].class),
    })
    @PostMapping
    default ObjServiceResult<M> create(@RequestBody M obj, @RequestParam(required = false) String[] fields) {
        return getModelService().create(obj, fields).asResult();
    }

}
