package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SceneBO extends BaseSceneBO<SceneModel, SceneBO> implements BaseEventModel {
    @ApiModelProperty(value = "事件")
    private Integer event;
    @ApiModelProperty(value = "事件状态")
    private Integer eventStatus;
    @ApiModelProperty(value = "事件进程")
    private String process;
    @ApiModelProperty(value = "事件进程状态")
    private Integer processStatus;
    @ApiModelProperty(value = "事件进程描述信息")
    private String processMessage;
    @ApiModelProperty(value = "事件进度百分比")
    private Integer progressPercentage;
    @ApiModelProperty(value = "创建消耗时间(秒)")
    private Long createConsumeSeconds;

    @Override
    public SceneModel createModel() {
        return new SceneModel();
    }

    @Override
    public String genTarget() {
        return getEid();
    }
}
