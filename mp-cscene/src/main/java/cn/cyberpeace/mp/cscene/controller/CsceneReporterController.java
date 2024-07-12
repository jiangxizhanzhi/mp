 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneReporterController.java.
 // File size 2821.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneBO;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CsceneReporterController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "场景上报", tags = "CSCENE-场景上报")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
@Data
@AuthTarget(TargetEnum.NO_AUTH)
public class CsceneReporterController {

    @Data
    public static class SceneFormatMessage extends FormatMessage<CollectedCsceneBO> {
        @ApiModelProperty(value = "数据类型: scene")
        private String type;
    }

    @Data
    public static class SceneElementFormatMessage extends FormatMessage<CollectedCsceneElementBO> {
        @ApiModelProperty(value = "数据类型: scene_element")
        private String type;
    }

    @Data
    public static class DeleteCsceneInstanceFormatMessage extends FormatMessage<String> {
        @ApiModelProperty(value = "数据类型: delete_cscene_instance")
        private String type;
        @ApiModelProperty(value = "场景实例id")
        private String data;
    }

    /**
     * reportScene
     *
     * @param data
     * @return
     */
    @ApiOperation("场景上报数据说明，非API")
    @GetMapping("/report_scene")
    public String reportScene(@RequestBody SceneFormatMessage data) {
        return null;
    }

    /**
     * reportSceneElement
     *
     * @param data
     * @return
     */
    @ApiOperation("场景网元上报数据说明，非API")
    @GetMapping("/report_scene_element")
    public String reportSceneElement(@RequestBody SceneElementFormatMessage data) {
        return null;
    }

    /**
     * reportDeleteCsceneInstance
     *
     * @param data
     * @return
     */
    @ApiOperation("删除场景实例上报数据说明，非API")
    @GetMapping("/report_delete_cscene_instance")
    public String reportDeleteCsceneInstance(@RequestBody DeleteCsceneInstanceFormatMessage data) {
        return null;
    }

}
