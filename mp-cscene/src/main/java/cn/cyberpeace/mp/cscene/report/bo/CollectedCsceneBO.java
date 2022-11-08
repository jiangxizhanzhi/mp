package cn.cyberpeace.mp.cscene.report.bo;

import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedCsceneBO extends CollectedSceneBO {

    @ApiModelProperty(value = "业务场景id")
    private String csceneId;

    @ApiModelProperty(value = "源操作: 启动流量node_start_flow，关闭流量node_stop_flow，启动探针node_start_probe，关闭探针node_stop_probe")
    private String callAction;

    public static CollectedCsceneBO fromCollectedSceneBO(CollectedSceneBO collectedSceneBO) {
        var collectedCsceneBO = new CollectedCsceneBO();
        collectedCsceneBO.setScene(collectedSceneBO.getScene());
        collectedCsceneBO.setScenePluginData(collectedSceneBO.getScenePluginData());
        return collectedCsceneBO;
    }

}
