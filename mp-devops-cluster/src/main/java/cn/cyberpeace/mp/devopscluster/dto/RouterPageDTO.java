package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.RouterPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouterPageDTO extends BasePageDTO {

    
    @ApiModelProperty(value = "路由名称")
    private String name;

    public RouterPageDTO(RouterPageRequest request){
        this.name = request.getName();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
