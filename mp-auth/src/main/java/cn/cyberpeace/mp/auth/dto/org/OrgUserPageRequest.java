package cn.cyberpeace.mp.auth.dto.org;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class OrgUserPageRequest extends PageRequest {

    @ApiModelProperty(value = "username 注意 后台会匹配 username 或者 name")
    private String username;

    @ApiModelProperty(value = "组织id")
    private String org;

    @ApiModelProperty(value = "用户id列表")
    private String[] ids;

}
