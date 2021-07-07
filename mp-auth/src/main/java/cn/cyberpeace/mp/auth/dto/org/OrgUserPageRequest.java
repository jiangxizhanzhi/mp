package cn.cyberpeace.mp.auth.dto.org;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class OrgUserPageRequest {

    @ApiModelProperty(value = "username")
    private String username;

    @ApiModelProperty(value = "组织id")
    private String org;

    @ApiModelProperty(value = "用户id列表")
    private String[] ids;

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

        // 默认值
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
    }

}
