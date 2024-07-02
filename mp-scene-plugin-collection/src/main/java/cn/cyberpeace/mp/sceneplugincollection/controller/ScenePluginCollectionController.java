package cn.cyberpeace.mp.sceneplugincollection.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigDataBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneDataBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.ControllerConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhLauncher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ScenePluginCollectionController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "场景采集插件", tags = "场景采集插件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
@AuthTarget(TargetEnum.NO_AUTH)
public class ScenePluginCollectionController {

    /**
     * loadSceneConfig
     *
     * @param data
     * @return
     */
    @ApiOperation("场景模板采集插件数据，非API")
    @GetMapping("/scene_config")
    public String loadSceneConfig(@RequestBody CollectionPluginSceneConfigDataBO data) {
        return null;
    }

    /**
     * loadScene
     *
     * @param data
     * @return
     */
    @ApiOperation("场景采集插件数据，非API")
    @GetMapping("/scene")
    public String loadScene(@RequestBody CollectionPluginSceneDataBO data) {
        return null;
    }

    /**
     * wazuhRunCommandResultCall
     *
     * @param alertData
     */
    @ApiOperation("wazuh执行agent命令结果回调")
    @PostMapping("/wazuh_run_command_result")
    public void wazuhRunCommandResultCall(@RequestBody WazuhLauncher.LaunchAlert alertData) {
        WazuhLauncher.runCommandResultCall(alertData);
    }

}
