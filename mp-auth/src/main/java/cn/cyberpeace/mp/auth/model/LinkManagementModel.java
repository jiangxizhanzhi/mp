package cn.cyberpeace.mp.auth.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_link_management")
@Builder
public class LinkManagementModel extends BaseModel {
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
    private String  roomId;
}
