package cn.cyberpeace.mp.scene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.ControllerConstant;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scene_configs")
@Data
public class SceneConfigController {

    private SceneConfigService sceneConfigService;

    @Autowired
    public SceneConfigController(SceneConfigService sceneConfigService) {
        setSceneConfigService(sceneConfigService);
    }

    @GetMapping("/{id}")
    public ServiceResult<SceneConfigDataBO> get(@PathVariable String id) {
        return sceneConfigService.get(id, false, null).asResult();
    }

    @GetMapping("/{id}/area/{areaId}")
    public ServiceResult<SceneConfigDataBO> getArea(@PathVariable String id, @PathVariable String areaId) {
        return sceneConfigService.get(id, true, areaId).asResult();
    }

    @PostMapping("/sync")
    public ServiceResult<SceneConfigDataBO> sync(@RequestBody SceneConfigDataBO sceneConfigDataBO) {
        return sceneConfigService.sync(sceneConfigDataBO, false).asResult();
    }

    @PostMapping("/sync_area")
    public ServiceResult<SceneConfigDataBO> syncArea(@RequestBody SceneConfigDataBO sceneConfigDataBO) {
        return sceneConfigService.sync(sceneConfigDataBO, true).asResult();
    }

    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return sceneConfigService.delete(id).asResult();
    }

    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return sceneConfigService.bulkDelete(ids).asResult();
    }

}
