package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.model.SceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SceneBO extends BaseSceneBO<SceneModel, SceneBO> {
    @ApiModelProperty(value = "事件")
    private Integer event;
    @ApiModelProperty(value = "事件状态")
    private Integer eventStatus;
    @ApiModelProperty(value = "事件进程")
    private Integer process;
    @ApiModelProperty(value = "事件进程状态")
    private Integer processStatus;
    @ApiModelProperty(value = "事件进程描述信息")
    private String processMessage;

    @Override
    public SceneModel createModel() {
        return new SceneModel();
    }
}
