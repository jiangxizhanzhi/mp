package cn.cyberpeace.mp.sceneplugincloud.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "场景云平台数据", tags = "场景云平台数据")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
public class ScenePluginCloudController {

    @ApiOperation("场景模板云平台数据说明")
    @GetMapping("/scene_config")
    public ServiceResult<CloudPluginSceneConfigDataBO> loadSceneConfig(@RequestBody CloudPluginSceneConfigDataBO data) {
        return new ServiceResult<>(data);
    }

    @ApiOperation("场景云平台数据说明")
    @GetMapping("/scene")
    public ServiceResult<CloudPluginSceneDataBO> loadScene(@RequestBody CloudPluginSceneDataBO data) {
        return new ServiceResult<>(data);
    }

}
