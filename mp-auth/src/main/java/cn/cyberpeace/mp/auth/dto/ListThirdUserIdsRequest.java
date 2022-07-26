package cn.cyberpeace.mp.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 批量获取用户id集合
 * @Author qiaokang
 * @Date：2022/5/24 15:43
 */
@ApiModel
@Data
public class ListThirdUserIdsRequest {

    @ApiModelProperty(value = "用户id集合", required = true)
    @NotNull(message = "userIds can not be empty")
    private List<String> userIds;

}
