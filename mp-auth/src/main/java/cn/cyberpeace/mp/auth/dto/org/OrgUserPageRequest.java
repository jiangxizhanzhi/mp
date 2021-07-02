package cn.cyberpeace.mp.auth.dto.org;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrgUserPageRequest {

    @ApiModelProperty(value = "username")
    private String username;

    @ApiModelProperty(value = "组织id")
    private String org;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;

    // todo 临时
    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer per_page;


    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }

        if (per_page != null) {
            pageSize = per_page;
        }
    }

}
