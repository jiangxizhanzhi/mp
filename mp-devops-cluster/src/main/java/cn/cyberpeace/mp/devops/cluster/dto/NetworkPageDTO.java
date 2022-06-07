package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 网络分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NetworkPageDTO extends BasePageDTO {

    /**
     * 网络名称
     */
    @ApiModelProperty(value = "网络名称")
    private String name;

    public NetworkPageDTO(NetworkPageRequest request){
        this.name = request.getName();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
