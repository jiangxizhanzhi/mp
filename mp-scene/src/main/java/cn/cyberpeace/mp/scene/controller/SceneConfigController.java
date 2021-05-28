package cn.cyberpeace.mp.scene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.ControllerConstant;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scene_configs")
public class SceneConfigController extends BaseModelController<SceneConfigModel, SceneConfigService> {

    @Autowired
    public SceneConfigController(SceneConfigService modelService) {
        setModelService(modelService);
    }

    @PostMapping("/sync")
    public ServiceResult<SceneConfigDataBO> sync(@RequestBody SceneConfigDataBO sceneConfigDataBO) {
        return getModelService().sync(sceneConfigDataBO, false).asResult();
    }

    @PostMapping("/sync_area")
    public ServiceResult<SceneConfigDataBO> syncArea(@RequestBody SceneConfigDataBO sceneConfigDataBO) {
        return getModelService().sync(sceneConfigDataBO, true).asResult();
    }

    @Override
    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return getModelService().delete(id).asResult();
    }

    @Override
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }

}
