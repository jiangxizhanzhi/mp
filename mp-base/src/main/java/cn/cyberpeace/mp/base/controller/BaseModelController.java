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

@Slf4j
@Data
public abstract class BaseModelController<M extends BaseModel, S extends BaseModelService<M>> {

    private S modelService;

    private boolean isUnlimitPagination(String listFuncName) {
        var unlimitPaginationAnnotation = getClass().getAnnotation(UnlimitPagination.class);
        if (unlimitPaginationAnnotation == null) {
            return false;
        }

        return Arrays.asList(unlimitPaginationAnnotation.value()).contains(listFuncName);
    }

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

    @GetMapping("/{id}")
    public ObjServiceResult<M> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return getModelService().get(id, fields).asResult();
    }

    @PostMapping
    public ObjServiceResult<M> create(@RequestBody M obj, @RequestParam(required = false) String[] fields) {
        return getModelService().create(obj, fields).asResult();
    }

    @PatchMapping("/{id}")
    public ObjServiceResult<M> update(@PathVariable String id, @RequestBody M obj,
                                      @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, fields).asResult();
    }

    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return getModelService().delete(id).asResult();
    }

    @PostMapping("/bulk_create")
    public ServiceResult<Integer> bulkCreate(@RequestBody List<M> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

    @PatchMapping("/bulk_update")
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<M> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }
}
