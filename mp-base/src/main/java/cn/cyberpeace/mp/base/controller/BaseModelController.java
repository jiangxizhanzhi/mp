package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/**
 * 通用的基于单表的具有增删改查能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
@Slf4j
@Data
public abstract class BaseModelController<M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>> {

    /** 在实现类注入 */
    private S modelService;

    /**
     * 判断查询方法是否不限制分页大小
     * @param listFuncName 查询方法名称
     * @return boolean
     */
    private boolean isUnlimitPagination(String listFuncName) {
        var unlimitPaginationAnnotation = getClass().getAnnotation(UnlimitPagination.class);
        if (unlimitPaginationAnnotation == null) {
            return false;
        }

        return Arrays.asList(unlimitPaginationAnnotation.value()).contains(listFuncName);
    }

    /**
     * 处理有效的查询参数
     * @param listFuncName 查询方法名称
     * @param queryParams 查询参数
     */
    public void handleQueryParams(String listFuncName, QueryParams queryParams) {
        queryParams.ensure();

        int page = queryParams.getPage() == null ? 0 : queryParams.getPage();
        int pageSize = queryParams.getPageSize() == null ? 0 : queryParams.getPageSize();
        if (pageSize <= 0) {
            pageSize = BaseSingleton.baseAppConfig.getDefaultPageSize();
        } else if (pageSize > BaseSingleton.baseAppConfig.getMaxPageSize() && !isUnlimitPagination(listFuncName)) {
            pageSize = BaseSingleton.baseAppConfig.getMaxPageSize();
        }
        queryParams.setPage(page);
        queryParams.setPageSize(pageSize);
    }

    /**
     * 通用查询方法
     * @param queryParams 查询参数
     * @return 列表查询结果
     */
    @ApiOperation("获取分页数据")
    @GetMapping
    public ListObjServiceResult<M> list(Q queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().list(queryParams).asResult();
    }

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    public ObjServiceResult<M> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        log.info("id:{}, fields:{}", id, fields);
        return getModelService().get(id, fields).asResult();
    }

    /**
     * 通用创建
     * @param obj 数据对象
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PostMapping
    public ObjServiceResult<M> create(@RequestBody M obj, @RequestParam(required = false) String[] fields) {
        return getModelService().create(obj, fields).asResult();
    }

    /**
     * 通用更新
     * @param id 数据id
     * @param obj 数据对象
     * @param fields 返回数据字段，只涉及更新字段
     * @return 单条数据结果
     */
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    public ObjServiceResult<M> update(@PathVariable String id, @RequestBody M obj,
                                      @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, fields).asResult();
    }

    /**
     * 通用删除
     * @param id 数据id
     * @return 是否删除成功
     */
    @ApiOperation("删除指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        log.info("id:{}", id);
        return getModelService().delete(id).asResult();
    }

    /**
     * 通用批量创建
     * @param objs 创建对象集
     * @return 成功数量
     */
    @ApiOperation("批量创建数据")
    @PostMapping("/bulk_create")
    public ServiceResult<Integer> bulkCreate(@RequestBody List<M> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

    /**
     * 通用批量更新
     * @param objs 更新对象集，需要更新的字段必须一致
     * @return 成功数量
     */
    @ApiOperation("批量更新数据")
    @PatchMapping("/bulk_update")
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<M> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

    /**
     * 通用批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @ApiOperation("批量删除数据")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }
}
