package cn.cyberpeace.mp.sceneplugincloud.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "场景云平台数据", tags = "场景云平台数据")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scene")
@Data
public class ScenePluginCloudController {

    @ApiOperation("场景模板云平台数据说明")
    public ServiceResult<BaseScenePluginDataBO<CloudPluginSceneBO, CloudPluginSceneElementBO,
            CloudPluginSceneElementPortBO>> loadSceneConfig(@RequestBody BaseScenePluginDataBO<CloudPluginSceneBO,
            CloudPluginSceneElementBO, CloudPluginSceneElementPortBO> data) {
        return new ServiceResult<>(data);
    }

    @ApiOperation("场景云平台数据说明")
    public ServiceResult<BaseScenePluginDataBO<CloudPluginSceneConfigBO, CloudPluginSceneConfigElementBO,
            CloudPluginSceneConfigElementPortBO>> loadScene(@RequestBody BaseScenePluginDataBO<CloudPluginSceneConfigBO,
            CloudPluginSceneConfigElementBO, CloudPluginSceneConfigElementPortBO> data) {
        return new ServiceResult<>(data);
    }

}
