package cn.cyberpeace.mp.auth.request.team;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LinkManagementRequest {

    private String id;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "战队id")
    private String teamId;
    @ApiModelProperty(value = "战队名称")
    private String teamName;
    @ApiModelProperty(value = "终端类型")
    private String terminalType;
    @ApiModelProperty(value = "ip地址")
    private String ipAddress;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "操作")
    private Integer operate;
    private String roomId;
}
