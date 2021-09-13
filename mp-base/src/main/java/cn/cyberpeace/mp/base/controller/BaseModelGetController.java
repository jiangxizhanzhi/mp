package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通用的基于单表的具有查询单条数据能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
public interface BaseModelGetController<M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    default ObjServiceResult<M> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return getModelService().get(id, fields).asResult();
    }

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "fields", value = "需要返回的字段", dataTypeClass = String[].class),
    })
    @GetMapping(params = "id")
    default ObjServiceResult<M> getByParam(@RequestParam String id, @RequestParam(required = false) String[] fields) {
        return getModelService().get(id, fields).asResult();
    }
}
