package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ClassUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
public abstract class BaseModelController<Model extends BaseModel, ModelService extends BaseModelService<Model>> {

    private ModelService modelService;

    public static final String[] listParamNames = new String[0];

    @GetMapping
    public ServiceResult list(@RequestParam Map<String, Object> params,
                              @RequestParam Map<String, String[]> listParams,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String[] searchFields,
                              @RequestParam(required = false) String[] orders,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) String[] fields) {
        String[] listParamNames = ClassUtils.getStaticFieldValue(this.getClass(), "listParamNames",
                String[].class);
        for (String listParamName: listParamNames) {
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
        return modelService.list(params, search, searchFields, orders, page, pageSize, fields).asResult();
    }

    @GetMapping("/{id}")
    public ServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return modelService.get(id, fields).asResult();
    }

    @PostMapping
    public ServiceResult create(@RequestBody Model obj, @RequestParam(required = false) String[] fields) {
        return modelService.create(obj, fields).asResult();
    }

    @PatchMapping("/{id}")
    public ServiceResult update(@PathVariable String id, @RequestBody Model obj,
                                @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return modelService.update(obj, fields).asResult();
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable String id) {
        return modelService.delete(id);
    }

    @PostMapping("/bulk_create")
    public ServiceResult bulkCreate(@RequestBody List<Model> objs) {
        return modelService.bulkCreate(objs).asResult();
    }

    @PatchMapping("/bulk_update")
    public ServiceResult bulkUpdate(@RequestBody List<Model> objs) {
        return modelService.bulkUpdate(objs).asResult();
    }

    @DeleteMapping("/bulk_delete")
    public ServiceResult bulkDelete(@RequestBody String[] ids) {
        return modelService.bulkDelete(ids);
    }
}
