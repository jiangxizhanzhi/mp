package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Data
public abstract class BaseModelController<Model extends BaseModel, ModelService extends BaseModelService<Model>> {

    private ModelService modelService;

    @GetMapping()
    public List<Model> queryList(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return modelService.queryList(page, pageSize);
    }

    @GetMapping("/{id}")
    public Model update(@PathVariable String id) {
        return modelService.get(id);
    }

    @PostMapping()
    public Model create(@RequestBody Model obj) {
        return modelService.create(obj);
    }

    @PatchMapping("/{id}")
    public Model update(@PathVariable String id, @RequestBody Model obj) {
        obj.setId(id);
        return modelService.update(obj);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable String id) {
        return modelService.delete(id);
    }
}
