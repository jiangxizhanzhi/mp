package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 实例分页请求信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstancePageDTO extends BasePageDTO {

    /**
     * 主机名称/节点名称
     */
    @ApiModelProperty(value = "主机名称/节点名称")
    private String host;

    @ApiModelProperty(value = "IP地址")
    private String addr;

    public InstancePageDTO(String host) {
        this.host = host;
    }

    public InstancePageDTO(String host, String addr) {
        this.host = host;
        this.addr = addr;
    }

    public InstancePageDTO(InstancePageRequest request){
        this.host = request.getHost();
        this.addr = request.getAddr();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
