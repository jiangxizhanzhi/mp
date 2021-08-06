package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserOnlineReportPageRequest extends PageRequest {

    @ApiModelProperty(value = "username 注意 后台会匹配 username 或者 name")
    private String username;

}
