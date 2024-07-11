package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.RouterPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName RouterPageDTO
 * @Description 路由分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageDTO
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
        super.setLimit(request.getPerPage());
        super.setOffset(request.getPage());
    }
}
