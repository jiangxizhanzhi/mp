package cn.cyberpeace.mp.auth.dto.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RolePageRequest {

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;


    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }
    }

}
