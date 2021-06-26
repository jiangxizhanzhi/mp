package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务状态信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "服务状态信息")
public class HypervisorServiceStateResponse extends BaseOpenStackResponse{

    /**
     * 服务
     */
    @ApiModelProperty(value = "服务")
    private String name;

    /**
     * 是否存活
     */
    @ApiModelProperty(value = "是否存活")
    private Boolean alive;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String state;

    public HypervisorServiceStateResponse(String name, Boolean alive) {
        this.name = name;
        this.alive = alive;
    }

    public HypervisorServiceStateResponse(String name, String state) {
        this.name = name;
        this.state = state;
    }
}
