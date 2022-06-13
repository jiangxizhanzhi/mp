package cn.cyberpeace.mp.sceneplugincloud.controller;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigDataBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneDataBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "场景云平台插件", tags = "场景云平台插件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
public class ScenePluginCloudController {

    @ApiOperation("场景模板云平台插件数据说明，非API")
    @GetMapping("/scene_config")
    public String loadSceneConfig(@RequestBody CloudPluginSceneConfigDataBO data) {
        return null;
    }

    @ApiOperation("场景云平台插件数据说明，非API")
    @GetMapping("/scene")
    public String loadScene(@RequestBody CloudPluginSceneDataBO data) {
        return null;
    }

    @ApiOperation(value = "场景云平台节点角色数据说明，非API", notes = "READ_CONFIG_FROM:cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel$Role")
    @GetMapping("/scene_element_role")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "example", paramType = "path", value = "")
    )
    public String loadSceneRole() {
        return null;
    }

    @ApiOperation(value = "场景云平台节点虚拟类型数据说明，非API", notes = "READ_CONFIG_FROM:cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel$VirtualType")
    @GetMapping("/scene_element_virtual_type")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "example", paramType = "path", value = "")
    )
    public String loadSceneElementVirtualType() {
        return null;
    }

    @ApiOperation(value = "场景云平台镜像类型数据说明，非API", notes = "READ_CONFIG_FROM:cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel$ImageType")
    @GetMapping("/scene_element_image_type")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "example", paramType = "path", value = "")
    )
    public String loadSceneElementImageType() {
        return null;
    }

}
