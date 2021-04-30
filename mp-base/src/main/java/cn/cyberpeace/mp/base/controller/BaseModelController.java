package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.constant.BaseControllerConstant;
import cn.cyberpeace.mp.base.controller.param.QueryParams;
import cn.cyberpeace.mp.base.controller.stereotype.ListParamNames;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
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

    private void handleQueryParams(String listFuncName,
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
            pageSize = BaseControllerConstant.DEFAULT_PAGE_SIZE;
        } else if (pageSize > BaseControllerConstant.MAX_PAGE_SIZE && !isUnlimitPagination(listFuncName)) {
            pageSize = BaseControllerConstant.MAX_PAGE_SIZE;
        }
        queryParams.setPage(page);
        queryParams.setPageSize(pageSize);
    }

    @GetMapping
    public ServiceResult list(HttpServletRequest request,
                              @RequestParam Map<String, Object> params,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String[] searchFields,
                              @RequestParam(required = false) String[] orders,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) String[] fields) {
        var queryParams = new QueryParams(params, search, searchFields, orders, page, pageSize, fields);
        handleQueryParams("list", ListParamNames.class, request, queryParams);
        return modelService.list(queryParams.getParams(), queryParams.getSearch(), queryParams.getSearchFields(),
                queryParams.getOrders(), queryParams.getPage(), queryParams.getPageSize(),
                queryParams.getFields()).asResult();
    }

    @GetMapping("/{id}")
    public ServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return modelService.get(id, fields).asResult();
    }

    @PostMapping
    public ServiceResult create(@RequestBody M obj, @RequestParam(required = false) String[] fields) {
        return modelService.create(obj, fields).asResult();
    }

    @PatchMapping("/{id}")
    public ServiceResult update(@PathVariable String id, @RequestBody M obj,
                                @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return modelService.update(obj, fields).asResult();
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable String id) {
        return modelService.delete(id).asResult();
    }

    @PostMapping("/bulk_create")
    public ServiceResult bulkCreate(@RequestBody List<M> objs) {
        return modelService.bulkCreate(objs).asResult();
    }

    @PatchMapping("/bulk_update")
    public ServiceResult bulkUpdate(@RequestBody List<M> objs) {
        return modelService.bulkUpdate(objs).asResult();
    }

    @DeleteMapping("/bulk_delete")
    public ServiceResult bulkDelete(@RequestBody String[] ids) {
        return modelService.bulkDelete(ids).asResult();
    }
}
