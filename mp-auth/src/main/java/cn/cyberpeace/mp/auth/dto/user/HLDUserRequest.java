package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description:
 * @Author hewei
 * @Date：2021/2/18 15:44
 */
@Data
@ApiModel
public class HLDUserRequest {

    @ApiModelProperty(value = "HLD用户id")
    private String userId;

    @ApiModelProperty(value = "HLD验证id")
    private String sessionId;

}
