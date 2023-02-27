package cn.cyberpeace.mp.auth.dto.org;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author qiaokang
 * @Date：2023/2/27 14:25
 */
@Data
@ApiModel(value = "查询用户id集合请求")
public class ListIdsByUserIdRequest {

    @ApiModelProperty(value = "用户名")
    private String username;
}
