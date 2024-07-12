 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenePluginFlowController.java.
 // File size 1549.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigDataBO;
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
@AuthTarget(TargetEnum.NO_AUTH)
public class ScenePluginFlowController {

    /**
     * loadSceneConfig
     *
     * @param data
     * @return
     */
    @ApiOperation("场景模板流量插件数据，非API")
    @GetMapping("/scene_config")
    public String loadSceneConfig(@RequestBody FlowPluginSceneConfigDataBO data) {
        return null;
    }

    /**
     * loadScene
     *
     * @param data
     * @return
     */
    @ApiOperation("场景流量插件数据，非API")
    @GetMapping("/scene")
    public String loadScene(@RequestBody FlowPluginSceneDataBO data) {
        return null;
    }

}
