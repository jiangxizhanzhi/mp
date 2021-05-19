package cn.cyberpeace.mp.scene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.constant.ControllerConstant;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scene_configs")
public class SceneConfigController extends BaseModelController<SceneConfigModel, SceneConfigService> {

    @Autowired
    public SceneConfigController(SceneConfigService modelService) {
        setModelService(modelService);
    }

    @PostMapping("/test")
    public Object test(@RequestBody Map<String, String> data) {
        String val = data.getOrDefault("test", "");
        return JsonUtils.parse(val, Object.class);
    }

}
