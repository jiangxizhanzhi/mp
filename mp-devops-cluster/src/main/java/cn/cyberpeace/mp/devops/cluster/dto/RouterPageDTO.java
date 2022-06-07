package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 路由分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouterPageDTO extends BasePageDTO {

    /**
     * 路由名称
     */
    @ApiModelProperty(value = "路由名称")
    private String name;

    public RouterPageDTO(RouterPageRequest request){
        this.name = request.getName();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
