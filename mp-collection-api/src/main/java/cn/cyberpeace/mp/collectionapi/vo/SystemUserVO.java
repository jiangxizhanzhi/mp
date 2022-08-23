package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemUserVO {
    @ApiModelProperty(value = "comment")
    private String comment;

    @ApiModelProperty(value = "shell")
    private String shell;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "分组名")
    private String groupName;

    @ApiModelProperty(value = "分组id")
    private String groupId;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "用户路径")
    private String dir;

    @ApiModelProperty(value = "采集时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "动作")
    private String action;

    @ApiModelProperty(value = "主机ip")
    private String agentIp;
}
