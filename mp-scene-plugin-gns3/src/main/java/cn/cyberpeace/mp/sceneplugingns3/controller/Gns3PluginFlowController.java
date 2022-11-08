package cn.cyberpeace.mp.sceneplugingns3.controller;

import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigDataBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneDataBO;
import cn.cyberpeace.mp.sceneplugingns3.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "场景gns3插件", tags = "场景gns3插件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
public class Gns3PluginFlowController {

    @ApiOperation("场景模板云平台插件数据说明，非API")
    @GetMapping("/scene_config")
    public String loadSceneConfig(@RequestBody Gns3PluginSceneConfigDataBO data) {
        return null;
    }

    @ApiOperation("场景模板云平台插件数据说明，非API")
    @GetMapping("/scene")
    public String loadScene(@RequestBody Gns3PluginSceneDataBO data) {
        return null;
    }
}