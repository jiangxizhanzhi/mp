package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 网络分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "网络分页返回信息")
public class NetworkPageResponse extends BaseResponse {

    /**
     * 网络名称
     */
    @ApiModelProperty("网络名称")
    private String name;

    /**
     * 网络类型
     */
    @ApiModelProperty("网络类型")
    private String networkType;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 已连接子网
     */
    @ApiModelProperty("IP地址")
    private List<Subnet> subnets;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Subnet implements Serializable {

        @ApiModelProperty("id")
        private String id;

        @ApiModelProperty("子网CIDR")
        private String cidr;

        @ApiModelProperty("网关ip")
        private String gatewayIp;
    }
}
