package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserHeartbeatRequest {

    @ApiModelProperty(value = "动作")
    private String action;

}
