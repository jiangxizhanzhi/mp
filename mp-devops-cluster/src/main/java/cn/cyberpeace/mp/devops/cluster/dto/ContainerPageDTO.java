package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 容器分页请求信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContainerPageDTO extends BasePageDTO {

    /**
     * 主机名/节点名称
     */
    @ApiModelProperty(value = "主机名/节点名称")
    private String host;

    @ApiModelProperty(value = "IP地址")
    private String addr;

    public ContainerPageDTO(String host) {
        this.host = host;
    }

    public ContainerPageDTO(String host, String addr) {
        this.host = host;
        this.addr = addr;
    }

    public ContainerPageDTO(ContainerPageRequest request){
        this.host = request.getHost();
        this.addr = request.getAddr();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
