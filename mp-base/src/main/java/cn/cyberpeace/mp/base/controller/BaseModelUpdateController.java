package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通用的基于单表的具有更新能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelUpdateController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 通用更新
     * @param id 数据id
     * @param obj 数据对象
     * @param fields 返回数据字段，只涉及更新字段
     * @return 单条数据结果
     */
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass=String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    default ObjServiceResult<M> update(@PathVariable String id, @RequestBody M obj,
                                       @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, fields).asResult();
    }

    /**
     * 通用更新, 兼容
     * @param obj 数据对象
     * @param fields 返回数据字段，只涉及更新字段
     * @return 单条数据结果
     */
    @ApiOperation("更新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping
    default ObjServiceResult<M> update(@RequestBody M obj,
                                       @RequestParam(required = false) String[] fields) {
        return getModelService().update(obj, fields).asResult();
    }

    /**
     * 通用更新, 兼容
     * @param id 数据id
     * @param obj 数据对象
     * @param fields 返回数据字段，只涉及更新字段
     * @return 单条数据结果
     */
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @PatchMapping(params = "id")
    default ObjServiceResult<M> updateByParam(@RequestParam String id, @RequestBody M obj,
                                              @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, fields).asResult();
    }

}
