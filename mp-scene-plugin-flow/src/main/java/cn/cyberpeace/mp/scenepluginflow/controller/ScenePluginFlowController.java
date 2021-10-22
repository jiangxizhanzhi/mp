package cn.cyberpeace.mp.scenepluginflow.controller;

import cn.cyberpeace.mp.base.util.MessageUtils;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneDataBO;
import cn.cyberpeace.mp.scenepluginflow.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "场景流量插件", tags = "场景流量插件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
public class ScenePluginFlowController {

    @ApiOperation("场景模板流量插件数据，非API")
    @GetMapping("/scene_config")
    public String loadSceneConfig() {
        System.out.println(MessageUtils.get("sceneplugincloud.event.initiate_base_properties"));
        System.out.println(MessageUtils.get("scenepluginflow.event.create_executor"));
        return null;
    }

    @ApiOperation("场景流量插件数据，非API")
    @GetMapping("/scene")
    public String loadScene(@RequestBody FlowPluginSceneDataBO data) {
        return null;
    }

}
