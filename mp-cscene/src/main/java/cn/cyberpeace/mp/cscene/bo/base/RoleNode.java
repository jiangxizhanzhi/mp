package cn.cyberpeace.mp.cscene.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleNode {
    @ApiModelProperty(value = "节点id")
    private String id;
    @ApiModelProperty(value = "分类类型")
    private String category;
}
