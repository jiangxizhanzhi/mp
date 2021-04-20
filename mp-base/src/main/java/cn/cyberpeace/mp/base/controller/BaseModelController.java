package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Data
public abstract class BaseModelController<Model extends BaseModel, ModelService extends BaseModelService<Model>> {

    private ModelService modelService;

    @GetMapping
    public ListObjResult<Model> list(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) String[] fields) {
        return modelService.list(page, pageSize, fields).asResult();
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return modelService.get(id, fields).asResult();
    }

    @PostMapping
    public Object create(@RequestBody Model obj, @RequestParam(required = false) String[] fields) {
        return modelService.create(obj, fields).asResult();
    }

    @PatchMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody Model obj,
                         @RequestParam(required = false) String[] fields) {
        return modelService.update(id, obj, fields).asResult();
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable String id) {
        return modelService.delete(id);
    }
}
