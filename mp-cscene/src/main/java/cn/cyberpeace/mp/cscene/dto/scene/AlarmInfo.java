package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "告警详情")
@Getter
@Setter
public class AlarmInfo {
    @ApiModelProperty(value = "主机名称")
    private String nodeName;
    @ApiModelProperty(value = "告警名称")
    private String name;
    @ApiModelProperty(value = "告警类别")
    private String type;
    @ApiModelProperty(value = "告警对象")
    private String target;
    @ApiModelProperty(value = "告警动作")
    private String action;
    @ApiModelProperty(value = "告警内容")
    private String content;
    @ApiModelProperty(value = "告警等级")
    private Integer level;
    @ApiModelProperty(value = "变更时间")
    private String time;
}
