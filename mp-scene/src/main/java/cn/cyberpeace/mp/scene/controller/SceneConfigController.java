package cn.cyberpeace.mp.scene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.ControllerConstant;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
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

    @GetMapping(params = "id")
    public ServiceResult<SceneConfigDataBO> get(@RequestParam String id,
                                                @RequestParam(required = false) String area) {
        if (area == null) {
            return sceneConfigService.get(id, false, null).asResult();
        } else {
            return sceneConfigService.get(id, true, area).asResult();
        }
    }

    @PostMapping("/sync")
    public ServiceResult<SceneConfigDataBO> sync(@RequestBody SceneConfigDataBO sceneConfigDataBO) {
        return sceneConfigService.sync(sceneConfigDataBO, false).asResult();
    }

    @PostMapping("/sync_area")
    public ServiceResult<SceneConfigDataBO> syncArea(@RequestBody SceneConfigDataBO sceneConfigDataBO) {
        return sceneConfigService.sync(sceneConfigDataBO, true).asResult();
    }

    @DeleteMapping(params = "id")
    public ServiceResult<Boolean> delete(@RequestParam String id) {
        return sceneConfigService.delete(id, false).asResult();
    }

    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return sceneConfigService.bulkDelete(ids, false).asResult();
    }

}
