package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.controller.param.QueryParams;
import cn.cyberpeace.mp.base.controller.stereotype.ListParamNames;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 通用的基于单表的具有增删改查能力的controller
 * @param <M> 数据model
 * @param <S> 数据model对应的service
 */
@Slf4j
@Data
public abstract class BaseModelController<M extends BaseModel, S extends BaseModelService<M>> {

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
     * @param listParamNamesAnnotationClass 查询方法对应的列表字段标记注解类
     * @param request http请求对象
     * @param queryParams 查询参数
     */
    public void handleQueryParams(String listFuncName,
                                  Class<? extends ListParamNames> listParamNamesAnnotationClass,
                                  HttpServletRequest request,
                                  QueryParams queryParams) {
        String[] actualListParamNames;
        ListParamNames listParamNamesAnnotation = getClass().getAnnotation(listParamNamesAnnotationClass);
        if (listParamNamesAnnotation != null) {
            actualListParamNames = listParamNamesAnnotation.value();
        } else {
            actualListParamNames = new String[0];
        }
        Map<String, Object> params = queryParams.getParams();
        Map<String, String[]> listParams = request.getParameterMap();
        for (String listParamName: actualListParamNames) {
            if (listParams.containsKey(listParamName)) {
                params.put(listParamName, listParams.get(listParamName));
            }
        }
        params.remove("search");
        params.remove("searchFields");
        params.remove("orders");
        params.remove("page");
        params.remove("pageSize");
        params.remove("fields");

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
     * @param request http请求对象
     * @param params 业务查询参数
     * @param search 搜索关键词
     * @param searchFields 搜索涉及字段
     * @param orders 排序字段
     * @param page 页码
     * @param pageSize 每页大小
     * @param fields 返回数据字段
     * @return 列表查询结果
     */
    @GetMapping
    public ListObjServiceResult<M> list(HttpServletRequest request,
                                        @RequestParam Map<String, Object> params,
                                        @RequestParam(required = false) String search,
                                        @RequestParam(required = false) String[] searchFields,
                                        @RequestParam(required = false) String[] orders,
                                        @RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer pageSize,
                                        @RequestParam(required = false) String[] fields) {
        var queryParams = new QueryParams(params, search, searchFields, orders, page, pageSize, fields);
        handleQueryParams("list", ListParamNames.class, request, queryParams);
        return getModelService().list(queryParams.getParams(), queryParams.getSearch(), queryParams.getSearchFields(),
                queryParams.getOrders(), queryParams.getPage(), queryParams.getPageSize(),
                queryParams.getFields()).asResult();
    }

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @GetMapping("/{id}")
    public ObjServiceResult<M> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return getModelService().get(id, fields).asResult();
    }

    /**
     * 通用创建
     * @param obj 数据对象
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
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
    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return getModelService().delete(id).asResult();
    }

    /**
     * 通用批量创建
     * @param objs 创建对象集
     * @return 成功数量
     */
    @PostMapping("/bulk_create")
    public ServiceResult<Integer> bulkCreate(@RequestBody List<M> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

    /**
     * 通用批量更新
     * @param objs 更新对象集，需要更新的字段必须一致
     * @return 成功数量
     */
    @PatchMapping("/bulk_update")
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<M> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

    /**
     * 通用批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }
}
