package cn.cyberpeace.mp.scene.core.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedSceneStatusBO {
    @ApiModelProperty(value = "场景id")
    private String csceneId;
    @ApiModelProperty(value = "名称")
    private String name;
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
}
