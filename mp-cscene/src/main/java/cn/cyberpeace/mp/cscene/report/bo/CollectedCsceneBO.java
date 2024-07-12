 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectedCsceneBO.java.
 // File size 1245.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.report.bo;

import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectedCsceneBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CollectedSceneBO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedCsceneBO extends CollectedSceneBO {

    @ApiModelProperty(value = "业务场景id")
    private String csceneId;

    @ApiModelProperty(value = "源操作: 启动流量node_start_flow，关闭流量node_stop_flow，启动探针node_start_probe，关闭探针node_stop_probe")
    private String callAction;

    /**
     * fromCollectedSceneBO
     *
     * @param collectedSceneBO
     * @return
     */
    public static CollectedCsceneBO fromCollectedSceneBO(CollectedSceneBO collectedSceneBO) {
        CollectedCsceneBO collectedCsceneBO = new CollectedCsceneBO();
        collectedCsceneBO.setScene(collectedSceneBO.getScene());
        collectedCsceneBO.setScenePluginData(collectedSceneBO.getScenePluginData());
        return collectedCsceneBO;
    }

}
