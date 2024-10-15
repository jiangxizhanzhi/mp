 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneContextBO.java.
 // File size 2676.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CsceneContextBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneContextBO {

    @ApiModelProperty(value = "场景访问用户id")
    private String accessUserId;

    @ApiModelProperty(value = "透明上下文")
    private Map<String, Object> passThrough;

    /**
     * loadTransparentContext
     */
    public void loadTransparentContext() {
        if (passThrough != null) {
            SysContextHolder.setPassThroughMap(passThrough);
        }
    }

    /**
     * toSceneContext
     *
     * @return
     */
    public SceneContextBO toSceneContext() {
        SceneContextBO sceneContext = new SceneContextBO();
        Map<String, Object> plugins = new HashMap<>();
        CloudScenePluginContext cloudScenePluginContext = toCloudScenePluginContext();
        plugins.put(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY, cloudScenePluginContext);
        sceneContext.setPlugins(plugins);
        return sceneContext;
    }

    /**
     * toCloudScenePluginContext
     *
     * @return
     */
    public CloudScenePluginContext toCloudScenePluginContext() {
        CloudScenePluginContext cloudScenePluginContext = new CloudScenePluginContext();
        if (StringUtils.isNotEmpty(accessUserId)) {
            cloudScenePluginContext.setRequestUser(accessUserId);
        }
        return cloudScenePluginContext;
    }
}
