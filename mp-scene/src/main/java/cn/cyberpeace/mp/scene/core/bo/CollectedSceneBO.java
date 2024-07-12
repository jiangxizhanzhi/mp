 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectedSceneBO.java.
 // File size 873.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName CollectedSceneBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedSceneBO {
    @ApiModelProperty(value = "场景基础数据")
    private SceneBO scene;
    @ApiModelProperty(value = "场景插件基础数据(字典: key-插件代号, value-插件数据[不同插件代号对应的数据模型不一样]), "
            + "cpcloud(云平台): CloudPluginSceneBO, "
            + "cpcollection(采集): CollectionPluginSceneBO, "
            + "cpflow(流量): FlowPluginSceneBO")
    private Map<String, Object> scenePluginData;
}
