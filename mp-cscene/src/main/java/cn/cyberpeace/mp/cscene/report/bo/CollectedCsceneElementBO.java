 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectedCsceneElementBO.java.
 // File size 1612.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.report.bo;

import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectedCsceneElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CollectedSceneElementBO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedCsceneElementBO extends CollectedSceneElementBO {

    @ApiModelProperty(value = "业务场景id")
    private String csceneId;

    @ApiModelProperty(value = "源操作: 启动流量node_start_flow，关闭流量node_stop_flow，启动探针node_start_probe，关闭探针node_stop_probe")
    private String callAction;

    /**
     * fromCollectedSceneElementBO
     *
     * @param collectedSceneElementBO
     * @return
     */
    public static CollectedCsceneElementBO fromCollectedSceneElementBO(CollectedSceneElementBO collectedSceneElementBO) {
        CollectedCsceneElementBO collectedCsceneElementBO = new CollectedCsceneElementBO();
        collectedCsceneElementBO.setSceneId(collectedSceneElementBO.getSceneId());
        collectedCsceneElementBO.setSceneElement(collectedSceneElementBO.getSceneElement());
        collectedCsceneElementBO.setSceneElementPluginData(collectedSceneElementBO.getSceneElementPluginData());
        collectedCsceneElementBO.setSceneElementPortPluginData(collectedSceneElementBO.getSceneElementPortPluginData());
        return collectedCsceneElementBO;
    }

}
