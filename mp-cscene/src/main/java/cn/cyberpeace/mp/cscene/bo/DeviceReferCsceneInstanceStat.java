package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceReferCsceneInstanceStat {
    @ApiModelProperty(value = "靶标名称")
    private String referenceImage;
    @ApiModelProperty(value = "场景id")
    private String csceneId;
    @ApiModelProperty(value = "场景名称")
    private String name;
    @ApiModelProperty(value = "场景事件")
    private Integer event;
    @ApiModelProperty(value = "场景事件状态")
    private Integer eventStatus;
    @ApiModelProperty(value = "创建用户id")
    private String createUser;
    @ApiModelProperty(value = "创建用户名")
    private String createUsername;
}
