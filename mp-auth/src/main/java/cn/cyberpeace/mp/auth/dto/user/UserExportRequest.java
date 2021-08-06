package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserExportRequest {

    @ApiModelProperty(value = "后台会匹配 username 或者 name")
    private String username;

    @ApiModelProperty(value = "组织id")
    private String org;

    @ApiModelProperty(value = "用户id列表")
    private String[] ids;

}
