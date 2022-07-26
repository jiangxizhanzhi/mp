package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.dto.user.ListThirdUserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 批量获取用户信息集合
 * @Author qiaokang
 * @Date：2022/5/24 15:43
 */
@ApiModel
@Data
public class ListThirdUsersRequest {

    @ApiModelProperty(value = "批量获取用户信息集合", required = true)
    @NotNull(message = "listThirdUserDtos can not be empty")
    private List<ListThirdUserDto> listThirdUserDtos;

}
