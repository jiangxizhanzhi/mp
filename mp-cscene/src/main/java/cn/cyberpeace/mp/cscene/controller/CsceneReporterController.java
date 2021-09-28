package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "场景上报", tags = "CSCENE-场景上报")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
public class CsceneReporterController {

    public static class SceneFormatMessage extends FormatMessage<CollectedSceneBO> {
        @ApiModelProperty(value = "数据类型: scene")
        private String type;
    }

    public static class SceneElementFormatMessage extends FormatMessage<CollectedSceneElementBO> {
        @ApiModelProperty(value = "数据类型: scene_element")
        private String type;
    }

    @ApiOperation("场景上报数据说明，非API")
    @GetMapping("/report_scene")
    public String reportScene(@RequestBody SceneFormatMessage data) {
        return null;
    }

    @ApiOperation("场景网元上报数据说明，非API")
    @GetMapping("/report_scene_element")
    public String reportSceneElement(@RequestBody SceneElementFormatMessage data) {
        return null;
    }

}
