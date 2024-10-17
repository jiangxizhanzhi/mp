package cn.cyberpeace.mp.auth.request.management;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PersonnelManagementRequest {

    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "战队id")
    private String teamId;
}
