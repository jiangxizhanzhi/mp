package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.NetworkPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NetworkPageDTO extends BasePageDTO {

    
    @ApiModelProperty(value = "网络名称")
    private String name;

    public NetworkPageDTO(NetworkPageRequest request){
        this.name = request.getName();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
